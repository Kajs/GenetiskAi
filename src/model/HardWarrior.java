package model;

public class HardWarrior extends Ai {
	
	public HardWarrior() {
		setAiType("Warrior");
		setSupportAction("shield");
		initialHp = 20;
		hp = initialHp;
		standardMeleeDamage = 5;
		meleeDamage = standardMeleeDamage;
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
					if (totalAllies > totalEnemies) {
						weight = 800;
						weight += nearestEnemyHp;
						compareAction(weight, adjacentPosition, "attack", "stun");
						
					} else {
						weight = 800;
						weight += 1.0/nearestEnemyHp;
						compareAction(weight, adjacentPosition, "attack", "normal");
					}
					
				}
				else {
					//support
					
					// Shield ally if HP is low, modified by number of enemies next to him and type
					if (nearestAllyHp < 8 && nearestAllyShielded == 0) {
						weight = 900;
						weight += adjacentEnemies;
						if (nearestAllyIsWizard == 1) {
							weight+=0.5;
						}
						if (nearestAllyIsCleric == 1) {
							weight+=1;
						}
						compareAction(weight, adjacentPosition, "support", "shield");
					}
				}
			}
			else {
				//move
				
				// Move towards enemies, but try to stay close to allies
				weight = 200;
				if(nearestEnemyDistance > 0) {weight += 10 + 1.0/nearestEnemyDistance;}
				if(nearestAllyDistance > 0) {weight += 5 + 1.0/nearestAllyDistance;}
				weight += adjacentEnemies;
				weight += adjacentAllies/10.0;
				//System.out.println("weight " + weight + ", nearestEnemyDistance " + nearestEnemyDistance + " at (" + adjacentHex.getPosition().getX() + "," + adjacentHex.getPosition().getY() + ")");

				
				//if(nearestAllyDistance != 0) { result += 0.1/nearestAllyDistance;}
				//System.out.println(result + ", nearestEnemyDistance " + nearestEnemyDistance);
				compareAction(weight, adjacentPosition, "move", "move1");      //move1
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
