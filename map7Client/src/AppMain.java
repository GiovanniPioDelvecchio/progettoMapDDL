import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

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
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class AppMain extends Application {
	
	private Socket clientSocket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	// L'indirizzo ip di default e' quello di loopback, che porta al localhost
	
	/*
	 * Viene creata una ObservableList di istanze di ServerInformation contenente le informazioni sulle possibili connessioni ai server effettuabili.
	 * La lista viene inizializzata con un server di default, che rappresenta il localhost
	 * 
	 * TODO: implementare serializzazione
	 */
	private ObservableList<ServerInformation> servers = FXCollections.observableArrayList(new ServerInformation("127.0.0.1", 8080, "default"));
	
	// Il server a cui viene inizializzato il programma e' quello di default
	private ServerInformation currServer = servers.get(0);

	private Scene selectionScene, homeScene, settingsScene, newServerScene;

	public static void main(String[] args) {
		
		launch(args);
	}
	
	public void start(Stage mainStage) {
		

		/** HOME **/

		mainStage.setTitle("Client");
		BorderPane homePane = new BorderPane();

		/*
		 * descrizione componenti per la barra degli strumenti superiore
		 */

		ToolBar tools = new ToolBar();
		Image gear = new Image("file:res/gear.png", 30, 30, true, true);
		ImageView gearV = new ImageView(gear);
		Image questionMark = new Image("file:res/questionMark.png", 30, 30, true, true);
		ImageView questionMarkV = new ImageView(questionMark);
		
		Button opt = new Button("Opzioni");
		Button help = new Button("Aiuto");
		opt.setGraphic(gearV);
		help.setGraphic(questionMarkV);
		opt.setOnAction(e -> mainStage.setScene(settingsScene));
		tools.getItems().addAll(opt, new Separator(), help);
		homePane.setTop(tools);		
		
		/*
		 * Descrizione componenti per la vbox centrale
		 */
		VBox centralPanel = new VBox(50);
		centralPanel.setAlignment(Pos.CENTER);
		Label sel = new Label("Seleziona un'operazione");
		Button load = new Button("Carica");
		load.setMinSize(130, 20);
		Button create = new Button("Crea");
		create.setMinSize(130, 20);
		centralPanel.getChildren().addAll(sel, load,create);
		homePane.setCenter(centralPanel);
		
		/*
		 * Se il tasto "Carica" viene premuto, ci si connette al server e si comunica di caricare
		 * dal file l'albero corrispondente al nome del file scelto
		 */
		
		load.setOnAction(e -> {
			
			try {

				connectToServer();
				out.writeObject(2);
				mainStage.setScene(selectionScene);
			} catch(IOException | NullPointerException e1) {
				
				/*
				 * Se qualcosa va storto con l'invio dei messaggi al server si mostra un alert
				 */
				showAlert("Non e' stato possibile comunicare l'operazione al server selezionato");
			}
		
		});
		
		/*
		 * Se il tasto "Crea" viene premuto, ci si connette al server e si comunica di caricare
		 * da un database la tabella da cui verrÃ  ricavato l'albero di regressione
		 */
		create.setOnAction(e -> {

			try {

				connectToServer();
				out.writeObject(0);
				mainStage.setScene(selectionScene);
			} catch(IOException | NullPointerException e1) {
				
				/*
				 * Se qualcosa va storto con l'invio dei messaggi al server si mostra un alert
				 */
				showAlert("Non e' stato possibile comunicare l'operazione al server selezionato");
			}
		});

		/** INSERIMENTO TABELLA **/

		// Si viene a creare un layout a griglia
		GridPane selectionPane = new GridPane();
		selectionPane.setAlignment(Pos.CENTER);
		selectionPane.setHgap(40);
		selectionPane.setVgap(30);
		selectionPane.setPadding(new Insets(30,30,30,30));
		
		// Sulla prima riga, per due colonne, si estende il label di richiesta.
		Label selLabel = new Label("Inserisci il nome della tabella");
		selectionPane.add(selLabel, 0, 0, 2, 1);
		
		// Sulla seconda riga, per due colonne, si estende il campo per inserire il testo contenente il nome della tabella
		TextField tableName = new TextField();
		selectionPane.add(tableName, 0, 1, 2, 1);
		
		// Si inserisce un bottone di conferma che rimane disattivato se non viene inserito del testo
		Button confirm = new Button("Conferma");
		confirm.setDisable(true);
		confirm.setOnAction(e -> {

			try {
				
				// Alla pressione si tenta di mandare il nome della tabella al server
				out.writeObject(tableName.getText());
				String ans = (String) in.readObject();
				
				if (!ans.equals("OK")) {
					
					showAlert(ans);
				} else {
					
					// mainStage.setScene(predictScene);
				}
			} catch (ClassNotFoundException | IOException e1) {
				
				/* 
				 * si presuppone che la connessione sia già stata stabilita
				 * e pertanto non si prevede di gestire una NullPointerException
				 */
				showAlert("Errore durante la connessione al server");
			} 
		});
		selectionPane.add(confirm, 0, 2);
		
		// si crea anche un bottone per tornare indietro alla home
		Button back = new Button("Indietro");
		back.setOnAction(e -> { 
			
			if (clientSocket != null) {
				if (clientSocket.isConnected()) {
					try {
	
						// se è in corso una comunicazione col server, si notifica che si sta tornando alla home
						out.writeObject("ABORT");
						clientSocket.close();
					} catch (IOException e1) {
	
						showAlert("Errore durante la connessione al server");
					}
				}
				
			}
			mainStage.setScene(homeScene);
		});
		selectionPane.add(back, 1, 2);
		
		// Si imposta il campo testuale in maniera che se risulta essere vuoto, il tasto di conferma viene disabilitato
		tableName.setOnKeyReleased(e -> {

			if (tableName.getText().equals("")) {
				
				confirm.setDisable(true);
			} else {
				
				confirm.setDisable(false);
				if(e.getCode().equals(KeyCode.ENTER)) {
					
					confirm.getOnAction().handle(new ActionEvent());
				}
			}
		});
		
		
		/** FINESTRA DELLE IMPOSTAZIONI **/
		
		BorderPane serversPane = new BorderPane();

		/* 
		 * Dichiaro un TableView per la visione dei server conosciuti, e utilizzo i dati
		 * di servers per tenerea aggiornata la tabella
		 */
		TableView<ServerInformation> serverTable = new TableView<ServerInformation>(servers);
		
		/*
		 * Dichiaro le colonne che compongono la tabella. Oltre ad assegnare il nome della tabella,
		 * lego ogni colonna all'attributo Property relativo in ServerInformation, tramite il metodo
		 * setCellValueFactory 
		 */
		TableColumn<ServerInformation, String> idCol = new TableColumn<ServerInformation, String>("ID");
		idCol.setCellValueFactory(new PropertyValueFactory<ServerInformation, String>("id"));
		
		TableColumn<ServerInformation, String> ipCol = new TableColumn<ServerInformation, String>("Indirizzo Ip");
		ipCol.setCellValueFactory(new PropertyValueFactory<ServerInformation, String>("ip"));
		
		TableColumn<ServerInformation, Integer> portCol = new TableColumn<ServerInformation, Integer>("Porta");
		portCol.setCellValueFactory(new PropertyValueFactory<ServerInformation, Integer>("port"));
		
		// Infine imposto le colonne della tabella a quelle appena create
		serverTable.getColumns().setAll(idCol, ipCol, portCol);
		
		// Creo un'istanza di TableViewFocusModel per gestire le celle evidenziate dall'utente
		TableView.TableViewFocusModel<ServerInformation> userFocus = new TableView.TableViewFocusModel<ServerInformation>(serverTable);
		serverTable.setFocusModel(userFocus);
		
		HBox settingsButtonsLayout = new HBox();
		settingsButtonsLayout.setAlignment(Pos.CENTER);
		settingsButtonsLayout.setPadding(new Insets(25, 25, 25, 25));
		
		
		Button addServer = new Button("Aggiungi");
		Button removeServer = new Button("Elimina");
		Button confirmServer = new Button("Conferma");
		Button backSettings = new Button("Indietro");
		
		backSettings.setOnAction(e -> {
			
			mainStage.setScene(homeScene);
		});

		settingsButtonsLayout.getChildren().addAll(addServer, removeServer, confirmServer, backSettings);
		
		addServer.setOnAction(e -> {
			
			mainStage.setScene(newServerScene);
		});
		
		removeServer.setOnAction(e -> {
			
			servers.remove(userFocus.getFocusedItem());
			if (servers.size() == 0) {
				
				confirmServer.setDisable(true);
			}
		});
		
		serversPane.setCenter(serverTable);
		serversPane.setBottom(settingsButtonsLayout);
		
		/** AGGIUNTA NUOVO SERVER **/
		
		GridPane newServerPane = new GridPane();
		newServerPane.setAlignment(Pos.CENTER);
		newServerPane.setHgap(10);
		newServerPane.setVgap(10);
		newServerPane.setPadding(new Insets(25, 50, 25, 25));
		
		Label ipLabel = new Label("Indirizzo IP");
		Label portLabel = new Label("Porta");
		Label idLabel = new Label("Server ID");
		
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
		Button confirmButtonSettings = new Button("Conferma");
		HBox backLayoutSettings = new HBox();
		backLayoutSettings.setAlignment(Pos.CENTER_LEFT);
		backLayoutSettings.getChildren().add(back);

		/*
		 * Si dichiara un oggetto di tipo EventHandler<ActionEvent>, il cui metodo handle descrive il comportamento
		 * di conferma alla pressione del pulsante confirmButtonSettings (o alla pressione del tasto enter).
		 * Si e' scelta un oggetto di classe anonima al posto di una lambda-espressione poichè il corpo
		 * della funzione handle è molto esteso ed è pertanto più facilmente leggibile
		 */
		EventHandler<ActionEvent> confirmEvent = new EventHandler<ActionEvent>() {

			public void handle(ActionEvent e) {
				
				/*
				 * Si controlla se l'ip inserito e' un indirizzo ip valido.
				 * Se l'indirizzo ip inserito e' mal formattato, viene visualizzato un errore, e non viene
				 * inserito il nuovo server.
				 */
				
				StringBuffer newIp = null;
				int intPort = -1;

				boolean isValid = true;
				for (TextField i : ipAdd) {
					
					int readInteger = -1;
					try {
	
						readInteger = Integer.parseInt(i.getText());
					} catch (NumberFormatException err) {
						
						isValid = false;
						break;
					}
					if (readInteger < 0 || readInteger > 255) {
						
						isValid = false;
						break;
					}
				}
				if (isValid) {
	
					/*
					 * Viene utilizzato un oggetto StringBuffer in maniera da non creare un nuovo oggetto di classe
					 * String per ogni iterazione del ciclo.
					 */
					newIp = new StringBuffer("");
					for (int i = 0; i < 3; i++) {
						
						newIp.append(ipAdd[i].getText());
						newIp.append(".");
					}
					newIp.append(ipAdd[3].getText());
				} else {
	
					showAlert("L'indirizzo IP inserito non è valido.\n"
							+ "I valori che compongono l'indirizzo devono essere interi da 0 a 255.");
					return;
				}
				
				/*
				 * Si parsifica il numero di porta inserito nel field associato. Se il numero di porta
				 * e' valido, si aggiorna l'attributo port, altrimenti viene lasciato invariato (e si visualizza
				 * un errore).
				 */
				String readPort = portField.getText();
				
				if (!readPort.equals("")) {
				
					final String errorMessage = "Il numero di porta inserito non è valido.\n"
							+ "Il numero di porta deve essere un intero fra 1 e 65535.";

					try {

						intPort = Integer.parseInt(readPort);
						if (!(intPort > 0 && intPort <= 65535)) {

							showAlert(errorMessage);
							return;
						}
					} catch (NumberFormatException f) {

						showAlert(errorMessage);
						return;
					}
				}
				
				String readId = idField.getText();
				
				/*
				 * L'ID dovra' essere un campo compilato, e non devono essere gia' presenti in memoria
				 * server con lo stesso id impostato
				 */
				if(readId.equals("")) {
					
					showAlert("Il server deve avere un identificatore");
					return;
				}
				
				// TODO: capire perche' non funziona
				if (servers.contains(new ServerInformation("", 0, readId))) {

					showAlert("Un server dall'ID \"" + readId + "\" e' gia' esistente.");
					return;
				}
				
				servers.add(new ServerInformation(newIp.toString(), intPort, readId));
				currServer = servers.get(servers.size() - 1);
				// Infine si aggiornano le stringhe di prompt dei campi testuali con i valori correnti di indirizzo ip e porta
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
		mainStage.setScene(homeScene);
		mainStage.show();
	}

	
	private void connectToServer() throws IOException {
		
			clientSocket = new Socket(currServer.getIp(), currServer.getPort());
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			in = new ObjectInputStream(clientSocket.getInputStream());
	}
	
	private void showAlert(String message) {

		/*
		 * Se qualcosa va storto e' possibile mostrare una finestra di dialogo con un messaggio
		 */
		
		Alert toShow = new Alert(Alert.AlertType.ERROR);
		toShow.setContentText(message);
		toShow.show();
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
