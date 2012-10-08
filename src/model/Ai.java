package model;

import java.awt.Color;

public class Ai {
	private Coordinate position;
	private Color color;
	
	
	public Ai(Coordinate startingPosition, int aiType) {
		position = startingPosition;
		if (aiType == 1) { color = Color.green; }
		else { color = Color.red; }
		
	}
	
	public void moveAction() {
		position.setIntX((position.getIntX()+1));
		position.setIntY((position.getIntY()+1));
	}
	
	public Coordinate getPosition() {
		return position;
	}
	
	public Color getColor() {
		return color;
	}
}
