package com.chatgptcode.image;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ImageLibrary {
    private static final String METADATA_FILE = "metadata.json";
    private static final DateTimeFormatter FILE_DATE = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private final Path root;
    private final ImageHashService hashService;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ImageLibrary(Path root, ImageHashService hashService) {
        this.root = root;
        this.hashService = hashService;
    }

    public LibraryEntry addImage(Path source) throws IOException {
        ensureDirectory();
        String uniqueName = buildFileName(source.getFileName().toString());
        Path target = root.resolve(uniqueName);
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

        String hash = hashService.hash(target);
        List<LibraryEntry> entries = readMetadata();
        LibraryEntry entry = new LibraryEntry(uniqueName, hash);
        entries.add(entry);
        writeMetadata(entries);
        return entry;
    }

    public List<SearchResult> search(Path queryImage, int top, int threshold) throws IOException {
        if (!Files.exists(queryImage)) {
            throw new IOException("Query image not found: " + queryImage);
        }
        ensureDirectory();
        String queryHash = hashService.hash(queryImage);
        List<LibraryEntry> entries = readMetadata();

        List<SearchResult> results = new ArrayList<>();
        for (LibraryEntry entry : entries) {
            int distance = ImageHashService.hammingDistance(queryHash, entry.hash());
            if (distance <= threshold) {
                results.add(new SearchResult(entry, distance));
            }
        }

        results.sort(Comparator.comparingInt(SearchResult::distance));
        return results.size() > top ? results.subList(0, top) : results;
    }

    private void ensureDirectory() throws IOException {
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }
    }

    private String buildFileName(String original) {
        String normalized = Optional.ofNullable(original)
                .map(name -> name.replaceAll("[^a-zA-Z0-9._-]", "_"))
                .orElse("image");
        String timestamp = FILE_DATE.format(LocalDateTime.now());
        String suffix = normalized.contains(".") ? normalized.substring(normalized.lastIndexOf('.')) : "";
        return timestamp + "_" + UUID.randomUUID() + suffix;
    }

    private List<LibraryEntry> readMetadata() throws IOException {
        Path metadataFile = root.resolve(METADATA_FILE);
        if (!Files.exists(metadataFile)) {
            return new ArrayList<>();
        }
        Type listType = new TypeToken<List<LibraryEntry>>() { }.getType();
        try (var reader = Files.newBufferedReader(metadataFile)) {
            List<LibraryEntry> entries = gson.fromJson(reader, listType);
            return entries != null ? entries : new ArrayList<>();
        }
    }

    private void writeMetadata(List<LibraryEntry> entries) throws IOException {
        Path metadataFile = root.resolve(METADATA_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(metadataFile)) {
            gson.toJson(entries, writer);
        }
    }
}
