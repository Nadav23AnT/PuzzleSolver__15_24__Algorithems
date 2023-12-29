import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PuzzleBoard {
  private final int size; // 4 for Puzzle 15, 5 for Puzzle 24
  private int[][] board;

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
  }

  public List<PuzzleBoard> generateNeighbors() {
    List<PuzzleBoard> neighbors = new ArrayList<>();
    int[] emptySpace = findEmptySpace();

    int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } }; // up, down, left, right

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
          return new int[] { i, j };
        }
      }
    }
    throw new IllegalStateException("No empty space found");
  }

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

  private int cost; // Number of moves from the start

  public int getCost() {
    return cost;
  }

  public void setCost(int cost) {
    this.cost = cost;
  }

  public void printBoard() {
    System.out.println("---------------");
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        System.out.printf("%2d ", board[i][j]);
      }
      System.out.println();
    }
    System.out.printf("---------------");
    System.out.println();
  }

  public boolean isSolved() {
    int expectedValue = 1;
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (i == size - 1 && j == size - 1) {
          // The last cell should be the empty space (0)
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
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    PuzzleBoard that = (PuzzleBoard) obj;
    return Arrays.deepEquals(board, that.board);
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(board);
  }

  // Additional utility methods can be added here as needed
}
