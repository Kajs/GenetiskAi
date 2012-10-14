package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Observable;

import model.Ai;
import model.Board;
import model.Coordinate;
import model.Hex;

public class GameState extends Observable {
	final Board board;
	private ArrayList<Ai> team1Alive = new ArrayList<Ai>();
	private ArrayList<Ai> team1Dead = new ArrayList<Ai>();
	private ArrayList<Ai> team2Alive = new ArrayList<Ai>();
	private ArrayList<Ai> team2Dead = new ArrayList<Ai>();
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
	
	public void insertAi(Ai ai, int team, Color color) {
		if (team == 1) { team1Alive.add(ai); }
		else { if (team == 2) {team2Alive.add(ai); }
		       else {System.out.println("Ai team was not 1 or 2");}
		}
		Hex hex = hexMatrix[ai.getPosition().getX()][ai.getPosition().getY()];
		ai.setColor(color);
		
		hex.setColor(ai.getColor());
		hex.setAi(ai);		
	}
	
	public void newRound() {
	    System.out.println("Taking a round");
		for (Ai ai : team1Alive) {
			Coordinate orgPos = ai.getPosition();
			Hex orgHex = hexMatrix[orgPos.getX()][orgPos.getY()];
			Action preferredAction = ai.action(adjacentHexes(orgPos), hexCake(orgPos), teamHp(team1Alive), teamHp(team2Alive));
			Coordinate newPos = preferredAction.getPosition();
			Hex newHex = hexMatrix[newPos.getX()][newPos.getY()];
			Ai targetAi = newHex.getAi();
			if (preferredAction.getType().equals("move")) {
				moveAi(ai, orgHex, newHex);
			} else {
				targetAi.setHp(targetAi.getHp()-ai.getMeleeDamage());
				if (!targetAi.isAlive()) {
					killAi(newHex);
				}
			}
			
		}
		for (Ai ai : team2Alive) {
			Coordinate orgPos = ai.getPosition();
			Hex orgHex = hexMatrix[orgPos.getX()][orgPos.getY()];
			Action preferredAction = ai.action(adjacentHexes(orgPos), hexCake(orgPos), teamHp(team2Alive), teamHp(team1Alive));
			Coordinate newPos = preferredAction.getPosition();
			Hex newHex = hexMatrix[newPos.getX()][newPos.getY()];
			Ai targetAi = newHex.getAi();
			if (preferredAction.getType().equals("move")) {
				moveAi(ai, orgHex, newHex);
			} else {
				targetAi.setHp(targetAi.getHp()-ai.getMeleeDamage());
				if (!targetAi.isAlive()) {
					killAi(newHex);
				}
			}
		}
		setChanged();
		notifyObservers(hexMatrix);
	}
	
	public void killAi(Hex hex) {
		Ai ai = hex.getAi();
		hex.removeAi();
		if (ai.getTeam() == 1) {
			team1Alive.remove(ai);
			team1Dead.add(ai);
		}
		else {
			team2Alive.remove(ai);
			team2Dead.add(ai);			
		}
	}
	
