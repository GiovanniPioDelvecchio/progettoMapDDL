package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;

/**
 * Classe utilizzata per modellare un Server multithreaded per la comunicazione con piu' Client.
 * 
 * Il server si espone di default sulla porta 8080, e genera una nuova istanza di ServerOneClient ogni volta
 * che viene contatto da un Client.
 * 
 * @author Domentico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
public class MultiServer {

	// Porta di default dove viene locato il Server.
	private int PORT = 8080;
	
	/**
	 * Costruttore di MultiServer.
	 * L'istanziazione di MultiServer equivale all'esecuzione
	 * del server sulla macchina, poiche' richiama il metodo <code>run</code>.
	 * 
	 * @param port Porta su cui si vogliono offrire i servizi del Server.
	 */
	public MultiServer(int port) {

		PORT = port;
		try {
			
			// Log di avvio del Server
			System.out.println("Started server at " + Instant.now());
			this.run();
		} catch (IOException e) {

			/*
			 * Eventuali errori sollevati durante l'esecuzione del server vengono stampati sulla console.
			 */
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Metodo principale di esecuzione del Server multithreaded.
	 * 
	 * Il server rimane in attesa della comunicazione da parte di un Client. Nel momento in cui viene
	 * contattato da un Client, viene istanziato un nuovo ServerOneClient (che effettuera' la comunicazione
	 * vera e propria con il Client).
	 * 
	 * @throws IOException In caso in cui avvengano errori di comunicazione con un Client.
	 */
	private void run() throws IOException {

		ServerSocket ssocket = new ServerSocket(PORT);
		try {

			while (true) {
				
				// Il server rimane in attesa di un contatto da parte di un Client
				Socket csocket = ssocket.accept();
				try {

					new ServerOneClient(csocket);
				} catch (IOException e) {

					/*
					 * In caso di errore da parte di un'istanza di ServerOneClient, viene chiusa
					 * la socket di comunicazione con il Client e viene stampato un messaggio sulla console
					 * del Server.
					 */
					csocket.close();
					System.out.println("Connection to client failed :" + e.getMessage());
				}
			} 
		} finally {

			/*
			 * Viene sempre chiusa la socket del MultiServer una volta finita l'esecuzione.
			 */
			ssocket.close();
		}
	}
	
}
