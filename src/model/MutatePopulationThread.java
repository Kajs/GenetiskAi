package model;

import java.util.Random;

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
	final Random randomGenerator = new Random();
	
	private double mutateLikelihoodStart;
	private double mutateLikelihoodEnd;
	
	public MutatePopulationThread(int start, int end, int populationLimit, int choices, int information, double mutateLikelihoodStart, double mutateLikelihoodEnd) {
		this.start = start;
		this.end = end;
		this.mutateLikelihoodStart = mutateLikelihoodStart;
		this.mutateLikelihoodEnd = mutateLikelihoodEnd;
		
		this.populationLimit = populationLimit;
		this.choices = choices;
		this.information = information;
	}
	
	public void run() {

		double mutateAmount = (end - start);
		double stepSize = (mutateLikelihoodEnd - mutateLikelihoodStart) / mutateAmount;
		double newMutateLikelihood = mutateLikelihoodStart;

		for (int i = start; i < end; i++) {
			//System.out.println("Mutate i: " + i + "__________________, end: " + end);
			double[][][][] mutant = choseParents(1, population, scaledFitness, totalFitness, populationLimit);
			
			newPopulation[i] = mutate(mutant[0], newMutateLikelihood, coinFlip());
			newMutateLikelihood += stepSize;
			//System.out.println("MutateLikelihood %: " + newMutateLikelihood);
		}
	}
	
	public double[][][] mutate (double[][][] child, double mutateLikelihood, boolean wholeTeam) {
		double[][][] mutant = new double[3][choices+1][information];
		int aiType = nextInt(3);
		
		for (int t = 0; t < 3; t++) {				
			for (int i = 0; i < choices + 1; i++) {
				for (int j = 0; j < information; j++) {
					boolean mutate = nextDouble() <= mutateLikelihood;
					
					if(mutate && (wholeTeam || !wholeTeam && aiType == t)) {
						double drasticLikelihood = nextDouble();
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
	
    private boolean coinFlip() { return randomGenerator.nextDouble() <= 0.5; }
	
	private double nextDouble() { return randomGenerator.nextDouble(); }
	
	private int nextInt(int val) { return randomGenerator.nextInt(val); }
}
