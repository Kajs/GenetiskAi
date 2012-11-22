package model;

import java.util.Random;

public class CrossPopulationThread implements Runnable {
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
		
		public CrossPopulationThread(int start, int end, int populationLimit, int choices, int information) {
			this.start = start;
			this.end = end;
			
			this.populationLimit = populationLimit;
			this.choices = choices;
			this.information = information;
		}
	
	public void run() {
		for(int i = start; i < end; i = i + 2) {
			//System.out.println("Cross i: " + i + "__________________, (" + start + "," + end + ")");
			double[][][] parents = choseParents(2, population, scaledFitness, totalFitness, populationLimit);			
			double[][] child1 = crossover(parents[0], parents[1]);
			double[][] child2 = crossover(parents[1], parents[0]);
			
			newPopulation[i] = child1;
			newPopulation[i + 1] = child2;
		}
	}
	
	public double[][] crossover (double[][] dad, double[][] mom) {
		double[][] child = new double[choices + 1][information];
		for (int i = 0; i < choices + 1; i++) {
			for (int j = 0; j < information; j++) {
				if (coinFlip()) {child[i][j] = dad[i][j];
				}
				else {
					child[i][j] = mom[i][j];
				}
			}
		}		
		return child;
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
					parentsFound = parentsFound + 1;
					break;
				}
			}
		}		
		return parents;
	}
	
	private boolean coinFlip() { return randomGenerator.nextDouble() <= 0.5; }
}
