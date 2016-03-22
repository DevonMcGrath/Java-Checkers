package checkers;

import tools.Board;
import tools.Point;

public class Checker {

	private int x;
	private int y;
	private int ID;

	//Basic constructor
	public Checker(int x, int y, int ID) {
		this.x = x;
		this.y = y;
		this.ID = ID;
	}

	//Eliminates the checker
	public void eliminate() {
		if (ID != Board.ID_WHITE_DEAD && ID != Board.ID_BLACK_DEAD) {
			this.x = -1;
			this.y = -1;
			this.ID = getDeadID(ID);
		}
	}

	//Returns the ID of that checker's dead variant
	private int getDeadID(int id) {

		//If the checker is white
		if (id == Board.ID_WHITE || id == Board.ID_WHITE_KING) {
			return Board.ID_WHITE_DEAD;
		}

		//The checker is black
		return Board.ID_BLACK_DEAD;
	}
	
	//Method returns true if the checker was eliminated
	public boolean isDead() {
		return (ID == Board.ID_WHITE_DEAD || ID == Board.ID_BLACK_DEAD);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getID() {
		return ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public void setLocation(Point location) {
		this.x = location.x;
		this.y = location.y;
	}
	
	public Point getPoint() {
		return new Point(x, y);
	}
}
