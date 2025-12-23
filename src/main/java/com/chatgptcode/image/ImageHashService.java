package com.chatgptcode.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImageHashService {
    private static final int SIZE = 8;

    public String hash(Path imagePath) throws IOException {
        if (!Files.exists(imagePath)) {
            throw new IOException("Image not found: " + imagePath);
        }
        BufferedImage image = ImageIO.read(imagePath.toFile());
        if (image == null) {
            throw new IOException("Unsupported image format: " + imagePath);
        }
        BufferedImage resized = resizeToSquare(image, SIZE);
        int[][] gray = new int[SIZE][SIZE];
        long total = 0;
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                int rgb = resized.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int value = (r + g + b) / 3;
                gray[y][x] = value;
                total += value;
            }
        }
        int average = (int) (total / (SIZE * SIZE));
        StringBuilder hash = new StringBuilder(SIZE * SIZE);
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                hash.append(gray[y][x] >= average ? '1' : '0');
            }
        }
        return hash.toString();
    }

    public static int hammingDistance(String hash1, String hash2) {
        if (hash1.length() != hash2.length()) {
            throw new IllegalArgumentException("Hashes must be same length");
        }
        int distance = 0;
        for (int i = 0; i < hash1.length(); i++) {
            if (hash1.charAt(i) != hash2.charAt(i)) {
                distance++;
            }
        }
        return distance;
    }

    private BufferedImage resizeToSquare(BufferedImage original, int size) {
        Image tmp = original.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }
}
