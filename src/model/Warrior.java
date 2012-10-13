package model;

import java.awt.Color;

public class Warrior extends Ai {
	
	public Warrior(Coordinate startingPosition) {
		setPosition(startingPosition);
		setAiType("Warrior");
		setColor(Color.red);
		setHp(20);
		setMeleeDamage(5);
    }
}
