package util;

/**
 * Classe utilizzata per le costanti del server.
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
public class Constants {

	// Porta di default dove viene eseguito il server
	public static final int DEFAULT_PORT = 8080;
	
	// Separatori per split effettuati su valori continui
	public static final String MINUS_EQUAL_COMPARATOR = "<=";
	public static final String GREATER_COMPARATOR = ">";
	public static final String DISCRETE_COMPARATOR = "=";
	
	// Costruzione della stringa di un nodo di split
	public static final String CONTINUOUS_PREFIX = "CONTINUOUS ";
	public static final String DISCRETE_PREFIX = "DISCRETE ";
	public static final String LEAF_PREFIX = "LEAF: class=";
	
	// Messaggi di comunicazione con il client
	public final static String SERVER_OK = "SERVER_OK";
	public final static String SERVER_QUERY = "QUERY";
	public final static String CLIENT_ABORT = "#ABORT";
	
	// Stringhe per la stampa delle informazioni sull'albero
	public final static String BEGIN_RULES = "********* RULES **********\n";
	public final static String END_RULES = "*************************\n";
	public final static String BEGIN_TREE = "********* TREE **********\n";
	public final static String END_TREE = "*************************\n";
	
	// Espressione regolare per la creazione del file di log
	public final static String DATE_REGEX = "yyyy-MM-dd_HH-mm-ss";
	
	/* MESSAGGI DI ECCEZIONE */
	
	// Data
	public static final String NO_DATABASE_CONNECTION = "Impossibile connettersi al database";
	public static final String TABLE_NOT_FOUND = "Tabella non trovata";
	public static final String TOO_FEW_ATTRIBUTES = "Gli attributi della tabella devono essere almeno due";
	public static final String NO_CLASS_ATTRIBUTE = "Attributo di classe mancante";
	public static final String BAD_DISCRETE_VALUE_CAST = "Errore durante il casting di un valore discreto";
	public static final String EMPTY_TABLE = "Tabella vuota";
	public static final String ERROR_DB_CONNECTION_CLOSING = "Errore durante la chiusura della connesione con il database";
	
	
	// ContinousNode
	public static final String BAD_TEST_CONDITION = "Si e' tentato di testare un nodo di split con "
			+ "un valore di split non esistente";
	
	// RegressionTree
	public static final String BAD_VALUE_SELECTION = "La risposta dovrebbe essere un intero fra 0 e ";
	
	// MultiServer
	public static final String ERROR_LOG_FILE_CLOSING = "Failed to close the log file";
}
