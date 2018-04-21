/* Name: Board
 * Author: Devon McGrath
 * Description: This class implements an 8x8 checker board. Under standard
 * rules, a checker can only move on black tiles, meaning there are only 32
 * available tiles. It uses three integers to represent the board, giving
 * 3 bits to each black tile.
 */

package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Board} class represents a game state for checkers. A standard
 * checker board is 8 x 8 (64) tiles, alternating white/black. Checkers are
 * only allowed on black tiles and can therefore only move diagonally. The
 * board is optimized to use as little memory space as possible and only uses
 * 3 integers to represent the state of the board (3 bits for each of the 32
 * tiles). This makes it fast and efficient to {@link #copy()} the board state.
 * <p>
 * This class uses integers to represent the state of each tile and
 * specifically uses these constants for IDs: {@link #EMPTY},
 * {@link #BLACK_CHECKER}, {@link #WHITE_CHECKER}, {@link #BLACK_KING},
 * {@link #WHITE_KING}.
 * <p>
 * Tile states can be retrieved through {@link #get(int)} and
 * {@link #get(int, int)}. Tile states can be set through
 * {@link #set(int, int)} and {@link #set(int, int, int)}. The entire game can
 * be reset with {@link #reset()}.
 */
public class Board {
	
	/** An ID indicating a point was not on the checker board. */
	public static final int INVALID = -1;

	/** The ID of an empty checker board tile. */
	public static final int EMPTY = 0;

	/** The ID of a white checker in the checker board. */
	public static final int BLACK_CHECKER = 4 * 1 + 2 * 1 + 1 * 0;
	
	/** The ID of a white checker in the checker board. */
	public static final int WHITE_CHECKER = 4 * 1 + 2 * 0 + 1 * 0;

	/** The ID of a black checker that is also a king. */
	public static final int BLACK_KING = 4 * 1 + 2 * 1 + 1 * 1;
	
	/** The ID of a white checker that is also a king. */
	public static final int WHITE_KING = 4 * 1 + 2 * 0 + 1 * 1;

	/** The current state of the board, represented as three integers. */
	private int[] state;
	
	/**
	 * Constructs a new checker game board, pre-filled with a new game state.
	 */
	public Board() {
		reset();
	}
	
	/**
	 * Creates an exact copy of the board. Any changes made to the copy will
	 * not affect the current object.
	 * 
	 * @return a copy of this checker board.
	 */
	public Board copy() {
		Board copy = new Board();
		copy.state = state.clone();
		return copy;
	}
	
	/**
	 * Resets the checker board to the original game state with black checkers
	 * on top and white on the bottom. There are both 12 black checkers and 12
	 * white checkers.
	 */
	public void reset() {

		// Reset the state
		this.state = new int[3];
		for (int i = 0; i < 12; i ++) {
			set(i, BLACK_CHECKER);
			set(31 - i, WHITE_CHECKER);
		}
	}
	
	/**
	 * Searches through the checker board and finds black tiles that match the
	 * specified ID.
	 * 
	 * @param id	the ID to search for.
	 * @return a list of points on the board with the specified ID. If none
	 * exist, an empty list is returned.
	 */
	public List<Point> find(int id) {
		
		// Find all black tiles with matching IDs
		List<Point> points = new ArrayList<>();
		for (int i = 0; i < 32; i ++) {
			if (get(i) == id) {
				points.add(toPoint(i));
			}
		}
		
		return points;
	}
	
	/**
	 * Sets the ID of a black tile on the board at the specified location.
	 * If the location is not a black tile, nothing is updated. If the ID is
	 * less than 0, the board at the location will be set to {@link #EMPTY}.
	 * 
	 * @param x		the x-coordinate on the board (from 0 to 7 inclusive).
	 * @param y		the y-coordinate on the board (from 0 to 7 inclusive).
	 * @param id	the new ID to set the black tile to.
	 * @see {@link #set(int, int)}, {@link #EMPTY}, {@link #BLACK_CHECKER},
	 * {@link #WHITE_CHECKER}, {@link #BLACK_KING}, {@link #WHITE_KING}
	 */
	public void set(int x, int y, int id) {
		set(toIndex(x, y), id);
	}
	
	/**
	 * Sets the ID of a black tile on the board at the specified location.
	 * If the location is not a black tile, nothing is updated. If the ID is
	 * less than 0, the board at the location will be set to {@link #EMPTY}.
	 * 
	 * @param index	the index of the black tile (from 0 to 31 inclusive).
	 * @param id	the new ID to set the black tile to.
	 * @see {@link #set(int, int, int)}, {@link #EMPTY}, {@link #BLACK_CHECKER},
	 * {@link #WHITE_CHECKER}, {@link #BLACK_KING}, {@link #WHITE_KING}
	 */
	public void set(int index, int id) {
		
		// Out of range
		if (!isValidIndex(index)) {
			return;
		}
		
		// Invalid ID, so just set to EMPTY
		if (id < 0) {
			id = EMPTY;
		}
		
		// Set the state bits
		for (int i = 0; i < state.length; i ++) {
			boolean set = ((1 << (state.length - i - 1)) & id) != 0;
			this.state[i] = setBit(state[i], index, set);
		}
	}
	
	/**
	 * Gets the ID corresponding to the specified point on the checker board.
	 * 
	 * @param x	the x-coordinate on the board (from 0 to 7 inclusive).
	 * @param y	the y-coordinate on the board (from 0 to 7 inclusive).
	 * @return the ID at the specified location or {@link #INVALID} if the
	 * location is not on the board or the location is a white tile.
	 * @see {@link #get(int)}, {@link #set(int, int)},
	 * {@link #set(int, int, int)}
	 */
	public int get(int x, int y) {
		return get(toIndex(x, y));
	}
	
	/**
	 * Gets the ID corresponding to the specified point on the checker board.
	 * 
	 * @param index	the index of the black tile (from 0 to 31 inclusive).
	 * @return the ID at the specified location or {@link #INVALID} if the
	 * location is not on the board.
	 * @see {@link #get(int, int)}, {@link #set(int, int)},
	 * {@link #set(int, int, int)}
	 */
	public int get(int index) {
		if (!isValidIndex(index)) {
			return INVALID;
		}
		return getBit(state[0], index) * 4 + getBit(state[1], index) * 2
				+ getBit(state[2], index);
	}
	
	/**
	 * Converts a black tile index (0 to 31 inclusive) to an (x, y) point, such
	 * that index 0 is (1, 0), index 1 is (3, 0), ... index 31 is (7, 7).
	 * 
	 * @param index	the index of the black tile to convert to a point.
	 * @return the (x, y) point corresponding to the black tile index or the
	 * point (-1, -1) if the index is not between 0 - 31 (inclusive).
	 * @see {@link #toIndex(int, int)}, {@link #toIndex(Point)}
	 */
	public static Point toPoint(int index) {
		int y = index / 4;
		int x = 2 * (index % 4) + (y + 1) % 2;
		return !isValidIndex(index)? new Point(-1, -1) : new Point(x, y);
	}
	
	/**
	 * Converts a point to an index of a black tile on the checker board, such
	 * that (1, 0) is index 0, (3, 0) is index 1, ... (7, 7) is index 31.
	 * 
	 * @param x	the x-coordinate on the board (from 0 to 7 inclusive).
	 * @param y	the y-coordinate on the board (from 0 to 7 inclusive).
	 * @return the index of the black tile or -1 if the point is not a black
	 * tile.
	 * @see {@link #toIndex(Point)}, {@link #toPoint(int)}
	 */
	public static int toIndex(int x, int y) {
		
		// Invalid (x, y) (i.e. not in board, or white tile)
		if (!isValidPoint(new Point(x, y))) {
			return -1;
		}
		
		return y * 4 + x / 2;
	}
	
	/**
	 * Converts a point to an index of a black tile on the checker board, such
	 * that (1, 0) is index 0, (3, 0) is index 1, ... (7, 7) is index 31.
	 * 
	 * @param p	the point to convert to an index.
	 * @return the index of the black tile or -1 if the point is not a black
	 * tile.
	 * @see {@link #toIndex(int, int)}, {@link #toPoint(int)}
	 */
	public static int toIndex(Point p) {
		return (p == null)? -1 : toIndex(p.x, p.y);
	}
	
	/**
	 * Sets or clears the specified bit in the target value and returns
	 * the updated value.
	 * 
	 * @param target	the target value to update.
	 * @param bit		the bit to update (from 0 to 31 inclusive).
	 * @param set		true to set the bit, false to clear the bit.
	 * @return the updated target value with the bit set or cleared.
	 * @see {@link #getBit(int, int)}
	 */
	public static int setBit(int target, int bit, boolean set) {
		
		// Nothing to do
		if (bit < 0 || bit > 31) {
			return target;
		}
		
		// Set the bit
		if (set) {
			target |= (1 << bit);
		}
		
		// Clear the bit
		else {
			target &= (~(1 << bit));
		}
		
		return target;
	}
	
	/**
	 * Gets the state of a bit and determines if it is set (1) or not (0).
	 * 
	 * @param target	the target value to get the bit from.
	 * @param bit		the bit to get (from 0 to 31 inclusive).
	 * @return 1 if and only if the specified bit is set, 0 otherwise.
	 * @see {@link #setBit(int, int, boolean)}
	 */
	public static int getBit(int target, int bit) {
		
		// Out of range
		if (bit < 0 || bit > 31) {
			return 0;
		}
		
		return (target & (1 << bit)) != 0? 1 : 0;
	}
	
	/**
	 * Gets the middle point on the checker board between two points.
	 * 
	 * @param p1	the first point of a black tile on the checker board.
	 * @param p2	the second point of a black tile on the checker board.
	 * @return the middle point between two points or (-1, -1) if the points
	 * are not on the board, are not distance 2 from each other in x and y,
	 * or are on a white tile.
	 * @see {@link #middle(int, int)}, {@link #middle(int, int, int, int)}
	 */
	public static Point middle(Point p1, Point p2) {
		
		// A point isn't initialized
		if (p1 == null || p2 == null) {
			return new Point(-1, -1);
		}
		
		return middle(p1.x, p1.y, p2.x, p2.y);
	}
	
	/**
	 * Gets the middle point on the checker board between two points.
	 * 
	 * @param index1	the index of the first point (from 0 to 31 inclusive).
	 * @param index2	the index of the second point (from 0 to 31 inclusive).
	 * @return the middle point between two points or (-1, -1) if the points
	 * are not on the board, are not distance 2 from each other in x and y,
	 * or are on a white tile.
	 * @see {@link #middle(Point, Point)}, {@link #middle(int, int, int, int)}
	 */
	public static Point middle(int index1, int index2) {
		return middle(toPoint(index1), toPoint(index2));
	}
	
	/**
	 * Gets the middle point on the checker board between two points.
	 * 
	 * @param x1	the x-coordinate of the first point.
	 * @param y1	the y-coordinate of the first point.
	 * @param x2	the x-coordinate of the second point.
	 * @param y2	the y-coordinate of the second point.
	 * @return the middle point between two points or (-1, -1) if the points
	 * are not on the board, are not distance 2 from each other in x and y,
	 * or are on a white tile.
	 * @see {@link #middle(int, int)}, {@link #middle(Point, Point)}
	 */
	public static Point middle(int x1, int y1, int x2, int y2) {
		
		// Check coordinates
		int dx = x2 - x1, dy = y2 - y1;
		if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0 || // Not in the board
				x1 > 7 || y1 > 7 || x2 > 7 || y2 > 7) {
			return new Point(-1, -1);
		} else if (x1 % 2 == y1 % 2 || x2 % 2 == y2 % 2) { // white tile
			return new Point(-1, -1);
		} else if (Math.abs(dx) != Math.abs(dy) || Math.abs(dx) != 2) {
			return new Point(-1, -1);
		}
		
		return new Point(x1 + dx / 2, y1 + dy / 2);
	}
	
	/**
	 * Checks if an index corresponds to a black tile on the checker board.
	 * 
	 * @param testIndex	the index to check.
	 * @return true if and only if the index is between 0 and 31 inclusive.
	 */
	public static boolean isValidIndex(int testIndex) {
		return testIndex >= 0 && testIndex < 32;
	}
	
	/**
	 * Checks if a point corresponds to a black tile on the checker board.
	 * 
	 * @param testPoint	the point to check.
	 * @return true if and only if the point is on the board, specifically on
	 * a black tile.
	 */
	public static boolean isValidPoint(Point testPoint) {
		
		if (testPoint == null) {
			return false;
		}
		
		// Check that it is on the board
		final int x = testPoint.x, y = testPoint.y;
		if (x < 0 || x > 7 || y < 0 || y > 7) {
			return false;
		}
		
		// Check that it is on a black tile
		if (x % 2 == y % 2) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		String obj = getClass().getName() + "[";
		for (int i = 0; i < 31; i ++) {
			obj += get(i) + ", ";
		}
		obj += get(31);
		
		return obj + "]";
	}
}
