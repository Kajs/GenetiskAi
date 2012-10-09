package model;

import java.awt.Color;
import java.util.ArrayList;

import control.Controller;
import static java.lang.Math.abs;

public class Wizard implements Ai {
	
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

    public Coordinate moveAction(ArrayList<Ai> enemies) {		
		return nearestEnemy(enemies);
	}

	public void setStunned(int stunRounds) {
		stunned = stunRounds;
	}

	public boolean getStunned() {
		if (stunned > 0) {
			stunned = stunned - 1;
			return true;
		}
		else {
			return false;
		}
	}

	public Coordinate getPosition() {
		return position;
	}

	public Color getColor() {
		return color;
	}

	public boolean setHp(int newHp) {
		hp = newHp;
		if (hp > 0) {
			return true;
		}
		else return false;
	}

    public int getHp() {
    	return hp;
    }
    
    public Coordinate nearestEnemy(ArrayList<Ai> enemies) {
    	if (enemies.isEmpty()) { return position; }
    	
		Ai closest = enemies.get(0);
		int fX = position.getIntX();
		int fY = position.getIntY();
		int eX = closest.getPosition().getIntX();
		int eY = closest.getPosition().getIntY();
		int dx = fX - eX;
		int dy = fY - eY;
		
    	for (Ai enemy : enemies) {
			int newDx = fX - enemy.getPosition().getIntX();
			int newDy = fY - enemy.getPosition().getIntY();
		
			if (abs(newDx) + abs(newDy) < abs(dx) + abs(dy)) {
				closest = enemy;
				dx = newDx;
				dy = newDy;
			}
		}
    	int x = abs(dx);
    	int y = abs(dy);
    	if (x + y < 1) { return position; }

    	for (Coordinate coordinate : freeCoordinates(fX, fY)) {
    		int newDx = abs(coordinate.getIntX() - closest.getPosition().getIntX());
    		int newDy = abs(coordinate.getIntY() - closest.getPosition().getIntY());
    		if (x + y > newDx + newDy) {
    			x = newDx;
    			y = newDy;
    		    position = coordinate;
    		}
    	}
    	return position;
    }
    
    private ArrayList<Coordinate> freeCoordinates(int x, int y) {
    	ArrayList<Coordinate> candidates = new ArrayList<Coordinate>();
    	Coordinate pos1 = new Coordinate(x + 1, y);
    	if (Controller.isOccupied(pos1) == 0) { candidates.add(pos1); }
    	Coordinate pos2 = new Coordinate(x - 1, y);
    	if (Controller.isOccupied(pos2) == 0) { candidates.add(pos2); }
    	Coordinate pos3 = new Coordinate(x, y + 1);
    	if (Controller.isOccupied(pos3) == 0) { candidates.add(pos3); }
    	Coordinate pos4 = new Coordinate(x, y - 1);
    	if (Controller.isOccupied(pos4) == 0) { candidates.add(pos4); }
    	
    	if (y % 2 == 0) {
    	    Coordinate pos5 = new Coordinate(x - 1, y - 1);
    	    if (Controller.isOccupied(pos5) == 0) { candidates.add(pos5); }
    	    Coordinate pos6 = new Coordinate(x - 1, y + 1);
    	    if (Controller.isOccupied(pos6) == 0) { candidates.add(pos6); }
    	}
    	else {
    		Coordinate pos5 = new Coordinate(x + 1, y + 1);
    	    if (Controller.isOccupied(pos5) == 0) { candidates.add(pos5); }
    	    Coordinate pos6 = new Coordinate(x + 1, y + 1);
    	    if (Controller.isOccupied(pos6) == 0) { candidates.add(pos6); }
    	}
    	return candidates;
    }
}
