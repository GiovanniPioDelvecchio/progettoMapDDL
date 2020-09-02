import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import utility.Constants;

/**
 * Classe che si occupa dell'interfaccia grafica del lato Client.<br>
 * Tale classe sfrutta quanto messo a disposizione dalla libreria javafx, la quale e' <i>built-in</in> in Java 8.<br>
 * Inoltre, <code>AppMain</code> gestisce anche la comunicazione via socket e permette il salvataggio
 * della lista dei server con cui il programma comunica. 
 * 
 * @see javafx
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
public class AppMain extends Application {
	
	/**
	 * istanza della classe Socket con la quale viene instaurata la comunicazione col server
	 */
	private Socket clientSocket;
	
	/**
	 * Stream di output per la scrittura di oggetti verso il server.
	 */
	private ObjectOutputStream out;
	
	/**
	 * Stream di input per la lettura di oggetti provenienti dal server
	 */
	private ObjectInputStream in;

	/**
	 * ObservableList di ServerInformation contenente le informazioni sulle possibili connessioni ai server effettuabili.
	 * La lista viene inizializzata con un server di default, che rappresenta il localhost
	 */
	private ObservableList<ServerInformation> servers = FXCollections.observableArrayList(new ServerInformation(Constants.DEFAULT_SERVER_IP,
			Constants.DEFAULT_SERVER_PORT, Constants.DEFAULT_SERVER_ID));

	/**
	 * Istanza di ServerInformation contenente le informazioni, appunto, relative al
	 * server selezionato con il quale instaurare la comunicazione. 
	 * Di default viene inizializzato al localhost.
	 */
	private ServerInformation currServer = servers.get(0);

	/**
	 * Istanze della classe Scene da impostare secondo il loro scopo ("scena"-home, "scena" di selezione...)
	 */
	private Scene selectionScene, homeScene, settingsScene, newServerScene, predictScene;
	
	/**
	 * Flag per indicare se si è scelto di caricare un albero già salvato su server (true)
	 * o di crearne uno nuovo (false).
	 */
	private boolean loadFlag = false;

	/**
	 * Main dell'applicazione. Richiama il metodo Application.launch.
	 * 
	 * @param args Argomenti a riga di comando con cui viene lanciata l'applicazione.
	 * 			   Tali argomenti sono poi passati in input al metodo launch.
	 */
	public static void main(String[] args) {
		
		launch(args);
	}
	
	/**
	 * Sovrascrittura del metodo <code>start</code> di <code>Application</code>.
	 * Tale metodo rappresenta il principale dell'applicazione che viene eseguito all'inizio del programma e
	 * il cui scopo e' di definire il comportamento delle varie componenti interattive (pulstanti, barre,
	 * finestre di dialogo...) e il loro aspetto.
	 * 
	 * @param mainStage riferimento alla finestra principale dell'applicazione.
	 * 
	 * @see Application
	 * 
	 */
	@Override
	public void start(Stage mainStage) {
		
		/** HOME **/
		
		BorderPane homePane = new BorderPane();

		/*
		 * descrizione componenti per la barra degli strumenti superiore (pulsanti e immagini su di essi)
		 */

		ToolBar tools = new ToolBar();
		
		Image gear = new Image(Constants.PATH_GEAR_ICON, 20, 20, true, true);
		ImageView gearV = new ImageView(gear);
		
		Image questionMark = new Image(Constants.PATH_HELP_ICON, 20, 20, true, true);
		ImageView questionMarkV = new ImageView(questionMark);
		
		Button opt = new Button(Constants.BUTTON_OPTIONS);
		Button help = new Button(Constants.BUTTON_HELP);
		
		/**
		 * Per i pulsanti vengono specificati degli ID a cui corrispondono
		 * specifiche caratterizzazioni grafiche, differenti da quelle specificate 
		 * per i pulsanti standard
		 */
		opt.setId(Constants.ID_SMALL_BUTTON);
		help.setId(Constants.ID_SMALL_BUTTON);
		
		opt.setGraphic(gearV);
		help.setGraphic(questionMarkV);
		opt.setOnAction(e -> mainStage.setScene(settingsScene));
		tools.getItems().addAll(opt, help);
		homePane.setTop(tools);		
		
		/*
		 * Descrizione componenti per la vbox (pannello a distribuzione verticale) centrale
		 * Presenta una etichetta esplicativa a cui seguono due pulsanti per caricare o creare
		 * un albero di regressione dal server.
		 */

		Label sel = new Label(Constants.LABEL_SELECTION);
		sel.setId(Constants.ID_WELCOME_LABEL);
		
		Button load = new Button(Constants.BUTTON_LOAD);
		load.setMinSize(130, 20);
		
		Button create = new Button(Constants.BUTTON_CREATE);
		create.setMinSize(130, 20);
		
		VBox centralPanel = new VBox(50);
		centralPanel.setAlignment(Pos.CENTER);
		centralPanel.getChildren().addAll(sel, load,create);
		homePane.setCenter(centralPanel);

		
		
		/*
		 * Se il tasto "Carica" viene premuto, ci si connette al server e si comunica di caricare
		 * dal file l'albero corrispondente al nome del file scelto
		 */
		
		load.setOnAction(e -> {

			try {

				loadFlag = true;
				connectToServer();
				out.writeObject(Constants.CLIENT_LOAD);
				mainStage.setScene(selectionScene);
			} catch (IOException | NullPointerException e1) {
				
				/*
				 * Se qualcosa va storto con l'invio dei messaggi al server si mostra un alert
				 */
				showAlert(Constants.ERROR_NO_COMMUNICATION, Alert.AlertType.ERROR);
			}
		
		});
		
		/*
		 * Se il tasto "Crea" viene premuto, ci si connette al server e si comunica di caricare
		 * da un database la tabella da cui verra' ricavato l'albero di regressione
		 */
		create.setOnAction(e -> {

			try {

				loadFlag = false;
				connectToServer();
				out.writeObject(Constants.CLIENT_CREATE);
				mainStage.setScene(selectionScene);
			} catch(IOException | NullPointerException e1) {
				
				/*
				 * Se qualcosa va storto con l'invio dei messaggi al server si mostra un alert
				 */
				showAlert(Constants.ERROR_NO_COMMUNICATION, Alert.AlertType.ERROR);
			}
		});
		
		
		
		/** HELP **/
		
		/**
		 * Alla pressione del tasto help viene mostrata una finestra di dialogo con informazioni
		 * relative all'applicazione
		 */
		help.setOnAction(e -> {
			/*
			 * Non si utilizza il metodo showAlert, poichè risulta essere necessario
			 * modificare il campo Title e Header per la finestra che andiamo a mostrare
			 */
			Alert helpScreen = new Alert(Alert.AlertType.INFORMATION);
			helpScreen.setHeaderText(Constants.HELP_WINDOW_NAME);
			helpScreen.setTitle(Constants.HELP_WINDOW_NAME);
			helpScreen.setContentText(Constants.HELP_CONTENT_TEXT);
			helpScreen.getDialogPane().getStylesheets().add(Constants.PATH_THEME);
			((Stage) helpScreen.getDialogPane().getScene().getWindow()).getIcons().add(new Image(Constants.PATH_CLIENT_ICON));
			helpScreen.show();
		});
		
		
		
		/** FINESTRA DI PREDIZIONE **/
		
		/*	
		 * La definizione della finestra di predizione si trova in maniera piuttosto controintuitiva prima
		 * della definizione della finestra di selezione poiche' quest'ultima richiama elementi della prima.
		 * Ad ogni modo, questa inversione di ordine non inficia il funzionamento del programma.
		 */
		
		// Definizione del layout generale della finestra
		BorderPane predictPane = new BorderPane();
		
		// Definizione del layout della porzione centrale del pannello precedente
		VBox predictBox = new VBox(50);
		predictBox.setAlignment(Pos.CENTER);
		
		// Definizione del layout dove mostrare i pulsanti per la predizione
		TilePane userChoices = new TilePane();
		userChoices.setAlignment(Pos.CENTER);
		userChoices.setHgap(5d);
		userChoices.setVgap(5d);
		userChoices.setPrefColumns(3);
		
		/** 
		 * Etichetta per mostrare il risultato della predizione
		 * Quando non si è ancora giunti alla predizione, mostra un'istruzione
		 */
		Label predictedValue = new Label(Constants.LABEL_PREDICTION_QUERY);
		predictedValue.setAlignment(Pos.CENTER);
		predictedValue.setId(Constants.ID_PREDICTION_LABEL);
		
		// Definizione del layout dei tasti "Ricomincia" e "Indietro"
		HBox predictButtons = new HBox(50);
		predictButtons.setAlignment(Pos.CENTER);

		// Definizione del tasto per ricominciare la predizione
		Button redo = new Button(Constants.BUTTON_RESTART);
		redo.setDisable(true);
		redo.setOnAction(e->{
			try {
				/**
				 * All'atto di ricominciare si comunica al server che si vuole ricominciare la predizione
				 * si resetta l'etichetta, si disattiva il tasto redo e si ricomincia 
				 */
				out.writeObject(Constants.CLIENT_PREDICT);
				predictedValue.setText(Constants.LABEL_PREDICTION_QUERY);
				redo.setDisable(true);
				handlePredict(userChoices, predictedValue, redo);
			} catch (IOException e1) {
				/**
				 * in caso di errore durante la comunicazione, si mostra una finestra di dialogo, 
				 * si tenta di chiudere la cominicazione (se non e' possibile si mostra un'altra finestra)
				 * e si torna alla home.
				 */
				showAlert(Constants.ERROR_SERVER_UNREACHABLE, Alert.AlertType.ERROR);
				if (!clientSocket.isClosed()) {
					try {
						
						clientSocket.close();
					} catch (IOException e2) {
						
						showAlert(Constants.ERROR_CLOSING_COMMUNICATION, Alert.AlertType.ERROR);
					}
				}
				
				mainStage.setScene(homeScene);
			}
			
		});
		
		// Definizione del tasto per tornare alla home
		Button backPredict = new Button(Constants.BUTTON_BACK);
		backPredict.setOnAction(e -> { 
			try {
					// Si notifica che si sta tornando alla home e si tenta di chiudere la connessione
					out.writeObject(Constants.CLIENT_END);
					clientSocket.close();
				} catch (IOException e1) {
					
					// in caso di errore durante la chiusura, si mostra una finestra di dialogo
					showAlert(Constants.ERROR_CLOSING_COMMUNICATION, Alert.AlertType.ERROR);
				}
			
				/**
				 * Inoltre si ripuliscono i pulsanti di predizione, si reinizializza la label e si
				 * disattiva il tasto redo.
				 */
				userChoices.getChildren().clear();
				predictedValue.setText(Constants.LABEL_PREDICTION_QUERY);
				redo.setDisable(true);
				mainStage.setScene(homeScene);
		});
		
		// Si aggiungono al layout i vari elementi definiti
		predictButtons.getChildren().add(redo);
		predictButtons.getChildren().add(backPredict);
		
		predictBox.getChildren().add(predictedValue);
		predictBox.getChildren().add(userChoices);
		predictBox.getChildren().add(predictButtons);

		predictPane.setCenter(predictBox);
		


		/** INSERIMENTO TABELLA **/

		// Si crea un layout a griglia per l'inserimento del nome della tabella
		GridPane selectionPane = new GridPane();
		selectionPane.setAlignment(Pos.CENTER);
		selectionPane.setHgap(40);
		selectionPane.setVgap(30);
		selectionPane.setPadding(new Insets(30,30,30,30));
		
		// Sulla prima riga, per due colonne, si estende il label di richiesta.
		Label selLabel = new Label(Constants.LABEL_TABLE_SELECTION);
		selectionPane.add(selLabel, 0, 0, 2, 1);
		
		// Sulla seconda riga, per due colonne, si estende il campo per inserire il testo contenente il nome della tabella
		TextField tableName = new TextField();
		selectionPane.add(tableName, 0, 1, 2, 1);
		
		// Si inserisce un pulsante di conferma che rimane disattivato se non viene inserito del testo
		Button confirm = new Button(Constants.BUTTON_CONFIRM);
		confirm.setDisable(true);
		confirm.setOnAction(e -> {

			try {

				// Alla pressione si tenta di mandare il nome della tabella al server
				out.writeObject(tableName.getText());

				String ans = (String) in.readObject();
				
				if (!ans.equals(Constants.SERVER_OK)) {
					// Se la stringa genera errore, viene mostrato a schermo
					showAlert(ans, Alert.AlertType.ERROR);
				} else {
					
					if (loadFlag == false) {
						
						// In caso di creazione di un albero si richiede automaticamente il suo salvataggio
						out.writeObject(Constants.CLIENT_SAVE);
						ans = ((String) in.readObject());
						/**
						 * Se il salvataggio dell'albero non va a buon fine, si permette comunque
						 * di accedere alla predizione
						 */
						if (!ans.equals(Constants.SERVER_OK)) {
						
							showAlert(Constants.ERROR_SAVING_TREE, Alert.AlertType.WARNING);
						}
					}
					
					/**
					 * Infine si passa alla finestra di predizione, si comunica al server di 
					 * cominciare la predizione e si inizia.
					 */
					mainStage.setScene(predictScene);
					
					out.writeObject(Constants.CLIENT_PREDICT);
					
					handlePredict(userChoices, predictedValue, redo);


				}
			} catch (ClassNotFoundException | IOException e1) {
				
				/* 
				 * In caso di errore di comunicazione si mostra una finestra di dialogo,
				 * si tenta di chiudere la comunicazione e si torna alla home
				 */
				showAlert(Constants.ERROR_INIT_CONNECTION, Alert.AlertType.ERROR);
				if (!clientSocket.isClosed()) {
					try {
						
						clientSocket.close();
					} catch (IOException e2) {
						
						showAlert(Constants.ERROR_CLOSING_COMMUNICATION, Alert.AlertType.ERROR);
					}
				}
				mainStage.setScene(homeScene);
			} 
		});
		selectionPane.add(confirm, 0, 2);
		
		// si crea anche un bottone per tornare indietro alla home

		Button backSelection = new Button(Constants.BUTTON_BACK);
		backSelection.setOnAction(e -> {	
			
		try {
				/*
				 * Si notifica che si sta tornando alla home.
				 * La stringa con cui lo si comunica deve essere composta in modo da non poter
				 * rappresentare una tabella che possa essere scelta.
				 */
				out.writeObject(Constants.CLIENT_ABORT);
				clientSocket.close();

				} catch (IOException e1) {
					
					showAlert(Constants.ERROR_CLOSING_COMMUNICATION, Alert.AlertType.ERROR);

				}
		
			mainStage.setScene(homeScene);
			
		});
		
		selectionPane.add(backSelection, 1, 2);

		


		// Si imposta il campo testuale in maniera che se risulta essere vuoto, il tasto di conferma viene disabilitato
		tableName.setOnKeyReleased(e -> {

			if (tableName.getText().equals("")) {
				
				confirm.setDisable(true);
			} else {
				
				confirm.setDisable(false);
				if (e.getCode().equals(KeyCode.ENTER)) {
					
					confirm.getOnAction().handle(new ActionEvent());
				}
			}
		});

		
		
		
		
		
		/** FINESTRA DELLE IMPOSTAZIONI **/
		
		/* Logica di serializzazione dei server conosciuti */
		
		/*
		 * Caricamento delle informazioni sui server precedentemente serializzate.
		 * In caso di caricamento non andato a buon fine, viene lasciata la lista dei server
		 * di default.
		 */
		try {

			FileInputStream serversInFile = new FileInputStream(Constants.PATH_SERVER_INFO);
			ObjectInputStream serversIn = new ObjectInputStream(serversInFile);

			/*
			 * La lista letta dal file e' una lista di MutableServerInformation, ovvero informazioni sui server
			 * modificabili. Dopo la lettura della lista, i suoi contenuti vengono inseriti nell'attributo
			 * servers, sotto forma di ServerInformation (quindi oggetti read-only).
			 */
			@SuppressWarnings("unchecked")
			// Il compilatore solleva un warning sul cast non sicuro (a causa dell'erasure).
			// Viene ignorato poiche' si e' certi di cio' che e' stato memorizzato.
			ArrayList<MutableServerInformation> serializedServerList = (ArrayList<MutableServerInformation>) serversIn.readObject();

			servers.clear();

			for (MutableServerInformation s : serializedServerList) {
				
				servers.add(s.toServerInformation());
			}

			serversIn.close();
			serversInFile.close();
		} catch (IOException | ClassNotFoundException e1) {
			/**
			 * In caso di errore durante il caricamento della lista server vengono
			 * mostrate delle finestre di dialogo contestuali e si mantiene la lista di default
			 */
			if (e1 instanceof FileNotFoundException) {
				
				showAlert(Constants.CONTENT_TEXT_NO_SERVERS_INFO, Alert.AlertType.WARNING);
			} else {

				showAlert(Constants.ERROR_LOADING_SERVERS, Alert.AlertType.ERROR);
			}
		}
		
		
		
		/* Definizione della schermata delle impostazioni */
		
		BorderPane serversPane = new BorderPane();

		/* 
		 * Viene dichiarata un TableView per la visione dei server conosciuti, si utilizzano i dati
		 * dell'attributo servers per tenere aggiornata la tabella.
		 */
		TableView<ServerInformation> serverTable = new TableView<ServerInformation>(servers);
		
		/*
		 * Vengono dichiarate le colonne che compongono la tabella. Oltre ad assegnare il nome della tabella,
		 * viene associata ogni colonna all'attributo Property relativo in ServerInformation, tramite il metodo
		 * setCellValueFactory.
		 */
		TableColumn<ServerInformation, String> idCol = new TableColumn<ServerInformation, String>(Constants.COLUMN_ID);
		idCol.setCellValueFactory(new PropertyValueFactory<ServerInformation, String>("id"));
		
		TableColumn<ServerInformation, String> ipCol = new TableColumn<ServerInformation, String>(Constants.COLUMN_IP_ADDRESS);
		ipCol.setCellValueFactory(new PropertyValueFactory<ServerInformation, String>("ip"));
		
		TableColumn<ServerInformation, Integer> portCol = new TableColumn<ServerInformation, Integer>(Constants.COLUMN_PORT);
		portCol.setCellValueFactory(new PropertyValueFactory<ServerInformation, Integer>("port"));
		
		// Infine vengono aggiunte le tabelle appena create alla TableView serverTable.
		serverTable.getColumns().add(idCol);
		serverTable.getColumns().add(ipCol);
		serverTable.getColumns().add(portCol);

		serverTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		/*
		 * Viene creato un oggetto TableViewFocusModel per poter gestire l'interazione fra l'utente e la tabella.
		 * L'oggetto viene associato alla tabella serverTable.
		 */
		TableView.TableViewFocusModel<ServerInformation> userFocus = new TableView.TableViewFocusModel<ServerInformation>(serverTable);
		serverTable.setFocusModel(userFocus);
		
		/*
		 * Infine vengono dichiarati i pulsanti per interagire con gli elementi della tabella.
		 */
		HBox settingsButtonsLayout = new HBox();
		settingsButtonsLayout.setAlignment(Pos.CENTER);
		settingsButtonsLayout.setPadding(new Insets(25, 25, 25, 25));
		settingsButtonsLayout.setSpacing(5d);

		Button addServer = new Button(Constants.BUTTON_ADD);
		Button removeServer = new Button(Constants.BUTTON_DELETE);
		Button confirmServer = new Button(Constants.BUTTON_CONFIRM);
		Button backSettings = new Button(Constants.BUTTON_BACK);
		addServer.setId(Constants.ID_SMALL_BUTTON);
		removeServer.setId(Constants.ID_SMALL_BUTTON);
		confirmServer.setId(Constants.ID_SMALL_BUTTON);
		backSettings.setId(Constants.ID_SMALL_BUTTON);
		
		Label currentServerInfo = new Label(Constants.LABEL_SERVER_CURR + currServer.getId());
		currentServerInfo.setId(Constants.ID_SERVER_LABEL);
		
		// Si definiscono i comportamenti dei tasti
		backSettings.setOnAction(e -> {
			
			mainStage.setScene(homeScene);
		});
		
		addServer.setOnAction(e -> {
			
			mainStage.setScene(newServerScene);
		});
		
		removeServer.setOnAction(e -> {
			
			servers.remove(userFocus.getFocusedItem());
			if (servers.size() == 0) {
				
				//Se la tabella viene svuotata, il tasto conferma si disattiva
				confirmServer.setDisable(true);
			}
		});
		
		settingsButtonsLayout.getChildren().addAll(addServer, removeServer, confirmServer, backSettings);

		serversPane.setTop(currentServerInfo);
		serversPane.setCenter(serverTable);
		serversPane.setBottom(settingsButtonsLayout);
		
		/** AGGIUNTA NUOVO SERVER **/
		
		/**
		 * Si crea una finestra con layout a griglia per l'inserimento delle informazioni
		 * relative ad un nuovo server con cui tentare la connessione
		 */
		GridPane newServerPane = new GridPane();
		newServerPane.setAlignment(Pos.CENTER);
		newServerPane.setHgap(10);
		newServerPane.setVgap(10);
		newServerPane.setPadding(new Insets(25, 50, 25, 25));
		
		Label ipLabel = new Label(Constants.LABEL_SERVER_IP_ADDRESS);
		Label portLabel = new Label(Constants.LABEL_SERVER_PORT);
		Label idLabel = new Label(Constants.LABEL_SERVER_ID);
		
		Button backNewServer = new Button(Constants.BUTTON_BACK);
		backNewServer.setOnAction(e->mainStage.setScene(settingsScene));
		
		
		/*
		 * Per limitare il margine di errore nell'inserimento dell'indirizzo IP,
		 * vengono utilizzati quattro TextField, ognuno rappresentante una porzione
		 * dell'indirizzo.
		 */
		HBox ipLayout = new HBox();
		ipLayout.setAlignment(Pos.BOTTOM_CENTER);
		TextField ipAdd[] = {
				
				new TextField(),
				new TextField(),
				new TextField(),
				new TextField()
		};
		
		/*
		 * Si imposta a 3 il numero di cifre inseribili in ogni textfield dell'indirizzo ip.
		 * Vengono aggiunti dei punti per migliorare la resa grafica dell'indirizzo.
		 * Ogni TextField viene aggiunto al layout.
		 * 
		 * Per ogni TextField viene impostata una larghezza massima di 35.
		 */
		for (int i = 0; i < 3; i++) {

			ipAdd[i].setMaxWidth(35d);
			ipLayout.getChildren().add(ipAdd[i]);
			ipLayout.getChildren().add(new Label("."));
		}
		ipAdd[3].setMaxWidth(35d);
		ipLayout.getChildren().add(ipAdd[3]);
		
		/*
		 * Come per l'indirizzo IP, la porta sara' inserita tramite un TextField.
		 * La larghezza massima del campo testuale e' 45.
		 */
		TextField portField = new TextField();
		portField.setMaxWidth(45d);
		
		TextField idField = new TextField();

		/*
		 * Con questa chiamata a funzione si aggiornano i testi di prompt dei campi testuali
		 * con i valori correnti dell'indirizzo ip e della porta.
		 */
		updateSettingsPromptText(ipAdd, portField, idField);

		/*
		 * Infine si utilizzano due pulsanti, uno di conferma e uno per tornare alla finestra di selezione
		 * del server.
		 */
		Button confirmButtonSettings = new Button(Constants.BUTTON_CONFIRM);
		HBox backLayoutSettings = new HBox();
		backLayoutSettings.setAlignment(Pos.CENTER_LEFT);
		backLayoutSettings.getChildren().add(backNewServer);

		/*
		 * Si dichiara un oggetto di tipo EventHandler<ActionEvent>, il cui metodo handle descrive il comportamento
		 * di conferma alla pressione del pulsante confirmButtonSettings (o alla pressione del tasto enter all'interno
		 * di un qualsiasi TextField della schermata).
		 * Si e' scelta un oggetto di classe anonima al posto di una lambda-espressione poiche' il corpo
		 * della funzione handle è molto esteso ed è pertanto più facilmente leggibile.
		 */
		EventHandler<ActionEvent> confirmEvent = new EventHandler<ActionEvent>() {

			public void handle(ActionEvent e) {
				
				/*
				 * Viene usato uno StringBuffer per la concatenzione del nuovo indirizzo IP per evitare
				 * la creazione ripetuta di nuovi oggetti String.
				 */
				StringBuffer newIp = null;
				int intPort = -1;

				/*
				 * Si controlla se l'IP inserito e' un indirizzo IP valido.
				 * Se l'indirizzo IP inserito e' mal formattato, viene visualizzato un errore, e non viene
				 * inserito il nuovo server.
				 */
				boolean isValid = true;
				for (TextField i : ipAdd) {
					
					int readInteger = -1;
					try {
	
						readInteger = Integer.parseInt(i.getText());
					} catch (NumberFormatException err) {
						
						isValid = false;
						break;
					}
					if (readInteger < Constants.MIN_IP_FIELD || readInteger > Constants.MAX_IP_FIELD) {
						
						isValid = false;
						break;
					}
				}
				if (isValid) {
	
					newIp = new StringBuffer("");
					for (int i = 0; i < 3; i++) {
						
						newIp.append(ipAdd[i].getText());
						newIp.append(".");
					}
					newIp.append(ipAdd[3].getText());
				} else {

					showAlert(Constants.ERROR_PARSING_IP, Alert.AlertType.ERROR);
					return;
				}
				
				/*
				 * Si parsifica il numero di porta inserito nel field associato. Se il numero di porta
				 * non e' valido, viene visualizzato un errore.
				 */
				String readPort = portField.getText();
				
				if (!readPort.equals("")) {

					try {

						intPort = Integer.parseInt(readPort);
						if (!(intPort > Constants.MIN_PORT && intPort <= Constants.MAX_PORT)) {

							showAlert(Constants.ERROR_PARSING_PORT, Alert.AlertType.ERROR);
							return;
						}
					} catch (NumberFormatException f) {

						showAlert(Constants.ERROR_PARSING_PORT, Alert.AlertType.ERROR);
						return;
					}
				}
				
				String readId = idField.getText();
				
				/*
				 * L'ID dovra' essere un campo compilato, e non devono essere gia' presenti in memoria
				 * server dallo stesso identificatore.
				 */
				if (readId.equals("")) {
					
					showAlert(Constants.ERROR_NO_SERVER_ID, Alert.AlertType.ERROR);
					return;
				}

				ServerInformation toAdd = new ServerInformation(newIp.toString(), intPort, readId);
				if (servers.contains(toAdd)) {

					showAlert(Constants.ERROR_ID_ALREADY_EXISTS + readId, Alert.AlertType.ERROR);
					return;
				}
				
				servers.add(toAdd);
				updateSettingsPromptText(ipAdd, portField, idField);
				
				// Si sblocca il pulsante per confermare l'aggiunta di un server
				confirmServer.setDisable(false);
				mainStage.setScene(settingsScene);
			}
		};

		/*
		 * Si imposta l'EventHandler confirmEvent come comportamento da assumere in caso di pressione di confirmButtonSettings
		 * o di Enter in qualsiasi campo testuale.
		 */
		confirmButtonSettings.setOnAction(confirmEvent);
		
		for (TextField i : ipAdd) {
			
			i.setOnKeyReleased(e -> {

				if (e.getCode().equals(KeyCode.ENTER)) {
					
					confirmEvent.handle(new ActionEvent());
				}
			});
		}
		
		portField.setOnKeyReleased(e -> {

			if (e.getCode().equals(KeyCode.ENTER)) {

				confirmEvent.handle(new ActionEvent());
			}
		});

		/*
		 * Questo bottone appartiene alla schermata di visione dei server, ma la dichiarazione del comportamento
		 * e' effettuata qua, poiche' deve richiamare updateSettingsPromptText con i parametri
		 * precedentemente dichiarati
		 */
		confirmServer.setOnAction(e -> {

			currServer = userFocus.getFocusedItem();
			currentServerInfo.setText(Constants.LABEL_SERVER_CURR + currServer.getId());
			updateSettingsPromptText(ipAdd, portField, idField);
			mainStage.setScene(homeScene);
		});
		
		/*
		 * Si aggiungono i nodi alla griglia di layout.
		 */
		newServerPane.add(idLabel, 1, 1);
		newServerPane.add(idField, 2, 1);
		newServerPane.add(ipLabel, 1, 2);
		newServerPane.add(ipLayout, 2, 2);
		newServerPane.add(portLabel, 1, 3);
		newServerPane.add(portField, 2, 3);
		newServerPane.add(confirmButtonSettings, 1, 4);
		newServerPane.add(backLayoutSettings, 2, 4);

		/**
		 * Inizializzazione delle scene con i relativi pannelli-layout principali
		 */
		homeScene = new Scene(homePane, 400, 400);
		selectionScene = new Scene(selectionPane, 400, 400);
		settingsScene = new Scene(serversPane, 400, 400);
		newServerScene = new Scene(newServerPane, 400,400);
		predictScene = new Scene(predictPane, 400, 400);

		/**
		 * Aggiunta per ciascuna scena dello stile visivo da adottare, descritto da un
		 * file di tipo .css
		 */
		homeScene.getStylesheets().add(Constants.PATH_THEME);
		selectionScene.getStylesheets().add(Constants.PATH_THEME);
		settingsScene.getStylesheets().add(Constants.PATH_THEME);
		newServerScene.getStylesheets().add(Constants.PATH_THEME);
		predictScene.getStylesheets().add(Constants.PATH_THEME);

		// Specifica della barra del titolo e del layout della schermata di home
		mainStage.setTitle(Constants.CLIENT_WINDOW_NAME);
		mainStage.getIcons().add(new Image(Constants.PATH_CLIENT_ICON));
		
		// Specifica del comportamento da assumere alla chiusura della finestra principale.
		mainStage.setOnCloseRequest(e -> {
			
			/** 
			 * Se e' in corso una comunicazione col server, si notifica la chiusura e 
			 * si tenta l'interruzione delle comunicazioni
			 */
			if (clientSocket != null) {
				if (!clientSocket.isClosed()) {
					try {
						
						/** 
						 * Si verifica in quale finestra ci si trova per 
						 * identificare il tipo di messaggio di chiusura da mandare
						 */
						if (mainStage.getScene().equals(selectionScene)) {
							
							out.writeObject(Constants.CLIENT_ABORT);
						} else {
							
							out.writeObject(Constants.CLIENT_END);
						}
						
						clientSocket.close();
					} catch (IOException e1) {
	
						showAlert(Constants.ERROR_CLOSING_COMMUNICATION, Alert.AlertType.ERROR);
					}
				
				}
			}
			try {
				/*
				 * Viene impostata la serializzazione della lista di server conosciuti alla chiusura
				 * del programma.
				 */
				FileOutputStream serverOutFile = new FileOutputStream(Constants.PATH_SERVER_INFO);
				ObjectOutputStream serverOut = new ObjectOutputStream(serverOutFile);

				/*
				 * Al posto di servers, viene serializzato un ArrayList di MutableServerInformation, ovvero la controparte
				 * mutabile di ServerInformation. Questa scelta e' stata fatta poiche' gli attributi di ServerInformation
				 * non sono serializzabili.
				 */
				ArrayList<MutableServerInformation> toSerialize = new ArrayList<MutableServerInformation>(servers.size());
				for (ServerInformation s : servers) {
					
					toSerialize.add(s.toMutableServerInformation());
				}
				serverOut.writeObject(toSerialize);

				serverOut.close();
				serverOutFile.close();
			} catch (IOException e1) {

				showAlert(Constants.ERROR_SAVING_SERVERS, Alert.AlertType.ERROR);
			}
		});
		
		/*
		 * Chiamata ai metodi per mostrare la scena principale
		 */
		mainStage.setScene(homeScene);
		mainStage.show();
	}

	/** 
	 * Metodo necessario per connettersi al server
	 * 
	 * @throws IOException lanciata nel caso in cui ci siano errori nella costruzione
	 * degli streams
	 */
	private void connectToServer() throws IOException {
		
			clientSocket = new Socket(currServer.getIp(), currServer.getPort());
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			in = new ObjectInputStream(clientSocket.getInputStream());
	}
	
	/** 
	 * Metodo con cui costruire e mostrare una generica finestra di dialogo di tipo Alert.
	 * Tale finestra vedrà specificato un messaggio da mostrare, <code>message</code>, e un
	 * tipo preimpostato tra quelli resi disponibili dall'enumerazione <code>Alert.AlertType</code>
	 * 
	 * @param message <code>String</code> da mostrare nella finestra di dialogo 
	 * @param type parametro di tipo <code>AlertType</code> per indicare il tipo di finestra di dialogo da mostrare
	 * 
	 * @see Alert
	 */
	private void showAlert(String message, Alert.AlertType type) {
		
		Alert toShow = new Alert(type);
		toShow.setContentText(message);
		toShow.getDialogPane().getStylesheets().add(Constants.PATH_THEME);
		
		// In base al tipo, viene mostrata una diversa icona
		if (type.equals(Alert.AlertType.ERROR)) {
			
			((Stage) toShow.getDialogPane().getScene().getWindow()).getIcons().add(new Image(Constants.PATH_ERROR_ICON));
		} else if  (type.equals(Alert.AlertType.WARNING)) {
			
			((Stage) toShow.getDialogPane().getScene().getWindow()).getIcons().add(new Image(Constants.PATH_WARNING_ICON));
		} else {
			
			((Stage) toShow.getDialogPane().getScene().getWindow()).getIcons().add(new Image(Constants.PATH_CLIENT_ICON));
		}
		toShow.show();
	}
	
	/**
	 * Metodo necessario per poter gestire la predizione attraverso il server
	 * 
	 * @param userChoices <code>TilePane</code> su cui inserire i tasti, corrispondenti alle possibili scelte 
	 * 		  (quando, esplorando l'albero di regressione, si arriva ad un nodo di split)
	 * @param predictedValue <code>Label</code> su cui scrivere il risultato della predizione
	 * @param redo <code>Button</code> necessario per poter effettuare una nuova predizione una volta finita
	 * 		  quella precedente. E' necessario avere questo parametro per poter gestire quando e' attivo e quando no.
	 * 
	 */
	private void handlePredict(TilePane userChoices, Label predictedValue, Button redo) {
		
		try {
			
			String toCheck;
			
			toCheck = (String)in.readObject();
			
			if (toCheck.equals(Constants.SERVER_QUERY)) {

				// In caso di nodo di split vengono generati i pulsanti per la selezione
				@SuppressWarnings("unchecked")
				// Il compilatore solleva un warning sul cast non sicuro che puo' esser ignorato
				// in quanto si e' sicuri di ricevere dal server (nella versione apposita) un arrayList di stringhe
				List<String> options = new ArrayList<String>((ArrayList<String>) in.readObject());
				int i = 0;
				for (String elem : options) {
					
					addSplitButton(userChoices, elem, i, predictedValue, redo);
					i++;
				}
				
			} else {
				
				// Altrimenti viene mostrato il valore predetto e si abilita il tasto per ricominciare
				predictedValue.setText(Constants.LABEL_PREDICTED_VALUE + ((Double)in.readObject()).toString());
				redo.setDisable(false);
				
				
			}
		} catch (ClassNotFoundException e) {
			
			showAlert(Constants.ERROR_COMMUNICATING_UNEXPECTED_ANSWER, Alert.AlertType.ERROR);
		} catch (IOException e) {

			showAlert(Constants.ERROR_COMMUNICATING, Alert.AlertType.ERROR);
		} catch (ClassCastException e) {
			
			showAlert(Constants.ERROR_COMMUNICATING_BAD_ANSWER, Alert.AlertType.ERROR);
		}
	}
	
	/** 
	 * Metodo necessario per aggiungere dei pulsanti alla finestra di predizione. 
	 * Tali pulsanti serviranno a scegliere le alternative possibili durante la predizione.
	 * Dato che viene descritta la caratteristica di ogni tasto, sono necessari anche i parametri 
	 * di handlePredict, che viene richiamata ogni volta che viene premuto un tasto.
	 * 
	 * @param userChoices <code>TilePane</code> in cui vanno inseriti i tasti.
	 * @param toInsert <code>Stringa</code> contenente l'etichetta del nuovo tasto da aggiungere.
	 * @param toSend <code>Integer</code> da inviare al server, corrispondente alla scelta desiderata.
	 * @param predictedValue riferimento al <code>Label</code> su cui scrivere il risultato (necessario per richiamare handlePredict).
	 * @param redo riferimento al <code>Button</code> per ricominciare la predizione (necessario per richiamare handlePredict).
	 */
	private void addSplitButton(TilePane userChoices, String toInsert, Integer toSend, Label predictedValue, Button redo) {
		
		Button toShow = new Button(toInsert);
		toShow.setOnAction(e->{
			
			try {
				
				out.writeObject(toSend);
				userChoices.getChildren().clear();
				handlePredict(userChoices, predictedValue, redo);
				
			} catch (IOException e1) {
				
				showAlert(Constants.ERROR_SENDING_VALUE, Alert.AlertType.ERROR);
			}
		});
		toShow.setId(Constants.ID_PREDICTION_BUTTON);
		userChoices.getChildren().add(toShow);
	}
	
	/**
	 * Metodo utilizzato per tenere aggiornati le stringhe di prompt nei campi
	 * dove inserire indirizzo IP, Id e Porta del server a cui collegarsi.
	 * Le stringhe utilizzate come testo di prompt sono i valori correnti dell'IP, dell'ID
	 * e della Porta. Il metodo, inoltre, esegue il <i>flush</i> dei campi.
	 * 
	 * @param ipAdd Array di <code>TextField</code> che compongono un indirizzo IP.
	 * @param portField <code>TextField</code> dove puo' essere inserito il numero di porta.
	 * @param idField <code>TextField</code>  dove puo' essere inserito l'ID
	 */
	private void updateSettingsPromptText(TextField[] ipAdd, TextField portField, TextField idField) {
		
		String[] currIpString = currServer.getIp().split("\\.");
		for (int i = 0; i < 4; i++) {
			
			ipAdd[i].setPromptText(currIpString[i]);
			ipAdd[i].setText("");
		}
		
		portField.setPromptText(new Integer(currServer.getPort()).toString());
		portField.setText("");
		idField.setPromptText(currServer.getId());
		idField.setText("");
	}
}
