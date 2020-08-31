package data;

/**
 * Classe che modella un'eccezione specificatamente lanciata durante la costruzione di un training set.<br>
 * Poiche' e' possibile lanciarla solamente all'interno del costruttore di <code>Data</code> ma e' possibile gestirla in
 * un altro punto del programma, la classe è pubblica mentre il costruttore ha visibilita' package.
 *
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
@SuppressWarnings("serial")
public class TrainingDataException extends Exception {

	/**
	 * Costruttore dell'eccezione.
	 * Poiché è possibile lanciarla solo durante la costruzione di un'istanza di <code>Data</code>, la visibilta'
	 * e' limitata al package.
	 * 
	 * @param message Stringa contente un messaggio descrittivo della situazione d'eccezione.
	 */
	TrainingDataException(String message) {

		super(message);
	}

	/**
	 * Sovrascrittura del metodo <code>toString</code> di <code>Object</code>.
	 * 
	 * @return Una stringa che racchiude il nome e il messaggio dell'eccezione.
	 */
	public String toString() {
		
		return this.getClass().getName() + ": " + this.getMessage();
	}
	
}
