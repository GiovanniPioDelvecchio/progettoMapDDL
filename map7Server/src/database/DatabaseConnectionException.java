package database;

public class DatabaseConnectionException extends Exception {

	public String toAdd = ": Failed to connect to the database";
	
	public DatabaseConnectionException() {
		
		super();
	}
	
	public DatabaseConnectionException(String toAdd) {
		
		super();
		this.toAdd = toAdd;
	}
	
	public String toString() {
		
		return this.getClass().getName() + toAdd;
	}
}
