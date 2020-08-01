package tree;

import data.Data;

/**
 * Classe che rappresenta un nodo foglia di un albero di regressione.
 * 
 * Oltre alle informazioni contenute dalla superclasse <code>Node</code>, <code>LeafNode</code>
 * contiene una predizione sul valore dell'attributo target in relazione alla porzione di dataset
 * rappresentata.
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
@SuppressWarnings("serial")
public class LeafNode extends Node {
	
	/**
	 * Predizione sul valore dell'attributo target in relazione al sottoinsieme del training
	 * set rappresentato dal nodo foglia.
	 */
	private Double predictedClassValue;
	

	/**
	 * Costruttore di LeafNode.
	 * 
	 * Implementato in maniera incrementale rispetto al costruttore di Node. Successivamente
	 * alla chiamata del costruttore della superclasse, viene calcolato il valore predetto
	 * dell'attributo target.
	 * 
	 * @param trainingSet Istanza della classe <code>Data</code> contenente il training set.
	 * @param beginExampleIndex Indice iniziale nella tabella contenente il sottoinsieme del dataset rappresentato dal nodo.
	 * @param endExampleIndex Indice finale nella tabella contenente il sottoinsieme del dataset rappresentato dal nodo.
	 */
	public LeafNode(Data trainingSet, int beginExampleIndex, int endExampleIndex) {
		
		super(trainingSet, beginExampleIndex, endExampleIndex);
		
		predictedClassValue = 0.0;
		double size = endExampleIndex - beginExampleIndex + 1;
		
		for (int i = beginExampleIndex; i <= endExampleIndex; i++) {
			
			predictedClassValue +=  trainingSet.getClassValue(i);
		}
		predictedClassValue = predictedClassValue/(size);
	}
	
	/**
	 * Getter per il valore predetto dell'attributo target.
	 * 
	 * @return Il valore predetto dell'attributo target relativo al nodo foglia corrente.
	 */
	public double getPredictedClassValue() {
		
		return predictedClassValue;
	}
	
	/**
	 * Getter per il numero di figli del nodo.
	 * 
	 * @return 0 per definizione di nodo foglia
	 */
	public int getNumberOfChildren() {

		return 0;
	}
	
	/**
	 * Sovrascrittura del metodo <code>toString</code> di <code>Object</code>.
	 * 
	 * @return lo stato dell'oggetto sotto forma di stringa.
	 */
	@Override
	public String toString() {
		
		return  "LEAF: class=" +  predictedClassValue + " "+ super.toString();
	}
}
