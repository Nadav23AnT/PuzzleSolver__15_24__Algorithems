import java.util.*;
import java.util.function.Function;

public class PuzzleGraph {
  private PuzzleBoard initialState;

  public PuzzleGraph(PuzzleBoard initialState) {
    this.initialState = initialState;
  }

  public void setInitialState(PuzzleBoard initialState) {
    this.initialState = initialState;
  }

  // Manhattan Distance Heuristic
  public static final Function<PuzzleBoard, Integer> manhattanDistance = board -> {
    int distance = 0;
    int size = board.getSize();
    int[][] tiles = board.getTiles();

    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        int value = tiles[i][j];
        if (value != 0) {
          int targetX = (value - 1) / size;
          int targetY = (value - 1) % size;
          distance += Math.abs(i - targetX) + Math.abs(j - targetY);
        }
      }
    }
    return distance;
  };

  // zeroFunction
  public static final Function<PuzzleBoard, Integer> zeroFunction = board -> 0;

  // Inadmissible Heuristic
  public static final Function<PuzzleBoard, Integer> inadmissibleHeuristic = board -> {
    int distance = 0;
    int size = board.getSize();
    int[][] tiles = board.getTiles();
    double factor = 2.0;

    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        int value = tiles[i][j];
        if (value != 0) {
          int targetX = (value - 1) / size;
          int targetY = (value - 1) % size;
          distance += (int) (factor * Math.sqrt(Math.pow(i - targetX, 2) + Math.pow(j - targetY, 2)));
        }
      }
    }
    return distance;
  };

  public void solveUsingBFS() {
    long startTime = System.nanoTime();
    int statesProcessed = 0; // Counter for states processed

    Queue<PuzzleBoard> queue = new LinkedList<>();
    Set<PuzzleBoard> visited = new HashSet<>();

    queue.add(initialState);
    visited.add(initialState);

    while (!queue.isEmpty()) {
      PuzzleBoard current = queue.poll();
      statesProcessed++; // Increment state counter

      if (current.isSolved()) {
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;

        System.out.println("BFS -- Solution found! --");
        current.printBoard();
        System.out.println("Time taken: " + duration + " ms");
        System.out.println("States processed: " + statesProcessed +"\n -------------------------");
        return;
      }

      for (PuzzleBoard neighbor : current.generateNeighbors()) {
        if (!visited.contains(neighbor)) {
          queue.add(neighbor);
          visited.add(neighbor);
        }
      }
    }

    System.out.println("No solution found.");
  }

  public void solveUsingAStar(Function<PuzzleBoard, Integer> heuristicFunction) {
    long startTime = System.nanoTime();
    int statesProcessed = 0; // Counter for states processed

    PriorityQueue<PuzzleBoard> openSet = new PriorityQueue<>(
        Comparator.comparingInt(board -> board.getCost() + heuristicFunction.apply(board)));
    Map<PuzzleBoard, Integer> costSoFar = new HashMap<>();

    initialState.setCost(0);
    openSet.add(initialState);
    costSoFar.put(initialState, 0);

    while (!openSet.isEmpty()) {
      PuzzleBoard current = openSet.poll();
      statesProcessed++; // Increment state counter

      if (current.isSolved()) {
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;

        System.out.println("AStar -- Solution found! --");
        current.printBoard();
        System.out.println("Time taken: " + duration + " ms");
        System.out.println("States processed: " + statesProcessed +"\n -------------------------");
        return;
      }

      for (PuzzleBoard neighbor : current.generateNeighbors()) {
        int newCost = costSoFar.get(current) + 1;
        if (!costSoFar.containsKey(neighbor) || newCost < costSoFar.get(neighbor)) {
          neighbor.setCost(newCost);
          costSoFar.put(neighbor, newCost);
          openSet.add(neighbor);
        }
      }
    }

    System.out.println("No solution found.");
  }
  // Additional methods...
}
