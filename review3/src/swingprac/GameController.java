/*
 * GameController.java
 * Implements the core game logic for the Color Fill game with advanced AI features.
 * Review 3 enhancements: Minimax with Alpha-Beta Pruning, Undo system, Turn timer, Live scoring.
 * Manages turn alternation, cell ownership changes, win conditions, and AI decision-making.
 * Includes Minimax algorithm for AI help feature and GameState for undo functionality.
 * Author: Bhargava Srisai
 * Version: 3.0
 */

package swingprac;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

public class GameController {
	private ColorFillUI ui;
	private Grid grid;
	private Set<Cell> humanCells = new HashSet<>();
	private Set<Cell> CPUCells = new HashSet<>();
	private boolean humanTurn = true;
	private boolean gameOver = false;
	// Timer fields
	private Timer turnTimer;
	private int timeLeft = 10;

	// --- UNDO SYSTEM FIELDS ---
	private Stack<GameState> undoStack = new Stack<>();
	private int undosRemaining = 3;

	/**
	 * Inner class that captures a snapshot of the game state.
	 * Used for implementing the undo functionality by storing the configuration
	 * of owned cells, cell colors, and whose turn it is.
	 */
	private class GameState {
		Map<Cell, Color> cellColors = new HashMap<>();
		Map<Cell, Owner> cellOwners = new HashMap<>();
		Set<Cell> hCells;
		Set<Cell> cCells;
		boolean hTurn;

		/**
		 * Creates a snapshot of the current game state for undo functionality.
		 * 
		 * @param grid the current game grid
		 * @param h the set of human-owned cells
		 * @param c the set of CPU-owned cells
		 * @param turn whose turn it is (true for human, false for CPU)
		 */
		GameState(Cell[][] grid, Set<Cell> h, Set<Cell> c, boolean turn) {
			for (Cell[] row : grid) {
				for (Cell cell : row) {
					cellColors.put(cell, cell.color);
					cellOwners.put(cell, cell.owner);
				}
			}
			this.hCells = new HashSet<>(h);
			this.cCells = new HashSet<>(c);
			this.hTurn = turn;
		}
	}
	// ---------------------------

	// Score fields updating
	private void updateScores() {
		ui.updateScores(humanCells.size(), CPUCells.size());
	}
	public GameController(ColorFillUI ui) {
		this.ui = ui;
		this.grid = ui.getGrid();
		difficultyListener();
		colorListener();
		initializeGame();
		newGameListener();
		resetGameListener();
		helpListener();
		undoListener(); // New Listener
	}

	private void undoListener() {
		if (ui.getUndoBtn() != null) {
			ui.getUndoBtn().addActionListener(e -> undoMove());
		}
	}

	private void undoMove() {
		// Validate undo is allowed: stack must have state, game must not be over, and limit must be available
		if (undoStack.isEmpty() || gameOver || undosRemaining <= 0) {
			return;
		}

		// Pop the saved game state from the stack (one undo per turn only)
		GameState state = undoStack.pop();
		undosRemaining--;
		
		// Restore all cell colors and ownership from the snapshot
		for (Map.Entry<Cell, Color> entry : state.cellColors.entrySet()) {
			Cell c = entry.getKey();
			c.color = entry.getValue();
			c.owner = state.cellOwners.get(c);
		}

		// Restore player-owned cell sets and turn state
		this.humanCells = state.hCells;
		this.CPUCells = state.cCells;
		this.humanTurn = state.hTurn;

		// Update UI to reflect restored game state
		ui.updateGridUI(grid.getCells());
		updateScores();
		ui.clearHighlight();
		startTurnTimer();
		
		// Optional: Update UI with remaining count if your UI supports it
		// ui.setUndoText("Undos: " + undosRemaining); 
	}

