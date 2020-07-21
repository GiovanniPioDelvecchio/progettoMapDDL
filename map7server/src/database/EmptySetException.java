package database;

public class EmptySetException extends Exception {

	public EmptySetException() {

		super("Empty database");
	}
	
	public EmptySetException(String errMessage) {

		super(errMessage);
	}
}
