package server;

/**
 * Classe che modella un'eccezione sollevata se viene immesso dall'utente un valore non valido.
 * In particolare viene utilizzato durante l'esplorazione di un albero di regressione
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
@SuppressWarnings("serial")
public class UnknownValueException extends Exception {

	/**
	 * Costruttore a zero argomenti, che richiama semplicemente
	 * il costruttore di Exception.
	 */
	public UnknownValueException() {

		super();
	}

	/**
	 * Costruttore che permette di associare un messaggio descrittivo all'eccezione.
	 * 
	 * @param message Stringa contenente un messaggio di errore.
	 */
	public UnknownValueException(String message) {

		super(message);
	}
}
