package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Observable;

public class GameState extends Observable {
	final Board board;
	private ArrayList<Ai> team1Alive = new ArrayList<Ai>();
	//private ArrayList<Ai> team1Dead = new ArrayList<Ai>();
	private ArrayList<Ai> team2Alive = new ArrayList<Ai>();
	//private ArrayList<Ai> team2Dead = new ArrayList<Ai>();
	private int rows;
	private int columns;
	
	public GameState(Coordinate startPosition, int rows, int columns, double hexSideSize) {
		this.rows = rows;
		this.columns = columns;
		board = new Board(startPosition, rows, columns, hexSideSize);
	}
	
	public Hex[][] getHexMatrix() {
		return board.getHexMatrix();
	}
	
	public void insertAi(Ai ai, int team) {
		if (team == 1) { team1Alive.add(ai); }
		else { team2Alive.add(ai); }
		board.getHexMatrix()[ai.getPosition().getX()][ai.getPosition().getY()].setColor(ai.getColor());
	}
	
	public void newRound() {
	    System.out.println("Taking a round");
		Hex[][] hexMatrix = board.getHexMatrix();
		for (Ai ai : team1Alive) {
			Coordinate orgPos = ai.getPosition();
			Hex hex = hexMatrix[orgPos.getX()][orgPos.getY()];
			hex.setColor(Color.white);
			hex.setOccupied(false);
			Coordinate newPos = ai.moveAction(team2Alive);
			hex = hexMatrix[newPos.getX()][newPos.getY()];
			hex.setColor(ai.getColor());
			hex.setOccupied(true);
		}
		for (Ai ai : team2Alive) {
			Coordinate orgPos = ai.getPosition();
			Hex hex = hexMatrix[orgPos.getX()][orgPos.getY()];
			hex.setColor(Color.white);
			hex.setOccupied(false);
			Coordinate newPos = ai.moveAction(team1Alive);
			hex = hexMatrix[newPos.getX()][newPos.getY()];
			hex.setColor(ai.getColor());
			hex.setOccupied(true);
		}
		setChanged();
		notifyObservers(hexMatrix);
	}
	
	public int isOccupied(Coordinate coordinate) {
		int x = coordinate.getX();
		int y = coordinate.getY();
		if (x >= 0 && y >= 0 && x < rows && y < columns) {
			if(board.getHexMatrix()[x][y].isOccupied()) { 
				return 1; }
			else { return 0; }
		}
		else { return -1; }
	}
}
