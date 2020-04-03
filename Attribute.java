
public abstract class Attribute 
{
	//Attributi
	private String name; //nome simbolico dell'attributo
	private int index; 	//identificativo numerico dell'attributo
	
	//Metodi
	Attribute(String name, int index)
	{
		this.name = name;
		this.index = index;
	}
	
	String getName() 
	{
		return name;
	}
	
	int getIndex() 
	{
		return index;
	}
	
	public String toString() 
	{
		return getName() + " " + getIndex();
	}
}

