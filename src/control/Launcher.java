package control;

import model.Coordinate;

public class Launcher {
	static int width = 800;
	static int height = 600;
	static int rows = 20;
	static int columns = 15;
	static double hexSideSize = 15;
	static Coordinate startPosition = new Coordinate(Math.sin(Math.toRadians(30)) * hexSideSize, 1);

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Controller control = new Controller(width, height, rows, columns, startPosition, hexSideSize);	    
	  }
}