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
	private final int rows;
	private final int columns;
	private final Hex[][] hexMatrix;
	
	public GameState(Coordinate startPosition, int rows, int columns, double hexSideSize) {
		this.rows = rows;
		this.columns = columns;
		board = new Board(startPosition, rows, columns, hexSideSize);
		hexMatrix = board.getHexMatrix();
	}
	
	public Hex[][] getHexMatrix() {
		return hexMatrix;
	}
	
	public void insertAi(Ai ai, int team) {
		if (team == 1) { team1Alive.add(ai); }
		else { team2Alive.add(ai); }
		board.getHexMatrix()[ai.getPosition().getX()][ai.getPosition().getY()].setColor(ai.getColor());
	}
	
	public void newRound() {
	    System.out.println("Taking a round");
		for (Ai ai : team1Alive) {
			Coordinate orgPos = ai.getPosition();
			Hex hex = hexMatrix[orgPos.getX()][orgPos.getY()];
			hex.setColor(Color.white);
			hex.setOccupied(false);
			
			Coordinate newPos = ai.moveAction(team2Alive);
			hex = hexMatrix[newPos.getX()][newPos.getY()];
			hex.setColor(ai.getColor());
			hex.setOccupied(true);
			hexCakeSlice(orgPos, new Coordinate(orgPos.getX() - 1, orgPos.getY() + 1));
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
			if(hexMatrix[x][y].isOccupied()) { 
				return 1; }
			else { return 0; }
		}
		else { return -1; }
	}
	
	public void colorHex(int row, int column, Color color) {
		hexMatrix[row][column].setColor(color);
		setChanged();
		notifyObservers(hexMatrix);
	}
	
	private void hexCakeSlice(Coordinate origin, Coordinate direction) {
		int x = direction.getX();
		int y = direction.getY();
		int dx = x - origin.getX();
		int dy = y - origin.getY();
		System.out.println("dx, dy, y %2: " + Integer.toString(dx) + ", " + Integer.toString(dy) + ", " + Integer.toString(y % 2));
		if(dx == -1 && dy == 0) {
			for (int col = y; col >= 0; col--) {
				for (int row = x; row >= 0; row--) {
					colorHex(row, col, Color.black);
				}
				if (col % 2 == 0 && x > 0) {
					x--;
				}
			}
			
			x = direction.getX();
			for (int col = y; col < columns; col++) {
				for (int row = x; row >= 0; row--) {
					colorHex(row, col, Color.black);
				}
				if (col % 2 == 0 && x > 0) {
					x--;
				}
			}
		}
		
		else { if (dx == 1 && dy == 0) {
			for (int col = y; col >= 0; col--) {
				for (int row = x; row < rows; row++) {
					colorHex(row, col, Color.black);
				}
				if (col % 2 == 0 && x < rows - 1) {
					x++;
				}
			}
			
			x = direction.getX();
			for (int col = y; col < columns; col++) {
				for (int row = x; row < rows; row++) {
					colorHex(row, col, Color.black);
				}
				if (col % 2 == 0 && x < rows - 1) {
					x++;
				}
			}
		    }
		
		else { if (dy == -1 && ((dx == 0 && y % 2 == 0) || (dx == 0 && y % 2 == 1))) {
			for (int col = y; col >= 0; col--) {
				for (int row = x; row >= 0; row--) {
					colorHex(row, col, Color.black);
				}
				if (col % 2 == 1 && x < rows - 1) {
					x++;
				}
			}
		    }
		
		else { if (dx == -1 && dy == 1) {
			for (int col = y; col < columns; col++) {
				for (int row = x; row >= 0; row--) {
					colorHex(row, col, Color.black);
				}
				if (col % 2 == 1 && x < rows - 1) {
					x++;
				}
			}
		    }
		else { if (false) {
			for (int col = y; col < columns; col++) {
				for (int row = x; row >= 0; row--) {
					colorHex(row, col, Color.black);
				}
				if (col % 2 == 1 && x < rows - 1) {
					x++;
				}
			}
		    }
			
		}
		}
		}
		}
	}
}
