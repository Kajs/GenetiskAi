package model;

import static java.lang.Math.floor;
import static java.lang.Math.pow;
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
	boolean cutOffUniqueValues;
	double cutOffDecimal;
	double[] fitness;
	double totalFitness;
	
	public ScaleFitnessThread(int start, int end, int type, double factor, double constant, boolean skipZeroFitnessScaling, boolean preferUniqueBest, double preferUniqueBestFactor, boolean cutOffUniqueValues, double cutOffDecimal) {
		this.start = start;
		this.end = end;
		this.type = type;
		this.factor = factor;
		this.constant = constant;
		this.skipZeroFitnessScaling = skipZeroFitnessScaling;
		this.preferUniqueBest = preferUniqueBest;
		this.preferUniqueBestFactor = preferUniqueBestFactor;
		this.cutOffUniqueValues = cutOffUniqueValues;
		this.cutOffDecimal = cutOffDecimal;
	}
	
	public void run() { scale(); }
	
	public void setVariables(double[] fitness) { this.fitness = fitness; }
	
	private void scale() {
		double fitValue;
		double lastValue = Double.MIN_VALUE;
		boolean unique = true;
		for (int i = start; i < end; i++) {
			fitValue = fitness[i];
			if (cutOffUniqueValues) { unique = (floor(fitValue * pow(10, cutOffDecimal)) != floor(lastValue * pow(10, cutOffDecimal))); }
			else { unique = (fitValue != lastValue); }
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
