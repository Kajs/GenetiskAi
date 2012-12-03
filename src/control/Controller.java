package control;

import java.awt.Color;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import model.*;
import view.BoardRenderer;
import view.XyChart;
import view.WindowManager;
import view.XyDeviationChart;
import view.XySplineChart;

public class Controller {
	
	final int width = 930;
	final int height = 600;
	static final int rows = 20;
	static final int columns = 40;
	public static final double boardDiagonal = rows + columns;
	final double hexSideSize = scaledHexSideSize();
	public static int roundDelay = 1000;  // in milliseconds
	final Coordinate startPosition = new Coordinate(sin(toRadians(30)) * hexSideSize, 1);
	
	final int maxRounds = 100;
	static final int maxGames = 100;
	public static int gamesCompleted = 0;
	
	final int populationSize = 1000;
	final int choices = 6;
	public final static int information = 31;
	final double keepPercent = 0.25;
    final double crossPercent = 0.25;
	final double mutateLikelihood = 0.9;
	final boolean elitism = true;
	final boolean skipZeroFitnessScaling = true;
	final boolean alwaysKeepBest = true;
	
	
	static Coordinate[][] geneticPositions;
	static Coordinate[][] staticPositions;
	final int enemyDifficulty = 0;
	
	public static GameState gameState;
	public static BoardRenderer boardRenderer;
	WindowManager window;
	
	public static double[][][][] bestTeams;
	public static boolean runBestTeamGames = false;
	public static boolean runSingleBestTeamGame = false;
	public static int singleBestTeamNumber;
	public static double[] bestTeamsFitness = new double[maxGames];
	private static double[] team1PopulationFitness = new double[maxGames];
	private static double[] team2PopulationFitness = new double[maxGames];
	
	
	// ____Scenario Section____
	private static Scenario[] scenarios;
	final boolean bothTeamsStart = false;
	final boolean alsoReversedPositions = false;
	final boolean testingStatics = true;
	final int testStaticDifficulty = 2;
	
	// ____Scenario Section____
	
	//_______________Thread Section________
	static int numThreads = 4;
	final MultiThreading multiThreading = new MultiThreading(numThreads);
	final GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(populationSize, choices, information, keepPercent, crossPercent, mutateLikelihood, skipZeroFitnessScaling, alwaysKeepBest, numThreads, multiThreading);
	private GameThread[] gameThreads;
	
	//_______________Thread Section________
	
	public Controller(boolean automatic, boolean displayAutomatic) {
		
		Launcher.allowActionOutput = false;
		gameState = new GameState(startPosition, rows, columns, hexSideSize);
		setupScenarios();
		
		if(numThreads > populationSize) { 
			numThreads = populationSize; 
			System.out.println("Warning: there are more threads than population size, threads set to populationSize"); 
			}

		gameThreads = new GameThread[numThreads];
		
		int stepSize = populationSize/numThreads;
		if(stepSize < 1) {stepSize = 1;}
		int start = 0;
		int end = stepSize;
		for (int i = 0; i < numThreads; i++) {
			if(i == numThreads - 1 || end > populationSize) {end = populationSize;}
			gameThreads[i] = new GameThread(new GameState(startPosition, rows, columns, hexSideSize), start, end, enemyDifficulty, maxRounds, choices, information, Launcher.allowBestTeamsFitnessOutput, scenarios, alsoReversedPositions, bothTeamsStart, testingStatics, testStaticDifficulty, geneticAlgorithm);
			start = end;
			end += stepSize;
		}
    	
    	if(displayAutomatic) {
    		boardRenderer = new BoardRenderer(rows, columns, gameState.getHexMatrix());
    		boardRenderer.setBackground(Color.white);
    		gameState.addObserver(boardRenderer);
    		window = new WindowManager(width, height, boardRenderer, this);
    		evolve(maxRounds, maxGames, populationSize);
    		
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
			evolve(maxRounds, maxGames, populationSize);
		}        	
	}
	
	
	
	
	
	//------------------------- run genetic algorithm
	
	
	
	
	
