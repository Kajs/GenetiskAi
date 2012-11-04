package model;

import java.util.ArrayList;
import control.Launcher;

public class Cleric extends Ai {
	
	public Cleric(double[][] weights) {
		setAiType("Cleric");
		setSupportAction("heal");
		initialHp = 15;
		hp = initialHp;
		standardMeleeDamage = 2.5;
		healAmount = 5.0;
		meleeDamage = standardMeleeDamage;
		weightMatrix = weights;
    }
	
	public Action action(Hex[] adjacentHexes, ArrayList<ArrayList<Hex>> hexCake, double myTeamHp, double enemyTeamHp, double totalEnemies, double totalAllies, double[][] adjacentAis) {
		bestAction = null;
		bestWeight = (int)Math.pow(-2, 31);
		for (int i = 0; i < hexCake.size(); i++) {
			ArrayList<Hex> slice = hexCake.get(i);
			ArrayList<Ai> enemies = new ArrayList<Ai>();
			ArrayList<Ai> allies = new ArrayList<Ai>();
			
			for (Hex hex : slice) {
				if (hex.isOccupied()) {
					Ai foundAi = hex.getAi();
					if(foundAi.getTeam() == getTeam()) {
						allies.add(foundAi);
					}
					else {
						//System.out.println("I found an enemy during loop " + i);
						enemies.add(foundAi);
					}
				}
			}
			if (totalEnemies > 0 && adjacentHexes[i] != null) {
				weight(adjacentHexes[i], enemies, allies, myTeamHp, enemyTeamHp, totalEnemies, totalAllies, adjacentAis[0][i], adjacentAis[1][i]);
			}	
		}
		
		if(bestAction == null) {
			bestAction = new Action(position, "move", "stay");
		}
		if(Launcher.allowActionOutput) {System.out.println(aiType + ", team " + team + " at (" + position.getX() + ", " + position.getY() + ") chose " + bestAction.getBaseType() + ", " + bestAction.getExtendedType() + " on (" + bestAction.getPosition().getX() + ", " + bestAction.getPosition().getY() + ")");}
		
		return bestAction;
	}
	
	public void weight (Hex adjacentHex, ArrayList<Ai> enemies, ArrayList<Ai> allies, double myTeamHp, double enemyTeamHp, double totalEnemies, double totalAllies, double adjacentEnemies, double adjacentAllies) {
		ArrayList<Double> information = getInformation(adjacentHex, enemies, allies, myTeamHp, enemyTeamHp, totalEnemies, totalAllies, adjacentEnemies, adjacentAllies);
		
		Coordinate adjacentPosition = adjacentHex.getPosition();
		
		if (adjacentHex != null) {
			if(adjacentHex.isOccupied()) {
				if(adjacentHex.getAi().getTeam() != team) {
					//attack
					compareAction(totalWeight(0, information), adjacentPosition, "attack", "normal");    //normalAttack
					//compareAction(totalWeight(1, information), adjacentPosition, "attack", "stun");      //stunAttack
				}
				else {
					compareAction(totalWeight(aActions + 0, information), adjacentPosition, "support", "heal");
					//support
				}
			}
			else {
				//move
				compareAction(totalWeight(aActions + sActions + 0, information), adjacentPosition, "move", "move1");      //move1
				compareAction(totalWeight(aActions + sActions + 1, information), adjacentPosition, "move", "move2");      //move2
				compareAction(totalWeight(aActions + sActions + 2, information), position, "move", "stay");               //stay put
			}
		}
	}
    
    public double totalWeight(int action, ArrayList<Double> information) {
    	double result = 0.0;
    	for (int i = 0; i < information.size(); i++) {
    		result = result + weightMatrix[action + 1][i] * information.get(i);
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
