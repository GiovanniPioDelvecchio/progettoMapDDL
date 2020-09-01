import server.MultiServer;

/**
 * Classe utilizzata per l'esecuzione del server. Esso viene avviato
 * sulla porta 8080 della macchina su cui viene eseguito.
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 */
public class MainTest {

	/**
	 * Metodo main della classe. Crea un nuovo oggetto <code>MultiServer</code>, che gestisce
	 * l'esecuzione del server sulla porta 8080 della macchina locale.
	 * 
	 * @param args Argomenti a riga di comando, non utilizzati dall'applicazione.
	 */
	public static void main(String [] args) {

		new MultiServer(8080);
	}

}
