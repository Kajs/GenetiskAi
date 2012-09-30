package model;

import java.util.ArrayList;

public class Board {
	private ArrayList<ArrayList<Hex>> allColumns = new ArrayList<ArrayList<Hex>>();
	private ArrayList<Hex> column;
	
    public Board(Coordinate startPosition, int sizeX, int sizeY, double hexSideSize) { 
    	double h = Math.sin(Math.toRadians(30)) * hexSideSize;
    	double r = Math.cos(Math.toRadians(30)) * hexSideSize;
		double a = 2 * r;
    	for(int columns = 0; columns < sizeY; columns++) {
    		double displacement = columns % 2 * a/2;
    		column = new ArrayList<Hex>();
    		for(int rows = 0; rows < sizeX; rows++) {
    			double xScale = columns * (hexSideSize + h);
    			double yScale = rows * (2 * r) + displacement;
    			double startPosX = startPosition.getX();
    			double startPosY = startPosition.getY();
    			Hex hex = new Hex(hexSideSize, new Coordinate(xScale + startPosX, yScale + startPosY));
    			String posX = Double.toString(xScale + startPosX);
    			String posY = Double.toString(yScale + startPosY);
    			System.out.println("BoardHex at: " + posX + ", " + posY);
    			column.add(rows, hex);
    		}
    		System.out.println("Board: column size is: " + column.size());
    		allColumns.add(column);
    	}
    	System.out.println("HexMatrixSize: " + Integer.toString(allColumns.size()));
    }
    public ArrayList<ArrayList<Hex>> getHexMatrix() {
    	return allColumns;
    }

}