	public void evolve(int maxRounds, int maxGames, int populationSize) {
		double[][][][] team1 = geneticAlgorithm.initialPopulation(populationSize, choices, information);
		bestTeams = new double[maxGames][3][choices+1][information];
		
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
			
			multiThreading.runGameThreads(gameThreads, team1, team1Fitness);
			
			for (int i = 0; i < numThreads; i++) {
				totalFitness = totalFitness + gameThreads[i].getTotalFitness();
				tm1AvrFit = tm1AvrFit + gameThreads[i].getTeam1AverageFitness() / numThreads;
				tm2AvrFit = tm2AvrFit + gameThreads[i].getTeam2AverageFitness() / numThreads;
				double gameThreadBestFitness = gameThreads[i].getBestFitness();
				int gameThreadBestTeam = gameThreads[i].getBestTeam();
				if(gameThreadBestFitness > bestFitness) {
					bestFitness = gameThreadBestFitness;
					bestTeam = gameThreadBestTeam;
				}
			}
			
			gamesCompleted += 1;
			
			bestTeams[game] = team1[bestTeam];
			
			bestTeamsFitness[game] = bestFitness;

			tm1FinalAvrFit = tm1FinalAvrFit + tm1AvrFit;
			tm2FinalAvrFit = tm2FinalAvrFit + tm2AvrFit;
			
			team1PopulationFitness[game] = tm1AvrFit;
			team2PopulationFitness[game] = tm2AvrFit;
			
		    System.out.println("Game " + (game + 1) + " bestFit: " + round(bestFitness, 2) + ", tm1AvrFit = " + round(tm1AvrFit, 2)  + ", tm2AvrFit = " + round(tm2AvrFit, 2));
			
			team1 = geneticAlgorithm.newPopulation(team1, team1Fitness, elitism, bestTeam);
		}
		
		tm1FinalAvrFit = tm1FinalAvrFit/(lastGame + 1);
		tm2FinalAvrFit = tm2FinalAvrFit/(lastGame + 1);
		
