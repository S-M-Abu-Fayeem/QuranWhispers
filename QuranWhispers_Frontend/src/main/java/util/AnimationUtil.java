package util;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.Node;
import java.util.List;

public class AnimationUtil {

    public static void fadeInElements(List<Node> elements, double fadeInSpeed) {
        for (int i = 0; i < elements.size(); i++) {
            Node node = elements.get(i);
            createFadeTransition(node, fadeInSpeed + (i * 0.5), 1.0);  // Fade in with a delay
        }
    }

    public static void fadeOutElements(List<Node> elements, double fadeOutSpeed) {
        for (int i = 0; i < elements.size(); i++) {
            Node node = elements.get(i);
            createFadeTransition(node, fadeOutSpeed + (i * 0.5), 0.0);  // Fade out with a delay
        }
    }

    private static void createFadeTransition(Node node, double durationInSeconds, double toValue) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(durationInSeconds), node);
        fadeTransition.setFromValue(1 - toValue);  // Start at the opposite opacity
        fadeTransition.setToValue(toValue);       // End at the target opacity
        fadeTransition.setCycleCount(1);          // Play once
        fadeTransition.setAutoReverse(false);    // No reverse effect
        fadeTransition.play();                   // Start the fade animation
    }

    public static void startFloatingAnimation(Node node, double speedFactor) {
        final double[] offsetX = {0}; // Used for smooth horizontal movement
        final double[] offsetY = {0}; // Used for smooth vertical movement

        // Start the AnimationTimer to continuously update position
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Calculate the time passed for smooth transition
                double time = now / 1_000_000_000.0;  // Convert to seconds

                // Smooth sinusoidal movement for both X and Y (floating)
                offsetX[0] = Math.cos(time * speedFactor) * 10;  // Smooth left-right motion
                offsetY[0] = Math.sin(time * speedFactor) * 10;  // Smooth up-down motion

                // Apply the translations
                node.setTranslateX(offsetX[0]);
                node.setTranslateY(offsetY[0]);
            }
        }.start(); // Start the animation timer to run continuously
    }
}
