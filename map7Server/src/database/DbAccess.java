package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;





public class DbAccess {

private final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
	
	private final String DBMS = "jdbc:mysql"; 
	
	private String SERVER= "localhost";
	
	private String DATABASE = "MapDB";
	
	private final String PORT = "3306"; 
	
	private String USER_ID = "MapUser";
	
	private String PASSWORD = "map";
	
	private Connection conn;
	
	public void initConnection() throws DatabaseConnectionException {
		
		try {
			Class.forName(DRIVER_CLASS_NAME).newInstance();
			
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
		
		System.out.println("Connection's String: " + connectionString);
		
		try {			
			conn = DriverManager.getConnection(connectionString);
		} catch(SQLException e) {
			System.out.println("[!] SQLException: " + e.getMessage());
			System.out.println("[!] SQLState: " + e.getSQLState());
			System.out.println("[!] VendorError: " + e.getErrorCode());
			throw new DatabaseConnectionException();
		}
	}
	
	public Connection getConnection() {
		
		return conn;
	}

	public void closeConnection() throws DatabaseConnectionException {
		
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new DatabaseConnectionException("Error while closing the database connection");
		}
	}
	
}
