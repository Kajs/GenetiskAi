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
	private Coordinate[][] geneticPositions;
	private Coordinate[][] staticPositions;
	private final GeneticAlgorithm geneticAlgorithm;
	private double[] team1Fitness;
	private boolean fitnessOutput;
	private int choices;
	private int information;
	//private int testDifficulty = 2;
	
	private double totalFitness;
	private double bestFitness;
	private int bestTeam;
	private double tm1AvrFit;
	private double tm2AvrFit;
	
	private Scenario[] scenarios;
	
	public GameThread(GameState gameState, int firstTeam, int lastTeam, int enemyDifficulty, int maxRounds, GeneticAlgorithm geneticAlgorithm, int choices, int information, boolean fitnessOutput, Scenario[] scenarios) {
		this.gameState = gameState;
		this.firstTeam = firstTeam;
		this.lastTeam = lastTeam;
		this.enemyDifficulty = enemyDifficulty;
		this.maxRounds = maxRounds;
		this.geneticAlgorithm = geneticAlgorithm;
		this.fitnessOutput = fitnessOutput;
		this.choices = choices;
		this.information = information;
		
		this.scenarios = scenarios;
	}
	
	public void run() {
		totalFitness = 0;
		bestFitness = (int)Math.pow(-2, 31);
		bestTeam = 0;
		tm1AvrFit = 0;
		tm2AvrFit = 0;
		
		
		for (int team = firstTeam; team < lastTeam; team++) {
			//System.out.println("Game " + i + ", team number " + lastAi + "_____________________________");
			double tm1FitVal = 0;
			double tm2FitVal = 0;
			
			
			for (int i = 0; i < scenarios.length; i++) {
				gameState.reset();
				this.geneticPositions = scenarios[i].geneticPositions;
				this.staticPositions = scenarios[i].staticPositions;
				currentTeam = new double[geneticPositions.length][3][choices+1][information];
				
				for (int teamPos = 0; teamPos < geneticPositions.length; teamPos++) {
					currentTeam[teamPos] = team1[team];
				}
				
				insertGeneticAis(currentTeam, geneticPositions);	
				//insertStaticAis(enemyDifficulty, geneticPositions, 1);
			    insertStaticAis(enemyDifficulty, staticPositions, 2);
			    
			    if(fitnessOutput) {System.out.println("\nTeam " + (team + 1) + " with fitness " + round(team1Fitness[team], 2) + " in scenario " + (i + 1) + "\n");}
			    
			    double[][] results = gameState.newGame(maxRounds);
			    
			    tm1FitVal += geneticAlgorithm.fitness(results[0]);
				tm2FitVal += geneticAlgorithm.fitness(results[1]);
			}
			
			tm1FitVal = tm1FitVal/scenarios.length;
			tm2FitVal = tm2FitVal/scenarios.length;
			
			totalFitness = totalFitness + tm1FitVal;
			
			if(tm1FitVal >= bestFitness) {
				bestFitness = tm1FitVal;
				bestTeam = team;
			}
			
			tm1AvrFit = tm1AvrFit + tm1FitVal;
			tm2AvrFit = tm2AvrFit + tm2FitVal;
			
			team1Fitness[team] = tm1FitVal;
		}
	}
	
	public void setTeam1(double[][][][] team1) {this.team1 = team1; }
	public void setTeam1Fitness(double[] team1Fitness) { this.team1Fitness = team1Fitness; }
	
	public double getTotalFitness() { return totalFitness; }
	public double getBestFitness() { return bestFitness; }
	public int getBestTeam() { return bestTeam; }
	public double getTeam1AverageFitness() { return tm1AvrFit; }
	public double getTeam2AverageFitness() { return tm2AvrFit; }
	
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
}
