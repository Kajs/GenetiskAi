package model;

import java.awt.Color;

import control.Launcher;

public class GameThread implements Runnable {
	private final GameState gameState;
	private double[][][][] team1;
	private double[][][] currentTeam;
	private final int firstTeam;
	private final int lastTeam;
	private int enemyDifficulty;
	private final int maxRounds;
	private boolean hasPrintedCurrentGame = false;
	private GeneticAlgorithm geneticAlgorithm;
	private Coordinate[][] geneticPositions;
	private Coordinate[][] staticPositions;
	private double[] team1Fitness;
	//private boolean bothTeamsStart;
	private boolean alsoReversedPositions;
	
	private boolean switchStartTeam = true;  //to clarify runGame parameters
	private boolean reversePositions = true; //to clarify runGame parameters
	
	private boolean testingStatics;
	private int testStaticDifficulty;
	private boolean allDifficulties;
	
	private int bestTeam;
	private double[] bestFitness = new double[3];
	private double[] vsBestFitness = new double[3];
	private double[] tm1ScenarioAvrFitness = new double[3];
	private double[] tm2ScenarioAvrFitness = new double[3];
	private double[] tm1GameAvrFitness = new double[3];
	private double[] tm2GameAvrFitness = new double[3];
	private int scenarioCounter;
	
	private Scenario[] scenarios;
	
	public GameThread(GameState gameState, int firstTeam, int lastTeam, int enemyDifficulty, int maxRounds, int choices, int information, Scenario[] scenarios, boolean alsoReversedPositions, boolean bothTeamsStart, boolean testingStatics, int testStaticDifficulty, GeneticAlgorithm geneticAlgorithm, boolean allDifficulties) {
		this.gameState = gameState;
		this.firstTeam = firstTeam;
		this.lastTeam = lastTeam;
		this.enemyDifficulty = enemyDifficulty;
		this.maxRounds = maxRounds;
		//this.bothTeamsStart = bothTeamsStart;
		this.alsoReversedPositions = alsoReversedPositions;
		this.testingStatics = testingStatics;
		this.testStaticDifficulty = testStaticDifficulty;
		this.allDifficulties = allDifficulties;
		
		this.scenarios = scenarios;
		this.geneticAlgorithm = geneticAlgorithm;
		
		currentTeam = new double[3][choices+1][information];
	}
	
	public void run() {
		fillArray(bestFitness, (int)Math.pow(-2, 31));
		fillArray(vsBestFitness, (int)Math.pow(-2, 31));
		fillArray(tm1GameAvrFitness, 0);
		fillArray(tm2GameAvrFitness, 0);
		bestTeam = 0;
		
		for (int team = firstTeam; team < lastTeam; team++) {
			
			if(Launcher.switchBestTeam) { 
				int newTeam = Launcher.switchBestTeamNumber;
				if(newTeam < firstTeam) { newTeam = firstTeam; }
				if(newTeam > lastTeam) { newTeam = lastTeam; }
				team = newTeam;
				Launcher.switchBestTeam = false;
			}
			
			//System.out.println("Game " + i + ", team number " + lastAi + "_____________________________");
			fillArray(tm1ScenarioAvrFitness, 0);
			fillArray(tm2ScenarioAvrFitness, 0);
			
			scenarioCounter = 0;
			
			for (int i = 0; i < scenarios.length; i++) {
				if(scenarios[i] == null) { continue; }
				geneticPositions = scenarios[i].geneticPositions;
				staticPositions = scenarios[i].staticPositions;				
				currentTeam = team1[team];
				
				if(Launcher.allowBestTeamsFitnessOutput) {System.out.println("\nTeam " + (team + 1) + " with fitness " + round(team1Fitness[team], 3) + " in scenario " + (i + 1) + "\n");}
				
				if(!allDifficulties) { runAllGames(enemyDifficulty); }
				else { runAllGames(0); runAllGames(1); runAllGames(2); }
			    
			} //not reversed team 2 starts
				
				if (allDifficulties) {scenarioCounter = scenarioCounter / 3; }
				for (int s = 0; s < 3; s++) { 
					tm1ScenarioAvrFitness[s] = tm1ScenarioAvrFitness[s] / scenarioCounter;
					tm2ScenarioAvrFitness[s] = tm2ScenarioAvrFitness[s] / scenarioCounter;
					tm1GameAvrFitness[s] += tm1ScenarioAvrFitness[s];
					tm2GameAvrFitness[s] += tm2ScenarioAvrFitness[s];
				}
				
				if(averageFitness(tm1ScenarioAvrFitness) > averageFitness(bestFitness) || averageFitness(tm1ScenarioAvrFitness) == averageFitness(bestFitness) && averageFitness(tm2ScenarioAvrFitness) < averageFitness(vsBestFitness)) {
					for (int s = 0; s < 3; s++) { 
						bestFitness[s] = tm1ScenarioAvrFitness[s];
						vsBestFitness[s] = tm2ScenarioAvrFitness[s];
					}
					bestTeam = team;
				}
				
				team1Fitness[team] = averageFitness(tm1ScenarioAvrFitness);
				
				if (Launcher.testPrintCurrentGame > 0 && !hasPrintedCurrentGame) { Launcher.testPrintCurrentGame--; System.out.println(Thread.currentThread().getName() + " has completed game " + team); hasPrintedCurrentGame = true;}
				if (Launcher.testPrintCurrentGame == 0) { hasPrintedCurrentGame = false; }
		}
		
		for (int s = 0; s < 3; s++) { 
			tm1GameAvrFitness[s] = tm1GameAvrFitness[s] / (lastTeam - firstTeam);
			tm2GameAvrFitness[s] = tm2GameAvrFitness[s] / (lastTeam - firstTeam);
		}
		
		//System.out.println("totalFit " + totalFitness + ", tm1Avr " + tm1GameAvrFit + ", tm2Avr " + tm2GameAvrFit);
    }
	
