/*
 * Cell.java
 * Represents a single cell in the game grid.
 * Each cell has a color, position, owner, and maintains references to neighboring cells.
 * Implements proper equals() and hashCode() for use in collections.
 * Author: Bhargava Srisai
 * Version: 2.0
 */

package swingprac;
import java.util.*;
import java.awt.Color;
public class Cell {
	private int row,col; // position in the grid
	public Color color;
	public List<Cell> neighbours;
	public Owner owner = Owner.NONE;
	public final Color initialColor;   // used when resetting the game
	public Cell(int row, int col, Color color) {
		this.row = row;
		this.col = col;
		this.color = color;
		this.initialColor = color; // store original color for reset
		neighbours = new ArrayList<>();
	}
		// Adds a neighbouring cell (used when building the grid)
	public void addNeighbour(Cell neighbour) {
		neighbours.add(neighbour);
	}
}
