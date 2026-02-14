package swingprac;

import java.util.*;
import javax.swing.*;
import java.awt.*;

public class GameController {
	private ColorFillUI ui;
	private Grid grid;
	private Set<Cell> humanCells = new HashSet<>();
	private Set<Cell> CPUCells = new HashSet<>();
	private boolean humanTurn = true;
	private boolean gameOver = false;

	// Timer fields
	private javax.swing.Timer turnTimer;
	private int timeLeft = 10;

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
		Cell[][] cells = grid.getCells();
		int rows = cells.length;
		int cols = rows;
		humanCells.add(cells[0][0]);
		CPUCells.add(cells[rows - 1][cols - 1]);

		updateScores();

		// Initialize timer if not already
		if (turnTimer == null) {
			turnTimer = new javax.swing.Timer(1000, e -> {
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
		timeLeft = 10;
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
				humanTurn(chosenColor);
			});
		}
	}

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

	private Owner checkWin() {
		int totalCells = (int) Math.pow(grid.getCells().length, 2);
		if (humanCells.size() >= totalCells / 2) {
			return Owner.HUMAN;
		} else if (CPUCells.size() >= totalCells / 2) {
			return Owner.CPU;
		}
		return Owner.NONE;
	}

	// DP Memoization Map
	private Map<String, Integer> memo = new HashMap<>();
	private static final int MAX_DEPTH = 3;

	private Color bestColor() {
		memo.clear();
		// Initial call for Minimax
		Color bestMove = null;
		int maxEval = Integer.MIN_VALUE;

		Color humanColor = humanCells.iterator().next().color;
		Color cpuColor = CPUCells.iterator().next().color;

		List<Color> possibleMoves = new ArrayList<>();
		for (Color color : grid.getColors()) {
			if (!color.equals(humanColor) && !color.equals(cpuColor)) {
				possibleMoves.add(color);
			}
		}

		for (Color color : possibleMoves) {
			// Simulate move
			Set<Cell> nextCpuCells = simulateMove(CPUCells, humanCells, color);

			// If no gain, maybe skip to save time? But sometimes 0 gain is necessary.
			// Optimization: Sort moves by immediate gain?

			int eval = minimax(nextCpuCells, humanCells, color, humanColor, MAX_DEPTH, false, Integer.MIN_VALUE,
					Integer.MAX_VALUE);

			if (eval > maxEval) {
				maxEval = eval;
				bestMove = color;
			}
		}

		return bestMove;
	}

	// Backtracking / Minimax Implementation
	private int minimax(Set<Cell> cpuSet, Set<Cell> humanSet, Color cpuColor, Color humanColor, int depth,
			boolean isMaximizing, int alpha, int beta) {
		// Create a unique key for DP
		// Using hash codes of sets + colors + depth + turn
		String key = cpuSet.hashCode() + "-" + humanSet.hashCode() + "-" + cpuColor.hashCode() + "-"
				+ humanColor.hashCode() + "-" + depth + "-" + isMaximizing;
		if (memo.containsKey(key)) {
			return memo.get(key);
		}

		// Base case
		if (depth == 0 || isGameOver(cpuSet, humanSet)) {
			int score = evaluateState(cpuSet, humanSet);
			memo.put(key, score);
			return score;
		}

		List<Color> validColors = new ArrayList<>();
		for (Color c : grid.getColors()) {
			if (!c.equals(cpuColor) && !c.equals(humanColor)) {
				validColors.add(c);
			}
		}

		if (isMaximizing) {
			int maxEval = Integer.MIN_VALUE;
			for (Color color : validColors) {
				Set<Cell> nextCpuSet = simulateMove(cpuSet, humanSet, color);
				int eval = minimax(nextCpuSet, humanSet, color, humanColor, depth - 1, false, alpha, beta);
				maxEval = Math.max(maxEval, eval);
				alpha = Math.max(alpha, eval);
				if (beta <= alpha)
					break;
			}
			memo.put(key, maxEval);
			return maxEval;
		} else {
			int minEval = Integer.MAX_VALUE;
			for (Color color : validColors) {
				Set<Cell> nextHumanSet = simulateMove(humanSet, cpuSet, color);
				int eval = minimax(cpuSet, nextHumanSet, cpuColor, color, depth - 1, true, alpha, beta);
				minEval = Math.min(minEval, eval);
				beta = Math.min(beta, eval);
				if (beta <= alpha)
					break;
			}
			memo.put(key, minEval);
			return minEval;
		}
	}

	private Set<Cell> simulateMove(Set<Cell> currentOwnerCells, Set<Cell> opponentCells, Color color) {
		Set<Cell> newOwned = new HashSet<>(currentOwnerCells);
		Queue<Cell> queue = new LinkedList<>(currentOwnerCells);
		Set<Cell> visited = new HashSet<>(currentOwnerCells); // To avoid reprocessing

		// In strictly simulation, we need to consider how flood fill works.
		// The `currentOwnerCells` are ALREADY owned. Changing their color to `color`
		// might capture neighbors that match `color`.

		while (!queue.isEmpty()) {
			Cell u = queue.poll();
			for (Cell v : u.neighbours) {
				if (!visited.contains(v) && !newOwned.contains(v) && !opponentCells.contains(v)) {
					// Check if unowned cell matches the chosen color
					// Note: unowned cells' effective color is their initialColor
					if (v.initialColor.equals(color)) {
						newOwned.add(v);
						queue.add(v);
						visited.add(v);
					}
				}
			}
		}
		return newOwned;
	}

	private boolean isGameOver(Set<Cell> cpu, Set<Cell> human) {
		int total = grid.getCells().length * grid.getCells()[0].length;
		return (cpu.size() + human.size()) == total; // Or close enough logic
	}

	private int evaluateState(Set<Cell> cpu, Set<Cell> human) {
		return cpu.size() - human.size();
	}

	private void cpuTurn() {
		Color chosenColor = bestColor();
		if (chosenColor != null) {
			fillCells(CPUCells, chosenColor, Owner.CPU);
			ui.updateGridUI(grid.getCells());
		}
		updateScores();
		Owner winner = checkWin();
		if (winner != Owner.NONE) {
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
	}

	private void resetGameListener() {
		ui.getResetBtn().addActionListener(e -> {
			ui.resetGridUI(grid.getCells());
			grid = ui.getGrid();
			newGame();
		});
	}
}