	private void difficultyListener() {
		ui.getDifficulties().addActionListener(e -> {
			int difficulty = ui.getDifficulties().getSelectedIndex() + 1;
			int rows = 0, cols = 0;
			switch (difficulty) {
				case 1:
					rows = cols = 6;
					break;
				case 2:
					rows = cols = 8;
					break;
				case 3:
					rows = cols = 10;
					break;
			}
			ui.rebuildGrid(rows, cols, difficulty);
			grid = ui.getGrid();
			initializeGame();
			gameOver = false;
			colorListener();
		});
	}
	private void initializeGame() {
		humanCells.clear();
		CPUCells.clear();
		undoStack.clear();    // Reset undo history
		undosRemaining = 3;   // Reset match limit
		Cell[][] cells = grid.getCells();
		int rows = cells.length;
		int cols = rows;
		humanCells.add(cells[0][0]);
		CPUCells.add(cells[rows - 1][cols - 1]);
		updateScores();
		// Initialize timer if not already
		if (turnTimer == null) {
			turnTimer = new Timer(1000, e -> {
				timeLeft--;
				ui.updateTimer(timeLeft);
				if (timeLeft <= 0) {
					stopTurnTimer();
					// Skip human turn
					humanTurn = false;
					cpuTurn();
				}
			});
		}
		startTurnTimer();
	}
	private void startTurnTimer() {
		timeLeft = 15;
		ui.updateTimer(timeLeft);
		if (turnTimer != null)
			turnTimer.restart();
	}
	private void stopTurnTimer() {
		if (turnTimer != null)
			turnTimer.stop();
	}
	private void colorListener() {
		for (JButton colorBtn : ui.getColorButtons()) {
			colorBtn.addActionListener(e -> {
				if (!humanTurn || gameOver) {
					return;
				}
				Color chosenColor = colorBtn.getBackground();
				Color humanColor = humanCells.iterator().next().color;
				Color CPUColor = CPUCells.iterator().next().color;
				if (chosenColor.equals(humanColor) || chosenColor.equals(CPUColor)) {
					return;
				}

				// SAVE STATE BEFORE THE MOVE
				// Clear stack first to enforce one undo per turn limit:
				// Player can undo their current turn, but once they move again, the previous undo is lost
				undoStack.clear(); 
				undoStack.push(new GameState(grid.getCells(), humanCells, CPUCells, humanTurn));

				humanTurn(chosenColor);
			});
		}
	}
	
	/**
	 * Handles the human player's turn.
	 * Changes the color of owned cells and expands territory to adjacent matching cells.
	 * Checks for win conditions and switches turn to CPU if game continues.
	 * 
	 * @param chosenColor the color selected by the human player
	 */
	private void humanTurn(Color chosenColor) {
		stopTurnTimer();
		fillCells(humanCells, chosenColor, Owner.HUMAN);
		ui.updateGridUI(grid.getCells());
		updateScores();
		Owner winner = checkWin();
		if (winner != Owner.NONE) {
			ui.displayWinner(winner);
			gameOver = true;
			return;
		}
		humanTurn = false;
		cpuTurn();
	}
	
	/**
	 * Propagates color change through owned cells and captures adjacent cells of the same color.
	 * Uses BFS (breadth-first search) to find all connected cells that should be captured.
	 * 
	 * @param cells the set of cells currently owned by a player
	 * @param chosenColor the new color for owned cells and cells to capture
	 * @param owner the player making the move (HUMAN or CPU)
	 */
	private void fillCells(Set<Cell> cells, Color chosenColor, Owner owner) {
		Queue<Cell> queue = new LinkedList<>(cells);
		Set<Cell> visited = new HashSet<>(cells);
		while (!queue.isEmpty()) {
			Cell current = queue.poll();
			current.color = chosenColor;
			for (Cell neighbour : current.neighbours) {
				if (!visited.contains(neighbour) && neighbour.owner == Owner.NONE
						&& neighbour.color.equals(chosenColor)) {
					visited.add(neighbour);
					queue.add(neighbour);
					cells.add(neighbour);
					neighbour.owner = owner;
				}
			}
		}
	}
	
