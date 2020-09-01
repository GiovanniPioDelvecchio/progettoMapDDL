package database;

/**
 * Classe che modella un'eccezione lanciata in caso la tabella del database a cui si sta cercando di accedere
 * non presenti tuple.<br>
 * Poiche' può essere lanciata solamente all'interno del package <code>database</code> ma catturata all'esterno,
 * i costruttori hanno visibilità ristretta (default) rispetto alla classe (pubblica).
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
@SuppressWarnings("serial")
public class EmptySetException extends Exception {
	
	/**
	 * Costruttore a zero argomenti dell'eccezione.
	 * Richiama il costruttore della superclasse con un messaggio di default.
	 */
	EmptySetException() {

		super("Empty database");
	}
	
	/**
	 * Costruttore dell'eccezione con messaggio.
	 * Richiama il costruttore della superclasse con un messaggio passato in input.
	 * 
	 * @param errMessage Stringa contenente il messaggio personalizzato da assegnare all'eccezione.
	 */
	EmptySetException(String errMessage) {

		super(errMessage);
	}
}
