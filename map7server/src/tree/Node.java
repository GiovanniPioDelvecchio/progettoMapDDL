package tree;

import java.io.Serializable;

import data.Data;

public abstract class Node implements Serializable{
	// Attributi

	private static int idNodeCount = 0; //contatore dei nodi generati nell'albero

	private int idNode; //identificativo numerico del nodo - comincia da 0
	
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

	

	//Metodi

	
	public Node(Data trainingSet, int beginExampleIndex, int endExampleIndex) {

		

		idNode = idNodeCount++;

		this.beginExampleIndex = beginExampleIndex;

		this.endExampleIndex = endExampleIndex;

		Double mean = this.computeAverage(trainingSet);

		//calcolo varianza

		variance = 0;

		for (int i = beginExampleIndex; i <= endExampleIndex; i++ ) {

			variance += Math.pow(trainingSet.getClassValue(i) - mean, 2);
			
		}
		
	}
	
	public int getIdNode() {
		
		return this.idNode;
	}
	
	public int getBeginExampleIndex() {
		
		return this.beginExampleIndex;
	}
	
	public int getEndExampleIndex() {
		
		return this.endExampleIndex;
	}
	
	public double getVariance() {
		
		return this.variance;
	}
	
	public abstract int getNumberOfChildren();
	
	public String toString() {
		
		String toReturn = new String("[Examples:"+this.beginExampleIndex + "-" + this.endExampleIndex + "]" 
								+" variance:"+ this.variance);
		return toReturn;
	}
	
	
	public double computeAverage(Data trainingSet) {
		
		double toReturn = 0;
		
		double size = endExampleIndex - beginExampleIndex + 1;
		
		for (int i = beginExampleIndex; i <= endExampleIndex; i++) {
			
			toReturn = toReturn + trainingSet.getClassValue(i);
		}
		toReturn = toReturn/(size);
		
		return toReturn;
	}
}