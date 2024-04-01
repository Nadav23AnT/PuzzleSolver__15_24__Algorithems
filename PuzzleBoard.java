import javax.swing.JTextArea;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PuzzleBoard {
    private final int size; // 4 for Puzzle 15, 5 for Puzzle 24
    private int[][] board;
    private PuzzleBoard parent; // Parent state
    private int cost; // Number of moves from the start

    public PuzzleBoard(int size, int[][] initialState) {
        this.size = size;
        this.board = new int[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(initialState[i], 0, this.board[i], 0, size);
        }
    }

    public PuzzleBoard(PuzzleBoard other) {
        this.size = other.size;
        this.board = new int[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(other.board[i], 0, this.board[i], 0, size);
        }
        this.parent = other.parent;
        this.cost = other.cost;
    }

    public List<PuzzleBoard> generateNeighbors() {
        List<PuzzleBoard> neighbors = new ArrayList<>();
        int[] emptySpace = findEmptySpace();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // up, down, left, right

        for (int[] dir : directions) {
            int newX = emptySpace[0] + dir[0];
            int newY = emptySpace[1] + dir[1];
            if (isValidPosition(newX, newY)) {
                int[][] newBoard = deepCopyBoard();
                newBoard[emptySpace[0]][emptySpace[1]] = newBoard[newX][newY];
                newBoard[newX][newY] = 0;
                neighbors.add(new PuzzleBoard(this.size, newBoard));
            }
        }
        return neighbors;
    }

    private int[] findEmptySpace() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == 0) {
                    return new int[]{i, j};
                }
            }
        }
        throw new IllegalStateException("No empty space found");
    }

    public void printBoard(JTextArea textArea) {
        StringBuilder sb = new StringBuilder();
        sb.append("---------------\n");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sb.append(String.format("%2d ", board[i][j]));
            }
            sb.append("\n");
        }
        sb.append("---------------\n");
        textArea.append(sb.toString());
    }

    public boolean isSolved() {
        int expectedValue = 1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == size - 1 && j == size - 1) {
                    return board[i][j] == 0;
                }
                if (board[i][j] != expectedValue) {
                    return false;
                }
                expectedValue++;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PuzzleBoard that = (PuzzleBoard) obj;
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && y >= 0 && x < size && y < size;
    }

    private int[][] deepCopyBoard() {
        int[][] newBoard = new int[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, size);
        }
        return newBoard;
    }

    // Getters and Setters
    public int getSize() {
        return size;
    }

    public int[][] getTiles() {
        int[][] copy = new int[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, board[i].length);
        }
        return copy;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public PuzzleBoard getParent() {
        return parent;
    }

    public void setParent(PuzzleBoard parent) {
        this.parent = parent;
    }
}
