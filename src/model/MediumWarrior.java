package model;

import java.util.ArrayList;
import control.Launcher;

public class MediumWarrior extends Ai {
	
	public MediumWarrior() {
		setAiType("Warrior");
		setSupportAction("shield");
		initialHp = 20;
		hp = initialHp;
		standardMeleeDamage = 5;
		meleeDamage = standardMeleeDamage;
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
		double result = 0;
		
		if (adjacentHex != null) {
			double nearestAllyDistance = information.get(6);
			double nearestEnemyDistance = information.get(10);
			
			if(adjacentHex.isOccupied()) {
				double aiInitialHp = adjacentHex.getAi().getInitialHp();
				
				if(adjacentHex.getAi().getTeam() != team) {
					//attack
					double nearestEnemyHp = information.get(9);
					double nearestEnemyShielded = information.get(12);
					double nearestEnemyStunned = information.get(11);
					
					result = 3.0 + 1.0/nearestEnemyHp + (totalEnemies - 1) - nearestEnemyShielded/10;
					compareAction(result, adjacentPosition, "attack", "normal");    //normalAttack
					
					result = (2.0 + nearestEnemyHp/aiInitialHp + (1.0/totalEnemies) + nearestEnemyShielded) * (1 - nearestEnemyStunned);
					compareAction(result, adjacentPosition, "attack", "stun");      //stunAttack
				}
				else {
					double nearestAllyHp = information.get(5);
					double nearestAllyShielded = information.get(8);
					
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