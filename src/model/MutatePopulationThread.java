package model;

import static model.GeneticAlgorithm.coinFlip;
import static model.GeneticAlgorithm.nextDouble;
import static model.GeneticAlgorithm.nextInt;

public class MutatePopulationThread implements Runnable {
	private double[][][][] population;
	private double[][][][] newPopulation;
	private double[] scaledFitness;
	private double totalFitness;
	private int populationLimit;
	private int information;
	private int choices;
	
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
		
        if(coinFlip()) {
			double temp = drasticEnd;
			drasticEnd = drasticStart;
			drasticStart = temp;
		}
		
		double stepSize = (drasticEnd - drasticStart) / (end - start);
		double newDrasticLikelihood = drasticStart;

		for (int i = start; i < end; i++) {
			//System.out.println("Mutate i: " + i + "__________________, end: " + end);
			if(i == end - 1) { newDrasticLikelihood = drasticEnd; }
			double[][][][] mutant = choseParents(1, population, scaledFitness, totalFitness, populationLimit);
			newPopulation[i] = mutate(mutant[0], newDrasticLikelihood, mutateLikelihood, coinFlip());
			newDrasticLikelihood += stepSize;
		}
	}
	
	public double[][][] mutate (double[][][] child, double drasticLikelihood, double mutateLikelihood, boolean wholeTeam) {
		double[][][] mutant = new double[3][choices+1][information];
		int aiType = nextInt(3);
		
		for (int t = 0; t < 3; t++) {				
			for (int i = 0; i < choices + 1; i++) {
				for (int j = 0; j < information; j++) {
					boolean mutate = nextDouble() <= mutateLikelihood;
					
					if(mutate && (wholeTeam || !wholeTeam && aiType == t)) {
						drasticLikelihood = nextDouble();
						boolean drasticMutation = nextDouble() <= drasticLikelihood;
						double value;
						
						if(drasticMutation) { value = mutateDrastic(); }
						else { value = mutateLight(child[t][i][j]);	}						
						mutant[t][i][j] = value;
					}	
					else { mutant[t][i][j] = child[t][i][j]; }
				}
			}
		}
		
		return mutant;
	}
	
	public double mutateDrastic() {
		double value = nextDouble();
		if (coinFlip()) { value = value * (-1.0); }
		return value;
	}
	
	public double mutateLight(double value) {
		double mutatePercentage = nextDouble() * 0.1;
		if (coinFlip()) {
			value = value * (1.0 + mutatePercentage);
			if (value > 1.0) { value = 1.0;	}
			if (value < -1.0) { value = -1.0; }
		}
		else { value = value * (1.0 - mutatePercentage); }
		return value;
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
