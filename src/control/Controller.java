package control;

import java.awt.Color;
import java.util.ArrayList;

import model.*;
import view.BoardRenderer;
import view.WindowManager;

public class Controller {
	
	static int width = 800;
	static int height = 600;
	static double hexSideSize = 10;
	static int rows = 40;
	static int columns = 60;
	public static int roundDelay = 1000;  // in milliseconds
	static Coordinate startPosition = new Coordinate(Math.sin(Math.toRadians(30)) * hexSideSize, 1);
	
	static int maxRounds = 100;
	static int maxGames = 200;
	static int gamesCompleted = 0;
	
	static int populationSize = 1000;
	static int choices = 6;
	static int information = 23;
	static double keepPercent = 0.25;
	static double crossPercent = 0.25;
	static double drasticLikelihood = 0.0;
	static double mutateLikelihood = 1.0;
	public boolean elitism = true;
	
	static Coordinate team1_ai1_startPos = new Coordinate(2, 1);
	static Coordinate team1_ai2_startPos = new Coordinate(1, 0);
	static Coordinate team1_ai3_startPos = new Coordinate(1, 1);
	static Coordinate team2_ai1_startPos = new Coordinate(35, 35);
	static Coordinate team2_ai2_startPos = new Coordinate(35, 36);
	static Coordinate team2_ai3_startPos = new Coordinate(35, 37);
	static int enemyDifficulty = 0;
	
	public static final GameState gameState = new GameState(startPosition, rows, columns, hexSideSize);
	public BoardRenderer boardRenderer;
	public WindowManager window;
	public GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
	public static double[][][][] bestTeams;
	public static boolean runBestTeamGames = false;
	public static ArrayList<Double> bestTeamsFitness = new ArrayList<Double>();
	
