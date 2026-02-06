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

	private int gain(Color color) {
		Queue<Cell> queue = new LinkedList<>(CPUCells);
		Set<Cell> visited = new HashSet<>(CPUCells);
		int gain = 0;
		while (!queue.isEmpty()) {
			Cell current = queue.poll();
			for (Cell neighbour : current.neighbours) {
				if (!visited.contains(neighbour) && neighbour.owner == Owner.NONE && neighbour.color.equals(color)) {
					queue.add(neighbour);
					visited.add(neighbour);
					gain++;
				}
			}
		}
		return gain;
	}

	private Color bestColor() {
		List<ColorGain> gains = new ArrayList<>();
		Color humanColor = humanCells.iterator().next().color;
		Color CPUColor = CPUCells.iterator().next().color;
		for (Color color : grid.getColors()) {
			if (color.equals(humanColor) || color.equals(CPUColor)) {
				continue;
			}
			gains.add(new ColorGain(color, gain(color)));
		}

		// Sort using Merge Sort (Divide and Conquer)
		gains = mergeSort(gains);

		if (gains.isEmpty())
			return null;
		// Return the color with max gain (first element after sort if we sort desc)
		return gains.get(0).gain > 0 ? gains.get(0).color : null;
	}

	// Helper class for sorting
	private class ColorGain {
		Color color;
		int gain;

		public ColorGain(Color color, int gain) {
			this.color = color;
			this.gain = gain;
		}
	}

	// Merge Sort Implementation
	private List<ColorGain> mergeSort(List<ColorGain> list) {
		if (list.size() <= 1)
			return list;
		int mid = list.size() / 2;
		List<ColorGain> left = mergeSort(list.subList(0, mid));
		List<ColorGain> right = mergeSort(list.subList(mid, list.size()));
		return merge(left, right);
	}

	private List<ColorGain> merge(List<ColorGain> left, List<ColorGain> right) {
		List<ColorGain> result = new ArrayList<>();
		int i = 0, j = 0;
		while (i < left.size() && j < right.size()) {
			// Sort in descending order of gain
			if (left.get(i).gain >= right.get(j).gain) {
				result.add(left.get(i++));
			} else {
				result.add(right.get(j++));
			}
		}
		while (i < left.size())
			result.add(left.get(i++));
		while (j < right.size())
			result.add(right.get(j++));
		return result;
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