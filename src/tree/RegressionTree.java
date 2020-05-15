package progettoMapDDL.src.tree;

import progettoMapDDL.src.data.Data;
import progettoMapDDL.src.data.DiscreteAttribute;
import progettoMapDDL.src.exceptions.UnknownValueException;
import progettoMapDDL.src.utility.Keyboard;

public class RegressionTree {
		
		// Attributi
	
		Node root;						// radice del sotto albero corrente
		RegressionTree childTree[];		// array di sottoalberi originati da root
		
		// Metodi
		RegressionTree() {}
	
		

		/**

		 * Verifica se il sottoinsieme può essere rappresentato come nodo foglia all'interno dell'albero di regressione.

		 * 

		 * @param trainingSet

		 * @param begin Indice di inizio del sottoinsieme nel dataset

		 * @param end Indice di fine del sottoinsieme nel dataset

		 * @param numberOfExamplesPerLeaf

		 * @return Vero se il sottoinsieme può essere rappresentato tramite una foglia, falso altrimenti.

		 */
		private boolean isLeaf(Data trainingSet,int begin, int end, int  numberOfExamplesPerLeaf) {
					
			return (end - begin + 1) <= numberOfExamplesPerLeaf;	
		}
		
		

		/**

		 * Dato un sottoinsieme del data set, determina il miglior attributo su cui eseguire uno split.

		 * 	

		 * @param trainingSet Dataset su cui si sta costruendo un albero di regressione

		 * @param begin Indice di inizio del sottoinsieme

		 * @param end Indice di fine del sottoinsieme

		 * @return Ritorna un nodo di tipo SplitNode sull'attributo indipendente la cui varianza nel sottoinsieme specificato è minima.

		 */
		private SplitNode determineBestSplitNode(Data trainingSet, int begin, int end) {
			
	        int nAttributes = trainingSet.getNumberOfExplanatoryAttributes();
	        DiscreteNode bestSplitNode = new DiscreteNode(trainingSet,begin,end,
	                (DiscreteAttribute)trainingSet.getExplanatoryAttribute(0));
	        Double bestVariance = bestSplitNode.getVariance();
	        
	        for (int i = 1; i < nAttributes; i++) {
	            DiscreteNode possibleSplitNode = new DiscreteNode(trainingSet,begin,end,
	                    (DiscreteAttribute)trainingSet.getExplanatoryAttribute(i));
	            if (bestVariance>possibleSplitNode.getVariance()) {
	                bestVariance = possibleSplitNode.getVariance();
	                bestSplitNode = possibleSplitNode;
	            }
	        }
	        
	        trainingSet.sort(bestSplitNode.getAttribute(), begin, end);
	        return bestSplitNode;
			
		}
		
		public RegressionTree(Data trainingSet){
			
			learnTree(trainingSet,0,trainingSet.getNumberOfExamples()-1,trainingSet.getNumberOfExamples()*10/100);
		}
		
		private void learnTree(Data trainingSet,int begin, int end,int numberOfExamplesPerLeaf){
			if( isLeaf(trainingSet, begin, end, numberOfExamplesPerLeaf)){
				//determina la classe che compare pi� frequentemente nella partizione corrente
				root=new LeafNode(trainingSet,begin,end);
			}
			else //split node
			{
				root = determineBestSplitNode(trainingSet, begin, end);
			
				if(root.getNumberOfChildren() > 1) {
					
					childTree=new RegressionTree[root.getNumberOfChildren()];	//il numero di figli di un nodo di split è pari al numero di
																				//splitInfo in un nodo di split
					
					for(int i=0; i < root.getNumberOfChildren(); i++) {
						
						childTree[i]=new RegressionTree();
						childTree[i].learnTree(trainingSet, ((SplitNode)root).getSplitInfo(i).beginIndex, ((SplitNode)root).getSplitInfo(i).endIndex, numberOfExamplesPerLeaf);
					}
				}
				else
					root = new LeafNode(trainingSet,begin,end);
				
			}
		}
		
		public Double predictClass() throws UnknownValueException {
			
			if(root instanceof LeafNode) {
				
			    return ((LeafNode) root).getPredictedClassValue(); 
			} else { 
				
				int risp; 
				System.out.println(((SplitNode)root).formulateQuery()); 
				risp=Keyboard.readInt(); 
			
				if(risp==-1 || risp>=root.getNumberOfChildren()) {
			    	
						throw new UnknownValueException("The answer should be an integer between 0 and " +(root.getNumberOfChildren()-1)+"!");  
				} else {
			    	
					return childTree[risp].predictClass();  
				}
			}
		}
		
		public void printRules() {
			
			//esplora i nodi dell'albero, se è di split crea la regola, se è leaf si scrive il valore predetto
			//la regola è data dagli split info in mapSplit di Discrete node
			System.out.println("********* RULES **********\n");
			String toPrint = new String();
			printRules(toPrint);
			System.out.println("*************************\n");
		}

		private void printRules(String current) {

	        if (root instanceof SplitNode) {

	            String partialRule = ((SplitNode) root).getAttribute().getName();
	            for (int i = 0; i < root.getNumberOfChildren(); i++) {
	                
	                String comparator = ((SplitNode) root).getSplitInfo(i).getComparator();
	                Object splitValue = ((SplitNode) root).getSplitInfo(i).getSplitValue();
	                
	                if(childTree[i].root instanceof SplitNode) {
	                    
	                    childTree[i].printRules((current + partialRule + comparator + splitValue + " AND " ));
	                } else {
	                    
	                    childTree[i].printRules((current + partialRule + comparator + splitValue));
	                }
	            }
	        } else if (root instanceof LeafNode) {
	            
	            System.out.print(current + " ==> Class=" + ((LeafNode) root).getPredictedClassValue() + "\n");
	        }
	    }
		
		public void printTree(){
			System.out.println("********* TREE **********\n");
			System.out.println(toString());
			System.out.println("*************************\n");
		}
		
		public String toString(){
			String tree=root.toString()+"\n";
			
			if( root instanceof LeafNode){
			
			}
			else //split node
			{
				for(int i=0;i<childTree.length;i++)
					tree +=childTree[i];
			}
			return tree;
		}
		
}
		
