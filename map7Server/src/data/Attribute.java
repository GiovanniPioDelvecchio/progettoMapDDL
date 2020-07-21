package data;

import java.io.Serializable;

public abstract class Attribute implements Serializable {
	
	// Attributi
	private String name;	// nome simbolico dell'attributo
	
	private int index; 		// identificativo numerico dell'attributo
	
	// Metodi
	public Attribute(String name, int index) {
		
		this.name = name;
		this.index = index;
	}
		
	public String getName() {
	
		return name;
	}
	
	public int getIndex() {
	
		return index;
	
	}

	public String toString() {
	
		return getName();
	}
}
