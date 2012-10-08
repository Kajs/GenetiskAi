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
		gameState.insertAi(new Ai(new Coordinate(0, 0), 1));
		
		boardRenderer = new BoardRenderer(rows, columns, gameState.getHexMatrix());
		boardRenderer.setBackground(Color.white);
		gameState.addObserver(boardRenderer);
		
		window = new WindowManager(width, height, boardRenderer, this);
	}
	
	public void testMove() {
		gameState.newRound();
	}
}
