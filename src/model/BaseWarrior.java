package model;

import java.awt.Color;
import java.util.ArrayList;

public class BaseWarrior extends Ai {
	private Action bestAction;
	private double bestWeight;
	
	public BaseWarrior() {
		setAiType("BaseWarrior");
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
			System.out.println("No best action found, staying put");
			bestAction = new Action(position, "move", "stay");
		}
		System.out.println(id + " chose " + bestAction.getBaseType() + " at (" + bestAction.getPosition().getX() + "," + bestAction.getPosition().getY() + ")");
		
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
