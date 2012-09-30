package model;

public class Board {
	private Hex[][] hexMatrix;
    public Board(double startX, double startY, int sizeX, int sizeY, double hexSideSize) { 
    	hexMatrix = new Hex[sizeX][sizeY];
    	double h = Math.sin(Math.toRadians(30)) * hexSideSize;
    	double r = Math.cos(Math.toRadians(30)) * hexSideSize;
		double a = 2 * r;
    	for(int columns = 0; columns < sizeY; columns++) {
    		double displacement = columns % 2 * a/2;
    		for(int rows = 0; rows < sizeX; rows++) {
    			double xScale = rows * a;
    			double yScale = columns * (hexSideSize + h) + displacement;
    			hexMatrix[rows][columns] = new Hex(hexSideSize, new Coordinate(xScale + startX, yScale + startY));
    		}
    	}
    }
    public Hex[][] getHexes() {
    	return hexMatrix;
    }

}
