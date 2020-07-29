package tree;

import java.io.Serializable;
import java.util.ArrayList;

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
		int beginIndex;
		
		/**
		 * Indice finale nella tabella contenente il sottoinsieme rappresentato da SplitNode
		 * degli esempi aggregati in base al valore dell'attributo di split.
		 */
		int endIndex;
		
		/**
		 * Numero del nodo figlio di SplitNode rappresentato da SPlitInfo.
		 */
		int numberChild;
		
		/**
		 * Stringa contenente il simbolo della relazione fra i valori dell'attributo di split
		 * rappresentati da un'istanza di SplitInfo, e i possibili valori dell'attributo di split.
		 */
		String comparator = "=";
		
		/**
		 * Costruttore di SplitInfo.
		 * Avvalore gli attributi della classe.
		 * 
		 * @param splitValue Valore dell'attributo di split che aggrega in un nodo figlio di uno SplitNode un sottoinsieme del training set.
		 * @param beginIndex Indice iniziale nella tabella contenente il training set della porzione rappresentata.
		 * @param endIndex Indice finale nella tabella contenente il training set della porzione rappresentata.
		 * @param numberChild Identificativo numerico del figlio di uno SplitNode rappresentato da SplitInfo. 
		 */
		SplitInfo(Object splitValue,int beginIndex,int endIndex,int numberChild) {
			
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
		SplitInfo(Object splitValue, int beginIndex, int endIndex, int numberChild, String comparator) {
			this.splitValue = splitValue;
			this.beginIndex = beginIndex;
			this.endIndex = endIndex;
			this.numberChild = numberChild;
			this.comparator = comparator;
		}
		
		/**
		 * Getter per l'attributo beginIndex.
		 * 
		 * @return Il valore di beginIndex.
		 */
		int getBeginindex() {

			return beginIndex;			
		}
		
		/**
		 * Getter per l'attributo endIndex.
		 * 
		 * @return Il valore di endIndex.
		 */
		int getEndIndex() {

			return endIndex;
		}

		/**
		 * Getter per l'attributo splitValue
		 * 
		 * @return Il valore di splitValue (sotto forma di istanza di Object, in quanto
		 * non si conosce a priori la natura dello split).
		 */
		 Object getSplitValue() {

			return splitValue;
		}

		 /**
		  * Sovrascrittura del metodo toString per uno SplitNode.
		  * 
		  * @return Una stringa contenente informazioni sul nodo di split.
		  */
		 @Override
		 public String toString() {

			 return "child " + numberChild +" split value"+comparator+splitValue + "[Examples:"+ beginIndex + "-" + endIndex + "]";
		 }
		
		 /**
		  * Getter per l'attributo comparator.
		  * 
		  * @return Il valore di comparator.
		  */
		 String getComparator() {

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
	ArrayList<SplitInfo> mapSplit;
	
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
	 * Metodo che permette di testare rispetto ad un possibile valore dell'attributo di split la sua appartenenza
	 * rispetto ad un figlio dello SplitNode.
	 * 
	 * @param value Possibile valore dell'attributo di split.
	 * @return Identificativo del figlio a cui appartiene il valore rappresentato di value.
	 * @throws UnknownValueException Viene lanciata nel caso in cui non esiste un figlio del nodo di split a cui apparterrebbe <code>value</code>.
	 */
	abstract int testCondition(Object value) throws UnknownValueException;
	
	/**
	 * Costruttore di SplitNode.
	 * 
	 * Implementato in maniera incrementale ripetto alla superclasse Node.
	 * Dopo la chiamata al costruttore della superclasse, avvalora gli attributi esclusivi a SplitNode.
	 * @param trainingSet 
	 * @param beginExampleIndex
	 * @param endExampleIndex
	 * @param attribute
	 */
	public SplitNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute){
			super(trainingSet, beginExampleIndex,endExampleIndex);
			this.attribute=attribute;
			trainingSet.sort(attribute, beginExampleIndex, endExampleIndex); // order by attribute
			setSplitInfo(trainingSet, beginExampleIndex, endExampleIndex, attribute);
						
			// compute variance
			splitVariance=0;
			for(SplitInfo si : mapSplit) {
				double localVariance = new LeafNode(trainingSet, si.getBeginindex(), si.getEndIndex()).getVariance();
				splitVariance+=(localVariance);
			}
	}
	
	/**
	 * Getter per l'attributo attribute.
	 * 
	 * @return Il valore di attribute.
	 */
	Attribute getAttribute() {
		
		return attribute;
	}
	
	/**
	 * Getter per la varianza dell'attributo target nel nodo di split.
	 * 
	 * @return Il valore del parametro splitVariance.
	 */
	public double getVariance() {
		
		return splitVariance;
	}
	
	/**
	 * Getter per il numero di figli generati dallo split.
	 * 
	 * @return Il numero di figli dello split (> 0).
	 */
	public int getNumberOfChildren() {
		 
		return mapSplit.size();
	}
	
	/**
	 * Getter per l'istanza di SplitInfo che descrive un certo figlio del nodo di split.
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
		for (int i=0; i < mapSplit.size(); i++)
			query += (i + ":" + attribute + mapSplit.get(i).getComparator() + mapSplit.get(i).getSplitValue()) + "\n";
		return query;
	}
	
	/**
	 * Sovrascrittura del metodo toString per SplitNode.
	 * 
	 * @return Una stringa che descrive il nodo di Split.
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
	 * Implementazione del metodo compareTo dell'interfaccia Comparable.
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
