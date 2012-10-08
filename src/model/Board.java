package model;

public class Board {
	private Hex[][] hexMatrix;
	private Hex[] hexArray;
	
    public Board(Coordinate startPosition, int rows, int columns, double hexSideSize) { 
    	hexMatrix = new Hex[rows][columns];
    	double h = Math.sin(Math.toRadians(30)) * hexSideSize;
    	double r = Math.cos(Math.toRadians(30)) * hexSideSize;
		double a = 2 * r;
    	for(int col = 0; col < columns; col++) {
    		double displacement = col % 2 * a/2;
    		hexArray = new Hex[rows];
    		for(int row = 0; row < rows; row++) {
    			double xScale = col * (hexSideSize + h);
    			double yScale = row * (2 * r) + displacement;
    			double startPosX = startPosition.getX();
    			double startPosY = startPosition.getY();
    			Hex hex = new Hex(hexSideSize, new Coordinate(xScale + startPosX, yScale + startPosY), row, col);
    			hexArray[row] = hex;
    		}
    		hexMatrix[col] = hexArray;
    	}
    }
    
    public Hex[][] getHexMatrix() {
    	return hexMatrix;
    }
}
