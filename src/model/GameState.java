package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Observable;

public class GameState extends Observable {
	final Board board;
	final ArrayList<Ai> aiList = new ArrayList<Ai>();
	
	public GameState(Coordinate startPosition, int rows, int columns, double hexSideSize) {
		board = new Board(startPosition, rows, columns, hexSideSize);
	}
	
	public Hex[][] getHexMatrix() {
		return board.getHexMatrix();
	}
	
	public void insertAi(Ai ai) {
		aiList.add(ai);
		board.getHexMatrix()[ai.getPosition().getIntY()][ai.getPosition().getIntX()].setColor(ai.getColor());
	}
	
	public void newRound() {
		Hex[][] hexMatrix = board.getHexMatrix();
		for (Ai ai : aiList) {
			Coordinate orgPos = ai.getPosition();
			hexMatrix[orgPos.getIntY()][orgPos.getIntX()].setColor(Color.white);
			ai.moveAction();
			Coordinate newPos = ai.getPosition();
			hexMatrix[newPos.getIntY()][newPos.getIntX()].setColor(ai.getColor());
			
			setChanged();
			notifyObservers(hexMatrix);
			System.out.println("Taking a round");
		}
	}
}
