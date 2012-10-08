package control;

import java.awt.Color;

import model.*;
import view.*;

public class Controller {

	static GameState gameState;
	BoardRenderer boardRenderer;
	WindowManager window;
	
	public Controller(int width, int height, int rows, int columns, Coordinate startPosition, double hexSideSize) {
		gameState = new GameState(startPosition, rows, columns, hexSideSize);
		gameState.insertAi(new Ai(new Coordinate(0, 0), 1));
		
		boardRenderer = new BoardRenderer(rows, columns, gameState.getHexMatrix());
		boardRenderer.setBackground(Color.white);
		window = new WindowManager(width, height, boardRenderer);
		testMove();
	}
	
	public void testMove() {
		gameState.newRound();
		boardRenderer.updateHexes(gameState.getHexMatrix());
	}
}
