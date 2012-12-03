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
	final ArrayList<Ai> team1Alive = new ArrayList<Ai>();
	final ArrayList<Ai> team1Dead = new ArrayList<Ai>();
	final ArrayList<Ai> team2Alive = new ArrayList<Ai>();
	final ArrayList<Ai> team2Dead = new ArrayList<Ai>();
	final int rows;
	final int columns;
	final double boardDiagonal = Controller.boardDiagonal;
	
	final boolean allowAngleOutput = Launcher.allowAngleOutput;
	final boolean allowAreaDamageOutput = Launcher.allowAreaDamageOutput;
	final boolean allowHealOutput = Launcher.allowHealOutput;
	final boolean allowNormalDamageOutput = Launcher.allowNormalDamageOutput;
	
	private Hex[][] hexMatrix;
	
	public GameState(Coordinate startPosition, int rows, int columns, double hexSideSize) {
		this.rows = rows;
		this.columns = columns;
		board = new Board(startPosition, rows, columns, hexSideSize);
		hexMatrix = board.getHexMatrix();
	}
	
	public double[][] newGame(int maxRounds, boolean team1Starts) {
		//format: 0_teamInitialHp, 1_teamHp, 2_teamAlive, 3_teamSize, 4_enemiesInitialHp, 5_enemiesHp, 6_enemiesAlive, 7_enemiesSize, 8_maxRounds, 9_rounds
		
		for (int i = 0; i <= maxRounds; i++) {
			
			if(team1Starts) { newRound(team1Alive, team2Alive); }
			else { newRound(team2Alive, team1Alive); }
			
			if(Launcher.allowRoundDelay) {
				try { Thread.sleep(Controller.roundDelay); } 
				catch(InterruptedException ex) { Thread.currentThread().interrupt(); }
			}
			
			while(Launcher.isPaused) {
				try { Thread.sleep(1000); } 
				catch(InterruptedException ex) { Thread.currentThread().interrupt(); }
			}
			
			if(team1Alive.isEmpty() || team2Alive.isEmpty() || i == maxRounds) {return getResults(maxRounds, i);}
		}
		
		System.out.println("Error in GameState.newGame(): did not stop properly");
		return null;
	}
	
	public void newRound(ArrayList<Ai> team1, ArrayList<Ai> team2) {
		if(Launcher.toggleRoundSeparator) { System.out.println("_____________________________"); }
		
		for (Ai ai : team1) {
			if (ai.getStunned()) { 
				ai.setStunned(false);
				}
			else {
				Coordinate orgPos = ai.getPosition();
				Hex orgHex = hexMatrix[orgPos.getX()][orgPos.getY()];
				Hex[] adjacentHexes = adjacentHexes(orgPos);
				double[][] adjacentHexAis = adjacentHexAis(adjacentHexes, ai.getTeam());
				Action preferredAction = ai.action(adjacentHexes, hexCakeOptimised(orgPos), teamHp(team1), teamHp(team2), (double) team1.size(), (double) team2.size(), adjacentHexAis, adjacentLocalAis(ai, orgHex), nearestAiDistances(ai, adjacentHexes));
				
				parseAction(preferredAction, ai, orgHex);
			}			
		}
		for (Ai ai : team2) {
			if (ai.getStunned()) {
				ai.setStunned(false);
			}
			else {
				Coordinate orgPos = ai.getPosition();
				Hex orgHex = hexMatrix[orgPos.getX()][orgPos.getY()];
				Hex[] adjacentHexes = adjacentHexes(orgPos);
				double[][] adjacentHexAis = adjacentHexAis(adjacentHexes, ai.getTeam());
				Action preferredAction = ai.action(adjacentHexes, hexCakeOptimised(orgPos), teamHp(team2), teamHp(team1), (double) team2.size(), (double) team1.size(), adjacentHexAis, adjacentLocalAis(ai, orgHex), nearestAiDistances(ai, adjacentHexes));
				
				parseAction(preferredAction, ai, orgHex);
			}
		}
		
		setChanged();
		notifyObservers(hexMatrix);
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
		ai.newStartId();
		
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
	
	private ArrayList<ArrayList<Hex>> hexCakeOptimised(Coordinate origin) {
		ArrayList<ArrayList<Hex>> hexCake = new ArrayList<ArrayList<Hex>>();
		ArrayList<Hex> north = new ArrayList<Hex>();
		ArrayList<Hex> northEast = new ArrayList<Hex>();
		ArrayList<Hex> southEast = new ArrayList<Hex>();
		ArrayList<Hex> south = new ArrayList<Hex>();
		ArrayList<Hex> southWest = new ArrayList<Hex>();
		ArrayList<Hex> northWest = new ArrayList<Hex>();
		
		Coordinate originPosition = hexMatrix[origin.getX()][origin.getY()].getStartPosition();
		Coordinate comparisonVector = new Coordinate(1.0,0.0);
		
		Ai originAi = hexMatrix[origin.getX()][origin.getY()].getAi();
		
		String id = originAi.getId();
		double team = originAi.getTeam();
		String targetAiType = "";
		
				
		for (Ai ai : team1Alive) {
			Coordinate aiPos = ai.getPosition();
			if (aiPos.getX() == origin.getX() && aiPos.getY() == origin.getY()) { continue; }
			
			if(ai.getTeam() == team) { targetAiType = "ally"; } else {targetAiType = "enemy"; }
			
			
			Hex targetHex = hexMatrix[aiPos.getX()][aiPos.getY()];
			Coordinate hexPosition = targetHex.getStartPosition();
			Coordinate hexVector = new Coordinate(hexPosition.getXD()-originPosition.getXD(), hexPosition.getYD()-originPosition.getYD());
			double angleToHex = Math.toDegrees((Math.atan2(hexVector.getYD(),hexVector.getXD())) - (Math.atan2(comparisonVector.getYD(),comparisonVector.getXD())));
			//System.out.println("Team1 Angle calculated: " + Controller.round(angleToHex, 2));
			
			// SouthEast
			if (angleToHex >= 0 && angleToHex <= 60) {
				if(allowAngleOutput) {System.out.println(id + " found " + targetAiType + " at SE: " + Controller.round(angleToHex, 2) + " (" + aiPos.getX() + ", " + aiPos.getY() + ")");}
				southEast.add(targetHex);
				continue;
			}
			//South
			if (angleToHex >= 60 && angleToHex <= 120) {
				if(allowAngleOutput) {System.out.println(id + " found " + targetAiType + " at S: " + Controller.round(angleToHex, 2) + " (" + aiPos.getX() + ", " + aiPos.getY() + ")");}
				south.add(targetHex);
				continue;
			}
			//SouthWest
			if (angleToHex >= 120 && angleToHex <= 180) {
				if(allowAngleOutput) {System.out.println(id + " found " + targetAiType + " at SW: " + Controller.round(angleToHex, 2) + " (" + aiPos.getX() + ", " + aiPos.getY() + ")");}
				southWest.add(targetHex);
				continue;
			}
			//NortEast
			if (angleToHex <= 0 && angleToHex >= -60) {
				if(allowAngleOutput) {System.out.println(id + " found " + targetAiType + " at NE: " + Controller.round(angleToHex, 2) + " (" + aiPos.getX() + ", " + aiPos.getY() + ")");}
				northEast.add(targetHex);
				continue;
			}
			//North
			if (angleToHex <= -60 && angleToHex >= -120) {
				if(allowAngleOutput) {System.out.println(id + " found " + targetAiType + " at N: " + Controller.round(angleToHex, 2) + " (" + aiPos.getX() + ", " + aiPos.getY() + ")");}
				north.add(targetHex);
				continue;
			}
			//NortWest
			if (angleToHex <= -120 && angleToHex >= -180) {
				if(allowAngleOutput) {System.out.println(id + " found " + targetAiType + " at NW: " + Controller.round(angleToHex, 2) + " (" + aiPos.getX() + ", " + aiPos.getY() + ")");}
				northWest.add(targetHex);
				continue;
			}
			
		}
		
		for (Ai ai : team2Alive) {
			Coordinate aiPos = ai.getPosition();
			if (aiPos.getX() == origin.getX() && aiPos.getY() == origin.getY()) { continue; }
			
			if(ai.getTeam() == team) { targetAiType = "ally"; } else {targetAiType = "enemy"; }
			
			Hex targetHex = hexMatrix[aiPos.getX()][aiPos.getY()];
			Coordinate hexPosition = targetHex.getStartPosition();
			Coordinate hexVector = new Coordinate(hexPosition.getXD()-originPosition.getXD(), hexPosition.getYD()-originPosition.getYD());
			double angleToHex = Math.toDegrees((Math.atan2(hexVector.getYD(),hexVector.getXD())) - (Math.atan2(comparisonVector.getYD(),comparisonVector.getXD())));
			//System.out.println("Team2 Angle calculated: " + Controller.round(angleToHex, 2));
			
			// SouthEast
			if (angleToHex >= 0 && angleToHex <= 60) {
				if(allowAngleOutput) {System.out.println(id + " found " + targetAiType + " at SE: " + Controller.round(angleToHex, 2) + " (" + aiPos.getX() + ", " + aiPos.getY() + ")");}
				southEast.add(targetHex);
				continue;
			}
			//South
			if (angleToHex >= 60 && angleToHex <= 120) {
				if(allowAngleOutput) {System.out.println(id + " found " + targetAiType + " at S: " + Controller.round(angleToHex, 2) + " (" + aiPos.getX() + ", " + aiPos.getY() + ")");}
				south.add(targetHex);
				continue;
			}
			//SouthWest
			if (angleToHex >= 120 && angleToHex <= 180) {
				if(allowAngleOutput) {System.out.println(id + " found " + targetAiType + " at SW: " + Controller.round(angleToHex, 2) + " (" + aiPos.getX() + ", " + aiPos.getY() + ")");}
				southWest.add(targetHex);
				continue;
			}
			//NortEast
			if (angleToHex <= 0 && angleToHex >= -60) {
				if(allowAngleOutput) {System.out.println(id + " found " + targetAiType + " at NE: " + Controller.round(angleToHex, 2) + " (" + aiPos.getX() + ", " + aiPos.getY() + ")");}
				northEast.add(targetHex);
				continue;
			}
			//North
			if (angleToHex <= -60 && angleToHex >= -120) {
				if(allowAngleOutput) {System.out.println(id + " found " + targetAiType + " at N: " + Controller.round(angleToHex, 2) + " (" + aiPos.getX() + ", " + aiPos.getY() + ")");}
				north.add(targetHex);
				continue;
			}
			//NortWest
			if (angleToHex <= -120 && angleToHex >= -180) {
				if(allowAngleOutput) {System.out.println(id + " found " + targetAiType + " at NW: " + Controller.round(angleToHex, 2) + " (" + aiPos.getX() + ", " + aiPos.getY() + ")");}
				northWest.add(targetHex);
				continue;
			}
		}
		
		hexCake.add(north);
		hexCake.add(northEast);
		hexCake.add(southEast);
		hexCake.add(south);
		hexCake.add(southWest);
		hexCake.add(northWest);
		return hexCake;
	}
	
	@SuppressWarnings("unused")
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
					//hexMatrix[row][col].setColor(Color.blue);
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
	
	public double[][] nearestAiDistances(Ai activeAi, Hex[] hexes) {     //will add 0 ally distance if only 1 is left on team
		double[][] distances = new double[4][6];
		int activeX = activeAi.getPosition().getX();
		int activeY = activeAi.getPosition().getY();
		
		for (int h = 0; h < 6; h++) {
			double team1Distance = boardDiagonal;
			double team2Distance = boardDiagonal;
			double averageTeam1Distance = 0.0;
			double averageTeam2Distance = 0.0;
			
			if(hexes[h] != null){
				boolean isMyself;
				for (Ai team1: team1Alive) {
					int x = team1.getPosition().getX();
					int y = team1.getPosition().getY();
					isMyself = (x == activeX && y == activeY);
					//Hex targetHex = hexMatrix[x][y];
					//double aiDistance = hexes[h].physicalDistance(targetHex);
					double aiDistance = hexes[h].getPosition().distance(team1.getPosition());
					if(aiDistance < team1Distance && !isMyself) { team1Distance = aiDistance; }
					if(!isMyself) { averageTeam1Distance += aiDistance; }
				}
				for (Ai team2: team2Alive) {
					int x = team2.getPosition().getX();
					int y = team2.getPosition().getY();
					isMyself = (x == activeX && y == activeY);
					//Hex targetHex = hexMatrix[x][y];
					//double aiDistance = hexes[h].physicalDistance(targetHex);
					double aiDistance = hexes[h].getPosition().distance(team2.getPosition());
					if(aiDistance < team2Distance && !isMyself) { team2Distance = aiDistance; }
					if(!isMyself) { averageTeam2Distance += aiDistance; }
				}
			}
			
			if(activeAi.getTeam() == 1) {
				distances[0][h] = team2Distance; //enemies
				distances[1][h] = team1Distance; //allies
			}
			else {
				distances[0][h] = team1Distance; //enemies
				distances[1][h] = team2Distance; //allies
				}
			
			if (activeAi.getTeam() == 1) {
				if (team1Alive.size() > 1) {
					averageTeam1Distance = averageTeam1Distance	/ (team1Alive.size() - 1);
					distances[3][h] = averageTeam1Distance; // average to allies
				} else {
					distances[3][h] = averageTeam1Distance;
				}

				if (team2Alive.size() >= 1) {
					averageTeam2Distance = averageTeam2Distance	/ (team2Alive.size());
					distances[2][h] = averageTeam2Distance; // average to
															// enemies
				} else {
					distances[2][h] = averageTeam2Distance;
				}
			} else {
				if (team2Alive.size() > 1) {
					averageTeam2Distance = averageTeam2Distance	/ (team2Alive.size() - 1);
					distances[3][h] = averageTeam2Distance; // average to allies
				} else {
					distances[3][h] = averageTeam2Distance;
				}

				if (team1Alive.size() >= 1) {
					averageTeam1Distance = averageTeam1Distance	/ (team1Alive.size());
					distances[2][h] = averageTeam1Distance; // average to
															// enemies
				} else {
					distances[2][h] = averageTeam1Distance;
				}
			}
			//System.out.println(activeAi.getId() + " T1A: " + Controller.round(averageTeam1Distance , 1)+ " T2A: " + Controller.round(averageTeam2Distance, 1));
		}
		
		//System.out.println(activeAi.getId() + " tm1A: " + Controller.round(averageTeam1Distance, 2) + ", tm2A: " + Controller.round(averageTeam2Distance, 2));
		return distances;
		
	}
	
	public double[][] adjacentHexAis(Hex[] hexes, int team) {
		double[][] adjacentAiCount = new double[2][6];
		int position = 0;
		
		for (Hex hex : hexes) {
			
			double enemies = 0;
			double allies = -1;      //it will find itself
			if(hex != null) {
				Hex[] adjHexes = adjacentHexes(hex.getPosition());
				for (Hex adjHex : adjHexes) {
					if(adjHex != null && adjHex.isOccupied()) {
						if(adjHex.getAi().getTeam() == team) {	allies++; }
						else { enemies++; }
					}
					
				}
			}			
			
			adjacentAiCount[0][position] = enemies;
			adjacentAiCount[1][position++] = allies;
			
		}
		
		return adjacentAiCount;
	}
	
	public double[] adjacentLocalAis(Ai activeAi, Hex hex) {
		double[] adjacentAiCount = new double[2];
			
			double enemies = 0;
			double allies = 0;
				Hex[] adjHexes = adjacentHexes(hex.getPosition());
				for (Hex adjHex : adjHexes) {
					if(adjHex != null && adjHex.isOccupied()) {
						if(adjHex.getAi().getTeam() == activeAi.getTeam()) {	allies++; }
						else { enemies++; }
					}
					
				}			
			
			adjacentAiCount[0] = enemies;
			adjacentAiCount[1] = allies;
		
		return adjacentAiCount;
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
	
	public void reset() {
		for(Ai ai : team1Alive) {
			int x = ai.getPosition().getX();
			int y = ai.getPosition().getY();
			hexMatrix[x][y].removeAi();
		}
		for(Ai ai : team2Alive) {
			int x = ai.getPosition().getX();
			int y = ai.getPosition().getY();
			hexMatrix[x][y].removeAi();
		}
		team1Alive.clear();
		team1Dead.clear();
		team2Alive.clear();
		team2Dead.clear();
	}
	
	public void parseAction(Action preferredAction, Ai ai, Hex orgHex) {
		Coordinate newPos = preferredAction.getPosition();
		Hex newHex = hexMatrix[newPos.getX()][newPos.getY()];		
		String baseType = preferredAction.getBaseType();
		String extendedType = preferredAction.getExtendedType();
		Ai targetAi = newHex.getAi();
		
		if(baseType.equals("move")) {
			moveAi(ai, orgHex, newHex); } else {
		if(baseType.equals("attack")) {
			if(extendedType.equals("stun")) {
				targetAi.setStunned(true); } else {
			if(extendedType.equals("area")) {
				if(allowAreaDamageOutput) {System.out.println(targetAi.getId() + ":  lost " + ai.getAreaDamage() + " hp to area damage, hp = " + (targetAi.getHp() - ai.getAreaDamage() + " (t)"));}
				doDamage(targetAi, ai.getAreaDamage(), newHex);
				double enemyTeam = targetAi.getTeam();
				Hex[] adjacentHexes = adjacentHexes(preferredAction.getPosition());
				for(Hex hex : adjacentHexes) {
					if(hex != null) {
						if(hex.isOccupied()) {
							Ai adjacentAi = hex.getAi();
							if(adjacentAi.getTeam() == enemyTeam) {
								double newHp = adjacentAi.getHp() - ai.getAreaDamage();
								if(allowAreaDamageOutput) {System.out.println(adjacentAi.getId() + ":  lost " + ai.getAreaDamage() + " hp to area damage, hp = " + newHp);}
								doDamage(adjacentAi, ai.getAreaDamage(), hex);
							}
						}
					}					
				}
			} else {
			if(extendedType.equals("normal")) {
				if(targetAi.getShielded()) {
					targetAi.setShielded(false);
				}
				else {
					double damage = ai.getMeleeDamage();
					if(allowNormalDamageOutput) {System.out.println(targetAi.getId() + ":  took " + damage + " damage, hp = " + (targetAi.getHp() - damage));}
					doDamage(targetAi, damage, newHex);				
				}
			}
			else {
				System.out.println("Unknown attack type"); }}}} else {
		if(baseType.equals("support")) {
			if(extendedType.equals("shield")) {
				targetAi.setShielded(true);	} else {
			if(extendedType.equals("heal")) {
				double currentHp = targetAi.getHp();
				double initialHp = targetAi.getInitialHp();
				if(currentHp < initialHp) {
					double healAmount = min(currentHp + ai.getHealAmount(), initialHp) - currentHp;
					if(allowHealOutput) {System.out.println(targetAi.getId() + ":  healed " + healAmount + ", hp = " + (currentHp + healAmount));}
					targetAi.setHp(currentHp + healAmount);
				}} else {
			if(extendedType.equals("boost")) {
				targetAi.setBoosted(true);
			} 
			else { System.out.println("Unknown support type");}}}
		}
		else {
			System.out.println("Unknown action type: " + baseType + ", " + extendedType); }}}
		}
	
	public double min(double a, double b) {
		if(a <= b) {
			return a;
		}
		else{
			return b;
		}
	}
	
	public void doDamage(Ai targetAi, double damage, Hex targetAiHex) {
		targetAi.setHp(targetAi.getHp() - damage);
		if (targetAi.isAlive() == false) {
			killAi(targetAiHex);
			//System.out.println("Killed " + targetAi.getId() + " team1Size: " + team1Alive.size() + ", team2Size: " + team2Alive.size());	
		}
	}
}
