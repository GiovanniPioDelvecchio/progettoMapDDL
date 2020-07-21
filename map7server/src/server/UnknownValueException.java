package server;

public class UnknownValueException extends Exception {
	
	public UnknownValueException() {
		super();
	}
	
	public UnknownValueException(String message) {
		super(message);
	}
}
