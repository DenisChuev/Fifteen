package dc.fifteen;

import java.util.Random;

public class Game {

    int[][] field;
    private int size, moves;
    private Random r = new Random(System.currentTimeMillis());

    Game(int size) {
        this.size = size;
        this.field = new int[size][size];
        init();
    }

    void init() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                field[i][j] = 0;
            }
        }
        for (int i = 0, k = 1; i < size; i++)
            for (int j = 0; j < size; j++, k++) {
                if (k == size * size) continue;
                field[i][j] = k;
            }
        for (int i = 1; i < 10000; i++) {
            int x = r.nextInt(size);
            int y = r.nextInt(size);
            makeMove(x, y);
        }
        moves = 0;
    }

    boolean isWon() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (size * i + j + 1 == size * size) continue;
                if (field[i][j] != size * i + j + 1) return false;
            }
        }
        return true;
    }

    boolean makeMove(int row, int col) {
        if (field[row][col] == 0) return false;
        if (row > 0) {
            if (field[row - 1][col] == 0) {
                field[row - 1][col] = field[row][col];
                field[row][col] = 0;
                moves++;
                return true;
            }
        }
        if (row < size - 1) {
            if (field[row + 1][col] == 0) {
                field[row + 1][col] = field[row][col];
                field[row][col] = 0;
                moves++;
                return true;
            }
        }
        if (col > 0) {
            if (field[row][col - 1] == 0) {
                field[row][col - 1] = field[row][col];
                field[row][col] = 0;
                moves++;
                return true;
            }
        }
        if (col < size - 1) {
            if (field[row][col + 1] == 0) {
                field[row][col + 1] = field[row][col];
                field[row][col] = 0;
                moves++;
                return true;
            }
        }
        return false;
    }

    int getMoves() {
        return moves;
    }
}