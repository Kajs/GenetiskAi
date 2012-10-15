package model;

import java.util.ArrayList;
import static java.lang.Math.floor;



public class HeapSort {
	
	private static void buildMaxHeap(ArrayList<Ai> ais, ArrayList<Double> fitness) {
		int heapSize = ais.size();
		for (int i  = (int)floor(heapSize/2); i>0; i--) {
			maxHeapify(ais, fitness, i, heapSize);
		}
	}

	private static void maxHeapify(ArrayList<Ai> ais, ArrayList<Double> fitness, int i, int heapSize) {
		int largest;
		if (i < heapSize && i > 0) {
			int l = i * 2;
			int r = l + 1;
			if (l <= heapSize && fitness.get(l-1) > fitness.get(i-1)) {
				largest = l; 
			} else {
				largest = i;
			}
			if (r <= heapSize && fitness.get(r-1) > fitness.get(largest-1)) {
				largest = r;
			}
			if (largest != i) {
				exchange(ais, fitness, i, largest);
				maxHeapify(ais, fitness, largest, heapSize);
			}
		}
	}
	
	private static void exchange (ArrayList<Ai> ais, ArrayList<Double> fitness, int i, int largest) {
		Ai tempAi = ais.get(i-1);
		double tempFitness = fitness.get(i-1);
		
		ais.set(i-1, ais.get(largest-1));
		fitness.set(i-1, fitness.get(largest-1));
		
		ais.set(largest-1, tempAi);
		fitness.set(largest-1, tempFitness);
	}
	
	public static void heapSort(ArrayList<Ai> ais, ArrayList<Double> fitness) {
		int heapSize = ais.size();
		
		buildMaxHeap(ais, fitness);
		
		for (int l = heapSize; l>1 ; l--) {
			exchange(ais, fitness, 1, l);
			heapSize--;
			maxHeapify(ais, fitness, 1, heapSize);
		}
	}
}
