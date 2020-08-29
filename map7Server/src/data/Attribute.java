package data;

import java.io.Serializable;

/**
 * Classe astratta creata per modellare un generico attributo di un esempio.<br>
 * Essa tiene traccia del nome dell'attributo e dell'indice intero rappresentante la "colonna"
 * che nel dataset raccoglie i valori di tale attributo e che conseguentemente lo identifica.<br>
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
@SuppressWarnings("serial")
public abstract class Attribute implements Serializable {

	/**
	 * Nome simbolico dell'attributo
	 */
	private String name;

	/**
	 * Identificativo numerico dell'attributo
	 */
	private int index;

	/**
	 * Costruttore di <code>Attribute</code>.
	 * Inizializza il nome e l'indice dell'attributo con i parametri passati in input.
	 * 
	 * @param name Stringa contentente il nome dell'attributo.
	 * @param index Intero identificativo dell'attributo. Affinche' rappresenti una colonna del dataset,
	 * 				dovrebbe essere maggiore o uguale di 0. Tale controllo non è qui eseguito.
	 */
	public Attribute(String name, int index) {
		
		this.name = name;
		this.index = index;
	}

	/**
	 * Metodo getter per ottenere il nome dell'attributo.
	 * 
	 * @return un oggetto di tipo String contenente il nome dell'attributo.
	 */
	public String getName() {
	
		return name;
	}

	/**
	 * Metodo getter per ottenere l'indice dell'attributi.
	 * 
	 * @return L'intero rappresentante l'indice dell'attributo.
	 */
	public int getIndex() {
	
		return index;
	
	}

	/**
	 * Sovrascrittura del metodo <code>toString</code> di <code>Object</code>.
	 * 
	 * @return lo stato dell'attributo sottoforma di stringa.
	 */
	public String toString() {

		return getName();
	}

}
