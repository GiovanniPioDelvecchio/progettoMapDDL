package utility;

/**
 * Classe contenente le costanti utilizzate nel client GUI.
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
public class Constants {

	// Proprieta' della connessione di default 
	public static final String DEFAULT_SERVER_IP = "127.0.0.1";
	public static final int DEFAULT_SERVER_PORT = 8080;
	public static final String DEFAULT_SERVER_ID = "(Default)";
	
	// Limiti numerici
	public static final int MIN_IP_FIELD = 0;
	public static final int MAX_IP_FIELD = 255;
	public static final int MIN_PORT = 0;
	public static final int MAX_PORT = 65535;
	
	// Titolo della finestra del programma
	public static final String CLIENT_WINDOW_NAME = "Client";
	public static final String HELP_WINDOW_NAME = "Aiuto";
	
	// Path per le risorse utilizzate nel programma
	public static final String PATH_CLIENT_ICON = "file:res/icon.png";
	public static final String PATH_GEAR_ICON = "file:res/gear.png";
	public static final String PATH_HELP_ICON = "file:res/questionMark.png";
	public static final String PATH_THEME = "file:src/theme.css";
	public static final String PATH_SERVER_INFO = "servers.info";
	public static final String PATH_WARNING_ICON = "file:res/warning.png";
	public static final String PATH_ERROR_ICON = "file:res/error.png";

	// Contenuti dei pulsanti
	public static final String BUTTON_OPTIONS = "Opzioni";
	public static final String BUTTON_HELP = "Aiuto";
	public static final String BUTTON_LOAD = "Carica";
	public static final String BUTTON_CREATE = "Crea";
	public static final String BUTTON_RESTART = "Ricomincia";
	public static final String BUTTON_BACK = "Indietro";
	public static final String BUTTON_CONFIRM = "Conferma";
	public static final String BUTTON_ADD = "Aggiungi";
	public static final String BUTTON_DELETE = "Elimina";
	
	// Contenuti delle label
	public static final String LABEL_SELECTION = "Seleziona un'operazione";
	public static final String LABEL_PREDICTION_QUERY = "Seleziona il valore dell'attributo:";
	public static final String LABEL_TABLE_SELECTION = "Inserisci il nome della tabella";
	public static final String LABEL_SERVER_IP_ADDRESS = "Indirizzo IP";
	public static final String LABEL_SERVER_PORT = "Porta";
	public static final String LABEL_SERVER_ID = "Server ID";
	public static final String LABEL_SERVER_CURR = "Server selezionato: ";
	public static final String LABEL_PREDICTED_VALUE = "Valore predetto:\n";

	
	

	// Contenuti delle finestre di alert
	public static final String ERROR_NO_COMMUNICATION = "Non e' stato possibile comunicare l'operazione al server selezionato.";
	public static final String ERROR_CLOSING_COMMUNICATION = "Errore durante la chiusura della connessione al server";
	public static final String ERROR_SERVER_UNREACHABLE = "Non e' stato possibile raggiungere il server";
	public static final String ERROR_INIT_CONNECTION = "Errore durante la connessione al server";
	public static final String ERROR_SAVING_TREE = "Errore nel salvataggio dei dati da parte del server";
	public static final String ERROR_LOADING_SERVERS = "Errore durante il caricamento della lista dei server conosciuti.\n"
			+ "Verra' caricata una lista di default.";
	public static final String ERROR_SAVING_SERVERS = "Errore durante la memorizzazione dei server conosciuti.";
	public static final String ERROR_PARSING_IP = "L'indirizzo IP inserito non è valido.\n"
			+ "I valori che compongono l'indirizzo devono essere interi da 0 a 255.";
	public static final String ERROR_PARSING_PORT = "Il numero di porta inserito non e' valido.\n"
			+ "Il numero di porta deve essere un intero fra 1 e 65535.";
	public static final String ERROR_NO_SERVER_ID = "Il server deve avere un identificatore";
	public static final String ERROR_ID_ALREADY_EXISTS = "Un server dal seguente ID e' gia' esistente: ";
	public static final String ERROR_COMMUNICATING = "Errore di comunicazione con il Server";
	public static final String ERROR_COMMUNICATING_BAD_ANSWER = "Errore di comunicazione con il Server: risposta erronea";
	public static final String ERROR_COMMUNICATING_UNEXPECTED_ANSWER = "Errore di comunicazione con il Server: risposta inattesa";
	public static final String ERROR_SENDING_VALUE = "Impossibile inviare la scelta selezionata al server (errore di comunicazione)";
	

	// CSS IDs
	public static final String ID_SMALL_BUTTON = "smallButton";
	public static final String ID_WELCOME_LABEL = "welcomeLabel";
	public static final String ID_PREDICTION_LABEL = "predictionLabel";
	public static final String ID_PREDICTION_BUTTON = "predictionButton";
	public static final String ID_SERVER_LABEL = "serverLabel";

	// Messaggi di comunicazione con il server
	public static final int CLIENT_CREATE = 0;
	public static final int CLIENT_LOAD = 2;
	public static final int CLIENT_SAVE = 1;
	public static final int CLIENT_END = -1;
	public static final int CLIENT_PREDICT = 3;
	public static final String CLIENT_ABORT = "#ABORT";
	public static final String SERVER_OK = "OK";
	public static final String SERVER_QUERY = "QUERY";


	// Contenuti di campi testuali
	public static final String HELP_CONTENT_TEXT = "Programma per la creazione ed esplorazione di alberi di regressione.\n\n"
			+ "E' possibile creare un albero connettendosi ad un server adeguato contenente dei dataset.\n"
			+ "Nella schermata delle impostazioni e' possibile specificare il server a cui connettersi tramite indirizzo IP e porta.\n\n"
			+ "L'opzione \"crea\" permette di creare un nuovo albero di regressione a partire da un dataset memorizzato nel server.\n"
			+ "L'opzione \"carica\" permette di caricare un albero di regressione creato in precedenza dal server.\n\n"
			+ "Per selezionare il dataset, e' necessario inserire il nome della tabella in cui e' memorizzato nel Database del server.\n"
			+ "Una volta inserito il nome della tabella, si potra' esplorare l'albero tramite una serie di query a cui rispondere.\n\n"
			+ "Autori: Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea";
	
	public static final String CONTENT_TEXT_NO_SERVERS_INFO = "Non e' stato trovato il file \"servers.info\" contenente le informazioni sui server conosciuti.\n"
			+ "Verra' caricata una lista di default.";
}
