package model;

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

}
