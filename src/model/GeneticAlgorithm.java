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
	//private int keepAmount;
	
	
	
	public GeneticAlgorithm (int populationSize, int choices, int information, double keepPercent, double crossPercent, double drasticLikelihood, double mutateLikelihood, boolean skipZeroFitnessScaling, boolean allwaysKeepBest, int numThreads) {
		this.populationSize = populationSize;
		this.choices = choices;
		this.information = information;
		this.numThreads = numThreads;
		this.allwaysKeepBest = allwaysKeepBest;
		this.skipZeroFitnessScaling = skipZeroFitnessScaling;
		
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
		int start = 0;
		int end = start + stepSize;
		
		for (int i = 0; i < numThreads; i++) {
			if(i == numThreads - 1) { end = keepAmount; }
			keepPopulationThreads[i] = new KeepPopulationThread(start, end, allwaysKeepBest, populationLimit, choices, information);
			start = end;
			end += stepSize;
		}
		
		stepSize = crossAmount/numThreads;
		stepSize = stepSize + (stepSize % 2);
		start = keepAmount;
		end = start + stepSize;
		
		for (int i = 0; i < numThreads; i++) {
			if(i == numThreads - 1 || end > keepAmount + crossAmount) { end = keepAmount + crossAmount; }
			crossPopulationThreads[i] = new CrossPopulationThread(start, end, populationLimit, choices, information);
			start = end;
			end += stepSize;
		}
		
		stepSize = mutateAmount/numThreads;
		start = keepAmount + crossAmount;
		end = start + stepSize;
		double drasticStepSize = 1.0/numThreads;
		double drasticStart = 0;
		double drasticEnd = drasticStepSize;
		
		for (int i = 0; i < numThreads; i++) {
			if(i == numThreads - 1) { end = populationSize; drasticEnd = 1.0;}
			mutatePopulationThreads[i] = new MutatePopulationThread(start, end, populationLimit, choices, information, drasticStart, drasticEnd, mutateLikelihood);
			start = end;
			drasticStart = drasticEnd;
			end += stepSize;
			drasticEnd += drasticStepSize;
		}
		
	}
	
	
	//------------------------- initial population
	
	
	
	public double[][][] initialPopulation(int size, int choices, int information) {
		if(Launcher.allowGenAlgAnnounce) {System.out.println("Generating initialPopulation");}
		
		double[][][] population = new double[size][choices + 1][information];
		
		for (int i = 0; i < size; i++) {
			population[i] = generateWeights(choices, information);
		}		
		return population;
	}
	
	public double[][] generateWeights(int choices, int information) {
		double[][] weights = new double[choices + 1][information];
		
		for (int i = 0; i < choices + 1; i++) {
			for (int j = 0; j < information; j++) {
				double value = randomGenerator.nextDouble();
				if (randomGenerator.nextInt(1) == 0) {
					weights[i][j] = value * (-1.0);
				}
				else {
					weights[i][j] = value;
				}
			}
		}
		return weights;
	}
	
	
	
	
	//------------------------- new population
	
	
	
	
	public double[][][] newPopulation(double[][][] population, double[] fitness) {
        if(allwaysKeepBest) { HeapSort.heapSortHigh(population, fitness, populationSize); }
		
		double[][][] newPopulation = new double[populationSize][choices+1][information];
		double[] scaledFitness;
		
		//scaledFitness = fitness;
		//scaledFitness = linearTransformationScaling(fitness, 0.9, 1.0);
		scaledFitness = exponentialScaling(fitness);
		double totalFitness = getTotalFitness(scaledFitness, populationLimit);
		
		Thread[][] threads = new Thread[3][numThreads];
		
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
		
		boolean activeThreads = true;
		while(activeThreads) {
			activeThreads = false;
			try { Thread.sleep(1); } 
			catch(InterruptedException ex) { Thread.currentThread().interrupt(); }
			
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < numThreads; j++) {
					if (threads[i][j].isAlive()) { activeThreads = true; continue; }
				}
			}
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
		
		double speedBonus = 0.0 * (maxRounds - rounds)/maxRounds;
		double fitness = (teamHp/teamInitialHp) * (teamAlive * 2);
		fitness = fitness + ((enemiesInitialHp-enemiesHp)/enemiesInitialHp)*((enemiesSize-enemiesAlive) * 2);
		fitness = fitness * (1 + speedBonus);
		double maxFitness = (teamSize + enemiesSize) * 2 * (1 + speedBonus);
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
			else { scaledFitness[i] = sqrt(fitValue + 1); }
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
}
