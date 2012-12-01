package model;

import java.util.Random;

import control.Launcher;
import static java.lang.Math.floor;
import static java.lang.Math.sqrt;

public class GeneticAlgorithm {
	private int choices;
	private int information;
	private int populationSize;
	private int populationLimit;
	
	private int keepAmount;
	private int crossAmount;
	private int mutateAmount;
	
	private int numThreads;
	private boolean allwaysKeepBest;
	private boolean skipZeroFitnessScaling;
	private Random randomGenerator = new Random(); 	
	private KeepPopulationThread[] keepPopulationThreads;
	private CrossPopulationThread[] crossPopulationThreads;
	private MutatePopulationThread[] mutatePopulationThreads;
	
	private Thread[][] threads;
	
	
	
	public GeneticAlgorithm (int populationSize, int choices, int information, double keepPercent, double crossPercent, double mutateLikelihood, boolean skipZeroFitnessScaling, boolean allwaysKeepBest, int numThreads) {
		this.populationSize = populationSize;
		this.choices = choices;
		this.information = information;
		this.numThreads = numThreads;
		this.allwaysKeepBest = allwaysKeepBest;
		this.skipZeroFitnessScaling = skipZeroFitnessScaling;
		
		threads = new Thread[3][numThreads];
		
		keepAmount = (int)floor(populationSize * keepPercent);
		crossAmount = (int)floor(populationSize * crossPercent);
		crossAmount = crossAmount + (crossAmount % 2);
		mutateAmount = populationSize - keepAmount - crossAmount;
		
		if(allwaysKeepBest) { populationLimit = keepAmount; }
		else { populationLimit = populationSize; }
		
		keepPopulationThreads = new KeepPopulationThread[numThreads];
		crossPopulationThreads = new CrossPopulationThread[numThreads];
		mutatePopulationThreads = new MutatePopulationThread[numThreads];
		
		int stepSize = keepAmount/numThreads;
		if(stepSize < 1) {stepSize = 1;}
		int start = 0;
		int end = start + stepSize;
		
		for (int i = 0; i < numThreads; i++) {
			if(i == numThreads - 1 || end > keepAmount) { end = keepAmount; }
			keepPopulationThreads[i] = new KeepPopulationThread(start, end, allwaysKeepBest, populationLimit, choices, information);
			start = end;
			end += stepSize;
		}
		
		stepSize = crossAmount/numThreads;
		stepSize = stepSize + (stepSize % 2);
		if(stepSize < 2) {stepSize = 2;}
		start = keepAmount;
		end = start + stepSize;
		
		for (int i = 0; i < numThreads; i++) {
			if(i == numThreads - 1 || end > keepAmount + crossAmount) { end = keepAmount + crossAmount; }
			crossPopulationThreads[i] = new CrossPopulationThread(start, end, populationLimit, choices, information);
			start = end;
			end += stepSize;
		}
		
		stepSize = mutateAmount/numThreads;
		if(stepSize < 1) {stepSize = 1;}
		start = keepAmount + crossAmount;
		end = start + stepSize;
		double drasticStart = 0;
		double drasticEnd = 1;
		
		for (int i = 0; i < numThreads; i++) {
			if(i == numThreads - 1 || end > populationSize) { end = populationSize;}
			mutatePopulationThreads[i] = new MutatePopulationThread(start, end, populationLimit, choices, information, drasticStart, drasticEnd, mutateLikelihood);
			start = end;
			end += stepSize;
		}
		
	}
	
	
	//------------------------- initial population
	
	
	
	public double[][][][] initialPopulation(int size, int choices, int information) {
		if(Launcher.allowGenAlgAnnounce) {System.out.println("Generating initialPopulation");}
		
		double[][][][] population = new double[size][3][choices + 1][information];
		
		for (int i = 0; i < size; i++) {
			population[i] = generateWeights(choices, information);
		}		
		return population;
	}
	
