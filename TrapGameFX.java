package com.example.javafx17;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class HelloApplication extends Application {
    private static final char CLOSED = '#';
    private static final char TRAP = '*';
    private static final int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
    private static final int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

    private int N, M;
    private int K;
    private int trapCount;
    private char[][] field;
    private boolean[][] traps;
    private boolean[][] opened;
    private int moves;
    private Button[][] buttons;
    private Label movesLabel;
    private GridPane gridPane;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showSettingsDialog();
    }

    private void initializeGame(int width, int height, int moveInterval, double trapPercentage) {
        this.N = width;
        this.M = height;
        this.K = moveInterval;
        this.trapCount = (int) (N * M * trapPercentage);
        this.field = new char[N][M];
        this.traps = new boolean[N][M];
        this.opened = new boolean[N][M];
        this.moves = 0;
        this.buttons = new Button[N][M];

        initializeField();
        placeTraps();
        updateHints();
    }

    private void initializeField() {
        for (int i = 0; i < N; i++) {
            Arrays.fill(field[i], CLOSED);
        }
    }

    private void placeTraps() {
        Random random = new Random();
        int trapsPlaced = 0;

        while (trapsPlaced < trapCount) {
            int x = random.nextInt(N);
            int y = random.nextInt(M);

            if (!traps[x][y]) {
                traps[x][y] = true;
                trapsPlaced++;
            }
        }
    }

    private void updateHints() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (opened[i][j] && !traps[i][j]) {
                    int count = countAdjacentTraps(i, j);
                    field[i][j] = (char) ('0' + count);
                    if (opened[i][j]){
                        buttons[i][j].setText(String.valueOf(field[i][j]));
                    }
                }
            }
        }
    }

    private int countAdjacentTraps(int x, int y) {
        int count = 0;
        for (int i = 0; i < 8; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            if (nx >= 0 && nx < N && ny >= 0 && ny < M && traps[nx][ny]) {
                count++;
            }
        }
        return count;
    }

    private void moveTraps() {
        Random random = new Random();
        List<int[]> trapPositions = new ArrayList<>();

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (traps[i][j]) {
                    trapPositions.add(new int[]{i, j});
                    traps[i][j] = false;
                }
            }
        }

        for (int[] pos : trapPositions) {
            int x, y;
            do {
                x = random.nextInt(N);
                y = random.nextInt(M);
            } while (traps[x][y] || opened[x][y]);
            traps[x][y] = true;
        }
        updateHints();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ловушки переместились!");
        alert.setHeaderText(null);
        alert.setContentText("Ловушки переместились после " + K + " ходов!");
        alert.showAndWait();
    }

    private void openCell(int x, int y) {
        if (opened[x][y]) {
            return;
        }

        opened[x][y] = true;
        buttons[x][y].setDisable(true);

        if (traps[x][y]) {
            field[x][y] = TRAP;
            buttons[x][y].setStyle("-fx-background-color: red; -fx-text-fill: white;");
            buttons[x][y].setText(String.valueOf(TRAP));
            gameOver(false);
            return;
        }

        int count = countAdjacentTraps(x, y);
        field[x][y] = (char) ('0' + count);
        buttons[x][y].setText(String.valueOf(field[x][y]));

        moves++;
        movesLabel.setText("Ходы: " + moves);

        if (checkWin()) {
            gameOver(true);
            return;
        }

        if (moves % K == 0) {
            moveTraps();
            updateButtonStates();
        }
    }

    private boolean checkWin() {
        int safeCells = N * M - trapCount;
        int openedSafeCells = 0;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (opened[i][j] && !traps[i][j]) {
                    openedSafeCells++;
                }
            }
        }

        return openedSafeCells == safeCells;
    }

    private void createButtons() {
        gridPane.getChildren().clear();

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                Button button = new Button(String.valueOf(field[i][j]));
                button.setMinSize(40, 40);
                button.setStyle("-fx-font-weight: bold;");

                final int x = i;
                final int y = j;

                button.setOnAction(e -> openCell(x, y));

                buttons[i][j] = button;
                gridPane.add(button, j, i);
            }
        }
    }

    private void updateButtonStates() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (opened[i][j]) {
                    buttons[i][j].setText(String.valueOf(field[i][j]));
                    buttons[i][j].setDisable(true);

                } else {
                    buttons[i][j].setText(String.valueOf(CLOSED));
                    buttons[i][j].setDisable(false);

                }
            }
        }
    }

    private void gameOver(boolean won) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                buttons[i][j].setDisable(true);
                if (traps[i][j]) {
                    buttons[i][j].setText(String.valueOf(TRAP));
                    buttons[i][j].setStyle("-fx-background-color: #ffcccc;");
                }
            }
        }

        Alert alert = new Alert(won ? Alert.AlertType.CONFIRMATION : Alert.AlertType.ERROR);
        alert.setTitle("Игра окончена");
        alert.setHeaderText(null);
        alert.setContentText(won ? "Поздравляем! Вы выиграли!" : "Вы проиграли! Попались в ловушку.");

        alert.showAndWait();
    }

    private void showSettingsDialog() {
        Stage settingsStage = new Stage();
        settingsStage.setTitle("Настройки игры");

        GridPane settingsPane = new GridPane();
        settingsPane.setAlignment(Pos.CENTER);
        settingsPane.setHgap(10);
        settingsPane.setVgap(10);
        settingsPane.setPadding(new Insets(20));

        TextField widthField = new TextField("5");
        TextField heightField = new TextField("5");
        TextField moveIntervalField = new TextField("3");
        Slider trapPercentageSlider = new Slider(5, 30, 10);
        trapPercentageSlider.setShowTickLabels(true);
        trapPercentageSlider.setShowTickMarks(true);
        trapPercentageSlider.setMajorTickUnit(5);
        trapPercentageSlider.setMinorTickCount(1);
        trapPercentageSlider.setSnapToTicks(true);

        settingsPane.add(new Label("Ширина поля:"), 0, 0);
        settingsPane.add(widthField, 1, 0);
        settingsPane.add(new Label("Высота поля:"), 0, 1);
        settingsPane.add(heightField, 1, 1);
        settingsPane.add(new Label("Интервал перемещения ловушек:"), 0, 2);
        settingsPane.add(moveIntervalField, 1, 2);
        settingsPane.add(new Label("Процент ловушек:"), 0, 3);
        settingsPane.add(trapPercentageSlider, 1, 3);

        Button startButton = new Button("Начать игру");
        startButton.setOnAction(e -> {
            try {
                int width = Integer.parseInt(widthField.getText());
                int height = Integer.parseInt(heightField.getText());
                int moveInterval = Integer.parseInt(moveIntervalField.getText());
                double trapPercentage = trapPercentageSlider.getValue() / 100.0;

                if (width < 3 || width > 20 || height < 3 || height > 20) {
                    showAlert("Ошибка", "Размер поля должен быть от 3 до 20");
                    return;
                }

                if (moveInterval < 1) {
                    showAlert("Ошибка", "Интервал перемещения должен быть положительным");
                    return;
                }

                initializeGame(width, height, moveInterval, trapPercentage);
                startGame();
                settingsStage.close();
            } catch (NumberFormatException ex) {
                showAlert("Ошибка", "Пожалуйста, введите корректные числовые значения");
            }
        });

        settingsPane.add(startButton, 0, 4, 2, 1);

        Scene settingsScene = new Scene(settingsPane);
        settingsStage.setScene(settingsScene);
        settingsStage.showAndWait();
    }

    private void startGame() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);

        movesLabel = new Label("Ходы: 0");
        movesLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        createButtons();

        root.getChildren().addAll(movesLabel, gridPane);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Игра Ловушки");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
