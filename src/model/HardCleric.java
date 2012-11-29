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
		getInformation();
		
		Coordinate adjacentPosition = adjacentHex.getPosition();
		double weight=0; 
		
		if (adjacentHex != null) {			
			if(adjacentHex.isOccupied()) {
				Ai adjacentAi = adjacentHex.getAi();
				
				if(adjacentAi.getTeam() != team) {
					//attack
					
					// Always attack unshielded enemy with less or equal hp to attack dmg, prefer clerics
					if (nearestEnemyHp <= meleeDamage && nearestEnemyShielded == 0 ) {
						weight = 1100;
						if (nearestEnemyIsWizard == 1) {
							weight+=1;
						}
						if (nearestEnemyIsCleric == 1) {
							weight+=2;
						}
						compareAction(weight, adjacentPosition, "attack", "normal");
					}
					
					else {
						weight = 600;
						weight += 1.0/nearestEnemyHp;
						compareAction(weight, adjacentPosition, "attack", "normal");
					}
				}
				else {
					//support
					weight = 800;
					
					double currentHp = adjacentAi.getHp();
					double initialHp = adjacentAi.getInitialHp();
					double healPotential = (initialHp - currentHp) / initialHp;
					
					//heal if possible
					
					if(healPotential > 0) {
						weight += healPotential;						
						compareAction(weight, adjacentPosition, "support", "heal");
					}
				}
			}
			else {
				//move
				
				// Move towards enemies, but try to stay close to allies
				if(adjacentLocalAllies == 0) {  //go to allies
					weight = 210;
					weight += 1.0/nearestAllyDistanceGlobal;
					weight += -0.1/nearestEnemyDistanceGlobal;
					weight += adjacentHexAllies;
					compareAction(weight, adjacentPosition, "move", "move1");
				}
				else {
					weight = 200;            //near allies, but away from enemies
					weight += -0.1 / (nearestEnemyDistanceGlobal);
					weight += 1.0 / (nearestAllyDistanceGlobal);
					weight += adjacentHexAllies;
					compareAction(weight, adjacentPosition, "move", "move1");
				}
				
				if(adjacentLocalAllies >= 1 && adjacentLocalEnemies == 0) {   //stay with allies if safe
					weight = 220;
					compareAction(weight, position, "move", "stay");
				}
				
				if(adjacentLocalEnemies >= 1) {         //run from enemies
					weight = 1000;
					weight -= 10 * adjacentHexEnemies;
					weight += adjacentHexAllies;
					compareAction(weight, adjacentPosition, "move", "move1");
				}
				
				//System.out.println("weight " + weight + ", nearestEnemyDistance " + nearestEnemyDistance + " at (" + adjacentHex.getPosition().getX() + "," + adjacentHex.getPosition().getY() + ")");
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
