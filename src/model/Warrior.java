package model;

import java.awt.Color;
import java.util.ArrayList;

import model.Ai;
import model.Coordinate;
import model.OffensiveAction;

import control.Controller;
import static java.lang.Math.abs;

public class Warrior extends Ai {
	
	public Coordinate position;
    private Color color;
    private String aiType;
    private int hp;
    private int meleeDamage;
    public int stunned;
    
    public Warrior(Coordinate startingPosition) {
	    position = startingPosition;
	    aiType = "Warrior";
	    color = Color.red;
	    hp = 20;
	    meleeDamage = 5;
    }
    
    public OffensiveAction getOffensiveAction() {
    	return new OffensiveAction("melee", new Coordinate(1, 1), meleeDamage, false);
    }

}
