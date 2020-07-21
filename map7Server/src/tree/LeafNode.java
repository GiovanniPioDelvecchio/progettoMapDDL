package tree;

import data.Data;

public class LeafNode extends Node {
	
	// Attributi
	
	private Double predictedClassValue;
	
	// Metodi
	
	public LeafNode(Data trainingSet, int beginExampleIndex, int endExampleIndex) {
		
		super(trainingSet, beginExampleIndex, endExampleIndex);
		
		predictedClassValue = 0.0;
		
		double size = endExampleIndex - beginExampleIndex + 1;
		
		for (int i = beginExampleIndex; i <= endExampleIndex; i++) {
			
			predictedClassValue +=  trainingSet.getClassValue(i);
		}
		predictedClassValue = predictedClassValue/(size);
		
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
