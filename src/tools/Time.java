package tools;

public class Time {
	
	private int millis;
	private int seconds;
	private int minutes;
	private int hours;
	
	//Constructor
	public Time() {
	
		reset();
	}
	
	//Resets the time
	public void reset() {
		
		this.millis = 0;
		this.seconds = 0;
		this.minutes = 0;
		this.hours = 0;
	}
	
	//Creates a string output
	public String toString() {
		
		String result = "";
		
		if (hours > 0) { //At least one hour of time
			result += hours + ":";
		}
		result += minutes + ":";
		if (seconds < 10) { //Need an extra zero
			result += "0" + seconds;
		}
		else {
			result += seconds;
		}
		
		return result;
	}
	
	//Increments the total time
	public void increment(int millis) {
		
		this.millis += millis;
		if (this.millis >= 1000) { //Adjust milliseconds and seconds
			this.seconds += + this.millis/1000;
			this.millis %= 1000;
		}
		if (seconds >= 60) { //Adjust seconds and minutes
			this.minutes += seconds/60;
			this.seconds %= 60;
		}
		if (minutes >= 60) { //Adjust minutes and hours
			this.hours += minutes/60;
			this.minutes %= 60;
		}
	}

}
