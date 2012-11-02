package model;

import java.awt.Color;
import java.util.ArrayList;
import control.Launcher;

public class MediumWarrior extends Ai {
	
	public MediumWarrior() {
		setAiType("Warrior");
		setSupportAction("shield");
		setColor(Color.red);
		initialHp = 20;
		hp = initialHp;
		standardMeleeDamage = 5;
		meleeDamage = standardMeleeDamage;
    }
	
	public Action action(Hex[] adjacentHexes, ArrayList<ArrayList<Hex>> hexCake, double myTeamHp, double enemyTeamHp, double totalEnemies, double totalAllies) {
		bestAction = null;
		bestWeight = (int)Math.pow(-2, 31);
		
		for (int i = 0; i < hexCake.size(); i++) {
			ArrayList<Hex> slice = hexCake.get(i);
			ArrayList<Ai> enemies = new ArrayList<Ai>();
			
			for (Hex hex : slice) {
				if (hex.isOccupied()) {
					Ai foundAi = hex.getAi();
					if(foundAi.getTeam() != team) {
						enemies.add(foundAi);
					}
				}
			}
			if (totalEnemies > 0 && adjacentHexes[i] != null) {
				weight(adjacentHexes[i], enemies);
			}	
		}
		
		if(bestAction == null) {
			bestAction = new Action(position, "move", "stay");
		}
		if(Launcher.allowActionOutput) {System.out.println(aiType + ", team " + team + " at (" + position.getX() + ", " + position.getY() + ") chose " + bestAction.getBaseType() + ", " + bestAction.getExtendedType() + " on (" + bestAction.getPosition().getX() + ", " + bestAction.getPosition().getY() + ")");}
		
		return bestAction;
	}
	
	public void weight (Hex adjacentHex, ArrayList<Ai> enemies) {
		
		Coordinate adjacentPosition = adjacentHex.getPosition();
		
		if (adjacentHex != null && bestWeight < 2) {
			if(adjacentHex.isOccupied()) {
				if(adjacentHex.getAi().getTeam() != team) {
					//attack
					bestAction = new Action(adjacentPosition, "attack", "normal");
					bestWeight = 2;
				}
			}
			else {
				if(enemies.size() > 0) {
					Ai nearestEnemy = nearestAi(enemies);
					double distance = position.distance(nearestEnemy.getPosition());
						bestAction = new Action(adjacentPosition, "move", "nearestEnemy");
						bestWeight = 1.0 / distance;
				}
				else {
					if (bestAction == null) {
						bestAction = new Action(adjacentPosition, "move", "base");
						bestWeight = 0;
					}
				}
			}
		}
	}
}

/*

package model;

import java.awt.Color;
import java.util.ArrayList;

import control.Launcher;

public class MediumWarrior extends Ai {
	private Action bestAction;
	private double bestWeight;
	
	public MediumWarrior() {
		setAiType("MediumWarrior");
		setColor(Color.red);
		setHp(20);
		setMeleeDamage(5);
    }
	
	public void reset() {
		setHp(20);
		setMeleeDamage(5);
	}
	
	public Action action(Hex[] adjacentHexes, ArrayList<ArrayList<Hex>> hexCake, double myTeamHp, double enemyTeamHp, double totalEnemies, double totalAllies) {
		bestAction = null;
		bestWeight = 0; //needs certain minimum
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
			Ai nearestEnemy = nearestAi(enemies);
			if (nearestEnemy != null) {
				weight(adjacentHexes[i], enemies, allies, myTeamHp, enemyTeamHp);
			}	
		}
		
		if(bestAction == null) {
			if(Launcher.allowActionOutput) {System.out.println("No best action found, staying put"); }
			bestAction = new Action(position, "move", "stay");
		}
		if(Launcher.allowActionOutput) {System.out.println(id + " chose " + bestAction.getBaseType() + " at (" + bestAction.getPosition().getX() + "," + bestAction.getPosition().getY() + ")");}
		
		return bestAction;
	}
	
	public void weight (Hex adjacentHex, ArrayList<Ai> enemies, ArrayList<Ai> allies, double myTeamHp, double enemyTeamHp) {
		Ai nearestEnemy = nearestAi(enemies);
		//Ai nearestAlly = nearestAi(allies);
		
		if (adjacentHex != null) {
			String actionType;
			double w;
			if(adjacentHex.isOccupied()) {
				if(adjacentHex.getAi().getTeam() != team) {
					
					actionType = "attack";
					w = 100-nearestEnemy.getHp();
				}
				else {
					actionType = "support";
					w = -100000;
				}
			}
			else {
				actionType = "move";
				w = 10-position.distance(nearestEnemy.getPosition());
			}
			
			if (w>bestWeight) {
				bestWeight = w;
				bestAction = new Action(adjacentHex.getPosition(), actionType, "base"); //not done			 	
			}
			
		}
	}
}

*/