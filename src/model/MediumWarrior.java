package model;

public class MediumWarrior extends Ai {
	
	public MediumWarrior() {
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
		double result = 0;
		
		if (adjacentHex != null) {
			
			if(adjacentHex.isOccupied()) {
				double aiInitialHp = adjacentHex.getAi().getInitialHp();
				
				if(adjacentHex.getAi().getTeam() != team) {
					//attack
					
					result = 3.0 + 1.0/nearestEnemyHp + (totalEnemies - 1) - nearestEnemyShielded/10;
					compareAction(result, adjacentPosition, "attack", "normal");    //normalAttack
					
					result = (2.0 + nearestEnemyHp/aiInitialHp + (1.0/totalEnemies) + nearestEnemyShielded) * (1 - nearestEnemyStunned);
					compareAction(result, adjacentPosition, "attack", "stun");      //stunAttack
				}
				else {					
					result = (aiInitialHp/nearestAllyHp + nearestEnemyDistance) * (1 - nearestAllyShielded);
					compareAction(result, adjacentPosition, "support", "shield");
					//support
				}
			}
			else {
				//move
				if(nearestEnemyDistance == 0) {result = 0; }
				else { result = 1.0/nearestEnemyDistance;}
				//if(nearestAllyDistance != 0) { result += 0.1/nearestAllyDistance;}
				//System.out.println(result + ", nearestEnemyDistance " + nearestEnemyDistance);
				compareAction(result, adjacentPosition, "move", "move1");      //move1
				//compareAction(0.0, adjacentPosition, "move", "move2");      //move2
				//compareAction(0.0, position, "move", "stay");               //stay put
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