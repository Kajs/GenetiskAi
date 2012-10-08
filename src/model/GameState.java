package model;

import java.awt.Color;
import java.util.ArrayList;

public class GameState {
	private Board board;
	private ArrayList<Ai> aiList = new ArrayList<Ai>();
	
	public GameState(Coordinate startPosition, int rows, int columns, double hexSideSize) {
		board = new Board(startPosition, rows, columns, hexSideSize);
	}
	
	public Hex[][] getHexMatrix() {
		return board.getHexMatrix();
	}
	
	public void insertAi(Ai ai) {
		aiList.add(ai);
	}
	
	public void newRound() {
		Hex[][] hexMatrix = board.getHexMatrix();
		for (Ai ai : aiList) {
			Coordinate orgPos = ai.getPosition();
			hexMatrix[orgPos.getIntX()][orgPos.getIntY()].setColor(Color.white);
			ai.moveAction();
			Coordinate newPos = ai.getPosition();
			hexMatrix[newPos.getIntX()][newPos.getIntY()].setColor(ai.getColor());
		}
	}
}
