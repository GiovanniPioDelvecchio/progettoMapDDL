package progettoMapDDL.src.exceptions;

public class UnknownValueException extends Exception{
	
	private String toPlot;
	
	public UnknownValueException(String toPlot) {
		this.toPlot = toPlot;
	}
	
	public String toString() {
		
		return toPlot;
	}
}