	public void setTeam1(double[][][][] team1) {this.team1 = team1; }
	public void setTeam1Fitness(double[] team1Fitness) { this.team1Fitness = team1Fitness; }
	
	public double[] getBestFitness() { return bestFitness; }
	public int getBestTeam() { return bestTeam; }
	public double[] getTeam1AverageFitness() { return tm1GameAvrFitness; }
	public double[] getTeam2AverageFitness() { return tm2GameAvrFitness; }
	public double[] getVsBestFitness() { return vsBestFitness; }
	
	private void insertGeneticAis(double[][][] geneticAis, Coordinate[][] geneticPositions, int team) {
		for (int teamPos = 0; teamPos < geneticPositions.length; teamPos++) {
			for (int aiType = 0; aiType < 3; aiType++) {
				if (geneticPositions[teamPos][aiType] != null) { 
					gameState.insertAi(newGeneticAi(geneticAis[aiType], aiType), team, geneticColors(aiType), geneticPositions[teamPos][aiType]); 
				}
			}
		}
	}	
	
	private void insertStaticAis(int difficulty, Coordinate[][] staticPositions, int team) {
		for (int teamPos = 0; teamPos < staticPositions.length; teamPos++) {
			for (int aiType = 0; aiType < 3; aiType++) {
				if (staticPositions[teamPos][aiType] != null) {	
					gameState.insertAi(newStaticAi(difficulty, aiType), team, staticColors(aiType, difficulty), staticPositions[teamPos][aiType]); 
				}
			}
		}
	}
	
	public void runAllGames(int enemyDifficulty) {
		/*
		runGame(!reversePositions, !switchStartTeam, enemyDifficulty); // not reversed, team 1 starts
	    if(bothTeamsStart) { runGame(!reversePositions, switchStartTeam, enemyDifficulty); } 
	    if(alsoReversedPositions) {
			runGame(reversePositions, !switchStartTeam, enemyDifficulty); //reversed, team 1 starts				    
		    if(bothTeamsStart) { runGame(reversePositions, switchStartTeam, enemyDifficulty); }
		}
		*/
		
		runGame(!reversePositions, !switchStartTeam, enemyDifficulty); // not reversed, team 1 starts
		if(alsoReversedPositions) { runGame(reversePositions, switchStartTeam, enemyDifficulty); } // reversed, team 2 starts
	}
	
