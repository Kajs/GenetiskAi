package control;

import java.awt.Color;
import java.util.ArrayList;

import model.*;
import view.BoardRenderer;
import view.WindowManager;

public class Controller {
	
	static int width = 800;
	static int height = 600;
	static double hexSideSize = 40;
	static int rows = 7;
	static int columns = 8;
	public static int roundDelay = 1000;  // in milliseconds
	static Coordinate startPosition = new Coordinate(Math.sin(Math.toRadians(30)) * hexSideSize, 1);
	
	static int maxRounds = 25;
	static int games = 50;
	
	static int populationSize = 200;
	static int choices = 6;
	static int information = 15;
	static double keepPercent = 0.20;
	static double crossPercent = 0.75;
	static double drasticLikelihood = 0.3;
	static double mutateLikelihood = 1.0;
	public boolean elitism = true;
	
	static Coordinate team1_ai1_startPos = new Coordinate(3, 3);
	static Coordinate team1_ai2_startPos = new Coordinate(0, 0);
	static Coordinate team1_ai3_startPos = new Coordinate(1, 1);
	static Coordinate team2_ai1_startPos = new Coordinate(5, 6);
	static Coordinate team2_ai2_startPos = new Coordinate(4, 4);
	static Coordinate team2_ai3_startPos = new Coordinate(5, 5);
	
	public static GameState gameState;
	public BoardRenderer boardRenderer;
	public WindowManager window;
	public GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
	public static double[][][][] bestTeams;
	public static ArrayList<Double> bestTeamsFitness = new ArrayList<Double>();
	
	public Controller(boolean automatic, boolean displayAutomatic) {
		gameState = new GameState(startPosition, rows, columns, hexSideSize);
        if(!automatic) {     
            gameState.insertAi(new Warrior(geneticAlgorithm.generateWeights(choices, information)), 1, Color.red, team1_ai1_startPos);
    	    gameState.insertAi(new Warrior(geneticAlgorithm.generateWeights(choices, information)), 1, Color.orange, team1_ai2_startPos);
    	    gameState.insertAi(new Warrior(geneticAlgorithm.generateWeights(choices, information)), 1, Color.yellow, team1_ai3_startPos);
    		gameState.insertAi(new BaseWarrior(), 2, Color.blue, team2_ai1_startPos);
    		gameState.insertAi(new BaseWarrior(), 2, Color.blue, team2_ai2_startPos);
    		gameState.insertAi(new BaseWarrior(), 2, Color.blue, team2_ai3_startPos);
    		
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
        		bestTeams = evolve(maxRounds, games, populationSize, displayAutomatic);
        	}
    		else {
    			Launcher.allowRoundDelay = false;
    			bestTeams = evolve(maxRounds, games, populationSize, displayAutomatic);
    		}
        }
	}
	
public static void newBestTeamGame(int bestTeam) {
	gameState.reset();
	gameState.insertAi(new Warrior(bestTeams[0][bestTeam]), 1, Color.red, team1_ai1_startPos);
    gameState.insertAi(new Warrior(bestTeams[1][bestTeam]), 1, Color.orange, team1_ai2_startPos);
    gameState.insertAi(new Warrior(bestTeams[2][bestTeam]), 1, Color.yellow, team1_ai3_startPos);
	gameState.insertAi(new BaseWarrior(), 2, Color.blue, team2_ai1_startPos);
	gameState.insertAi(new BaseWarrior(), 2, Color.blue, team2_ai2_startPos);
	gameState.insertAi(new BaseWarrior(), 2, Color.blue, team2_ai3_startPos);
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
		
		for (int i = 0; i < games; i++) {
			double tm1AvrFit = 0;
			double tm2AvrFit = 0;
			ArrayList<Double> team1Fitness = new ArrayList<Double>();
			double bestFitness = (int)Math.pow(-2, 31);
			int bestTeam = 0;
			
			
			for (int lastAi = 0; lastAi < populationSize; lastAi++) {
				//System.out.println("Game " + i + ", team number " + lastAi + "_____________________________");
				
				gameState.reset();
				
				gameState.insertAi(new Warrior(team1Warriors[lastAi]), 1, Color.red, team1_ai1_startPos);
			    gameState.insertAi(new Warrior(team1Wizards[lastAi]), 1, Color.orange, team1_ai2_startPos);
			    gameState.insertAi(new Warrior(team1Clerics[lastAi]), 1, Color.yellow, team1_ai3_startPos);
			    
			    gameState.insertAi(new BaseWarrior(), 2, Color.blue, team2_ai1_startPos);
	    		gameState.insertAi(new BaseWarrior(), 2, Color.blue, team2_ai2_startPos);
	    		gameState.insertAi(new BaseWarrior(), 2, Color.blue, team2_ai3_startPos);

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
			
			team1Warriors = geneticAlgorithm.newPopulation(team1Warriors, team1Fitness, keepPercent, crossPercent, drasticLikelihood, mutateLikelihood, elitism);
			team1Wizards = geneticAlgorithm.newPopulation(team1Wizards, team1Fitness, keepPercent, crossPercent, drasticLikelihood, mutateLikelihood, elitism);
			team1Clerics = geneticAlgorithm.newPopulation(team1Clerics, team1Fitness, keepPercent, crossPercent, drasticLikelihood, mutateLikelihood, elitism);
		}
		tm1FinalAvrFit = tm1FinalAvrFit/(double)games;
		tm2FinalAvrFit = tm2FinalAvrFit/(double)games;
		
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
}
