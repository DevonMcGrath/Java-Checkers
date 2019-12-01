/* Name: ComputerPlayer
 * Author: Devon McGrath
 * Description: This class represents a computer player which can update the
 * game state without user interaction.
 */

package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import logic.MoveGenerator;
import logic.MoveLogic;

/**
 * The {@code ComputerPlayer} class represents a computer player and updates
 * the board based on a model.
 */
public class ComputerPlayer extends Player {
	
	/* ----- WEIGHTS ----- */
	/** The weight of being able to skip. */
	private static final double WEIGHT_SKIP = 25;
	
	/** The weight of being able to skip on next turn. */
	private static final double SKIP_ON_NEXT = 20;
	
	/** The weight associated with being safe then safe before and after. */
	private static final double SAFE_SAFE = 5;

	/** The weight associated with being safe then unsafe before and after. */
	private static final double SAFE_UNSAFE = -40;

	/** The weight associated with being unsafe then safe before and after. */
	private static final double UNSAFE_SAFE = 40;

	/** The weight associated with being unsafe then unsafe before and after. */
	private static final double UNSAFE_UNSAFE = -40;
	
	/** The weight of a checker being safe. */
	private static final double SAFE = 3;
	
	/** The weight of a checker being unsafe. */
	private static final double UNSAFE = -5;
	
	/** The factor used to multiply some weights when the checker being
	 * observed is a king. */
	private static final double KING_FACTOR = 2;
	/* ------------ */

	@Override
	/*@
	  @ also
	  @ ensures \result == false;
	  @*/
	public boolean isHuman() {
		return false;
	}

	@Override
	/*@
	  @ also
	  @ requires game == null || game.isGameOver();
	  @ assignable \nothing;
	  @ also
	  @ requires game != null && !game.isGameOver();
	  @ ensures (\exists int i; 0 <= i && i < getMoves(\old(game).copy()).size();
	  @				(\exists int j; 0 <= j && j < 32;
	  @					(
	  @						\old(game).copy().getBoard().get(j) 
	  @						== 
	  @						game.copy().getBoard().get(getMoves(\old(game).copy()).get(i).getEndIndex())
	  @					) 
	  @					&& (game.copy().getBoard().get(j) == 0)
	  @				)
	  @			);
	  @*/
	public void updateGame(Game game) {
		
		// Nothing to do
		if (game == null || game.isGameOver()) {
			return;
		}
		
		// Get the available moves
		Game copy = game.copy();
		List<Move> moves = getMoves(copy);

		// Determine which one is the best
		int n = moves.size(), count = 1;
		double bestWeight = Move.WEIGHT_INVALID;
		for (int i = 0; i < n; i ++) {
			Move m = moves.get(i);
			getMoveWeight(copy.copy(), m);
			if (m.getWeight() > bestWeight) {
				count = 1;
				bestWeight = m.getWeight();
			} else if (m.getWeight() == bestWeight) {
				count ++;
			}
		}

		// Randomly select a move
		int move = ((int) (Math.random() * count)) % count;
		for (int i = 0; i < n; i ++) {
			Move m = moves.get(i);
			if (bestWeight == m.getWeight()) {
				if (move == 0) {
					game.move(m.getStartIndex(), m.getEndIndex());
				} else {
					move --;
				}
			}
		}
	}
	
	private  /*@ spec_public pure */ int getCheckerIndex(Game game, int i) {
		Point p = this.getCheckers(game).get(i);
		return Board.toIndex(p);
	}
	
