package model;

public class Action {
	private Coordinate position;
	private String type;
	
	public Action(Coordinate position, String type) {
		this.position = position;
		this.type = type;
	}
	
	
	public Coordinate getPosition() {
		return position;
	}
	
	public String getType() {
		return type;
	}
}
