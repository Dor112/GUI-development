package com.example.javafx17;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class MetroApp extends Application {

    // Настройки подключения к БД
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/metro_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "A27032006";

    private Connection dbConnection; // Переименовали переменную, чтобы избежать конфликта
    private MetroMap metroMap;
    private ComboBox<Station> startStationCombo;
    private ComboBox<Station> endStationCombo;
    private ListView<String> routesList;
    private Canvas metroCanvas;
    private GraphicsContext gc;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Подключение к базе данных
            connectToDatabase();

            // Инициализация данных метро
            metroMap = new MetroMap(dbConnection); // Используем переименованную переменную

            // Создание интерфейса
            BorderPane root = new BorderPane();
            root.setPadding(new Insets(10));

            // Верхняя панель - выбор станций
            HBox topPanel = createTopPanel();
            root.setTop(topPanel);

            // Центр - карта метро
            metroCanvas = new Canvas(800, 600);
            gc = metroCanvas.getGraphicsContext2D();
            drawMetroMap();

            // Правая панель - список маршрутов
            routesList = new ListView<>();
            routesList.setPrefWidth(300);
            routesList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    highlightRoute(newVal);
                }
            });

            ScrollPane scrollPane = new ScrollPane(metroCanvas);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);

            HBox centerPanel = new HBox(scrollPane, routesList);
            root.setCenter(centerPanel);

            // Сцена и отображение
            Scene scene = new Scene(root, 1200, 700);
            primaryStage.setTitle("Московское метро - Поиск маршрутов");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось запустить приложение: " + e.getMessage());
        }
    }

    private HBox createTopPanel() {
        HBox topPanel = new HBox(10);
        topPanel.setPadding(new Insets(10));
        topPanel.setAlignment(Pos.CENTER);

        Label startLabel = new Label("Откуда:");
        startStationCombo = new ComboBox<>();
        startStationCombo.setItems(FXCollections.observableArrayList(metroMap.getAllStations()));
        startStationCombo.setPromptText("Выберите станцию");
        startStationCombo.setPrefWidth(250);

        Label endLabel = new Label("Куда:");
        endStationCombo = new ComboBox<>();
        endStationCombo.setItems(FXCollections.observableArrayList(metroMap.getAllStations()));
        endStationCombo.setPromptText("Выберите станцию");
        endStationCombo.setPrefWidth(250);

        Button findRouteBtn = new Button("Найти маршрут");
        findRouteBtn.setOnAction(e -> findRoute());

        topPanel.getChildren().addAll(startLabel, startStationCombo, endLabel, endStationCombo, findRouteBtn);
        return topPanel;
    }

    private void connectToDatabase() throws SQLException {
        try {
            dbConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); // Используем переименованную переменную
            System.out.println("Подключение к базе данных успешно!");
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к базе данных: " + e.getMessage());
            throw e;
        }
    }

    private void findRoute() {
        Station start = startStationCombo.getValue();
        Station end = endStationCombo.getValue();

        if (start == null || end == null) {
            showAlert("Ошибка", "Пожалуйста, выберите начальную и конечную станции");
            return;
        }

        if (start.equals(end)) {
            showAlert("Ошибка", "Начальная и конечная станции совпадают");
            return;
        }

        List<Route> routes = metroMap.findRoutes(start, end);
        if (routes.isEmpty()) {
            routesList.getItems().setAll("Маршрут не найден");
        } else {
            List<String> routeDescriptions = routes.stream()
                    .map(Route::toString)
                    .collect(Collectors.toList());
            routesList.getItems().setAll(routeDescriptions);
            highlightRoute(routeDescriptions.get(0));
        }
    }

    private void drawMetroMap() {
        gc.clearRect(0, 0, metroCanvas.getWidth(), metroCanvas.getHeight());

        // Рисуем линии метро
        for (Line line : metroMap.getLines()) {
            gc.setStroke(Color.web(line.getColor()));
            gc.setLineWidth(4);

            List<Station> stations = metroMap.getStationsOnLine(line);
            int i = 0;
            for (; i < stations.size() - 1; i++) {
                Station s1 = stations.get(i);
                Station s2 = stations.get(i+1);
                gc.strokeLine(s1.getX(), s1.getY(), s2.getX(), s2.getY());
            }
            Station s1 = stations.get(i-1);
            Station s2 = stations.get(0);
            gc.strokeLine(s1.getX(), s1.getY(), s2.getX(), s2.getY());
        }

        // Рисуем станции
        for (Station station : metroMap.getAllStations()) {
            gc.setFill(Color.WHITE);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);

            double radius = station.isTransfer() ? 8:0;
            gc.fillOval(station.getX() - radius, station.getY() - radius, radius * 2, radius * 2);
            gc.strokeOval(station.getX() - radius, station.getY() - radius, radius * 2, radius * 2);

            // Подписи станций
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font(10));
            gc.fillText(station.getName(), station.getX() + 10, station.getY());
        }
    }

    private void highlightRoute(String routeDescription) {
        // Сначала рисуем обычную карту
        drawMetroMap();

        // Затем выделяем маршрут
        Route route = metroMap.findRoutes(startStationCombo.getValue(), endStationCombo.getValue())
                .stream()
                .filter(r -> r.toString().equals(routeDescription))
                .findFirst()
                .orElse(null);

        if (route != null) {
            gc.setStroke(Color.RED);
            gc.setLineWidth(6);

            List<Station> stations = route.getStations();
            for (int i = 0; i < stations.size() - 1; i++) {
                Station s1 = stations.get(i);
                Station s2 = stations.get(i+1);
                gc.strokeLine(s1.getX(), s1.getY(), s2.getX(), s2.getY());
            }

            // Выделяем станции на маршруте
            for (Station station : stations) {
                gc.setFill(Color.RED);
                double radius = station.isTransfer() ? 10 : 8;
                gc.fillOval(station.getX() - radius, station.getY() - radius, radius * 2, radius * 2);
                gc.setStroke(Color.BLACK);
                gc.strokeOval(station.getX() - radius, station.getY() - radius, radius * 2, radius * 2);
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void stop() {
        try {
            if (dbConnection != null) { // Используем переименованную переменную
                dbConnection.close();
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
        }
    }

    // Внутренние классы для модели данных
    class MetroMap {
        private List<Line> lines;
        private List<Station> stations;
        private List<MetroConnection> connections; // Переименовали класс Connection в MetroConnection
        private Connection dbConnection; // Используем полное имя класса java.sql.Connection

        public MetroMap(Connection dbConnection) throws SQLException {
            this.dbConnection = dbConnection;
            loadDataFromDatabase();
        }

        private void loadDataFromDatabase() throws SQLException {
            loadLines();
            loadStations();
            loadConnections();
        }

        private void loadLines() throws SQLException {
            lines = new ArrayList<>();
            String sql = "SELECT line_id, name, color, is_ring FROM lines";
            try (Statement stmt = dbConnection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    lines.add(new Line(
                            rs.getInt("line_id"),
                            rs.getString("name"),
                            rs.getString("color"),
                            rs.getBoolean("is_ring")
                    ));
                }
            }
        }

        private void loadStations() throws SQLException {
            stations = new ArrayList<>();
            String sql = "SELECT station_id, name, line_id, x_coordinate, y_coordinate, is_transfer FROM stations";
            try (Statement stmt = dbConnection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    stations.add(new Station(
                            rs.getInt("station_id"),
                            rs.getString("name"),
                            findLineById(rs.getInt("line_id")),
                            rs.getDouble("x_coordinate"),
                            rs.getDouble("y_coordinate"),
                            rs.getBoolean("is_transfer")
                    ));
                }
            }
        }

        private void loadConnections() throws SQLException {
            connections = new ArrayList<>();
            String sql = "SELECT connection_id, station1_id, station2_id, travel_time, is_transfer FROM connections";
            try (Statement stmt = dbConnection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    connections.add(new MetroConnection( // Используем переименованный класс
                            rs.getInt("connection_id"),
                            findStationById(rs.getInt("station1_id")),
                            findStationById(rs.getInt("station2_id")),
                            rs.getInt("travel_time"),
                            rs.getBoolean("is_transfer")
                    ));
                }
            }
        }

        public List<Line> getLines() {
            return lines;
        }

        public List<Station> getAllStations() {
            return stations;
        }

        public List<Station> getStationsOnLine(Line line) {
            return stations.stream()
                    .filter(s -> s.getLine().equals(line))
                    .sorted(Comparator.comparingDouble(Station::getId))
                    .collect(Collectors.toList());
        }

        public List<Route> findRoutes(Station start, Station end) {
            // Алгоритм BFS для поиска всех возможных маршрутов
            List<Route> routes = new ArrayList<>();
            Queue<Route> queue = new LinkedList<>();
            queue.add(new Route(Collections.singletonList(start)));

            while (!queue.isEmpty()) {
                Route current = queue.poll();
                Station lastStation = current.getLastStation();

                if (lastStation.equals(end)) {
                    routes.add(current);
                    continue;
                }

                for (MetroConnection conn : getConnections(lastStation)) { // Используем переименованный класс
                    Station neighbor = conn.getOtherStation(lastStation);
                    if (!current.containsStation(neighbor)) {
                        List<Station> newPath = new ArrayList<>(current.getStations());
                        newPath.add(neighbor);
                        queue.add(new Route(newPath));
                    }
                }
            }

            // Сортируем маршруты по количеству станций
            routes.sort(Comparator.comparingInt(r -> r.getStations().size()));
            return routes;
        }

        private List<MetroConnection> getConnections(Station station) { // Используем переименованный класс
            return connections.stream()
                    .filter(c -> c.containsStation(station))
                    .collect(Collectors.toList());
        }

        private Line findLineById(int lineId) {
            return lines.stream()
                    .filter(l -> l.getId() == lineId)
                    .findFirst()
                    .orElse(null);
        }

        private Station findStationById(int stationId) {
            return stations.stream()
                    .filter(s -> s.getId() == stationId)
                    .findFirst()
                    .orElse(null);
        }
    }

    class Line {
        private int id;
        private String name;
        private String color;
        private boolean isRing;

        public Line(int id, String name, String color, boolean isRing) {
            this.id = id;
            this.name = name;
            this.color = color;
            this.isRing = isRing;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getColor() { return color; }
        public boolean isRing() { return isRing; }
    }

    class Station {
        private int id;
        private String name;
        private Line line;
        private double x;
        private double y;
        private boolean isTransfer;

        public Station(int id, String name, Line line, double x, double y, boolean isTransfer) {
            this.id = id;
            this.name = name;
            this.line = line;
            this.x = x;
            this.y = y;
            this.isTransfer = isTransfer;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public Line getLine() { return line; }
        public double getX() { return x; }
        public double getY() { return y; }
        public boolean isTransfer() { return isTransfer; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Station station = (Station) o;
            return id == station.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return name + " (" + line.getName() + ")";
        }
    }

    // Переименовали класс Connection в MetroConnection, чтобы избежать конфликта
    class MetroConnection {
        private int id;
        private Station station1;
        private Station station2;
        private int travelTime;
        private boolean isTransfer;

        public MetroConnection(int id, Station station1, Station station2, int travelTime, boolean isTransfer) {
            this.id = id;
            this.station1 = station1;
            this.station2 = station2;
            this.travelTime = travelTime;
            this.isTransfer = isTransfer;
        }

        public boolean containsStation(Station station) {
            return station1.equals(station) || station2.equals(station);
        }

        public Station getOtherStation(Station station) {
            if (station1.equals(station)) return station2;
            if (station2.equals(station)) return station1;
            return null;
        }
    }

    class Route {
        private List<Station> stations;

        public Route(List<Station> stations) {
            this.stations = stations;
        }

        public List<Station> getStations() {
            return stations;
        }

        public Station getLastStation() {
            return stations.get(stations.size() - 1);
        }

        public boolean containsStation(Station station) {
            return stations.contains(station);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Маршрут (").append(stations.size()).append(" станций):\n");

            Station prev = null;
            for (Station station : stations) {
                if (prev != null && !prev.getLine().equals(station.getLine())) {
                    sb.append(" -> Пересадка на ").append(station.getLine().getName()).append("\n");
                }
                sb.append(station.getName()).append("\n");
                prev = station;
            }

            return sb.toString();
        }
    }
}
