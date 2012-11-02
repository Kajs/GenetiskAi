package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Observable;

import control.Controller;
import control.Launcher;
import model.Ai;
import model.Board;
import model.Coordinate;
import model.Hex;

public class GameState extends Observable {
	private Board board;
	private ArrayList<Ai> team1Alive = new ArrayList<Ai>();
	private ArrayList<Ai> team1Dead = new ArrayList<Ai>();
	private ArrayList<Ai> team2Alive = new ArrayList<Ai>();
	private ArrayList<Ai> team2Dead = new ArrayList<Ai>();
	private final int rows;
	private final int columns;
	private Hex[][] hexMatrix;
	//private GeneticAlgorithm gA = new GeneticAlgorithm(); //for testing purposes
	
	public GameState(Coordinate startPosition, int rows, int columns, double hexSideSize) {
		this.rows = rows;
		this.columns = columns;
		board = new Board(startPosition, rows, columns, hexSideSize);
		hexMatrix = board.getHexMatrix();
	}
	
	public void reset() {
		team1Alive.clear();
		team1Dead.clear();
		team2Alive.clear();
		team2Dead.clear();
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				hexMatrix[r][c].reset();
			}
		}
	}
	
	public double[][] newGame(int maxRounds) {
		//format: 0_teamInitialHp, 1_teamHp, 2_teamAlive, 3_teamSize, 4_enemiesInitialHp, 5_enemiesHp, 6_enemiesAlive, 7_enemiesSize, 8_maxRounds, 9_rounds
		
		for (int i = 0; i <= maxRounds; i++) {
			//System.out.println("Starting round " + i);
			newRound();
			
			if(Launcher.allowRoundDelay) {
				try { Thread.sleep(Controller.roundDelay); } 
				catch(InterruptedException ex) { Thread.currentThread().interrupt(); }
			}
			
			while(Launcher.isPaused) {
				try { Thread.sleep(1000); } 
				catch(InterruptedException ex) { Thread.currentThread().interrupt(); }
			}
			
			if(team1Alive.isEmpty() || team2Alive.isEmpty() || i == maxRounds) {
				//if(team1Alive.isEmpty()) {System.out.println("Team 1 died _-_-_-_");}
				//if(team2Alive.isEmpty()) {System.out.println("Team 2 died _-_-_-_");}
				//if(i == maxRounds) {System.out.println("Max rounds reached");}
				
				//System.out.println("Team 1 fitnes: " + Controller.round(gA.fitness(results[0]), 2) + ", " + team1InitialHp + ", " + team1Hp + ", " + team1Survivors + ", " + team1Size + ", " + team2InitialHp + ", " + team2Hp + ", " + team2Survivors + ", " + team2Size);
				return getResults(maxRounds, i);
			}
		}
		
		System.out.println("Error in GameState.newGame(): did not stop properly");
		return null;
	}
	
	public double[][] getResults(int maxRounds, int currentRound){
		double[][] results = new double[2][10];
		double[] team1Results = new double[10];
		double[] team2Results = new double[10];
		
		double team1InitialHp = 0;
		double team1Hp = 0;
		double team1Survivors = (double) team1Alive.size();
		double team1Size = team1Alive.size() + team1Dead.size();
		for (Ai ai : team1Alive) { 
			team1InitialHp = team1InitialHp + ai.getInitialHp();
			team1Hp = team1Hp + ai.getHp();
		}
		for (Ai ai : team1Dead)  {
			team1InitialHp = team1InitialHp + ai.getInitialHp();
		}
		
		double team2InitialHp = 0;
		double team2Hp = 0;
		double team2Survivors = (double) team2Alive.size();
		double team2Size = team2Alive.size() + team2Dead.size();
		for (Ai ai : team2Alive) { 
			team2InitialHp = team2InitialHp + ai.getInitialHp();
			team2Hp = team2Hp + ai.getHp();
		}
		for (Ai ai : team2Dead)  {
			team2InitialHp = team2InitialHp + ai.getInitialHp();
		}
		
		team1Results[0] = team1InitialHp;
		team1Results[1] = team1Hp;
		team1Results[2] = team1Survivors;
		team1Results[3] = team1Size;
		team1Results[4] = team2InitialHp;
		team1Results[5] = team2Hp;
		team1Results[6] = team2Survivors;
		team1Results[7] = team2Size;
		team1Results[8] = maxRounds;
		team1Results[9] = currentRound;     //rounds
		
		team2Results[0] = team2InitialHp;
		team2Results[1] = team2Hp;
		team2Results[2] = team2Survivors;
		team2Results[3] = team2Size;
		team2Results[4] = team1InitialHp;
		team2Results[5] = team1Hp;
		team2Results[6] = team1Survivors;
		team2Results[7] = team1Size;
		team2Results[8] = maxRounds;
		team2Results[9] = currentRound;     //rounds
		
		if(team1Hp < 0 || team2Hp < 0) {System.out.println("Error: a teams alive players had total hp < 0"); }
		
		results[0] = team1Results;
		results[1] = team2Results;	
		return results;
	}
	
	public Hex[][] getHexMatrix() {
		return hexMatrix;
	}
	
	public void insertAi(Ai ai, int team, Color color, Coordinate startPosition) {
		ai.setTeam(team);
		ai.setColor(color);
		ai.setPosition(startPosition);
		ai.generateId();
		
		if (team == 1) { team1Alive.add(ai); }
		else { if (team == 2) {team2Alive.add(ai); }
		       else {System.out.println("Ai team was not 1 or 2");}
		}
		
		Hex hex = hexMatrix[ai.getPosition().getX()][ai.getPosition().getY()];		
		hex.setColor(ai.getColor());
		hex.setAi(ai);
		
		setChanged();
		notifyObservers(hexMatrix);
	}
	
	public void newRound() {
		if(Launcher.toggleRoundSeparator) { System.out.println("New round____________________"); }
		
		for (Ai ai : team1Alive) {
			if (ai.getStunned()) { 
				ai.setStunned(false);
				}
			else {
				Coordinate orgPos = ai.getPosition();
				Hex orgHex = hexMatrix[orgPos.getX()][orgPos.getY()];
				Action preferredAction = ai.action(adjacentHexes(orgPos), hexCake(orgPos), teamHp(team1Alive), teamHp(team2Alive), (double) team2Alive.size(), (double) team1Alive.size());
				
				parseAction(preferredAction, ai, orgHex);
			}			
		}
		for (Ai ai : team2Alive) {
			if (ai.getStunned()) {
				ai.setStunned(false);
			}
			else {
				Coordinate orgPos = ai.getPosition();
				Hex orgHex = hexMatrix[orgPos.getX()][orgPos.getY()];
				Action preferredAction = ai.action(adjacentHexes(orgPos), hexCake(orgPos), teamHp(team2Alive), teamHp(team1Alive), (double) team1Alive.size(), (double) team2Alive.size());
				
				parseAction(preferredAction, ai, orgHex);
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
	
	public double teamHp(ArrayList<Ai> ais) {
		double totalHp = 0;
		for (Ai ai : ais) {
			totalHp = totalHp + ai.getHp();
		}
		return totalHp;
	}
	
	public void parseAction(Action preferredAction, Ai ai, Hex orgHex) {
		Coordinate newPos = preferredAction.getPosition();
		Hex newHex = hexMatrix[newPos.getX()][newPos.getY()];		
		String baseType = preferredAction.getBaseType();
		String extendedType = preferredAction.getExtendedType();
		Ai targetAi = newHex.getAi();
		
		switch(baseType) {
		case "move":
			moveAi(ai, orgHex, newHex);
			break;
		case "attack":
			if(extendedType.equals("stun")) {
				targetAi.setStunned(true);
			}
			else {
				if(targetAi.getShielded()) {
					targetAi.setShielded(false);
				}
				else {
					targetAi.setHp(targetAi.getHp() - ai.getMeleeDamage());
					if (targetAi.isAlive() == false) {
						killAi(newHex);
						//System.out.println("Killed " + targetAi.getId() + " team1Size: " + team1Alive.size() + ", team2Size: " + team2Alive.size());						
					}
				}
			}
			break;
		case "support":
			if(extendedType.equals("shield")) {
				targetAi.setShielded(true);
			}
			if(extendedType.equals("heal")) {
				double currentHp = targetAi.getHp();
				double initialHp = targetAi.getInitialHp();
				if(currentHp < initialHp) {
					targetAi.setHp(min(targetAi.getHp() + ai.getHealAmount(), initialHp));
				}
			}
			break;
		default:
			System.out.println("Unknown action type: " + baseType + ", " + extendedType);
			break;
		}
	}
	
	public double min(double a, double b) {
		if(a <= b) {
			return a;
		}
		else{
			return b;
		}
	}
}
