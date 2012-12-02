package model;

import static java.lang.Math.abs;
import java.awt.Color;
import java.util.ArrayList;
import model.Ai;
import model.Coordinate;
import control.Controller;
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
	public double initialHp;
	public Ai copy;
	
	//__________________class variables
	public double warriorMeleeDamage = 5;
	public double warriorInitialHp = 20;
	
	public double wizardMeleeDamage = 4;
	public double wizardAreaDamage = 1.75;
	public double wizardInitialHp = 10;
	
	public double clericMeleeDamage = 2.5;
	public double clericHealAmount = 5;
	public double clericInitialHp = 15;
	//__________________class variables
	
	//__________________ weight variables section
	public Hex adjacentHex;
	public ArrayList<Ai> enemies = new ArrayList<Ai>();
	public ArrayList<Ai> allies = new ArrayList<Ai>();
	public double myTeamHp;
	public double enemyTeamHp;
	double staticsAlive;
	double geneticsAlive;
	double adjacentHexEnemies;
	double adjacentHexAllies;
	double adjacentLocalEnemies;
	double adjacentLocalAllies;
	double nearestEnemyDistanceGlobal;
	double nearestAllyDistanceGlobal;
	double averageEnemyDistance;
	double averageAllyDistance;
	
	//__________________ weight variables section
    
	//__________________ information variables section
	public double[] information = new double[Controller.information];
	public double nearestEnemyHp;
	public double nearestEnemyIsBoosted;
	public double nearestEnemyDistance;
	public double nearestEnemyStunned;
	public double nearestEnemyShielded;
	public double nearestEnemyIsWarrior;
	public double nearestEnemyIsWizard;
	public double nearestEnemyIsCleric;
	public double nearestAllyHp;
	public double nearestAllyIsBoosted;
	public double nearestAllyDistance;
	public double nearestAllyStunned;
	public double nearestAllyShielded;
	public double nearestAllyIsWarrior;
	public double nearestAllyIsWizard;
	public double nearestAllyIsCleric;
	public Ai nearestEnemy;
	public Ai nearestAlly;
	//__________________ information variables section
	
    public Ai() {
    }
    
    public Action action(Hex[] adjacentHexes, ArrayList<ArrayList<Hex>> hexCake, double myTeamHp, double enemyTeamHp, double geneticsAlive, double staticsAlive, double[][] adjacentHexAis, double[] adjacentLocalAis, double[][] nearestAiDistances) {   	
    	
		bestAction = null;
		bestWeight = (int)Math.pow(-2, 31);
		
		for (int i = 0; i < hexCake.size(); i++) {
			ArrayList<Hex> slice = hexCake.get(i);
			enemies.clear();
			allies.clear();
			
			for (Hex hex : slice) {
				if (hex.isOccupied()) {
					Ai foundAi = hex.getAi();
					if(foundAi.getTeam() == team) { allies.add(foundAi); }
					else { enemies.add(foundAi); }
				}
			}
			
			if (staticsAlive > 0 && adjacentHexes[i] != null) {
				this.adjacentHex = adjacentHexes[i];
				this.myTeamHp = myTeamHp;
				this.enemyTeamHp = enemyTeamHp;
				this.staticsAlive = staticsAlive;
				this.geneticsAlive = geneticsAlive;
				this.adjacentHexEnemies = adjacentHexAis[0][i];
				this.adjacentHexAllies = adjacentHexAis[1][i];
				this.adjacentLocalEnemies = adjacentLocalAis[0];
				this.adjacentLocalAllies = adjacentLocalAis[1];
				this.nearestEnemyDistanceGlobal = nearestAiDistances[0][i];
				this.nearestAllyDistanceGlobal = nearestAiDistances[1][i];
				this.averageEnemyDistance = nearestAiDistances[2][i];
				this.averageAllyDistance = nearestAiDistances[3][i];
				getInformation();
				weight();
				//weight(adjacentHexes[i], enemies, allies, myTeamHp, enemyTeamHp, totalEnemies, totalAllies, adjacentAis[0][i], adjacentAis[1][i]);
			}	
		}
		
		if(bestAction == null) {
			bestAction = new Action(position, "move", "stay");
		}
		if(Launcher.allowActionOutput) {System.out.println(aiType + ", team " + team + " at (" + position.getX() + ", " + position.getY() + ") chose " + bestAction.getBaseType() + ", " + bestAction.getExtendedType() + " on (" + bestAction.getPosition().getX() + ", " + bestAction.getPosition().getY() + ")");}
		
		return bestAction;
	}
    
    public void weight() { System.out.println("Ai.weight must be overwritten by extending classes"); }
    
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
    
    public double getHealAmount() {
    	if(boosted) {
    		if(Launcher.allowBoostOutput) {System.out.println(getId() + ":  healing " + (healAmount * (1.0 + boostFactor) + " hp due to boost"));}
    		boosted = false;
    		return healAmount * (1.0 + boostFactor);
    	}
    	else {
    		return healAmount;
    	}
    }

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
    	
    	Ai closest = null;
    	double smallest = Double.MAX_VALUE;
    	
    	for (int i = 0; i < ais.size(); i++) {
    		Ai ai = ais.get(i);
    		double distance = position.distance(ai.getPosition());
    		if(distance < smallest) {
    			closest = ai;
    			smallest = distance;
    		}
    	}
    	
    	return closest;
    }
    
    public void getInformation() {
		nearestEnemyHp = 0;
		nearestEnemyIsBoosted = 0;
		nearestEnemyDistance = 40;
		nearestEnemyStunned = 0;
		nearestEnemyShielded = 0;
		nearestEnemyIsWarrior = 0;
		nearestEnemyIsWizard = 0;
		nearestEnemyIsCleric = 0;
		nearestAllyHp = 0;
		nearestAllyIsBoosted = 0;
		nearestAllyDistance = 40;
		nearestAllyStunned = 0;
		nearestAllyShielded = 0;
		nearestAllyIsWarrior = 0;
		nearestAllyIsWizard = 0;
		nearestAllyIsCleric = 0;
		nearestEnemy = nearestAi(enemies);
		nearestAlly = nearestAi(allies);
		
		if(nearestEnemy != null) {
			nearestEnemyHp = nearestEnemy.getHp();
			nearestEnemyDistance = position.distance(nearestEnemy.getPosition());	
			//System.out.println(getId() + " to " + nearestEnemy.getId() + " = " + nearestEnemyDistance);
			if(nearestEnemy.getBoosted()) {nearestEnemyIsBoosted = 1;}
			if(nearestEnemy.getShielded()) {nearestEnemyShielded = 1;}
			if(nearestEnemy.getStunned()) {nearestEnemyStunned = 1;}
			
			String enemyType = nearestEnemy.getAiType();
			if(enemyType.equals("Warrior")) { nearestEnemyIsWarrior = 1; } else { 
			if(enemyType.equals("Wizard")) { nearestEnemyIsWizard = 1; } else {
			if(enemyType.equals("Cleric")) { nearestEnemyIsCleric = 1; } 
			else { System.out.println("Unknown enemy ai type: " + enemyType); }}}				
		}
		
		if(nearestAlly != null) {
			nearestAllyHp = nearestAlly.getHp(); 
			nearestAllyDistance = position.distance(nearestAlly.getPosition());	
			//System.out.println(getId() + " to " + nearestAlly.getId() + " = " + nearestAllyDistance);
			if(nearestAlly.getBoosted()) {nearestAllyIsBoosted = 1;}
			if(nearestAlly.getShielded()) {nearestAllyShielded = 1;}
			if(nearestAlly.getStunned()) {nearestAllyStunned = 1;}
			
			String allyType = nearestAlly.getAiType();
			if(allyType.equals("Warrior")) { nearestAllyIsWarrior = 1; } else {
		    if(allyType.equals("Wizard")) { nearestAllyIsWizard = 1; } else {
		    if(allyType.equals("Cleric")) { nearestAllyIsCleric = 1; } 
		    else { System.out.println("Unknown ally ai type: " + allyType); }}}
		}
		
		information[0] = hp;
		information[1] = myTeamHp;
		information[2] = enemyTeamHp;
		information[3] = (double) enemies.size();
		information[4] = (double) allies.size();
		information[5] = nearestAllyHp;
		information[6] = nearestAllyDistance;
		information[7] = nearestAllyStunned;
		information[8] = nearestAllyShielded;
		information[9] = nearestEnemyHp;
		information[10] = nearestEnemyDistance;
		information[11] = nearestEnemyStunned;
		information[12] = nearestEnemyShielded;
		information[13] = staticsAlive;
		information[14] = geneticsAlive;
		information[15] = nearestEnemyIsWarrior;
		information[16] = nearestEnemyIsWizard;
		information[17] = nearestEnemyIsCleric;
		information[18] = nearestAllyIsWarrior;
		information[19] = nearestAllyIsWizard;
		information[20] = nearestAllyIsCleric;
		information[21] = nearestAllyIsBoosted;
		information[22] = nearestEnemyIsBoosted;
		information[23] = adjacentHexEnemies;
		information[24] = adjacentHexAllies;
		information[25] = nearestEnemyDistanceGlobal;
		information[26] = nearestAllyDistanceGlobal;
		information[27] = adjacentLocalEnemies;
		information[28] = adjacentLocalAllies;
		information[29] = averageEnemyDistance;
		information[30] = averageAllyDistance;
		
		
		if(Launcher.allowAdjacentAiOutput) {
			if(adjacentHexEnemies > 0) {System.out.println("Team " + team + " " + aiType + " at " + "(" + position.getX() + ", " + position.getY() + "): found " + (int)adjacentHexEnemies + " enemies"); }
			if(adjacentHexAllies > 0) { System.out.println("Team " + team + " " + aiType + " at " + "(" + position.getX() + ", " + position.getY() + "): found " + (int)adjacentHexAllies + " allies"); }
		}
    }    
}
