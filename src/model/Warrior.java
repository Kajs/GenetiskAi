package model;

public class Warrior extends Ai {
	
	public Warrior(double[][] weights) {
		setAiType("Warrior");
		setSupportAction("shield");
		initialHp = warriorInitialHp;
		hp = initialHp;
		meleeDamage = warriorMeleeDamage;
		weightMatrix = weights;
    }
	
	//public void weight (Hex adjacentHex, ArrayList<Ai> enemies, ArrayList<Ai> allies, double myTeamHp, double enemyTeamHp, double totalEnemies, double totalAllies, double adjacentEnemies, double adjacentAllies) {
	public void weight() {
		getInformation();
		
		Coordinate adjacentPosition = adjacentHex.getPosition();
		
		if (adjacentHex != null) {
			if(adjacentHex.isOccupied()) {
				if(adjacentHex.getAi().getTeam() != team) {
					//attack
					compareAction(totalWeight(0), adjacentPosition, "attack", "normal");    //normalAttack
					compareAction(totalWeight(1), adjacentPosition, "attack", "stun");      //stunAttack
				}
				else {
					compareAction(totalWeight(aActions + 0), adjacentPosition, "support", "shield");
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
    
    public double totalWeight(int action) {
    	double result = 0.0;
    	for (int i = 0; i < information.length; i++) {
    		result = result + weightMatrix[action + 1][i] * information[i];
    	}
    	result = result * weightMatrix[0][action];
    	return result;
    }
    
    public void compareAction(double result, Coordinate position, String baseType, String extendedType) {
		if (result > bestWeight) {
			bestAction = new Action(position, baseType, extendedType);
			bestWeight = result;
		}
	}
}
