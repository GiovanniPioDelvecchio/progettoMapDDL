
package progettoMapDDL.src.tree;

import progettoMapDDL.src.data.Data;

public class LeafNode extends Node {
	
	// Attributi
	
	private Double predictedClassValue;
	
	// Metodi
	
	public LeafNode(Data trainingSet, int beginExampleIndex, int endExampleIndex) {
		
		super(trainingSet, beginExampleIndex, endExampleIndex);
		
		predictedClassValue = super.computeAverage(trainingSet);
	}
	
	public double getPredictedClassValue() {
		
		return predictedClassValue;
	}
	
	public int getNumberOfChildren() {
		
		//Ritorna 0 perché è un nodo foglia
		return 0;
	}
	
	public String toString() {
		
		return  "LEAF: class="+  predictedClassValue +" "+ super.toString();
	}
}
