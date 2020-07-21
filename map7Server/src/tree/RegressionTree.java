package tree;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.TreeSet;

import data.Data;
import data.DiscreteAttribute;
import server.UnknownValueException;
import data.Attribute;
import data.ContinuousAttribute;

public class RegressionTree  implements Serializable{
	
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

       TreeSet<SplitNode> ts = new TreeSet<SplitNode>(); 
                
              
        for (int i = 0; i < nAttributes; i++) {
        	
        	/*
            ts.add(new DiscreteNode(trainingSet,begin,end,
                    (DiscreteAttribute)trainingSet.getExplanatoryAttribute(i)));
                    
            */
        	Attribute toCheck = trainingSet.getExplanatoryAttribute(i);
        	
        	if (toCheck instanceof DiscreteAttribute) { //qui viene utilizzato l'RTTI
        		
        		ts.add(new DiscreteNode(trainingSet, begin, end, (DiscreteAttribute)toCheck));
        		
        	} else {
        		
        		ts.add(new  ContinuousNode(trainingSet, begin, end, (ContinuousAttribute)toCheck));
        	}
        	
        }
        SplitNode bestSplitNode = ts.first();
        
        trainingSet.sort(bestSplitNode.getAttribute(), begin, end);
        return bestSplitNode;
	}
	
	public RegressionTree(Data trainingSet){
		
		//System.out.println(trainingSet);
		learnTree(trainingSet,0,trainingSet.getNumberOfExamples()-1,trainingSet.getNumberOfExamples()*10/100);
	}
	
	private void learnTree(Data trainingSet,int begin, int end,int numberOfExamplesPerLeaf){
		
		if(isLeaf(trainingSet, begin, end, numberOfExamplesPerLeaf)){
			// determina la classe che compare pi� frequentemente nella partizione corrente
			root=new LeafNode(trainingSet,begin,end);
		} else {
			// split node
			root = determineBestSplitNode(trainingSet, begin, end);
		
			if(root.getNumberOfChildren() > 1) {
				
				childTree=new RegressionTree[root.getNumberOfChildren()];	// il numero di figli di un nodo di split è pari al numero di
																			// splitInfo in un nodo di split
				
				for(int i=0; i < root.getNumberOfChildren(); i++) {
					
					childTree[i]=new RegressionTree();
					childTree[i].learnTree(trainingSet, ((SplitNode)root).getSplitInfo(i).beginIndex, ((SplitNode)root).getSplitInfo(i).endIndex, numberOfExamplesPerLeaf);
				}
			}
			else
				root = new LeafNode(trainingSet,begin,end);
			
		}
	}
	
	
	public Double predictClass(ObjectOutputStream out, ObjectInputStream in) throws UnknownValueException {
		
		if(root instanceof LeafNode) {
			
			try {
				out.writeObject("OK");
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		    return ((LeafNode) root).getPredictedClassValue(); 
		} else { 
			
			try {
				out.writeObject("QUERY");
				int risp; 
				out.writeObject(((SplitNode)root).formulateQuery()); 
				
				risp = (Integer)in.readObject(); 
				
			
				if(risp==-1 || risp>=root.getNumberOfChildren()) {
			    	
						throw new UnknownValueException("The answer should be an integer between 0 and " +(root.getNumberOfChildren()-1)+"!");  
				} else {
			    	
					
					return childTree[risp].predictClass(out, in);  
				}
			} catch(IOException e) {
				
				e.printStackTrace();	// da propagare
				
			} catch(ClassNotFoundException e) {
				e.printStackTrace();	// da propagare
			}
			return null;
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
	
	public void salva(String nomeFile) throws FileNotFoundException, IOException {
		
		FileOutputStream whereSave = new FileOutputStream(nomeFile);
		ObjectOutputStream whereSaveStream = new ObjectOutputStream(whereSave);
		whereSaveStream.writeObject(this);
		whereSaveStream.close();
		whereSave.close();
	}
	
	public static RegressionTree carica(String nomeFile) throws FileNotFoundException,IOException,ClassNotFoundException {
		
		FileInputStream whereLoad = new FileInputStream(nomeFile);
		ObjectInputStream whereLoadStream = new ObjectInputStream(whereLoad);
		RegressionTree toReturn =  (RegressionTree)whereLoadStream.readObject();
		whereLoadStream.close();
		whereLoad.close();
		return toReturn;
		
	}

}
