package server;

/**
 * Eccezione che modella un errore da parte dell'utente durante l'esplorazione di un
 * albero di regressione.
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
public class UnknownValueException extends Exception {

	/**
	 * Costruttore a zero argomenti, che richiama semplicemente
	 * il costruttore di Exception.
	 */
	public UnknownValueException() {

		super();
	}

	/**
	 * Costruttore che permette di specificare la causa del sollevamento dell'eccezione.
	 * 
	 * @param message Stringa contenente un messaggio di errore.
	 */
	public UnknownValueException(String message) {

		super(message);
	}
}
