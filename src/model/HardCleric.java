package model;

public class HardCleric extends Ai {
	
	public HardCleric() {
		setAiType("Cleric");
		setSupportAction("heal");
		initialHp = clericInitialHp;
		hp = initialHp;
		healAmount = clericHealAmount;
		meleeDamage = clericMeleeDamage;
    }
	
	public void weight () {
		
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
