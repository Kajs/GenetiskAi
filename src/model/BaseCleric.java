package model;

public class BaseCleric extends Ai {
	
	public BaseCleric() {
		setAiType("Cleric");
		setSupportAction("heal");
		initialHp = clericInitialHp;
		hp = initialHp;
		healAmount = clericHealAmount;
		meleeDamage = clericMeleeDamage;
    }
	
	public void weight () {
		
		Coordinate adjacentPosition = adjacentHex.getPosition();
		
		if(adjacentHex.isOccupied()) {
			Ai adjacentAi = adjacentHex.getAi();
			
			if(adjacentAi.getTeam() != team) {
				//attack
				if(bestWeight < 2) {
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
			if(1.0 / nearestEnemyDistance > bestWeight) {
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
