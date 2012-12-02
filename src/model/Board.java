package model;

import model.Coordinate;
import model.Hex;

public class Board {
	final Hex[][] hexMatrix;
	private Hex[] hexArray;
	
    public Board(Coordinate startPosition, int rows, int columns, double hexSideSize) { 
    	hexMatrix = new Hex[rows][columns];
    	double h = Math.sin(Math.toRadians(30)) * hexSideSize;
    	double r = Math.cos(Math.toRadians(30)) * hexSideSize;
		double a = 2 * r;
		
    	for(int row = 0; row < rows; row++) {
    		int consistentX = 0 + row;
    		int consistentY = 0 - row;
    		boolean consistentFlip = true;
    		
    		hexArray = new Hex[columns];
    		for(int col = 0; col < columns; col++) {
    			double displacement = col % 2 * a/2;
    			double xScale = col * (hexSideSize + h);
    			double yScale = row * (2 * r) + displacement;
    			double startPosX = startPosition.getXD();
    			double startPosY = startPosition.getYD();
    			
    			Hex hex = new Hex(hexSideSize, new Coordinate(xScale + startPosX, yScale + startPosY));
    			hex.setPosition(new Coordinate(row, col));
    			
    			int[] consistentPosition = new int[2];
    			if(consistentFlip) {consistentPosition[0] = consistentX++; consistentPosition[1] = consistentY;}
    			else {consistentPosition[0] = consistentX; consistentPosition[1] = consistentY++;}
    			consistentFlip = !consistentFlip;
    			hex.getPosition().setConsistentPosition(consistentPosition);
    			
    			hexArray[col] = hex;
    		}
    		hexMatrix[row] = hexArray;
    	}
    }
    
    public Hex[][] getHexMatrix() {
    	return hexMatrix;
    }
}