	/**
	 * Checks if either player has reached 50% of the grid to win.
	 * 
	 * @return Owner.HUMAN if human reaches 50%, Owner.CPU if CPU reaches 50%, Owner.NONE if game continues
	 */
	private Owner checkWin() {
		int totalCells = (int) Math.pow(grid.getCells().length, 2);
		if (humanCells.size() >= totalCells / 2) {
			return Owner.HUMAN;
		} else if (CPUCells.size() >= totalCells / 2) {
			return Owner.CPU;
		}
		return Owner.NONE;
	}
	
	/**
	 * Calculates how many unowned cells adjacent to CPU cells would be captured
	 * if the CPU changes to the specified color. Used for greedy AI strategy.
	 * 
	 * @param color the color to evaluate
	 * @return the number of cells that would be captured (gain) for this color choice
	 */
	private int gain(Color color) {
		Queue<Cell> queue = new LinkedList<>(CPUCells);
		Set<Cell> visited = new HashSet<>(CPUCells);
		int gain = 0;
		while(!queue.isEmpty()) {
			Cell current = queue.poll();
			for(Cell neighbour: current.neighbours) {
				if(!visited.contains(neighbour) && neighbour.owner==Owner.NONE && neighbour.color.equals(color)) {
					queue.add(neighbour);
					visited.add(neighbour);
					gain++;
				}
			}
		}
		return gain;
	}
	// picks best color for the cpu
	private Color bestColor() {
		Color bestColor = null;
		int maxGain = -1;
		Color humanColor = humanCells.iterator().next().color;
		Color CPUColor = CPUCells.iterator().next().color;
		for(Color color: grid.getColors()) {
			if(color.equals(humanColor) || color.equals(CPUColor)) {
				continue;
			}
			int gain = gain(color);
			if(gain>maxGain) {
				maxGain = gain;
				bestColor = color;
			}
		}
		return bestColor;
	}
	private void cpuTurn() {
		Color chosenColor = bestColor();
		if(chosenColor!=null) {
			fillCells(CPUCells,chosenColor,Owner.CPU);
			ui.updateGridUI(grid.getCells());
			updateScores();
		}
		Owner winner = checkWin();
		if(winner!=Owner.NONE) {
			ui.displayWinner(winner);
			gameOver = true;
			return;
		}
		humanTurn = true;
		startTurnTimer();
	}
	private void newGameListener() {
		ui.getNewGameBtn().addActionListener(e -> {
			int difficulty = ui.getDifficulties().getSelectedIndex() + 1;
			int rows = 0, cols = 0;
			switch (difficulty) {
				case 1:
					rows = cols = 6;
					break;
				case 2:
					rows = cols = 8;
					break;
				case 3:
					rows = cols = 10;
					break;
			}
			ui.rebuildGrid(rows, cols, difficulty);
			grid = ui.getGrid();
			newGame();
		});
	}
	private void newGame() {
		gameOver = false;
		humanTurn = true;
		initializeGame();
		colorListener();
		helpListener();
		ui.clearHighlight();
	}
	private void resetGameListener() {
		ui.getResetBtn().addActionListener(e -> {
			ui.resetGridUI(grid.getCells());
			grid = ui.getGrid();
			newGame();
		});
	}
	// ---- MINIMAX HELP SYSTEM ----

	private void helpListener() {
		ui.getHelpBtn().addActionListener(e -> {
			if (!humanTurn || gameOver) return;
			Color suggested = minimaxBestColor(humanCells, CPUCells, 3);
			ui.highlightSuggestedColor(suggested);
		});
	}
	
	private Color minimaxBestColor(Set<Cell> hCells, Set<Cell> cCells, int depth) {
		Color humanColor = hCells.iterator().next().color;
		Color cpuColor = cCells.iterator().next().color;
		Color bestColor = null;
		int bestScore = Integer.MIN_VALUE;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		
		for (Color color : grid.getColors()) {
			if (color.equals(humanColor) || color.equals(cpuColor)) continue;
			
			Set<Cell> newHuman = new HashSet<>(hCells);
			simulateFill(newHuman, color, Owner.HUMAN);
			int score = minimax(newHuman, cCells, depth - 1, alpha, beta, false);
			
			if (score > bestScore) {
				bestScore = score;
				bestColor = color;
			}
			alpha = Math.max(alpha, bestScore);
		}
		return bestColor;
	}
	
