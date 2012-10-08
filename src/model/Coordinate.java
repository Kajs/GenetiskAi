package model;

public class Coordinate {
	private double x;
	private double y;
	private int intX;
	private int intY;
	
	public Coordinate(double xIn, double yIn) {
		x = xIn;
		y = yIn;		
	}
	
	public Coordinate(int xIn, int yIn) {
		intX = xIn;
		intY = yIn;		
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setX(double newX) {
		x = newX;
	}
	
	public void setY(double newY) {
		y = newY;
	}
	
	public int getIntX() {
		return intX;
	}
	
	public int getIntY() {
		return intY;
	}
	
	public void setIntX(int newX) {
		intX = newX;
	}
	
	public void setIntY(int newY) {
		intY = newY;
	}

}
