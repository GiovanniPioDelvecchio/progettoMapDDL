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
	public static final int MIN_PORT = 0;
	public static final int MAX_PORT = 65535;
	
	// Separatori per split effettuati su valori continui
	public static final String MINUS_EQUAL_COMPARATOR = "<=";
	public static final String GREATER_COMPARATOR = ">";
	public static final String DISCRETE_COMPARATOR = "=";
	
	// Costruzione della stringa di un nodo di split
	public static final String CONTINUOUS_PREFIX = "CONTINUOUS ";
	public static final String DISCRETE_PREFIX = "DISCRETE ";
	public static final String LEAF_PREFIX = "LEAF: class=";
	
	// Messaggi di comunicazione con il client
	public static final int CLIENT_CREATE = 0;
	public static final int CLIENT_SAVE = 1;
	public static final int CLIENT_LOAD = 2;
	public static final int CLIENT_END = -1;
	public static final int CLIENT_PREDICT = 3;
	public static final String CLIENT_ABORT = "#ABORT";
	public static final String SERVER_OK = "OK";
	public static final String SERVER_QUERY = "QUERY";
	
	// Stringhe per la stampa delle informazioni sull'albero
	public final static String BEGIN_RULES = "********* RULES **********\n";
	public final static String END_RULES = "*************************\n";
	public final static String BEGIN_TREE = "********* TREE **********\n";
	public final static String END_TREE = "*************************\n";
	
	// Espressione regolare per la creazione del file di log
	public final static String DATE_REGEX = "yyyy-MM-dd_HH-mm-ss";
	
	// Costanti per il mapping dei valori di MySQL a tipi di Java
	public final static String SQL_NUMBER_TYPE = "number";
	public final static String SQL_STRING_TYPE = "string";
	public final static String SQL_CHAR = "CHAR";
	public final static String SQL_VARCHAR = "VARCHAR";
	public final static String SQL_LONGVARCHAR = "LONGVARCHAR";
	public final static String SQL_BIT = "BIT";
	public final static String SQL_SHORT= "SHORT";
	public final static String SQL_INT = "INT";
	public final static String SQL_LONG = "LONG";
	public final static String SQL_FLOAT = "FLOAT";
	public final static String SQL_DOUBLE = "DOUBLE";
	
	public final static String SQL_TYPE_NAME = "TYPE_NAME";
	public final static String SQL_COLUMN_NAME = "COLUMN_NAME";
	
	/* MESSAGGI DI ECCEZIONE */
	// MainTest
	public static final String ERROR_BAD_PORT = "Impossibile utilizzare la porta selezionata:"
			+ " deve un intero compreso tra 0 e 65535\n";
	
	// Data
	public static final String ERROR_NO_DATABASE_CONNECTION = "Impossibile connettersi al database";
	public static final String ERROR_TABLE_NOT_FOUND = "Tabella non trovata";
	public static final String ERROR_TOO_FEW_ATTRIBUTES = "Gli attributi della tabella devono essere almeno due";
	public static final String ERROR_NO_CLASS_ATTRIBUTE = "Attributo di classe mancante";
	public static final String ERROR_BAD_DISCRETE_VALUE_CAST = "Errore durante il casting di un valore discreto";
	public static final String ERROR_EMPTY_TABLE = "Tabella vuota";
	public static final String ERROR_DB_CONNECTION_CLOSING = "Errore durante la chiusura della connesione con il database";
	public static final String ERROR_NULL_VALUES = "La tabella presenta esempi con attributi non avvalorati";
	
	// ContinousNode
	public static final String ERROR_BAD_TEST_CONDITION = "Si e' tentato di testare un nodo di split con "
			+ "un valore di split non esistente";
	
	// RegressionTree
	public static final String ERROR_BAD_VALUE_SELECTION = "La risposta dovrebbe essere un intero fra 0 e ";
	
	// MultiServer
	public static final String ERROR_LOG_FILE_CLOSING = "Failed to close the log file";
	
	// EmptySetException
	public static final String ERROR_EMPTY_DATABASE = "Database vuoto";
	
	// DatabaseConnectionException
	public static final String ERROR_DATABASE_CONNECTION_FAILURE = ": Errore durante la connessione con il Database";
	
}
