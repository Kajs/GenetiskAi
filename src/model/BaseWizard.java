package model;
/*
import java.awt.Color;
import java.util.ArrayList;
*/

public class BaseWizard extends Ai {
	/*
	private Action bestAction;
	private double bestWeight;
	
	public BaseWizard(Coordinate startingPosition, int team) {
		setPosition(startingPosition);
		setAiType("BaseWizard");
		setColor(Color.red);
		setHp(15);
		setMeleeDamage(4);
		this.team = team;
		generateId();
    }
	
	public Action action(Hex[] adjacentHexes, ArrayList<ArrayList<Hex>> hexCake, int myTeamHp, int enemyTeamHp) {
		bestAction = null;
		bestWeight = 0; //needs certain minimum
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
			Ai nearestEnemy = nearestAi(enemies);
			if (nearestEnemy != null) {
				weight(adjacentHexes[i], enemies, allies, myTeamHp, enemyTeamHp);
			}	
		}
		
		if(bestAction == null) {
			System.out.println("No best action found, staying put");
			bestAction = new Action(position, "move");
		}
		System.out.println(id + " chose " + bestAction.getType() + " at (" + bestAction.getPosition().getX() + "," + bestAction.getPosition().getY() + ")");
		
		return bestAction;
	}
	
	public void weight (Hex adjacentHex, ArrayList<Ai> enemies, ArrayList<Ai> allies, int myTeamHp, int enemyTeamHp) {
		Ai nearestEnemy = nearestAi(enemies);
		Ai nearestAlly = nearestAi(allies);
		
		if (adjacentHex != null) {
			String actionType;
			double w;
			if(adjacentHex.isOccupied()) {
				if(adjacentHex.getAi().getTeam() != team) {
					
					actionType = "attack";
					w = 100-nearestEnemy.getHp();
				}
				else {
					actionType = "support";
					w = -1;
				}
			}
			else {
				actionType = "move";
				if (nearestAlly != null) {
					w = 20-position.distance(nearestAlly.getPosition());	
				}
				else {
					w = 10-position.distance(nearestEnemy.getPosition());
				}
			}
			
			if (w>bestWeight) {
				bestWeight = w;
				bestAction = new Action(adjacentHex.getPosition(), actionType); //not done			 	
			}
			
		}
	}
	*/
}
