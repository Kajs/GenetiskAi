package model;

public class HardWarrior extends Ai {
	
	public HardWarrior() {
		setAiType("Warrior");
		setSupportAction("shield");
		initialHp = warriorInitialHp;
		hp = initialHp;
		meleeDamage = warriorMeleeDamage;
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
					
					// Stun when outnumbering enemies
					if (totalAllies > totalEnemies && nearestEnemyStunned == 0) {
						weight = 800;
						weight += nearestEnemyHp;
						compareAction(weight, adjacentPosition, "attack", "stun");
					}
					
					if(myTeamHp > enemyTeamHp) {
						weight = 850;
						weight += 1.0/nearestEnemyHp;
						compareAction(weight, adjacentPosition, "attack", "normal");
					}
					
					weight = 800;
					weight += 1.0/nearestEnemyHp;
					compareAction(weight, adjacentPosition, "attack", "normal");
					
				}
				else {
					//support
					
					// Shield ally if HP is low, modified by number of enemies next to him and type
					if (nearestAllyHp < 8 && nearestAllyShielded == 0) {
						weight = 910;
						weight += adjacentHexEnemies;
						if (nearestAllyIsWizard == 1) {
							weight+=0.5;
						}
						if (nearestAllyIsCleric == 1) {
							weight+=1;
						}
						compareAction(weight, adjacentPosition, "support", "shield");
					}
					
					if(nearestAllyShielded == 0) {
						weight = 400;
						compareAction(weight, adjacentPosition, "support", "shield");
					}
				}
			}
			else {
				//move
				
				// Move towards enemies, but try to stay close to allies
				if(adjacentLocalAllies == 0 && adjacentHexAllies == 0) {
					weight = 210;
					weight += 1.0/nearestAllyDistanceGlobal;
					weight += 0.1/nearestEnemyDistanceGlobal;
					compareAction(weight, adjacentPosition, "move", "move1"); 
				}
				
				if(adjacentLocalAllies == 0 && adjacentHexAllies == 1) {
					weight = 220;
					compareAction(weight, position, "move", "stay"); 
				}
				
				if(adjacentHexEnemies >= 1) {
					weight = 500;
					weight += adjacentHexAllies;
					compareAction(weight, adjacentPosition, "move", "move1"); 
				}
				
				weight = 200;
				weight += 1.0 / (nearestEnemyDistanceGlobal);
				compareAction(weight, adjacentPosition, "move", "move1"); 
				
				//System.out.println("weight " + weight + ", nearestEnemyDistance " + nearestEnemyDistance + " at (" + adjacentHex.getPosition().getX() + "," + adjacentHex.getPosition().getY() + ")");

				//System.out.println(result + ", nearestEnemyDistance " + nearestEnemyDistance);
				compareAction(weight, adjacentPosition, "move", "move1");      //move1
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