	private Ai newGeneticAi(double[][] weights, int type) {
		switch(type) {
		case 0:	return new Warrior(weights);
		case 1: return new Wizard(weights);
		case 2: return new Cleric(weights);
		}
		return null;
	}
	
	private Ai newStaticAi(int difficulty, int type) {
		switch(difficulty) {
		case 0:    //base
			switch(type) {
			case 0:
				return new BaseWarrior();
			case 1:
				return new BaseWizard();
			case 2:
				return new BaseCleric();
			}
		case 1:
			switch(type) {
			case 0:
				return new MediumWarrior();
			case 1:
				return new MediumWizard();
			case 2:
				return new MediumCleric();
			}
		case 2:
			switch(type) {
			case 0:
				return new HardWarrior();
			case 1:
				return new HardWizard();
			case 2:
				return new HardCleric();
			}
		}
		System.out.println("newEnemy did not return correctly");
		return null;
	}
	
	private Color geneticColors(int aiType) {
		switch(aiType) {
		case 0:
			return Color.black;
		case 1:
			return Color.blue;
		case 2:
			return Color.cyan;
		}
		return null;
	}
	
	private Color staticColors(int aiType, int difficulty) {
		switch(difficulty) {
		case 0:
			switch(aiType) {
			case 0:
				return Color.LIGHT_GRAY;
			case 1:
				return Color.MAGENTA;
			case 2:
				return Color.PINK;
			}
		case 1:
			switch(aiType) {
			case 0:
				return Color.GRAY;
			case 1:
				return Color.RED;
			case 2:
				return Color.GREEN;
			}
		case 2:
			switch(aiType) {
			case 0:
				return Color.DARK_GRAY;
			case 1:
				return Color.ORANGE;
			case 2:
				return Color.YELLOW;
			}
		default:
			return null;
		}
		
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	public void runGame(boolean reversed, boolean switchStartTeam, int enemyDifficulty) {
		if(Launcher.allowBestTeamsFitnessOutput) {
			String gameDescription = "    ";
			if(!reversed) { gameDescription += "normal game"; }
			else { gameDescription += "reversed game"; }
			if(!switchStartTeam) { gameDescription += ", team 1 starts"; }
			else { gameDescription += ", team 2 starts"; }

			if(enemyDifficulty == 0) { gameDescription += ", basic"; }
			if(enemyDifficulty == 1) { gameDescription += ", medium"; }
			if(enemyDifficulty == 2) { gameDescription += ", hard"; }
			
			
			System.out.println(gameDescription);
		}
		
		gameState.reset();
		scenarioCounter += 1;
		
		//insert genetic or static ais as team 1. Normal is geneticPositions, reversed is staticPositions
		if(testingStatics) {
			if(reversed) { insertStaticAis(testStaticDifficulty, staticPositions, 1); }
			else { insertStaticAis(testStaticDifficulty, geneticPositions, 1); }
		}
		else {
			if(reversed) { insertGeneticAis(currentTeam, staticPositions, 1); }
			else { insertGeneticAis(currentTeam, geneticPositions, 1); }
			 
		}
		
		//insert opponent static ais as team 2. Normal is staticPositions, reversed is geneticPositions
		
		if(reversed) { insertStaticAis(enemyDifficulty, geneticPositions, 2); }
		else { insertStaticAis(enemyDifficulty, staticPositions, 2); }
		
		double[][] results = gameState.newGame(maxRounds, switchStartTeam);
	    
	    tm1ScenarioAvrFitness[enemyDifficulty] += geneticAlgorithm.fitness(results[0]);
	    tm2ScenarioAvrFitness[enemyDifficulty] += geneticAlgorithm.fitness(results[1]);
	}
	
	private double averageFitness(double[] arr) {
		int l = arr.length;
		double total = 0;
		
		for (int i = 0; i < l; i++) { 
			if(allDifficulties || !allDifficulties && i == enemyDifficulty) { total += arr[i]; } 
		}
		if(allDifficulties) { return total / 3; }
		else { return total; }
	}
	
	private void fillArray(double[] arr, int val) { for (int i = 0; i < arr.length; i++) { arr[i] = val; }}
}
