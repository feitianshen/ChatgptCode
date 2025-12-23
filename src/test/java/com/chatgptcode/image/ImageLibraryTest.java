package com.chatgptcode.image;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ImageLibraryTest {

    @TempDir
    Path tempDir;

    @Test
    void addsImageAndStoresMetadata() throws IOException {
        ImageLibrary library = new ImageLibrary(tempDir, new ImageHashService());
        Path image = createSolidImage(Color.GREEN, tempDir.resolve("green.png"));
        LibraryEntry entry = library.addImage(image);

        assertTrue(entry.hash().length() > 0);
        assertTrue(entry.fileName().contains(".png"));
        Path storedFile = tempDir.resolve(entry.fileName());
        assertTrue(storedFile.toFile().exists());
    }

    @Test
    void searchFindsSimilarImagesWithinThreshold() throws IOException {
        ImageLibrary library = new ImageLibrary(tempDir, new ImageHashService());
        Path base = createSolidImage(Color.YELLOW, tempDir.resolve("base.png"));
        library.addImage(base);
        Path query = createSolidImage(new Color(245, 245, 0), tempDir.resolve("query.png"));

        List<SearchResult> results = library.search(query, 3, 8);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(0, results.get(0).distance());
    }

    private Path createSolidImage(Color color, Path path) throws IOException {
        BufferedImage image = new BufferedImage(40, 40, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, 40, 40);
        g2d.dispose();
        ImageIO.write(image, "png", path.toFile());
        return path;
    }
}
