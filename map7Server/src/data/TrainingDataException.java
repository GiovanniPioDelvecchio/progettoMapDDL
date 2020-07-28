package data;

/**
 * Classe che modella un'eccezione specificatamente lanciata durante la costruzione di un training set.<br>
 * Poichè è possibile lanciarla solamente all'interno del costruttore di <code>Data</code> ma è possibile gestirla in
 * un altro punto del programma, la classe è pubblica mentre il costruttore ha visibilità package.
 *
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
public class TrainingDataException extends Exception {

	/**
	 * Costruttore della Eccezione.
	 * Poiché è possibile lanciarla solo durante la costruzione di un'istanza di <code>Data</code>, la visibiltà
	 * è limitata al package. Chiama il costruttore di <code>Exception</code>.
	 * 
	 * @param message Stringa contente un messaggio descrittivo della situazione d'eccezione.
	 */
	TrainingDataException(String message) {

		super(message);
	}

	/**
	 * Sovrascrittura del metodo <code>toString</code> di <code>Object</code>.
	 * 
	 * @return una stringa che racchiude il nome e il messaggio dell'eccezione.
	 */
	public String toString() {
		
		return this.getClass().getName() + ": " + this.getMessage();
	}
	
}
