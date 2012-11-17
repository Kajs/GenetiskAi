package model;

import java.util.ArrayList;
import control.Launcher;

public class HardWizard extends Ai {
	
	public HardWizard() {
		setAiType("Wizard");
		setSupportAction("boost");
		initialHp = 10;
		hp = initialHp;
		standardMeleeDamage = 4;
		areaDamage = standardMeleeDamage / 2 + 0.5;
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
		
		if (adjacentHex != null) {
			if(adjacentHex.isOccupied()) {
				Ai adjacentAi = adjacentHex.getAi();
				
				if(adjacentAi.getTeam() != team) {
					if(bestWeight < 3) {
						//attack
						bestAction = new Action(adjacentPosition, "attack", "area");
						bestWeight = 3;
					}
				}
				else{
					if(bestWeight < 2 && !adjacentAi.getBoosted()) {
						bestAction = new Action(adjacentPosition, "support", "boost");
						bestWeight = -1;
					}					
				}
			}
			else {
				if(enemies.size() > 0 && bestWeight < 2) {
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