package model;

import java.awt.Color;

public class Wizard extends Ai {
	
	public Coordinate position;
    final Color color;
    final String aiType;
    private int hp;
    private int meleeDamage;
    public int stunned;
	
	public Wizard(Coordinate startingPosition) {
	    position = startingPosition;
	    aiType = "Wizard";
	    color = Color.green;
	    hp = 20;
	    meleeDamage = 5;
    }
}
