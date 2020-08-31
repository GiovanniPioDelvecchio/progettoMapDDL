import server.MultiServer;
import util.Constants;

/**
 * Classe utilizzata per l'esecuzione del server. Esso viene avviato di default
 * sulla porta 8080 della macchina su cui viene eseguito, a meno che non si specifichi diversamente
 * da CLI.
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 */
public class MainTest {
	
	/**
	 * Main del server. Inizializza un thread di tipo MultiServer sulla porta indicata tra gli argomenti
	 * a riga di comando. Se tale porta è assente, viene utilizzata la porta di default 8080, se non è possbile
	 * parsificarla o risulta essere fuori range, viene visualizzato un messaggio di errore e il server
	 * <b>non</b> viene avviato.
	 * 
	 * @param args vettore di stringhe acquisite a riga di comando che potrebbe contenere la porta
	 *        dove istanziare il server.
	 */
	public static void main(String [] args) {
		
		int port = Constants.DEFAULT_PORT;
		
		if (args.length != 0) {
			try {
				
				port = Integer.parseInt(args[0]);
				
				if (port < Constants.MIN_PORT || port > Constants.MAX_PORT) {
					
					System.out.println(Constants.ERROR_BAD_PORT);
					return;
				}
			} catch(NumberFormatException e) {
				
				System.out.println(Constants.ERROR_BAD_PORT);
				return;
			}
		}
		
		new MultiServer(port);
	}
	

}
