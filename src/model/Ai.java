package model;

import static java.lang.Math.abs;

import java.awt.Color;
import java.util.ArrayList;
import model.Ai;
import model.Coordinate;

public class Ai {
	
	public Coordinate position;
    public Color color;
    public String aiType;
    public int hp;
    public int meleeDamage;
    public int stunned;
    public int team;
    public String id;
    public int[] weights;
    public Action bestAction;
	public double bestWeight;
	public double scale = 5.0;
    
    public Ai() {
    }
    
    public Action action(Hex[] adjacenHexes, ArrayList<ArrayList<Hex>> hexCake, int myTeamHp, int enemyTeamHp) {
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
		if (id != null) { System.out.println(id + ": Hp = " + hp); }
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
    
    public void setWeights(int[] weights) {
    	this.weights = weights;
    }
    
    public double getWeigth(int position) {
    	return weights[position];
    }
    
    public void setWeight(int position, int value) {
    	weights[position] = value;
    }
    
    public void w3MoveNearEnemy(Coordinate adjacentPosition, int hp, int nearestEnemyHp, int nearestEnemyDistance) {
    	if (nearestEnemyDistance != 0) {
    		double weight = hp * weights[0] + nearestEnemyHp * weights[5] + (scale / nearestEnemyDistance) * weights[8];
    		if (weight > bestWeight) {
    			bestWeight = weight;
    			bestAction = new Action(adjacentPosition, "move");
    		}    		
    	}
	}
	
	public void w4MoveNearAlly(Coordinate adjacentPosition, int hp, int nearestAllyHp, int nearestAllyDistance) {
		if (nearestAllyDistance != 0) {
			double weight = hp * weights[0] + nearestAllyHp * weights[6] * (scale / nearestAllyDistance) * weights[7];
			if (weight > bestWeight) {
				bestWeight = weight;
				bestAction = new Action(adjacentPosition, "move");
			}
		}
	}
	
	public void w5MoveAwayEnemies(Coordinate adjacentPosition, int hp, int totalEnemies, int sliceEnemies, int nearestEnemyDistance) {
		double weight = hp * weights[0] + totalEnemies * weights[9] - sliceEnemies * weights[3] + nearestEnemyDistance * weights[8];
		if (weight > bestWeight) {
			bestWeight = weight;
			bestAction = new Action(adjacentPosition, "move");
		}
	}
	
	public void w6MoveAwayAllies(Coordinate adjacentPosition, int hp, int totalAllies, int sliceAllies, int nearestAllyDistance) {
		double weight = hp * weights[0] + totalAllies * weights[10] - sliceAllies * weights[4] + nearestAllyDistance * weights[7];
		if (weight > bestWeight) {
			bestWeight = weight;
			bestAction = new Action(adjacentPosition, "move");
		}
	}
	
	public void w7MoveMostEnemies(Coordinate adjacentPosition, int sliceEnemies, int nearestEnemyDistance) {
		if (nearestEnemyDistance != 0) {
			double weight = sliceEnemies * weights[3] + (5 / nearestEnemyDistance) * weights[8];
			if (weight > bestWeight) {
				bestWeight = weight;
				bestAction = new Action(adjacentPosition, "move");
			}
		}
	}
	
	public void w8MoveMostAllies(Coordinate adjacentPosition, int sliceAllies, int nearestAllyDistance) {
		if (nearestAllyDistance != 0) {
			double weight = sliceAllies * weights[4] + (5 / nearestAllyDistance) * weights[7];
			if (weight > bestWeight) {
				bestWeight = weight;
				bestAction = new Action(adjacentPosition, "move");
			}
		}
	}
}
