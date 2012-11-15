package model;

public class CopyArrayThread implements Runnable {
	
	private double[] orgArray;
	private double[] copy;
	private int start;
	private int end;
	
	public CopyArrayThread(double[] orgArray, double[] copy, int start, int end) {
		this.orgArray = orgArray;
		this.copy = copy;
		this.start = start;
		this.end = end;
	}	
	
	public void run() {
		for (int i = start; i < end; i++) {
			copy[i] = orgArray[i];
		}
	}
}
