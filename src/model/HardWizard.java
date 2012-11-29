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
		getInformation();
		
		Coordinate adjacentPosition = adjacentHex.getPosition();
		double weight=0; 
		
		if (adjacentHex != null) {			
			if(adjacentHex.isOccupied()) {
				if(adjacentHex.getAi().getTeam() != team) {
					//attack
					
					// Always attack unshielded enemy with less or equal hp to attack dmg, prefer clerics
					if (nearestEnemyHp <= meleeDamage && nearestEnemyShielded == 0 ) {
						weight = 1000;
						if (nearestEnemyIsWizard == 1) {
							weight+=1;
						}
						if (nearestEnemyIsCleric == 1) {
							weight+=2;
						}
						compareAction(weight, adjacentPosition, "attack", "normal");
					}
					
					// Area on two or more enemies
					if (adjacentHexEnemies >= 1) {
						weight = 900;
						weight += 1.0/nearestEnemyHp;
						compareAction(weight, adjacentPosition, "attack", "area");
					}
					
					weight = 800;   // normal attack else
					weight += 1.0/nearestEnemyHp;
					compareAction(weight, adjacentPosition, "attack", "normal");
					
				}
				else {
					//support
					
					// Shield ally if HP is low, modified by number of enemies next to him and type
					if (nearestAllyIsBoosted == 0 && nearestAllyIsWarrior == 1 && nearestAllyStunned == 0) {
						weight = 700;
						compareAction(weight, adjacentPosition, "support", "boost");
					}
					
					if(nearestAllyIsBoosted == 0) {
						weight = 400;
						compareAction(weight, adjacentPosition, "support", "boost");
					}
				}
			}
			else {
				// Move towards enemies, but try to stay close to allies
				if(adjacentLocalAllies == 0 && adjacentHexAllies == 0) {  //go to allies
					weight = 210;
					weight += 1.0/nearestAllyDistanceGlobal;
					weight += -0.1/nearestEnemyDistanceGlobal;
					compareAction(weight, adjacentPosition, "move", "move1");
				}
				if(adjacentHexAllies >= 1 && nearestEnemyIsCleric == 0) {  //follow the fighter or wizard
					weight = 220;
					//weight += 1.0/nearestAllyDistanceGlobal;
					weight += 0.1/nearestEnemyDistanceGlobal;
					weight += nearestAllyIsWarrior;
					compareAction(weight, adjacentPosition, "move", "move1");
				}
				else {
					weight = 200;            //near allies, but away from enemies
					weight += 0.1 / (nearestEnemyDistanceGlobal);
					weight += 1.0 / (nearestAllyDistanceGlobal);
					weight += adjacentHexAllies;
					compareAction(weight, adjacentPosition, "move", "move1");
				}
				
				//System.out.println("weight " + weight + ", nearestEnemyDistance " + nearestEnemyDistance + " at (" + adjacentHex.getPosition().getX() + "," + adjacentHex.getPosition().getY() + ")");

				//System.out.println(result + ", nearestEnemyDistance " + nearestEnemyDistance);
				//System.out.println(getId() + "E: " + nearestEnemyDistanceGlobal + ", A: " + nearestAllyDistanceGlobal + " at (" + adjacentHex.getPosition().getX() + "," + adjacentHex.getPosition().getY() + "), weight " + bestWeight);
			}
		}
	}
	
    public void compareAction(double result, Coordinate position, String baseType, String extendedType) {
		if (result > bestWeight) {
			bestAction = new Action(position, baseType, extendedType);
			bestWeight = result;
		}
	}
}