package model;

import java.util.ArrayList;
import java.util.Random;
import static java.lang.Math.floor;
import static java.lang.Math.sqrt;

public class GeneticAlgorithm {
	private int choices;
	private int information;
	private Random randomGenerator = new Random(); 
	
	
	public GeneticAlgorithm () {
		
	}
	
	public ArrayList<ArrayList<Ai>> initialPopulation(int size, int choices, int information) {
		ArrayList<ArrayList<Ai>> population = new ArrayList<ArrayList<Ai>>();
		ArrayList<Ai> warriors = new ArrayList<Ai>();
		ArrayList<Ai> wizards = new ArrayList<Ai>();
		ArrayList<Ai> clerics = new ArrayList<Ai>();
		
		for (int i = 0; i < size; i++) {
			warriors.add(new Warrior(generateWeights(choices, information)));
			wizards.add(new Warrior(generateWeights(choices, information))); //needs change to wizards
			clerics.add(new Warrior(generateWeights(choices, information)));  //needs change to clerics
		}
		
		population.add(warriors);
		population.add(wizards);
		population.add(clerics);
		
		return population;
	}
	
	public double[][] generateWeights(int choices, int information) {
		this.choices = choices;
		this.information = information;
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
	
	public ArrayList<Ai> newPopulation(ArrayList<Ai> ais, ArrayList<Double> fitness, double keepPercent, double crossPercent, double drasticLikelihood, double mutateLikelihood) {
		int keepAmount = (int)floor((double)ais.size() * keepPercent);
		int crossAmount = (int)floor((double)ais.size() * crossPercent);
		crossAmount = crossAmount - (crossAmount % 2);
		int mutateAmount = ais.size() - keepAmount - crossAmount;
		
		ArrayList<Ai> newPopulation = new ArrayList<Ai>();
		
		ArrayList<Double> scaledFitness = fitness;
		double totalFitness = 0;
		for (int i = 0; i < scaledFitness.size(); i++) {
			totalFitness = totalFitness + scaledFitness.get(i);
		}
		
		ArrayList<Ai> carryOver = choseParents(keepAmount, ais, scaledFitness, totalFitness);
		for (Ai ai : carryOver) {
			if (ai.getAiType().equals("Warrior")) { 
				newPopulation.add(new Warrior(ai.getWeights()));
			}
			/*
			if (ai.getAiType().equals("Wizard")) { 
				newPopulation.add(new Wizard(ai.getWeights()));  
			}
			if (ai.getAiType().equals("Cleric")) { 
			    newPopulation.add(new Cleric(ai.getWeights())); 
			}
			*/
		}	
		
		for(int i = 0; i < crossAmount/2; i++) {
			ArrayList<Ai> parents = choseParents(2, ais, scaledFitness, totalFitness);
			
			double[][] child1 = crossover(parents.get(0).getWeights(), parents.get(0).getWeights());
			double[][] child2 = crossover(parents.get(0).getWeights(), parents.get(1).getWeights());
			if (parents.get(0).getAiType().equals("Warrior")) { 
				newPopulation.add(new Warrior(child1));              //crossover population
				newPopulation.add(new Warrior(child2));
			}
			/*
			if (parents.get(0).getAiType().equals("Wizard")) { 
				newPopulation.add(new Wizard(child1));
				newPopulation.add(new Wizard(child2));  
			}
			if (parents.get(0).getAiType().equals("Cleric")) { 
				newPopulation.add(new Cleric(child1));
				newPopulation.add(new Cleric(child2)); 
			}
				*/
		}
		ArrayList<Ai> mutants = choseParents(mutateAmount, ais, scaledFitness, totalFitness);
		for (Ai ai : mutants) {
			double[][] mutant = mutate(ai.getWeights(), drasticLikelihood, mutateLikelihood);
			if (ai.getAiType().equals("Warrior")) { 
				newPopulation.add(new Warrior(mutant));
			}
			/*
			if (ai.getAiType().equals("Wizard")) { 
				newPopulation.add(new Wizard(mutant));  
			}
			if (ai.getAiType().equals("Cleric")) { 
			    newPopulation.add(new Cleric(mutant)); 
			}
			*/
		}	
		return newPopulation;
	}
	
	public ArrayList<Ai> choseParents(int numberOfParents, ArrayList<Ai> ais, ArrayList<Double> fitness, double totalFitness) {
		ArrayList<Ai> parents = new ArrayList<Ai>();
		int parentsFound = 0;
		
		while(parentsFound < numberOfParents) {
			double chance = randomGenerator.nextDouble();
			double summedFitness = 0.0;
			for (int i = 0; i < ais.size(); i++) {
				summedFitness = summedFitness + fitness.get(i);
				if (chance <= summedFitness / totalFitness) {
					parents.add(ais.get(i));
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
}
