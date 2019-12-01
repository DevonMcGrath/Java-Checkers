/* Name: Game
 * Author: Devon McGrath
 * Description: This class represents a game of checkers. It provides a method
 * to update the game state and keep track of who's turn it is.
 */

package model;

import java.awt.Point;
import java.util.List;

import logic.MoveGenerator;
import logic.MoveLogic;

/**
 * The {@code Game} class represents a game of checkers and ensures that all
 * moves made are valid as per the rules of checkers.
 */
public class Game {

	/** The current state of the checker board. */
	private /*@ spec_public*/ Board board;
	
	/** The flag indicating if it is player 1's turn. */
	private /*@ spec_public*/ boolean isP1Turn;
	
	/** The index of the last skip, to allow for multiple skips in a turn. */
	private /*@ spec_public*/ int skipIndex;
	
	//@ public initially isP1Turn;
	
	/*@
	  @ ensures isP1Turn;
	  @ ensures skipIndex == -1;
	  @ ensures (\forall int i; 0 <= i && i < 12; board.get(i) == Board.BLACK_CHECKER && board.get(31 - i) == Board.WHITE_CHECKER);
	  @*/
	public Game() {
		restart();
	}
	
	public Game(String state) {
		setGameState(state);
	}
	
	public Game(Board board, boolean isP1Turn, int skipIndex) {
		this.board = (board == null)? new Board() : board;
		this.isP1Turn = isP1Turn;
		this.skipIndex = skipIndex;
	}
	
	/**
	 * Creates a copy of this game such that any modifications made to one are
	 * not made to the other.
	 * 
	 * @return an exact copy of this game.
	 */
	/*@
	  @ ensures (\forall int i; 0 <= i && i < 3; \result.getBoard().state[i] == board.state[i]);
	  @ ensures \result.isP1Turn() == isP1Turn();
	  @ ensures \result.getSkipIndex() == getSkipIndex();
	  @*/
	public /*@ pure */ Game copy() {
		Game g = new Game();
		g.board = board.copy();
		g.isP1Turn = isP1Turn;
		g.skipIndex = skipIndex;
		return g;
	}
	
	/**
	 * Resets the game of checkers to the initial state.
	 */
	/*@
	  @ ensures isP1Turn;
	  @ ensures skipIndex == -1;
	  @ ensures (\forall int i; 0 <= i && i < 12; board.get(i) == Board.BLACK_CHECKER && board.get(31 - i) == Board.WHITE_CHECKER);
	  @*/
	public void restart() {
		this.board = new Board();
		this.isP1Turn = true;
		this.skipIndex = -1;
	}
	
	/**
	 * Attempts to make a move from the start point to the end point.
	 * 
	 * @param start	the start point for the move.
	 * @param end	the end point for the move.
	 * @return true if and only if an update was made to the game state.
	 * @see {@link #move(int, int)}
	 */
	public boolean move(Point start, Point end) {
		if (start == null || end == null) {
			return false;
		}
		return move(Board.toIndex(start), Board.toIndex(end));
	}
	
