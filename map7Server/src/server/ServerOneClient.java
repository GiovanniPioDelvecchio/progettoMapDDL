package server;

import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Instant;

import data.Data;
import data.TrainingDataException;
import tree.RegressionTree;

/**
 * Classe utilizzata per comunicare con un singolo Client su un Thread separato da quello principale.
 * 
 * Questa classe viene istanziata dal metodo <code>run()</code> di <code>MultiServer</code> quando un Client
 * vuole connettersi al Server. Il suo compito principale e' quello di comunicare con il Client ed effettuare
 * le operazioni richieste.
 * 
 * @see MultiServer
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
public class ServerOneClient extends Thread {

	
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private String logFileName;
	
	/**
	 * Costruttore di ServerOneClient.
	 * 
	 * La nuova istanza della classe comunichera' con il Client su di un nuovo thread.
	 * 
	 * @param s Socket di comunicazione con il Client che ha effettuato la connessione.
	 * @param logFileName Nome del file che viene utilizzato per trascrivere i log
	 * @throws IOException Lanciata in caso di errore di comunicazione con il Client.
	 */
	public ServerOneClient(Socket s, String logFileName) throws IOException {

		FileWriter logFile = new FileWriter(logFileName, true);
		
		// Si avvalorano gli attributi di classe utilizzati per la comunicazione con il Client
		socket = s;
		in = new ObjectInputStream(s.getInputStream());
		out = new ObjectOutputStream(s.getOutputStream());
		this.logFileName = logFileName;
		
		// Log di avvio della comunicazione con il Client
		String connectionLog = "Thread " + this.getId() + ": " + "Connected with client " + socket + " at " + Instant.now();
		System.out.println(connectionLog);
		synchronized (logFile) {

			logFile.write("\n" + connectionLog);
			logFile.close();
		}
		
		/*
		 * Si avvia l'esecuzione del metodo run() su di un nuovo Thread, per permettere la comunicazione
		 * con piu' Client contemporaneamente da parte di MultiServer.
		 */
		this.start();
	}
	
	/**
	 * Metodo principale di esecuzione di ServerOneClient.
	 * 
	 * Si occupa di ricevere richieste dal Client e fornire i dati richiesti.
	 * Ogni operazione e' identificata da un Integer, che viene letto e scritto tramite
	 * gli ObjectInputStream/ObjectOutputStream.
	 * 
	 */
	public void run() {

		
		Integer clientDecision = null;
		String log;
		FileWriter logFile;
		
		try {
			
			logFile = new FileWriter(logFileName, true);
			
			// Viene letta la prima scelta effettuata dall'utente tramite il Client
			clientDecision = (Integer) in.readObject();
			RegressionTree tree = null;

			// Viene letto l'intero 0 se l'utente vuole generare un nuovo albero di regressione
			if (clientDecision == 0) {

				Data trainingSet = null;
				
				try {

					/*
					 * Viene letta dallo Stream la stringa contenente il nome della tabella SQL
					 * contenente il dataset da cui creare l'albero di regressione.
					 */
					String trainingfileName = (String) in.readObject();
					trainingSet = new Data(trainingfileName);
					tree = new RegressionTree(trainingSet);

					/*
					 * Per comunicare al Client che non ci sono stati problemi, viene inviata la stringa "OK"
					 */
					out.writeObject("OK");
					
					/*
					 * Il salvataggio dell'albero avviene alla lettura dell'intero 1 sullo stream di comunicazione
					 */
					clientDecision = (Integer) in.readObject();
					if (clientDecision == 1) {
						
						try {
							
							tree.salva(trainingfileName + ".dmp");
							
							// Viene comunicato al Client che il salvataggio e' andato a buon fine
							out.writeObject("OK");
						} catch (IOException e) {
							
							/*
							 * In caso di errore durante il salvataggio, viene effettuato un log dell'errore
							 */
							System.out.println(e.toString());
							synchronized (logFile) {

								logFile.write("\n" + e.toString());
								logFile.close();
							}
						}
					}
				} catch (TrainingDataException e) {

					/*
					 * Nel caso in cui il costrttore di Data sollevi un'eccezione (quindi non e' possibile creare
					 * l'albero di regressione), viene inviata la motivazione dell'errore al Client sotto forma di
					 * stringa. Cio' portera' alla terminazione della comunicazione con il Client.
					 */
					out.writeObject(e.toString());
					return;
				}
			} 
			
			/*
			 * La lettura dell'intero 2 indica il caricamento di un albero di regressione presente in memoria.
			 */
			if(clientDecision == 2) {
				
				Data trainingSet = null;
				try {

					/*
					 * Viene letta dal Client la stringa contenente il nome della tabella SQL di cui si era creato
					 * l'albero di regressione in precedenza. L'utente non ha bisogno di inserire l'estenzione ".dmp"
					 * nella stringa inserita.
					 */
					String trainingfileName = (String) in.readObject();
					tree = RegressionTree.carica(trainingfileName + ".dmp");
				} catch (FileNotFoundException e) {

					/*
					 * Nel caso in cui non fosse presente in memoria il file dal nome specificato, viene inviato al Client
					 * un messaggio di errore, la comunicazione con il Client viene terminata e l'istanza di ServerOneClient 
					 * ferma la sua esecuzione.
					 */
					out.writeObject(e.toString());
					return;
				} catch (IOException e) {

					/*
					 * Se si verificano errori durante il caricamento dell'albero di regressione da file, si invia al Client
					 * la motivazione dell'errore, e si termina la comunicazione con esso.
					 */
					out.writeObject(e.toString());
					return;
				} catch (ClassNotFoundException e) {

					/*
					 * In caso di mancato caricamento del classfile di RegressionTree da parte del Server, viene sollevata
					 * una ClassNotFoundException. La motivazione dell'errore viene inviata al Client, e la comunicazione viene
					 * chiusa.
					 */
					out.writeObject(e.toString());
					return;
				} 
				
				/*
				 * In caso di corretto caricamento da parte del Server dell'albero di regressione da file,
				 * viene inviata la stringa "OK" al Client.
				 */
				out.writeObject("OK");
			}
			
			/*
			 * Viene letta l'azione che vuole effettuare il Client.
			 */
			clientDecision = (Integer) in.readObject();
			
			/*
			 * L'intero 3 indica l'avvio dell'esplorazione dell'albero di regressione da parte dell'utente.
			 * Finche' l'operazione da svolgere sara' identificata da questo numero, verra' avviato un nuovo
			 * processo di esplorazione dell'albero.
			 */
			while (clientDecision == 3) {
				
				try {

					/*
					 * Vengono passati a predictClass gli stream di Input e Output di comunicazione con il Client.
					 * Il processo di esplorazione dell'albero viene delegato a predictClass, e il suo risultato sara'
					 * scritto sull ObjectOutputStream da parte del Server.
					 */
					out.writeObject(tree.predictClass(out, in));
					
					/*
					 * Questa lettura e' effettuata nel caso in cui l'utente voglia esplorare nuovamente l'albero.
					 */
					clientDecision = (Integer) in.readObject();
				} catch (UnknownValueException e) {

					/*
					 * Nel caso in cui l'utente effettui una scelta sbagliata durante l'esplorazione dell'albero di regressione,
					 * viene catturata una UnknownValueException. Il suo toString() verra' inviato come messaggio di errore 
					 * al Client. La connessione rimane aperta per permettere una nuova esplorazione dell'albero.
					 */
					out.writeObject(e.toString());
				}
			}
		} catch (IOException | ClassNotFoundException e) {

			/*
			 * In caso di errore di comunicazione con il Client, viene stampato un messaggio di log sulla console del Server, e viene
			 * terminata l'esecuzione dell'istanza di ServerOneClient.
			 */
			log = "Thread " + this.getId() + ": " + e.getMessage();
			System.out.println(log);
			
			try {
				
				logFile = new FileWriter(logFileName, true);
				synchronized (logFile) {

					logFile.write("\n" + log);
					logFile.close();
				}
			} catch (IOException e2) {

				System.out.println("Unable to log on file: " + e2.getMessage());
			}
			return;
		} finally {

			/*
			 * Nel blocco finally si tenta la terminazione della comunicaizone con il Client, indicata dalla lettura sullo stream
			 * dell'intero -1.
			 */
			try {

				/*
				 * Se clientDecision e' null, vuol dire che e' avvenuto un errore di comunicazione con il Client, quindi
				 * la variabile non e' stata avvalorata. Cio' porta alla segnalazione di una chiusura con errore della
				 * comunicazione.
				 */
				logFile = new FileWriter(logFileName, true);
				if (clientDecision != null && clientDecision == -1) {

					log = "Thread " + this.getId() + ": " + "closing connection with " + socket + " at " + Instant.now();
					System.out.println(log);
					 
					synchronized (logFile) {
						
						logFile.write("\n" + log);
						logFile.close();
					}
				} else {

					log = "Thread " + this.getId() + ": " + "aborted connection with " + socket + " at " + Instant.now();
					System.out.println(log);
					synchronized (logFile) {

						logFile.write("\n" + log);
						logFile.close();
					}
				}
				socket.close();
			} catch (IOException e) {

				/*
				 * In caso di errore di comunicazione con il Client durante la chiusura del Socket, viene
				 * stampato un messaggio di errore sulla console del Server.
				 */
				log = "Thread " + this.getId() + ": " + "error closing connection with " + socket + " : " + e.getClass().getName()
						+ " : " + e.getMessage();
				System.out.println(log);
			
				try {
					
					logFile = new FileWriter(logFileName, true);
					synchronized (logFile) {
	
							logFile.write("\n" + e.toString());
							logFile.close();
					}
				} catch (IOException e1) {

					System.out.println("Unable to write on log file: " + e1.getMessage());
				}
			}
		}
	}
}
