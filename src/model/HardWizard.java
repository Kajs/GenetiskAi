package model;

public class HardWizard extends Ai {
	
	public HardWizard() {
		setAiType("Wizard");
		setSupportAction("boost");
		initialHp = wizardInitialHp;
		hp = initialHp;
		areaDamage = wizardAreaDamage;
		meleeDamage = wizardMeleeDamage;
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