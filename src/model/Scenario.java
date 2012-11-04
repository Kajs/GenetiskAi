package model;

public class Scenario {

	Coordinate[] geneticPositions;
	Coordinate[] staticPositions;
	Ai[] staticAis;
	Ai[] geneticAis;
	
	public Scenario (Ai[] staticAis, Coordinate[] staticPositions, Ai[] geneticAis, Coordinate[] geneticPositions) {
		this.staticAis = staticAis;
		this.staticPositions = staticPositions;
		this.geneticPositions = geneticPositions;
		this.geneticAis = geneticAis;		
	}

}
