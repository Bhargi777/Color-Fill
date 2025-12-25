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
			int difficulty = ui.getDifficulties().getSelectedIndex()+1;
			int rows = 0, cols = 0;
			switch(difficulty) {
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
		CPUCells.add(cells[rows-1][cols-1]);

	}
	private void colorListener() {
		for(JButton colorBtn : ui.getColorButtons()) {
			colorBtn.addActionListener(e -> {
				if(!humanTurn || gameOver) {
					return;
				}
				Color chosenColor = colorBtn.getBackground();
				Color humanColor = humanCells.iterator().next().color;
				Color CPUColor = CPUCells.iterator().next().color;
				if(chosenColor.equals(humanColor) || chosenColor.equals(CPUColor)) {
					return;
				}
				humanTurn(chosenColor);
			});
		}
	}
	private void humanTurn(Color chosenColor) {
		fillCells(humanCells, chosenColor, Owner.HUMAN);
		ui.updateGridUI(grid.getCells());
		Owner winner = checkWin();
		if(winner!=Owner.NONE) {
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
		while(!queue.isEmpty()) {
			Cell current = queue.poll();
			current.color = chosenColor;
			for(Cell neighbour: current.neighbours) {
				if(!visited.contains(neighbour) && neighbour.owner==Owner.NONE && neighbour.color.equals(chosenColor)) {
					visited.add(neighbour);
					queue.add(neighbour);
					cells.add(neighbour);
					neighbour.owner = owner;
				}
			}
		}
	}
	private Owner checkWin() {
		int totalCells = (int)Math.pow(grid.getCells().length, 2);
		if(humanCells.size()>=totalCells/2) {
			return Owner.HUMAN;
		}
		else if(CPUCells.size()>=totalCells/2) {
			return Owner.CPU;
		}
		return Owner.NONE;
	}
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
		}
		Owner winner = checkWin();
		if(winner!=Owner.NONE) {
			ui.displayWinner(winner);
			gameOver = true;
			return;
		}
		humanTurn = true;
	}
	private void newGameListener() {
		ui.getNewGameBtn().addActionListener(e ->{
			int difficulty = ui.getDifficulties().getSelectedIndex()+1;
			int rows = 0, cols = 0;
			switch(difficulty) {
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