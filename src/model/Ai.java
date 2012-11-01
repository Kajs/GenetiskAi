package model;

import static java.lang.Math.abs;
import java.awt.Color;
import java.util.ArrayList;
import model.Ai;
import model.Coordinate;
import control.Launcher;

public class Ai {
	
	int aActions = 2;
	int sActions = 1;
	int mActions = 6;
	
	public Coordinate position;
    public Color color;
    public String aiType;
    public double hp;
    public double meleeDamage;
    public boolean stunned;
    public boolean shielded;
    public int team;
    public String id;
    public double[][] weightMatrix;
    public Action bestAction;
	public double bestWeight;
	public String supportAction;
	public double standardMeleeDamage;
	public double initialHp;
    
    public Ai() {
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
    
    public void setHp(double newHp) {
		hp = newHp;
		if(id != null && Launcher.allowHpOutput) { System.out.println(aiType + ", team " + team + " at (" + position.getX() + ", " + position.getY() + "): hp = " + hp);}
	}

    public double getHp() {
    	return hp;
    }
    
    public boolean isAlive() {
    	return hp > 0;
    }
    
    public double getMeleeDamage() {
    	return meleeDamage;
    }
    
    public void setMeleeDamage(double damage) {
    	meleeDamage = damage;
    }

	public void setStunned(boolean status) {
		if(status == false && Launcher.allowStunOutput) {
			System.out.println(id + " is stunned");
		}
		stunned = status;
	}

	public boolean getStunned() {
		return stunned;
	}
	
	public void setShielded(boolean status) {
		if (!status && Launcher.allowShieldOutput) { System.out.println(id + " lost shield"); }
		shielded = status;
	}
	
	public boolean getShielded() {
		return shielded;
	}
	
	public int getTeam() {
		return team;
	}
	
	public void setTeam(int team) {
		this.team = team;
	}
    
	public void setSupportAction(String action) {
		supportAction = action;
	}
	
	public String getSupportAction() {
		return supportAction;
	}
	
	public double getInitialHp() {
		return initialHp;
	}
	
    public Ai nearestAi(ArrayList<Ai> ais) {
    	int size = ais.size();
    	if (size == 0) { return null; }
    	
		Ai closest = ais.get(0);
		int x = position.getX();
		int y = position.getY();
		int eX = closest.getPosition().getX();
		int eY = closest.getPosition().getY();
		int dx = abs(eX - x);
		int dy = abs(eY - y);
	
    	for (int i = 1; i < size; i++) {
    		Ai enemy = ais.get(i);
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
    
    public void generateId() {
    	id = aiType + ", " + "team " + getTeam() + " from (" + position.getX() + "," + position.getY() + ")";
    }
    
    public String getId() {
    	return id;
    }
    
    public void setWeights(double[][] weights) {
    	weightMatrix = weights;
    }
    
    public double[][] getWeights() {
    	return weightMatrix;
    }
    
    public Action action(Hex[] adjacentHexes, ArrayList<ArrayList<Hex>> hexCake, double myTeamHp, double enemyTeamHp, double totalEnemies, double totalAllies) 
    {
    	System.out.println("Ai.Action() should be overwritten by extending classes");
    	return new Action(new Coordinate(0, 0), "standard", "Ai");
    }
}
