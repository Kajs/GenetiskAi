package model;

import java.awt.Color;
import java.util.ArrayList;
import control.Launcher;

public class BaseWarrior2 extends Ai {
	
	public BaseWarrior2() {
		setAiType("Warrior");
		setSupportAction("shield");
		setColor(Color.red);
		initialHp = 20;
		hp = initialHp;
		standardMeleeDamage = 5;
		meleeDamage = standardMeleeDamage;
    }
	
	public Action action(Hex[] adjacentHexes, ArrayList<ArrayList<Hex>> hexCake, double myTeamHp, double enemyTeamHp, double totalEnemies, double totalAllies) {
		bestAction = null;
		for (int i = 0; i < hexCake.size(); i++) {
			ArrayList<Hex> slice = hexCake.get(i);
			boolean hasEnemies = false;
			
			for (Hex hex : slice) {
				if (hex.isOccupied()) {
					Ai foundAi = hex.getAi();
					if(foundAi.getTeam() != getTeam()) {
						hasEnemies = true;
					}
				}
			}
			if (totalEnemies > 0 && adjacentHexes[i] != null) {
				weight(adjacentHexes[i], hasEnemies);
			}	
		}
		
		if(bestAction == null) {
			if(!Launcher.isAutomatic) {System.out.println("No best action found, staying put");}
			bestAction = new Action(position, "move", "stay");
		}
		if(!Launcher.isAutomatic) {System.out.println(aiType + ", team " + team + " at (" + position.getX() + ", " + position.getY() + ") chose " + bestAction.getBaseType() + ", " + bestAction.getExtendedType() + " on (" + bestAction.getPosition().getX() + ", " + bestAction.getPosition().getY() + ")");}
		
		return bestAction;
	}
	
	public void weight (Hex adjacentHex, boolean hasEnemies) {
		
		Coordinate adjacentPosition = adjacentHex.getPosition();
		
		if (adjacentHex != null) {
			if(adjacentHex.isOccupied()) {
				if(adjacentHex.getAi().getTeam() != team) {
					//attack
					bestAction = new Action(adjacentPosition, "attack", "normal");
				}
			}
			else {
				if (bestAction == null) {
					bestAction = new Action(adjacentPosition, "move", "base");
				}
			}
		}
	}
}