		System.out.println("Final average fitness team1: " + tm1FinalAvrFit + ", team2: " + tm2FinalAvrFit);
	}
	
	private void runSingleBestTeamGame(int bestTeam) {
		GameThread bestGameThread = new GameThread(gameState, 0, 1, enemyDifficulty, maxRounds, choices, information, Launcher.allowBestTeamsFitnessOutput, scenarios, alsoReversedPositions, bothTeamsStart, testingStatics, testStaticDifficulty, geneticAlgorithm);
		final double[][][][] singleBestTeam = new double[1][3][choices+1][information];
		final double[] singleBestTeamFitness = new double[1];
		singleBestTeamFitness[0] = bestTeamsFitness[bestTeam];
		singleBestTeam[0] = bestTeams[bestTeam];
		
		bestGameThread.setTeam1(singleBestTeam);
		bestGameThread.setTeam1Fitness(singleBestTeamFitness);
		Thread lastThread = new Thread(bestGameThread);
		lastThread.start();
		}

	private void runBestTeamGames() {
		GameThread bestGameThread = new GameThread(gameState, 0, gamesCompleted, enemyDifficulty, maxRounds, choices, information, Launcher.allowBestTeamsFitnessOutput, scenarios, alsoReversedPositions, bothTeamsStart, testingStatics, testStaticDifficulty, geneticAlgorithm);
		bestGameThread.setTeam1(bestTeams);
		bestGameThread.setTeam1Fitness(bestTeamsFitness);
		Thread lastThread = new Thread(bestGameThread);
		lastThread.start();
	}
		
	public void setupScenarios() {
		
		scenarios = new Scenario[4];
		int scenarioCounter = 0;
		
		//Scenario 0 3v3 standard, spreadout starting positions
		
		geneticPositions = new Coordinate[3][3];
		geneticPositions[0][0] = new Coordinate(13, 2);
		geneticPositions[0][1] = new Coordinate(7, 2);
		geneticPositions[0][2] = new Coordinate(1, 2);

		staticPositions = new Coordinate[3][3];
		staticPositions[0][0] = new Coordinate(6, 20);
		staticPositions[0][1] = new Coordinate(5, 20);
		staticPositions[0][2] = new Coordinate(7, 20);		
		
		scenarios[scenarioCounter++] = new Scenario(geneticPositions, staticPositions);	
		
		//Scenario 0.1 3v3 standard, close starting positions
		
		geneticPositions = new Coordinate[3][3];
		geneticPositions[0][0] = new Coordinate(9, 2);
		geneticPositions[0][1] = new Coordinate(7, 2);
		geneticPositions[0][2] = new Coordinate(5, 2);

		staticPositions = new Coordinate[3][3];
		staticPositions[0][0] = new Coordinate(6, 20);
		staticPositions[0][1] = new Coordinate(5, 20);
		staticPositions[0][2] = new Coordinate(7, 20);		
		
		scenarios[scenarioCounter++] = new Scenario(geneticPositions, staticPositions);
		
		//Scenario 0.2 positioned in the middle of map
		
		geneticPositions = new Coordinate[3][3];
		geneticPositions[0][0] = new Coordinate(18, 15);
		geneticPositions[0][1] = new Coordinate(18, 13);
		geneticPositions[0][2] = new Coordinate(18, 17);

		staticPositions = new Coordinate[3][3];
		staticPositions[0][0] = new Coordinate(2, 14);
		staticPositions[0][1] = new Coordinate(2, 12);
		staticPositions[0][2] = new Coordinate(2, 16);		
		
		scenarios[scenarioCounter++] = new Scenario(geneticPositions, staticPositions);		
		
		//Scenario 1 surrounded
		
		geneticPositions = new Coordinate[3][3];
		geneticPositions[0][0] = new Coordinate(10, 20);
		geneticPositions[0][1] = new Coordinate(10, 21);
		geneticPositions[0][2] = new Coordinate(11, 20);

		staticPositions = new Coordinate[3][3];
		staticPositions[0][0] = new Coordinate(1, 1);
		staticPositions[0][1] = new Coordinate(1, 39);
		staticPositions[0][2] = new Coordinate(19, 1);
		staticPositions[1][0] = new Coordinate(19, 39);
		
		scenarios[scenarioCounter++] = new Scenario(geneticPositions, staticPositions);		
	}
	
	

	
	//------------------------- misc functions
	
	
	
	
	public double scaledHexSideSize() {
		double maxHexHeight = (double)height/((double)rows * cos(toRadians(30)) * (2.0 + 1.0/(double)rows)) * 0.8941;
		double maxHexWidth = (double)width/((sin(toRadians(30)) + (double)columns * (1.0 + sin(toRadians(30))))) * 0.98;
		if(maxHexHeight <= maxHexWidth) {
			return maxHexHeight;
		}
		else{
			return maxHexWidth;
		}
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
	
	//_________________________________JFreeChart section
	
	public static int numChartLines = 3;
	
	public static String[] chartNames() {
		String[] names = new String[numChartLines];
		names[0] = "Team 1 best";
		names[1] = "Team 1 average";
		names[2] = "Team 2 average";
		return names;
	}
	
	public static double[][] chartData() {
		double[][] fitnessMatrix = new double[numChartLines][gamesCompleted];
		for (int i = 0; i < gamesCompleted; i++) {
			fitnessMatrix[0][i] = bestTeamsFitness[i];
			fitnessMatrix[1][i] = team1PopulationFitness[i];
			fitnessMatrix[2][i] = team2PopulationFitness[i];
		}
		return fitnessMatrix;
	}
	
	public static void showFitnessXyChart() {
		XyChart fitnessChart = new XyChart("Xy Chart", chartData(), chartNames());
		fitnessChart.showResults();
	}
	
	public static void showFitnessXySplineChart() {
		XySplineChart fitnessChart = new XySplineChart("Xy Spline Chart", chartData(), chartNames());
		fitnessChart.showResults();
	}
	
	public static void showFitnessXyDeviationChart() {
		XyDeviationChart fitnessChart = new XyDeviationChart("Xy Standard Deviation Chart", chartData(), chartNames());
		fitnessChart.showResults();
	}
	
	//_________________________________JFreeChart section
}
