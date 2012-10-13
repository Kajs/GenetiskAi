package model;
import static java.lang.Math.abs;

public class Coordinate {
	private double xD;
	private double yD;
	private int x;
	private int y;
	
	public Coordinate(double xD, double yD) {
		this.xD = xD;
		this.yD = yD;		
	}
	
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;		
	}
	
	public double getXD() {
		return xD;
	}
	
	public double getYD() {
		return yD;
	}
	
	public void setXD(double xD) {
		this.xD = xD;
	}
	
	public void setYD(double yD) {
		this.yD = yD;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int distance(Coordinate end) {
		return abs(end.getY() - y) + abs(end.getX() - x);
	}
	
	public Coordinate  adjacentHex(Coordinate co, int direction) {
		int x = co.getX();
		int y = co.getY();
		boolean isEven = (y % 2 == 0);
		if (direction == 0) { return new Coordinate(x + 1, y); }         //North
		if (direction == 1) {                                           //North East
			if (isEven) { return new Coordinate(x - 1, y + 1); }
			else { return new Coordinate(x, y + 1); }
		}
		if (direction == 2) {                                           //South East
			if (isEven) { return new Coordinate(x, y + 1); }
			else {return new Coordinate(x + 1, y + 1); }
		}
		if (direction == 3) { return new Coordinate(x + 1, y); }        //South
		if (direction == 4) {                                           //South West
			if (isEven) { return new Coordinate(x, y - 1); }
			else { return new Coordinate(x + 1, y - 1); }
		}
		if (direction == 5) {                                           //North West
			if (isEven) { return new Coordinate(x - 1, y - 1); }
			else { return new Coordinate(x, y - 1); }
		}
		return null;
	}

}
