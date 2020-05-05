package progettoMapDDL.src;

/*

 * Classe che rappresenta un nodo di split su attributo discreto. Eredita da SplitNode

 */

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
	void setSplitInfo(Data trainingSet,int beginExampelIndex, int endExampleIndex,  Attribute attribute) {
		
		int mapSplitIndex = 0;
		int attributeIndex = ((DiscreteAttribute)attribute).getIndex();
		int beginSplitIndex = beginExampelIndex;
		int endSplitIndex = beginSplitIndex;
		
		mapSplit = new SplitInfo[((DiscreteAttribute)attribute).getNumberOfDistinctValues()];
		
		if (beginExampelIndex == endExampleIndex) {
			
			Object onlyVal = trainingSet.getExplanatoryValue(beginExampelIndex, attributeIndex);
			mapSplit[mapSplitIndex] = new SplitInfo(onlyVal, beginSplitIndex, endSplitIndex, mapSplitIndex);
			
		} else {
			
			
			for (int i = beginExampelIndex; i < endExampleIndex; i++) {
				
				
				
				Object currentVal = trainingSet.getExplanatoryValue(i + 1, attributeIndex);
				Object previousVal = trainingSet.getExplanatoryValue(i, attributeIndex);
				
				if (((String)(currentVal)).equals(((String)(previousVal)))) {
					
					endSplitIndex++;
									
				} else {
					
					mapSplit[mapSplitIndex] = new SplitInfo(previousVal, beginSplitIndex, endSplitIndex, mapSplitIndex);
					endSplitIndex++;
					beginSplitIndex = endSplitIndex;
					mapSplitIndex++;
					
				}
				
				if (endSplitIndex == endExampleIndex) {
					
					mapSplit[mapSplitIndex] = new SplitInfo(currentVal, beginSplitIndex, endSplitIndex, mapSplitIndex);
				}
			}
		}
		
		int counter = 0;
		
		while( (counter < mapSplit.length) && (mapSplit[counter] != null)) {
			
			counter++;
		}
		
		SplitInfo temp[] = new SplitInfo[counter];
		
		for (int i = 0; i < counter; i++) {
			
			temp[i] = mapSplit[i];
		}
		
		mapSplit = temp;
		
	}
	 
	/**

	 * Controlla l'esistenza di un figlio che assume il valore di split del parametro passato.

	 * 

	 * @param value Valore su cui effettuare il test

	 * @return L'eventaule indice dello SplitInfo all'interno di mapSplit con valore di split value

	 */
	int testCondition(Object value) {
		
		for (int i = 0; i < mapSplit.length; i++) {
			
			if (value.equals(mapSplit[i].getSplitValue())) {
				return i;
			}
		}
		
		return -1; //da sostituire con un'eccezione
	} 
	
	/**

	 * Specializzazione del metodo toString per DiscreteNode.

	 */
	public String toString() {
		
		String v = "DISCRETE "  +super.toString();
		return v;
		
	}
}
