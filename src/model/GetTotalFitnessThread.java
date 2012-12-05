package model;

public class GetTotalFitnessThread implements Runnable{
	int start;
	int end;
	double[] scaledFitness;
	double totalFitness;
	
	public GetTotalFitnessThread(int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	public void run() {
		totalFitness = 0;
		for (int i = start; i < end; i++) { totalFitness += scaledFitness[i]; }
	}
	
	public void setVariables(double[] scaledFitness) { this.scaledFitness = scaledFitness; }
	
	public double getTotalFitness() { return totalFitness; }
}
