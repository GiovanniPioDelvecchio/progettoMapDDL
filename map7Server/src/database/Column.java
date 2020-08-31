package database;

import util.Constants;

/**
 * Classe che modella una colonna di una tabella di un database. In particolare tiene traccia del nome
 * e del tipo dei valori contenuti.
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
public class Column {
	
	/**
	 * Stringa contenente il nome della colonna.
	 */
	private String name;
	
	/**
	 * Stringa contenente il nome del tipo dei valori contenuti nella colonna.
	 */
	private String type;

	/**
	 * Costruttore di <code>Column</code>. Inizializza i campi di nome e tipo con quelli passati in input.
	 * 
	 * @param name Stringa contenente il nome da assegnare all'istanza di <code>Column</code>.
	 * @param type Stringa contenente il tipo della colonna da assegnare all'istanza di <code>Column</code>.
	 */
	Column(String name, String type) {
		
		this.name = name;
		this.type = type;
	}

	
	/**
	 * Metodo getter per ottenere il nome della colonna.
	 * 
	 * @return Una stringa contenente il nome della colonna.
	 */
	public String getColumnName() {
		
		return name;
	}

	/**
	 * Metodo utilizzato per stabilire se una colonna contiene degli attributi
	 * a dominio numerico (continuo) oppure no.
	 * 
	 * @return True se la colonna contiene valori di tipo numerico, False altrimenti.
	 */
	public boolean isNumber() {
		
		return type.equals(Constants.SQL_NUMBER_TYPE);
	}

	/**
	 * Sovrascrittura del metodo <code>toString</code> di <code>Object</code>.
	 * 
	 * @return Una stringa che concatena nome e tipo della colonna nel formato "[nome]:[tipo]".
	 */
	public String toString() {
		
		return name + ":" + type;
	}
}