	public void moveAi(Ai ai, Hex orgHex, Hex newHex) {
		orgHex.removeAi();
		newHex.setAi(ai);
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
	
	public void colorHex(Coordinate position, Color color) {
		hexMatrix[position.getX()][position.getY()].setColor(color);
		setChanged();
		notifyObservers(hexMatrix);
	}
	
	private ArrayList<ArrayList<Hex>> hexCake(Coordinate origin) {
		ArrayList<ArrayList<Hex>> hexCake = new ArrayList<ArrayList<Hex>>();
		ArrayList<Hex> north = new ArrayList<Hex>();
		ArrayList<Hex> northEast = new ArrayList<Hex>();
		ArrayList<Hex> southEast = new ArrayList<Hex>();
		ArrayList<Hex> south = new ArrayList<Hex>();
		ArrayList<Hex> southWest = new ArrayList<Hex>();
		ArrayList<Hex> northWest = new ArrayList<Hex>();
		
		int x = origin.getX() - 1;
		int y = origin.getY();
		boolean isEven = y % 2 == 0;
		boolean prepareToBreak;
		
		//get NORTH (x - 1, y + 0)
		    prepareToBreak = false;
			for (int col = y; col >= 0; col--) {
				if(x < 0 || y < 0 || x >= rows || y >= columns) { break; }
				
				for (int row = x; row >= 0; row--) {
					//hexMatrix[row][col].setColor(Color.yellow);
					north.add(hexMatrix[row][col]);
				}
				
				if(prepareToBreak) { break; }
				if(x == 0) { prepareToBreak = true; }
				
				if (col % 2 == 0 && x > 0) {
					x--;
				}
			}
			
			x = origin.getX() - 1;
			if(y % 2 == 0) { x--; }
			prepareToBreak = false;
			
			for (int col = y + 1; col < columns; col++) {
				if(x < 0 || y < 0 || x >= rows || y >= columns) { break; }
				
				for (int row = x; row >= 0; row--) {
					//hexMatrix[row][col].setColor(Color.yellow);
					north.add(hexMatrix[row][col]);
				}
				
				if(prepareToBreak) { break; }
				if(x == 0) { prepareToBreak = true; }
				
				if (col % 2 == 0 && x > 0) {
					x--;
				}
			}
			
		//check SOUTH (x + 1, y + 0)
			x = origin.getX() + 1;
			prepareToBreak = false;
			for (int col = y; col >= 0; col--) {
				if(x < 0 || y < 0 || x >= rows || y >= columns) { break; }
				
				for (int row = x; row < rows; row++) {
				    //hexMatrix[row][col].setColor(Color.yellow);
					south.add(hexMatrix[row][col]);
				}
				
				if(prepareToBreak) { break; }
				if(x == rows - 1) { prepareToBreak = true; }
				
				if (col % 2 == 1 && x < rows - 1) {
					x++;
				}
			}			
			
			x = origin.getX() + 1;
			if (y % 2 == 1) { x++; }
			prepareToBreak = false;
			for (int col = y + 1; col < columns; col++) {
				if(x < 0 || y < 0 || x >= rows || y >= columns) { break; }
				
				for (int row = x; row < rows; row++) {
					//hexMatrix[row][col].setColor(Color.yellow);
					south.add(hexMatrix[row][col]);
				}
				
				if(prepareToBreak) { break; }
				if(x == rows - 1) { prepareToBreak = true; }
				
				if (col % 2 == 1 && x < rows - 1) {
					x++;
				}
			}
		
		//check NORTH WEST (even: x - 1, y - 1. odd: x - 0, y - 1) 
            if(isEven) { x = origin.getX() - 1; }
            else { x = origin.getX(); }
            y = origin.getY() - 1;
            
			for (int col = y; col >= 0; col--) {
				if(x < 0 || y < 0 || x >= rows || y >= columns) { break; }
				
				for (int row = x; row >= 0; row--) {
					//hexMatrix[row][col].setColor(Color.yellow);
					northWest.add(hexMatrix[row][col]);
				}
				if (col % 2 == 1 && x < rows - 1) {
					x++;
				}
			}
		
		//check NORTH EAST (even: x - 1, y + 1. odd: x - 0, y + 1)
			if(isEven) { x = origin.getX() - 1; }
            else { x = origin.getX(); }
			y = origin.getY() + 1;
			
			for (int col = y; col < columns; col++) {
				if(x < 0 || y < 0 || x >= rows || y >= columns) { break; }
				
				for (int row = x; row >= 0; row--) {
					//hexMatrix[row][col].setColor(Color.yellow);
					northEast.add(hexMatrix[row][col]);
				}
				if (col % 2 == 1 && x < rows - 1) {
					x++;
				}
			}
		
		//check SOUTH EAST (even: x + 0, y + 1. odd: x + 1, y + 1)
			if(isEven) { x = origin.getX(); }
            else { x = origin.getX() + 1; }
			y = origin.getY() + 1;
			
			for (int col = y; col < columns; col++) {
				if(x < 0 || y < 0 || x >= rows || y >= columns) { break; }
				
				for (int row = x; row < rows; row++) {
					//hexMatrix[row][col].setColor(Color.yellow);
					southEast.add(hexMatrix[row][col]);
				}
				if (col % 2 == 0 && x > 0) {
					x--;
				}
			}
			
		//check SOUTH WEST (even: x + 0, y - 1. odd: x + 1, y - 1)
			if(isEven) { x = origin.getX(); }
            else { x = origin.getX() + 1; }
			y = origin.getY() - 1;
			
			for (int col = y; col >= 0; col--) {
				if(x < 0 || y < 0 || x >= rows || y >= columns) { break; }
				
				for (int row = x; row < rows; row++) {
					//hexMatrix[row][col].setColor(Color.yellow);
					southWest.add(hexMatrix[row][col]);
				}
				if (col % 2 == 0 && x > 0) {
					x--;
				}
			}
/*
System.out.println("north size: " + Integer.toString(north.size()));
System.out.println("north east size: " + Integer.toString(northEast.size()));
System.out.println("south east size: " + Integer.toString(southEast.size()));
System.out.println("south size: " + Integer.toString(south.size()));
System.out.println("south west size: " + Integer.toString(southWest.size()));
System.out.println("north west size: " + Integer.toString(northWest.size()));
*/
			hexCake.add(north);
			hexCake.add(northEast);
			hexCake.add(southEast);
			hexCake.add(south);
			hexCake.add(southWest);
			hexCake.add(northWest);
			return hexCake;
	}
	
	public Hex[] adjacentHexes(Coordinate coordinate) {
		Hex[] adjacentHexes = new Hex[6];
		
		for (int i = 0; i < 6; i++) {
			Coordinate adjacentCoordinate = coordinate.adjacentPosition(i);
			if(withinBounds(adjacentCoordinate)) {
				adjacentHexes[i] = hexMatrix[adjacentCoordinate.getX()][adjacentCoordinate.getY()];
			}
		}
		return adjacentHexes;
	}
	
	public boolean withinBounds(Coordinate coordinate) {
		int x = coordinate.getX();
		int y = coordinate.getY();
		return (x >= 0 && x < rows && y >= 0 && y < columns);
	}
	
	public int teamHp(ArrayList<Ai> ais) {
		int totalHp = 0;
		for (Ai ai : ais) {
			totalHp = totalHp + ai.getHp();
		}
		return totalHp;
	}
}
