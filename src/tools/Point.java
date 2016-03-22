package tools;

import java.util.List;

public class Point {

	public int x, y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Point(Point p) {
		if (p != null) {
			this.x = p.x;
			this.y = p.y;
		}
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

	//Method creates a string representation of the object
	public String toString() {

		return "("+x+", "+y+")";
	}

	//Method to check for point equality
	public static boolean isEqual(Point p1, Point p2) {

		//Special case
		if (p1 == null || p2 == null) {
			return false;
		}

		return (p1.x == p2.x && p1.y == p2.y);
	}

	//Method calculates the x distance between two points
	public static int calculateDX(Point p1, Point p2) {

		//Special case
		if (p1 == null || p2 == null) {
			return -1;
		}

		return (p2.x - p1.x);
	}

	//Method calculates the y distance between two points
	public static int calculateDY(Point p1, Point p2) {

		//Special case
		if (p1 == null || p2 == null) {
			return -1;
		}

		return (p2.y - p1.y);
	}

	//Compares two points to see if p1 is closer to (0,0) than p2
	public static boolean isInRange(Point p1, Point p2) {

		//Special case
		if (p1 == null || p2 == null) {
			return false;
		}

		return ((p1.x < p2.x && p1.x >= 0) && (p1.y < p2.y && p1.y >= 0));
	}

	//Calculates the middle point
	public static Point middle(Point p1, Point p2) {

		//Special case
		if (p1 == null || p2 == null) {
			return null;
		}

		return new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
	}

	//Method that creates a new point from a move
	public static Point createNewPoint(Point p, Move move) {

		//Special case
		if (p == null || move == null) {
			return null;
		}
		return new Point(p.getX() + move.getDX(), p.getY() + move.getDY());
	}

	//Checks if 'p' is in the array of points
	public static boolean contains(Point p, Point[] array) {

		//Special case
		if (p == null || array == null) {
			return false;
		}

		//Loop through array to compare values
		for (Point test : array) {
			if (isEqual(p, test)) {
				return true; //Point was found
			}
		}

		//Failed to find p
		return false;
	}

	//Combines two point arrays
	public static Point[] combine(Point[] array1, Point[] array2) {

		//Special cases
		if (array1 == null && array2 == null) {
			return null;
		}
		if (array1 == null) {
			return array2;
		}
		if (array2 == null) {
			return array1;
		}

		Point[] result = new Point[array1.length + array2.length];

		//Add array 1 to the result
		for (int i = 0; i < array1.length; i ++) {
			result[i] = array1[i];
		}

		//Add array 2 to the result
		for (int i = 0; i < array2.length; i ++) {
			result[i+array1.length] = array2[i];
		}

		//Return the new array
		return result;
	}

	//Converts a list to array
	public static Point[] toArray(List<Point> list) {

		Point[] result = new Point[list.size()];

		//Loop through to add values
		int count = 0;
		for (Point p : list) {
			result[count] = p;
			count ++;
		}

		return result;
	}

	//Method to calculate the change in distance when p1 is moved to p2
	//looking at it from point 'end' || negative means closer!
	public static double calculateChangeInDist(Point p1, Point p2, Point end) {

		//Special case
		if (p1 == null || p2 == null || end == null) {
			return -1;
		}

		return calculateDist(p2,end) - calculateDist(p1,end);
	}

	//Method to calculate the distance between two points
	public static double calculateDist(Point p1, Point p2) {

		//Special case
		if (p1 == null || p2 == null) {
			return -1;
		}

		return Math.sqrt(Math.pow(calculateDX(p1,p2), 2) +
				Math.pow(calculateDY(p1,p2), 2));
	}
}
