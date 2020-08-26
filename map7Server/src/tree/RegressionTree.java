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

/**
 * Classe utilizzata per modellare un albero di regressione.
 * 
 * La creazione di un albero di regressione puo' essere effettuata utilizzando un'istanza di <code>Data</code>.
 * Fornisce dei metodi di salvataggio e di caricamento tramite serializzazione di un oggetto.
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
@SuppressWarnings("serial")
public class RegressionTree implements Serializable {
	
	/**
	 * Nodo radice del (sotto)albero rappresentato da RegressionTree.
	 */
	private Node root;
	
	/**
	 * Array di figli (rappresentabili come sottoalberi) originati da root.
	 */
	private RegressionTree childTree[];
	
	// Costruttore a zero argomenti. Viene utilizzato per la costruzione di sottoalberi in learnTree
	RegressionTree() {}

	/**
	 * Verifica se il sottoinsieme puo' essere rappresentato come nodo foglia all'interno dell'albero di regressione.
	 * 
	 * @param trainingSet Istanza di <code>Data</code> contenente il training set
	 * @param begin Indice di inizio del sottoinsieme nella tabella contenente il training set.
	 * @param end Indice di fine del sottoinsieme nella tabella contenente il training set.
	 * @param numberOfExamplesPerLeaf Valore numerico che rappresenta il numero massimo di esempi rappresentabili da un nodo foglia.
	 * 
	 * @return Vero se il sottoinsieme puo' essere rappresentato tramite una foglia, falso altrimenti.
	 * 
	 */
	private boolean isLeaf(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
				
		return (end - begin + 1) <= numberOfExamplesPerLeaf;	
	}
	
	

	/**
	 * Dato un sottoinsieme del training set, determina il miglior attributo su cui eseguire uno split.
	 * 	
	 * @param trainingSet Istanza di <code>Data</code> contenente il training set.
	 * @param begin Indice di inizio del sottoinsieme nella tabella contenente il training set.
	 * @param end Indice di fine del sottoinsieme nella tabella contenente il training set.
	 * 
	 * @return Un nodo di tipo SplitNode sull'attributo indipendente la cui varianza dell'attributo target negli esempi compresi fra begin ed end e' minima.
	 */
	private SplitNode determineBestSplitNode(Data trainingSet, int begin, int end) {

       int nAttributes = trainingSet.getNumberOfExplanatoryAttributes();

       // Si utilizza un TreeSet di SplitNode in modo da poter facilmente individuare il nodo di split con varianza minima.
       TreeSet<SplitNode> ts = new TreeSet<SplitNode>(); 

       // Vengono scorsi tutti gli attributi del training set, creando nodi di split per ognuno di essi
        for (int i = 0; i < nAttributes; i++) {

        	Attribute toCheck = trainingSet.getExplanatoryAttribute(i);

        	if (toCheck instanceof DiscreteAttribute) {

        		ts.add(new DiscreteNode(trainingSet, begin, end, (DiscreteAttribute) toCheck));
        	} else {

        		ts.add(new ContinuousNode(trainingSet, begin, end, (ContinuousAttribute) toCheck));
        	}
        }
        
        // Il miglior nodo di split sara' quello con la varianza minima, ovvero il primo elemento nel TreeSet.
        SplitNode bestSplitNode = ts.first();
        
        // Infine porzione di esempi contenuta fra begin ed end viene ordinata in base all'attributo di split migliore
        trainingSet.sort(bestSplitNode.getAttribute(), begin, end);

        return bestSplitNode;
	}
	
	/**
	 * Costruttore pubblico di RegressionTree.
	 * 
	 * @param trainingSet Istanza di <code>Data</code> contenente il training set da cui creare un albero di regressione.
	 */
	public RegressionTree(Data trainingSet) {
		
		learnTree(trainingSet, 0, trainingSet.getNumberOfExamples() - 1, trainingSet.getNumberOfExamples() * (10/100));
	}

	/**
	 * Metodo utilizzato per la costruzione di un albero di regressione.
	 * 
	 * <code>Data</code> una porzione di training set, determina se effettuare uno split o rappresentarla tramite un
	 * nodo foglia.
	 * 
	 * @param trainingSet Istanza di <code>Data</code> contenente il training set da cui creare un albero di regressione.
	 * @param begin Indice di inizio del sottoinsieme nella tabella contenente il training set.
	 * @param end Indice di fine del sottoinsieme nella tabella contenente il training set.
	 * @param numberOfExamplesPerLeaf Numero che rappresenta la soglia secondo quale una porzione di training set puo' 
	 * 				essere rappresentata tramite un nodo foglia.
	 */
	private void learnTree(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
		
		/*
		 * Se la porzione possiede un numero di esempi abbastanza basso, la si rappresenta
		 * tramite un nodo foglia.
		 */
		if (isLeaf(trainingSet, begin, end, numberOfExamplesPerLeaf)) {

			// determina la classe che compare piu' frequentemente nella partizione corrente
			root = new LeafNode(trainingSet, begin, end);
		} else {
			
			/*
			 * Se la porzione porta alla creazione di un nodo di split, si determina lo split con la minore
			 * varianza dell'attributo target.
			 */
			root = determineBestSplitNode(trainingSet, begin, end);
		
			/*
			 * Se il numero di figli e' maggiore di uno, allora si popola childTree con istanze di RegressionTree
			 * costruite tramite le informazioni fornite dalle istanze di SplitInfo fornite dalla radice.
			 */
			if (root.getNumberOfChildren() > 1) {

				childTree = new RegressionTree[root.getNumberOfChildren()];

				for (int i = 0; i < root.getNumberOfChildren(); i++) {

					childTree[i] = new RegressionTree();
					childTree[i].learnTree(trainingSet, ((SplitNode) root).getSplitInfo(i).beginIndex, ((SplitNode) root).getSplitInfo(i).endIndex, numberOfExamplesPerLeaf);
				}
			}
			else {

				/*
				 * Se il nodo di split ha un solo figlio allora esso e' logicamente rappresentabile
				 * come un nodo foglia (poiche' uno split e' individuabile se presenti almeno due figli).
				 */
				root = new LeafNode(trainingSet, begin, end);
			}
		}
	}
	
	/**
	 * Metodo che si interfaccia con un utente per esplorare l'albero di regressione costruito in profondita'.
	 * 
	 * Ogni nodo di split genera una query testuale, a cui un utente deve rispondere con un intero
	 * che rappresenta il figlio su cui proseguire l'esplorazione in profondita'. Una volta arrivato
	 * ad un nodo foglia, viene restituita la predizione dell'attributo target.
	 * 
	 * @param out ObjectOutputStream su cui inviare le query testuali all'utente.
	 * @param in ObjectInputStream da cui ricevere le scelte effettuate dall'utente sotto forma di Integer.
	 * @return Il valore dell'attributo target predetto per il nodo foglia su cui l'utente e' arrivato.
	 * @throws UnknownValueException Lanciata nel caso in cui l'utente abbia effettuato una scelta non valida
	 * 			durante l'esplorazione dell'albero.
	 * @throws IOException Nel caso di errore di comunicazione con il client.
	 * @throws ClassNotFoundException Nel caso non sia stata correttamente caricato il classfile associato ad un
	 * 			oggetto letto dallo stream.
	 */
	public Double predictClass(ObjectOutputStream out, ObjectInputStream in) throws UnknownValueException, IOException, ClassNotFoundException {
		
		if (root instanceof LeafNode) {

			/*
			 * Viene notificato all'utente che e' stato raggiunto un nodo foglia.
			 */
			out.writeObject("OK");

			// Viene ritornato il valore dell'attributo target predetto
		    return ((LeafNode) root).getPredictedClassValue(); 
		} else {

			/*
			 * La stringa "QUERY" serve a notificare l'utente che si e' arrivati ad un nodo di split.
			 */
			out.writeObject("QUERY");
			out.writeObject(((SplitNode) root).queryAsList());
			int risp;
			
			/*
			 * L'utente scegliera' un nodo figlio su cui prosegurie l'esplorazione. Questa scelta e' rappresentata
			 * tramite l'indice del figlio del nodo di split.
			 */
			risp = (Integer) in.readObject(); 

			if (risp == -1 || risp >= root.getNumberOfChildren()) {

				/*
				 * In caso di scelta errata viene sollevata una UnknownValueException. 
				 */
				throw new UnknownValueException("The answer should be an integer between 0 and " + (root.getNumberOfChildren() - 1) + "!");  
			} else {

				/*
				 * Se il figlio selezionato e' sempre un nodo di split, viene chiamata ricorsivamente predictClass (dall'istanza di RegressionTree
				 * che ha come radice il figlio selezionato).
				 */
				return childTree[risp].predictClass(out, in);  
			}
		}
	}
	
	/**
	 * Metodo di stampa delle regole che descrivono in quale maniera e' stato generato e puo' essere
	 * esplorato l'albero.
	 */
	public void printRules() {
		
		// esplora i nodi dell'albero, se e' di split crea la regola, se e' leaf si scrive il valore predetto
		System.out.println("********* RULES **********\n");
		String toPrint = new String();
		printRules(toPrint);
		System.out.println("*************************\n");
	}

	/**
	 * Metodo ricorsivo di supporto per la stampa delle predizioni generate dall'albero.
	 * 
	 * @param current Stringa su cui si sta costruendo la rappresentazione testuale dell'albero.
	 */
	private void printRules(String current) {

		/*
		 * Se la radice dell'istanza di RegressionTree e' un nodo di split, allora si costruisce una
		 * nuova stringa che rappresenta la decisione parziale presa sui possibili valori di split.
		 */
        if (root instanceof SplitNode) {

        	String partialRule = ((SplitNode) root).getAttribute().getName();
            for (int i = 0; i < root.getNumberOfChildren(); i++) {
                
                String comparator = ((SplitNode) root).getSplitInfo(i).getComparator();
                Object splitValue = ((SplitNode) root).getSplitInfo(i).getSplitValue();
                
                /*
                 * Per ogni figlio generato dal nodo di split, si effettua una chiamata ricorsiva a printRules.
                 * Se il figlio e' nuovo nodo di split, allora si aggiunge la stringa " AND " in previsione di
                 * un nuovo suffisso rappresentante un valore di split.
                 */
                if (childTree[i].root instanceof SplitNode) {
                    
                    childTree[i].printRules((current + partialRule + comparator + splitValue + " AND " ));
                } else {
                    
                    childTree[i].printRules((current + partialRule + comparator + splitValue));
                }
            }
        } else if (root instanceof LeafNode) {
            
        	/* 
        	 * Se la radice dell'istanza di RegressionTree corrente e' un nodo foglia, si stampa la stringa
        	 * costruita, con la predizione dell'attributo target associata.
        	 */
            System.out.print(current + " ==> Class=" + ((LeafNode) root).getPredictedClassValue() + "\n");
        }
    }
	
	/**
	 * Metodo di stampa per un albero di regressione.
	 * 
	 */
	public void printTree() {

		System.out.println("********* TREE **********\n");
		System.out.println(toString());
		System.out.println("*************************\n");
	}
	
	/**
	 * Sovrascrittura del metodo <code>toString</code> di <code>Object</code>.
	 * 
	 * @return lo stato dell'oggetto sotto forma di stringa.
	 */
	@Override
	public String toString() {

		String tree = root.toString() + "\n";
		
		if (root instanceof LeafNode) {
		
		} else {

			for (int i = 0; i < childTree.length; i++) {

				tree += childTree[i];	
			}
		}

		return tree;
	}
	
	/**
	 * Metodo per serializzare un albero di regressione in un file.
	 * 
	 * @param nomeFile Nome del file in cui si vuole salvare l'albero di regressione.
	 * @throws FileNotFoundException Lanciata nel caso in cui e' impossibile la creazione o la scrittura del file specificato
	 * @throws IOException Lanciata nel caso in cui si verifichino errori nella serializzazione dell'albero.
	 */
	public void salva(String nomeFile) throws FileNotFoundException, IOException {

		FileOutputStream whereSave = new FileOutputStream(nomeFile);
		ObjectOutputStream whereSaveStream = new ObjectOutputStream(whereSave);
		whereSaveStream.writeObject(this);
		whereSaveStream.close();
		whereSave.close();
	}
	
	/**
	 * Metodo per il caricamento di un albero di regressione serializzato in precedenza.
	 * 
	 * @param nomeFile Nome del file contenente un'istanza di RegressionTree serializzata.
	 * @return L'istanza di RegressionTree serializzata nel file specificato.
	 * @throws FileNotFoundException Lanciata nel caso in cui non sia stato trovato il file specificato.
	 * @throws IOException Lanciata in caso di errori nella lettura da file.
	 * @throws ClassNotFoundException Lanciata nel caso in cui non sia stata correttamente caricata la classe RegressionTree.
	 */
	public static RegressionTree carica(String nomeFile) throws FileNotFoundException, IOException, ClassNotFoundException {
		
		FileInputStream whereLoad = new FileInputStream(nomeFile);
		ObjectInputStream whereLoadStream = new ObjectInputStream(whereLoad);
		RegressionTree toReturn =  (RegressionTree) whereLoadStream.readObject();
		whereLoadStream.close();
		whereLoad.close();
		return toReturn;
	}
}