	/**
	 * Attempts to make a move given the start and end index of the move.
	 * 
	 * @param startIndex	the start index of the move.
	 * @param endIndex		the end index of the move.
	 * @return true if and only if an update was made to the game state.
	 * @see {@link #move(Point, Point)}
	 * 
	 */
	/*@
	  @ requires isValidMove(startIndex, endIndex);
	  @ assignable board, isP1Turn, skipIndex;
	  @ ensures board.get(startIndex) == Board.EMPTY;
	  @ ensures Board.isValidIndex(Board.toIndex(Board.middle(startIndex, endIndex))) 
	  @				==> (board.get(Board.toIndex(Board.middle(startIndex, endIndex))) == Board.EMPTY);
	  @ ensures (\forall int i; 0 <= i && i < 32;
	  @				(i != startIndex && i != endIndex && i != getBoardIndexMiddle(startIndex, endIndex))
	  @					==> (board.get(i) == \old(board).get(i))
	  @ 		);
	  @ ensures (getYCoordinate(endIndex) != 0 && getYCoordinate(endIndex) != 7)
	  @				==> board.get(endIndex) == \old(board.get(startIndex));
	  @ ensures (getYCoordinate(endIndex) == 0 && \old(board).get(endIndex) == Board.WHITE_CHECKER) 
	  @				==> board.get(endIndex) == Board.WHITE_KING;
	  @ ensures (getYCoordinate(endIndex) == 7 && \old(board).get(endIndex) == Board.BLACK_CHECKER) 
	  @				==> board.get(endIndex) == Board.BLACK_KING;
	  @ ensures (
	  @				(isValidIndex(getBoardIndexMiddle(startIndex, endIndex)) && !hasNoSkips(endIndex)) 
	  @					==> skipIndex == endIndex
	  @			) 
	  @			&&
	  @			(
	  @				(!isValidIndex(getBoardIndexMiddle(startIndex, endIndex)) || hasNoSkips(endIndex)) 
	  @					==> isP1Turn == \old(!isP1Turn) && skipIndex == -1
	  @			);
	  @ ensures \result == true;
	  @ also
	  @ requires !isValidMove(startIndex, endIndex);
	  @ ensures \result == false;
	  @*/
	public boolean move(int startIndex, int endIndex) {
		
		// Validate the move
		if (!MoveLogic.isValidMove(this, startIndex, endIndex)) {
			return false;
		}
		
		// Make the move
		Point middle = Board.middle(startIndex, endIndex);
		int midIndex = Board.toIndex(middle);
		this.board.set(endIndex, board.get(startIndex));
		this.board.set(midIndex, Board.EMPTY);
		this.board.set(startIndex, Board.EMPTY);
		
		// Make the checker a king if necessary
		Point end = Board.toPoint(endIndex);
		int id = board.get(endIndex);
		boolean switchTurn = false;
		if (end.y == 0 && id == Board.WHITE_CHECKER) {
			this.board.set(endIndex, Board.WHITE_KING);
			switchTurn = true;
		} else if (end.y == 7 && id == Board.BLACK_CHECKER) {
			this.board.set(endIndex, Board.BLACK_KING);
			switchTurn = true;
		}
		
		// Check if the turn should switch (i.e. no more skips)
		boolean midValid = Board.isValidIndex(midIndex);
		if (midValid) {
			this.skipIndex = endIndex;
		}
		if (!midValid || MoveGenerator.getSkips(
				board.copy(), endIndex).isEmpty()) {
			switchTurn = true;
		}
		if (switchTurn) {
			this.isP1Turn = !isP1Turn;
			this.skipIndex = -1;
		}
		
		return true;
	}
	
	public /*@ pure */ int getYCoordinate(int index) {
		return Board.toPoint(index).y;
	}
	
	public /*@ pure */ int getBoardIndexMiddle(int id1, int id2) {
		return Board.toIndex(Board.middle(id1, id2));
	}
	
	public /*@ pure */ boolean isValidMove(int id1, int id2) {
		return MoveLogic.isValidMove(this, id1, id2);
	}
	
	public /*@ pure */ boolean isValidIndex(int index) {
		return Board.isValidIndex(index);
	}
	
	public /*@ pure */ boolean hasNoSkips(int index) {
		return MoveGenerator.getSkips(board.copy(), index).isEmpty();
	}
	
	public /*@ pure */ boolean hasNoMoves(int index) {
		return MoveGenerator.getMoves(board.copy(), index).isEmpty();
	}
	
	/**
	 * Gets a copy of the current board state.
	 * 
	 * @return a non-reference to the current game board state.
	 */
	public /*@ pure */ Board getBoard() {
		return board.copy();
	}
	
