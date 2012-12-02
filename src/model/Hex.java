package model;
import java.awt.Color;
import java.awt.geom.Path2D;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

import model.Coordinate;

public class Hex {
	private double side;
	private double h;
	private double r;
	private double x;
	private double y;
	private Path2D shape;
	private Coordinate startPosition;
	private Color color;
	private Coordinate position;
	private boolean isOccupied = false;
    private Ai ai;
    
    private int[] consistentPosition;
	
	public Hex(double sideLength, Coordinate position) {
		color = Color.white;
		startPosition = position;
	    side = sideLength;
	    x = position.getXD();
	    y = position.getYD();
	    h = sin(toRadians(30)) * side;
	    r = cos(toRadians(30)) * side;
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
    
    public Coordinate getPosition() {
    	return position;
    }
    
    public void setPosition(Coordinate newPos) {
    	position = newPos;
    }
    
    public Ai getAi() {
    	return ai;
    }
    
    public void setAi(Ai ai) {
    	this.ai = ai;
    	isOccupied = true;
    	color = ai.getColor();
    	ai.setPosition(position);
    }
    
    public void removeAi() {
    	ai = null;
    	isOccupied = false;
    	color = Color.white;
    }
    /*
    public double physicalDistance(Hex endHex) {
    	double dx = endHex.getStartPosition().getXD() - startPosition.getXD();
    	double dy = endHex.getStartPosition().getYD() - startPosition.getYD();
    	double result = sqrt(dx * dx + dy * dy);
    	return result;    	
    }
    */
    public void setConsistentPosition(int[] consistentPosition) {this.consistentPosition = consistentPosition;}
    public int[] getConsistentPosition() {return consistentPosition;}
}
