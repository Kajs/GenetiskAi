package model;


public class MediumCleric extends Ai {
	
	public MediumCleric() {
		setAiType("Cleric");
		setSupportAction("heal");
		initialHp = clericInitialHp;
		hp = initialHp;
		healAmount = clericHealAmount;
		meleeDamage = clericMeleeDamage;
    }
	
	public void weight () {
		
		Coordinate adjacentPosition = adjacentHex.getPosition();
		double weight=0; 
		
		if (adjacentHex != null) {
			if(adjacentHex.isOccupied()) {
				Ai adjacentAi = adjacentHex.getAi();
				
				if(adjacentAi.getTeam() != team) {
					//attack
					weight = 300;
					compareAction(weight, adjacentPosition, "attack", "normal");
				}
				else {
					double currentHp = adjacentAi.getHp();
					double initialHp = adjacentAi.getInitialHp();
					double healPotential = (initialHp - currentHp) / initialHp;
					
					if(healPotential > 0) {
						weight = 400;
						compareAction(weight, adjacentPosition, "support", "heal");
					}
				}
			}
			else {
				//move
				weight = 200;            //near allies, but away from enemies
				weight += -0.1 / (nearestEnemyDistanceGlobal);
				weight += 1.0 / (nearestAllyDistanceGlobal);
				compareAction(weight, adjacentPosition, "move", "move1");
				
				// Move towards enemies, but try to stay close to allies
				if(adjacentLocalAllies == 0) {  //go to allies
					weight = 210;
					weight += 0.1/nearestAllyDistanceGlobal;
					weight += 1.0/averageAllyDistance;
					weight += -0.1/nearestEnemyDistanceGlobal;
					compareAction(weight, adjacentPosition, "move", "move2");
				}
				if(adjacentLocalAllies == 1 && adjacentHexEnemies == 0) {  //go to allies
					weight = 220;
					weight += 0.1/nearestAllyDistanceGlobal;
					weight += 1.0/averageAllyDistance;
					weight += 1.0/nearestEnemyDistanceGlobal;
					compareAction(weight, adjacentPosition, "move", "move3");
				}
				
				if(adjacentLocalAllies >= 2 && adjacentLocalEnemies == 0) {   //stay with allies if safe  måske 2 i vaerdi?
					weight = 220;
					compareAction(weight, position, "move", "stay");
				}
				
				if(adjacentLocalAllies < adjacentHexAllies) {   //go to most allies
					weight = 900;
					compareAction(weight, adjacentPosition, "move", "move4");
				}
				
				if(adjacentLocalEnemies >= 1) {         //run from enemies
					weight = 1000;
					weight -= 10 * adjacentHexEnemies;
					weight += adjacentHexAllies;
					compareAction(weight, adjacentPosition, "move", "move5");
				}
				
				//System.out.println("weight " + weight + ", nearestEnemyDistance " + nearestEnemyDistance + " at (" + adjacentHex.getPosition().getX() + "," + adjacentHex.getPosition().getY() + ")");
				//System.out.println(getId() + "E: " + nearestEnemyDistanceGlobal + ", A: " + nearestAllyDistanceGlobal + " at (" + adjacentHex.getPosition().getX() + "," + adjacentHex.getPosition().getY() + "), weight " + bestWeight);
			}
		}
	}
}
