package model;

public class Coordinate {
	private double x;
	private double y;
	
	public Coordinate(double xIn, double yIn) {
		x = xIn;
		y = yIn;		
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setX(double newX) {
		x=newX;
	}
	
	public void setY(double newY) {
		y=newY;
	}

}
