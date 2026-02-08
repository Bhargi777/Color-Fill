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
This folder contains significant enhancements over the first version.
- **Sorting Implementation**: Uses Merge Sort (Divide & Conquer) to rank color choices for the CPU.
- **Live Scoring**: Real-time score tracking for both players during the game.
- **Turn Timer**: A 10-second countdown for the human player to add a speed challenge.

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
