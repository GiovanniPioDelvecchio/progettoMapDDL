package data;

public class TrainingDataException extends Exception {

	

	TrainingDataException(String message) {

		super(message);

	}

	TrainingDataException(){

		super();

	}

	TrainingDataException(String message, Throwable cause) {

		super(message, cause);

	}


	TrainingDataException(Throwable cause) {

		super(cause);

	}
	
	public String toString() {
		
		return this.getClass().getName() +": "+ this.getMessage() /*+ ": " + (this.getCause()!=null ?  this.getCause().toString() : "")*/;
	}
	
}
