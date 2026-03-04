# Color Fill Game - Review 3

This version expands upon Review 2 by introducing an **Undo System**, **Turn Timer**, and an **AI Help Feature** that uses advanced algorithmic analysis.

## Key Changes & Improvements from Review 2

- **Undo System**:
  - Players can undo moves up to 3 times per game.
  - Implemented via a `GameState` snapshot class that captures cell colors, ownership, and turn state.
  - Uses a `Stack<GameState>` to store the game history and restore previous states.

- **Turn Timer**:
  - Each player has 15 seconds per turn.
  - If a player doesn't move within the time limit, their turn is automatically skipped.
  - Implemented using Java Swing `Timer` with a countdown display.

- **AI Help Feature (Minimax + Alpha-Beta Pruning)**:
  - Players can click the "Help" button to receive the optimal move suggestion.
  - The suggestion is computed using the **Minimax Algorithm** with **Alpha-Beta Pruning**.
  - The algorithm explores up to depth 3, evaluating game states by comparing the human and CPU cell counts.
  - Alpha-Beta pruning eliminates branches that cannot affect the final decision, reducing computation time.

- **Difficulty Levels**:
  - Easy: 6×6 grid with 4 colors
  - Medium: 8×8 grid with 5 colors
  - Difficult: 10×10 grid with 7 colors

## Algorithm Analysis

- **Minimax with Alpha-Beta Pruning**:
  - **Time Complexity**: In the worst case, explores $O(C^D)$ nodes where $C$ is the branching factor (valid color moves) and $D$ is the depth limit (3). Alpha-Beta pruning significantly reduces this in practice by cutting off branches.
  - **Space Complexity**: $O(D)$ for the recursion call stack, where $D$ is the maximum depth (3).

- **Undo System**:
  - **Time Complexity**: $O(n)$ for state restoration, where $n$ is the number of cells on the grid.
  - **Space Complexity**: $O(n \times k)$ where $n$ is the grid size and $k$ is the number of undo states stored (max 3).

## Project Structure

```
review3/
├── src/swingprac/
│   ├── Cell.java           - Represents individual cells with color, position, and ownership
│   ├── Grid.java           - Manages grid initialization and cell neighbor connections
│   ├── ColorFillUI.java    - Creates the GUI with grid, color buttons, and control panels
│   ├── GameController.java - Implements game logic, Minimax help feature, and undo system
│   ├── Owner.java          - Enum for player ownership (HUMAN, CPU, NONE)
│   └── HelloSwing.java     - Entry point that initializes the application
└── bin/swingprac/          - Compiled bytecode
```

## Technologies Used

- **Language**: Java
- **Algorithms**: Minimax, Alpha-Beta Pruning, BFS (for cell flood-filling)
- **GUI Framework**: Java Swing
- **Data Structures**: Stack (for undo history), HashSet (for owned cells), Queue (for BFS)

## Game Features

- **Two-Player Gameplay**: Human player starts at top-left, CPU at bottom-right
- **Color Selection**: Players select colors to expand their territory
- **Flood Fill Mechanics**: When a player selects a color, all connected cells of that color become owned
- **Win Condition**: First player to own 50% of the grid wins
- **Help System**: Players can request AI suggestions powered by Minimax algorithm
- **Undo Limit**: Limited to 3 undos per game to maintain challenge

## Installation & Running

1. Compile the project:
   ```bash
   javac -d bin src/swingprac/*.java
   ```

2. Run the application:
   ```bash
   java -cp bin swingprac.HelloSwing
   ```

3. Gameplay:
   - Select a difficulty level (Easy/Medium/Difficult)
   - Click color buttons to make moves
   - Click "Help" to see the optimal move suggestion (computed via Minimax)
   - Click "Undo" to revert moves (up to 3 times per game)
   - Click "New Game" to start a fresh match
   - Click "Reset" to restart with the same grid

## Design Decisions

- **GameState Class**: Encapsulates all mutable game state for efficient snapshots during undo operations.
- **Hashcode/Equals in Cell**: Enables cells to be used reliably in HashSet/HashMap for neighbor tracking and content-based equality.
- **Minimax for Help Only**: The actual CPU player uses a greedy gain-based strategy for efficiency. Minimax is reserved for the help feature to avoid GUI blocking.
