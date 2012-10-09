package model;

public class OffensiveAction {
	private String type;
	private Coordinate target;
	private int damage;
	private boolean isStunDamage;
	
	public OffensiveAction (String type, Coordinate target, int damage, boolean isStunDamage) {
		this.type = type;
		this.target = target;
		this.damage = damage;
		this.isStunDamage = isStunDamage;
	}
	
	public String getType() {
		return type;
	}
	
	public Coordinate getTarget() {
		return target;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public boolean isStunDamage() {
		return isStunDamage;
	}
}
