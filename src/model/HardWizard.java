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
					if (adjacentHexEnemies >= 1 && enemiesAliveGlobal > 1) {
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
						//if(nearestEnemyShielded == 1 && nearestEnemyDistance <= 4) {weight = 0;}
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
				weight += 0.1 / nearestAllyDistanceGlobal;
				//if(alliesAliveGlobal > 1) { weight += 0.1 / (averageAllyDistance); }
				compareAction(weight, adjacentPosition, "move", "move1");
				
				// Move towards enemies, but try to stay close to allies if alone
				if(adjacentLocalAllies == 0 && alliesAliveGlobal > 1) {
					weight = 210;
					weight += 1.0/nearestAllyDistanceGlobal;
					weight += 0.01/averageAllyDistance;
					weight += 0.0001/nearestEnemyDistanceGlobal;
					compareAction(weight, adjacentPosition, "move", "move2"); 
				}
				
				if(adjacentLocalAllies == 1 && alliesAliveGlobal >= 2) {
					weight = 220;
					weight += 0.01/nearestAllyDistanceGlobal;
					weight += 1.0/averageAllyDistance;
					weight += 0.0001/nearestEnemyDistanceGlobal;
					compareAction(weight, adjacentPosition, "move", "move3"); 
				}
				
				if(adjacentLocalAllies == 1 && alliesAliveGlobal > 2) {
					weight = 230;
					weight += 1.0/averageAllyDistance;
					weight += 0.0001/nearestAllyDistanceGlobal;
					weight -= 0.01/nearestEnemyDistanceGlobal;
					weight -= 0.01/averageEnemyDistance;
					compareAction(weight, adjacentPosition, "move", "move4"); 
				}
				
				if(adjacentLocalAllies == 2 && alliesAliveGlobal >= 3) {
					weight = 240;
					//weight += 0.1/nearestEnemyDistanceGlobal;
					weight += 1.0/averageEnemyDistance;
					compareAction(weight, adjacentPosition, "move", "move5"); 
				}  
				
				if(adjacentLocalEnemies == 0 && adjacentHexEnemies >= 1 && hp == initialHp) {
					weight = 250;
					weight += adjacentHexAllies;
					compareAction(weight, adjacentPosition, "move", "move6");
				}
				
				if(alliesAliveGlobal == 1 && hp == initialHp && adjacentLocalEnemies == 0 && adjacentHexEnemies >= 1) {
					weight = 260;
					compareAction(weight, position, "move", "stay");
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