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

public class AppMain extends Application {
	
	private Socket clientSocket;
	private ObjectOutputStream out;
	private ObjectInputStream in;

	/*
	 * Viene creata una ObservableList di istanze di ServerInformation contenente le informazioni sulle possibili connessioni ai server effettuabili.
	 * La lista viene inizializzata con un server di default, che rappresenta il localhost
	 */
	private ObservableList<ServerInformation> servers = FXCollections.observableArrayList(new ServerInformation(Constants.DEFAULT_SERVER_IP,
			Constants.DEFAULT_SERVER_PORT, Constants.DEFAULT_SERVER_ID));

	// Il server a cui viene inizializzato il programma e' quello di default
	private ServerInformation currServer = servers.get(0);

	private Scene selectionScene, homeScene, settingsScene, newServerScene, predictScene;
	private boolean loadFlag = false;

	public static void main(String[] args) {
		
		launch(args);
	}
	
	public void start(Stage mainStage) {
		

		/** HOME **/

		mainStage.setTitle(Constants.CLIENT_WINDOW_NAME);
		BorderPane homePane = new BorderPane();
		mainStage.getIcons().add(new Image(Constants.PATH_CLIENT_ICON));

		/*
		 * descrizione componenti per la barra degli strumenti superiore
		 */

		ToolBar tools = new ToolBar();
		Image gear = new Image(Constants.PATH_GEAR_ICON, 20, 20, true, true);
		ImageView gearV = new ImageView(gear);
		Image questionMark = new Image(Constants.PATH_HELP_ICON, 20, 20, true, true);
		ImageView questionMarkV = new ImageView(questionMark);
		
		Button opt = new Button(Constants.BUTTON_OPTIONS);
		Button help = new Button(Constants.BUTTON_HELP);
		opt.setId(Constants.ID_SMALL_BUTTON);
		help.setId(Constants.ID_SMALL_BUTTON);
		opt.setGraphic(gearV);
		help.setGraphic(questionMarkV);
		opt.setOnAction(e -> mainStage.setScene(settingsScene));
		tools.getItems().addAll(opt, help);
		homePane.setTop(tools);		
		
		/*
		 * Descrizione componenti per la vbox centrale
		 */
		VBox centralPanel = new VBox(50);
		centralPanel.setAlignment(Pos.CENTER);
		Label sel = new Label(Constants.LABEL_SELECTION);
		sel.setId(Constants.ID_WELCOME_LABEL);
		Button load = new Button(Constants.BUTTON_LOAD);
		load.setMinSize(130, 20);
		Button create = new Button(Constants.BUTTON_CREATE);
		create.setMinSize(130, 20);
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
				out.writeObject(2);
				mainStage.setScene(selectionScene);
			} catch(IOException | NullPointerException e1) {
				
				/*
				 * Se qualcosa va storto con l'invio dei messaggi al server si mostra un alert
				 */
				showAlert(Constants.ERROR_NO_COMMUNICATION);
			}
		
		});
		
		/*
		 * Se il tasto "Crea" viene premuto, ci si connette al server e si comunica di caricare
		 * da un database la tabella da cui verra'� ricavato l'albero di regressione
		 */
		create.setOnAction(e -> {

			try {
				loadFlag = false;
				connectToServer();
				out.writeObject(0);
				mainStage.setScene(selectionScene);
			} catch(IOException | NullPointerException e1) {
				
				/*
				 * Se qualcosa va storto con l'invio dei messaggi al server si mostra un alert
				 */
				showAlert(Constants.ERROR_NO_COMMUNICATION);
			}
		});
		

		/** FINESTRA DI PREDIZIONE **/
		
		/*	
		 * Si trova in questo punto perché la descrizione della scena di predizione
		 *	deve precedere la predizione stessa (handlePredict)
		 */
		BorderPane predictPane = new BorderPane();
		VBox predictBox = new VBox(50);
		TilePane userChoices = new TilePane();
		Label predictedValue = new Label(Constants.LABEL_PREDICTION_QUERY);
		HBox predictButtons = new HBox(50);
		predictBox.setAlignment(Pos.CENTER);
		userChoices.setAlignment(Pos.CENTER);
		userChoices.setHgap(5d);
		userChoices.setVgap(5d);
		userChoices.setPrefColumns(3);
		predictedValue.setAlignment(Pos.CENTER);
		predictButtons.setAlignment(Pos.CENTER);
		predictedValue.setId(Constants.ID_PREDICTION_LABEL);
		
		Button redo = new Button(Constants.BUTTON_RESTART);
		redo.setDisable(true);
		Button backPredict = new Button(Constants.BUTTON_BACK);
		backPredict.setOnAction(e -> { 
			try {
					// Si notifica che si sta tornando alla home
					out.writeObject(-1);
					clientSocket.close();
				} catch (IOException e1) {
					showAlert(Constants.ERROR_CLOSING_COMMUNICATION);
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
		
		redo.setOnAction(e->{
			
			try {
				out.writeObject(3);
				predictedValue.setText(Constants.LABEL_PREDICTION_QUERY);
				redo.setDisable(true);
				handlePredict(userChoices, predictedValue, redo);
			} catch (IOException e1) {
				
				showAlert(Constants.ERROR_SERVER_UNREACHABLE);
				mainStage.setScene(homeScene);
			}
			
		});
		
		
		predictButtons.getChildren().add(redo);
		predictButtons.getChildren().add(backPredict);
		predictBox.getChildren().add(predictedValue);
		predictBox.getChildren().add(userChoices);
		predictBox.getChildren().add(predictButtons);

		predictPane.setCenter(predictBox);
		
		

		/** HELP **/
		
		help.setOnAction(e -> {
			
			Alert helpScreen = new Alert(Alert.AlertType.INFORMATION);
			helpScreen.setHeaderText(Constants.HELP_WINDOW_NAME);
			helpScreen.setTitle(Constants.HELP_WINDOW_NAME);
			helpScreen.setContentText(Constants.HELP_CONTENT_TEXT);
			helpScreen.getDialogPane().getStylesheets().add(Constants.PATH_THEME);
			((Stage) helpScreen.getDialogPane().getScene().getWindow()).getIcons().add(new Image(Constants.PATH_CLIENT_ICON));
			helpScreen.show();
		});


		/** INSERIMENTO TABELLA **/

		// Si viene a creare un layout a griglia
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
		
		// Si inserisce un bottone di conferma che rimane disattivato se non viene inserito del testo
		Button confirm = new Button(Constants.BUTTON_CONFIRM);
		
		
		confirm.setDisable(true);
		confirm.setOnAction(e -> {

			try {

				// Alla pressione si tenta di mandare il nome della tabella al server
				out.writeObject(tableName.getText());

				String ans = (String) in.readObject();
				
				if (!ans.equals(Constants.SERVER_OK)) {
					
					showAlert(ans);
				} else {
					
					if (loadFlag == false) {
						
						out.writeObject(1);
						ans = ((String) in.readObject());
						/* Se il salvataggio dell'albero non va a buon fine, si permette comunque
						 * di accedere alla predizione
						 */
						if (!ans.equals(Constants.SERVER_OK)) {
						
							showAlert(Constants.ERROR_SAVING_TREE);
						}
					}
					
					
					mainStage.setScene(predictScene);
					
					out.writeObject(3);
					
					handlePredict(userChoices, predictedValue, redo);


				}
			} catch (ClassNotFoundException | IOException e1) {
				
				/* 
				 * si presuppone che la connessione sia gi� stata stabilita
				 * e pertanto non si prevede di gestire una NullPointerException
				 */
				showAlert(Constants.ERROR_INIT_CONNECTION);
				mainStage.setScene(homeScene);
			} 
		});
		selectionPane.add(confirm, 0, 2);
		
		// si crea anche un bottone per tornare indietro alla home

		Button backSelection = new Button(Constants.BUTTON_BACK);
		backSelection.setOnAction(e->{	
			
		try {
				/*
				 * Si notifica che si sta tornando alla home
				 *la stringa comincia con # poich� nessun file pu� avere tale nome
				 */
				out.writeObject(Constants.CLIENT_ABORT);
				clientSocket.close();
				} catch (IOException e1) {
	
				showAlert(Constants.ERROR_CLOSING_COMMUNICATION);
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
			ArrayList<MutableServerInformation> serializedServerList = (ArrayList<MutableServerInformation>) serversIn.readObject();

			servers.clear();
			for (MutableServerInformation s : serializedServerList) {
				
				servers.add(s.toServerInformation());
			}

			serversIn.close();
			serversInFile.close();
		} catch (IOException | ClassNotFoundException e1) {
			
			if (e1 instanceof FileNotFoundException) {
				
				Alert serversNotFound = new Alert(Alert.AlertType.WARNING);
				serversNotFound.setContentText(Constants.CONTENT_TEXT_NO_SERVERS_INFO);
				serversNotFound.getDialogPane().getStylesheets().add(Constants.PATH_THEME);
				((Stage) serversNotFound.getDialogPane().getScene().getWindow()).getIcons().add(new Image(Constants.PATH_WARNING_ICON));
				serversNotFound.show();
			} else {

				showAlert(Constants.ERROR_LOADING_SERVERS);
			}
		}
		
		/*
		 * Viene impostata la serializzazione della lista di server conosciuti alla chiusura
		 * del programma.
		 */
		mainStage.setOnCloseRequest(e -> {
			
			if (clientSocket != null) {
				if (clientSocket.isConnected()) {
					try {
						// se e' in corso una comunicazione col server, si notifica che si sta tornando alla home
						if(mainStage.getScene().equals(selectionScene)) {
							out.writeObject(Constants.CLIENT_ABORT);
						} else {
							out.writeObject(-1);
						}
						clientSocket.close();
					} catch (IOException e1) {
	
						showAlert(Constants.ERROR_CLOSING_COMMUNICATION);
					}
				
				}
			}
			try {
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

				showAlert(Constants.ERROR_SAVING_SERVERS);
			}
		});
		
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
		TableColumn<ServerInformation, String> idCol = new TableColumn<ServerInformation, String>("ID");
		idCol.setCellValueFactory(new PropertyValueFactory<ServerInformation, String>("id"));
		
		TableColumn<ServerInformation, String> ipCol = new TableColumn<ServerInformation, String>("Indirizzo Ip");
		ipCol.setCellValueFactory(new PropertyValueFactory<ServerInformation, String>("ip"));
		
		TableColumn<ServerInformation, Integer> portCol = new TableColumn<ServerInformation, Integer>("Porta");
		portCol.setCellValueFactory(new PropertyValueFactory<ServerInformation, Integer>("port"));
		
		// Infine vengono aggiunte le tabelle appena create alla TableView serverTable.
		serverTable.getColumns().setAll(idCol, ipCol, portCol);
		serverTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		/*
		 * Viene creato un oggetto TableViewFocusModel per poter gestire l'interazione fra l'utente e la tabella.
		 * L'oggetto viene associato alla tabella serverTable.
		 */
		TableView.TableViewFocusModel<ServerInformation> userFocus = new TableView.TableViewFocusModel<ServerInformation>(serverTable);
		serverTable.setFocusModel(userFocus);
		
		/*
		 * Infine vengono dichiarati i bottoni per interagire con gli elementi della tabella.
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
		
		backSettings.setOnAction(e -> {
			
			mainStage.setScene(homeScene);
		});
		
		addServer.setOnAction(e -> {
			
			mainStage.setScene(newServerScene);
		});
		
		removeServer.setOnAction(e -> {
			
			servers.remove(userFocus.getFocusedItem());
			if (servers.size() == 0) {
				
				confirmServer.setDisable(true);
			}
		});
		
		settingsButtonsLayout.getChildren().addAll(addServer, removeServer, confirmServer, backSettings);

		serversPane.setCenter(serverTable);
		serversPane.setBottom(settingsButtonsLayout);
		
		/** AGGIUNTA NUOVO SERVER **/
		
		GridPane newServerPane = new GridPane();
		newServerPane.setAlignment(Pos.CENTER);
		newServerPane.setHgap(10);
		newServerPane.setVgap(10);
		newServerPane.setPadding(new Insets(25, 50, 25, 25));
		
		Label ipLabel = new Label(Constants.LABEL_SERVER_IP_ADDRESS);
		Label portLabel = new Label(Constants.LABEL_SERVER_PORT);
		Label idLabel = new Label(Constants.LABEL_SERVER_ID);
		
		Button backSetting = new Button(Constants.BUTTON_BACK);
		backSetting.setOnAction(e->mainStage.setScene(homeScene));
		
		
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
		 * Infine si utilizzano due pulsanti, uno di conferma e uno per tornare alla home del programma.
		 */
		Button confirmButtonSettings = new Button(Constants.BUTTON_CONFIRM);
		HBox backLayoutSettings = new HBox();
		backLayoutSettings.setAlignment(Pos.CENTER_LEFT);
		backLayoutSettings.getChildren().add(backSetting);

		/*
		 * Si dichiara un oggetto di tipo EventHandler<ActionEvent>, il cui metodo handle descrive il comportamento
		 * di conferma alla pressione del pulsante confirmButtonSettings (o alla pressione del tasto enter all'interno
		 * di un qualsiasi TextField della schermata).
		 * Si e' scelta un oggetto di classe anonima al posto di una lambda-espressione poiche' il corpo
		 * della funzione handle � molto esteso ed � pertanto pi� facilmente leggibile.
		 */
		EventHandler<ActionEvent> confirmEvent = new EventHandler<ActionEvent>() {

			public void handle(ActionEvent e) {
				
				/*
				 * Viene usato uno StringBuffer per la concatenzione del nuovo indirizzo Ip per evitare
				 * la creazione ripetuta di nuovi oggetti String.
				 */
				StringBuffer newIp = null;
				int intPort = -1;

				/*
				 * Si controlla se l'ip inserito e' un indirizzo ip valido.
				 * Se l'indirizzo ip inserito e' mal formattato, viene visualizzato un errore, e non viene
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

					showAlert(Constants.ERROR_PARSING_IP);
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

							showAlert(Constants.ERROR_PARSING_PORT);
							return;
						}
					} catch (NumberFormatException f) {

						showAlert(Constants.ERROR_PARSING_PORT);
						return;
					}
				}
				
				String readId = idField.getText();
				
				/*
				 * L'ID dovra' essere un campo compilato, e non devono essere gia' presenti in memoria
				 * server dallo stesso identificatore.
				 */
				if (readId.equals("")) {
					
					showAlert(Constants.ERROR_NO_SERVER_ID);
					return;
				}

				ServerInformation toAdd = new ServerInformation(newIp.toString(), intPort, readId);
				if (servers.contains(toAdd)) {

					showAlert(Constants.ERROR_ID_ALREADY_EXISTS + readId);
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
		 * e' effettuata qua, poiche' deve richiamare updateSettingsPromptText
		 */
		confirmServer.setOnAction(e -> {

			currServer = userFocus.getFocusedItem();
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

		/*
		 * Chiamata ai metodi per mostrare la scena principale
		 */
		homeScene = new Scene(homePane, 400, 400);
		selectionScene = new Scene(selectionPane, 400, 400);
		settingsScene = new Scene(serversPane, 400, 400);
		newServerScene = new Scene(newServerPane, 400,400);
		predictScene = new Scene(predictPane, 400, 400);

		homeScene.getStylesheets().add(Constants.PATH_THEME);
		selectionScene.getStylesheets().add(Constants.PATH_THEME);
		settingsScene.getStylesheets().add(Constants.PATH_THEME);
		newServerScene.getStylesheets().add(Constants.PATH_THEME);
		predictScene.getStylesheets().add(Constants.PATH_THEME);

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
	 * Se qualcosa va storto e' possibile mostrare una finestra di dialogo con un messaggio,
	 * tramite questo metodo.
	 * 
	 * @param message <code>String</code> da mostrare nella finestra di dialogo 
	 */
	private void showAlert(String message) {
		
		Alert toShow = new Alert(Alert.AlertType.ERROR);
		toShow.setContentText(message);
		toShow.getDialogPane().getStylesheets().add(Constants.PATH_THEME);
		((Stage) toShow.getDialogPane().getScene().getWindow()).getIcons().add(new Image(Constants.PATH_ERROR_ICON));
		toShow.show();
	}
	
	/**
	 * Metodo necessario per poter gestire la predizione attraverso il server
	 * 
	 * @param userChoices <code>TilePane</code> su cui inserire i tasti, corrispondenti alle possibili scelte 
	 * (quando, esplorando l'albero di regressione, si arriva ad un nodo di split)
	 * @param predictedValue <code>Label</code> su cui scrivere il risultato della predizione
	 * @param redo <code>Button</code> necessario per poter effettuare una nuova predizione una volta finita
	 * quella precedente. E' necessario avere questo parametro per poter gestire quando è attivato e quando no.
	 * 
	 */
	private void handlePredict(TilePane userChoices, Label predictedValue, Button redo) {
		
		try {
			
			String toCheck;
			
			toCheck = (String)in.readObject();
			
			
			if (toCheck.equals(Constants.SERVER_QUERY)) {

				List<String> options = new ArrayList<String>((ArrayList<String>)in.readObject());
				int i = 0;
				for (String elem : options) {
					
					addSplitButton(userChoices, elem, i, predictedValue, redo);
					i++;
				}
				
			} else {
				
				predictedValue.setText(Constants.LABEL_PREDICTED_VALUE + ((Double)in.readObject()).toString());
				redo.setDisable(false);
				
				
			}
		} catch (ClassNotFoundException e) {
			
			showAlert(Constants.ERROR_COMMUNICATING_UNEXPECTED_ANSWER);
		} catch (IOException e) {

			showAlert(Constants.ERROR_COMMUNICATING);
		} catch (ClassCastException e) {
			
			showAlert(Constants.ERROR_COMMUNICATING_BAD_ANSWER);
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
				
				showAlert(Constants.ERROR_SENDING_VALUE);
			}
		});
		toShow.setId(Constants.ID_PREDICTION_BUTTON);
		userChoices.getChildren().add(toShow);
	}
	
	/**
	 * Metodo utilizzato per tenere aggiornati le stringhe di prompt nei campi
	 * dove inserire indirizzo Ip e Porta del server a cui collegarsi.
	 * Le stringhe utilizzate come testo di prompt sono i valori correnti dell'Ip
	 * e della Porta.
	 * 
	 * @param ipAdd Array di <code>TextField</code> che compongono un indirizzo Ip.
	 * @param portField <code>TextField</code> dove verra' inserito il numero di porta.
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
