package control;

import java.awt.Color;

import view.*;

public class Controller {

	BoardRenderer boardRenderer;
	WindowManager window;
	
	public Controller(int windowWidth, int windowHeight) {
		boardRenderer = new BoardRenderer();
		boardRenderer.setBackground(Color.white);
		window = new WindowManager(windowWidth, windowHeight, boardRenderer);
	}
}
