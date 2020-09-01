package server;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Instant;

import data.Data;
import data.TrainingDataException;
import tree.RegressionTree;
import util.Constants;

/**
 * Classe utilizzata per comunicare con un singolo Client su di un Thread separato da quello principale.<br>
 * 
 * Questa classe viene istanziata dal metodo <code>run()</code> di <code>MultiServer</code> quando un Client
 * vuole connettersi al Server. Il suo compito principale e' quello di comunicare con il Client ed effettuare
 * le operazioni richieste. <br>
 * Come per <code>MultiServer</code>, anche questa classe opera sullo stesso file di log, passato come parametro
 * al costruttore.
 * 
 * 
 * @see MultiServer
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
public class ServerOneClient extends Thread {

	/**
	 * Socket utilizzata per la comunicazione con un client.
	 */
	private Socket socket;
	
	/**
	 * Stream di Input su cui verranno ricevute le comunicazioni del Client. 
	 */
	private ObjectInputStream in;
	
	/**
	 * Stream di Output su verranno inviate le comunicazioni al Client.
	 */
	private ObjectOutputStream out;
	
	/**
	 * Stream testuale per la scrittura del log dell'attivita' del thread su file.
	 */
	private BufferedWriter logFile;
	
	/**
	 * Costruttore di <code>ServerOneClient</code>.
	 * 
	 * La nuova istanza della classe comunichera' con il Client su di un nuovo thread.
	 * 
	 * @param s Socket di comunicazione con il Client che ha effettuato la connessione.
	 * @param l Stream testuale già aperto che viene utilizzato per trascrivere i log.
	 * 
	 * @throws IOException Lanciata in caso di errore di comunicazione con il Client.
	 */
	public ServerOneClient(Socket s, BufferedWriter l) throws IOException {

	
		// Si avvalorano gli attributi di classe utilizzati per la comunicazione con il Client
		socket = s;
		in = new ObjectInputStream(s.getInputStream());
		out = new ObjectOutputStream(s.getOutputStream());
		this.logFile = l;
		
		// Log di avvio della comunicazione con il Client
		String connectionLog = "Thread " + this.getId() + ": " + "Connected with client " + socket + " at " + Instant.now();
		System.out.println(connectionLog);

		synchronized (logFile) {

			logFile.write("\n" + connectionLog);
			logFile.flush();
		}
		
		/*
		 * Si avvia l'esecuzione del metodo run() su di un nuovo Thread, per permettere la comunicazione
		 * con piu' Client contemporaneamente da parte di MultiServer.
		 */
		this.start();
	}
	
	/**
	 * Metodo principale di esecuzione di <code>ServerOneClient</code>.
	 * 
	 * Si occupa di ricevere richieste dal Client e fornire i dati richiesti.
	 * Ogni operazione e' identificata da un Integer, che viene letto e scritto tramite
	 * gli ObjectInputStream e ObjectOutputStream.
	 * 
	 */
	public void run() {

		
		Integer clientDecision = null;
		String log;
		RegressionTree tree = null;
		boolean noTable = true;
		
		try {
			
			// Viene letta la prima scelta effettuata dall'utente tramite il Client
			clientDecision = (Integer) in.readObject();
			/*
			 * Si attende poi da parte del client la stringa contenente il nome di una tabella/file valido
			 * (finchè noTable = false)
			 */
			while (noTable) {
				String trainingfileName = (String) in.readObject();
				
				if (trainingfileName.equals(Constants.CLIENT_ABORT)) {
					// Se viene ricevuta la stringa speciale di chiusura (l'utente torna alla home)
					// si procede alla chiusura della connessione
					clientDecision = Constants.CLIENT_END;
					return;
				}
	
				// Viene letto l'intero 0 se l'utente vuole generare un nuovo albero di regressione
				if (clientDecision == Constants.CLIENT_CREATE) {
	
					Data trainingSet = null;
					
					try {
	
						/*
						 * Viene inizializzato il training set a partire dalla tabella inserita in input
						 */
						trainingSet = new Data(trainingfileName);
						tree = new RegressionTree(trainingSet);

						/*
						 * Per comunicare al Client che non ci sono stati problemi, viene inviata la stringa "OK"
						 */
						out.writeObject(Constants.SERVER_OK);
						
						/*
						 * Il salvataggio dell'albero avviene alla lettura dell'intero 1 sullo stream di comunicazione
						 */
						clientDecision = (Integer) in.readObject();
						if (clientDecision == Constants.CLIENT_SAVE) {

							try {
								
								tree.salva(trainingfileName + ".dmp");
								out.writeObject(Constants.SERVER_OK);
								// Viene comunicato al Client che il salvataggio e' andato a buon fine
							} catch (IOException e) {

								/*
								 * In caso di errore durante il salvataggio, viene effettuato un log dell'errore
								 */
								System.out.println(e.toString());
								
								synchronized (logFile) {

									logFile.write("\n" + e.toString());
									logFile.flush();
								}
								
								out.writeObject(e.toString());
							} finally {
								/*
								 * anche in caso di errore nel salvataggio, la predizione viene eseguita.
								 */
								noTable = false;
							}
						}
					} catch (TrainingDataException e) {
	
						/*
						 * Nel caso in cui il costrttore di Data sollevi un'eccezione (quindi non e' possibile creare
						 * l'albero di regressione), viene inviata la motivazione dell'errore al Client sotto forma di
						 * stringa. 
						 */
						out.writeObject(e.toString());
					}
				} 
				
				/*
				 * La lettura dell'intero 2 indica il caricamento di un albero di regressione presente in memoria.
				 */
				if (clientDecision == Constants.CLIENT_LOAD) {
					
					try {
	
						/*
						 * Al nome della tabella fornita dall'utente, viene aggiunta l'estensione ".dmp", e successivamente viene
						 * caricata l'istanza di RegressionTree serializzata in precedenza nel file da tale nome.
						 */
						tree = RegressionTree.carica(trainingfileName + ".dmp");
						noTable = false;
						/*
						 * In caso di corretto caricamento da parte del Server dell'albero di regressione da file,
						 * viene inviata la stringa "OK" al Client.
						 */
						out.writeObject("OK");
						
					} catch (FileNotFoundException e) {
	
						/*
						 * Nel caso in cui non fosse presente in memoria il file dal nome specificato, viene inviato al Client
						 * un messaggio di errore, ma la connessione non viene interrotta.
						 */
						out.writeObject(e.toString());
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
	
				}
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
			while (clientDecision == Constants.CLIENT_PREDICT) {
				
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
					 * Nel caso in cui termini in anticipo l'esplorazione dell'albero di regressione,
					 * viene catturata una UnknownValueException. Di conseguenza, si passa alla chiusura
					 * della connessione.
					 */
					clientDecision = Constants.CLIENT_END;
					return;
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

				synchronized (logFile) {

					logFile.write("\n" + log);
					logFile.flush();
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
				if (clientDecision != null && clientDecision == -1) {

					log = "Thread " + this.getId() + ": " + "Closing connection with " + socket + " at " + Instant.now();
					System.out.println(log);
					 
					synchronized (logFile) {
						
						logFile.write("\n" + log);
						logFile.flush();
					}
				} else {

					log = "Thread " + this.getId() + ": " + "Aborted connection with " + socket + " at " + Instant.now();
					System.out.println(log);
					synchronized (logFile) {

						logFile.write("\n" + log);
						logFile.flush();
					}
				}
				socket.close();
			} catch (IOException e) {

				/*
				 * In caso di errore di comunicazione con il Client durante la chiusura del Socket, viene
				 * stampato un messaggio di errore sulla console del Server e nel file di log.
				 */
				log = "Thread " + this.getId() + ": " + "error closing connection with " + socket + " : " + e.getClass().getName()
						+ " : " + e.getMessage();
				System.out.println(log);
			
				try {
					
					
					synchronized (logFile) {
	
							logFile.write("\n" + e.toString());
							logFile.flush();
					}
				} catch (IOException e1) {

					System.out.println("Unable to write on log file: " + e1.getMessage());
				}
			}
		}
	}
}
