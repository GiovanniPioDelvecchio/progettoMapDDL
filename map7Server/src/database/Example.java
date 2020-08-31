package database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Classe che modella una transazione (tupla) letta dalla base di dati.<br>
 * La classe implementa l'interfaccia <code>Iterable</code> in modo da poter essere esplorata
 * per mezzo di oggetti <code>Iterator</code> e l'interfaccia <code>Comparable</code> in modo da
 * poter confrontare due diverse transazioni.
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
public class Example implements Comparable<Example>, Iterable<Object> {
	
	/**
	 * Lista contenente l'insieme di elementi di una tupla
	 */
	private List<Object> example = new ArrayList<Object>();

	/**
	 * Metodo per aggiungere un elemento alla tupla.
	 * 
	 * @param o Oggetto da aggiungere in coda all'istanza di <code>Example</code>
	 */
	public void add(Object o){

		example.add(o);
	}
	
	/**
	 * Metodo getter per ottenere l'i-esimo valore della transazione.
	 * 
	 * @param i Indice intero non negativo indicante la posizione dell'elemento da leggere nella tupla.
	 * 
	 * @return L'i-esima istanza di <code>Object</code> contenuta nella tupla.
	 */
	public Object get(int i) {

		return example.get(i);
	}

	/**
	 * Implementazione del metodo compareTo per l'interfaccia <code>Comparable</code>
	 * Tutti gli oggetti all'interno della tupla devono implementare l'interfaccia comparable.
	 * 
	 * @param ex Istanza da confrontare con l'istanza corrente di Example
	 * 
	 * @return 0 Se le tuple contengono gli stessi elementi nello stesso ordine, -1 se l'i-esimo
	 *         elemento dell'istanza corrente di Example è minore (secondo il compareTo dell'oggetto) dell'i-esimo
	 *         elemento di ex, 1 altrimenti.
	 *         
	 * @throws ClassCastException Lanciata se uno degli oggetti della tupla non implementa l'interfaccia
	 * 		   <code>Comparable</code>.
	 */
	public int compareTo(Example ex) {
		
		int i = 0;
		
		for (Object o : ex.example) {

			if (!o.equals(this.example.get(i))) {

				return ((Comparable) o).compareTo(example.get(i));
			}
			
			i++;
		}
		
		return 0;
	}

	/**
	 * Sovrascrittura del metodo <code>toString</code> di <code>Object</code>.
	 * 
	 * @return Una stringa che concatena le stringhe restituite dal <code>toString</code> di ogni oggetto nella tupla.
	 */
	public String toString() {
		
		StringBuffer str = new StringBuffer("");
		
		for(Object o : example) {
		
			str.append(o.toString() +  " ");
		}
		
		return str.toString();
	}

	/**
	 * Implementazione del metodo richiesto dall'interfaccia <code>Iterable</code>.
	 * 
	 * @return Un oggetto di tipo iteratore per scorrere gli elementi all'interno della tupla.
	 */
	public Iterator<Object> iterator() {
		
		return example.iterator();
	}
}
