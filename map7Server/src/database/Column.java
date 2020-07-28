package database;

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
	 * Costruttore di Column. Inizializza i campi di nome e tipo con quelli passati in input.
	 * 
	 * @param name Stringa contenente il nome da assegnare all'istanza di Column.
	 * @param type Stringa contenente il tipo della colonan da assegnare all'istanza di Column.
	 */
	Column(String name,String type) {
		
		this.name = name;
		this.type = type;
	}

	
	/**
	 * Metodo getter per ottenere il nome della colonna.
	 * 
	 * @return una Stringa contenente il nome della colonna.
	 */
	public String getColumnName() {
		
		return name;
	}

	/**
	 * Metodo utilizzato per stabilire se una colonna contiene degli attributi
	 * a dominio numerico (continuo) oppure no.
	 * 
	 * @return true se la colonna contiene valori di tipo numerico, false altrimenti.
	 */
	public boolean isNumber() {
		
		return type.equals("number");
	}

	/**
	 * Sovrascrittura del metodo toString() di Object.
	 * 
	 * @return Una stringa che concatena nome e tipo della colonna nel formato "[nome]:[tipo]".
	 */
	public String toString(){
		
		return name + ":" + type;
	}
}
