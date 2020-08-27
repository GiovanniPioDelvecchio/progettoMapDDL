package database;

import util.Constants;

/**
 * Classe che modella un'eccezione generata durante la connessione al Database.
 * Poich� pu� essere lanciata solamente all'interno del package <code>database</code>
 * ma gestita all'esterno, � pubblica mentre i suoi costruttori hanno visibilit� package.
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */

@SuppressWarnings("serial")
public class DatabaseConnectionException extends Exception {

	/**
	 * Costruttore a 0 args di DbCException.
	 * Richiama il costruttore della classe Exception.
	 */
	DatabaseConnectionException() {
		
		super();
	}
	
	/**
	 * Costruttore con messaggio di DbException.
	 * Richiama il costruttore con messaggio della classe Exception.
	 * 
	 * @param message stringa contenente la descrizione dell'errore che l'eccezione modella.
	 */
	DatabaseConnectionException(String message) {
		
		super(message);
	}
	
	/**
	 * Sovrascrittura del metodo <code>toString()</code> di <code>Object</code>
	 * 
	 * @return una stringa contenente il nome dell'eccezione concatenato al messaggio, se c'�, o
	 * 		   ad un messaggio generico.
	 */
	public String toString() {
		
		if (this.getMessage() == null ) {
		
			return this.getClass().getName() + Constants.ERROR_DATABASE_CONNECTION_FAILURE;
		} else {
		
			return this.getClass().getName() + ": " + this.getMessage();
		}
	}
}
