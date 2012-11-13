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
	static double mutateLikelihood = 0.95;
	public boolean elitism = true;
	public boolean skipZeroFitnessScaling = true;
	public boolean alwaysKeepBest = true;
	
	
	static Coordinate[][] geneticPositions;
	static Coordinate[][] staticPositions = new Coordinate[2][3];
	static int enemyDifficulty = 0;
	
	public static final GameState gameState = new GameState(startPosition, rows, columns, hexSideSize);
	public BoardRenderer boardRenderer;
	public WindowManager window;
	public GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(populationSize, choices, information, keepPercent, crossPercent, drasticLikelihood, mutateLikelihood, skipZeroFitnessScaling, alwaysKeepBest, numThreads);
	public static double[][][][] bestTeams;
	public static boolean runBestTeamGames = false;
	public static boolean runSingleBestTeamGame = false;
	public static int singleBestTeamNumber;
	public static double[] bestTeamsFitness = new double[maxGames];
	
	
	
	
	//_______________Thread Section________
	private static int numThreads = 33;
	private Thread[] threads;
	private GameThread[] gameThreads;
	
	//_______________Thread Section________
	
	public Controller(boolean automatic, boolean displayAutomatic) {
		setTeamPositions(false);
		Launcher.allowActionOutput = false;
		
		threads = new Thread[numThreads];
		gameThreads = new GameThread[numThreads];
		
		int stepSize = populationSize/numThreads;
		if(stepSize < 1) {stepSize = 1;}
		int start = 0;
		int end = stepSize;
		for (int i = 0; i < numThreads; i++) {
			if(i == numThreads - 1 || end > populationSize) {end = populationSize;}
			gameThreads[i] = new GameThread(new GameState(startPosition, rows, columns, hexSideSize), start, end, enemyDifficulty, maxRounds, geneticPositions, staticPositions, geneticAlgorithm, choices, information, Launcher.allowBestTeamsFitnessOutput);
			start = end;
			end += stepSize;
		}
    	
    	if(displayAutomatic) {
    		boardRenderer = new BoardRenderer(rows, columns, gameState.getHexMatrix());
    		boardRenderer.setBackground(Color.white);
    		gameState.addObserver(boardRenderer);
    		window = new WindowManager(width, height, boardRenderer, this);
    		bestTeams = evolve(maxRounds, maxGames, populationSize);
    		
    		Launcher.stop = false;
    		while(!Launcher.stop) {
    			if(runBestTeamGames) {
    				runBestTeamGames();
    				runBestTeamGames = false;
    			}
    			if(runSingleBestTeamGame) {
    				runSingleBestTeamGame(singleBestTeamNumber);
    				runSingleBestTeamGame = false;
    			}
    			try { Thread.sleep(1000); }
        		catch(InterruptedException ex) { Thread.currentThread().interrupt(); }
    		}
    	}
		else {
			Launcher.allowRoundDelay = false;
			bestTeams = evolve(maxRounds, maxGames, populationSize);
		}        	
	}
	
	
	
	
	
	//------------------------- run genetic algorithm
	
	
	
	
	
	public double[][][][] evolve(int maxRounds, int maxGames, int populationSize) {
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
			
			for (int i = 0; i < numThreads; i++) {
				gameThreads[i].setTeam1(team1);
				gameThreads[i].setTeam1Fitness(team1Fitness);
				threads[i] = new Thread(gameThreads[i], Integer.toString(i));
				threads[i].start();
			}
			
			boolean activeThreads = true;
			
			while(activeThreads) {
				try { Thread.sleep(1); } 
				catch(InterruptedException ex) { Thread.currentThread().interrupt(); }
				activeThreads = false;
				for (Thread thread : threads) {
					if(thread.isAlive()) {activeThreads = true; continue;}
				}
			}
			
			for (int i = 0; i < numThreads; i++) {
				totalFitness = totalFitness + gameThreads[i].getTotalFitness();
				tm1AvrFit = tm1AvrFit + gameThreads[i].getTeam1AverageFitness();
				tm2AvrFit = tm2AvrFit + gameThreads[i].getTeam2AverageFitness();
				double gameThreadBestFitness = gameThreads[i].getBestFitness();
				int gameThreadBestTeam = gameThreads[i].getBestTeam();
				if(gameThreadBestFitness > bestFitness) {
					bestFitness = gameThreadBestFitness;
					bestTeam = gameThreadBestTeam;
				}
			}
			
			
			gamesCompleted += 1;
			
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
			
			team1[0] = geneticAlgorithm.newPopulation(team1[0], copyArray(team1Fitness));  //copying team1Fitness in case Genetic Algorithm uses heapsort
			team1[1] = geneticAlgorithm.newPopulation(team1[1], copyArray(team1Fitness));
			team1[2] = geneticAlgorithm.newPopulation(team1[2], team1Fitness);
		}
		tm1FinalAvrFit = tm1FinalAvrFit/(lastGame + 1);
		tm2FinalAvrFit = tm2FinalAvrFit/(lastGame + 1);
		
		System.out.println("Final average fitness team1: " + tm1FinalAvrFit + ", team2: " + tm2FinalAvrFit);
		
		return bestTeams;
	}
	
	
	
	
	
	//------------------------- check best team games after evolve() finishes
	
		private void runSingleBestTeamGame(int bestTeam) {
			GameThread bestGameThread = new GameThread(gameState, 0, 1, enemyDifficulty, maxRounds, geneticPositions, staticPositions, geneticAlgorithm, choices, information, Launcher.allowBestTeamsFitnessOutput);
			final double[][][][] singleBestTeam = new double[3][1][choices+1][information];
			singleBestTeam[0][0] = bestTeams[0][bestTeam];
			singleBestTeam[1][0] = bestTeams[1][bestTeam];
			singleBestTeam[2][0] = bestTeams[2][bestTeam];
			
			bestGameThread.setTeam1(singleBestTeam);
			bestGameThread.setTeam1Fitness(bestTeamsFitness);
			Thread lastThread = new Thread(bestGameThread);
			lastThread.start();
			}

		private void runBestTeamGames() {
			GameThread bestGameThread = new GameThread(gameState, 0, gamesCompleted, enemyDifficulty, maxRounds, geneticPositions, staticPositions, geneticAlgorithm, choices, information, Launcher.allowBestTeamsFitnessOutput);
			bestGameThread.setTeam1(bestTeams);
			bestGameThread.setTeam1Fitness(bestTeamsFitness);
			Thread lastThread = new Thread(bestGameThread);
			lastThread.start();
		}
	
	
	
	//------------------------- team positions
	
	
	
		public void setTeamPositions(boolean random) {
			if(random) {
				
			}
			else {
				geneticPositions = new Coordinate[3][1];            //format: [aiType][aiNumber]
				geneticPositions[0][0] = new Coordinate(1, 2);
				geneticPositions[1][0] = new Coordinate(3, 3);
				geneticPositions[2][0] = new Coordinate(3, 5);
				/*
				geneticPositions[1][0] = new Coordinate(12, 0);
				geneticPositions[1][1] = new Coordinate(10, 14);
				geneticPositions[1][2] = new Coordinate(4, 4);
				*/
				staticPositions = new Coordinate[3][7];
				staticPositions[0][0] = new Coordinate(12, 12);
				staticPositions[1][0] = new Coordinate(10, 12);
				staticPositions[2][0] = new Coordinate(12, 10);
				//staticPositions[0][1] = new Coordinate(10, 11);
				//staticPositions[1][1] = new Coordinate(7, 14);
				//staticPositions[2][1] = new Coordinate(0, 16);
				//staticPositions[0][2] = new Coordinate(5, 18);
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