	public double[][][] generateWeights(int choices, int information) {
		double[][][] weights = new double[3][choices + 1][information];
		
		for (int t = 0; t < 3; t++) {
			for (int i = 0; i < choices + 1; i++) {
				for (int j = 0; j < information; j++) {
					double value = randomGenerator.nextDouble();
					if (coinFlip()) {
						weights[t][i][j] = value * (-1.0);
					}
					else {
						weights[t][i][j] = value;
					}
				}
			}
		}
		
		return weights;
	}
	
	
	
	
	//------------------------- new population
	
	
	
	
	public double[][][][] newPopulation(double[][][][] population, double[] fitness, boolean elitism, int bestTeam) {
		double[][][] bestAi = null;
		double bestAiFitness = 0;
		
		if(elitism) {
			bestAi = population[bestTeam];
			bestAiFitness = fitness[bestTeam];
		}
		
        if(allwaysKeepBest) { HeapSort.heapSortHigh(population, fitness, populationSize); }
		
		double[][][][] newPopulation = new double[populationSize][3][choices+1][information];
		double[] scaledFitness;
		
		//scaledFitness = fitness;
		//scaledFitness = linearTransformationScaling(fitness, 0.9, 1.0/populationSize);
		scaledFitness = exponentialScaling(fitness);
		double totalFitness = getTotalFitness(scaledFitness, populationLimit);
		
		for (int i = 0; i < numThreads; i++) {
			keepPopulationThreads[i].setVariables(population, newPopulation, scaledFitness, totalFitness);
			threads[0][i] = new Thread(keepPopulationThreads[i], "[0][" + i + "]");
			threads[0][i].start();
			crossPopulationThreads[i].setVariables(population, newPopulation, scaledFitness, totalFitness);
			threads[1][i] = new Thread(crossPopulationThreads[i], "[1][" + i + "]");
			threads[1][i].start();
			mutatePopulationThreads[i].setVariables(population, newPopulation, scaledFitness, totalFitness);
			threads[2][i] = new Thread(mutatePopulationThreads[i], "[2][" + i + "]");
			threads[2][i].start();
		}
		
		for (int i = 0; i < 3; i++) { sync(threads[i]); }
		
		if(elitism) {
			newPopulation[0] = bestAi;
			fitness[0] = bestAiFitness;
		}
		
		return newPopulation;
	}
	
	
	
	
	//------------------------- fitness function
	
	
	
	
	
	public double fitness (double[] results) {
		double teamInitialHp = results[0];
		double teamHp = results[1];
		double teamAlive = results[2];
		double teamSize = results[3];
		double enemiesInitialHp = results[4];
		double enemiesHp = results[5];
		double enemiesAlive = results[6];
		double enemiesSize = results[7];
		double maxRounds = results[8];
		double rounds = results[9];
		
		double bonusFactor = 0.0;
		double speedBonus = bonusFactor * (maxRounds - rounds)/maxRounds;
		double fitness = (teamHp/teamInitialHp) * (teamAlive * 2);
		fitness = fitness + ((enemiesInitialHp-enemiesHp)/enemiesInitialHp)*((enemiesSize-enemiesAlive) * 2);
		fitness = fitness * (1 + speedBonus);
		double maxFitness = (teamSize + enemiesSize) * 2 * (1 + bonusFactor);
		fitness = fitness/maxFitness;
		if(teamHp < 0 || enemiesHp < 0) {System.out.println("Fitness: " + fitness +", " + teamInitialHp + ", " + teamHp + ", " + teamAlive + ", " + teamSize + ", " + enemiesInitialHp + ", " + enemiesHp + ", " + enemiesAlive + ", " + enemiesSize + ", " + maxRounds + ", " + rounds);}
		return fitness;
	}
	
	
	
	
	
	//------------------------- scaling functions
	
	
	
	
	
	public double[] exponentialScaling(double[] orgFitness) {
		int length = orgFitness.length;
		double[] scaledFitness = new double[length];
		for (int i = 0; i < length; i++) {
			double fitValue = orgFitness[i];
			
			if(skipZeroFitnessScaling && fitValue == 0) { scaledFitness[i] = 0.0; }
			else { scaledFitness[i] = sqrt(fitValue + 1.0/populationSize); }
		}
		return scaledFitness;
	}
	
	public double[] linearTransformationScaling(double[] orgFitness, double a, double b) {
		int length = orgFitness.length;
		double[] scaledFitness = new double[length];
		for (int i = 0; i < length; i++) {
			double fitValue = orgFitness[i];
			
			if(skipZeroFitnessScaling && fitValue == 0) { scaledFitness[i] = 0.0; }			
			else { scaledFitness[i] = fitValue * a + b; }
		}
		return scaledFitness;
	}
	
	
	
	
	//------------------------- misc functions
	
	
	
	public double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	public double getTotalFitness(double[] fitness, int limit) {
		double totalFitness = 0;
		for (int i = 0; i < limit; i++) {
			totalFitness = totalFitness + fitness[i];
		}
		return totalFitness;
	}
	
	public void sync(Thread[] threads) {
		for (int i = 0; i < threads.length; i++) {
			try { threads[i].join(); } 
			catch(InterruptedException ex) { Thread.currentThread().interrupt(); }
		}
	}
	
	private boolean coinFlip() { return randomGenerator.nextDouble() <= 0.5; }
}
