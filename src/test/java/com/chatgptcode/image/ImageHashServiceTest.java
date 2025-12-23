package com.chatgptcode.image;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ImageHashServiceTest {

    @Test
    void hashesAreStableForSameImage() throws IOException {
        ImageHashService service = new ImageHashService();
        Path image = createSolidImage(Color.RED);
        String hash1 = service.hash(image);
        String hash2 = service.hash(image);
        assertEquals(hash1, hash2);
    }

    @Test
    void hammingDistanceReflectsDifferences() throws IOException {
        ImageHashService service = new ImageHashService();
        Path red = createSolidImage(Color.RED);
        Path blue = createSolidImage(Color.BLUE);
        String redHash = service.hash(red);
        String blueHash = service.hash(blue);
        int distance = ImageHashService.hammingDistance(redHash, blueHash);
        assertTrue(distance > 0);
        assertTrue(distance <= redHash.length());
    }

    private Path createSolidImage(Color color) throws IOException {
        BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, 50, 50);
        g2d.dispose();
        Path tempFile = Files.createTempFile("solid", ".png");
        ImageIO.write(image, "png", tempFile.toFile());
        return tempFile;
    }
}
