package model;

import java.awt.Color;

public class GameThread implements Runnable {
	private final GameState gameState;
	private double[][][][] team1;
	private double[][][][] currentTeam;
	private final int firstTeam;
	private final int lastTeam;
	private int enemyDifficulty;
	private final int maxRounds;
	private GeneticAlgorithm geneticAlgorithm;
	private Coordinate[][] geneticPositions;
	private Coordinate[][] staticPositions;
	private double[] team1Fitness;
	private boolean fitnessOutput;
	private int choices;
	private int information;
	private boolean bothTeamsStart;
	private boolean alsoReversedPositions;
	
	private boolean switchStartTeam = true;  //just used to clarify runGame parameters
	private boolean reversePositions = true; //just used to clarify runGame parameters
	
	private boolean testingStatics;
	private int testStaticDifficulty;
	
	private double totalFitness;
	private double bestFitness;
	private int bestTeam;
	private double tm1GameAvrFit;
	private double tm2GameAvrFit;
	private double tm1ScenarioSummedFit;
	private double tm2ScenarioSummedFit;
	private double fitScale;
	
	private Scenario[] scenarios;
	
	public GameThread(GameState gameState, int firstTeam, int lastTeam, int enemyDifficulty, int maxRounds, int choices, int information, boolean fitnessOutput, Scenario[] scenarios, boolean alsoReversedPositions, boolean bothTeamsStart, boolean testingStatics, int testStaticDifficulty, GeneticAlgorithm geneticAlgorithm) {
		this.gameState = gameState;
		this.firstTeam = firstTeam;
		this.lastTeam = lastTeam;
		this.enemyDifficulty = enemyDifficulty;
		this.maxRounds = maxRounds;
		this.fitnessOutput = fitnessOutput;
		this.choices = choices;
		this.information = information;
		this.bothTeamsStart = bothTeamsStart;
		this.alsoReversedPositions = alsoReversedPositions;
		this.testingStatics = testingStatics;
		this.testStaticDifficulty = testStaticDifficulty;
		
		this.scenarios = scenarios;
		this.geneticAlgorithm = geneticAlgorithm;
	}
	
	public void run() {
		bestFitness = (int)Math.pow(-2, 31);
		bestTeam = 0;
		double tm1GameSummedFitness = 0;
		double tm2GameSummedFitness = 0;
		
		for (int team = firstTeam; team < lastTeam; team++) {
			//System.out.println("Game " + i + ", team number " + lastAi + "_____________________________");
			tm1ScenarioSummedFit = 0;
			tm2ScenarioSummedFit = 0;
			fitScale = 0;			
			
			for (int i = 0; i < scenarios.length; i++) {
				if(scenarios[i] == null) { continue; }
				this.geneticPositions = scenarios[i].geneticPositions;
				this.staticPositions = scenarios[i].staticPositions;
				
				currentTeam = new double[geneticPositions.length][3][choices+1][information];
				
				for (int teamPos = 0; teamPos < geneticPositions.length; teamPos++) {
					currentTeam[teamPos] = team1[team];
				}
				
				if(fitnessOutput) {System.out.println("\nTeam " + (team + 1) + " with fitness " + round(team1Fitness[team], 2) + " in scenario " + (i + 1) + "\n");}
				
				runGame(!reversePositions, !switchStartTeam); // not reversed, team 1 starts
			    if(bothTeamsStart) { runGame(!reversePositions, switchStartTeam); } 
			    if(alsoReversedPositions) {
					runGame(reversePositions, !switchStartTeam); //reversed, team 1 starts				    
				    if(bothTeamsStart) { runGame(reversePositions, switchStartTeam); }
				}
			    
			} //not reversed team 2 starts
				
				tm1GameSummedFitness = tm1GameSummedFitness + tm1ScenarioSummedFit/fitScale;
				tm2GameSummedFitness = tm2GameSummedFitness + tm2ScenarioSummedFit/fitScale;
				
				if(tm1ScenarioSummedFit/fitScale >= bestFitness) {
					bestFitness = tm1ScenarioSummedFit/fitScale;
					bestTeam = team;
				}
				
				team1Fitness[team] = tm1ScenarioSummedFit/fitScale;
		}
		
		totalFitness = tm1GameSummedFitness;
		tm1GameAvrFit = tm1GameSummedFitness / (lastTeam - firstTeam);
		tm2GameAvrFit = tm2GameSummedFitness / (lastTeam - firstTeam);
		
		//System.out.println("totalFit " + totalFitness + ", tm1Avr " + tm1GameAvrFit + ", tm2Avr " + tm2GameAvrFit);
    }
	
