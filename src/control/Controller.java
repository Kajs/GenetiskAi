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
	static Coordinate startPosition = new Coordinate(Math.sin(Math.toRadians(30)) * hexSideSize, 1);
	
	static int maxRounds = 25;
	static int games = 1000;
	
	static int populationSize = 1000;
	static int choices = 6;
	static int information = 15;
	static double keepPercent = 0.2;
	static double crossPercent = 0.8;
	static double drasticLikelihood = 0.3;
	static double mutateLikelihood = 0.1;
	
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
	
	public Controller(boolean automatic) {
		
		gameState = new GameState(startPosition, rows, columns, hexSideSize);
		
        if(!automatic) {            
            gameState.insertAi(new Warrior(geneticAlgorithm.generateWeights(choices, information)), 1, Color.red, new Coordinate(3, 3));
    	    gameState.insertAi(new Warrior(geneticAlgorithm.generateWeights(choices, information)), 1, Color.red, new Coordinate(0, 0));
    		gameState.insertAi(new Warrior(geneticAlgorithm.generateWeights(choices, information)), 2, Color.green, new Coordinate(5, 6));
    		gameState.insertAi(new Warrior(geneticAlgorithm.generateWeights(choices, information)), 2, Color.green, new Coordinate(4, 4));
    		
        	boardRenderer = new BoardRenderer(rows, columns, gameState.getHexMatrix());
    		boardRenderer.setBackground(Color.white);
    		gameState.addObserver(boardRenderer);
    		
    		window = new WindowManager(width, height, boardRenderer, this);
        }
        else {
        	evolve(maxRounds, games, populationSize);
        }
	}
	
	public static int isOccupied(Coordinate coordinate) {
		return gameState.isOccupied(coordinate);
	}
	
	public static void colorHex(Coordinate position, Color color) {
		gameState.colorHex(position, color);
	}
	
	public void evolve(int maxRounds, int games, int populationSize) {
		ArrayList<ArrayList<Ai>> initialPopulation = geneticAlgorithm.initialPopulation(populationSize, choices, information);
		ArrayList<Ai> team1Warriors = initialPopulation.get(0);
		ArrayList<Ai> team1Wizards = initialPopulation.get(1);
		ArrayList<Ai> team1Clerics = initialPopulation.get(2);
		
		double tm1FinalAvrFit = 0;
		double tm2FinalAvrFit = 0;
		
		for (int i = 0; i < games; i++) {
			int lastAi = 0;
			double tm1AvrFit = 0;
			double tm2AvrFit = 0;
			ArrayList<Double> team1Fitness = new ArrayList<Double>();
			
			while (lastAi < populationSize) {
				//System.out.println("Game " + i + ", team number " + lastAi + "_____________________________");
				
				gameState = new GameState(startPosition, rows, columns, hexSideSize);
				gameState.reset();
				
				gameState.insertAi(team1Warriors.get(lastAi), 1, Color.red, team1_ai1_startPos);
			    gameState.insertAi(team1Wizards.get(lastAi), 1, Color.orange, team1_ai2_startPos);
			    gameState.insertAi(team1Clerics.get(lastAi), 1, Color.yellow, team1_ai3_startPos);
			    
			    gameState.insertAi(new BaseWarrior2(), 2, Color.blue, team2_ai1_startPos);
	    		gameState.insertAi(new BaseWarrior2(), 2, Color.blue, team2_ai2_startPos);
	    		gameState.insertAi(new BaseWarrior2(), 2, Color.blue, team2_ai3_startPos);

				double[][] results = gameState.newGame(maxRounds);
				
				double tm1FitVal = geneticAlgorithm.fitness(results[0]);
				double tm2FitVal = geneticAlgorithm.fitness(results[1]);
				
				tm1AvrFit = tm1AvrFit + tm1FitVal;
				tm2AvrFit = tm2AvrFit + tm2FitVal;
				
				team1Fitness.add(tm1FitVal);
				++lastAi;
				
				//System.out.println("Game " + i + ": team1 fitness = " + team1Fitness + ", team2Fitness = " + team2Fitness);
			}
			tm1AvrFit = tm1AvrFit / (double) populationSize;
			tm2AvrFit = tm2AvrFit / (double) populationSize;
			tm1FinalAvrFit = tm1FinalAvrFit + tm1AvrFit;
			tm2FinalAvrFit = tm2FinalAvrFit + tm2AvrFit;
			System.out.println("Game " + (i + 1) + ": tm1AvrFit = " + round(tm1AvrFit, 2)  + ", tm2AvrFit = " + round(tm2AvrFit, 2));
			
			team1Warriors = geneticAlgorithm.newPopulation(team1Warriors, team1Fitness, keepPercent, crossPercent, drasticLikelihood, mutateLikelihood);
			team1Wizards = geneticAlgorithm.newPopulation(team1Wizards, team1Fitness, keepPercent, crossPercent, drasticLikelihood, mutateLikelihood);
			team1Clerics = geneticAlgorithm.newPopulation(team1Clerics, team1Fitness, keepPercent, crossPercent, drasticLikelihood, mutateLikelihood);
		}
		tm1FinalAvrFit = tm1FinalAvrFit/(double)games;
		tm2FinalAvrFit = tm2FinalAvrFit/(double)games;
		
		System.out.println("Final average fitness team1: " + tm1FinalAvrFit + ", team2: " + tm2FinalAvrFit);
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
}
