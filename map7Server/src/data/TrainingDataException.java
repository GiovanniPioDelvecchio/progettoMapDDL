package data;

/**
 * Classe che modella un'eccezione specificatamente lanciata durante la costruzione di un training set.<br>
 * Poich� � possibile lanciarla solamente all'interno del costruttore di <code>Data</code> ma � possibile gestirla in
 * un altro punto del programma, la classe � pubblica mentre il costruttore ha visibilit� package.
 *
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
public class TrainingDataException extends Exception {

	/**
	 * Costruttore della Eccezione.
	 * Poich� � possibile lanciarla solo durante la costruzione di un'istanza di <code>Data</code>, la visibilt�
	 * � limitata al package. Chiama il costruttore di <code>Exception</code>.
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
