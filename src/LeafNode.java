
package progettoMapDDL.src;

public class LeafNode extends Node {
	
	// Attributi
	
	Double predictedClassValue;
	
	// Metodi
	
	LeafNode(Data trainingSet, int beginExampleIndex, int endExampleIndex) {
		
		super(trainingSet, beginExampleIndex, endExampleIndex);
		
		predictedClassValue = super.computeAverage(trainingSet);
	}
	
	double getPredictedClassValue() {
		
		return predictedClassValue;
	}
	
	int getNumberOfChildren() {
		
		//Ritorna 0 perché è un nodo foglia
		return 0;
	}
	
	public String toString() {
		
		return  "LEAF: class="+  predictedClassValue +" "+ super.toString();
	}
}
