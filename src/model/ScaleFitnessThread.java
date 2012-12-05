package model;

import static java.lang.Math.sqrt;

public class ScaleFitnessThread implements Runnable{
	int start;
	int end;
	int type;
	double factor;
	double constant;
	boolean skipZeroFitnessScaling;
	double[] fitness;
	double totalFitness;
	
	public ScaleFitnessThread(int start, int end, int type, double factor, double constant, boolean skipZeroFitnessScaling) {
		this.start = start;
		this.end = end;
		this.type = type;
		this.factor = factor;
		this.constant = constant;
		this.skipZeroFitnessScaling = skipZeroFitnessScaling;
	}
	
	public void run() {
		switch(type) {
		case 0:
			linearTransformationScaling();
			break;
		case 1:
			exponentialScaling();
			break;
		default:
			System.out.println("MultiThreading.runScaleFitness(): no case for type " + type);
		}
	}
	
	public void setVariables(double[] fitness) { this.fitness = fitness; }
	
	private void linearTransformationScaling() {
		double fitValue;
		for (int i = start; i < end; i++) {
			fitValue = fitness[i];
			if(skipZeroFitnessScaling && fitValue == 0) { fitness[i] = 0.0; }			
			else { fitness[i] = fitValue * factor + constant; }
		}
	}
	
	public void exponentialScaling() {
		double fitValue;
		for (int i = start; i < end; i++) {
			fitValue = fitness[i];
			
			if(skipZeroFitnessScaling && fitValue == 0) { fitness[i] = 0.0; }
			else { fitness[i] = sqrt(fitValue + constant); }
		}
	}
}
