/*
 * Cell.java
 * Represents a single cell in the game grid.
 * Each cell has a color, position, owner, and maintains references to neighboring cells.
 * Implements proper equals() and hashCode() for use in collections and state tracking.
 * Author: Bhargava Srisai
 * Version: 3.0
 */

package swingprac;

import java.util.*;
import java.awt.Color;

public class Cell {
	private int row, col;
	public Color color;
	public List<Cell> neighbours;
	public Owner owner = Owner.NONE;
	public final Color initialColor;

	/**
	 * Constructs a Cell at the specified grid position with the given color.
	 * 
	 * @param row the row index of this cell in the grid
	 * @param col the column index of this cell in the grid
	 * @param color the initial color assigned to this cell
	 */
	public Cell(int row, int col, Color color) {
		this.row = row;
		this.col = col;
		this.color = color;
		this.initialColor = color;
		neighbours = new ArrayList<>();
	}

	/**
	 * Adds a neighboring cell to this cell's adjacency list.
	 * Used during grid initialization to establish grid connectivity.
	 * 
	 * @param neighbour the adjacent cell to add to the neighbor list
	 */
	public void addNeighbour(Cell neighbour) {
		neighbours.add(neighbour);
	}

	/**
	 * Retrieves the row index of this cell in the grid.
	 * 
	 * @return the row position (0-indexed)
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Retrieves the column index of this cell in the grid.
	 * 
	 * @return the column position (0-indexed)
	 */
	public int getCol() {
		return col;
	}

	/**
	 * Determines if two cells are equal based on their grid position.
	 * Two cells are equal if they occupy the same row and column.
	 * 
	 * @param o the object to compare with
	 * @return true if both cells have the same position, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Cell cell = (Cell) o;
		return row == cell.row && col == cell.col;
	}

	/**
	 * Computes a hash code for this cell based on its position.
	 * Enables cells to be used in hash-based collections like HashSet and HashMap.
	 * 
	 * @return hash code derived from row and column indices
	 */
	@Override
	public int hashCode() {
		return Objects.hash(row, col);
	}

	@Override
	public String toString() {
		return "(" + row + "," + col + ")";
	}
}
