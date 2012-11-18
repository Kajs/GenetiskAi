package model;

public class BaseWizard extends Ai {
	
	public BaseWizard() {
		setAiType("Wizard");
		setSupportAction("boost");
		initialHp = 10;
		hp = initialHp;
		standardMeleeDamage = 4;
		areaDamage = standardMeleeDamage / 2 + 0.5;
		meleeDamage = standardMeleeDamage;
    }
	
	public void weight () {
		
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