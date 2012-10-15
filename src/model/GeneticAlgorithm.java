package model;

import java.util.ArrayList;
import java.util.Random;

public class GeneticAlgorithm {
	private int weights;
	private Random randomGenerator = new Random(); 
	
	
	public GeneticAlgorithm () {
		
	}
	
	public int[] generateWeights(int numberOfWeights) {
		weights=numberOfWeights;
		
		int[] weights = new int[numberOfWeights];
		
		
		for (int i=0; i<numberOfWeights; i++) {
			weights[i] = randomGenerator.nextInt(2)-1;
		}
		
		return weights;
	}
	
	public int[] crossover (int[] dad, int[] mom, int crossoverPoint) {
		int[] child = new int[weights];
		for (int i = 0; i<crossoverPoint; i++) {
			child[i] = dad[i];
					}
		for (int i = crossoverPoint; i<weights; i++) {
			child[i] = mom[i];
		}
		
		return child;
	}
	
	public void mutate (int[] child) {
		int positionToMutate = randomGenerator.nextInt(weights-1);
		int valueToMutate = child[positionToMutate];
		
		int upOrDown = randomGenerator.nextInt(1);
		if (valueToMutate == -1) {
			if (upOrDown == 0) {
				valueToMutate = 1;
			} else { 
				valueToMutate = 0; 
				}
		} 
		if (valueToMutate == 0) {
			if (upOrDown == 0) {
				valueToMutate = -1;
			} else { 
				valueToMutate = 1; 
				}
		} 
		if (valueToMutate == 1) {
			if (upOrDown == 0) {
				valueToMutate = 0;
			} else { 
				valueToMutate = -1; 
				}
		}
		child[positionToMutate] = valueToMutate;
	}
	
	public double fitness (int teamInitialHp, int teamHp, int teamAlive, int teamSize, int enemiesInitialHp, int enemiesHp, int enemiesAlive, int enemiesSize) {
		double fitness = (teamHp/teamInitialHp)*(teamAlive/teamSize);
		fitness = fitness + ((enemiesInitialHp-enemiesHp)/enemiesInitialHp)*((enemiesSize-enemiesAlive)/enemiesSize); 
		return fitness;
	}
	
	public ArrayList<Ai> chooseParents(int numberOfParents, ArrayList<Ai> ais, ArrayList<Double> fitness) {
		HeapSort.heapSort(ais, fitness);
		ArrayList<Ai> parents = new ArrayList<Ai>();
		double totalFitness = 0;
		int parentsFound = 0;
		int arraySize = fitness.size();
		
		for (int i = 0; i < arraySize; i++) {
			totalFitness = totalFitness + fitness.get(i);
		}
		
		while(parentsFound < numberOfParents) {
			int position = randomGenerator.nextInt(arraySize - 1);
			double chanceRoll = randomGenerator.nextDouble();
			double probability = fitness.get(position) / totalFitness;
			if (chanceRoll <= probability) {
				parents.add(ais.get(position));
			}
			
		}
		
		return parents;
	}
}
