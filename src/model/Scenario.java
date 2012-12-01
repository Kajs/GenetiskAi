package model;

public class Scenario {

	Coordinate[][] geneticPositions;
	Coordinate[][] staticPositions;
	private boolean geneticsStart;
	
	public Scenario (Coordinate[][] geneticPositions, Coordinate[][] staticPositions, boolean geneticsStart) {
		this.geneticPositions = geneticPositions;
		this.staticPositions = staticPositions;
		this.geneticsStart = geneticsStart;
	}
	
	public boolean geneticsStart() {
		return geneticsStart;
	}

}
