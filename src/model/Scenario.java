package model;

public class Scenario {

	Coordinate[][] geneticPositions;
	Coordinate[][] staticPositions;
	
	public Scenario (Coordinate[][] geneticPositions, Coordinate[][] staticPositions) {
		this.geneticPositions = geneticPositions;
		this.staticPositions = staticPositions;
	}
}
