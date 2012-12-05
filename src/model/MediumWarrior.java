package model;


public class MediumWarrior extends Ai {
	
	public MediumWarrior() {
		setAiType("Warrior");
		setSupportAction("shield");
		initialHp = warriorInitialHp;
		hp = initialHp;
		meleeDamage = warriorMeleeDamage;
    }
	
	public void weight () {
		
		Coordinate adjacentPosition = adjacentHex.getPosition();
		double weight=0; 
		
		if (adjacentHex != null) {			
			if(adjacentHex.isOccupied()) {
				Ai adjacentAi = adjacentHex.getAi();
				if(adjacentAi.getTeam() != team) {
					//attack
					weight = 400;
					compareAction(weight, adjacentPosition, "attack", "normal");
					
					if(nearestEnemyIsWizard == 1 && adjacentLocalAllies >= 2 && nearestEnemyStunned == 0) {
						weight = 500;
						compareAction(weight, adjacentPosition, "attack", "stun");
					}
				}
				else {
					if(!adjacentAi.getShielded()) {
						weight = 300;
						compareAction(weight, adjacentPosition, "support", "shield");
					}	
				}
			}
			else {
				weight = 200;            //near allies, but away from enemies
				weight += 1.0 / (nearestEnemyDistanceGlobal);
				weight += 0.1 / nearestAllyDistanceGlobal;
				if(adjacentHexEnemies == 1) {weight += 1;}
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
					weight += 1.0/nearestEnemyDistanceGlobal;
					weight -= 0.0001/averageEnemyDistance;
					compareAction(weight, adjacentPosition, "move", "move5"); 
				} 
				
				if(adjacentLocalEnemies == 0 && adjacentHexEnemies >= 1 && hp == initialHp) {
					weight = 250;
					weight += adjacentHexAllies;
					compareAction(weight, adjacentPosition, "move", "move6");
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
