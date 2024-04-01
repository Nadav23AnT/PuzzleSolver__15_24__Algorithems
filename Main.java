import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Random;

public class Main {
  private static JTextArea textArea;
  private static JLabel clockLabel;
  private static Timer timer;
  private static long startTime;
  private static JButton resetButton;
  private static JButton solveButton;
  private static JTextField numShakesField;

  public static void main(String[] args) {
    JFrame frame = new JFrame("Nadav's Puzzle Solver");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(700, 600);

    JPanel inputPanel = new JPanel();
    JComboBox<String> puzzleTypeComboBox = new JComboBox<>(new String[] { "15", "24" });
    JComboBox<String> initializationMethodComboBox = new JComboBox<>(new String[] { "Manually", "Random" });
    solveButton = new JButton("Solve");
    resetButton = new JButton("Reset");
    resetButton.setEnabled(false);
    clockLabel = new JLabel("Time: 0s");
    numShakesField = new JTextField(5); // Input field for the number of shakes
    numShakesField.setText("30"); // Default value

    inputPanel.add(new JLabel("Puzzle type:"));
    inputPanel.add(puzzleTypeComboBox);
    inputPanel.add(new JLabel("Initialization:"));
    inputPanel.add(initializationMethodComboBox);
    inputPanel.add(new JLabel("Num shakes:"));
    inputPanel.add(numShakesField);
    inputPanel.add(solveButton);
    inputPanel.add(resetButton);
    inputPanel.add(clockLabel);

    textArea = new JTextArea(25, 50);
    textArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(textArea);

    frame.getContentPane().add(BorderLayout.NORTH, inputPanel);
    frame.getContentPane().add(BorderLayout.CENTER, scrollPane);

    solveButton.addActionListener(new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        runSolver(puzzleTypeComboBox, initializationMethodComboBox);
      }
    });

    resetButton.addActionListener(e -> reset());
    timer = new Timer(1000, e -> updateClock());
    frame.setVisible(true);
  }

  private static void runSolver(JComboBox<String> puzzleTypeComboBox, JComboBox<String> initializationMethodComboBox) {
    int puzzleType = Integer.parseInt((String) puzzleTypeComboBox.getSelectedItem());
    int size = (puzzleType == 15) ? 4 : 5;

    PuzzleBoard startBoard;
    if ("Manually".equals(initializationMethodComboBox.getSelectedItem())) {
      startBoard = manuallyEnterBoard(size);
    } else {
      int numShakes = Integer.parseInt(numShakesField.getText().trim());
      startBoard = createRandomBoard(size, numShakes);
    }

    textArea.setText("");
    PuzzleGraph puzzleSolver = new PuzzleGraph(startBoard);
    textArea.append("Start Vertex:\n");
    startBoard.printBoard(textArea);

    startTime = System.currentTimeMillis();
    timer.start();
    resetButton.setEnabled(true);

    new Thread(() -> {
      solveButton.setEnabled(false);
      executeSolvingAlgorithms(puzzleSolver);
      solveButton.setEnabled(true);
      timer.stop();
    }).start();
  }

  public static Stats runSolverNonGUI(int puzzleSize, int numShakes, String algorithm) {
    if (textArea == null) {
      textArea = new JTextArea();
    }

    int size = puzzleSize == 15 ? 4 : 5;
    PuzzleBoard board = createRandomBoard(size, numShakes);
    PuzzleGraph puzzleSolver = new PuzzleGraph(board);

    long startTime = System.currentTimeMillis();
    switch (algorithm) {
      case "BFS":
        puzzleSolver.solveUsingBFS(textArea);
        break;
      case "DFS":
        puzzleSolver.solveUsingAStar(PuzzleGraph.zeroFunction, textArea);
        break;
      case "A*":
        puzzleSolver.solveUsingAStar(PuzzleGraph.manhattanDistance, textArea);
        break;
      case "A* Non-Admissible":
        puzzleSolver.solveUsingAStar(PuzzleGraph.nonAdmissibleHeuristic, textArea);
        break;
      // Add more cases as necessary
    }
    long endTime = System.currentTimeMillis();

    int statesProcessed = extractNumberFromString(textArea.getText(), "States processed: ");
    int movementsMade = extractNumberFromString(textArea.getText(), "Movements made: ");
    long duration = endTime - startTime;

    return new Stats(duration, statesProcessed, movementsMade);
  }

  private static int extractNumberFromString(String text, String label) {
    int startIndex = text.indexOf(label) + label.length();
    int endIndex = text.indexOf('\n', startIndex);
    if (startIndex < 0 || endIndex < 0)
      return 0; // Label not found or no newline after number
    String numberStr = text.substring(startIndex, endIndex).trim();
    try {
      return Integer.parseInt(numberStr);
    } catch (NumberFormatException e) {
      e.printStackTrace();
      return 0;
    }
  }

  private static void executeSolvingAlgorithms(PuzzleGraph puzzleSolver) {
    SwingUtilities.invokeLater(() -> textArea.append("--> Checking BFS Algorithm:\n"));
    puzzleSolver.solveUsingBFS(textArea);

    SwingUtilities
        .invokeLater(() -> textArea.append("--> Checking AStar Algorithm with Zero Function (Dijkstra's):\n"));
    try {
      Thread.sleep(100); // Small delay to ensure the title is printed before the result
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    puzzleSolver.solveUsingAStar(PuzzleGraph.zeroFunction, textArea);

    SwingUtilities.invokeLater(() -> textArea.append("--> Checking AStar Algorithm with Manhattan Distance:\n"));
    try {
      Thread.sleep(100); // Small delay
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    puzzleSolver.solveUsingAStar(PuzzleGraph.manhattanDistance, textArea);

    SwingUtilities.invokeLater(() -> textArea.append("--> Checking AStar Algorithm with Non-Admissible Heuristic:\n"));
    try {
      Thread.sleep(100); // Small delay
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    puzzleSolver.solveUsingAStar(PuzzleGraph.nonAdmissibleHeuristic, textArea);
  }

  private static void updateClock() {
    long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
    clockLabel.setText("Time: " + elapsedSeconds + "s");
  }

  private static void reset() {
    timer.stop();
    textArea.setText("");
    clockLabel.setText("Time: 0s");
    resetButton.setEnabled(false);
    solveButton.setEnabled(true);
  }

  private static PuzzleBoard createRandomBoard(int size, int maxMoves) {
    int[][] solvedBoard = new int[size][size];
    int num = 1;
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        solvedBoard[i][j] = num++;
      }
    }
    solvedBoard[size - 1][size - 1] = 0;

    PuzzleBoard board = new PuzzleBoard(size, solvedBoard);
    Random random = new Random();
    int moves = random.nextInt(maxMoves + 1);

    for (int i = 0; i < moves; i++) {
      List<PuzzleBoard> neighbors = board.generateNeighbors();
      board = neighbors.get(random.nextInt(neighbors.size()));
    }

    return board;
  }

  private static PuzzleBoard manuallyEnterBoard(int size) {
    JDialog inputDialog = new JDialog();
    inputDialog.setModal(true);
    inputDialog.setLayout(new GridLayout(size + 1, size));
    JTextField[][] textFields = new JTextField[size][size];

    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        JTextField textField = new JTextField(2);
        textFields[i][j] = textField;
        inputDialog.add(textField);
      }
    }

    JButton submitButton = new JButton("Submit");
    inputDialog.add(submitButton);
    submitButton.addActionListener(e -> {
      int[][] board = new int[size][size];
      for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
          try {
            board[i][j] = Integer.parseInt(textFields[i][j].getText().trim());
          } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(inputDialog, "Invalid input. Please enter numbers only.");
            return;
          }
        }
      }
      inputDialog.dispose();
    });

    inputDialog.pack();
    inputDialog.setVisible(true);

    int[][] board = new int[size][size];
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        board[i][j] = Integer.parseInt(textFields[i][j].getText().trim());
      }
    }
    return new PuzzleBoard(size, board);
  }
}

// A simple container class to hold statistics
class Stats {
  long time;
  int statesProcessed;
  int movementsMade;

  public Stats(long time, int statesProcessed, int movementsMade) {
    this.time = time;
    this.statesProcessed = statesProcessed;
    this.movementsMade = movementsMade;
  }
}