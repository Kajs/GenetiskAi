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
					if (adjacentHexEnemies >= 2) {
						weight = 900;
						weight += 1.0/nearestEnemyHp;
						weight += adjacentHexEnemies;
						compareAction(weight, adjacentPosition, "attack", "area");
					}
					
					weight = 800;   // normal attack else
					weight += 1.0/nearestEnemyHp;
					compareAction(weight, adjacentPosition, "attack", "normal");
					
				}
				else {
					//support
					
					// Boost ally if no enemy is near and prefer warriors that are not stunned
					if (nearestAllyIsBoosted == 0 && nearestAllyIsWarrior == 1 && nearestAllyStunned == 0) {
						weight = 700;
						weight += adjacentHexEnemies;
						compareAction(weight, adjacentPosition, "support", "boost");
					}
					
					if(nearestAllyIsBoosted == 0) {
						weight = 400;
						compareAction(weight, adjacentPosition, "support", "boost");
					}
				}
			}
			else {
				weight = 200;            //near allies, but away from enemies
				weight += 1.0 / (nearestEnemyDistanceGlobal);
				weight += 0.1 / (nearestAllyDistanceGlobal);
				compareAction(weight, adjacentPosition, "move", "move1");
				
				if(adjacentLocalAllies == 0 && adjacentHexAllies == 0) {  //go to allies
					weight = 210;
					weight += 1.0/nearestAllyDistanceGlobal;
					weight += 0.1/nearestEnemyDistanceGlobal;
					weight += 1.0/averageAllyDistance;
					compareAction(weight, adjacentPosition, "move", "move1");
				}
				// Move towards enemies, but try to stay close to allies
				if(adjacentLocalAllies == 0 && adjacentHexAllies == 1) {  //go to allies
					weight = 220;
					//weight += 0.1/nearestAllyDistanceGlobal;
					weight += 1.0/nearestEnemyDistanceGlobal;
					weight += 1.0/averageAllyDistance;
					compareAction(weight, adjacentPosition, "move", "move1");
				}
				
							
				if(adjacentLocalEnemies == 0 && adjacentHexEnemies >= 1) {  //go to enemies
					weight = 230;
					//weight += 1.0/nearestAllyDistanceGlobal;
					weight += 0.1/nearestEnemyDistanceGlobal;
					weight += 1.0/averageAllyDistance;
					weight += adjacentHexAllies;
					compareAction(weight, adjacentPosition, "move", "move1");
				}
				
				// Move to allies if there are some
				if (averageAllyDistance > 1 && alliesAliveGlobal > 1) {
					System.out.println("WizAvgAllyDist: " + averageAllyDistance);
					weight = 500;
					weight += 1.0/averageAllyDistance;
					//weight -= 0.1/averageEnemyDistance;
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