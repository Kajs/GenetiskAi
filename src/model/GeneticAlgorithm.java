package model;

import java.util.Random;

import control.Launcher;
import static java.lang.Math.floor;
import static java.lang.Math.sqrt;

public class GeneticAlgorithm {
	private int choices;
	private int information;
	private int size;
	private boolean skipZeroFitnessScaling;
	private Random randomGenerator = new Random(); 	
	//private int keepAmount;
	
	public GeneticAlgorithm () {
		
	}
	
	
	//------------------------- initial population
	
	
	
	public double[][][] initialPopulation(int size, int choices, int information) {
		if(Launcher.allowGenAlgAnnounce) {System.out.println("Generating initialPopulation");}
		this.size = size;
		this.choices = choices;
		this.information = information;
		
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
	
	
	
	
	public double[][][] newPopulation(double[][][] population, double[] fitness, double keepPercent, double crossPercent, double drasticLikelihood, double mutateLikelihood, boolean elitism, int bestTeam, double totalFitness, boolean skipZeroFitnessScaling, boolean alwaysKeepBest) {
		this.skipZeroFitnessScaling = skipZeroFitnessScaling;
		double[] scaledFitness;
		int populationLimit;
		
		int keepAmount = (int)floor(size * keepPercent);
		int crossAmount = (int)floor(size * crossPercent);
		crossAmount = crossAmount - (crossAmount % 2);
		int mutateAmount = size - keepAmount - crossAmount;
		
		if(alwaysKeepBest) {
			populationLimit = keepAmount;
			HeapSort.heapSortHigh(population, fitness, size);
		}
		else { 
			populationLimit = size; 
		}
		
		double[][][] newPopulation = new double[size][choices+1][information];
		
		//scaledFitness = fitness;
		//scaledFitness = linearTransformationScaling(fitness, 0.9, 1.0);
		scaledFitness = exponentialScaling(fitness);
		totalFitness = getTotalFitness(scaledFitness, populationLimit);
		
		for (int i = 0; i < keepAmount; i++) {
			if(alwaysKeepBest) {newPopulation[i] = population[i];}
			else {
				if(i == 0 && elitism) {	newPopulation[0] = population[bestTeam];	}					
				else { newPopulation[i] = choseParents(1, population, scaledFitness, totalFitness, populationLimit)[0]; }
			}
		}
		
		
		for(int i = keepAmount; i < crossAmount + keepAmount; i = i + 2) {
			//System.out.println("Cross i: " + i);
			double[][][] parents = choseParents(2, population, scaledFitness, totalFitness, populationLimit);			
			double[][] child1 = crossover(parents[0], parents[1]);
			double[][] child2 = crossover(parents[1], parents[0]);
			
			newPopulation[i] = child1;
			newPopulation[i + 1] = child2;
		}
		
		double stepSize = (1.0 - drasticLikelihood) / mutateAmount;
		double newDrasticLikelihood = drasticLikelihood;
		
		for (int i = keepAmount + crossAmount; i < keepAmount + crossAmount + mutateAmount; i++) {
			//System.out.println("Mutate i: " + i);
			double[][][] mutant = choseParents(1, population, scaledFitness, totalFitness, populationLimit);
			newPopulation[i] = mutate(mutant[0], newDrasticLikelihood, mutateLikelihood);
			newDrasticLikelihood = newDrasticLikelihood + stepSize;
		}
		
		return newPopulation;
	}
	
	
	
	
	
	//------------------------- crossover, mutate and selection functions
	
	
	
	
	
	public double[][][] choseParents(int numberOfParents, double[][][] population, double[] fitness, double totalFitness, int populationLimit) {
		double[][][] parents = new double[numberOfParents][choices+1][information];
		int parentsFound = 0;
		
		while(parentsFound < numberOfParents) {
			double chance = randomGenerator.nextDouble();
			double summedFitness = 0.0;
			for (int i = 0; i < populationLimit; i++) {
				summedFitness = summedFitness + fitness[i];
				if (chance <= summedFitness / totalFitness) {
					parents[parentsFound] = population[i];
					break;
				}
			}
			parentsFound = parentsFound + 1;
		}		
		return parents;
	}
	
	public double[][] crossover (double[][] dad, double[][] mom) {
		double[][] child = new double[choices + 1][information];
		for (int i = 0; i < choices + 1; i++) {
			for (int j = 0; j < information; j++) {
				boolean flip = randomGenerator.nextInt(1) == 1;
				if (flip) {child[i][j] = dad[i][j];
				}
				else {
					child[i][j] = mom[i][j];
				}
			}
		}		
		return child;
	}
	
	public double[][] mutate (double[][] child, double drasticLikelihood, double mutateLikelihood) {
		double[][] mutant = new double[choices + 1][information];
		
		for (int i = 0; i < choices + 1; i++) {
			for (int j = 0; j < information; j++) {
				boolean mutate = randomGenerator.nextDouble() <= mutateLikelihood;
				if(mutate) {
					boolean flip = randomGenerator.nextDouble() <= drasticLikelihood;
					if(flip) {
						double value = randomGenerator.nextDouble();
						if (randomGenerator.nextInt(1) == 0) {
							mutant[i][j] = value * (-1.0);
						}
						else {
							mutant[i][j] = value;
						}
					}
					else {
						double value = child[i][j];
						if (randomGenerator.nextInt(1) == 0) {
							value = value * 1.1;
							if (value > 1.0) {
								value = 1.0;
							}
							if (value < -1.0) {
								value = -1.0;
							}
							mutant[i][j] = value;
						}
						else {
							value = value * 0.9;
							mutant[i][j] = value;
						}						
					}
				}					
			}
		}
		return mutant;
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
