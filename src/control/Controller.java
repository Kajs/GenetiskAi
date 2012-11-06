package control;

import java.awt.Color;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import model.*;
import view.BoardRenderer;
import view.WindowManager;

public class Controller {
	
	static int width = 800;
	static int height = 600;
	static int rows = 13;
	static int columns = 23;
	static double hexSideSize = scaledHexSideSize();
	public static int roundDelay = 1000;  // in milliseconds
	static Coordinate startPosition = new Coordinate(sin(toRadians(30)) * hexSideSize, 1);
	
	static int maxRounds = 70;
	static int maxGames = 100;
	public static int gamesCompleted = 0;
	
	static int populationSize = 1000;
	static int choices = 6;
	static int information = 25;
	static double keepPercent = 0.25;
	static double crossPercent = 0.25;
	static double drasticLikelihood = 0.0;
	static double mutateLikelihood = 1.0;
	public boolean elitism = false;
	public boolean skipZeroFitnessScaling = false;
	public boolean alwaysKeepBest = true;
	
	
	static Coordinate[][] geneticPositions;
	static Coordinate[][] staticPositions = new Coordinate[2][3];
	static int enemyDifficulty = 0;
	
	public static final GameState gameState = new GameState(startPosition, rows, columns, hexSideSize);
	public BoardRenderer boardRenderer;
	public WindowManager window;
	public GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
	public static double[][][][] bestTeams;
	public static boolean runBestTeamGames = false;
	public static double[] bestTeamsFitness = new double[maxGames];
	
	public Controller(boolean automatic, boolean displayAutomatic) {
		setTeamPositions(false);
		Launcher.allowActionOutput = false;
    	
    	if(displayAutomatic) {
    		gameState.reset();
    		boardRenderer = new BoardRenderer(rows, columns, gameState.getHexMatrix());
    		boardRenderer.setBackground(Color.white);
    		gameState.addObserver(boardRenderer);
    		window = new WindowManager(width, height, boardRenderer, this);
    		bestTeams = evolve(maxRounds, maxGames, populationSize, displayAutomatic);
    		
    		Launcher.stop = false;
    		while(!Launcher.stop) {
    			if(runBestTeamGames) {
    				runBestTeamGames();
    				runBestTeamGames = false;
    			}
    			try { Thread.sleep(1000); }
        		catch(InterruptedException ex) { Thread.currentThread().interrupt(); }
    		}
    	}
		else {
			Launcher.allowRoundDelay = false;
			bestTeams = evolve(maxRounds, maxGames, populationSize, displayAutomatic);
		}        	
	}
	
	
	
	
	
	//------------------------- run genetic algorithm
	
	
	
	
	
