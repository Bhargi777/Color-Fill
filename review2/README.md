# Color Fill Game - Review 2

This version expands upon Review 1 by introducing **real-time scoring**, **turn timers**, an **undo system**, and an **AI help feature**.

## Key Changes & Improvements from Review 1

- **Live Score Display**:
  - Real-time score tracking for both Human and CPU displayed at the top of the window
  - Scores update immediately after every move
  - Players can monitor their progress throughout the game

- **Turn Timer**:
  - Each player has 15 seconds per turn to make their move
  - If the timer reaches zero, the player's turn is automatically skipped
  - Adds urgency and strategy to decision-making

- **Undo System**:
  - Players can undo moves up to 3 times per game
  - Implemented via a `GameState` snapshot class that captures cell colors, ownership, and turn state
  - Uses a `Stack<GameState>` to store and restore previous game states
  - Undo stack resets when creating a new game

- **AI Help Feature (Minimax)**:
  - Players can click the "Help" button to receive an optimal move suggestion
  - The suggestion uses the **Minimax Algorithm** to explore future game states
  - Evaluates moves up to depth 3, computing which choice leads to the best outcome

## Project Structure

```
review2/
├── src/swingprac/
│   ├── Cell.java           - Represents individual cells with color, position, and ownership  
│   ├── Grid.java           - Manages grid initialization and cell neighbor connections
│   ├── ColorFillUI.java    - Creates GUI with grid, color buttons, score display, and timer
│   ├── GameController.java - Implements game logic, undo system, and Minimax help feature
│   ├── Owner.java          - Enum for player ownership (HUMAN, CPU, NONE)
│   └── HelloSwing.java     - Entry point that initializes the application
└── bin/swingprac/          - Compiled bytecode
```

## Technologies Used

- **Language**: Java
- **GUI Framework**: Java Swing
- **Algorithms**: Minimax (for help feature), BFS (for flood-fill)
- **Data Structures**: Stack (undo history), HashSet (owned cells), Queue (BFS)

## Installation & Running

1. Ensure you have Java installed (Java 8 or higher)
2. Compile the project:
   ```bash
   javac -d bin src/swingprac/*.java
   ```

3. Run the application:
   ```bash
   java -cp bin swingprac.HelloSwing
   ```

## Game Controls

- **Difficulty Dropdown**: Select Easy (6×6), Medium (8×8), or Difficult (10×10)
- **Color Buttons**: Click to select a color for your turn
- **Help Button**: Click to see the optimal move suggestion (computed via Minimax)
- **Undo Button**: Click to revert your last move (limited to 3 undos per game)
- **New Game**: Start a fresh game with the selected difficulty
- **Reset**: Reset the current game back to initial state

## Features

- **Timer Display**: Shows countdown for current turn
- **Score Display**: Shows live cell count for both players
- **Difficulty Adaptation**: Grid size and color palette change with difficulty selection
- **Visual Highlighting**: Help button highlights the suggested color for your move

## Algorithm Analysis

- **Minimax for Help Feature**:
  - **Time Complexity**: Explores $O(C^D)$ nodes in the game tree where $C$ is the branching factor and $D$ is depth limit (3)
  - **Space Complexity**: $O(D)$ for recursion stack, where $D = 3$
  - Provides near-instant suggestions while keeping the GUI responsive

- **Undo System**:
  - **Time Complexity**: $O(n)$ for state restoration, where $n$ is grid size
  - **Space Complexity**: $O(n \times k)$ where $k$ is number of states stored (max 3)

## Design Decisions

- **Separate CPU Strategy**: The actual CPU player still uses the simple greedy gain-based strategy for efficiency. Minimax is reserved for the help feature only.
- **GameState Snapshots**: Encapsulates all mutable game state for efficient undo operations
- **Stack-based Undo**: Limits undos to one per turn by clearing the stack after each undo
- **Timer Implementation**: Uses Java Swing Timer for responsive countdown without blocking the GUI
