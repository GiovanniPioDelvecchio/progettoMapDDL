package tree;

import java.io.Serializable;

import data.Data;


/**
 * Classe astratta utilizzata per modellare un nodo dell'albero di regressione.
 * 
 * Un nodo nell'albero di regressione possiede informazioni sulla porzione di dataset
 * che rappresenta, e la varianza calcolata rispetto all'attributo target nel sottoinsieme
 * rappresentato.
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
@SuppressWarnings("serial")
public abstract class Node implements Serializable {

	/**
	 * Contatore dei nodi generati dall'albero
	 */
	private static int idNodeCount = 0;
	
	/**
	 * Identificatore del nodo (cominciano da 0)
	 */
	private int idNode;
	
	/**
	 * Indice di partenza nella tabella contenente il dataset della porzione di
	 * esempi rappresentata dal nodo
	 */
	private int beginExampleIndex; 

	/**
	 * Indice finale nella tabella contenente il dataset della porzione di
	 * esempi rappresentata dal nodo
	 */
	private int endExampleIndex;

	/**
	 * Valore della varianza calcolata rispetto all'attributo di classe, 
	 * nel sotto-insieme di training del nodo
	 */
	private double variance; 

	/**
	 * Costruttore ereditato dalle specializzazioni di <code>Node</code>.
	 * Effettua il calcolo della varianza dell'attributo target nella porzione
	 * di training set rappresentata.
	 * 
	 * @param trainingSet Istanza di <code>Data</code> contenente il training set su cui costruire l'albero
	 * @param beginExampleIndex Indice iniziale della porzione di training set rappresentata dal nodo
	 * @param endExampleIndex Indice finale della porzione di training set rappresentata dal nodo
	 */
	public Node(Data trainingSet, int beginExampleIndex, int endExampleIndex) {

		idNode = idNodeCount++;
		this.beginExampleIndex = beginExampleIndex;
		this.endExampleIndex = endExampleIndex;
		Double mean = 0.0;

		variance = 0;

		double delta;
		int n = 0;
		
		// Algoritmo proposto da Donald Knuth in The Art of Computer Programming per il calcolo della varianza
		// in un solo ciclo
		for (int i = beginExampleIndex; i <= endExampleIndex; i++) {

			n += 1;
			delta = trainingSet.getClassValue(i) - mean;
			mean += delta / n;
			variance += delta * (trainingSet.getClassValue(i) - mean);
		}
		
	}
	
	/**
	 * Getter per l'intero identificativo del nodo.
	 * 
	 * @return L'intero identificativo del nodo.
	 */
	public int getIdNode() {
		
		return idNode;
	}
	
	/**
	 * Getter per l'indice iniziale nella tabella della porzione di trainig set
	 * rappresentata dal nodo.
	 * 
	 * @return L'indice iniziale nella tabella del sottoinsieme rappresentato dal nodo.
	 */
	public int getBeginExampleIndex() {
		
		return beginExampleIndex;
	}
	
	/**
	 * Getter per l'indice finale nella tabella della porzione di training set
	 * rappresentata dal nodo.
	 * 
	 * @return L'indice finale nella tabella del sottoinsieme rappresentato dal nodo.
	 */
	public int getEndExampleIndex() {
		
		return endExampleIndex;
	}
	
	/**
	 * Getter per la varianza dell'attributo target calcolata nel sottoinsieme
	 * del training set rappresentato dal nodo.
	 * 
	 * @return Varianza dell'attributo target associata al nodo corrente.
	 */
	public double getVariance() {
		
		return variance;
	}
	
	/**
	 * Metodo getter astratto per il numero di figli del nodo.
	 * 
	 * @return Un intero che rappresenta il numero dei figli del nodo.
	 */
	public abstract int getNumberOfChildren();
	
	/**
	 * Sovrascrittura del metodo <code>toString</code> di <code>Object</code>.
	 * 
	 * @return lo stato dell'oggetto sotto forma di stringa.
	 */
	@Override
	public String toString() {
		
		return new String("[Examples:" + this.beginExampleIndex + "-" + this.endExampleIndex + "]" 
								+ " variance:" + this.variance);
	}
}
