package model;

public class HeapSortThread implements Runnable {
	private double[][][] ais;
	private double[] fitness;
	private int limit;
	private boolean highToLow;
	
	public HeapSortThread(double[][][] ais, double[] fitness, int limit, boolean highToLow) {
		this.ais = ais;
		this.fitness = fitness;
		this.limit = limit;
		this.highToLow = highToLow;
	}
	public void run() {
		if(highToLow) {	HeapSort.heapSortHigh(ais, fitness, limit);	}
		else { HeapSort.heapSortLow(ais, fitness, limit); }
	}
}
