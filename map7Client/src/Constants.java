
/**
 * Classe contenente le costanti utilizzate nel client GUI.
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
class Constants {

	// Proprieta' della connessione di default 
	static final String DEFAULT_SERVER_IP = "127.0.0.1";
	static final int DEFAULT_SERVER_PORT = 8080;
	static final String DEFAULT_SERVER_ID = "(Default)";
	
	// Limiti numerici
	static final int MIN_IP_FIELD = 0;
	static final int MAX_IP_FIELD = 255;
	static final int MIN_PORT = 0;
	static final int MAX_PORT = 65535;
	
	// Titolo della finestra del programma
	static final String CLIENT_WINDOW_NAME = "Client";
	static final String HELP_WINDOW_NAME = "Aiuto";
	
	// Path per le risorse utilizzate nel programma
	static final String PATH_CLIENT_ICON = "file:res/icon.png";
	static final String PATH_GEAR_ICON = "file:res/gear.png";
	static final String PATH_HELP_ICON = "file:res/questionMark.png";
	static final String PATH_THEME = "file:src/theme.css";
	static final String PATH_SERVER_INFO = "servers.info";
	static final String PATH_WARNING_ICON = "file:res/warning.png";
	static final String PATH_ERROR_ICON = "file:res/error.png";

	// Contenuti dei pulsanti
	static final String BUTTON_OPTIONS = "Opzioni";
	static final String BUTTON_HELP = "Aiuto";
	static final String BUTTON_LOAD = "Carica";
	static final String BUTTON_CREATE = "Crea";
	static final String BUTTON_RESTART = "Ricomincia";
	static final String BUTTON_BACK = "Indietro";
	static final String BUTTON_CONFIRM = "Conferma";
	static final String BUTTON_ADD = "Aggiungi";
	static final String BUTTON_DELETE = "Elimina";
	
	// Contenuti delle label
	static final String LABEL_SELECTION = "Seleziona un'operazione";
	static final String LABEL_PREDICTION_QUERY = "Seleziona il valore dell'attributo:";
	static final String LABEL_TABLE_SELECTION = "Inserisci il nome della tabella";
	static final String LABEL_SERVER_IP_ADDRESS = "Indirizzo IP";
	static final String LABEL_SERVER_PORT = "Porta";
	static final String LABEL_SERVER_ID = "Server ID";
	static final String LABEL_PREDICTED_VALUE = "Valore predetto:\n";
	

	// Contenuti delle finestre di alert
	static final String ERROR_NO_COMMUNICATION = "Non e' stato possibile comunicare l'operazione al server selezionato.";
	static final String ERROR_CLOSING_COMMUNICATION = "Errore durante la chiusura della connessione al server";
	static final String ERROR_SERVER_UNREACHABLE = "Non e' stato possibile raggiungere il server";
	static final String ERROR_INIT_CONNECTION = "Errore durante la connessione al server";
	static final String ERROR_SAVING_TREE = "Errore nel salvataggio dei dati da parte del server";
	static final String ERROR_LOADING_SERVERS = "Errore durante il caricamento della lista dei server conosciuti.\n"
			+ "Verra' caricata una lista di default.";
	static final String ERROR_SAVING_SERVERS = "Errore durante la memorizzazione dei server conosciuti.";
	static final String ERROR_PARSING_IP = "L'indirizzo IP inserito non è valido.\n"
			+ "I valori che compongono l'indirizzo devono essere interi da 0 a 255.";
	static final String ERROR_PARSING_PORT = "Il numero di porta inserito non e' valido.\n"
			+ "Il numero di porta deve essere un intero fra 1 e 65535.";
	static final String ERROR_NO_SERVER_ID = "Il server deve avere un identificatore";
	static final String ERROR_ID_ALREADY_EXISTS = "Un server dal seguente ID e' gia' esistente: ";
	static final String ERROR_COMMUNICATING = "Errore di comunicazione con il Server";
	static final String ERROR_COMMUNICATING_BAD_ANSWER = "Errore di comunicazione con il Server: risposta erronea";
	static final String ERROR_COMMUNICATING_UNEXPECTED_ANSWER = "Errore di comunicazione con il Server: risposta inattesa";
	static final String ERROR_SENDING_VALUE = "Impossibile inviare la scelta selezionata al server (errore di comunicazione)";
	

	// CSS IDs
	static final String ID_SMALL_BUTTON = "smallButton";
	static final String ID_WELCOME_LABEL = "welcomeLabel";
	static final String ID_PREDICTION_LABEL = "predictionLabel";
	static final String ID_PREDICTION_BUTTON = "predictionButton";

	// Messaggi di comunicazione con il server
	static final int CLIENT_CREATE = 0;
	static final int CLIENT_LOAD = 2;
	static final int CLIENT_SAVE = 1;
	static final int CLIENT_END = -1;
	static final int CLIENT_PREDICT = 3;
	static final String CLIENT_ABORT = "#ABORT";
	static final String SERVER_OK = "OK";
	static final String SERVER_QUERY = "QUERY";


	// Contenuti di campi testuali
	static final String HELP_CONTENT_TEXT = "Programma per la creazione ed esplorazione di alberi di regressione.\n\n"
			+ "E' possibile creare un albero connettendosi ad un server adeguato contenente dei dataset.\n"
			+ "Nella schermata delle impostazioni e' possibile specificare il server a cui connettersi tramite indirizzo IP e porta.\n\n"
			+ "L'opzione \"crea\" permette di creare un nuovo albero di regressione a partire da un dataset memorizzato nel server.\n"
			+ "L'opzione \"carica\" permette di caricare un albero di regressione creato in precedenza dal server.\n\n"
			+ "Per selezionare il dataset, e' necessario inserire il nome della tabella in cui e' memorizzato nel Database del server.\n"
			+ "Una volta inserito il nome della tabella, si potra' esplorare l'albero tramite una serie di query a cui rispondere.\n\n"
			+ "Autori: Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea";
	
	static final String CONTENT_TEXT_NO_SERVERS_INFO = "Non e' stato trovato il file \"servers.info\" contenente le informazioni sui server conosciuti.\n"
			+ "Verra' caricata una lista di default.";
}
