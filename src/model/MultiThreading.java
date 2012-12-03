package model;

public class MultiThreading {
	Thread[] threads;
	int numThreads;
	
	public MultiThreading(int numThreads) {
		this.numThreads = numThreads;
		threads = new Thread[numThreads];
	}
	
	public void runThreads() {
		for (int i = 0; i < threads.length; i++) { threads[i].start(); }
		sync();
	}
	
	public void runCrossPopulationThreads(CrossPopulationThread[] crossPopulationThreads, double[][][][] population, double[][][][] newPopulation, double[] scaledFitness, double totalFitness) {
		for (int i = 0; i < numThreads; i++) {
			crossPopulationThreads[i].setVariables(population, newPopulation, scaledFitness, totalFitness);
			threads[i] = new Thread(crossPopulationThreads[i]);
		}
		runThreads();	
	}
	
	public void runGameThreads(GameThread[] gameThreads, double[][][][] team1, double[] team1Fitness) {
		for (int i = 0; i < numThreads; i++) {
			gameThreads[i].setTeam1(team1);
			gameThreads[i].setTeam1Fitness(team1Fitness);
			threads[i] = new Thread(gameThreads[i]);
		}
		runThreads();
	}
	
	public void runKeepPopulationThreads(KeepPopulationThread[] keepPopulationThreads, double[][][][] population, double[][][][] newPopulation, double[] scaledFitness, double totalFitness) {
		for (int i = 0; i < numThreads; i++) {
			keepPopulationThreads[i].setVariables(population, newPopulation, scaledFitness, totalFitness);
			threads[i] = new Thread(keepPopulationThreads[i]);
		}
		runThreads();		
	}
	
	public void runMutatePopulationThreads(MutatePopulationThread[] mutatePopulationThreads, double[][][][] population, double[][][][] newPopulation, double[] scaledFitness, double totalFitness) {
		for (int i = 0; i < numThreads; i++) {
			mutatePopulationThreads[i].setVariables(population, newPopulation, scaledFitness, totalFitness);
			threads[i] = new Thread(mutatePopulationThreads[i]);
		}
		runThreads();		
	}
	
	private void sync() {
		for (int i = 0; i < threads.length; i++) {
			try { threads[i].join(); } 
			catch(InterruptedException ex) { Thread.currentThread().interrupt(); }
		}
	}
}
