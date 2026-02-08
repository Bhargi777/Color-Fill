# Color Fill Game

A Java Swing-based interactive game where you compete against the CPU to fill a grid with colors. The player who controls the most cells when the grid is completely filled wins!

## Features

- **Interactive GUI**: Built with Java Swing for a user-friendly graphical interface
- **Multiple Difficulty Levels**: Choose from Easy, Medium, or Hard difficulty
- **Dynamic Grid Sizes**: 
  - Easy: 6x6 grid with 4 colors
  - Medium: 8x8 grid with 5 colors
  - Hard: 10x10 grid with 7 colors
- **Two-Player Mode**: Play against the CPU with alternating turns
- **Game Controls**: New Game and Reset buttons for easy gameplay management

## How to Play

1. **Starting Position**: You start at the top-left corner, CPU starts at the bottom-right corner
2. **Objective**: Fill as many grid cells as possible with your color
3. **Gameplay**: 
   - Select a color to expand your territory
   - All adjacent cells of the same color will be claimed by the player whose turn it is
   - The CPU will play optimally to maximize its own territory
   - Game ends when the entire grid is filled
4. **Winning**: The player controlling the most cells wins!

## Project Structure

```
DAA/
├── src/swingprac/
│   ├── Cell.java           - Represents a single grid cell
│   ├── Grid.java           - Manages the grid and cell initialization
│   ├── ColorFillUI.java    - Creates the graphical user interface
│   ├── GameController.java - Handles game logic and turn management
│   ├── Owner.java          - Enum for player ownership
│   └── HelloSwing.java     - Entry point of the application
└── bin/swingprac/          - Compiled bytecode
```

## Technologies Used

- **Language**: Java
- **GUI Framework**: Java Swing
- **Graphics**: AWT (Abstract Window Toolkit)

## Installation & Running

1. Ensure you have Java installed (Java 8 or higher recommended)
2. Compile the project:
   ```bash
   javac -d bin src/swingprac/*.java
   ```
3. Run the application:
   ```bash
   java -cp bin swingprac.HelloSwing
   ```

## Game Controls

- **Difficulty Dropdown**: Select Easy, Medium, or Hard
- **Color Buttons**: Click to select a color for your turn
- **New Game**: Start a fresh game with the current difficulty
- **Reset**: Reset the current game

## Algorithm Insights

The game uses graph algorithms to:
- Track cell adjacency through a neighbor-based system
- Efficiently compute connected components of the same color
- Implement game logic for both human and CPU players

## Future Enhancements

- Improved AI strategy with lookahead algorithms
- Multiplayer network support
- Score tracking and leaderboard
- Sound effects and animations
- Different board shapes and layouts


