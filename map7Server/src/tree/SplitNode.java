package tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import data.Attribute;
import data.Data;
import server.UnknownValueException;

/**
 * Classe astratta che rappresenta un nodo di split all'interno di un albero di regressione.
 * 
 * Contiene informazioni sull'attributo su cui e' effettuato lo split, e sui figli generati
 * dallo split.
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
@SuppressWarnings("serial")
public abstract class SplitNode extends Node implements Comparable<SplitNode> {
	
	/**
	 * Classe contenente informazioni che descrivono lo split effettuato nello SplitNode.
	 * 
	 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
	 *
	 */
	protected class SplitInfo implements Serializable {

		/**
		 * Valore assunto dall'attributo di split dello SplitNode in una determinata
		 * porzione di dataset.
		 */
		Object splitValue;
		
		/**
		 * Indice iniziale nella tabella contenente il sottoinsieme rappresentato da SplitNode
		 * degli esempi aggregati in base al valore dell'attributo di split.
		 */
		private int beginIndex;
		
		/**
		 * Indice finale nella tabella contenente il sottoinsieme rappresentato da SplitNode
		 * degli esempi aggregati in base al valore dell'attributo di split.
		 */
		private int endIndex;
		
		/**
		 * Numero del nodo figlio di SplitNode rappresentato da SplitInfo.
		 */
		private int numberChild;
		
		/**
		 * Stringa contenente il simbolo della relazione fra i valori dell'attributo di split
		 * rappresentati da un'istanza di SplitInfo, e i possibili valori dell'attributo di split.
		 */
		private String comparator = "=";
		
		/**
		 * Costruttore di SplitInfo.
		 * Avvalora gli attributi della classe.
		 * 
		 * @param splitValue Valore dell'attributo di split che aggrega in un nodo figlio di uno SplitNode un sottoinsieme del training set.
		 * @param beginIndex Indice iniziale nella tabella contenente il training set della porzione rappresentata.
		 * @param endIndex Indice finale nella tabella contenente il training set della porzione rappresentata.
		 * @param numberChild Identificativo numerico del figlio di uno SplitNode rappresentato da SplitInfo. 
		 */
		protected SplitInfo(Object splitValue, int beginIndex, int endIndex, int numberChild) {
			
			this.splitValue = splitValue;
			this.beginIndex = beginIndex;
			this.endIndex = endIndex;
			this.numberChild = numberChild;
		}
		
		/**
		 * Costruttore di SplitInfo che specifica un comparatore personalizzato.
		 * 
		 * @param splitValue Valore dell'attributo di split che aggrega in un nodo figlio di uno SplitNode un sottoinsieme del training set.
		 * @param beginIndex Indice iniziale nella tabella contenente il training set della porzione rappresentata.
		 * @param endIndex Indice finale nella tabella contenente il training set della porzione rappresentata.
		 * @param numberChild Identificativo numerico del figlio di uno SplitNode rappresentato da SplitInfo. 
		 * @param comparator Stringa con cui avvalorare il parametro comparator
		 */
		protected SplitInfo(Object splitValue, int beginIndex, int endIndex, int numberChild, String comparator) {

			this.splitValue = splitValue;
			this.beginIndex = beginIndex;
			this.endIndex = endIndex;
			this.numberChild = numberChild;
			this.comparator = comparator;
		}
		
		/**
		 * Getter per l'indice iniziale nella tabella del sottoinsieme rappresentato dall'istanza di SplitInfo.
		 * 
		 * @return L'indice iniziale del sottoinsieme associato a SplitInfo.
		 */
		protected int getBeginIndex() {

			return beginIndex;			
		}
		
		/**
		 * Getter per l'indice finale nella tabella del sottoinsieme rappresentato dall'istanza di SplitInfo.
		 * 
		 * @return L'indice finale del sottoinsieme associato a SplitInfo.
		 */
		protected int getEndIndex() {

			return endIndex;
		}

		/**
		 * Getter per il valore di split associato all'istanza di SplitInfo.
		 * 
		 * @return Il valore di split associata a SplitInfo (sotto forma di istanza di Object, in quanto
		 * non si conosce a priori la natura dello split).
		 */
		protected Object getSplitValue() {

			return splitValue;
		}

		 /**
		 * Sovrascrittura del metodo <code>toString</code> di <code>Object</code>.
		 * 
		 * @return Lo stato dell'oggetto sotto forma di stringa.
		 */
		 @Override
		 public String toString() {

			 return "child " + numberChild + " split value" + comparator + splitValue + "[Examples:" + beginIndex + "-" + endIndex + "]";
		 }
		
		 /**
		  * Getter per il simbolo di comparazione fra l'attributo di split associato all'istanza di
		  * SplitInfo e gli altri possibili valori dell'attributo.
		  * 
		  * @return Il simbolo di comparazione associato all'attributo di split.
		  */
		 protected String getComparator() {

			return comparator;
		}	
	}

	/**
	 * Attributo su cui viene effettuato uno split in un albero di regressione.
	 */
	private Attribute attribute;	

	/**
	 * Lista contenente informazioni sui nodi figli generati dallo split.
	 */
	protected ArrayList<SplitInfo> mapSplit;
	
	/**
	 * Varianza dell'attributo target in relazione allo split eseguito su di un attributo
	 * del dataset.
	 */
	private double splitVariance;
	
	/**
	 * Metodo astratto che avvalora la lista di SplitInfo generati dallo split.
	 * 
	 * @param trainingSet Istanza di Data contenente la popolazione del training set.
	 * @param beginExampelIndex Indice iniziale nella tabella del sottoinsieme del training set rappresentato dallo SplitNode.
	 * @param endExampleIndex Indice finale nella tabella del sottoinsieme del training set rappresentato dallo SplitNode.
	 * @param attribute Attributo su cui viene effettuato lo split.
	 */
	abstract void setSplitInfo(Data trainingSet, int beginExampelIndex, int endExampleIndex, Attribute attribute);
	
	/**
	 * Dato un valore, controlla se esiste un figlio del nodo di split con split value pari all'attributo passato in input.
	 * 
	 * @param value Possibile valore dell'attributo di split.
	 * @return Identificativo del figlio a cui appartiene il valore rappresentato di value.
	 * @throws UnknownValueException Viene lanciata nel caso in cui non esiste un figlio del nodo di split a cui apparterrebbe <code>value</code>.
	 */
	abstract int testCondition(Object value) throws UnknownValueException;
	
	/**
	 * Costruttore di <code>SplitNode</code>.
	 * 
	 * Implementato in maniera incrementale ripetto alla superclasse <code>Node</code>.
	 * Dopo la chiamata al costruttore della superclasse, avvalora gli attributi esclusivi a SplitNode.
	 * 
	 * @param trainingSet Istanza di Data contenente la popolazione del training set.
	 * @param beginExampleIndex Indice iniziale nella tabella del sottoinsieme del training set rappresentato dallo SplitNode.
	 * @param endExampleIndex Indice finale nella tabella del sottoinsieme del training set rappresentato dallo SplitNode.
	 * @param attribute Attributo su cui viene effettuato lo split.
	 */
	public SplitNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {

			super(trainingSet, beginExampleIndex, endExampleIndex);
			this.attribute = attribute;
			trainingSet.sort(attribute, beginExampleIndex, endExampleIndex);
			setSplitInfo(trainingSet, beginExampleIndex, endExampleIndex, attribute);
						
			// calcolo della varianza
			splitVariance = 0;
			for (SplitInfo si : mapSplit) {

				double localVariance = new LeafNode(trainingSet, si.getBeginIndex(), si.getEndIndex()).getVariance();
				splitVariance += (localVariance);
			}
	}
	
	/**
	 * Getter per l'attributo in base al quale viene effettuato lo split.
	 * 
	 * @return L'attributo di split associato al nodo.
	 */
	Attribute getAttribute() {
		
		return attribute;
	}
	
	/**
	 * Getter per la varianza dell'attributo target nel nodo di split.
	 * 
	 * @return La varianza dell'attributo target nel nodo di split.
	 */
	public double getVariance() {
		
		return splitVariance;
	}
	
	/**
	 * Getter per il numero di figli generati dallo split.
	 * 
	 * @return Il numero di figli dello split (maggiore di 0).
	 */
	public int getNumberOfChildren() {
		 
		return mapSplit.size();
	}
	
	/**
	 * Getter per l'istanza di <code>SplitInfo</code> che descrive un certo figlio del nodo di split.
	 * 
	 * @param child Indice del figlio del nodo di split di cui conoscere lo SplitInfo.
	 * @return L'istanza di SplitInfo associata al figlio dello split node specificato.
	 */
	SplitInfo getSplitInfo(int child) {
		
		return mapSplit.get(child);
	}
	
	
	/**
	 * Metodo per formulare una stringa che descrive un nodo di split ed enumera i suoi figli.
	 * 
	 * @return Una stringa contenente informazioni sullo split e i suoi figli.
	 */
	String formulateQuery() {
		
		String query = "";
		for (int i = 0; i < mapSplit.size(); i++) {

			query += (i + ":" + attribute + mapSplit.get(i).getComparator() + mapSplit.get(i).getSplitValue()) + "\n";
		}
		return query;
	}
	
	/**
	 * Sovrascrittura del metodo <code>toString</code> di <code>Object</code>.
	 * 
	 * @return lo stato dell'oggetto sotto forma di stringa.
	 */
	@Override
	public String toString() {
		
		String v = "SPLIT : attribute=" + attribute + " Nodo: " + super.toString() +  " Split Variance: " + getVariance() + "\n" ;
		
		for (SplitInfo si : mapSplit) {

			v += "\t" + si + "\n";
		}
		
		return v;
	}
	
	/**
	 * Implementazione del metodo <code>compareTo</code> dell'interfaccia <code>Comparable</code>.
	 * 
	 * @return 0 se i due nodi di split hanno la stessa varianza, 1 se la prima varianza e' maggire della seconda,
	 * -1 altrimenti.
	 */
	public int compareTo(SplitNode o) {

		if (this.splitVariance == o.getVariance()) {
			
			return 0;
		} else if (this.splitVariance > o.getVariance()) {

			return 1;
		} else {

			return -1;
		}
	}
}
