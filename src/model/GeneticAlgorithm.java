package model;

import java.util.Random;

import static control.Controller.storedTeams;
import control.Launcher;

import static java.lang.Math.floor;
import static java.lang.Math.ceil;

public class GeneticAlgorithm {
	public static int geneticThreads;
	
	int choices;
	int information;
	int populationSize;
	int populationLimit;
	
	int keepAmount;
	int crossAmount;
	int mutateAmount;
	
	boolean allwaysKeepBest;
	boolean bestAreHalfRandom;
	boolean preferUniqueBest;
	boolean skipZeroFitnessScaling;
	public final static Random randomGenerator = new Random();
	KeepPopulationThread[] keepPopulationThreads;
	CrossPopulationThread[] crossPopulationThreads;
	MutatePopulationThread[] mutatePopulationThreads;
	GetTotalFitnessThread[] getTotalFitnessThreads;
	ScaleFitnessThread[] scaleFitnessThreads;
	
    MultiThreading multiThreading;
    final HeapSort heapSort = new HeapSort();
    
    double[][][][] populationSubset;
    double[] fitnessSubset;
	
	
	
	public GeneticAlgorithm (int populationSize, int choices, int information, double keepPercent, double crossPercent, boolean skipZeroFitnessScaling, boolean allwaysKeepBest, boolean bestAreHalfRandom, boolean preferUniqueBest, double preferUniqueBestFactor, boolean cutOffUniqueValues, double cutOffDecimal, int numThreads, MultiThreading multiThreading, int fitnessScalingType) {
		numThreads = 1; //back to singleThreading
		geneticThreads = numThreads;
		
		this.populationSize = populationSize;
		this.choices = choices;
		this.information = information;
		this.allwaysKeepBest = allwaysKeepBest;
		this.bestAreHalfRandom = bestAreHalfRandom;
		this.skipZeroFitnessScaling = skipZeroFitnessScaling;
		this.preferUniqueBest = preferUniqueBest;
		this.multiThreading = multiThreading;
		
		keepAmount = (int)ceil(populationSize * keepPercent);
		crossAmount = (int)floor(populationSize * crossPercent);
		crossAmount = crossAmount + (crossAmount % 2);
		mutateAmount = populationSize - keepAmount - crossAmount;
		
		if(allwaysKeepBest) { populationLimit = keepAmount; }
		else { populationLimit = populationSize; }
		populationSubset = new double[keepAmount][3][choices+1][information];
		fitnessSubset = new double[keepAmount];
		
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
		
		for (int i = 0; i < numThreads; i++) {
			if(i == numThreads - 1 || end > populationSize) { end = populationSize;}
			double mutateLikelihoodStart = 1.0 / numThreads * i;
			double mutateLikelihoodEnd = 1.0 / numThreads * (i + 1);
			double drasticLikelihoodStart = 0.0;
			double drasticLikelihoodEnd = 1.0;
			
			mutatePopulationThreads[i] = new MutatePopulationThread(start, end, populationLimit, choices, information, mutateLikelihoodStart, mutateLikelihoodEnd, drasticLikelihoodStart, drasticLikelihoodEnd);
			start = end;
			end += stepSize;
		}
		
		stepSize = populationSize/numThreads;
		if(stepSize < 1) {stepSize = 1;}
		start = 0;
		end = start + stepSize;
		
		for (int i = 0; i < numThreads; i++) {
			if(i == numThreads - 1 || end > populationSize) { end = populationSize;}
			getTotalFitnessThreads[i] = new GetTotalFitnessThread(start, end);
			start = end;
			end += stepSize;
		}
		
		stepSize = populationSize/numThreads;
		if(stepSize < 1) {stepSize = 1;}
		start = 0;
		end = start + stepSize;
		
		for (int i = 0; i < numThreads; i++) {
			if(i == numThreads - 1 || end > populationSize) { end = populationSize;}
			scaleFitnessThreads[i] = new ScaleFitnessThread(start, end, fitnessScalingType, 0.9, 1.0/populationSize, skipZeroFitnessScaling, preferUniqueBest, preferUniqueBestFactor, cutOffUniqueValues, cutOffDecimal);
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
		
		if(elitism) { bestAi = population[bestTeam]; }
		
		heapSort.heapSortHigh(population, fitness, populationSize);
		double[][][][] newPopulation = new double[populationSize][3][choices+1][information];
		
		if(Launcher.testUnscaledFitness) { for (int i = 0; i < fitness.length; i++) { System.out.println("Unscaled at " + i + ": " + fitness[i]); }}
		double[] scaledFitness = fitness;		
		multiThreading.runScaleFitnessThreads(scaleFitnessThreads, scaledFitness);
		double totalFitness = multiThreading.runGetTotalFitnessThreads(getTotalFitnessThreads, scaledFitness);		
		if(allwaysKeepBest && preferUniqueBest) { heapSort.heapSortHigh(population, scaledFitness, populationSize); } //sort again to move duplicates towards the end of the array;
		
		double subsetTotalFitness = 0;
		if(allwaysKeepBest) {
			
			int border = (int)ceil((double)keepAmount/2.0);
			for (int i = 0; i < border; i++) {
				populationSubset[i] = population[i]; 
				fitnessSubset[i] = scaledFitness[i];					
				if(bestAreHalfRandom) { totalFitness -= scaledFitness[i]; }
			}
			
			for (int i = border; i < keepAmount; i++) {
				int pos = i;
				if (bestAreHalfRandom) {
					int startPos = border;
					if(preferUniqueBest) { startPos = i; }
					pos = choseFitnessPosition(scaledFitness, totalFitness, startPos);
				}

				populationSubset[i] = population[pos];
				fitnessSubset[i] = scaledFitness[pos];				

				if(bestAreHalfRandom && preferUniqueBest) { 
					totalFitness -= scaledFitness[pos];
					swapPositions(population, scaledFitness, i, pos);
				}
			}
			
			for (int i = 0; i < fitnessSubset.length; i++) { subsetTotalFitness += fitnessSubset[i]; }
		}
		else {
			fitnessSubset = scaledFitness;
			populationSubset = population;
			subsetTotalFitness = totalFitness;
		}
		
		if(Launcher.testFitnessSubset) { for (int i = 0; i < fitnessSubset.length; i++) { System.out.println("Subset at " + i + ": " + fitnessSubset[i]); }}
		if(Launcher.countDuplicateFitnessValues) { System.out.println("Identical values = " + countDuplicateValues(fitnessSubset)); }
		
		multiThreading.runKeepPopulationThreads(keepPopulationThreads, populationSubset, newPopulation, fitnessSubset, subsetTotalFitness);
		multiThreading.runCrossPopulationThreads(crossPopulationThreads, populationSubset, newPopulation, fitnessSubset, subsetTotalFitness);
		multiThreading.runMutatePopulationThreads(mutatePopulationThreads, populationSubset, newPopulation, fitnessSubset, subsetTotalFitness);
		
		if(elitism) { newPopulation[0] = bestAi; }
		if(Launcher.insertStoredTeam) {
			newPopulation[newPopulation.length-1] = storedTeams[Launcher.insertStoredTeamPosition];
			Launcher.insertStoredTeam = false;
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
		
		double speedFactor = 0.0;
		double damageFactor = 1.0;
		double killFactor = 2.0;
		double speedBonus = speedFactor * (maxRounds - rounds)/maxRounds;
		
		double fitness = (teamHp/teamInitialHp) * (teamAlive * killFactor);
		fitness = fitness + (enemiesInitialHp-enemiesHp)/enemiesInitialHp * (damageFactor + ((enemiesSize-enemiesAlive) * killFactor));
		fitness = fitness * (1 + speedBonus);
		double maxFitness = (teamSize + enemiesSize) * killFactor * (1 + speedFactor) + damageFactor;
		fitness = fitness/maxFitness;
		if(teamHp < 0 || enemiesHp < 0) {System.out.println("Fitness: " + fitness +", " + teamInitialHp + ", " + teamHp + ", " + teamAlive + ", " + teamSize + ", " + enemiesInitialHp + ", " + enemiesHp + ", " + enemiesAlive + ", " + enemiesSize + ", " + maxRounds + ", " + rounds);}
		return fitness;
	}	
	
	
	
	
	//------------------------- misc functions
	
	private boolean coinFlip() { return randomGenerator.nextDouble() <= 0.5; }
	
	private int countDuplicateValues(double[] fitness) {
		int count = 0;
		
		for (int t = 0; t < fitness.length; t++) {
			for (int i = 0; i < fitness.length; i++) { if(i != t && fitness[t] == fitness[i]) { count++; break;}}
		}
		
		return count;
	}
	
	private double nextDouble() { return randomGenerator.nextDouble(); }
	
	public void updateMutateProbability() { for (int i = 0; i < mutatePopulationThreads.length; i++) {
		mutatePopulationThreads[i].updateMutateLikelihood();
	}}
	
	public void resetMutateProbability() { for (int i = 0; i < mutatePopulationThreads.length; i++) {
		mutatePopulationThreads[i].resetMutateLikelihood(false);
	}}
	
	public int choseFitnessPosition(double[] fitness, double totalFitness, int startPos) {
		
		double chance = nextDouble();
		double summedFitness = 0.0;
		for (int i = startPos; i < populationSize; i++) {
			summedFitness += fitness[i];
			if (chance <= summedFitness / totalFitness) { return i; }		
		}
		
		System.out.println("Error in GeneticAlgorithm.choseFitnessPosition: reached end of array without match, summedFitness = " + summedFitness + ", totalFitness = " + totalFitness);
		return (startPos + randomGenerator.nextInt(fitness.length - startPos));
	}
	
	public void swapPositions(double[][][][] population, double[] fitness, int pos1, int pos2) {
		double[][][] tempTeam = population[pos1];
		double tempFit = fitness[pos1];
		
		population[pos1] = population[pos2];
		fitness[pos1] = fitness[pos2];
		
		population[pos2] = tempTeam;
		fitness[pos2] = tempFit;
	}
}
