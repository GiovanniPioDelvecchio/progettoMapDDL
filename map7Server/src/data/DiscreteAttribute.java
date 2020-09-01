package data;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Classe creata per modellare un generico attributo discreto di un esempio.<br>
 * Per "attributo discreto" s'intende dal dominio con valori discreti.<br>
 * Ogni attributo tiene traccia del dominio dei valori che esso può assumere e quest'ultimo
 * può essere scorso con un iteratore.
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
@SuppressWarnings("serial")
public class DiscreteAttribute extends Attribute implements Iterable<String> {
	
	/**
	 * Set di Stringhe che contiene i valori assumibili (dominio) dall'attributo.
	 * E' implementato come un TreeSet poichè l'ordinamento lessicografico delle stringhe
	 * ottimizza le operazioni di ricerca, che sono le più frequenti.
	 */
	private Set<String> values = new TreeSet<>();
	
	/**
	 * Costruttore per <code>DiscreteAttribute</code>.
	 * Richiama il costruttore della classe <code>Attribute</code> e poi avvalora il dominio dell'attributo
	 * con il Set passato in input.
	 * 
	 * @param name Stringa contenente il nome dell'attributo.
	 * @param index Indice intero non negativo che identifica l'attributo (e la sua posizione nel data set).
	 * @param values Set di stringhe utilizzato per avvalorare il dominio dell'attributo.
	 */
	public DiscreteAttribute(String name, int index, Set<String> values) {
		
		super(name, index);
		this.values = values;
	}
	
	/**
	 * Metodo getter per ottenere il numero di valori distinti assumibili dall'attributo.
	 * 
	 * @return Il numero di valori distinti del dominio dell'attirbuto
	 */
	public int getNumberOfDistinctValues() {
		
		return values.size();
	}

	/**
	 * Override del metodo dell'interfaccia <code>Iterable</code> che restituisce una istanza di <code>Iterator</code>,
	 * che permette di scorrere il dominio dell'attributo.
	 * 
	 * @return Un iteratore per scorrere i valori del dominio
	 */
	@Override
	public Iterator<String> iterator() {

		return values.iterator();
	}
	
}
