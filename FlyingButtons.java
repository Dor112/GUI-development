package com.example.javafx17;

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
    private static final int MAX_BUTTONS = 1000;
    private static final double MIN_SEPARATION = BUTTON_SIZE * 1.2;

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
        }


        for (MovingButton mb : buttons) {
            boolean collided = false;


            if (mb.x <= 0 || mb.x + BUTTON_SIZE >= WIDTH) {
                mb.dx *= -1;
                mb.x = clamp(mb.x, 0, WIDTH - BUTTON_SIZE);
                collided = true;
            }
            if (mb.y <= 0 || mb.y + BUTTON_SIZE >= HEIGHT) {
                mb.dy *= -1;
                mb.y = clamp(mb.y, 0, HEIGHT - BUTTON_SIZE);
                collided = true;
            }


            for (MovingButton other : buttons) {
                if (other != mb && mb.intersects(other)) {
                    handleCollision(mb, other);
                    collided = true;
                    break;
                }
            }


            if (collided) {
                mb.collisions++;

                if (mb.collisions >= MAX_COLLISIONS && !mb.splitScheduled && buttons.size() < MAX_BUTTONS) {
                    mb.splitScheduled = true;

                    PauseTransition delay = new PauseTransition(Duration.seconds(0.5));
                    delay.setOnFinished(e -> {
                        if (spawnButtonsAround(mb, root)) {
                            mb.collisions = 0;
                        }
                        mb.splitScheduled = false;
                    });
                    delay.play();
                }
            }
        }
    }

    private void handleCollision(MovingButton btn1, MovingButton btn2) {

        btn1.dx *= -1;
        btn1.dy *= -1;
        btn2.dx *= -1;
        btn2.dy *= -1;


        double dx = btn2.x - btn1.x;
        double dy = btn2.y - btn1.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        double minDistance = BUTTON_SIZE;

        if (distance < minDistance) {
            double angle = Math.atan2(dy, dx);
            double targetX = btn1.x + Math.cos(angle) * minDistance;
            double targetY = btn1.y + Math.sin(angle) * minDistance;


            btn2.x = targetX;
            btn2.y = targetY;


            btn2.x = clamp(btn2.x, 0, WIDTH - BUTTON_SIZE);
            btn2.y = clamp(btn2.y, 0, HEIGHT - BUTTON_SIZE);
        }
    }

    private boolean spawnButtonsAround(MovingButton source, Pane root) {
        int newButtonsCount = 0;
        int maxAttempts = 20;
        // Пытаемся создать 4 новые кнопки вокруг исходной
        for (int i = 0; i < 4 && buttons.size() < MAX_BUTTONS; i++) {
            double angle = i * Math.PI / 2;
            double distance = BUTTON_SIZE * 1.5;

            for (int attempt = 0; attempt < maxAttempts; attempt++) {

                double offsetX = (random.nextDouble() - 0.5) * BUTTON_SIZE * 0.5;
                double offsetY = (random.nextDouble() - 0.5) * BUTTON_SIZE * 0.5;

                double newX = source.x + Math.cos(angle) * distance + offsetX;
                double newY = source.y + Math.sin(angle) * distance + offsetY;

                newX = clamp(newX, 0, WIDTH - BUTTON_SIZE);
                newY = clamp(newY, 0, HEIGHT - BUTTON_SIZE);

                MovingButton newBtn = new MovingButton(newX, newY);

                if (!intersectsAny(newBtn)) {
                    buttons.add(newBtn);
                    root.getChildren().add(newBtn.button);
                    newButtonsCount++;
                    break;
                }
            }
        }

        return newButtonsCount > 0;
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
            this.dx = (random.nextDouble() - 0.5) * 8 + 1;
            this.dy = (random.nextDouble() - 0.5) * 8 + 1;
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
            double dx = this.x - other.x;
            double dy = this.y - other.y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            return distance < BUTTON_SIZE;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
