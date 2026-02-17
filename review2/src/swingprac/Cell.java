package swingprac;

import java.util.*;
import java.awt.Color;

public class Cell {
	private int row, col;
	public Color color;
	public List<Cell> neighbours;
	public Owner owner = Owner.NONE;
	public final Color initialColor;

	public Cell(int row, int col, Color color) {
		this.row = row;
		this.col = col;
		this.color = color;
		this.initialColor = color;
		neighbours = new ArrayList<>();
	}

	public void addNeighbour(Cell neighbour) {
		neighbours.add(neighbour);
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
}
