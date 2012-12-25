package model;

import static java.lang.Math.sqrt;

public class ScaleFitnessThread implements Runnable{
	int start;
	int end;
	int type;
	double factor;
	double constant;
	boolean skipZeroFitnessScaling;
	boolean preferUniqueBest;
	double preferUniqueBestFactor;
	double[] fitness;
	double totalFitness;
	
	public ScaleFitnessThread(int start, int end, int type, double factor, double constant, boolean skipZeroFitnessScaling, boolean preferUniqueBest, double preferUniqueBestFactor) {
		this.start = start;
		this.end = end;
		this.type = type;
		this.factor = factor;
		this.constant = constant;
		this.skipZeroFitnessScaling = skipZeroFitnessScaling;
		this.preferUniqueBest = preferUniqueBest;
		this.preferUniqueBestFactor = preferUniqueBestFactor;
	}
	
	public void run() { scale(); }
	
	public void setVariables(double[] fitness) { this.fitness = fitness; }
	
	private void scale() {
		double fitValue;
		double lastValue = Double.MIN_VALUE;
		boolean unique = true;
		for (int i = start; i < end; i++) {
			fitValue = fitness[i];
			unique = (fitValue != lastValue);
			lastValue = fitValue;
			
			if(preferUniqueBest && !unique) { fitValue = fitValue * preferUniqueBestFactor; }
			if(skipZeroFitnessScaling && fitValue == 0) { fitness[i] = 0.0; }			
			else { 
				switch(type) {
				case 0:
					fitness[i] = fitValue * factor + constant;    //linear
					break;
				case 1:
					fitness[i] = sqrt(fitValue + constant);       //exponential
					break;
				default:
					System.out.println("MultiThreading.runScaleFitness(): no case for type " + type);
				}
			}
		}
	}
}
