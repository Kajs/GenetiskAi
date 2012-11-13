package model;

import java.util.ArrayList;
import control.Launcher;

public class BaseCleric extends Ai {
	
	public BaseCleric() {
		setAiType("Cleric");
		setSupportAction("shield");
		initialHp = 15;
		hp = initialHp;
		standardMeleeDamage = 2.5;
		healAmount = 5.0;
		meleeDamage = standardMeleeDamage;
    }
	
	public Action action(Hex[] adjacentHexes, ArrayList<ArrayList<Hex>> hexCake, double myTeamHp, double enemyTeamHp, double totalEnemies, double totalAllies, double[][] adjacentAis) {
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
		
		if (adjacentHex != null && bestWeight < 3) {
			if(adjacentHex.isOccupied()) {
				Ai adjacentAi = adjacentHex.getAi();
				
				if(adjacentAi.getTeam() != team) {
					if(bestWeight < 2) {
						//attack
						bestAction = new Action(adjacentPosition, "attack", "normal");
						bestWeight = 2;
					}
				}
				else {
					double currentHp = adjacentAi.getHp();
					double initialHp = adjacentAi.getInitialHp();
					double healPotential = (initialHp - currentHp) / initialHp;
					if(healPotential > 0 && 2 + healPotential > bestWeight) {
						bestAction = new Action(adjacentPosition, "support", "heal");
						bestWeight = 2 + healPotential;
					}
				}
			}
			else {
				if(enemies.size() > 0) {
					Ai nearestEnemy = nearestAi(enemies);
					double distance = position.distance(nearestEnemy.getPosition());
					if(1.0/distance > bestWeight) {
						bestAction = new Action(adjacentPosition, "move", "nearestEnemy");
						bestWeight = 1.0 / distance;
					}
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
