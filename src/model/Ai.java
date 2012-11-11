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
    public double areaDamage;
    public double healAmount;
    public boolean stunned;
    public boolean shielded;
    public boolean boosted;
    public double boostFactor = 0.5;
    public int team;
    public String startId;
    public double[][] weightMatrix;
    public Action bestAction;
	public double bestWeight;
	public String supportAction;
	public double standardMeleeDamage;
	public double initialHp;
	public Ai copy;
    
    public Ai() {
    }
    
    public Action action(Hex[] adjacentHexes, ArrayList<ArrayList<Hex>> hexCake, double myTeamHp, double enemyTeamHp, double totalEnemies, double totalAllies, double[][] adjacentAis) 
    {
    	System.out.println("Ai.Action() should be overwritten by extending classes");
    	return new Action(new Coordinate(0, 0), "standard", "Ai");
    }
    
    public void newStartId() { startId = aiType + ", " + "team " + getTeam() + " from (" + position.getX() + "," + position.getY() + ")"; }
    
    public boolean getBoosted() { return boosted; }
    
    public Color getColor() { return color; }
    
    public void setColor(Color color) { this.color = color; }
    
    public void setAiType(String type) { aiType = type; }
    
    public String getAiType() { return aiType; } 
    
    public double getAreaDamage() {
    	if(boosted) {
    		boosted = false;
    		return areaDamage * (1.0 + boostFactor);
    	}
    	else {
    		return areaDamage;
    	}
    }
    
    public double getHealAmount() { return healAmount; }

    public double getHp() { return hp; }
    
    public String getId() { return aiType + ", tm" + team + " at " + getPositionAsString(); }
    
    public double getInitialHp() { return initialHp; }
    
    public double getMeleeDamage() {
    	if(boosted) {
    		if(Launcher.allowBoostOutput) {System.out.println(getId() + ":  doing " + (meleeDamage * (1.0 + boostFactor) + " damage due to boost"));}
    		boosted = false;
    		return meleeDamage * (1.0 + boostFactor);
    	}
    	else {
    		return meleeDamage;
    	}
    }
    
    public Coordinate getPosition() {
		return position;
	}
    
    public String getPositionAsString() {return "(" + position.getX() + "," + position.getY() + ")";}
    
    public boolean getShielded() {
		return shielded;
	}
    
    public String getStartId() {
    	return startId;
    }
    
    public boolean getStunned() {
		return stunned;
	}
    
    public String getSupportAction() {
		return supportAction;
	}
    
    public int getTeam() {
		return team;
	}
    
    public double[][] getWeights() {
    	return weightMatrix;
    }
    
    public boolean isAlive() {
    	return hp > 0;
    }
    
    public void setBoosted(boolean status) {
		if (status && Launcher.allowBoostOutput) { System.out.println(getId() + ":  boosted"); }
		boosted = status;
	}
    
    public void setHp(double newHp) {
    	String sign;
    	if (newHp < hp) { sign = "-"; }
    	else { sign = "+"; }
    	if(startId != null && Launcher.allowHpOutput) { System.out.println(getId() + ":  hp = " + newHp + " (" + sign + abs(newHp - hp) + ")");}
		hp = newHp;
	} 
    
    public void setMeleeDamage(double damage) {
    	meleeDamage = damage;
    }
    
    public void setPosition(Coordinate newPos) {
    	position = newPos;
    }
    
    public void setShielded(boolean status) {
		if (!status && Launcher.allowShieldOutput) { System.out.println(getId() + ":  lost shield"); }
		if(status && Launcher.allowShieldOutput) {System.out.println(getId() + ":  shielded");}
		shielded = status;
	}

	public void setStunned(boolean status) {
		if(status == false && Launcher.allowStunOutput) {
			System.out.println(getId() + ":  stunned");
		}
		stunned = status;
	}
	
	public void setSupportAction(String action) {
		supportAction = action;
	}
	
	public void setTeam(int team) {
		this.team = team;
	}	
	
	public void setWeights(double[][] weights) {
    	weightMatrix = weights;
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
    
    public ArrayList<Double> getInformation(Hex adjacentHex, ArrayList<Ai> enemies, ArrayList<Ai> allies, double myTeamHp, double enemyTeamHp, double totalEnemies, double totalAllies, double adjacentEnemies, double adjacentAllies) {
    	ArrayList<Double> information = new ArrayList<Double>();
		double nearestEnemyHp = 0;
		double nearestEnemyIsBoosted = 0;
		double nearestEnemyDistance = 0;
		double nearestEnemyStunned = 0;
		double nearestEnemyShielded = 0;
		double nearestEnemyIsWarrior = 0;
		double nearestEnemyIsWizard = 0;
		double nearestEnemyIsCleric = 0;
		double nearestAllyHp = 0;
		double nearestAllyIsBoosted = 0;
		double nearestAllyDistance = 0;
		double nearestAllyStunned = 0;
		double nearestAllyShielded = 0;
		double nearestAllyIsWarrior = 0;
		double nearestAllyIsWizard = 0;
		double nearestAllyIsCleric = 0;
		Ai nearestEnemy = nearestAi(enemies);
		Ai nearestAlly = nearestAi(allies);
		
		if(nearestEnemy != null) {
			nearestEnemyHp = nearestEnemy.getHp();
			nearestEnemyDistance = position.distance(nearestEnemy.getPosition());	
			if(nearestEnemy.getBoosted()) {nearestEnemyIsBoosted = 1;}
			if(nearestEnemy.getShielded()) {nearestEnemyShielded = 1;}
			if(nearestEnemy.getStunned()) {nearestEnemyStunned = 1;}
			
			switch(nearestEnemy.getAiType()) {
			case "Warrior":
				nearestEnemyIsWarrior = 1;
				break;
			case "Wizard":
				nearestEnemyIsWizard = 1;
				break;
			case "Cleric":
				nearestEnemyIsCleric = 1;
				break;
			default:
				System.out.println("Unknown enemy ai type " + nearestEnemy.getAiType());
			}
				
		}
		if(nearestAlly != null) {
			nearestAllyHp = nearestAlly.getHp(); 
			nearestAllyDistance = position.distance(nearestAlly.getPosition());	
			if(nearestAlly.getBoosted()) {nearestAllyIsBoosted = 1;}
			if(nearestAlly.getShielded()) {nearestAllyShielded = 1;}
			if(nearestAlly.getStunned()) {nearestAllyStunned = 1;}
			
			switch(nearestAlly.getAiType()) {
			case "Warrior":
				nearestAllyIsWarrior = 1;
				break;
			case "Wizard":
				nearestAllyIsWizard = 1;
				break;
			case "Cleric":
				nearestAllyIsCleric = 1;
				break;
			default:
				System.out.println("Unknown ally ai type " + nearestAlly.getAiType());
			}
		}
		
		information.add(hp);
		information.add(myTeamHp);
		information.add(enemyTeamHp);
		information.add((double) enemies.size());
		information.add((double) allies.size());
		information.add(nearestAllyHp);
		information.add(nearestAllyDistance);
		information.add(nearestAllyStunned);
		information.add(nearestAllyShielded);
		information.add(nearestEnemyHp);
		information.add(nearestEnemyDistance);
		information.add(nearestEnemyStunned);
		information.add(nearestEnemyShielded);
		information.add(totalEnemies);
		information.add(totalAllies);
		information.add(nearestEnemyIsWarrior);
		information.add(nearestEnemyIsWizard);
		information.add(nearestEnemyIsCleric);
		information.add(nearestAllyIsWarrior);
		information.add(nearestAllyIsWizard);
		information.add(nearestAllyIsCleric);
		information.add(nearestAllyIsBoosted);
		information.add(nearestEnemyIsBoosted);
		information.add(adjacentEnemies);
		information.add(adjacentAllies);
		
		if(Launcher.allowAdjacentAiOutput) {
			if(adjacentEnemies > 0) {System.out.println("Team " + team + " " + aiType + " at " + "(" + position.getX() + ", " + position.getY() + "): found " + (int)adjacentEnemies + " enemies"); }
			if(adjacentAllies > 0) { System.out.println("Team " + team + " " + aiType + " at " + "(" + position.getX() + ", " + position.getY() + "): found " + (int)adjacentAllies + " allies"); }
		}
		
    	
		return information;
    }    
}
