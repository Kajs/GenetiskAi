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
		ArrayList<Ai> choices = new ArrayList<Ai>();
		boolean isFirst = true;
		bestWeight = 0; //needs certain minimum
		
		for (int i = 0; i < hexCake.size(); i++) {
			ArrayList<Hex> slice = hexCake.get(i);
			ArrayList<Ai> enemies = new ArrayList<Ai>();
			ArrayList<Ai> allies = new ArrayList<Ai>();
			
			for (Hex hex : slice) {
				//System.out.println("Hex - team: " + hex.getTeam() + ", isOccupied: " + hex.isOccupied());
				if(hex.getPosition().getX() == 11 && hex.getPosition().getY() == 10) {
					System.out.println("I found 11, 10");
				}
				if (hex.isOccupied()) {
					if(hex.getAi().getTeam() == this.getTeam()) {
						System.out.println("Adding ai to allies");
						allies.add(hex.getAi());
					}
					else { enemies.add(hex.getAi());
					System.out.println("Adding ai to enemies");}
				}
			}
			System.out.println("Before calc action");
			if(isFirst) {
				if (!enemies.isEmpty()) {
					weight(position.adjacentHex(position, i), nearestEnemy(enemies));
					isFirst = false;
				}
			}
			else {
				if (!enemies.isEmpty()) {
					weight(position.adjacentHex(position, i), nearestEnemy(enemies));
				}
			}
		}
		return bestAction;
	}
	
	public void weight (Coordinate adjacentHex, Ai ai) {
		System.out.println("Starting weight function");
		int weight = this.getPosition().distance(ai.getPosition());
		if (weight > bestWeight) {
			System.out.println("I made an action");
			bestWeight = weight;
			bestAction = new Action(adjacentHex, "move"); //not done			 
		}
	}
}
