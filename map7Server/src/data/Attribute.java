package data;

import java.io.Serializable;

/**
 * Classe astratta creata per modellare un generico attributo di un esempio.<br>
 * Essa tiene traccia del nome dell'attributo e dell'indice intero rappresentante la "colonna"
 * che nel dataset raccoglie i valori di tale attributo e che conseguentemente lo identifica.<br>
 * Una sottoclasse che eredita da Attribute non ha metodi astratti da implementare.<br>
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */

public abstract class Attribute implements Serializable {
	
	/**
	 * nome simbolico dell'attributo
	 */
	private String name;

	/**
	 * identificativo numerico dell'attributo
	 */
	private int index;
	
	/**
	 * Costruttore di Attribute
	 * Inizializza il nome e l'indice dell'attributo con i parametri passati in input.
	 * @param name
	 * @param index
	 */
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
