package data;

public class TrainingDataException extends Exception {

	

	TrainingDataException(String message) {

		super(message);
	}

	public String toString() {
		
		return this.getClass().getName() +": "+ this.getMessage();
	}
	
}
