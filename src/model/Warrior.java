package model;

import java.awt.Color;
import java.util.ArrayList;

public class Warrior extends Ai {
	private Action bestAction;
	private int bestWeight;
	
	public Warrior(Coordinate startingPosition) {
		setPosition(startingPosition);
		setAiType("Warrior");
		setColor(Color.red);
		setHp(20);
		setMeleeDamage(5);
    }
	
	public Action action(ArrayList<ArrayList<Hex>> hexCake) {
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
						System.out.println("I found an enemy during loop " + i);
						enemies.add(foundAi);
					}
				}
			}
			Ai nearestEnemy = nearestEnemy(enemies);
			if (nearestEnemy != null) {
				weight(position.adjacentHex(position, i), nearestEnemy);
			}	
		}
		
		if(bestAction == null) {
			System.out.println("No best action found, staying put");
			bestAction = new Action(position, "move");
		}

		return bestAction;
	}
	
	public void weight (Coordinate adjacentHex, Ai nearestEnemy) {
		int w = position.distance(nearestEnemy.getPosition());
		String actionType;
		if (w == 1) {
			actionType = "attack";
		}
		else actionType = "move";
		
		System.out.println("Weigth, bestWeight: " + w + ", " + bestWeight + " adj x, y: " + adjacentHex.getX() + ", " + adjacentHex.getY() + " x, y: " + position.getX() + ", " + position.getY() );
		if (w > bestWeight) {
			bestWeight = w;
			bestAction = new Action(adjacentHex, actionType); //not done			 
		}
	}
}
