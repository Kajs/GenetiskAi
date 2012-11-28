package model;

public class BaseWarrior extends Ai {
	
	public BaseWarrior() {
		setAiType("Warrior");
		setSupportAction("shield");
		initialHp = warriorInitialHp;
		hp = initialHp;
		meleeDamage = warriorMeleeDamage;
    }
	
	public void weight () {
		
		Coordinate adjacentPosition = adjacentHex.getPosition();
		
		if (adjacentHex != null) {
			if(adjacentHex.isOccupied()) {
				Ai adjacentAi = adjacentHex.getAi();
				if(adjacentAi.getTeam() != team) {
					if(bestWeight < 3) {
						//attack
						bestAction = new Action(adjacentPosition, "attack", "normal");
						bestWeight = 3;
					}
				}
				else {
					if(!adjacentAi.getShielded() && bestWeight < 2) {
						bestAction = new Action(adjacentPosition, "support", "shield");
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