import java.util.*;

class Edge {
    int target;
    int weight;

    Edge(int target, int weight) {
        this.target = target;
        this.weight = weight;
    }
}

public class ShortestPath {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        System.out.print("Введите количество комнат (вершин): ");
        int n = scanner.nextInt();

        List<List<Edge>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        System.out.print("Введите количество коридоров (рёбер): ");
        int edgesCount = scanner.nextInt();

        System.out.println("Введите рёбра в формате (u v w), где u и v — номера комнат, w — длина коридора:");
        for (int i = 0; i < edgesCount; i++) {
            int u = scanner.nextInt();
            int v = scanner.nextInt();
            int w = scanner.nextInt();
            graph.get(u).add(new Edge(v, w));
        }


        System.out.print("Введите начальную комнату: ");
        int start = scanner.nextInt();
        System.out.print("Введите комнату с сокровищем(меньшее чем количество комнат): ");
        int end = scanner.nextInt();


        int shortestPathLength = dijkstra(graph, start, end, n);

        if (shortestPathLength == Integer.MAX_VALUE) {
            System.out.println("Сокровище недостижимо");
        } else {
            System.out.println("Длина кратчайшего пути: " + shortestPathLength);
        }

        scanner.close();
    }

    public static int dijkstra(List<List<Edge>> graph, int start, int end, int n) {
        int[] distances = new int[n];
        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[start] = 0;

        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));
        pq.add(new Edge(start, 0));

        while (!pq.isEmpty()) {
            Edge current = pq.poll();
            int currentVertex = current.target;

            if (currentVertex == end) {
                break;
            }

            for (Edge edge : graph.get(currentVertex)) {
                int newDist = distances[currentVertex] + edge.weight;
                if (newDist < distances[edge.target]) {
                    distances[edge.target] = newDist;
                    pq.add(new Edge(edge.target, newDist));
                }
            }
        }

        return distances[end];
    }
}