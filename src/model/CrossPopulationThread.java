package model;

import java.util.Random;

public class CrossPopulationThread implements Runnable {
		private double[][][][] population;
		private double[][][][] newPopulation;
		private double[] scaledFitness;
		private double totalFitness;
		private int populationLimit;
		private int information;
		private int choices;
		
		private int start;
		private int end;
		final Random randomGenerator = new Random();
		
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
			double[][][][] parents = choseParents(2, population, scaledFitness, totalFitness, populationLimit);			
			double[][][] child1 = crossOver(parents[0], parents[1], true);
			double[][][] child2 = crossOver(parents[1], parents[0], false);
			
			newPopulation[i] = child1;
			newPopulation[i + 1] = child2;
		}
	}
	
	public double[][][] crossOver (double[][][] dad, double[][][] mom, boolean wholeTeam) {
		double[][][] child = new double[3][choices + 1][information];
		int aiType = nextInt(3);
		
		for (int t = 0; t < 3; t++) {
			for (int i = 0; i < choices + 1; i++) {
				for (int j = 0; j < information; j++) {
					double value;
					
					if (coinFlip() && (wholeTeam || !wholeTeam && t == aiType)) {value = dad[t][i][j];	}
					else { value = mom[t][i][j]; }					
					child[t][i][j] = value;
				}
			}
		}	
		return child;
	}
	
	public void setVariables(double[][][][] population, double[][][][] newPopulation, double[] scaledFitness, double totalFitness) {
		this.population = population;
		this.newPopulation = newPopulation;
		this.scaledFitness = scaledFitness;
		this.totalFitness = totalFitness;
	}
	
	public double[][][][] choseParents(int numberOfParents, double[][][][] population, double[] fitness, double totalFitness, int populationLimit) {
		double[][][][] parents = new double[numberOfParents][3][choices+1][information];
		int parentsFound = 0;
		
		while(parentsFound < numberOfParents) {
			double chance = nextDouble();
			double summedFitness = 0.0;
			for (int i = 0; i < populationLimit; i++) {
				summedFitness += fitness[i];
				if (chance <= summedFitness / totalFitness) {
					parents[parentsFound++] = population[i];
					break;
				}
			}
		}		
		return parents;
	}
	
    private boolean coinFlip() { return randomGenerator.nextDouble() <= 0.5; }
	
	private double nextDouble() { return randomGenerator.nextDouble(); }
	
	private int nextInt(int val) { return randomGenerator.nextInt(val); }
	
	
}
