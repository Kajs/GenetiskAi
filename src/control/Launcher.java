package control;

import control.Controller;
import model.Coordinate;

public class Launcher {
	static int width = 800;
	static int height = 600;
	static int rows = 7;
	static int columns = 8;
	static int maxRounds = 25;
	static int games = 100;
	static int populationSize = 1000;
	static double hexSideSize = 40;
	public static boolean isAutomatic = true;
	static Coordinate startPosition = new Coordinate(Math.sin(Math.toRadians(30)) * hexSideSize, 1);

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Controller control = new Controller(width, height, rows, columns, startPosition, hexSideSize, isAutomatic, maxRounds, games, populationSize);
	  }
}