	/**
	 * Determines if the game is over. The game is over if one or both players
	 * cannot make a single move during their turn.
	 * 
	 * @return true if the game is over.
	 */
	/*@
	  @ ensures ((board.find(Board.BLACK_CHECKER).size() + board.find(Board.BLACK_KING).size()) == 0)
	  @			==> \result == true;
	  @ ensures ((board.find(Board.WHITE_CHECKER).size() + board.find(Board.WHITE_KING).size()) == 0)
	  @			==> \result == true;
	  @ ensures isP1Turn &&
	  @ 		(\exists int i; 0 <= i && i < 32;
	  @				(board.get(i) == Board.BLACK_CHECKER || board.get(i) == Board.BLACK_KING)
	  @				&&
	  @				(!hasNoMoves(i) || !hasNoSkips(i))
	  @			) ==> \result == false;
	  @ ensures !isP1Turn &&
	  @ 		(\exists int i; 0 <= i && i < 32;
	  @				(board.get(i) == Board.WHITE_CHECKER || board.get(i) == Board.WHITE_KING)
	  @				&&
	  @				(!hasNoMoves(i) || !hasNoSkips(i))
	  @			) ==> \result == false;
	  @*/
	public /*@ pure */ boolean isGameOver() {

		// Ensure there is at least one of each checker
		List<Point> black = board.find(Board.BLACK_CHECKER);
		black.addAll(board.find(Board.BLACK_KING));
		if (black.isEmpty()) {
			return true;
		}
		List<Point> white = board.find(Board.WHITE_CHECKER);
		white.addAll(board.find(Board.WHITE_KING));
		if (white.isEmpty()) {
			return true;
		}
		
		// Check that the current player can move
		List<Point> test = isP1Turn? black : white;
		for (Point p : test) {
			int i = Board.toIndex(p);
			if (!MoveGenerator.getMoves(board, i).isEmpty() ||
					!MoveGenerator.getSkips(board, i).isEmpty()) {
				return false;
			}
		}
		
		// No moves
		return true;
	}
	
	public /*@ pure */ boolean isP1Turn() {
		return isP1Turn;
	}
	
	public void setP1Turn(boolean isP1Turn) {
		this.isP1Turn = isP1Turn;
	}
	
	public /*@ pure */ int getSkipIndex() {
		return skipIndex;
	}
	
	/**
	 * Gets the current game state as a string of data that can be parsed by
	 * {@link #setGameState(String)}.
	 * 
	 * @return a string representing the current game state.
	 * @see {@link #setGameState(String)}
	 */
	/*@
	  @ ensures (\forall int i; 0 <= i && i < 32; 
	  @				(0 <= i && i < 32) ==> getIntInStringAt(\result, i) == board.get(i)
	  @			);
	  @ ensures getIntInSubstringAt(\result, 33) == skipIndex;
	  @ also
	  @ requires isP1Turn;
	  @ ensures getIntInStringAt(\result, 32) == 1;
	  @ also
	  @ requires !isP1Turn;
	  @ ensures getIntInStringAt(\result, 32) == 0;
	  @*/
	public String getGameState() {
		
		// Add the game board
		String state = "";
		for (int i = 0; i < 32; i ++) {
			state += "" + board.get(i);
		}
		
		// Add the other info
		state += (isP1Turn? "1" : "0");
		state += skipIndex;
		
		return state;
	}
	
	/**
	 * Parses a string representing a game state that was generated from
	 * {@link #getGameState()}.
	 * 
	 * @param state	the game state.
	 * @see {@link #getGameState()}
	 */
	/*@
	  @ requires state == null || state.isEmpty();
	  @ assignable \nothing;
	  @ also
	  @ requires state != null && !state.isEmpty();
	  @ ensures (\forall int i; 0 <= i && i < 32; 
	  @				(0 <= i && i < 32) ==> getIntInStringAt(state, i) == board.get(i)
	  @			);
	  @ ensures getIntInSubstringAt(state, 33) == skipIndex;
	  @ also
	  @ requires state != null && !state.isEmpty() && getIntInStringAt(state, 32) == 1;
	  @ ensures isP1Turn;
	  @ also
	  @ requires state != null && !state.isEmpty() && getIntInStringAt(state, 32) == 0;
	  @ ensures !isP1Turn;
	  @*/
	public void setGameState(String state) {
		
		restart();
		
		// Trivial cases
		if (state == null || state.isEmpty()) {
			return;
		}
		
		// Update the board
		int n = state.length();
		for (int i = 0; i < 32 && i < n; i ++) {
			try {
				int id = Integer.parseInt("" + state.charAt(i));
				this.board.set(i, id);
			} catch (NumberFormatException e) {}
		}
		
		// Update the other info
		if (n > 32) {
			this.isP1Turn = (state.charAt(32) == '1');
		}
		if (n > 33) {
			try {
				this.skipIndex = Integer.parseInt(state.substring(33));
			} catch (NumberFormatException e) {
				this.skipIndex = -1;
			}
		}
	}
	
	public /*@ pure */ int getIntInStringAt(String string, int position) {
		return Integer.parseInt("" + string.charAt(position));
	}
	
	public /*@ pure */ int getIntInSubstringAt(String string, int start) {
		return Integer.parseInt(string.substring(start));
	}
}