	public void setTeam1(double[][][][] team1) {this.team1 = team1; }
	public void setTeam1Fitness(double[] team1Fitness) { this.team1Fitness = team1Fitness; }
	
	public double getTotalFitness() { return totalFitness; }
	public double getBestFitness() { return bestFitness; }
	public int getBestTeam() { return bestTeam; }
	public double getTeam1AverageFitness() { return tm1GameAvrFit; }
	public double getTeam2AverageFitness() { return tm2GameAvrFit; }
	
	private void insertGeneticAis(double[][][][] geneticAis, Coordinate[][] geneticPositions) {
		for (int teamPos = 0; teamPos < geneticPositions.length; teamPos++) {
			for (int aiType = 0; aiType < 3; aiType++) {
				if (geneticPositions[teamPos][aiType] != null) { gameState.insertAi(newGeneticAi(geneticAis[teamPos][aiType], aiType), 1, geneticColors(aiType), geneticPositions[teamPos][aiType]); }
			}
		}
	}
	
	private void insertStaticAis(int difficulty, Coordinate[][] staticPositions, int team) {
		for (int teamPos = 0; teamPos < staticPositions.length; teamPos++) {
			for (int aiType = 0; aiType < 3; aiType++) {
				if (staticPositions[teamPos][aiType] != null) {	gameState.insertAi(newStaticAi(difficulty, aiType), team, staticColors(aiType, difficulty), staticPositions[teamPos][aiType]); }
			}
		}
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
			return Color.red;
		case 1:
			return Color.orange;
		case 2:
			return Color.yellow;
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
				return Color.CYAN;
			case 2:
				return Color.PINK;
			}
		case 1:
			switch(aiType) {
			case 0:
				return Color.DARK_GRAY;
			case 1:
				return Color.BLUE;
			case 2:
				return Color.GREEN;
			}
		case 2:
			switch(aiType) {
			case 0:
				return Color.BLACK;
			case 1:
				return Color.MAGENTA;
			case 2:
				return Color.BLUE;
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
	
	public void runGame(boolean reversed, boolean switchStartTeam) {
		if(fitnessOutput) {
			String gameDescription = "";
			if(!reversed) { gameDescription += "normal game, "; }
			else { gameDescription += "reversed game, "; }
			if(!switchStartTeam) { gameDescription += "team 1 starts"; }
			else { gameDescription += "team 2 starts"; }
			System.out.println(gameDescription);
		}
		
		gameState.reset();
		fitScale += 1;
		
		//insert genetic or static ais as team 1. Normal is geneticPositions, reversed is staticPositions
		if(testingStatics) {
			if(reversed) { insertStaticAis(testStaticDifficulty, staticPositions, 1); }
			else { insertStaticAis(testStaticDifficulty, geneticPositions, 1); }
		}
		else {
			if(reversed) { insertGeneticAis(currentTeam, staticPositions); }
			else { insertGeneticAis(currentTeam, geneticPositions); }
			 
		}
		
		//insert opponent static ais as team 2. Normal is staticPositions, reversed is geneticPositions
		
		if(reversed) { insertStaticAis(enemyDifficulty, geneticPositions, 2); }
		else { insertStaticAis(enemyDifficulty, staticPositions, 2); }
		
		double[][] results = gameState.newGame(maxRounds, switchStartTeam);
		double tm1Result = geneticAlgorithm.fitness(results[0]);
		double tm2Result = geneticAlgorithm.fitness(results[1]);
	    
	    tm1ScenarioSummedFit += tm1Result;
	    tm2ScenarioSummedFit += tm2Result;
	}
}
