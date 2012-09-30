package model;
import java.lang.Math;
import java.util.ArrayList;

public class Hex {
	private double side;
	private double h;
	private double r;
	private double x;
	private double y;
	private Coordinate startPosition;
	private ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
	
	public Hex(double sideLength, Coordinate position) {
	side = sideLength;
	x = position.getX();
	y = position.getY();
	h = Math.sin(Math.toRadians(30)) * side;
	r = Math.cos(Math.toRadians(30)) * side;
	calculateVertices();
	}
    private void calculateVertices()
    {
    	startPosition = new Coordinate(x, y);
    	coordinates.add(startPosition);
    	coordinates.add(new Coordinate(x + side, y));
    	coordinates.add(new Coordinate(x + side + h, y + r));
    	coordinates.add(new Coordinate(x + side, y + r + r));
    	coordinates.add(new Coordinate(x, y + r + r));
    	coordinates.add(new Coordinate(x - h, y + r));
    }
    public ArrayList<Coordinate> getVertices() {
    	return coordinates;
    }
    
    public Coordinate getStartPosition() {
    	return startPosition;
    }
}
