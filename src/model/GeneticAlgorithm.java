package model;

import java.util.Random;

import static java.lang.Math.floor;

public class GeneticAlgorithm {
	int choices;
	int information;
	int populationSize;
	int populationLimit;
	
	int keepAmount;
	int crossAmount;
	int mutateAmount;
	
	boolean allwaysKeepBest;
	boolean skipZeroFitnessScaling;
	public final static Random randomGenerator = new Random();
	KeepPopulationThread[] keepPopulationThreads;
	CrossPopulationThread[] crossPopulationThreads;
	MutatePopulationThread[] mutatePopulationThreads;
	GetTotalFitnessThread[] getTotalFitnessThreads;
	ScaleFitnessThread[] scaleFitnessThreads;
	
    MultiThreading multiThreading;
    final HeapSort heapSort = new HeapSort();
	
	
	
	public GeneticAlgorithm (int populationSize, int choices, int information, double keepPercent, double crossPercent, boolean skipZeroFitnessScaling, boolean allwaysKeepBest, int numThreads, MultiThreading multiThreading, int fitnessScalingType) {
		this.populationSize = populationSize;
		this.choices = choices;
		this.information = information;
		this.allwaysKeepBest = allwaysKeepBest;
		this.skipZeroFitnessScaling = skipZeroFitnessScaling;
		this.multiThreading = multiThreading;
		
		keepAmount = (int)floor(populationSize * keepPercent);
		crossAmount = (int)floor(populationSize * crossPercent);
		crossAmount = crossAmount + (crossAmount % 2);
		mutateAmount = populationSize - keepAmount - crossAmount;
		
		if(allwaysKeepBest) { populationLimit = keepAmount; }
		else { populationLimit = populationSize; }
		
		keepPopulationThreads = new KeepPopulationThread[numThreads];
		crossPopulationThreads = new CrossPopulationThread[numThreads];
		mutatePopulationThreads = new MutatePopulationThread[numThreads];
		getTotalFitnessThreads = new GetTotalFitnessThread[numThreads];
		scaleFitnessThreads = new ScaleFitnessThread[numThreads];
		
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
		
		double mutateLikelihoodStart = 0.0;
		double mutateLikelihoodEnd = 0.0;
		double drasticLikelihoodStart = 0.0;
		double drasticLikelihoodEnd = 0.0;
		
		for (int i = 0; i < numThreads; i++) {
			if(i == numThreads - 1 || end > populationSize) { end = populationSize;}
			mutateLikelihoodStart = 1.0 / numThreads * i;
			mutateLikelihoodEnd = 1.0 / numThreads * (i+1);
			drasticLikelihoodStart = 0;
			drasticLikelihoodEnd = 1.0;
			//System.out.println("Mutate %: " + mutateLikelihoodStart + " to " + mutateLikelihoodEnd);
			//System.out.println("Drastic %: " + drasticLikelihoodStart + " to " + drasticLikelihoodEnd);
			mutatePopulationThreads[i] = new MutatePopulationThread(start, end, populationLimit, choices, information, mutateLikelihoodStart, mutateLikelihoodEnd, drasticLikelihoodStart, drasticLikelihoodEnd);
			start = end;
			end += stepSize;
		}
		
		stepSize = populationLimit/numThreads;
		if(stepSize < 1) {stepSize = 1;}
		start = 0;
		end = start + stepSize;
		
		for (int i = 0; i < numThreads; i++) {
			if(i == numThreads - 1 || end > populationLimit) { end = populationLimit;}
			getTotalFitnessThreads[i] = new GetTotalFitnessThread(start, end);
			start = end;
			end += stepSize;
		}
		
		stepSize = populationLimit/numThreads;
		if(stepSize < 1) {stepSize = 1;}
		start = 0;
		end = start + stepSize;
		
		for (int i = 0; i < numThreads; i++) {
			if(i == numThreads - 1 || end > populationLimit) { end = populationLimit;}
			scaleFitnessThreads[i] = new ScaleFitnessThread(start, end, fitnessScalingType, 0.9, 1.0/populationLimit, skipZeroFitnessScaling);
			start = end;
			end += stepSize;
		}
	}
	
	
	//------------------------- initial population
	
	
	
	public double[][][][] initialPopulation(int size, int choices, int information) {
		
		double[][][][] population = new double[size][3][choices + 1][information];
		
		for (int i = 0; i < size; i++) {
			population[i] = generateWeights(choices, information);
		}		
		return population;
	}
	
	private double[][][] generateWeights(int choices, int information) {
		double[][][] weights = new double[3][choices + 1][information];
		
		for (int t = 0; t < 3; t++) {
			for (int i = 0; i < choices + 1; i++) {
				for (int j = 0; j < information; j++) {
					double value = nextDouble();
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
		
        if(allwaysKeepBest) { heapSort.heapSortHigh(population, fitness, populationSize); }
		
		double[][][][] newPopulation = new double[populationSize][3][choices+1][information];
		double[] scaledFitness = fitness;
		
		multiThreading.runScaleFitnessThreads(scaleFitnessThreads, scaledFitness);
		double totalFitness = multiThreading.runGetTotalFitnessThreads(getTotalFitnessThreads, scaledFitness);
		
		multiThreading.runKeepPopulationThreads(keepPopulationThreads, population, newPopulation, scaledFitness, totalFitness);
		multiThreading.runCrossPopulationThreads(crossPopulationThreads, population, newPopulation, scaledFitness, totalFitness);
		multiThreading.runMutatePopulationThreads(mutatePopulationThreads, population, newPopulation, scaledFitness, totalFitness);
		
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
	
	
	
	
	//------------------------- misc functions
	
	private boolean coinFlip() { return randomGenerator.nextDouble() <= 0.5; }
	
	private double nextDouble() { return randomGenerator.nextDouble(); }
}
