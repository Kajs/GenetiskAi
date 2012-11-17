package model;

import java.util.Random;

public class MutatePopulationThread implements Runnable {
	private double[][][] population;
	private double[][][] newPopulation;
	private double[] scaledFitness;
	private double totalFitness;
	private int populationLimit;
	private int information;
	private int choices;
	private Random randomGenerator = new Random();
	
	private int start;
	private int end;
	private double drasticStart;
	private double drasticEnd;
	private double mutateLikelihood;
	
	public MutatePopulationThread(int start, int end, int populationLimit, int choices, int information, double drasticStart, double drasticEnd, double mutateLikelihood) {
		this.start = start;
		this.end = end;
		this.drasticStart = drasticStart;
		this.drasticEnd = drasticEnd;
		this.mutateLikelihood = mutateLikelihood;
		
		this.populationLimit = populationLimit;
		this.choices = choices;
		this.information = information;
	}
	
	public void run() {
		double stepSize = (drasticEnd - drasticStart) / (end - start);
		double newDrasticLikelihood = drasticStart;

		for (int i = start; i < end; i++) {
			//System.out.println("Mutate i: " + i + "__________________, end: " + end);
			if(i == end - 1) { newDrasticLikelihood = drasticEnd; }
			double[][][] mutant = choseParents(1, population, scaledFitness, totalFitness, populationLimit);
			newPopulation[i] = mutate(mutant[0], newDrasticLikelihood, mutateLikelihood);
			newDrasticLikelihood = newDrasticLikelihood + stepSize;
		}
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
	
	public void setVariables(double[][][] population, double[][][] newPopulation, double[] scaledFitness, double totalFitness) {
		this.population = population;
		this.newPopulation = newPopulation;
		this.scaledFitness = scaledFitness;
		this.totalFitness = totalFitness;
	}
	
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

}