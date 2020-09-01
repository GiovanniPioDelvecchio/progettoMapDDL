package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import util.Constants;

/**
 * Classe che modella un accesso ad un database MySQL.<br> 
 * In particolare la classe stabilisce una connessione di default al database "MapDB" locato sulla macchina
 * dove il programma viene lanciato alla porta 3306.
 * L'utente utilizzato dal server e' "MapUser".
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
public class DbAccess {

	/**
	 * Costante contenente il nome della classe-Driver per accedere ad un database MySQL
	 */
	private final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
	
	/**
	 * Costante contenente il nome del DBMS a cui il programma deve accedere. Nel caso in esame è MySQL
	 */
	private final String DBMS = "jdbc:mysql"; 
	
	/**
	 * Stringa contenente l'indirizzo IP del server dove si trova il database.
	 * Il valore localhost indica la macchina su cui si sta eseguendo il server.
	 */
	private String SERVER = "localhost";
	
	/**
	 * Stringa contenente il nome della base di dati a cui accedere
	 */
	private String DATABASE = "MapDB";
	
	/**
	 * Costante contenente il numero di porta del server MySQL
	 */
	private final String PORT = "3306"; 
	
	/**
	 * Stringa contenente lo UserName utilizzato per accedere al database
	 */
	private String USER_ID = "MapUser";
	
	/**
	 * Stringa contenente la password associata all'UserName da utilizzare per accedere al db
	 */
	private String PASSWORD = "map";
	
	/**
	 * Istanza di Connection che modella la connessione al database, da inizializzare.
	 */
	private Connection conn;
	
	/**
	 * Metodo per inizializzare la connessione alla base di dati.
	 * A tale fine fa utilizzo delle informazioni di default.
	 * 
	 * @throws DatabaseConnectionException Lanciata in caso non venga trovata la classe del driver,
	 * 		   in caso ci sia errore o accesso illegale ad una classe durante l'istanziazione del driver 
	 *         o se viene sollevata un'eccezione durante la creazione della connessione.
	 */
	public void initConnection() throws DatabaseConnectionException {
		
		try {
			
			// Crea una istanza del driver per verificare che esso sia disponibile per l'utilizzo
			Class.forName(DRIVER_CLASS_NAME).newInstance();
			
			/**
			 * In caso la classe del driver non sia disponibile, oppure vengano
			 * generati errori durante la sua istanziazione, tali eccezioni vengono
			 * propagate come DatabaseConnectionException.
			 * Il messaggio di errore viene però stampato nella console e non inserito nell'eccezione
			 * poiche' non e' necessario notificare al Client la natura dell'errore durante la connessione.
			 */
		} catch(ClassNotFoundException e) {
			
			System.out.println("[!] Driver not found: " + e.getMessage());
			throw new DatabaseConnectionException();
			
		} catch(InstantiationException e){
			
			System.out.println("[!] Error during the instantiation : " + e.getMessage());
			throw new DatabaseConnectionException();
			
		} catch(IllegalAccessException e){
			
			System.out.println("[!] Cannot access the driver : " + e.getMessage());
			throw new DatabaseConnectionException();
		}
		
		// Si compone la stringa per la connessione al database
		String connectionString = DBMS + "://" + SERVER + ":" + PORT + "/" + DATABASE
				+ "?user=" + USER_ID + "&password=" + PASSWORD + "&serverTimezone=UTC";
		
		try {
			// Si tenta la connessione al database...
			conn = DriverManager.getConnection(connectionString);
			
		} catch (SQLException e) {
			
			// ...e in caso di errore, si stampa un messaggio lato server e 
			// si rilancia una DbCException per notificare il client.
			System.out.println("[!] SQLException: " + e.getMessage());
			System.out.println("[!] SQLState: " + e.getSQLState());
			System.out.println("[!] VendorError: " + e.getErrorCode());
			throw new DatabaseConnectionException();
		}
	}
	
	/**
	 * Metodo getter per ottenere la connessione incapsulata in un'istanza di DBAccess.
	 *
	 * @return La connessione al database se inizializzata, null altrimenti.
	 */
	public Connection getConnection() {

		return conn;
	}

	/**
	 * Chiude la connessione al database. Se la connessione non è stata inizializzata, non esegue alcuna operazione.
	 * 
	 * @throws DatabaseConnectionException Lanciata se la chiusura di una connessione genera una <code>SQLExcepition</code>.
	 */
	public void closeConnection() throws DatabaseConnectionException {
		
		try {
			
			if (conn != null) {
				
				conn.close();
			}
		} catch (SQLException e) {

			throw new DatabaseConnectionException(Constants.ERROR_DB_CONNECTION_CLOSING);
		}
	}
	
}