	public double[][][][] evolve(int maxRounds, int maxGames, int populationSize, boolean displayAutomatic) {
		double[][][][] team1 = new double[3][populationSize][choices+1][information];
		team1[0] = geneticAlgorithm.initialPopulation(populationSize, choices, information);   // warriors
		team1[1] = geneticAlgorithm.initialPopulation(populationSize, choices, information);   // wizards
		team1[2] = geneticAlgorithm.initialPopulation(populationSize, choices, information);   // clerics 
		double[][][][] bestTeams = new double[3][maxGames][choices+1][information];
		
		double tm1FinalAvrFit = 0;
		double tm2FinalAvrFit = 0;
		int lastGame = 0;
		
		for (int game = 0; game < maxGames && !Launcher.stop; game++) {
			lastGame = game;
			double tm1AvrFit = 0;
			double tm2AvrFit = 0;
			double[] team1Fitness = new double[populationSize];
			double totalFitness = 0;
			double bestFitness = (int)Math.pow(-2, 31);
			int bestTeam = 0;
			
			
			for (int team = 0; team < populationSize; team++) {
				//System.out.println("Game " + i + ", team number " + lastAi + "_____________________________");
				
				gameState.reset();
				
				double[][][][] currentTeam = new double[3][1][choices+1][information];
				currentTeam[0][0] = team1[0][team];
				currentTeam[1][0] = team1[1][team];
				currentTeam[2][0] = team1[2][team];
				
				insertGeneticAis(currentTeam, geneticPositions);			    
			    insertStaticAis(enemyDifficulty, staticPositions);

				double[][] results = gameState.newGame(maxRounds);
				
				double tm1FitVal = geneticAlgorithm.fitness(results[0]);
				double tm2FitVal = geneticAlgorithm.fitness(results[1]);
				totalFitness = totalFitness + tm1FitVal;
				
				if(tm1FitVal > bestFitness) {
					bestFitness = tm1FitVal;
					bestTeam = team;
				}
				
				tm1AvrFit = tm1AvrFit + tm1FitVal;
				tm2AvrFit = tm2AvrFit + tm2FitVal;
				
				team1Fitness[team] = tm1FitVal;
				gamesCompleted = game + 1;
				//System.out.println("Game " + i + ": team1 fitness = " + team1Fitness + ", team2Fitness = " + team2Fitness);
			}
			
			bestTeams[0][game] = team1[0][bestTeam];
			bestTeams[1][game] = team1[1][bestTeam];
			bestTeams[2][game] = team1[2][bestTeam];
			
			bestTeamsFitness[game] = bestFitness;
			
			//System.out.println("bestFitness: " + bestFitness + ", team number: " + bestTeam + ", stored Fitness: " + team1Fitness.get(bestTeam));
			
			tm1AvrFit = tm1AvrFit / (double) populationSize;
			tm2AvrFit = tm2AvrFit / (double) populationSize;
			tm1FinalAvrFit = tm1FinalAvrFit + tm1AvrFit;
			tm2FinalAvrFit = tm2FinalAvrFit + tm2AvrFit;
			
			System.out.println("Game " + (game + 1) + " bestFit: " + round(bestFitness, 2) + ", tm1AvrFit = " + round(tm1AvrFit, 2)  + ", tm2AvrFit = " + round(tm2AvrFit, 2));
			
			team1[0] = geneticAlgorithm.newPopulation(team1[0], copyArray(team1Fitness), keepPercent, crossPercent, drasticLikelihood, mutateLikelihood, elitism, bestTeam, totalFitness, skipZeroFitnessScaling, alwaysKeepBest);  //copying team1Fitness in case Genetic Algorithm uses heapsort
			team1[1] = geneticAlgorithm.newPopulation(team1[1], copyArray(team1Fitness), keepPercent, crossPercent, drasticLikelihood, mutateLikelihood, elitism, bestTeam, totalFitness, skipZeroFitnessScaling, alwaysKeepBest);
			team1[2] = geneticAlgorithm.newPopulation(team1[2], team1Fitness, keepPercent, crossPercent, drasticLikelihood, mutateLikelihood, elitism, bestTeam, totalFitness, skipZeroFitnessScaling, alwaysKeepBest);
		}
		tm1FinalAvrFit = tm1FinalAvrFit/(lastGame + 1);
		tm2FinalAvrFit = tm2FinalAvrFit/(lastGame + 1);
		
		System.out.println("Final average fitness team1: " + tm1FinalAvrFit + ", team2: " + tm2FinalAvrFit);
		
		return bestTeams;
	}
	
	
	
	
	
	//------------------------- check best team games after evolve() finishes
	
	
	
	
	
		public static void newBestTeamGame(int bestTeam) {
			if(Launcher.allowBestTeamsFitnessOutput) { System.out.println("Best team " + bestTeam + " with fitness " + round(bestTeamsFitness[bestTeam], 2)); }
			gameState.reset();
			double[][][][] currentBestTeam = new double[3][1][choices+1][information];
			currentBestTeam[0][0] = bestTeams[0][bestTeam];
			currentBestTeam[1][0] = bestTeams[1][bestTeam];
			currentBestTeam[2][0] = bestTeams[2][bestTeam];
			insertGeneticAis(currentBestTeam, geneticPositions);
			insertStaticAis(enemyDifficulty, staticPositions);
			}

