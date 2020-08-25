package server;

import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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
	
	// File dove verranno effettuate le stampe di log
	private String logFileName;
	
	/**
	 * Costruttore di MultiServer.
	 * L'istanziazione di MultiServer equivale all'esecuzione
	 * del server sulla macchina, poiche' richiama il metodo <code>run</code>.
	 * 
	 * @param port Porta su cui si vogliono offrire i servizi del Server.
	 */
	public MultiServer(int port) {

		String log;
		FileWriter logFile;

		logFileName = "server-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".log";
		PORT = port;
		try {
			
			log = "Started server at " + Instant.now();
			
			// Log di avvio del Server
			System.out.println(log);

			logFile = new FileWriter(logFileName, true);
			logFile.write(log);
			logFile.close();

			this.run();
		} catch (IOException e) {

			/*
			 * Eventuali errori sollevati durante l'esecuzione del server vengono stampati sulla console.
			 */
			System.out.println(e.getMessage());
		} finally {
			
			log = "Closing server at " + Instant.now();
			System.out.println(log);
			try {

				logFile = new FileWriter(logFileName, true);
				logFile.write("\n" + log);
				logFile.close();
			} catch (IOException e) {

				System.out.println("Failed to close the log file");
			}
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

		FileWriter logFile;
		ServerSocket ssocket = new ServerSocket(PORT);
		try {

			String log;
			while (true) {
				
				// Il server rimane in attesa di un contatto da parte di un Client
				Socket csocket = ssocket.accept();
				try {

					new ServerOneClient(csocket, logFileName);
				} catch (IOException e) {

					/*
					 * In caso di errore da parte di un'istanza di ServerOneClient, viene chiusa
					 * la socket di comunicazione con il Client e viene stampato un messaggio sulla console
					 * del Server.
					 */
					csocket.close();
					
					log = "Connection to client failed :" + e.getMessage();
					System.out.println(log);

					logFile = new FileWriter(logFileName, true);
					logFile.write("\n" + log);
					logFile.close();
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
