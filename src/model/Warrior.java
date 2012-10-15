package model;

import java.awt.Color;
import java.util.ArrayList;

public class Warrior extends Ai {
	
	public Warrior(Coordinate startingPosition, int team) {
		setPosition(startingPosition);
		setAiType("Warrior");
		setColor(Color.red);
		setHp(20);
		setMeleeDamage(5);
		this.team = team;
		generateId();
    }
	
	public Action action(Hex[] adjacentHexes, ArrayList<ArrayList<Hex>> hexCake, int myTeamHp, int enemyTeamHp, int totalEnemies, int totalAllies) {
		bestAction = null;
		bestWeight = (int)Math.pow(-2, 31);
		System.out.println(bestWeight);
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
			if (totalEnemies > 0) {
				weight(adjacentHexes[i], enemies, allies, myTeamHp, enemyTeamHp, totalEnemies, totalAllies);
			}	
		}
		
		if(bestAction == null) {
			System.out.println("No best action found, staying put");
			bestAction = new Action(position, "move");
		}
		System.out.println(id + " chose " + bestAction.getType() + " at (" + bestAction.getPosition().getX() + "," + bestAction.getPosition().getY() + ")");
		
		return bestAction;
	}
	
	public void weight (Hex adjacentHex, ArrayList<Ai> enemies, ArrayList<Ai> allies, int myTeamHp, int enemyTeamHp, int totalEnemies, int totalAllies) {
		int nearestEnemyHp;
		int nearestEnemyDistance;
		int nearestAllyHp;
		int nearestAllyDistance;
		Ai nearestEnemy = nearestAi(enemies);
		Ai nearestAlly = nearestAi(allies);
		
		if(nearestEnemy == null) { 
			nearestEnemyHp = 0; 
			nearestEnemyDistance = 0;
			} 
		else {
			nearestEnemyHp = nearestEnemy.getHp();
			nearestEnemyDistance = position.distance(nearestEnemy.getPosition());			
			}
		if(nearestAlly == null) { 
			nearestAllyHp = 0;
			nearestAllyDistance = 0;		
		} 
		else { 
			nearestAllyHp = nearestAlly.getHp(); 
			nearestAllyDistance = position.distance(nearestAlly.getPosition());		       
		}
		Coordinate adjacentPosition = adjacentHex.getPosition();
		
		if (adjacentHex != null) {
			if(adjacentHex.isOccupied()) {
				if(adjacentHex.getAi().getTeam() != team) {
					//attack
					w0NormalAttack(adjacentPosition, hp, enemyTeamHp, enemies.size(), nearestEnemyHp);
					w1StunAttack(adjacentPosition, hp, enemyTeamHp, enemies.size(), nearestEnemyHp);
				}
				else {
					//support
				}
			}
			else {
				//move
				w3MoveNearEnemy(adjacentPosition, hp, nearestEnemyHp, nearestEnemyDistance);
				w4MoveNearAlly(adjacentPosition, hp, nearestAllyHp, nearestAllyDistance);
				w5MoveAwayEnemies(adjacentPosition, hp, totalEnemies, enemies.size(), nearestEnemyDistance);
				w6MoveAwayAllies(adjacentPosition, hp, totalAllies, allies.size(), nearestAllyDistance);
				w7MoveMostEnemies(adjacentPosition, enemies.size(), nearestEnemyDistance);
				w8MoveMostAllies(adjacentPosition, allies.size(), nearestAllyDistance);
			}
		}
	}
	
	public void w0NormalAttack(Coordinate adjacentPosition, int hp, int enemyTeamHp, int sliceEnemies, int nearestEnemyHp) {
		int weight = hp * weights[0] + enemyTeamHp * weights[2] + sliceEnemies * weights[3] * nearestEnemyHp * weights[5];
		if (weight > bestWeight) {
			bestWeight = weight;
			bestAction = new Action(adjacentPosition, "normalAttack");
		}
	}
	
	public void w1StunAttack(Coordinate adjacentPosition, int hp, int enemyTeamHp, int sliceEnemies, int nearestEnemyHp) {
		double weight = hp * weights[0] + enemyTeamHp * weights[2] + sliceEnemies * weights[3] + nearestEnemyHp * weights[5];
		if (weight > bestWeight) {
			bestWeight = weight;
			bestAction = new Action(adjacentPosition, "stunAttack");
		}
	}
	
	
}
