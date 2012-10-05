package model;

public class Ai {
	private Coordinate position;
	
	
	public Ai(Coordinate startingPosition) {
		position = startingPosition;
		int profession = 1;		
		
	}
	
	public Coordinate moveAction() {
		position.setX((position.getX()+1));
		position.setY((position.getY()+1));
		return position;
	}

}
