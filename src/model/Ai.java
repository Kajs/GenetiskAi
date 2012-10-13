package model;

import static java.lang.Math.abs;

import java.awt.Color;
import java.util.ArrayList;
import control.Controller;
import model.Ai;
import model.Coordinate;
import control.Controller;
import static java.lang.Math.abs;

public class Ai {
	
	public Coordinate position;
    private Color color;
    private String aiType;
    private int hp;
    private int meleeDamage;
    public int stunned;
    
    public Ai() {
    }

    public Coordinate moveAction(ArrayList<ArrayList<Hex>> hexCake) {
    	for (Hex hex : hexCake.get(0)) {
    		hex.setColor(Color.black);
    	}
		return position;
	}
    
    public Coordinate getPosition() {
		return position;
	}
    
    public void setPosition(Coordinate newPos) {
    	position = newPos;
    }
    
    public Color getColor() {
		return color;
	}
    
    public void setColor(Color color) {
    	this.color = color;
    }
    
    public void setAiType(String type) {
    	aiType = type;
    }
    
    public String getAiType() {
    	return aiType;
    }    
    
    public void setHp(int newHp) {
		hp = newHp;
	}

    public int getHp() {
    	return hp;
    }
    
    public boolean isAlive() {
    	if (hp > 0) {
			return true;
		}
		else return false;
    }
    
    public int getMeleeDamage() {
    	return meleeDamage;
    }
    
    public void setMeleeDamage(int damage) {
    	meleeDamage = damage;
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
    
    public Coordinate nearestEnemy(ArrayList<Ai> enemies) {
    	if (enemies.isEmpty()) { return position; }
    	
		Ai closest = enemies.get(0);
		int fX = position.getX();
		int fY = position.getY();
		int eX = closest.getPosition().getX();
		int eY = closest.getPosition().getY();
		int dx = fX - eX;
		int dy = fY - eY;
		
    	for (Ai enemy : enemies) {
			int newDx = fX - enemy.getPosition().getX();
			int newDy = fY - enemy.getPosition().getY();
		
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
    		int newDx = abs(coordinate.getX() - closest.getPosition().getX());
    		int newDy = abs(coordinate.getY() - closest.getPosition().getY());
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
