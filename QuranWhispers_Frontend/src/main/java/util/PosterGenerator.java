package util;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.json.JSONObject;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PosterGenerator {

    public static void generatePosterAndSave(int surahNum, int ayahNum) {
        // Logging the start of the process
        System.out.println("Generating poster for Surah: " + surahNum + ", Ayah: " + ayahNum);

        // Creating canvas to draw the poster
        Canvas canvas = new Canvas(1800, 900);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Draw poster content on the canvas
        drawPosterContent(gc, surahNum, ayahNum);

        // Use Platform.runLater() to ensure snapshot happens on JavaFX application thread
        Platform.runLater(() -> {
            // Take a snapshot of the canvas
            WritableImage snapshot = canvas.snapshot(null, null);

            // Use relative paths instead of absolute paths
            String basePath = "src/main/resources/images/verse_posters";  // This path should be relative to your project structure
            File folder = new File(basePath);

            // Ensure folder exists or create it
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // File where the poster will be saved
            File file = new File(folder, surahNum + "_" + ayahNum + ".png");

            try {
                // Convert the snapshot to BufferedImage
                BufferedImage bufferedImage = convertWritableImageToBufferedImage(snapshot);

                // Save the image as a PNG
                ImageIO.write(bufferedImage, "PNG", file);
                System.out.println("Poster saved to: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to save the poster.");
            }
        });
    }

    private static BufferedImage convertWritableImageToBufferedImage(WritableImage writableImage) {
        int width = (int) writableImage.getWidth();
        int height = (int) writableImage.getHeight();

        // Create a BufferedImage with ARGB color model
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        javafx.scene.image.PixelReader pixelReader = writableImage.getPixelReader();

        // Loop through every pixel and set it to the BufferedImage
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);

                // Manually extract RGBA components
                int alpha = (int) (color.getOpacity() * 255);  // Alpha channel (0-255)
                int red = (int) (color.getRed() * 255);       // Red channel (0-255)
                int green = (int) (color.getGreen() * 255);   // Green channel (0-255)
                int blue = (int) (color.getBlue() * 255);     // Blue channel (0-255)

                // Set RGB value with alpha (ARGB)
                int argb = (alpha << 24) | (red << 16) | (green << 8) | blue;
                bufferedImage.setRGB(x, y, argb);
            }
        }

        return bufferedImage;
    }

    private static void drawWrappedText(GraphicsContext gc, String text, double x, double y, double maxWidth) {
        Text textNode = new Text(text);
        textNode.setFont(gc.getFont());

        // Create a string array of wrapped lines based on max width
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        double lineHeight = textNode.getLayoutBounds().getHeight();
        double currentY = y;

        // Loop through each word, adding it to the line if it fits within maxWidth
        for (String word : words) {
            currentLine.append(word).append(" ");
            textNode.setText(currentLine.toString());

            if (textNode.getLayoutBounds().getWidth() > maxWidth) {
                gc.fillText(currentLine.toString().trim(), x, currentY);
                currentY += lineHeight;
                currentLine.setLength(0);
                currentLine.append(word).append(" ");
            }
        }

        // Draw the last line
        if (currentLine.length() > 0) {
            gc.fillText(currentLine.toString().trim(), x, currentY);
        }
    }

    public static void drawPosterContent(GraphicsContext gc, int surahNum, int ayahNum) {
        // Load the background image (ensure no transparency issues)
        Image backgroundImage = new Image(PosterGenerator.class.getResourceAsStream("/images/verse_posters/background.png"));

        // Draw the background image (fill the entire canvas)
        gc.drawImage(backgroundImage, 0, 0, 1800, 900); // Adjust size if necessary

        // Set up font for Arabic text
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(60));

        JSONObject data = QuranAPI.generateVerse(surahNum, ayahNum);

        // Wrap Arabic text
        String arabicText = data.getString("arabicTranslationText");
        drawWrappedText(gc, arabicText, 100, 125, 1200);

        // Set up font for English text
        gc.setFont(javafx.scene.text.Font.font(40));
        String englishText = data.getString("englishTranslationText");
        drawWrappedText(gc, englishText, 250, 400, 1200);

        // Set up font for Bengali text
        String bengaliText = data.getString("bengaliTranslationText");
        drawWrappedText(gc, bengaliText, 400, 600, 1200);

        // Footer (verse number)
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(100));
        gc.fillText(surahNum + ":" + ayahNum, 1500, 850);
    }
}
