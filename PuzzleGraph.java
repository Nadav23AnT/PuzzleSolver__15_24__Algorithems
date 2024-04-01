import javax.swing.JTextArea;
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
    public static final Function<PuzzleBoard, Integer> nonAdmissibleHeuristic = board -> {
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
        return distance * 2;
    };

    public void solveUsingBFS(JTextArea textArea) {
        long startTime = System.nanoTime();
        int statesProcessed = 0;

        Queue<PuzzleBoard> queue = new LinkedList<>();
        Set<PuzzleBoard> visited = new HashSet<>();

        initialState.setCost(0);
        queue.add(initialState);
        visited.add(initialState);

        while (!queue.isEmpty()) {
            PuzzleBoard current = queue.poll();
            statesProcessed++;

            if (current.isSolved()) {
                long endTime = System.nanoTime();
                long duration = (endTime - startTime) / 1_000_000;
                int movementsCount = calculateMovements(current);

                textArea.append("BFS -- Solution found! --\n");
                current.printBoard(textArea);
                textArea.append("Time taken: " + duration + " ms\n");
                textArea.append("States processed: " + statesProcessed + "\n");
                textArea.append("Movements made: " + movementsCount + "\n");
                textArea.append("-------------------------\n");
                return;
            }

            for (PuzzleBoard neighbor : current.generateNeighbors()) {
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor);
                    visited.add(neighbor);
                    neighbor.setParent(current);
                }
            }
        }

        textArea.append("No solution found.\n");
    }

    public void solveUsingAStar(Function<PuzzleBoard, Integer> heuristicFunction, JTextArea textArea) {
        long startTime = System.nanoTime();
        int statesProcessed = 0;

        PriorityQueue<PuzzleBoard> openSet = new PriorityQueue<>(
                Comparator.comparingInt(board -> board.getCost() + heuristicFunction.apply(board)));
        Map<PuzzleBoard, Integer> costSoFar = new HashMap<>();

        initialState.setCost(0);
        openSet.add(initialState);
        costSoFar.put(initialState, 0);

        while (!openSet.isEmpty()) {
            PuzzleBoard current = openSet.poll();
            statesProcessed++;

            if (current.isSolved()) {
                long endTime = System.nanoTime();
                long duration = (endTime - startTime) / 1_000_000;
                int movementsCount = calculateMovements(current);

                textArea.append("AStar -- Solution found! --\n");
                current.printBoard(textArea);
                textArea.append("Time taken: " + duration + " ms\n");
                textArea.append("States processed: " + statesProcessed + "\n");
                textArea.append("Movements made: " + movementsCount + "\n");
                textArea.append("-------------------------\n");
                return;
            }

            for (PuzzleBoard neighbor : current.generateNeighbors()) {
                int newCost = costSoFar.get(current) + 1;
                if (!costSoFar.containsKey(neighbor) || newCost < costSoFar.get(neighbor)) {
                    neighbor.setCost(newCost);
                    costSoFar.put(neighbor, newCost);
                    openSet.add(neighbor);
                    neighbor.setParent(current);
                }
            }
        }

        textArea.append("No solution found.\n");
    }

    private int calculateMovements(PuzzleBoard solutionState) {
        int movementsCount = 0;
        PuzzleBoard currentState = solutionState;
        while (currentState != null && currentState.getParent() != null) {
            movementsCount++;
            currentState = currentState.getParent();
        }
        return movementsCount;
    }
}
