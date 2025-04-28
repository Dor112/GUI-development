package org.example.gui_development_newest;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlyingButtonsApp extends Application {

    private static final double BUTTON_SIZE = 50;
    private static final double WIDTH = 800;
    private static final double HEIGHT = 600;
    private static final int MAX_COLLISIONS = 5;
    private static final int MAX_BUTTONS = 100;

    private final List<MovingButton> buttons = new ArrayList<>();
    private final Random random = new Random();

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        MovingButton initial = new MovingButton(WIDTH / 2, HEIGHT / 2);
        buttons.add(initial);
        root.getChildren().add(initial.button);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateButtons(root);
            }
        };

        timer.start();

        stage.setTitle("Flying Buttons");
        stage.setScene(scene);
        stage.show();
    }

    private void updateButtons(Pane root) {
        for (MovingButton mb : buttons) {
            mb.move();

            boolean collided = false;

            if (mb.x <= 0 || mb.x + BUTTON_SIZE >= WIDTH) {
                mb.dx *= -1;
                collided = true;
            }
            if (mb.y <= 0 || mb.y + BUTTON_SIZE >= HEIGHT) {
                mb.dy *= -1;
                collided = true;
            }

            for (MovingButton other : buttons) {
                if (other != mb && mb.intersects(other)) {
                    mb.dx *= -1;
                    mb.dy *= -1;
                    collided = true;
                    break;
                }
            }

            if (collided) {
                mb.collisions++;

                if (mb.collisions == MAX_COLLISIONS && !mb.splitScheduled) {
                    mb.splitScheduled = true;

                    PauseTransition delay = new PauseTransition(Duration.seconds(2));
                    delay.setOnFinished(e -> {
                        if (buttons.size() < MAX_BUTTONS && !spawnButtonsAround(mb, root)) {
                            mb.splitScheduled = false;  // Не делим, если не нашли места
                        }
                    });
                    delay.play();
                }
            }
        }
    }

    private boolean spawnButtonsAround(MovingButton source, Pane root) {
        double[][] offsets = {
                {-60, -60}, {60, -60}, {-60, 60}, {60, 60}
        };

        boolean buttonsCreated = false;

        for (double[] offset : offsets) {
            int attempts = 0;
            boolean foundSpot = false;

            while (attempts < 10) {
                double newX = clamp(source.x + offset[0] + random.nextDouble() * 10 - 5, 0, WIDTH - BUTTON_SIZE);
                double newY = clamp(source.y + offset[1] + random.nextDouble() * 10 - 5, 0, HEIGHT - BUTTON_SIZE);
                MovingButton newBtn = new MovingButton(newX, newY);

                if (!intersectsAny(newBtn)) {
                    buttons.add(newBtn);
                    root.getChildren().add(newBtn.button);
                    foundSpot = true;
                    buttonsCreated = true;
                    break;
                }
                attempts++;
            }

            if (!foundSpot) {
                break;  // Если хотя бы одно место не найдено, прекращаем деление
            }
        }

        return buttonsCreated;
    }

    private boolean intersectsAny(MovingButton newBtn) {
        for (MovingButton existing : buttons) {
            if (newBtn.intersects(existing)) {
                return true;
            }
        }
        return false;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    private class MovingButton {
        Button button;
        double x, y;
        double dx, dy;
        int collisions = 0;
        boolean splitScheduled = false;

        public MovingButton(double startX, double startY) {
            this.x = startX;
            this.y = startY;
            this.dx = random.nextDouble() * 4 + 1;
            this.dy = random.nextDouble() * 4 + 1;
            this.button = new Button("Btn");
            this.button.setPrefSize(BUTTON_SIZE, BUTTON_SIZE);
            this.button.setLayoutX(x);
            this.button.setLayoutY(y);
        }

        public void move() {
            x += dx;
            y += dy;
            button.setLayoutX(x);
            button.setLayoutY(y);
        }

        public boolean intersects(MovingButton other) {
            return button.getBoundsInParent().intersects(other.button.getBoundsInParent());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