	/**
	 * Gets all the available moves and skips for the current player.
	 * 
	 * @param game	the current game state.
	 * @return a list of valid moves that the player can make.
	 */
	/*@
	  @ requires game.getSkipIndex() >= 0;
	  @ ensures (\forall int i; 0 <= i && i < MoveGenerator.getSkips(game.getBoard(), game.getSkipIndex()).size();
	  @				((Move)\result.get(i)).getEndIndex() ==
	  @				Board.toIndex(MoveGenerator.getSkips(game.getBoard(), game.getSkipIndex()).get(i))
	  @				&&
	  @				((Move)\result.get(i)).getStartIndex() == game.getSkipIndex()
	  @			);
	  @ also
	  @ requires game.getSkipIndex() < 0;
	  @ requires !this.getAllSkips(game, this.getCheckers(game)).isEmpty();
	  @ ensures !\result.isEmpty();
	  @ also
	  @ requires game.getSkipIndex() < 0;
	  @ requires this.getAllSkips(game, this.getCheckers(game)).isEmpty();
	  @ ensures (\forall int i; 0 <= i && i < this.getCheckers(game).size();
	  @				(\forall int j; 0 <= j && j < MoveGenerator.getMoves(game.getBoard(), this.getCheckerIndex(game, i)).size();
	  @					(this.getCheckerIndex(game, i) 
	  @						== ((Move)\result.get(i)).getStartIndex()
	  @					)
	  @					&&
	  @					(Board.toIndex(MoveGenerator.getMoves(game.getBoard(), i).get(j)) 
	  @						== ((Move)\result.get(i)).getEndIndex()
	  @					)
	  @				)
	  @			);
	  @*/
	private /*@ spec_public pure */ List<Move> getMoves(Game game) {
		
		// The next move needs to be a skip
		if (game.getSkipIndex() >= 0) {
			
			List<Move> moves = new ArrayList<Move>();
			List<Point> skips = MoveGenerator.getSkips(game.getBoard(),
					game.getSkipIndex());
			for (Point end : skips) {
				moves.add(new Move(game.getSkipIndex(), Board.toIndex(end)));
			}
			
			return moves;
		}
		
		// Get the checkers
		List<Point> checkers = getCheckers(game);
		
		List<Move> moves = getAllSkips(game, checkers); 
		
		// If there are no skips, add the regular moves
		if (moves.isEmpty()) {
			for (Point checker : checkers) {
				int index = Board.toIndex(checker);
				Board b = game.getBoard();
				List<Point> movesEnds = MoveGenerator.getMoves(b, index);
				for (Point end : movesEnds) {
					moves.add(new Move(index, Board.toIndex(end)));
				}
			}
		}
		
		return moves;
	}
	
	/*@
	  @ requires game.isP1Turn();
	  @ ensures \result.size() == getMergedLists(game.getBoard().find(Board.BLACK_CHECKER), game.getBoard().find(Board.BLACK_KING)).size();
	  @ ensures (\forall int i; 0 <= i && i < getMergedLists(game.getBoard().find(Board.BLACK_CHECKER), game.getBoard().find(Board.BLACK_KING)).size();
	  @				getMergedLists(game.getBoard().find(Board.BLACK_CHECKER), game.getBoard().find(Board.BLACK_KING)).get(i).equals(\result.get(i))
	  @			);
	  @ also
	  @ requires !game.isP1Turn();
	  @ ensures \result.size() == getMergedLists(game.getBoard().find(Board.WHITE_CHECKER), game.getBoard().find(Board.WHITE_KING)).size();
	  @ ensures (\forall int i; 0 <= i && i < getMergedLists(game.getBoard().find(Board.WHITE_CHECKER), game.getBoard().find(Board.WHITE_KING)).size();
	  @				getMergedLists(game.getBoard().find(Board.WHITE_CHECKER), game.getBoard().find(Board.WHITE_KING)).get(i).equals(\result.get(i))
	  @			);
	  @*/
	private /*@ spec_public pure */ List<Point> getCheckers(Game game) {
		List<Point> checkers = new ArrayList<>();
		Board b = game.getBoard();
		if (game.isP1Turn()) {
			checkers.addAll(b.find(Board.BLACK_CHECKER));
			checkers.addAll(b.find(Board.BLACK_KING));
		} else {
			checkers.addAll(b.find(Board.WHITE_CHECKER));
			checkers.addAll(b.find(Board.WHITE_KING));
		}
		
		return checkers;
	}
	
	public /*@ pure */ static List<Point> getMergedLists(List<Point> checkers, List<Point> kings) {
		List<Point> all = new ArrayList<>();
		all.addAll(checkers);
		all.addAll(kings);
		
		return all;
	}
	
	private /*@ spec_public pure */ List<Move> getAllSkips(Game game, List<Point> checkers) {
		List<Move> moves = new ArrayList<>();
		Board b = game.getBoard();
		for (Point checker : checkers) {
			int index = Board.toIndex(checker);
			List<Point> skips = MoveGenerator.getSkips(b, index);
			for (Point end : skips) {
				Move m = new Move(index, Board.toIndex(end));
				m.changeWeight(WEIGHT_SKIP);
				moves.add(m);
			}
		}
		
		return moves;
	}
	
