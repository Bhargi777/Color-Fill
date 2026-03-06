# Color Fill Game

This repository contains the progression of the Color Fill game, a Java Swing-based interactive game where you compete against the CPU to fill a grid with colors.

## Repository Structure

The project is organized into the following folders representing different stages of development:

### 1. review1
This folder contains the foundation of the Color Fill game.
- **Core Logic**: Implements the base gameplay using Breadth-First Search (BFS) for the flood-fill mechanism.
- **GUI**: Initial Swing interface with grid rendering and color selection.
- **CPU**: A basic greedy AI that picks colors based on immediate gain.
- **Difficulty**: Support for Easy, Medium, and Hard grid sizes.

### 2. review2
This folder contains significant enhancements over the first version, including Merge Sort and live game features.
- **Sorting Implementation**: Uses Merge Sort (Divide & Conquer) to rank color choices for the CPU.
- **Live Scoring**: Real-time score tracking for both players during the game.
- **Turn Timer**: A 10-second countdown for the human player to add a speed challenge.

## 3. review3
This folder contains algorithmic optimizations and enhanced user interaction features, focusing on efficiency improvements and reversible gameplay.
- **Alpha-Beta Pruning**: Implements branch-and-bound optimization in the Minimax algorithm, introducing alpha (maximizer bound) and beta (minimizer bound) to terminate unpromising branches early. Reduces nodes explored from 125 to ~85.
- **Undo Functionality**: Stack-based state management system using deep-copy snapshots, allowing one undo per turn with a maximum of 3 undos per game.
- **Complexity Reduction**: Minimax search reduced from O(k^d × N²) to O(k^(d/2) × N²) in best case, achieving 2-5× performance improvement.
- **State Management**: HashMap-based storage of complete game state (cell colors, ownership, player territories) with O(N²) time and space complexity per snapshot.


## How to Run

Each folder contains its own source code and build instructions. Generally, to run the game from within either folder:

1. Compile the code:
   ```bash
   javac -d bin src/swingprac/*.java
   ```
2. Run the application:
   ```bash
   java -cp bin swingprac.HelloSwing
   ```
