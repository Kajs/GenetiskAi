package model;

public class BaseWizard extends Ai {
	
	public BaseWizard() {
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
					//attack
					if(bestWeight < 3) {
						bestAction = new Action(adjacentPosition, "attack", "area");
						bestWeight = 3;
					}
				}
				else{
					if(bestWeight < -1 && !adjacentAi.getBoosted()) {
						bestAction = new Action(adjacentPosition, "support", "boost");
						bestWeight = -1;
					}					
				}
			}
			else {
				if(bestWeight < 1.0 / nearestEnemyDistance) {
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