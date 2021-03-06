package data;

/**
 * Classe creata per modellare un generico attributo continuo di un esempio.<br>
 * Per "attributo continuo" si intende dal dominio continuo (generalmente numerico).
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */

@SuppressWarnings("serial")
public class ContinuousAttribute extends Attribute {
	
	/**
	 * Costruttore per la classe <code>ContinuousAttribute</code>. Richiama semplicemente il costruttore della
	 * super-classe.
	 * 
	 * 
	 * @param name Stringa contenente il nome dell'attributo.
	 * @param index Intero indicante l'indice identificativo dell'attributo.
	 * 
 	 * @see Attribute
	 */
	public ContinuousAttribute(String name, int index) {
		
		super(name, index);
	}

}
