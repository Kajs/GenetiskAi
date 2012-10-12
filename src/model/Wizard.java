package model;

import java.awt.Color;
import java.util.ArrayList;

import model.Ai;
import model.Coordinate;
import model.OffensiveAction;

import control.Controller;
import static java.lang.Math.abs;

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
    
    public OffensiveAction getOffensiveAction() {
    	return new OffensiveAction("melee", new Coordinate(1, 1), meleeDamage, false);
    }
}
