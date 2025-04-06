import java.util.*;

public class TrapGame {
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

    public TrapGame(int N, int M, int K, double trapPercentage) {
        this.N = N;
        this.M = M;
        this.K = K;
        this.trapCount = (int) (N * M * trapPercentage);
        this.field = new char[N][M];
        this.traps = new boolean[N][M];
        this.opened = new boolean[N][M];
        this.moves = 0;

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

        System.out.println("Ловушки переместились!");
        updateHints();
    }

    
    private boolean openCell(int x, int y) {
        if (x < 0 || x >= N || y < 0 || y >= M || opened[x][y]) {
            System.out.println("Некорректные координаты или клетка уже открыта.");
            return false;
        }

        opened[x][y] = true;
        if (traps[x][y]) {
            field[x][y] = TRAP;
            return false;
        }

        int count = countAdjacentTraps(x, y);
        field[x][y] = (char) ('0' + count);
        return true;
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

  
    private void displayField(boolean showTraps) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (showTraps && traps[i][j]) {
                    System.out.print(TRAP + " ");
                } else {
                    System.out.print(field[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    public void play() {
        Scanner scanner = new Scanner(System.in);
        boolean gameOver = false;

        while (!gameOver) {
            System.out.println("Текущее поле:");
            displayField(false);

            System.out.print("Введите координаты клетки (строка столбец): ");
            int x = scanner.nextInt();
            int y = scanner.nextInt();

            if (!openCell(x, y)) {
                System.out.println("Вы наступили на ловушку! Игра окончена.");
                displayField(true);
                gameOver = true;
                break;
            }

            moves++;
            if (checkWin()) {
                System.out.println("Поздравляем! Вы открыли все безопасные клетки.");
                gameOver = true;
                break;
            }

            if (moves % K == 0) {
                moveTraps();
            }
        }

        scanner.close();
    }

    public static void main(String[] args) {
        int N = 5;
        int M = 5;
        int K = 3;
        double trapPercentage = 0.1;

        TrapGame game = new TrapGame(N, M, K, trapPercentage);
        game.play();
    }
}