	/**
	 * Minimax algorithm with Alpha-Beta Pruning for evaluating future game states.
	 * Recursively explores the game tree up to a specified depth, alternating between
	 * maximizing the human's score and minimizing the CPU's score.
	 * 
	 * @param hCells current human-owned cells
	 * @param cCells current CPU-owned cells
	 * @param depth remaining depth to explore in the game tree
	 * @param alpha best value found so far for maximizing player
	 * @param beta best value found so far for minimizing player
	 * @param isMaximizing true if we're maximizing human score, false if minimizing CPU score
	 * @return the minimax score of the position
	 */
	private int minimax(Set<Cell> hCells, Set<Cell> cCells, int depth, int alpha, int beta, boolean isMaximizing) {
		// Terminal conditions: check for win states or depth limit
		int totalCells = grid.getCells().length * grid.getCells().length;
		if (hCells.size() >= totalCells / 2) return 1000; // Human wins - high score
		if (cCells.size() >= totalCells / 2) return -1000; // CPU wins - low score
		if (depth == 0) return hCells.size() - cCells.size(); // Leaf node - evaluate position
		
		// Get current colors for both players
		Color humanColor = hCells.isEmpty() ? null : hCells.iterator().next().color;
		Color cpuColor = cCells.isEmpty() ? null : cCells.iterator().next().color;
		
		if (isMaximizing) {
			// Maximizing player (Human) - looking for highest score
			int maxEval = Integer.MIN_VALUE;
			for (Color color : grid.getColors()) {
				// Skip colors already owned by either player
				if (color.equals(humanColor) || color.equals(cpuColor)) continue;
				
				// Simulate human's move with this color
				Set<Cell> newHuman = new HashSet<>(hCells);
				int before = newHuman.size();
				simulateFill(newHuman, color, Owner.HUMAN);
				// Continue only if this color captures new cells
				if (newHuman.size() == before) continue;
				
				// Recursively evaluate CPU's best response
				int eval = minimax(newHuman, cCells, depth - 1, alpha, beta, false);
				maxEval = Math.max(maxEval, eval);
				// Alpha-Beta Pruning: update alpha and prune branches
				alpha = Math.max(alpha, eval);
				if (beta <= alpha) break; // Beta cutoff - this branch can be ignored
			}
			return maxEval == Integer.MIN_VALUE ? hCells.size() - cCells.size() : maxEval;
		} else {
			// Minimizing player (CPU) - looking for lowest score
			int minEval = Integer.MAX_VALUE;
			for (Color color : grid.getColors()) {
				// Skip colors already owned by either player
				if (color.equals(humanColor) || color.equals(cpuColor)) continue;
				
				// Simulate CPU's move with this color
				Set<Cell> newCPU = new HashSet<>(cCells);
				int before = newCPU.size();
				simulateFill(newCPU, color, Owner.CPU);
				// Continue only if this color captures new cells
				if (newCPU.size() == before) continue;
				
				// Recursively evaluate human's best response
				int eval = minimax(hCells, newCPU, depth - 1, alpha, beta, true);
				minEval = Math.min(minEval, eval);
				// Alpha-Beta Pruning: update beta and prune branches
				beta = Math.min(beta, eval);
				if (beta <= alpha) break; // Alpha cutoff - this branch can be ignored
			}
			return minEval == Integer.MAX_VALUE ? hCells.size() - cCells.size() : minEval;
		}
	}
	
	private void simulateFill(Set<Cell> cells, Color chosenColor, Owner owner) {
		Queue<Cell> queue = new LinkedList<>(cells);
		Set<Cell> visited = new HashSet<>(cells);
		while (!queue.isEmpty()) {
			Cell current = queue.poll();
			for (Cell neighbour : current.neighbours) {
				if (!visited.contains(neighbour)
						&& neighbour.owner == Owner.NONE
						&& neighbour.color.equals(chosenColor)) {
					visited.add(neighbour);
					queue.add(neighbour);
					cells.add(neighbour);
				}
			}
		}
	}
}