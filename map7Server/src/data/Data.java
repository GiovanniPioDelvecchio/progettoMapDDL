package data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import database.DatabaseConnectionException;
import database.DbAccess;
import database.EmptySetException;
import database.Example;
import database.TableData;
import database.TableSchema;
import database.Column;

import util.Constants;

/**
 * Classe creata per modellare un insieme di esempi per l'addestramento di un albero di regressione.<br>
 * Una sua istanza puo' essere inizializzata a partire da una tabella MySQL di un database attraverso i metodi 
 * e le classi esposte dal package database.
 * 
 * @see database
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
public class Data {
	
	/**
	 * Lista contenente gli esempi del training set.
	 */
	private List<Example> data = new ArrayList<>();
	
	/**
	 * Intero indicante il numero complessivo di esempi del training set.
	 */
	private int numberOfExamples;
	
	/**
	 * Lista contenente gli attributi indipendenti (in ordine) contenuti in ciascun esempio.
	 */
	private List<Attribute> explanatorySet;
	
	/**
	 * Attributo continuo di classe, o "attributo target", del training set.
	 */
	private ContinuousAttribute classAttribute;
	
	/**
	 * Costruttore di <code>Data</code>.
	 * Inizializza il training set a partire da una tabella di un database, il cui nome e' fornito in 
	 * input. Tale tabella deve essere non vuota, con almeno due attributi di cui l'ultimo deve sempre essere l'attributo target.
	 * 
	 * @param tableName Stringa contenente il nome della tabella da cui inizializzare il training set.
	 * 
	 * @throws TrainingDataException viene lanciata se vi e' un errore nella connessione alla base di dati,
	 * 		   l'attributo target non e' dichiarato come numerico o ha valori non accettabili,
	 * 		   vi sono meno di due attributi o la tabella e' vuota, non presente o ha valori nulli.
	 */
	public Data(String tableName) throws TrainingDataException {
		
		DbAccess db = new DbAccess();
		
		try {
			
			db.initConnection();
		} catch (DatabaseConnectionException e) {
			/* 
			 * si rilancia l'eccezione ottenuta in caso di errore di connessione
			 * sottoforma di TrainingDataException.
			 */
			throw new TrainingDataException(Constants.ERROR_NO_DATABASE_CONNECTION);
		}
		
		TableSchema schema;
		TableData data;
		explanatorySet = new LinkedList<>();
		
		try {
			
			schema = new TableSchema(db, tableName);
			data = new TableData(db);
			/* 
			 * Si leggono dal database lo schema della tabella e i suoi esempi.
			 */
			
			if (schema.getNumberOfAttributes() == 0) {
				
				// Schema e' restituito senza attributi se la tabella non esiste.
				throw new TrainingDataException(Constants.ERROR_TABLE_NOT_FOUND);
			}
			
			if (schema.getNumberOfAttributes() < 2) {
				
				// Si lancia l'eccezione se gli attributi sono meno di due.
				throw new TrainingDataException(Constants.ERROR_TOO_FEW_ATTRIBUTES);
			}
			
			if (!schema.getColumn(schema.getNumberOfAttributes() - 1).isNumber()) {
				
				// Si lancia un'eccezione se l'ultimo attributo (target) della tabella non e' numerico.
				throw new TrainingDataException(Constants.ERROR_NO_CLASS_ATTRIBUTE);
			}
			
			if(data.hasNull(tableName)) {
				
				// Si lancia un'eccezione se vi è una tupla con almeno un valore nullo
				throw new TrainingDataException(Constants.ERROR_NULL_VALUES);
			}
			
			int index = 0;
			
			for (Column c : schema) {
				// Per ogni colonna nello schema si crea il relativo attributo e lo si inserisce nell'explanatory set.
				if (c.isNumber()) {
					
					if (index == schema.getNumberOfAttributes() - 1) {
						

						//se l'attributo e' l'ultimo, allora e' l'attributo di classe
						classAttribute = new ContinuousAttribute(c.getColumnName(), index);
					} else {
						
						explanatorySet.add(new ContinuousAttribute(c.getColumnName(), index));
					}
					
				} else {
					
					/*
					 * Poiche' getDistinctColumnValues restituisce un Set di Object, ed esso non puo' essere
					 * direttamente "castato" in un set di stringhe (utile ad inizializzare l'attributo discreto)
					 * si esegue un "travaso" con cast esplicito.
					 * In caso di errore durante il cast viene sollevata un'eccezione gestita in seguito.
					 */
					Set<String> set = new TreeSet<>();
					Set<Object> explSet = data.getDistinctColumnValues(tableName, c);
					
					for(Object o : explSet) {
						
						set.add((String)o);
					}
					
					explanatorySet.add(new DiscreteAttribute(c.getColumnName(), index, set));
				}
				
				index++;
			}
			
			/*
			 * Si termina acquisendo gli esempi e la dimensione del training set.
			 */
			this.data = data.getTransazioni(tableName);
			numberOfExamples = this.data.size();
			
		} catch (SQLException e) {
			// In caso di errore durante la manipolazione del database, viene rilanciata l'eccezione sottoforma di TDException
			throw new TrainingDataException(e.getMessage());
		} catch (ClassCastException e) {
			
			// Viene rilanciata anche l'eccezione generata in caso di errore durante il casting...
			throw new TrainingDataException(Constants.ERROR_BAD_DISCRETE_VALUE_CAST);
		} catch (EmptySetException e) {
			
			// ...e in caso di training set vuoto.
			throw new TrainingDataException(Constants.ERROR_EMPTY_TABLE);
		} finally {
			/* 
			 * Qualsiasi cosa accada nel blocco precedente, si chiude la connessione al database.
			 * Ricordiamo che se la connessione rimane aperta, potrebbero essere generati ulteriori errori
			 * poiche' il database ha un massimale di connessioni aperte che puo' mantenere.
			 */ 
			try {
				
				db.closeConnection();
			} catch (DatabaseConnectionException e) {

				// Si lancia un'eccezione anche in caso di errore durante la chiusura del database
				throw new TrainingDataException(Constants.ERROR_DB_CONNECTION_CLOSING);
			}
		}
		
	}
	
	/**
	 * Sovrascrittura del metodo <code>toString</code> di <code>Object</code>.
	 * 
	 * @return una stringa contenente tutti gli esempi del training set separati dal carattere di fine linea e
	 * 		   coi valori separati da virgole.
	 */
	 @Override
    public String toString() {
        
        StringBuffer value = new StringBuffer("");

        for (int i = 0; i < numberOfExamples; i++) {
            
            for (int j = 0; j < explanatorySet.size(); j++) {
    
                value.append(data.get(i).get(j) + ",");
            }
                
            value.append(data.get(i).get(explanatorySet.size() - 1) + "\n");
        }

        return value.toString();
    }

	/**
	 * Metodo getter per cui dato un indice (partendo da 0), restituisce il valore
	 * dell'attributo di classe dell'esempio corrispondente a tale indice.
	 * 
	 * @param exampleIndex Intero non negativo rappresentante un indice di un esempio. 
	 * 
	 * @return un oggetto di tipo Double contenente il valore dell'attributo target dell'esempio.
	 * 
	 * @throws IndexOutOfBoundsException Lanciata se l'indice fornito e' fuori range.
	 */
	public Double getClassValue(int exampleIndex) {
		
			return (double) data.get(exampleIndex).get(classAttribute.getIndex());
	}

	/**
	 * Metodo getter per la dimensione del training set.
	 * 
	 * @return Un intero indicante il numero di esempi dell'insieme di training.
	 */
	public int getNumberOfExamples() {
		
		return numberOfExamples;
	}
  
	/**
	 * Metodo getter per cui dato un indice di un esempio e un indice di attributo, restituisce il valore dell'attributo
	 * corrispondente all'esempio indicato in input.
	 * 
	 * @param exampleIndex Intero non negativo rappresentante un indice di un esempio. 
	 * @param attributeIndex Intero non negativo rappresentante un indice di un attributo.
	 * 
	 * @return Un oggetto di tipo <code>Object</code> contenente il valore dell'attributo indicato nell'esempio indicato.
	 * 
	 * @throws IndexOutOfBoundsException Lanciata se uno dei due indici forniti in input e' fuori range.
	 */
	public Object getExplanatoryValue(int exampleIndex, int attributeIndex) {
		
			return data.get(exampleIndex).get(attributeIndex);

	}
	

	/**
	 * Metodo getter per ottenere, dato un indice intero, il corrispondente attributo tra quelli registrati nel data set.
	 * 
	 * @param index Indice intero non negativo che indica un attributo del data set.
	 * 
	 * @return L'attributo rinvenuto al corrispondente indice.
	 * 
	 * @throws IndexOutOfBoundsException Lanciata se l'indice fornito e' fuori range.
	 */
	public Attribute getExplanatoryAttribute(int index) {

			return explanatorySet.get(index);
	}

	/**
	 * Metodo getter per ottenere l'attributo di classe del training set.
	 *
	 * @return L'attributo continuo del target nel training set.
	 */
	public ContinuousAttribute getClassAttribute()	{
    
		return classAttribute;
	}
	
	/**
	 * Metodo getter per ottenere il numero di attributi del training set.
	 * 
	 * @return Il numero di attributi del dataset.
	 */
	public int getNumberOfExplanatoryAttributes() {
		
		return explanatorySet.size();
	}
	
	/**
	 * Metodo di sorting per <code>Data</code>. Ordina il set rispetto all'attributo passato in input sulla porzione di esempi
	 * indicata da <code>beginExampleIndex</code> e <code>endExampleIndex</code>, utilizzando l'algoritmo del quicksort.
	 * 
	 * @param attribute Attributo rispetto al quale eseguire il sorting del set.
	 * @param beginExampleIndex Indice dell'esempio da cui comincia la porzione del set da ordinare.
	 * @param endExampleIndex Indice dell'esempio in cui finisce (compreso) la porzione del set da ordinare.
	 * 
	 * @throws IndexOutOfBoundsException Lanicata se uno dei due indici e' fuori range oppure
	 *		   l'indice dell'attributo lo e'.
	 */
	public void sort(Attribute attribute, int beginExampleIndex, int endExampleIndex) {
		
		quicksort(attribute, beginExampleIndex, endExampleIndex);
	}
	
	
	/**
	 * Metodo che cambia di posizione due esempi nel set.
	 * Viene utilizzato per l'implementazione del quicksort.
	 * 
	 * @param i Indice del primo elemento da scambiare.
	 * @param j Indice del secondo elemento da scambiare.
	 */
	private void swap(int i,int j) {

		Example temp = data.get(i);
		data.set(i, data.get(j));
		data.set(j, temp);
	}
	

	/**
	 * Metodo di supporto al quicksort per il partizionamento di una sezione di esempi su di un
	 * attributo Discreto.
	 * 
	 * @param attribute Attributo discreto rispetto al quale si sta eseguendo il sorting.
	 * @param inf Indice inferiore della porzione di esempi da partizionare.
	 * @param sup Indice superiore della porzione di esempi da partizionare.
	 */
	private int partition(DiscreteAttribute attribute, int inf, int sup) {
		
		int i, j;
		i = inf; 
		j = sup;
		int	med = (inf + sup) / 2;
		String x = (String)getExplanatoryValue(med, attribute.getIndex());
		
		swap(inf, med);
	
		while (true) {
			
			while (i <= sup && ((String)getExplanatoryValue(i, attribute.getIndex())).compareTo(x) <= 0) { 
				
				i++;
			}
		
			while (((String)getExplanatoryValue(j, attribute.getIndex())).compareTo(x) > 0) {
				
				j--;
			}
			
			if (i < j) { 
				
				swap(i, j);
			} else {
				
				break;
			}
		}
		
		swap(inf, j);
		
		return j;
	
	}

	
	
	/**
	 * Metodo di supporto al quicksort per il partizionamento di una sezione di esempi su di un
	 * attributo Continuo.
	 * 
	 * @param attribute Attributo continuo rispetto al quale si sta eseguendo il sorting.
	 * @param inf Indice inferiore della porzione di esempi da partizionare.
	 * @param sup Indice superiore della porzione di esempi da partizionare.
	 */
	private int partition(ContinuousAttribute attribute, int inf, int sup) {
		
		int i, j;
		i = inf; 
		j = sup; 
		int	med = (inf + sup) / 2;
		Double x = (Double)getExplanatoryValue(med, attribute.getIndex());
		
		swap(inf, med);
	
		while (true) {
			
			while (i <= sup && ((Double)getExplanatoryValue(i, attribute.getIndex())).compareTo(x) <= 0) { 
				
				i++; 
			}
		
			while (((Double)getExplanatoryValue(j, attribute.getIndex())).compareTo(x) > 0) {
				
				j--;
			}
			
			if (i < j) {
				
				swap(i, j);
			} else {
				
				break;
			}
		}
		
		swap(inf, j);
		
		return j;
	
	}
	
	/**
	 * Metodo di supporto che implementa l'algoritmo di quicksort per l'ordinamento del dataset.
	 * 
	 * @param attribute Attributo rispetto al quale si sta eseguendo il sorting.
	 * @param inf Indice inferiore della porzione di esempi da ordinare.
	 * @param sup Indice superiore della porzione di esempi da ordinare.
	 */
	private void quicksort(Attribute attribute, int inf, int sup) {
		
		if (sup >= inf) {
			
			int pos;
			
			if (attribute instanceof DiscreteAttribute) {
				
				pos = partition((DiscreteAttribute) attribute, inf, sup);
			} else {
				
				pos = partition((ContinuousAttribute) attribute, inf, sup);
			}
			
			if ((pos - inf) < (sup - pos + 1)) {
				
				quicksort(attribute, inf, pos - 1); 
				quicksort(attribute, pos + 1, sup);
			} else {
				
				quicksort(attribute, pos + 1, sup); 
				quicksort(attribute, inf, pos - 1);
			}
		}
	}

}