		public static void runBestTeamGames() {
			for (int i = 0; i < gamesCompleted; i++) {
				if(Launcher.allowBestTeamsFitnessOutput) {System.out.println("\nBest team " + (i + 1) + " with fitness " + round(bestTeamsFitness[i], 2) + "\n");}
				gameState.reset();
				double[][][][] currentBestTeam = new double[3][1][choices+1][information];
				currentBestTeam[0][0] = bestTeams[0][i];
				currentBestTeam[1][0] = bestTeams[1][i];
				currentBestTeam[2][0] = bestTeams[2][i];
				insertGeneticAis(currentBestTeam, geneticPositions);
				insertStaticAis(enemyDifficulty, staticPositions);
				gameState.newGame(maxRounds);
			}
		}
	
	
	
		
		
	
	//------------------------- insert ai functions
		
		
		
		
	
	public static void insertGeneticAis(double[][][][] geneticAis, Coordinate[][] geneticPositions) {
		for (int aiType = 0; aiType < 3; aiType++) {
			for (int i = 0; i < geneticPositions[aiType].length && geneticPositions[aiType][i] != null; i++) {
				gameState.insertAi(newGeneticAi(geneticAis[aiType][i], aiType), 1, geneticColors(aiType), geneticPositions[aiType][i]);
			}
		}
	}
	
	public static void insertStaticAis(int difficulty, Coordinate[][] staticPositions) {
		for (int aiType = 0; aiType < 3; aiType++) {
			for (int i = 0; i < staticPositions[aiType].length && staticPositions[aiType][i] != null; i++) {
				gameState.insertAi(newStaticAi(difficulty, aiType), 2, staticColors(aiType), staticPositions[aiType][i]);
			}
		}
	}
	
	public static Ai newGeneticAi(double[][] weights, int type) {
		switch(type) {
		case 0:	return new Warrior(weights);
		case 1: return new Wizard(weights);
		case 2: return new Cleric(weights);
		}
		return null;
	}
	
	public static Ai newStaticAi(int difficulty, int type) {
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
				//return new HardWarrior();  to be implemented
			case 1:
				//return new HardWizard();   to be implemented
			case 2:
				//return new HardCleric();   to be implemented
			}
		}
		System.out.println("newEnemy did not return correctly");
		return null;
	}
	
	static Color geneticColors(int aiType) {
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
	
	static Color staticColors(int aiType) {
		switch(aiType) {
		case 0:
			return Color.BLACK;
		case 1:
			return Color.GRAY;
		case 2:
			return Color.LIGHT_GRAY;
		}
		return null;
	}
	
	
	
	
	
	
	//------------------------- team positions
	
	
	
		public void setTeamPositions(boolean random) {
			if(random) {
				
			}
			else {
				geneticPositions = new Coordinate[3][1];            //format: [aiType][aiNumber]
				geneticPositions[0][0] = new Coordinate(5, 5);
				geneticPositions[1][0] = new Coordinate(2, 19);
				geneticPositions[2][0] = new Coordinate(4, 1);
				/*
				geneticPositions[1][0] = new Coordinate(12, 0);
				geneticPositions[1][1] = new Coordinate(10, 14);
				geneticPositions[1][2] = new Coordinate(4, 4);
				*/
				staticPositions = new Coordinate[3][3];
				staticPositions[0][0] = new Coordinate(0, 21);
				staticPositions[1][0] = new Coordinate(1, 22);
				staticPositions[2][0] = new Coordinate(0, 22);
				//staticPositions[1][0] = new Coordinate(12, 0);
				//staticPositions[1][1] = new Coordinate(0, 4);
				//staticPositions[1][2] = new Coordinate(0, 5);
				//staticPositions[2][0] = new Coordinate(12, 18);
			}
		}
	
	
	
	
	
	
	//------------------------- misc functions
	
	
	
	
	public static double scaledHexSideSize() {
		double maxHexHeight = (double)height/((double)rows * cos(toRadians(30)) * (2.0 + 1.0/(double)rows)) * 0.8941;
		double maxHexWidth = (double)width/((sin(toRadians(30)) + (double)columns * (1.0 + sin(toRadians(30))))) * 0.98;
		if(maxHexHeight <= maxHexWidth) {
			return maxHexHeight;
		}
		else{
			return maxHexWidth;
		}
	}
	
	public double[] copyArray(double[] orgArray) {
		int length = orgArray.length;
		double[] copy = new double[length];
		for (int i = 0; i < length; i++) {
			copy[i] = orgArray[i];
		}
		return copy;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	public double[][][][] getBestTeams() {
		return bestTeams;
	}
}
