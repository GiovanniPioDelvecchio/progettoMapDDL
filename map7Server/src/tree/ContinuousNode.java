package tree;

import java.util.ArrayList;
import java.util.List;

import data.Attribute;
import data.ContinuousAttribute;
import data.Data;

/**
 * Classe usata per rappresentare un nodo di split su di un attributo continuo.
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 * 
 */
@SuppressWarnings("serial")
public class ContinuousNode extends SplitNode {

	/**
	 * Costruttore di ContinuousNode.
	 * 
	 * Richiama semplicemente il costruttore della superclasse SplitNode.
	 * 
	 * @param trainingSet Istanza di Data contenente informazioni sul training set.
	 * @param beginExampleIndex Indice di inizio nella tabella contenente il sottoinsieme di training set rappresentato dal nodo di split continuo.
	 * @param endExampleIndex Indice di fine nella tabella contenente il sottoinsieme di training set rappresentato dal nodo di split continuo.
	 * @param attribute Attributo continuo indipendente su cui viene effettuato lo split.
	 * 
	 * @see ContinuousAttribute
	 */
	public ContinuousNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, ContinuousAttribute attribute) {
		
		super(trainingSet, beginExampleIndex, endExampleIndex, attribute);
	}

	/**
	 * Implementazione del metodo astratto setSplitInfo ereditato da SplitInfo.
	 * 
	 * Il metodo si occupa di popolare l'attributo mapSplit che contiene informazioni sui figli del nodo di split continuo.
	 * 
	 * @param trainingSet Istanza di Data contenente il training set su cui costrutire l'albero di regressione
	 * @param beginExampleIndex Indice di inizio nella tabella contenente il sottoinsieme di training set rappresentato dal nodo di split continuo. 
	 * @param endExampleIndex Indice di fine nella tabella contenente il sottoinsieme di training set rappresentato dal nodo di split continuo.
	 * @param attribute Attributo continuo indipendente su cui viene effettuato lo split.
	 */
	void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
		

		Double currentSplitValue = (Double) trainingSet.getExplanatoryValue(beginExampleIndex, attribute.getIndex());
		double bestInfoVariance = 0;
		List <SplitInfo> bestMapSplit = null;
		
		for (int i = beginExampleIndex + 1; i <= endExampleIndex; i++){
			
			Double value = (Double) trainingSet.getExplanatoryValue(i, attribute.getIndex());
			
			/*
			 * Scorrendo il sottoinsieme di esempi, quando si trova un esempio con valore assunto dall'attributo di split
			 * diverso da currentSplitValue si considera un eventuale split fra valori con attributo di split <= di currentSplitValue
			 * e valori con attributo di split > di currentSplitValue.
			 * 
			 * Si utilizza l'operatore != poiche' si assume che il sottoinsieme in trainingSet sia gia' stato ordinato relativamente
			 * all'attributo di split in precedenza. 
			 */
			if (value.doubleValue() != currentSplitValue.doubleValue()){

				/*
				 * Viene calcolata la varianza dell'attributo target in un eventuale split
				 * [beginExampleIndex, i - 1], [i, endExampleIndex]
				 * Tale varianza "candidata" sara' confrontata con migliore varianza dell'attributo target generata da
				 * altre possibili configurazioni dello split precedentemente generate. Si sta quindi effettuando una ricerca
				 * dello split che generi la minor varianza dell'attributo target.
				 */
				double localVariance = new LeafNode(trainingSet, beginExampleIndex, i - 1).getVariance();
				double candidateSplitVariance = localVariance;
				localVariance = new LeafNode(trainingSet, i, endExampleIndex).getVariance();
				candidateSplitVariance += localVariance;

				if (bestMapSplit == null){

					bestMapSplit = new ArrayList<SplitInfo>();
					bestMapSplit.add(new SplitInfo(currentSplitValue, beginExampleIndex, i - 1, 0, "<="));
					bestMapSplit.add(new SplitInfo(currentSplitValue, i, endExampleIndex, 1, ">"));
					bestInfoVariance = candidateSplitVariance;
				} else {		

					if (candidateSplitVariance < bestInfoVariance) {

						bestInfoVariance = candidateSplitVariance;
						bestMapSplit.set(0, new SplitInfo(currentSplitValue, beginExampleIndex, i - 1, 0, "<="));
						bestMapSplit.set(1, new SplitInfo(currentSplitValue, i, endExampleIndex, 1, ">"));
					}
				}
				currentSplitValue = value;
			}
		}
		mapSplit = (ArrayList<SplitInfo>) bestMapSplit;
		
		/*
		 * Nel caso in cui il sottoinsieme di esempi assume sempre lo stesso valore nell'attributo di split, bisogna
		 * aggiungere a fine ciclo l'unico elemento di mapSplit
		 */
		if (bestMapSplit == null) {

			mapSplit = new ArrayList<SplitInfo>();
			mapSplit.add(new SplitInfo(currentSplitValue, beginExampleIndex, endExampleIndex, 0));
		} else {

			if ((mapSplit.get(1).beginIndex == mapSplit.get(1).getEndIndex())) {

				mapSplit.remove(1);
			}
		}
	}

	/**
	 * Implementazione del metodo astratto testCondition ereditato da SplitNode.
	 * 
	 * @param value Valore di split di un figlio del nodo di cui cercare l'identificativo numerico.
	 * @return Un intero che indica quale nodo possiede come valore di split quello passato in input.
	 */
	int testCondition(Object value) {
		
		for (int i = 0; i < mapSplit.size(); i++) {
			
			if (value.equals(mapSplit.get(i).getSplitValue())) {

				return i;
			}
		}
		
		return -1; // da sostituire con un'eccezione
	}

	/**
	 * Sovrascrittura del metodo toString.
	 * 
	 * @return Una stringa contenente informazioni sul nodo di split continuo.
	 */
	@Override
	public String toString() {
		
		String v = "CONTINUOUS "  + super.toString();
		return v;
	}
}
