package progettoMapDDL.src.data;

public class DiscreteAttribute extends Attribute 
{
	//Attributi
	private String values[];
	
	//Metodi
	public DiscreteAttribute(String name, int index, String values[]) {
	
		super(name, index);
		this.values = values;
	}
	
	public int getNumberOfDistinctValues() {
	
		return values.length;
	}
	
	String getValue(int i) {
		
		if (i < getNumberOfDistinctValues()) {		
			return values[i];
		}
		return "out of bound";	//da sostituire con la 
								//giusta eccezione
	}

}

