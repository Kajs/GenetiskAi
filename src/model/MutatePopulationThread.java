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
	private double updateStepSize = 0.05 / GeneticAlgorithm.geneticThreads;
	private int updateCounter = 0;
	private double updateCeiling;
	private double updateCeilingStepSize;
	public boolean updateMutateLikelihood = false;
	private double drasticLikelihoodStart;
	private double drasticLikelihoodEnd;
	
	public MutatePopulationThread(int start, int end, int populationLimit, int choices, int information, double mutateLikelihoodStart, double mutateLikelihoodEnd, double drasticLikelihoodStart, double drasticLikelihoodEnd) {
		this.start = start;
		this.end = end;
		this.mutateLikelihoodStart = mutateLikelihoodStart;
		this.mutateLikelihoodEnd = mutateLikelihoodEnd;
		this.drasticLikelihoodStart = drasticLikelihoodStart;
		this.drasticLikelihoodEnd = drasticLikelihoodEnd;
		
		this.populationLimit = populationLimit;
		this.choices = choices;
		this.information = information;
		
		updateCeilingStepSize = 0.25 / GeneticAlgorithm.geneticThreads;
		updateCeiling = mutateLikelihoodStart + updateCeilingStepSize;
	}
	
	public void run() {

		double newMutateLikelihood = mutateLikelihoodStart;
		if (updateMutateLikelihood) { increaseMutateLikelihood(); }
		double mutateLikelihoodStepSize = (mutateLikelihoodEnd - (mutateLikelihoodStart + updateCounter * updateStepSize / GeneticAlgorithm.geneticThreads)) / (end - start);
		
		double temp = drasticLikelihoodStart;
		drasticLikelihoodStart = drasticLikelihoodEnd;
		drasticLikelihoodEnd = temp;
		
		double drasticStepSize = (drasticLikelihoodEnd - drasticLikelihoodStart) / (end - start);
		double newDrasticLikelihood = drasticLikelihoodStart;

		for (int i = start; i < end; i++) {
			//System.out.println("Mutate i: " + i + "__________________, end: " + end);
			double[][][][] mutant = choseParents(1, population, scaledFitness, totalFitness, populationLimit);
			
			newMutateLikelihood += mutateLikelihoodStepSize;
			newPopulation[i] = mutate(mutant[0], newMutateLikelihood + updateCounter * updateStepSize, newDrasticLikelihood, coinFlip());
			newDrasticLikelihood += drasticStepSize;
		}
	}
	
	public double[][][] mutate (double[][][] child, double mutateLikelihood, double drasticLikelihood, boolean wholeTeam) {
		double[][][] mutant = new double[3][choices+1][information];
		int aiType = nextInt(3);
		
		for (int t = 0; t < 3; t++) {				
			for (int i = 0; i < choices + 1; i++) {
				for (int j = 0; j < information; j++) {
					boolean mutate = nextDouble() <= mutateLikelihood;
					
					if(mutate && (wholeTeam || !wholeTeam && aiType == t)) {
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
	
	private void increaseMutateLikelihood() {
		updateCounter++;
		double newStepSize = (mutateLikelihoodEnd - (mutateLikelihoodStart + updateCounter * updateStepSize)) / (end - start);
		if ((mutateLikelihoodStart + newStepSize + updateCounter * updateStepSize) / mutateLikelihoodEnd > updateCeiling) { resetMutateLikelihood(true); }
		updateMutateLikelihood = false;
	}
	
	public void updateMutateLikelihood() { updateMutateLikelihood = true; }
	
	public void resetMutateLikelihood(boolean limitReached) { 
		updateCounter = 0;
		if (limitReached) {
			if (updateCeiling == mutateLikelihoodEnd) { updateCeiling = mutateLikelihoodStart + updateCeilingStepSize; }
			else {
				updateCeiling += updateCeilingStepSize;
				if (updateCeiling > mutateLikelihoodEnd) { updateCeiling = mutateLikelihoodEnd; }
			}
		}
		else { updateCeiling = mutateLikelihoodStart + updateCeilingStepSize; };
	}
}
