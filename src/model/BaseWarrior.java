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
					//attack
					if (bestWeight < 3) {
						bestAction = new Action(adjacentPosition, "attack", "normal");
						bestWeight = 3;						
					}
				}
				else {
					if(!adjacentAi.getShielded() && bestWeight < -1) {
						bestAction = new Action(adjacentPosition, "support", "shield");
						bestWeight = -1;
					}	
				}
			
			}
			else {
				if(bestWeight < 1.0/nearestEnemyDistance) {
					bestAction = new Action(adjacentPosition, "move", "nearestEnemy");
					bestWeight = 1.0 / nearestEnemyDistance;
				}
				else {
					if (bestAction == null) {
						bestAction = new Action(adjacentPosition, "move", "base");
					}
				}
			}
		}
	}
}