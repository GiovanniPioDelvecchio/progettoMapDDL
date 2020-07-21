package data;

public class TrainingDataException extends Exception {

	

	TrainingDataException(String message) {

		super(message);
	}

	TrainingDataException(){

		super();
	}
	
	public String toString() {
		
		return this.getClass().getName() +": "+ this.getMessage();
	}
	
}
