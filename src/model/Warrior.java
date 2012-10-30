package model;

import java.awt.Color;
import java.util.ArrayList;
import control.Launcher;

public class Warrior extends Ai {
	
	public Warrior(double[][] weights) {
		setAiType("Warrior");
		setSupportAction("shield");
		setColor(Color.red);
		initialHp = 20;
		hp = initialHp;
		standardMeleeDamage = 5;
		meleeDamage = standardMeleeDamage;
		weightMatrix = weights;
    }
	
	public Action action(Hex[] adjacentHexes, ArrayList<ArrayList<Hex>> hexCake, double myTeamHp, double enemyTeamHp, double totalEnemies, double totalAllies) {
		bestAction = null;
		bestWeight = (int)Math.pow(-2, 31);
		for (int i = 0; i < hexCake.size(); i++) {
			ArrayList<Hex> slice = hexCake.get(i);
			ArrayList<Ai> enemies = new ArrayList<Ai>();
			ArrayList<Ai> allies = new ArrayList<Ai>();
			
			for (Hex hex : slice) {
				if (hex.isOccupied()) {
					Ai foundAi = hex.getAi();
					if(foundAi.getTeam() == getTeam()) {
						allies.add(foundAi);
					}
					else {
						//System.out.println("I found an enemy during loop " + i);
						enemies.add(foundAi);
					}
				}
			}
			if (totalEnemies > 0 && adjacentHexes[i] != null) {
				weight(adjacentHexes[i], enemies, allies, myTeamHp, enemyTeamHp, totalEnemies, totalAllies);
			}	
		}
		
		if(bestAction == null) {
			if(!Launcher.isAutomatic) {System.out.println("No best action found, staying put");}
			bestAction = new Action(position, "move", "stay");
		}
		if(!Launcher.isAutomatic) {System.out.println(aiType + ", team " + team + " at (" + position.getX() + ", " + position.getY() + ") chose " + bestAction.getBaseType() + ", " + bestAction.getExtendedType() + " on (" + bestAction.getPosition().getX() + ", " + bestAction.getPosition().getY() + ")");}
		
		return bestAction;
	}
	
	public void weight (Hex adjacentHex, ArrayList<Ai> enemies, ArrayList<Ai> allies, double myTeamHp, double enemyTeamHp, double totalEnemies, double totalAllies) {
		ArrayList<Double> information = new ArrayList<Double>();
		double nearestEnemyHp;
		double nearestEnemyDistance;
		double nearestEnemyStunned;
		double nearestEnemyShielded;
		double nearestAllyHp;
		double nearestAllyDistance;
		double nearestAllyStunned;
		double nearestAllyShielded;
		Ai nearestEnemy = nearestAi(enemies);
		Ai nearestAlly = nearestAi(allies);
		
		if(nearestEnemy == null) {
			nearestEnemyHp = 0.0; 
			nearestEnemyDistance = 0.0;
			nearestEnemyStunned = 0.0;
			nearestEnemyShielded = 0.0;
		} 
		else {
			nearestEnemyHp = nearestEnemy.getHp();
			nearestEnemyDistance = position.distance(nearestEnemy.getPosition());	
			if(nearestEnemy.getStunned()) {
				nearestEnemyStunned = 1.0;
			}
			else {
				nearestEnemyStunned = 0.0;
			}
			
			if(nearestEnemy.getShielded()) {
				nearestEnemyShielded = 1.0;
			}
			else{
				nearestEnemyShielded = 0.0;
			}
		}
		if(nearestAlly == null) { 
			nearestAllyHp = 0.0;
			nearestAllyDistance = 0.0;
			nearestAllyStunned = 0.0;
			nearestAllyShielded = 0.0;
		} 
		else { 
			nearestAllyHp = nearestAlly.getHp(); 
			nearestAllyDistance = position.distance(nearestAlly.getPosition());		
			if(nearestAlly.getStunned()) {
				nearestAllyStunned = 1.0;
			}
			else {
				nearestAllyStunned = 0.0;
			}
			
			if(nearestAlly.getShielded()) {
				nearestAllyShielded = 1.0;
			}
			else{
				nearestAllyShielded = 0.0;
			}
		}
		
		Coordinate adjacentPosition = adjacentHex.getPosition();
		
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
		
		if (adjacentHex != null) {
			if(adjacentHex.isOccupied()) {
				if(adjacentHex.getAi().getTeam() != team) {
					//attack
					compareAction(totalWeight(0, information), adjacentPosition, "attack", "normal");    //normalAttack
					compareAction(totalWeight(1, information), adjacentPosition, "attack", "stun");      //stunAttack
				}
				else {
					compareAction(totalWeight(aActions + 0, information), adjacentPosition, "support", "shield");
					//support
				}
			}
			else {
				//move
				compareAction(totalWeight(aActions + sActions + 0, information), adjacentPosition, "move", "move1");      //move1
				compareAction(totalWeight(aActions + sActions + 1, information), adjacentPosition, "move", "move2");      //move2
				compareAction(totalWeight(aActions + sActions + 2, information), position, "move", "stay");               //stay put
			}
		}
	}
    
    public double totalWeight(int action, ArrayList<Double> information) {
    	double result = 0.0;
    	for (int i = 0; i < information.size(); i++) {
    		result = result + weightMatrix[action + 1][i] * information.get(i);
    	}
    	result = result * weightMatrix[0][action];
    	return result;
    }
    
    public void compareAction(double result, Coordinate position, String baseType, String extendedType) {
		if (result > bestWeight) {
			bestAction = new Action(position, baseType, extendedType);
			bestWeight = result;
		}
	}
}