	/**
	 * Gets the number of skips that can be made in one turn from a given start
	 * index.
	 * 
	 * @param game			the game state to check against.
	 * @param startIndex	the start index of the skips.
	 * @param isP1Turn		the original player turn flag.
	 * @return the maximum number of skips available from the given point.
	 */
	private int getSkipDepth(Game game, int startIndex, boolean isP1Turn) {
		
		// Trivial case
		if (isP1Turn != game.isP1Turn()) {
			return 0;
		}
		
		// Recursively get the depth
		List<Point> skips = MoveGenerator.getSkips(game.getBoard(), startIndex);
		int depth = 0;
		for (Point end : skips) {
			int endIndex = Board.toIndex(end);
			game.move(startIndex, endIndex);
			int testDepth = getSkipDepth(game, endIndex, isP1Turn);
			if (testDepth > depth) {
				depth = testDepth;
			}
		}
		
		return depth + (skips.isEmpty()? 0 : 1);
	}
	
	/**
	 * Determines the weight of a move based on a number of factors (e.g. how
	 * safe the checker is before/after, whether it can take an opponents
	 * checker after, etc).
	 * 
	 * @param game	the current game state.
	 * @param m		the move to test.
	 */
	private void getMoveWeight(Game game, Move m) {
		
		Point start = m.getStart(), end = m.getEnd();
		int startIndex = Board.toIndex(start), endIndex = Board.toIndex(end);
		Board b = game.getBoard();
		boolean changed = game.isP1Turn();
		boolean safeBefore = MoveLogic.isSafe(b, start);
		int id = b.get(startIndex);
		boolean isKing = (id == Board.BLACK_KING || id == Board.WHITE_KING);
		
		// Set the initial weight
		m.changeWeight(getSafetyWeight(b, game.isP1Turn()));
		
		// Make the move
		if (!game.move(m.getStartIndex(), m.getEndIndex())) {
			m.setWeight(Move.WEIGHT_INVALID);
			return;
		}
		b = game.getBoard();
		changed = (changed != game.isP1Turn());
		id = b.get(endIndex);
		isKing = (id == Board.BLACK_KING || id == Board.WHITE_KING);
		boolean safeAfter = true;
		
		// Determine if a skip could be made on next move
		if (changed) {
			safeAfter = MoveLogic.isSafe(b, end);
			int depth = getSkipDepth(game, endIndex, !game.isP1Turn());
			if (safeAfter) {
				m.changeWeight(SKIP_ON_NEXT * depth * depth);
			} else {
				m.changeWeight(SKIP_ON_NEXT);
			}
		}
		
		// Check how many more skips are available
		else {
			int depth = getSkipDepth(game, startIndex, game.isP1Turn());
			m.changeWeight(WEIGHT_SKIP * depth * depth);
		}
		
		// Add the weight appropriate to how safe the checker is
		if (safeBefore && safeAfter) {
			m.changeWeight(SAFE_SAFE);
		} else if (!safeBefore && safeAfter) {
			m.changeWeight(UNSAFE_SAFE);
		} else if (safeBefore && !safeAfter) {
			m.changeWeight(SAFE_UNSAFE * (isKing? KING_FACTOR : 1));
		} else {
			m.changeWeight(UNSAFE_UNSAFE);
		}
		m.changeWeight(getSafetyWeight(b,
				changed? !game.isP1Turn() : game.isP1Turn()));
	}
	
	/**
	 * Calculates the 'safety' state of the game for the player specified. The
	 * player has 'safe' and 'unsafe' checkers, which respectively, cannot and
	 * can be skipped by the opponent in the next turn.
	 * 
	 * @param b			the board state to check against.
	 * @param isBlack	the flag indicating if black checkers should be observed.
	 * @return the weight corresponding to how safe the player's checkers are.
	 */
	/*@
	  @ requires isBlack;
	  @ 
	  @ also
	  @ requires !isBlack;
	  @*/
	private double getSafetyWeight(Board b, boolean isBlack) {
		
		// Get the checkers
		double weight = 0;
		List<Point> checkers = new ArrayList<>();
		if (isBlack) {
			checkers.addAll(b.find(Board.BLACK_CHECKER));
			checkers.addAll(b.find(Board.BLACK_KING));
		} else {
			checkers.addAll(b.find(Board.WHITE_CHECKER));
			checkers.addAll(b.find(Board.WHITE_KING));
		}
		
		// Determine conditions for each checker
		for (Point checker : checkers) {
			int index = Board.toIndex(checker);
			int id = b.get(index);
			boolean isKing = (id == Board.BLACK_KING || id == Board.WHITE_KING);
			if (MoveLogic.isSafe(b, checker)) {
				weight += SAFE;
			} else {
				weight += UNSAFE * (isKing? KING_FACTOR : 1);
			}
		}
		
		return weight;
	}
}