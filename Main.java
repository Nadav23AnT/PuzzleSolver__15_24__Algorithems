import java.util.Scanner;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Choose the puzzle type
        System.out.println("--- Welcome to Nadav's Puzzle Solver !! (BFS , DIJ , AStar) ---");
        System.out.println("Choose the puzzle type (15 or 24): ");
        int puzzleType = scanner.nextInt();
        int size = (puzzleType == 15) ? 4 : 5;

        // Choose board initialization method
        System.out.println("Enter 1 to input the board manually, 2 for a random board: ");
        int choice = scanner.nextInt();

        PuzzleBoard startBoard;
        if (choice == 1) {
            startBoard = manuallyEnterBoard(scanner, size);
        } else {
            startBoard = createRandomBoard(size, 20);
        }
        
        // Further processing (e.g., solving the puzzle) goes here
        PuzzleGraph puzzleSolver = new PuzzleGraph(startBoard);
        System.out.println("Start Vertex:");
        startBoard.printBoard();
  
        // Checking BFS Algorithem
        System.out.println("--> Checking BFS Algorithem:");
        puzzleSolver.solveUsingBFS();
        // Checking AStar Algorithem
        System.out.println("--> Checking AStar Algorithem:");
        runAndMeasure(puzzleSolver, PuzzleGraph.zeroFunction, "Zero Function (Dijkstra's)");
        puzzleSolver.setInitialState(new PuzzleBoard(startBoard)); // Reset the board
        runAndMeasure(puzzleSolver, PuzzleGraph.manhattanDistance, "Manhattan Distance");
        puzzleSolver.setInitialState(new PuzzleBoard(startBoard)); // Reset the board
        runAndMeasure(puzzleSolver, PuzzleGraph.inadmissibleHeuristic, "Inadmissible Heuristic");
        
        

        // Close the scanner
        scanner.close();
    }

    private static void runAndMeasure(PuzzleGraph puzzleSolver, Function<PuzzleBoard, Integer> heuristic,
            String heuristicName) {
        System.out.println("Testing with " + heuristicName + "\n");
        long startTime = System.nanoTime();
        puzzleSolver.solveUsingAStar(heuristic);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
        System.out.println(heuristicName + " - Time taken: " + duration + " ms\n");
    }

    private static PuzzleBoard manuallyEnterBoard(Scanner scanner, int size) {
        int[][] board = new int[size][size];
        System.out.println("Enter the board row by row:");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = scanner.nextInt();
            }
        }
        return new PuzzleBoard(size, board);
    }

    private static PuzzleBoard createRandomBoard(int size, int maxMoves) {
        // Generate the solved board
        int[][] solvedBoard = new int[size][size];
        int num = 1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                solvedBoard[i][j] = num++;
            }
        }
        solvedBoard[size - 1][size - 1] = 0; // Empty space

        PuzzleBoard board = new PuzzleBoard(size, solvedBoard);

        // Randomly decide the number of moves for shuffling
        Random random = new Random();
        int moves = random.nextInt(maxMoves + 1);

        // Apply random moves
        for (int i = 0; i < moves; i++) {
            List<PuzzleBoard> neighbors = board.generateNeighbors();
            board = neighbors.get(random.nextInt(neighbors.size()));
        }

        return board;
    }

}
