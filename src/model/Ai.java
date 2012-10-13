package model;

import static java.lang.Math.abs;

import java.awt.Color;
import java.util.ArrayList;
import model.Ai;
import model.Coordinate;

public class Ai {
	
	public Coordinate position;
    private Color color;
    private String aiType;
    private int hp;
    private int meleeDamage;
    public int stunned;
    private int team;
    
    public Ai() {
    }
    
    public Action action(ArrayList<ArrayList<Hex>> hexCake) {
    	System.out.println("action from Ai needs to be overwritten by extending classes");
    	return null;
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
	
	public int getTeam() {
		return team;
	}
	
	public void setTeam(int team) {
		this.team = team;
	}
    
    public Ai nearestEnemy(ArrayList<Ai> enemies) {
    	int size = enemies.size();
    	if (size == 0) { return null; }
    	
		Ai closest = enemies.get(0);
		int x = position.getX();
		int y = position.getY();
		int eX = closest.getPosition().getX();
		int eY = closest.getPosition().getY();
		int dx = abs(eX - x);
		int dy = abs(eY - y);
	
    	for (int i = 1; i < size; i++) {
    		Ai enemy = enemies.get(i);
    		Coordinate ePos = enemy.getPosition();
			int newDx = abs(x - ePos.getX());
			int newDy = abs(y - ePos.getY());
		
			if (newDx + newDy < dx + dy) {
				closest = enemy;
				dx = newDx;
				dy = newDy;
			}
		}
    	return closest;
    }
}
