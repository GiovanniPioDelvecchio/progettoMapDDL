import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import utility.Keyboard;

/**
 * Classe utilizzata per definire un client CLI che comunica con il server definito in map7Server.
 * 
 * L'unico elemento contenuto nella classe e' il metodo <code>main</code>, che si occupa di mostrare
 * all'utente le informazioni fornite dal server e comunicare le operazioni da effettuare.
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 * 
 */
public class MainTest {

	/**
	 * Metodo che implementa un'interfaccia a riga di comando per comunicare con il server.
	 * 
	 * Per la comunicazione con il sever, il client invia degli interi per identificare l'azione
	 * da effettuare. Vengono stampati sia errori lato client che lato server.
	 * 
	 * @param args Gli argomenti con cui chiamare il main sono:<br>
	 * <ol>
	 * <li>l'indirizzo IP della macchina su cui e' eseguito il server</li>
	 * <li>la porta dove e' locato il server</li>
	 * </ol>
	 */
	public static void main(String[] args) {

		@SuppressWarnings("unused")
		/*
		 * La seguente variabile viene utilizzata solamente per verificare che l'indirizzo passato
		 * sia corretto, e non viene più utilizzata, pertanto il compilatore solleva un warning.
		 * Quest'ultimo viene qui ignorato poiche' la variabile e il suo utilizzo sono stati forniti in principio così.
		 */
		InetAddress addr;

		/*
		 * Se non sono stati forniti i parametri richiesti dall'applicazione, viene
		 * visualizzato un messaggio di errore e il programma viene terminato.
		 */
		if (args.length != 2) {

			System.out.println("Error: missing arguments");
			return;
		}

		try {

			addr = InetAddress.getByName(args[0]);
		} catch (UnknownHostException e) {

			System.out.println("Error: server not found");
			return;
		}

		Socket socket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;

		/*
		 * Viene inizializzata la socket di comunicazione con il client (e stream di input/output)
		 * tramite gli argomenti forniti al client.
		 */
		try {
			
			socket = new Socket(args[0], Integer.parseInt(args[1]));
			System.out.println(socket);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {

			/*
			 * Se il Client non riesce a contattare la macchina specificata dall'utente,
			 * verra' catturata una UnknownHostException, e il programma viene terminato.
			 */
			System.out.println("Error: server not found");
			return;
		} catch (IOException e) {

			/*
			 * In caso di errore di comunicazione con il Server (con cui si e' riuscita a stabilire
			 * una connessione), viene catturata una IOException e il programma viene terminato.
			 */
			System.out.println("Error during communication with server");
			return;
		}

		String answer = "";
		
		/*
		 * L'utente puo' scegliere se generare un nuovo albero di regressione a
		 * partire da un dataset presente nel database del server, o caricarne
		 * uno gia' creato in precedenza dal server. La scelta e' identificata
		 * da un intero.
		 */
		int decision = 0;
		do {
		
			System.out.println("Learn Regression Tree from data [1]");
			System.out.println("Load Regression Tree from archive [2]");
			decision = Keyboard.readInt();
		} while (!(decision == 1) && !(decision == 2));
		
		/*
		 * In entrambi i casi, l'utente dovra' fornire il nome del dataset di cui
		 * esplorare l'albero di regressione. Il nome corrisponde al nome della tabella
		 * SQL su cui sono memorizzati gli esempi nel server.
		 */
		String tableName = "";
		System.out.println("File name:");
		tableName = Keyboard.readString();

		try {

			/*
			 * Se l'utente vuole creare un  nuovo albero di regressione, viene inviato
			 * l'intero 0 al server, che creera' un nuovo albero di regressione a partire
			 * dal nome della tabella fornita dall'utente.
			 */
			if (decision == 1) {
				System.out.println("Starting data acquisition phase!");
				
				out.writeObject(0);
				out.writeObject(tableName);
				answer = in.readObject().toString();

				/*
				 * Se non sono stati sollevati problemi lato server, il client riceve
				 * la stringa "OK". In caso di errore, viene stampato il messaggio ricevuto e
				 * il programma viene terminato.
				 */
				if (!answer.equals("OK")) {

					System.out.println(answer);
					return;
				}

				/*
				 * A questo punto il client invia al server l'intero 1, la cui lettura
				 * lato server porta al salvataggio dell'albero di regressione appena creato.
				 */
				System.out.println("Starting learning phase!");
				out.writeObject(1);
			} else {

				/*
				 * Nel caso in cui l'utente voglia caricare un albero di regressione gia' creato,
				 * verra' inviato al server l'intero 2 e il nome dell'albero di regressione da caricare.
				 */
				out.writeObject(2);
				out.writeObject(tableName);	
			}
			
			// Se la comunicazione con il server e' andata a buon fine, verra' letta la stringa "OK" dal socket.
			answer = in.readObject().toString();
			
			if (!answer.equals("OK")) {

				/*
				 * In caso di ricezione errata da parte del Server dell'intero, viene stampato il messaggio ricevuto
				 * e terminato il programma
				 */
				System.out.println(answer);
				return;
			}

			/*
			 * La partenza dell'esplorazione dell'albero da parte dell'utente viene segnalata dal
			 * server dalla scrittura dell'intero 3 sulla socket. Le query da presentare all'utente
			 * sono fornite sotto forma di stringa da parte del server.
			 */
			char risp = 'y';
			do {

				out.writeObject(3);
				System.out.println("Starting prediction phase!");
				answer = in.readObject().toString();

				while (answer.equals("QUERY")){

					// La lettura della stringa "QUERY" indica la formulazione di una query all'utente
					answer = in.readObject().toString();
					System.out.println(answer);
					int path = Keyboard.readInt();
					out.writeObject(path);
					answer = in.readObject().toString();
				}
			
				/* 
				 * Alla lettura di "OK" si e' raggiunto un nodo foglia dell'albero, che presentera' la predizione
				 * sull'attributo target.
				 */
				if (answer.equals("OK")) {

					answer = in.readObject().toString();
					System.out.println("Predicted class:" + answer);
					
				} else {
	
					// In caso di mancato raggiungimento di un nodo foglia, viene stampato il messaggio inviato dal server
					System.out.println(answer);
				}

				// L'utente puo' ripetere l'esplorazione dell'albero
				System.out.println("Would you repeat ? (y/n)");
				risp = Keyboard.readChar();
			} while (Character.toUpperCase(risp) == 'Y');

			// Il client indica la sua chiusura al server scrivendo l'intero -1 sul socket
			out.writeObject(-1);
			
		} catch (IOException | ClassNotFoundException e){
			
			/*
			 * Nel processo di comunicazione con il Server, eventuali errori vengono catturati sotto forma di
			 * IOException. L'eccezione ClassNotFoundException viene catturata nel caso in cui viene letto un oggetto
			 * il cui classfile non e' stato caricato dal Client. In entrambi casi viene stampata la causa dell'eccezione tramite
			 * il suo metodo toString.
			 */
			System.out.println(e.toString());
		} finally {
			
			try {

				socket.close();
			} catch (IOException e) {

				/*
				 * Questa IOException viene catturata in caso di errore durante la comunicazione al Server
				 * della chiusura del Client, o in caso di errore nella chiusura del Socket con il Server.
				 */
				System.out.println("Error closing connection with the server: " + e.getClass().getName() + " : " + e.getMessage());
			}
		}
	}

}
