package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe che modella un accesso ad un database MySQL.<br> In particolare la classe stabilisce una connessione
 * di default al database "MapDB" locato sulla macchina dove il programma viene lanciato alla porta 3306 con
 * credenziali "MapUser" e "map" (UserName e Password).
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */

public class DbAccess {

	/**
	 * Costante privata contenente il nome della classe-Driver per accedere ad un database MySQL
	 */
	private final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
	
	/**
	 * Costante privata contenente il nome del DBMS a cui il programma deve accedere. Nel caso in esame è MySQL
	 */
	private final String DBMS = "jdbc:mysql"; 
	
	/**
	 * Stringa contenente il nome del server dov'è locato il database.
	 */
	private String SERVER= "localhost";
	
	/**
	 * Stringa contenente il nome della base di dati a cui accedere
	 */
	private String DATABASE = "MapDB";
	
	/**
	 * Costante privata contenente il numero di porta del server MySQL
	 */
	private final String PORT = "3306"; 
	
	/**
	 * Stringa contenente l'id da user utilizzato per accedere al database
	 */
	private String USER_ID = "MapUser";
	
	/**
	 * Stringa contenente la password corredata all'UserName da utilizzare per accedere al db
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
	 * @throws DatabaseConnectionException Lancia un'eccezione in caso non venga trovata la classe del driver,
	 * 		   in caso ci sia errore o accesso illegale ad una classe durante l'istanziazione del driver 
	 *         o se viene sollevata un'eccezione durante la creazione della connessione.
	 */
	public void initConnection() throws DatabaseConnectionException {
		
		try {
			
			//Crea una istanza del driver per verificare che esso sia disponibile per l'utilizzo
			Class.forName(DRIVER_CLASS_NAME).newInstance();
			
			/**
			 * In caso la classe del driver non sia disponibile, oppure vengano
			 * generati errori durante la sua istanziazione, tali eccezioni vengono
			 * "rimbalzate" come DbCException.
			 * Il messaggio di errore viene però stampato nella console e non inserito nell'eccezione
			 * poichè non è necessario notificare al Client la natura dell'errore durante la connessione.
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
		
		String connectionString = DBMS + "://" + SERVER + ":" + PORT + "/" + DATABASE
				+ "?user=" + USER_ID + "&password=" + PASSWORD + "&serverTimezone=UTC";
		
		try {

			conn = DriverManager.getConnection(connectionString);
			
		} catch(SQLException e) {
			
			System.out.println("[!] SQLException: " + e.getMessage());
			System.out.println("[!] SQLState: " + e.getSQLState());
			System.out.println("[!] VendorError: " + e.getErrorCode());
			throw new DatabaseConnectionException();
		}
	}
	
	/**
	 * Metodo getter per ottenere la connessione incapsulata in un'istanza di DBAccess.
	 *
	 * @return la connessione al database se inizializzata, null altrimenti
	 */
	public Connection getConnection() {

		return conn;
	}

	/**
	 * 
	 * @throws DatabaseConnectionException
	 */
	public void closeConnection() throws DatabaseConnectionException {
		
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new DatabaseConnectionException("Error while closing the database connection");
		}
	}
	
}
