package model;

import java.util.ArrayList;
import java.util.Random;
import static java.lang.Math.floor;
import static java.lang.Math.sqrt;

public class GeneticAlgorithm {
	private int choices;
	private int information;
	private int size;
	private Random randomGenerator = new Random(); 	
	
	public GeneticAlgorithm () {
		
	}
	
	public double[][][] initialPopulation(int size, int choices, int information) {
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
	
	public double[][][] newPopulation(double[][][] population, ArrayList<Double> fitness, double keepPercent, double crossPercent, double drasticLikelihood, double mutateLikelihood, boolean elitism) {
		int keepAmount = (int)floor(size * keepPercent);
		int crossAmount = (int)floor(size * crossPercent);
		crossAmount = crossAmount - (crossAmount % 2);
		int mutateAmount = size - keepAmount - crossAmount;
		
		double[][][] newPopulation = new double[size][choices+1][information];
		
		ArrayList<Double> scaledFitness = exponentialScaling(fitness);
		double totalFitness = 0;
		double bestFitness = Math.pow(-2, 31);
		//double unscaledBestFitness = Math.pow(-2, 31);
		int bestFitnessPosition = 0;
		
		for (int i = 0; i < scaledFitness.size(); i++) {			
			double fit = scaledFitness.get(i);
			if(fit > bestFitness) { 
				bestFitness = fit;
				//unscaledBestFitness = fitness.get(i);
				bestFitnessPosition = i;
			}
			totalFitness = totalFitness + fit;
		}
		
		for (int i = 0; i < keepAmount; i++) {
			if(i == 0 && elitism) {
				newPopulation[0] = population[bestFitnessPosition];
				//System.out.println("adding fitness " + round(unscaledBestFitness, 2) + " from position " + bestFitnessPosition);
				continue;
			}
			newPopulation[i] = choseParents(1, population, scaledFitness, totalFitness)[0];
		}
		
		for(int i = keepAmount; i < crossAmount/2 + keepAmount; i++) {
			double[][][] parents = choseParents(2, population, scaledFitness, totalFitness);			
			double[][] child1 = crossover(parents[0], parents[1]);
			double[][] child2 = crossover(parents[1], parents[0]);
			
			newPopulation[i] = child1;
			newPopulation[i + 1] = child2;
		}
		
		for (int i = keepAmount + crossAmount; i < keepAmount + crossAmount + mutateAmount; i++) {
			double[][][] mutant = choseParents(1, population, scaledFitness, totalFitness);
			newPopulation[i] = mutate(mutant[0], drasticLikelihood, mutateLikelihood);
		}
		
		return newPopulation;
	}
	
	public double[][][] choseParents(int numberOfParents, double[][][] population, ArrayList<Double> fitness, double totalFitness) {
		double[][][] parents = new double[numberOfParents][choices+1][information];
		int parentsFound = 0;
		
		while(parentsFound < numberOfParents) {
			double chance = randomGenerator.nextDouble();
			double summedFitness = 0.0;
			for (int i = 0; i < size; i++) {
				summedFitness = summedFitness + fitness.get(i);
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
		
		double fitness = (teamHp/teamInitialHp) * (teamAlive * 2);
		fitness = fitness + ((enemiesInitialHp-enemiesHp)/enemiesInitialHp)*((enemiesSize-enemiesAlive) * 2); 
		//fitness = fitness + fitness * (maxRounds - rounds)/maxRounds;
		if(teamHp < 0 || enemiesHp < 0) {System.out.println("Fitness: " + fitness +", " + teamInitialHp + ", " + teamHp + ", " + teamAlive + ", " + teamSize + ", " + enemiesInitialHp + ", " + enemiesHp + ", " + enemiesAlive + ", " + enemiesSize + ", " + maxRounds + ", " + rounds);}
		return fitness;
	}
	
	public ArrayList<Double> exponentialScaling(ArrayList<Double> orgFitness) {
		ArrayList<Double> scaledFitness = new ArrayList<Double>();
		for (int i = 0; i < orgFitness.size(); i++) {
			scaledFitness.add(sqrt(orgFitness.get(i) + 1));
		}
		return scaledFitness;
	}
	
	public double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
}
