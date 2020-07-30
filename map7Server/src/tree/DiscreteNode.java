package tree;

import java.util.ArrayList;

import data.Attribute;
import data.Data;
import data.DiscreteAttribute;
import server.UnknownValueException;


/**
 * Classe usata per rappresentare un nodo di split su di un attributo continuo.
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
@SuppressWarnings("serial")
public class DiscreteNode extends SplitNode {

	/**
	 * Costruttore di DiscreteNode.
	 * 
	 * Richiama il costruttore della superclasse SplitNode.
	 * 
	 * @param trainingSet Istanza di Data contenente gli esempi del training set da cui costruire un albero di regressinoe.
	 * @param beginExampelIndex Indice di inizio nella tabella contenente il sottoinsieme di esempi rappresentato dal nodo di split.
	 * @param endExampleIndex Indice di fine nella tabella contenente il sottoinsieme di esempi rappresentato dal nodo di split.
	 * @param attribute Attributo discreto su cui si sta effettuando uno split in un albero di regressione.
	 * 
	 * @see DiscreteAttribute
	 */
	public DiscreteNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, DiscreteAttribute attribute) {

		super(trainingSet, beginExampleIndex, endExampleIndex, attribute);
	}
	
	/**
	 * Implementazione del metodo astratto setSplitInfo ereditato da SplitInfo.
	 * 
	 * Il metodo si occupa di popolare l'attributo mapSplit che contiene informazioni sui figli del nodo di split discreto.
	 * 
	 * @param trainingSet Istanza di Data contenente gli esempi del training set da cui costruire un albero di regressinoe.
	 * @param beginExampleIndex Indice di inizio nella tabella contenente il sottoinsieme di esempi rappresentato dal nodo di split.
	 * @param endExampleIndex Indice di fine nella tabella contenente il sottoinsieme di esempi rappresentato dal nodo di split.
	 * @param attribute Attributo discreto su cui si sta effettuando uno split in un albero di regressione.
	 */
	public void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {

		int mapSplitIndex = 0;
		int attributeIndex = ((DiscreteAttribute)attribute).getIndex();
		
		// Utilizzo due indici per tenere traccia degli estremi di ogni sottoinsieme generato dallo split sull'attributo discreto
		int beginSplitIndex = beginExampleIndex;
		int endSplitIndex = beginSplitIndex;

		mapSplit = new ArrayList<>();

		// Se beginExampleIndex ed endExampleIndex coincidono, il sottoinsieme contiene un solo esempio (il nodo di split avra' un solo figlio)
		if (beginExampleIndex == endExampleIndex) {
			
			Object onlyVal = trainingSet.getExplanatoryValue(beginExampleIndex, attributeIndex);
			mapSplit.add(new SplitInfo(onlyVal, beginSplitIndex, endSplitIndex, mapSplitIndex));
		} else {

			// Si legge il valore corrente assunto dall'attributo di split nell'esempio all'indice beginExampleIndex
			Object distinctValues = trainingSet.getExplanatoryValue(beginExampleIndex, attributeIndex);
			
			for (int i = beginExampleIndex + 1; i <= endExampleIndex; i++) {

				// Scorrendo gli esempi, appena si trova un esempio dal valore diverso da distinctValues si aggiunge un'istanza di SplitInfo a mapSplit
				if (!(((String) (trainingSet.getExplanatoryValue(i, attributeIndex))).equals((String) distinctValues))) {

					mapSplit.add(new SplitInfo(distinctValues, beginSplitIndex, i - 1, mapSplitIndex));
					beginSplitIndex = i;
					distinctValues = trainingSet.getExplanatoryValue(i, attributeIndex);
					mapSplitIndex++;
				}
			}

			// Viene aggiunto l'ultimo split, generato dall'ultima porzione di esempi
			mapSplit.add(new SplitInfo(distinctValues, beginSplitIndex, endExampleIndex, mapSplitIndex));
		}
	}
	 
	/**
	 * Dato un valore, controlla se esiste un figlio del nodo con attributo di split pari al valore passato.
	 *
	 * @param value Valore su cui effettuare il test.
	 * @return L'indice dell'istanza di SplitInfo appartenente a mapSplit con split value pari all'argomento.
	 * @throws UnknownValueException Nel caso in cui nessun figlio del nodo ha split value pari all'argomento.
	 */
	int testCondition(Object value) throws UnknownValueException {

		for (int i = 0; i < mapSplit.size(); i++) {
			
			if (value.equals(mapSplit.get(i).getSplitValue())) {
				return i;
			}
		}

		throw new UnknownValueException("testCondition failure");
	} 
	
	/**
	 * Specializzazione del metodo toString per DiscreteNode.
	 * 
	 * @return Una stringa contenente informazioni sul nodo di split discreto.
	 */
	@Override
	public String toString() {

		String v = "DISCRETE " + super.toString();
		return v;
	}
}
