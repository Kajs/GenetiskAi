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
	
	public Action action(Hex[] adjacentHexes, ArrayList<ArrayList<Hex>> hexCake, double myTeamHp, double enemyTeamHp, double totalEnemies, double totalAllies, double[][] adjacentAis, double[][] nearestAiDistances) {
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
			//double hp = information.get(0);                        // 00
			//double myTeamHp = information.get(1);                  // 01
			//double enemyTeamHp = information.get(2);               // 02
			double enemiesSize = information.get(3);   // 03
			double alliesSize = information.get(4);    // 04
			double nearestAllyHp = information.get(5);             // 05
			double nearestAllyDistance = information.get(6);       // 06
			double nearestALlyStunned = information.get(7);        // 07
			double nearestAllyShielded = information.get(8);       // 08
			double nearestEnemyHp = information.get(9);            // 09
			double nearestEnemyDistance = information.get(10);      // 10
			double nearestEnemyStunned = information.get(11);       // 11
			double nearestEnemyShielded = information.get(12);      // 12
			//double totalEnemies = information.get(13);              // 13
			//double totalAllies = information.get(14);               // 14
			double nearestEnemyIsWarrior = information.get(15);     // 15
			double nearestEnemyIsWizard = information.get(16);      // 16
			double nearestEnemyIsCleric = information.get(17);      // 17
			double nearestAllyIsWarrior = information.get(18);      // 18
			double nearestAllyIsWizard = information.get(19);       // 19
			double nearestAllyIsCleric = information.get(20);       // 20
			double nearestAllyIsBoosted = information.get(21);      // 21
			double nearestEnemyIsBoosted = information.get(22);     // 22
			//double adjacentEnemies = information.get(23);           // 23
			//double adjacentAllies = information.get(24);            // 24
			
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
				System.out.println("weight " + weight + ", nearestEnemyDistance " + nearestEnemyDistance + " at (" + adjacentHex.getPosition().getX() + "," + adjacentHex.getPosition().getY() + ")");

				
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
