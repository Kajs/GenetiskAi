package model;

import java.awt.Color;
import java.util.ArrayList;

public interface Ai {
	
	public Coordinate moveAction(ArrayList<Ai> enemies);
	
	public OffensiveAction getOffensiveAction();
	
	public Coordinate getPosition();
	
	public Color getColor();
	
	public boolean setHp(int newHp);
	
	public int getHp();
	
	public boolean getStunned();
	
	public void setStunned(int stunRounds);
	
	public Coordinate nearestEnemy(ArrayList<Ai> enemies);
}