	public Controller(boolean automatic, boolean displayAutomatic) {
		//gameState = new GameState(startPosition, rows, columns, hexSideSize);
        if(!automatic) { 
        	insertGeneticAis(geneticAlgorithm.generateWeights(choices, information), geneticAlgorithm.generateWeights(choices, information), geneticAlgorithm.generateWeights(choices, information));
    		insertStaticAis();
    		
        	boardRenderer = new BoardRenderer(rows, columns, gameState.getHexMatrix());
    		boardRenderer.setBackground(Color.white);
    		gameState.addObserver(boardRenderer);
    		
    		window = new WindowManager(width, height, boardRenderer, this);
        }
        else {
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
	}
	
public static void newBestTeamGame(int bestTeam) {
	if(Launcher.allowBestTeamsFitnessOutput) { System.out.println("Best team " + bestTeam + " with fitness " + round(bestTeamsFitness.get(bestTeam), 2)); }
	gameState.reset();
	insertGeneticAis(bestTeams[0][bestTeam], bestTeams[1][bestTeam], bestTeams[2][bestTeam]);
	insertStaticAis();
	}

public static void runBestTeamGames() {
	for (int i = 0; i < gamesCompleted; i++) {
		if(Launcher.allowBestTeamsFitnessOutput) { System.out.println("Best team " + (i + 1) + " with fitness " + round(bestTeamsFitness.get(i), 2)); }
		gameState.reset();
		insertGeneticAis(bestTeams[0][i], bestTeams[1][i], bestTeams[2][i]);
		insertStaticAis();
		gameState.newGame(maxRounds);
	}
}
	
	public static int isOccupied(Coordinate coordinate) {
		return gameState.isOccupied(coordinate);
	}
	
	public static void colorHex(Coordinate position, Color color) {
		gameState.colorHex(position, color);
	}
	
	public double[][][][] evolve(int maxRounds, int games, int populationSize, boolean displayAutomatic) {
		double[][][] team1Warriors = geneticAlgorithm.initialPopulation(populationSize, choices, information);
		double[][][] team1Wizards = geneticAlgorithm.initialPopulation(populationSize, choices, information);
		double[][][] team1Clerics = geneticAlgorithm.initialPopulation(populationSize, choices, information);
		double[][][][] bestTeams = new double[3][games][choices+1][information];
		
		double tm1FinalAvrFit = 0;
		double tm2FinalAvrFit = 0;
		int lastGame = 0;
		
		for (int i = 0; i < games && !Launcher.stop; i++) {
			lastGame = i;
			double tm1AvrFit = 0;
			double tm2AvrFit = 0;
			ArrayList<Double> team1Fitness = new ArrayList<Double>();
			double bestFitness = (int)Math.pow(-2, 31);
			int bestTeam = 0;
			
			
			for (int lastAi = 0; lastAi < populationSize; lastAi++) {
				//System.out.println("Game " + i + ", team number " + lastAi + "_____________________________");
				
				gameState.reset();
				
				insertGeneticAis(team1Warriors[lastAi], team1Wizards[lastAi], team1Clerics[lastAi]);			    
			    insertStaticAis();

				double[][] results = gameState.newGame(maxRounds);
				
				double tm1FitVal = geneticAlgorithm.fitness(results[0]);
				double tm2FitVal = geneticAlgorithm.fitness(results[1]);
				
				if(tm1FitVal > bestFitness) {
					bestFitness = tm1FitVal;
					bestTeam = lastAi;
				}
				
				tm1AvrFit = tm1AvrFit + tm1FitVal;
				tm2AvrFit = tm2AvrFit + tm2FitVal;
				
				team1Fitness.add(tm1FitVal);
				gamesCompleted = i + 1;
				//System.out.println("Game " + i + ": team1 fitness = " + team1Fitness + ", team2Fitness = " + team2Fitness);
			}
			
			bestTeams[0][i] = team1Warriors[bestTeam];
			bestTeams[1][i] = team1Wizards[bestTeam];
			bestTeams[2][i] = team1Clerics[bestTeam];
			
			bestTeamsFitness.add(bestFitness);
			
			//System.out.println("bestFitness: " + bestFitness + ", team number: " + bestTeam + ", stored Fitness: " + team1Fitness.get(bestTeam));
			
			tm1AvrFit = tm1AvrFit / (double) populationSize;
			tm2AvrFit = tm2AvrFit / (double) populationSize;
			tm1FinalAvrFit = tm1FinalAvrFit + tm1AvrFit;
			tm2FinalAvrFit = tm2FinalAvrFit + tm2AvrFit;
			
			System.out.println("Game " + (i + 1) + " bestFit: " + round(bestFitness, 2) + ", tm1AvrFit = " + round(tm1AvrFit, 2)  + ", tm2AvrFit = " + round(tm2AvrFit, 2));
			
			team1Warriors = geneticAlgorithm.newPopulation(team1Warriors, copyArrayList(team1Fitness), keepPercent, crossPercent, drasticLikelihood, mutateLikelihood, elitism);  //copying team1Fitness in case Genetic Algorithm uses heapsort
			team1Wizards = geneticAlgorithm.newPopulation(team1Wizards, copyArrayList(team1Fitness), keepPercent, crossPercent, drasticLikelihood, mutateLikelihood, elitism);
			team1Clerics = geneticAlgorithm.newPopulation(team1Clerics, team1Fitness, keepPercent, crossPercent, drasticLikelihood, mutateLikelihood, elitism);
		}
		
		tm1FinalAvrFit = tm1FinalAvrFit/(lastGame + 1);
		tm2FinalAvrFit = tm2FinalAvrFit/(lastGame + 1);
		
		System.out.println("Final average fitness team1: " + tm1FinalAvrFit + ", team2: " + tm2FinalAvrFit);
		
		return bestTeams;
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
	
	public static void insertGeneticAis(double[][] warrior, double[][] wizard, double[][] cleric) {
		gameState.insertAi(new Warrior(warrior), 1, Color.red, team1_ai1_startPos);
	    gameState.insertAi(new Wizard(wizard), 1, Color.orange, team1_ai2_startPos);
	    gameState.insertAi(new Cleric(cleric), 1, Color.yellow, team1_ai3_startPos);
	}
	
	public static void insertStaticAis() {
		gameState.insertAi(newStaticAi(enemyDifficulty, 0), 2, Color.blue, team2_ai1_startPos);
		gameState.insertAi(newStaticAi(enemyDifficulty, 1), 2, Color.blue, team2_ai2_startPos);
		gameState.insertAi(newStaticAi(enemyDifficulty, 2), 2, Color.blue, team2_ai3_startPos);
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
				//return new MediumWizard();   to be implemented
			case 2:
				//return new MediumCleric();   to be implemented
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
	
	public ArrayList<Double> copyArrayList(ArrayList<Double> orgArrayList) {
		ArrayList<Double> copy = new ArrayList<Double>();
		for (int i = 0; i < orgArrayList.size(); i++) {
			copy.add(orgArrayList.get(i));
		}
		return copy;
	}
}
