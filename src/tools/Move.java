/* Name: Move
 * Author: Devon McGrath
 * Date: 12/31/2015
 * Description: This class stores the change in direction for a specific move.
 */

package tools;

public class Move {
	
	//Only possible moves for this program
	public static final Move M_UP_LEFT = new Move(-1, -1);
	public static final Move M_UP_RIGHT = new Move(1, -1);
	public static final Move M_DOWN_LEFT = new Move(-1, 1);
	public static final Move M_DOWN_RIGHT = new Move(1, 1);
	public static final Move S_UP_LEFT = new Move(-2, -2);
	public static final Move S_UP_RIGHT = new Move(2, -2);
	public static final Move S_DOWN_LEFT = new Move(-2, 2);
	public static final Move S_DOWN_RIGHT = new Move(2, 2);

	private int dx, dy;
	private Point start;

	//Constructor
	public Move(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	//Constructor with start point
	public Move(Point start, int dx, int dy) {
		this.start = start;
		this.dx = dx;
		this.dy = dy;
	}

	//Method to create an end point
	public Point createEndPoint() {
		
		return new Point(start.x + dx, start.y + dy);
	}

	//Method to check for move equality
	public static boolean isEqaul(Move m1, Move m2) {
		return (m1.getDX() == m2.getDX() && m1.getDY() == m2.getDY());
	}

	//Check if m1 is reverse of m2
	public static boolean isReverse(Move m1, Move m2) {

		//Special case
		if (m1 == null || m2 == null) {
			return false;
		}

		return (m1.getDX() == -m2.getDX() && m1.getDY() == -m2.getDY());
	}

	//Add two moves together
	public static Move add(Move m1, Move m2) {

		//Special case
		if (m1 == null || m2 == null) {
			return null;
		}
		
		return new Move(m1.getDX() + m2.getDX(), m1.getDY() + m2.getDY());
	}

	public int getDX() {
		return dx;
	}

	public void setDX(int dx) {
		this.dx = dx;
	}

	public int getDY() {
		return dy;
	}

	public void setDY(int dy) {
		this.dy = dy;
	}
	
	public Point getStart() {
		return start;
	}
}
