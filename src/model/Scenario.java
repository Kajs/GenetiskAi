package model;

public class Scenario {

	Coordinate[][] geneticPositions;
	Coordinate[][] staticPositions;
	//int[] aiTypes;
	
	public Scenario (Coordinate[][] geneticPositions, Coordinate[][] staticPositions) {
		//this.aiTypes = aiTypes;
		this.geneticPositions = geneticPositions;
		this.staticPositions = staticPositions;
	}

}
