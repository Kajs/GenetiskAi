package model;
import java.awt.Color;
import java.awt.geom.Path2D;
import java.lang.Math;

public class Hex {
	private double side;
	private double h;
	private double r;
	private double x;
	private double y;
	private Path2D shape;
	private Coordinate startPosition;
	private Color color;
	private boolean isOccupied = false;
	
	public Hex(double sideLength, Coordinate position) {
		color = Color.white;
		startPosition = position;
	    side = sideLength;
	    x = position.getXD();
	    y = position.getYD();
	    h = Math.sin(Math.toRadians(30)) * side;
	    r = Math.cos(Math.toRadians(30)) * side;
	    makeShape();
	}
	
    private void makeShape()
    {
    	startPosition = new Coordinate(x, y);
    	shape = new Path2D.Double();
    	shape.moveTo(x, y);
    	shape.lineTo(x + side, y);
    	shape.lineTo(x + side + h, y + r);
    	shape.lineTo(x + side, y + r + r);
    	shape.lineTo(x, y + r + r);
    	shape.lineTo(x - h, y + r);
    	shape.closePath();
    }
    
    public void setColor(Color newColor) {
    	color = newColor;
    }
    
    public Color getColor() {
    	return color;
    }
    
    public Coordinate getStartPosition() {
    	return startPosition;
    }
    
    public Path2D getShape() {
    	return shape;
    }
    
    public boolean isOccupied() {
    	return isOccupied;
    }
    
    public void setOccupied(boolean isOccupied) {
    	this.isOccupied = isOccupied;
    }
}
