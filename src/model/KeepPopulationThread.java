package model;

import static model.GeneticAlgorithm.nextDouble;

public class KeepPopulationThread implements Runnable {
	private double[][][][] population;
	private double[][][][] newPopulation;
	private boolean alwaysKeepBest;
	private double[] scaledFitness;
	private double totalFitness;
	private int populationLimit;
	private int information;
	private int choices;
	
	private int start;
	private int end;
	
	public KeepPopulationThread(int start, int end, boolean alwaysKeepBest, int populationLimit, int choices, int information) {
		this.start = start;
		this.end = end;
		
		this.alwaysKeepBest = alwaysKeepBest;
		this.populationLimit = populationLimit;
		this.choices = choices;
		this.information = information;
	}
	
	public void run() {
		for (int i = start; i < end; i++) {
			//System.out.println("Keep i: " + i + "__________________, (" + start + "," + end + ")");
			if(alwaysKeepBest) {newPopulation[i] = population[i];}
			else { newPopulation[i] = choseParents(1, population, scaledFitness, totalFitness, populationLimit)[0]; }
		}	
		
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
}
