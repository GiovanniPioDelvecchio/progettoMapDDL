import server.MultiServer;

/**
 * Classe utilizzata per l'esecuzione del server. Esso viene avviato
 * sulla porta 8080 della macchina su cui viene eseguito.
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 */
public class MainTest {

	public static void main(String [] args) {

		new MultiServer(8080);
	}

}
