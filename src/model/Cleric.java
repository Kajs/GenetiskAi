package model;

public class Cleric extends Ai {
	
	public Cleric(double[][] weights) {
		setAiType("Cleric");
		setSupportAction("heal");
		initialHp = clericInitialHp;
		hp = initialHp;
		healAmount = clericHealAmount;
		meleeDamage = clericMeleeDamage;
		weightMatrix = weights;
    }
	
	//public void weight (Hex adjacentHex, ArrayList<Ai> enemies, ArrayList<Ai> allies, double myTeamHp, double enemyTeamHp, double totalEnemies, double totalAllies, double adjacentEnemies, double adjacentAllies) {
	public void weight() {
		
		Coordinate adjacentPosition = adjacentHex.getPosition();
		
		if (adjacentHex != null) {
			if(adjacentHex.isOccupied()) {
				if(adjacentHex.getAi().getTeam() != team) {
					//attack
					compareAction(totalWeight(0), adjacentPosition, "attack", "normal");    //normalAttack
					//compareAction(totalWeight(1, information), adjacentPosition, "attack", "stun");      //stunAttack
				}
				else {
					compareAction(totalWeight(aActions + 0), adjacentPosition, "support", "heal");
					//support
				}
			}
			else {
				//move
				compareAction(totalWeight(aActions + sActions + 0), adjacentPosition, "move", "move1");      //move1
				compareAction(totalWeight(aActions + sActions + 1), adjacentPosition, "move", "move2");      //move2
				compareAction(totalWeight(aActions + sActions + 2), position, "move", "stay");               //stay put
			}
		}
	}
}
