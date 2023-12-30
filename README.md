# Puzzle Solver: BFS, Dijkstra, and A* Comparison

This project implements and compares three different algorithms: Breadth-First Search (BFS), Dijkstra's Algorithm (simulated using A* with a zero heuristic), and the A* algorithm with various heuristics, for solving the "Puzzle 15" and "Puzzle 24" games.

![alt text](https://github.com/Nadav23AnT/PuzzleSolver__15_24__Algorithems/blob/main/image_01.png)
## Overview
The "Puzzle 15" and "Puzzle 24" are sliding puzzles consisting of a frame of numbered square tiles in random order with one tile missing. The objective is to place the tiles in order by making sliding moves that use the empty space.

This project aims to explore and analyze the performance of three fundamental pathfinding algorithms:

- **Breadth-First Search (BFS)**
- **Dijkstra's Algorithm** (Implemented as A* with a zero heuristic function)
- **A* Algorithm** (With Manhattan Distance and an inadmissible heuristic)

## Features

- Generation of random puzzle states with a specified number of moves from the solved state.
- Implementation of BFS for finding the shortest path to the solution.
- Implementation of A* with different heuristic functions:
  - Zero Function (simulating Dijkstra's Algorithm)
  - Manhattan Distance
  - An inadmissible heuristic for comparison
- Performance comparison between the algorithms based on:
  - Actual running time
  - The number of developed vertices (states processed)

## Getting Started

### Prerequisites

- Java Development Kit (JDK) [Version 8 or higher]

### Running the Project

1. **Clone the repository:**

   ```sh
   git clone https://github.com/Nadav23AnT/PuzzleSolver__15_24__Algorithems.git

![alt text](https://github.com/Nadav23AnT/PuzzleSolver__15_24__Algorithems/blob/main/image_02.png)
