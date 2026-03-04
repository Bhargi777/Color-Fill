# Color Fill Game - Review 1

A Java Swing-based interactive game where you compete against the CPU to fill a grid with colors. The player who controls the most cells when a threshold is reached wins!

## Overview

This is the foundation version of the Color Fill Game. It features a basic GUI with game mechanics and CPU AI using a simple greedy strategy (gain-based color selection).

## Key Features

- **Interactive GUI**: Built with Java Swing for a user-friendly graphical interface
- **Multiple Difficulty Levels**: 
  - Easy: 6×6 grid with 4 colors
  - Medium: 8×8 grid with 5 colors
  - Difficult: 10×10 grid with 7 colors
- **Two-Player Gameplay**: Play against the CPU with alternating turns
- **Greedy AI Strategy**: CPU uses a gain-based approach to maximize territory expansion
- **Game Controls**: New Game and Reset buttons for easy gameplay management

## How to Play

1. **Starting Position**: Human player begins at the top-left corner, CPU starts at the bottom-right
2. **Objective**: Be the first to control 50% of the grid cells
3. **Gameplay**: 
   - Click color buttons to select a color for your turn
   - All adjacent cells of the selected color become yours (flood-fill mechanic)
   - The CPU automatically plays its turn using a greedy gain strategy
   - Game ends when one player reaches 50% grid ownership
4. **Winning**: The first player to control the majority of cells wins

## Project Structure

```
review1/
├── src/swingprac/
│   ├── Cell.java           - Represents a single grid cell with color and ownership
│   ├── Grid.java           - Manages grid initialization and cell neighbor connections
│   ├── ColorFillUI.java    - Creates the graphical user interface
│   ├── GameController.java - Implements game logic with greedy CPU strategy
│   ├── Owner.java          - Enum for player ownership (HUMAN, CPU, NONE)
│   └── HelloSwing.java     - Entry point of the application
└── bin/swingprac/          - Compiled bytecode
```

## Technologies Used

- **Language**: Java
- **GUI Framework**: Java Swing
- **Data Structures**: HashSet (for cell tracking), Queue (for BFS flood-fill)
- **Algorithms**: BFS (breadth-first search for connected components)

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

- **Difficulty Dropdown**: Select Easy, Medium, or Difficult to change grid size and colors
- **Color Buttons**: Click to select a color for your turn
- **New Game**: Start a fresh game with the selected difficulty
- **Reset**: Reset the current game back to initial state

## CPU Strategy

The CPU uses a **greedy gain-based approach**:
- For each available color, it calculates how many cells it would gain
- The color that yields the maximum gain is selected
- This strategy is computed in $O(C \times N)$ time, where $C$ is the number of colors and $N$ is the number of cells

## Design Notes

- Cells maintain neighbors via a graph adjacency list structure for efficient flood-fill operations
- The game uses BFS to propagate color changes through connected regions
- Game state tracking distinguishes between HUMAN, CPU, and NONE ownership


