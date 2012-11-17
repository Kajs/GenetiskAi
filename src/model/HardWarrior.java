package model;

import java.util.ArrayList;
import control.Launcher;

public class HardWarrior extends Ai {
	
	public HardWarrior() {
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
		double weight=0; 
		
		if (adjacentHex != null) {
			if(adjacentHex.isOccupied()) {
				if(adjacentHex.getAi().getTeam() != team) {
					//attack
					
					// Always attack unshielded enemy with less or equal hp to attack dmg, prefer clerics
					if (information.get(9) <= meleeDamage && information.get(12) == 0 ) {
						weight = 100;
						if (information.get(16) == 1) {
							weight+=1;
						}
						if (information.get(17) == 1) {
							weight+=2;
						}
						compareAction(weight, adjacentPosition, "attack", "normal");
					}
					
					// Stun boosted enemy if warrior, HP is low and we are not shielded
					if (information.get(22) == 1 && information.get(15) == 1 && information.get(0) < 8 && this.getShielded() == false) {
						weight = 60;
						compareAction(weight, adjacentPosition, "attack", "stun");
					}
					
					// Prefer normal attack on isolated enemies, preferring clerics, then wizards, last warriors
					weight = 40 - information.get(23);
					if (information.get(16) == 1) {
						weight+=1;
					}
					if (information.get(17) == 1) {
						weight+=2;
					}
					compareAction(weight, adjacentPosition, "attack", "normal");
					
				}
				else {
					//support
					
					// Shield ally if HP is low, modified by number of enemies next to him and type
					if (information.get(5) < 8 && information.get(8) == 0) {
						weight = 80;
						weight += information.get(23);
						if (information.get(19) == 1) {
							weight+=1;
						}
						if (information.get(20) == 1) {
							weight+=2;
						}
						compareAction(weight, adjacentPosition, "support", "shield");
					}
				}
			}
			else {
				//move
				
				// Move towards enemies, but try to stay close to allies
				weight = 20;
				weight += information.get(3) +1;
				weight += information.get(4) * (information.get(6)-1);
				compareAction(weight, adjacentPosition, "move", "move");
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
