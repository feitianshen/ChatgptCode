package com.chatgptcode.image;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class App {
    private static final int DEFAULT_THRESHOLD = 10;
    private static final int DEFAULT_TOP = 5;

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            printUsage();
            return;
        }

        String command = args[0];
        Path libraryPath = Paths.get("library");
        int threshold = DEFAULT_THRESHOLD;
        int top = DEFAULT_TOP;

        for (int i = 1; i < args.length; i++) {
            if ("--lib".equals(args[i]) && i + 1 < args.length) {
                libraryPath = Paths.get(args[++i]);
            } else if ("--threshold".equals(args[i]) && i + 1 < args.length) {
                threshold = Integer.parseInt(args[++i]);
            } else if ("--top".equals(args[i]) && i + 1 < args.length) {
                top = Integer.parseInt(args[++i]);
            }
        }

        ImageLibrary library = new ImageLibrary(libraryPath, new ImageHashService());

        switch (command) {
            case "add" -> handleAdd(library, args);
            case "search" -> handleSearch(library, args, top, threshold);
            default -> printUsage();
        }
    }

    private static void handleAdd(ImageLibrary library, String[] args) throws IOException {
        if (args.length < 2) {
            printUsage();
            return;
        }
        Path imagePath = Paths.get(args[1]);
        LibraryEntry entry = library.addImage(imagePath);
        System.out.printf("Added %s with hash %s%n", entry.fileName(), entry.hash());
    }

    private static void handleSearch(ImageLibrary library, String[] args, int top, int threshold) throws IOException {
        if (args.length < 2) {
            printUsage();
            return;
        }
        Path imagePath = Paths.get(args[1]);
        List<SearchResult> results = library.search(imagePath, top, threshold);
        if (results.isEmpty()) {
            System.out.println("No similar images found.");
            return;
        }
        System.out.println("Matches:");
        for (SearchResult result : results) {
            System.out.printf("%s (distance=%d)%n", result.entry().fileName(), result.distance());
        }
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  add <imagePath> [--lib <libraryDir>]");
        System.out.println("  search <imagePath> [--lib <libraryDir>] [--top <count>] [--threshold <maxDistance>]");
    }
}
