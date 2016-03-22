/* Name: WeightArray
 * Author: Devon McGrath
 * Date: 01/01/2016
 * Description: This class holds the weights for each move a checker can make.
 */

package ai;

import tools.Point;
import checkers.Checker;

public class WeightArray {

	/* Weights for each different scenario
	 * Key:
	 * W = weight
	 * S = safe
	 * US = unsafe
	 * K = king
	 * 
	 * Moves:
	 * [0] - up left
	 * [1] - up right
	 * [2] - down left
	 * [3] - down right
	 */
	public static final float W_INVALID = -80;
	public static final float W_S_S = 10;
	public static final float W_US_S = 50;
	public static final float W_S_US = -25;
	public static final float W_US_US = -25;
	public static final float W_SKIP_ON_NEXT_MOVE = 25;
	public static final float W_ENEMY_SKIP_AFTER = -10;
	public static final float W_BECOMES_K = 75;
	public static final float W_GETS_STUCK = -10;

	private Checker checker;
	private float[] weightArray;

	//Constructor
	public WeightArray(Checker checker) {

		this.checker = checker;
		this.weightArray = new float[4];

		//Initialize the weight array
		for (int n = 0; n < weightArray.length; n ++) {
			this.weightArray[n] = 0;
		}
	}

	//Returns true if the index is not = the invalid weight
	public boolean isValid(int index) {

		//Check if in range
		if (index < 0 || index >= weightArray.length) {
			return false;
		}

		return (weightArray[index] != W_INVALID);
	}

	//Method to add to a weight
	public void increment(int index, float changeInWeight) {

		//Only set if index is within range
		if (index >= 0 && index < weightArray.length &&
				weightArray[index] != W_INVALID) {
			this.weightArray[index] += changeInWeight;
		}
	}

	//Method to set the value in an index of the weight array
	public void setWeight(int index, float weight) {

		//Only set if index is within range
		if (index >= 0 && index < weightArray.length) {
			this.weightArray[index] = weight;
		}
	}

	//Method to get the value in an index of the weight array
	public float getWeight(int index) {

		//Only set if index is within range
		if (index >= 0 && index < weightArray.length) {
			return weightArray[index];
		}

		return W_INVALID;
	}

	public float[] getWeightArray() {
		return weightArray;
	}

	public Checker getChecker() {
		return checker;
	}

	//Returns the index with the highest value
	public int max() {

		//Loop through the array
		int index = 0;
		for (int n = 0; n < weightArray.length; n ++) {
			if (weightArray[n] > weightArray[index]) {
				index = n;
			}
		}

		return index;
	}

	//Checks if any move/skip is valid
	public boolean hasValidTurn() {

		//Loop through the weight array
		for (int n = 0; n < weightArray.length; n ++) {
			if (weightArray[n] != W_INVALID) {
				return true;
			}
		}

		//No valid turn
		return false;
	}

	//Method returns a string representation of the object
	public String toString() {

		//Invalid checker
		if (checker == null) {
			return "no checker to create weight array";
		}

		String object = "";

		//Create points
		int x = checker.getX(), y = checker.getY();
		Point p1 = new Point(x-1, y-1);
		Point p2 = new Point(x+1, y-1);
		Point p3 = new Point(x-1, y+1);
		Point p4 = new Point(x+1, y+1);

		//Add the checker info
		object += "Checker At: "+checker.getPoint().toString()+"\n";

		//Add the weights
		object += "Move To "+p1.toString()+": "+weightArray[0]+"\n";
		object += "Move To "+p2.toString()+": "+weightArray[1]+"\n";
		object += "Move To "+p3.toString()+": "+weightArray[2]+"\n";
		object += "Move To "+p4.toString()+": "+weightArray[3];

		return object;
	}

	//Method returns the size of the weight array
	public int size() {
		return weightArray.length;
	}

	//Method returns a point where the x = index in the weightArray[]
	//and y = index in weightArray[x]
	public static Point getMaxLocation(WeightArray[] array) {

		int x = 0, y = 0;

		//Loop through array
		for (int n = 0; n < array.length; n ++) {

			if (array[n].getWeight(array[n].max())
					> array[x].getWeight(y)) {
				x = n;
				y = array[n].max();
			}
		}

		return new Point(x, y);
	}

	//Method to get the maximum weight
	public static float maxWeight(WeightArray[] array) {

		//Return the weight at the max locationS
		return maxWeight(array, getMaxLocation(array));
	}

	//Method to get the maximum weight
	public static float maxWeight(WeightArray[] array, Point max) {

		//Return the weight at the point
		return array[max.x].getWeight(max.y);
	}

	//Method counts the appearance of value in the array
	public static int count(WeightArray[] array, float value) {

		int count = 0;

		//Loop through the array
		for (WeightArray a : array) {

			//Loop through each weight array
			for (int n = 0; n < a.size(); n ++) {
				if (a.getWeight(n) == value) {
					count ++;
				}
			}
		}

		return count;
	}

	//Returns a random move location that is the best weight; same type of
	//return as the 'getMaxLocation()' method
	public static Point randMove(WeightArray[] array) {

		//Get the max location
		Point max = getMaxLocation(array);
		
		//Count the number of appearances of the max value
		int count = count(array, maxWeight(array, max));
		
		//Return the nth location
		int n = (int)(1 + Math.random() * count);
		return nthLocation(array, n, maxWeight(array, max));
	}
	
	//Returns the location of the nth appearance of value;
	//same type of return as 'getMaxLocation()' method
	public static Point nthLocation(WeightArray[] array, int n, float value) {
		
		int count = 0;
		int current = 0;

		//Loop through the array
		for (WeightArray a : array) {

			//Loop through each weight array
			for (int i = 0; i < a.size(); i ++) {
				if (a.getWeight(i) == value) {
					count ++;
					
					if (count == n) {
						return new Point(current, i);
					}
				}
			}
			current ++;
		}
		
		//Failed
		return null;
	}
}
