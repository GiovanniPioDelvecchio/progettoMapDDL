package tree;

import java.util.ArrayList;
import java.util.List;

import data.Attribute;
import data.ContinuousAttribute;
import data.Data;
import server.UnknownValueException;

/**
 * Classe usata per rappresentare un nodo di split su di un attributo continuo.
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 * 
 */
@SuppressWarnings("serial")
class ContinuousNode extends SplitNode {

	/**
	 * Costruttore di ContinuousNode.
	 * 
	 * Richiama semplicemente il costruttore della superclasse <code>SplitNode</code>.
	 * 
	 * @param trainingSet Istanza di <code>Data</code> contenente informazioni sul training set.
	 * @param beginExampleIndex Indice di inizio nella tabella contenente il sottoinsieme di training set rappresentato dal nodo di split continuo.
	 * @param endExampleIndex Indice di fine nella tabella contenente il sottoinsieme di training set rappresentato dal nodo di split continuo.
	 * @param attribute Attributo continuo indipendente su cui viene effettuato lo split.
	 * 
	 * @see ContinuousAttribute
	 */
	ContinuousNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, ContinuousAttribute attribute) {
		
		super(trainingSet, beginExampleIndex, endExampleIndex, attribute);
	}

	/**
	 * Implementazione del metodo astratto setSplitInfo ereditato da <code>SplitNode</code>.
	 * 
	 * Il metodo si occupa di trascrivere le informazioni relative al modo in cui si e' effettuato lo split sull'attributo continuo.
	 * 
	 * @param trainingSet Istanza di <code>Data</code> contenente il training set su cui costruire l'albero di regressione.
	 * @param beginExampleIndex Indice di inizio nella tabella contenente il sottoinsieme di training set rappresentato dal nodo di split continuo. 
	 * @param endExampleIndex Indice di fine nella tabella contenente il sottoinsieme di training set rappresentato dal nodo di split continuo.
	 * @param attribute Attributo continuo indipendente su cui viene effettuato lo split. All'atto pratico, <code>attribute</code> deve essere
	 * 					istanza di <code>ContinuousAttribute</code>, poiche' viene effettuato un cast a Double sul valore degli attributi ricavati dalla
	 * 					colonna corrispondente all'indice di attribute.
	 * 
	 * @throws ClassCastExcetpion Lanciata nel caso in cui attribute sia istanza di DiscreteAttribute invece che di ContinuousAttribute.
	 */
	void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
		

		Double currentSplitValue = (Double) trainingSet.getExplanatoryValue(beginExampleIndex, attribute.getIndex());
		double bestInfoVariance = 0;
		List <SplitInfo> bestMapSplit = null;
		
		for (int i = beginExampleIndex + 1; i <= endExampleIndex; i++) {
			
			Double value = (Double) trainingSet.getExplanatoryValue(i, attribute.getIndex());
			
			/*
			 * Scorrendo il sottoinsieme di esempi, quando si trova un esempio con valore assunto dall'attributo di split
			 * diverso da currentSplitValue si considera un eventuale split fra valori con attributo di split <= di currentSplitValue
			 * e valori con attributo di split > di currentSplitValue.
			 * 
			 * Si utilizza l'operatore != poiche' si assume che il sottoinsieme in trainingSet sia gia' stato ordinato relativamente
			 * all'attributo di split in precedenza. 
			 */
			if (value.doubleValue() != currentSplitValue.doubleValue()) {

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

				if (bestMapSplit == null) {

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

			// Si rimuovono split inutili (che includono tutti gli esempi nella stessa partizione)
			if ((mapSplit.get(1).getBeginIndex() == mapSplit.get(1).getEndIndex())) {

				mapSplit.remove(1);
			}
		}
	}

	/**
	 * Implementazione del metodo astratto testCondition ereditato da <code>SplitNode</code>.
	 * 
	 * @param value Valore di split di un figlio del nodo di cui cercare l'identificativo numerico.
	 * @return Un intero che indica quale nodo possiede come valore di split quello passato in input.
	 * @throws UnknownValueException Nel caso in cui nessun figlio del nodo ha split value pari all'argomento.
	 */
	int testCondition(Object value) throws UnknownValueException {
		
		for (int i = 0; i < mapSplit.size(); i++) {
			
			if (value.equals(mapSplit.get(i).getSplitValue())) {

				return i;
			}
		}
		
		throw new UnknownValueException("Tried to test the split node to a non existant split value");
	}

	/**
	 * Sovrascrittura del metodo <code>toString</code> di <code>Object</code>.
	 * 
	 * @return lo stato dell'oggetto sotto forma di stringa.
	 */
	@Override
	public String toString() {
		
		String v = "CONTINUOUS "  + super.toString();
		return v;
	}
}
