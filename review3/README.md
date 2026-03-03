# Color Fill Game - Review 3

This version expands upon Review 2 by introducing advanced Artificial Intelligence concepts to the CPU player.

## Key Changes & Improvements from Review 2

- **Backtracking Logic Implementation**: 
  - Replaced the previous greedy "Merge Sort" approach with a full **Minimax Algorithm**.
  - The CPU now simulates future game states (up to 3 moves ahead) to make optimal decisions, rather than just looking at immediate gain.
  - Recursion is used to explore the game tree, alternating between maximizing the CPU's score and minimizing the Human's score.

- **Dynamic Programming (DP) Logic Implementation**:
  - Implemented **Memoization** to optimize the Minimax algorithm.
  - A `HashMap` stores evaluations of previously encountered game states (keyed by the unique configuration of owned cells and colors).
  - This drastically reduces redundant calculations, strictly adhering to DP principles by solving overlapping subproblems only once.

## Algorithm Analysis

- **Time Complexity (Minimax + DP)**: The Minimax algorithm explores the game tree up to a maximum depth of 3 (`MAX_DEPTH=3`). Without optimization, it would evaluate in $O(C^D)$ time, where $C$ is the number of valid color moves and $D$ is the depth limit. By applying **Alpha-Beta Pruning** and **Memoization** (which prevents the recalculation of duplicate game states), average-case performance approaches near-instant evaluation, allowing the GUI thread to remain responsive.
- **Space Complexity**: Space corresponds mainly to the maximum recursion depth, which is $O(D)$, along with the DP `HashMap` states. The map's memory usage is bounded by the number of unique game configurations evaluated, yielding an overall space complexity of $O(S + D)$ where $S$ is the state memory size. 

## Individual Contribution

- **Bhargava Srisai**: Handled 100% of the Review 3 integration. This includes the formulation and translation of the Backtracking pattern into the AI's core logic (`Minimax`), applying the Dynamic Programming enhancement (`HashMap` memoization schema), establishing Alpha-Beta limits for branch culling, and properly adapting `GameController.java` to support these algorithmic operations.

## Project Structure

```
review3/
├── src/swingprac/
│   ├── Cell.java           - Updated with hashCode/equals for state tracking
│   ├── Grid.java           - Manages the grid and cell initialization
│   ├── ColorFillUI.java    - Creates the graphical user interface
│   ├── GameController.java - Implements Minimax and Memoization
│   ├── Owner.java          - Enum for player ownership
│   └── HelloSwing.java     - Entry point of the application
└── bin/swingprac/          - Compiled bytecode
```

## Technologies Used

- **Language**: Java
- **Algorithms**: Minimax (Backtracking), Memoization (DP)
- **GUI Framework**: Java Swing

## Installation & Running

1. Compile the project:
   ```bash
   javac -d bin src/swingprac/*.java
   ```
2. Run the application:
   ```bash
   java -cp bin swingprac.HelloSwing
   ```
