package control;

import java.awt.Color;

import model.*;
import view.*;

public class Controller {

	public static GameState gameState;
	final BoardRenderer boardRenderer;
	final WindowManager window;
	
	public Controller(int width, int height, int rows, int columns, Coordinate startPosition, double hexSideSize) {
		gameState = new GameState(startPosition, rows, columns, hexSideSize);
		gameState.insertAi(new Warrior(new Coordinate(0, 0)), 1);
		gameState.insertAi(new Wizard(new Coordinate(5, 2)), 2);
		gameState.insertAi(new Wizard(new Coordinate(0, 2)), 2);
		
		boardRenderer = new BoardRenderer(rows, columns, gameState.getHexMatrix());
		boardRenderer.setBackground(Color.white);
		gameState.addObserver(boardRenderer);
		
		window = new WindowManager(width, height, boardRenderer, this);
	}
	
	public static int isOccupied(Coordinate coordinate) {
		return gameState.isOccupied(coordinate);
	}
}
