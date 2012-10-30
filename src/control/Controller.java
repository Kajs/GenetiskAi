package control;

import java.awt.Color;
import java.util.ArrayList;

import model.*;
import view.BoardRenderer;
import view.WindowManager;

public class Controller {
	int choices = 6;
	int information = 15;
	int maxRounds = 100;
	double keepPercent = 0.2;
	double crossPercent = 0.8;
	double drasticLikelihood = 0.3;
	double mutateLikelihood = 0.4;
	Coordinate startPosition;
	double hexSideSize;
	int rows;
	int columns;

	public static GameState gameState;
	public BoardRenderer boardRenderer;
	public WindowManager window;
	public GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
	
	public Controller(int width, int height, int rows, int columns, Coordinate startPosition, double hexSideSize, boolean automatic, int maxRounds, int games, int populationSize) {
		this.startPosition = startPosition;
		this.hexSideSize = hexSideSize;
		this.rows = rows;
		this.columns = columns;
		
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
		ArrayList<Ai> team1Warriors = new ArrayList<Ai>();
		ArrayList<Ai> team1Wizards = new ArrayList<Ai>();
		ArrayList<Ai> team1Clerics = new ArrayList<Ai>();
		
		double[][] enemyWeight1 = geneticAlgorithm.generateWeights(choices, information);
		double[][] enemyWeight2 = geneticAlgorithm.generateWeights(choices, information);
		double[][] enemyWeight3 = geneticAlgorithm.generateWeights(choices, information);
		
		double team1FinalAverageFitness = 0.0;
		double team2FinalAverageFitness = 0.0;
		
		for (int i = 0; i < populationSize; i++) {
			double[][] weights = geneticAlgorithm.generateWeights(choices, information);
			team1Warriors.add(new Warrior(weights));
		}
		
		for (int i = 0; i < populationSize; i++) {
			double[][] weights = geneticAlgorithm.generateWeights(choices, information);
			team1Wizards.add(new Warrior(weights));
		}
		
		for (int i = 0; i < populationSize; i++) {
			double[][] weights = geneticAlgorithm.generateWeights(choices, information);
			team1Clerics.add(new Warrior(weights));
		}
		
		for (int i = 0; i < games; i++) {
			int lastAi = 0;
			double team1AverageFitness = 0.0;
			double team2AverageFitness = 0.0;
			int team1Alive = -1;
			int team2Alive = -1;
			ArrayList<Double> team1Fitness = new ArrayList<Double>();
			
			while (lastAi < populationSize) {
				//System.out.println("Game " + i + ", team number " + lastAi + "_____________________________");
				
				gameState = new GameState(startPosition, rows, columns, hexSideSize);
				gameState.reset();
				
				gameState.insertAi(team1Warriors.get(lastAi), 1, Color.red, new Coordinate(3, 3));
			    gameState.insertAi(team1Wizards.get(lastAi), 1, Color.orange, new Coordinate(0, 0));
			    gameState.insertAi(team1Clerics.get(lastAi), 1, Color.yellow, new Coordinate(1, 1));
			    
			    gameState.insertAi(new BaseWarrior2(), 2, Color.blue, new Coordinate(5, 6));
	    		gameState.insertAi(new BaseWarrior2(), 2, Color.blue, new Coordinate(4, 4));
	    		gameState.insertAi(new BaseWarrior2(), 2, Color.blue, new Coordinate(5, 5));
			    
			    /*
			    gameState.insertAi(new BaseWarrior(), 2, Color.blue, new Coordinate(5, 6));
				gameState.insertAi(new BaseWarrior(), 2, Color.blue, new Coordinate(4, 4));
				gameState.insertAi(new BaseWarrior(), 2, Color.blue, new Coordinate(5, 5));
				*/
				double[][] results = gameState.newGame(maxRounds);
				
				team1Alive = (int) results[0][2];
				team2Alive = (int) results[1][2];
				
				double team1FitnessValue = geneticAlgorithm.fitness(results[0]);
				double team2FitnessValue = geneticAlgorithm.fitness(results[1]);
				
				team1AverageFitness = team1AverageFitness + team1FitnessValue;
				team2AverageFitness = team2AverageFitness + team2FitnessValue;
				
				team1Fitness.add(team1FitnessValue);
				++lastAi;
				
				//System.out.println("Game " + i + ": team1 fitness = " + team1Fitness + ", team2Fitness = " + team2Fitness);
			}
			team1AverageFitness = team1AverageFitness / (double) populationSize;
			team2AverageFitness = team2AverageFitness / (double) populationSize;
			team1FinalAverageFitness = team1FinalAverageFitness + team1AverageFitness;
			team2FinalAverageFitness = team2FinalAverageFitness + team2AverageFitness;
			System.out.println("Game " + (i + 1) + " done: team 1 average fitness = " + team1AverageFitness  + ", team 2 average fitness = " + team2AverageFitness + ", team1Alive: " + team1Alive + ", team2Alive: " + team2Alive);
			
			team1Warriors = geneticAlgorithm.newPopulation(team1Warriors, team1Fitness, keepPercent, crossPercent, drasticLikelihood, mutateLikelihood);
			team1Wizards = geneticAlgorithm.newPopulation(team1Wizards, team1Fitness, keepPercent, crossPercent, drasticLikelihood, mutateLikelihood);
			team1Clerics = geneticAlgorithm.newPopulation(team1Clerics, team1Fitness, keepPercent, crossPercent, drasticLikelihood, mutateLikelihood);
		}
		team1FinalAverageFitness = team1FinalAverageFitness/(double)games;
		team2FinalAverageFitness = team2FinalAverageFitness/(double)games;
		
		System.out.println("Final average fitness team1: " + team1FinalAverageFitness + ", team2: " + team2FinalAverageFitness);
	}
}
