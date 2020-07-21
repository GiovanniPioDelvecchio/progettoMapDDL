package tree;

import java.io.Serializable;

import data.Data;

public abstract class Node implements Serializable{

	// Attributi
	private static int idNodeCount = 0; 	// contatore dei nodi generati nell'albero
	private int idNode; 					// identificativo numerico del nodo - comincia da 0
	
	/*
	 *  indice nell'array del training set del primo nesempio coperto dal nodo corrente
	 */
	private int beginExampleIndex; 

	/*
	 * indice nell'array del training set dell'ultimo esempio coperto dal nodo corrente. 
	 * beginExampleIndex e endExampleIndex individuano un sotto-insieme di training.
	 */
	private int endExampleIndex;

	/*
	 * valore della varianza calcolata, rispetto all'attributo di classe, 
	 * nel sotto-insieme di training del nodo
	 */
	private double variance; 

	// Metodi
	public Node(Data trainingSet, int beginExampleIndex, int endExampleIndex) {

		idNode = idNodeCount++;
		this.beginExampleIndex = beginExampleIndex;
		this.endExampleIndex = endExampleIndex;
		Double mean = 0.0;

		// calcolo varianza
		variance = 0;

		double delta;
		int n = 0;
		
		// algoritmo proposto da Knuth in Art of Computer Programming per il calcolo della varianza
		// in un solo ciclo
		for (int i = beginExampleIndex; i <= endExampleIndex; i++ ) {

			n += 1;
			delta = trainingSet.getClassValue(i) - mean;
			mean += delta/(n);
			variance += delta*(trainingSet.getClassValue(i) - mean);
		}
		
	}
	
	public int getIdNode() {
		
		return idNode;
	}
	
	public int getBeginExampleIndex() {
		
		return beginExampleIndex;
	}
	
	public int getEndExampleIndex() {
		
		return endExampleIndex;
	}
	
	public double getVariance() {
		
		return variance;
	}
	
	public abstract int getNumberOfChildren();
	
	public String toString() {
		
		return new String("[Examples:"+this.beginExampleIndex + "-" + this.endExampleIndex + "]" 
								+" variance:"+ this.variance);
	}
}
