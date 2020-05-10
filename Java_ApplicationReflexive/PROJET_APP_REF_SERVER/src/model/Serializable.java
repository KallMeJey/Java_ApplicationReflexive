package model;

public abstract class Serializable {

	public abstract String serialize();
	
	public abstract Serializable deserialize(String line);
	
}
