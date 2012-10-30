package model;

public class Action {
	private Coordinate position;
	private String baseType;
	private String extendedType;
	
	public Action(Coordinate position, String baseType, String extendedType) {
		this.position = position;
		this.baseType = baseType;
		this.extendedType = extendedType;
	}
	
	
	public Coordinate getPosition() {
		return position;
	}
	
	public String getBaseType() {
		return baseType;
	}
	
	public String getExtendedType() {
		return extendedType;
	}
}
