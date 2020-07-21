package tree;

import java.util.ArrayList;

import data.Attribute;
import data.Data;
import data.DiscreteAttribute;
import server.UnknownValueException;

public class DiscreteNode extends SplitNode{
	
	
	// Metodi
	public DiscreteNode(Data trainingSet,int beginExampelIndex, int endExampleIndex,   DiscreteAttribute attribute) {
		
		super(trainingSet, beginExampelIndex, endExampleIndex, attribute);
	}
	
	/**
	 * Calcola il numero di figli dello split e popola l'array mapSplit con le informazioni di ogni split.
	 * 
	 * @param trainingSet Dataset su cui si sta costruendo l'albero di regressione
	 * @param beginExampleIndex Indice del primo valore contenuto nel nodo
	 * @param endExampleIndex Indice dell'ultimo valore contenuto nel nodo
	 * @param attribute Attributo su cui si sta effettuando lo split
	 * 
	 */
	public void setSplitInfo(Data trainingSet,int beginExampelIndex, int endExampleIndex,  Attribute attribute) {
		
		int mapSplitIndex = 0;
		int attributeIndex = ((DiscreteAttribute)attribute).getIndex();
		int beginSplitIndex = beginExampelIndex;
		int endSplitIndex = beginSplitIndex;
		
		mapSplit = new ArrayList<>();
		
		if (beginExampelIndex == endExampleIndex) {
			
			Object onlyVal = trainingSet.getExplanatoryValue(beginExampelIndex, attributeIndex);
			mapSplit.add(new SplitInfo(onlyVal, beginSplitIndex, endSplitIndex, mapSplitIndex));
			
		} else {
			
			Object distinctValues = trainingSet.getExplanatoryValue(beginExampelIndex, attributeIndex);
			
			for (int i = beginExampelIndex + 1; i<=endExampleIndex; i++) {
				
				if(!(((String)(trainingSet.getExplanatoryValue(i, attributeIndex))).equals((String)distinctValues))) {
					
					mapSplit.add(new SplitInfo(distinctValues, beginSplitIndex, i - 1, mapSplitIndex));
					beginSplitIndex = i;
					distinctValues = trainingSet.getExplanatoryValue(i, attributeIndex);
					
					mapSplitIndex++;
				}
			}
			
			mapSplit.add(new SplitInfo(distinctValues, beginSplitIndex, endExampleIndex, mapSplitIndex)) ;
		}
	}
	 
	/**
	 * Controlla l'esistenza di un figlio che assume il valore di split del parametro passato.
	 *
	 * @param value Valore su cui effettuare il test
	 * @return L'eventaule indice dello SplitInfo all'interno di mapSplit con valore di split value
	 */
	int testCondition(Object value) throws UnknownValueException{
		
		for (int i = 0; i < mapSplit.size(); i++) {
			
			if (value.equals(mapSplit.get(i).getSplitValue())) {
				return i;
			}
		}
		
		throw new UnknownValueException("testCondition failure"); //da sostituire con un'eccezione
	} 
	
	/**
	 * Specializzazione del metodo toString per DiscreteNode
	 */
	public String toString() {
		
		String v = "DISCRETE "  +super.toString();
		return v;
	}
}
