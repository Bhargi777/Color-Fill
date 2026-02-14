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
