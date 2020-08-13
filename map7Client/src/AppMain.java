import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
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
	private String ip = "127.0.0.1";
	private int port = 8080;

	private Scene selectionScene, homeScene, settingsScene, predictScene;
	
	private boolean loadFlag = false;

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
				loadFlag = true;
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
		 * da un database la tabella da cui verrà ricavato l'albero di regressione
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
				showAlert("Non e' stato possibile comunicare l'operazione al server selezionato");
			}
		});
		
		/** FINESTRA DI PREDIZIONE **/
		
		/*	
		 * Si trova in questo punto perché la descrizione della scena di predizione
		 *	deve precedere la predizione stessa (handlePredict)
		 */
		BorderPane predictPane = new BorderPane();
		VBox predictBox = new VBox();
		Label predictMessage = new Label();
		Label predictedValue = new Label();
		TextField userChoice = new TextField();
		HBox predictButtons = new HBox();
		Button confirmChoice = new Button("Conferma");
		
		
		Button redo = new Button("Ricomincia");
		redo.setDisable(true);
		Button backPredict = new Button("Indietro");
		backPredict.setOnAction(e -> { 
			
			if (clientSocket != null) {
				if (clientSocket.isConnected()) {
					try {
	
						// se � in corso una comunicazione col server, si notifica che si sta tornando alla home
						out.writeObject(-1);
						clientSocket.close();
					} catch (IOException e1) {
	
						showAlert("Errore durante la connessione al server");
					}
				}
				
			}
			mainStage.setScene(homeScene);
		});
		
		redo.setOnAction(e->{
			
			try {
				out.writeObject(3);
				predictedValue.setText("");
				redo.setDisable(true);
				handlePredict(predictMessage, predictedValue, redo);
			} catch (IOException e1) {
				
				showAlert("Non è stato possibile raggiungere il server");
			}
			
		});
		
		confirmChoice.setOnAction(e->{
			
			if (!predictMessage.getText().equals("")) {
				
				
				try {
					
					Integer toSend = Integer.parseInt(userChoice.getText());
					userChoice.clear();
					//System.out.println(toSend);
					out.writeObject(toSend);
					
					handlePredict(predictMessage, predictedValue, redo);
					
				} catch (IOException e1) {
					
					showAlert("Errore nella comunicazione con il server");
					
				} catch (NumberFormatException e2) {
					
					showAlert("Inserire un numero intero");
				}
			}
		});
		
		
		
		
		predictButtons.getChildren().add(confirmChoice);
		predictButtons.getChildren().add(redo);
		predictButtons.getChildren().add(backPredict);
		predictBox.getChildren().add(predictMessage);
		predictBox.getChildren().add(userChoice);
		predictBox.getChildren().add(predictButtons);
		predictBox.getChildren().add(predictedValue);
		predictPane.setCenter(predictBox);
		
		
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
					
					
					
					if (loadFlag == false) {
						
						out.writeObject(1);
						if (!((String) in.readObject()).equals("OK")) {
						
							showAlert("Errore nel salvataggio dei dati");
						}
					}
					
					
					
					mainStage.setScene(predictScene);
					
					out.writeObject(3);
					//confirmChoice.setDisable(false);
					
					handlePredict(predictMessage, predictedValue, redo);
					
					//confirmChoice.setDisable(true);

				}
			} catch (ClassNotFoundException | IOException e1) {
				
				/* 
				 * si presuppone che la connessione sia gi� stata stabilita
				 * e pertanto non si prevede di gestire una NullPointerException
				 */
				showAlert("Errore durante la connessione al server");
			} 
		});
		selectionPane.add(confirm, 0, 2);
		
		// si crea anche un bottone per tornare indietro alla home
		Button backSelection= new Button("Indietro");
		backSelection.setOnAction(backPredict.getOnAction());
		
		selectionPane.add(backSelection, 1, 2);
		
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
		
		GridPane settingsPane = new GridPane();
		settingsPane.setAlignment(Pos.CENTER);
		settingsPane.setHgap(10);
		settingsPane.setVgap(10);
		settingsPane.setPadding(new Insets(25, 50, 25, 25));
		
		Label ipLabel = new Label("Indirizzo IP");
		Label portLabel = new Label("Porta");
		
		Button backSetting = new Button("Indietro");
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

		/*
		 * Con questa chiamata a funzione si aggiornano i testi di prompt dei campi testuali
		 * con i valori correnti dell'indirizzo ip e della porta.
		 */
		updateSettingsPromptText(ipAdd, portField);

		/*
		 * Infine si utilizzano due pulsanti, uno di conferma e uno per tornare alla home del programma.
		 */
		Button confirmButtonSettings = new Button("Conferma");
		HBox backLayoutSettings = new HBox();
		backLayoutSettings.setAlignment(Pos.CENTER_LEFT);
		backLayoutSettings.getChildren().add(backSetting);

		/*
		 * Si dichiara un oggetto di tipo EventHandler<ActionEvent>, il cui metodo handle descrive il comportamento
		 * di conferma alla pressione del pulsante confirmButtonSettings (o alla pressione del tasto enter).
		 * Si e' scelta un oggetto di classe anonima al posto di una lambda-espressione poich� il corpo
		 * della funzione handle � molto esteso ed � pertanto pi� facilmente leggibile
		 */
		EventHandler<ActionEvent> confirmEvent = new EventHandler<ActionEvent>() {

			public void handle(ActionEvent e) {
				
				/*
				 * Si controlla se l'ip inserito e' un indirizzo ip valido.
				 * Se i field sono vuoti, si lascia il valore dell'ultimo indirizzo ip utilizzato.
				 * Se l'indirizzo ip inserito e' mal formattato, viene visualizzato un errore, e non
				 * si modifica l'indirizzo ip corrente.
				 */
				
				// Se nessun campo di testo e' riempito, allora l'indirizzo ip utilizzato e' l'ultimo selezionato.
				boolean isDefault = Arrays.asList(ipAdd).stream().filter(i -> ((TextField) i).getText().equals("")).count() == 4;
				
				if (!isDefault) {

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
						StringBuffer newIp = new StringBuffer("");
						for (int i = 0; i < 3; i++) {
							
							newIp.append(ipAdd[i].getText());
							newIp.append(".");
						}
						newIp.append(ipAdd[3].getText());
						ip = new String(newIp);
					} else {
		
						showAlert("L'indirizzo IP inserito non � valido.\n"
								+ "I valori che compongono l'indirizzo devono essere interi da 0 a 255.");
					}
				}
				
				/*
				 * Si parsifica il numero di porta inserito nel field associato. Se il numero di porta
				 * e' valido, si aggiorna l'attributo port, altrimenti viene lasciato invariato (e si visualizza
				 * un errore).
				 */
				String readPort = portField.getText();
				
				if (!readPort.equals("")) {
				
					final String errorMessage = "Il numero di porta inserito non � valido.\n"
							+ "Il numero di porta deve essere un intero fra 1 e 65535.";
					int intPort;
					try {

						intPort = Integer.parseInt(readPort);
						if (intPort > 0 && intPort <= 65535) {

							port = intPort;
						} else {

							showAlert(errorMessage);
						}
					} catch (NumberFormatException f) {

						showAlert(errorMessage);
					}
				}
				
				// Infine si aggiornano le stringhe di prompt dei campi testuali con i valori correnti di indirizzo ip e porta
				updateSettingsPromptText(ipAdd, portField);
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
		 * Si aggiungono i nodi alla griglia di layout.
		 */
		settingsPane.add(ipLabel, 1, 1);
		settingsPane.add(ipLayout, 2, 1);
		settingsPane.add(portLabel, 1, 2);
		settingsPane.add(portField, 2, 2);
		settingsPane.add(confirmButtonSettings, 1, 3);
		settingsPane.add(backLayoutSettings, 2, 3);
		
		/*
		 * Chiamata ai metodi per mostrare la scena principale
		 */
		homeScene = new Scene(homePane, 400, 400);
		selectionScene = new Scene(selectionPane, 400, 400);
		settingsScene = new Scene(settingsPane, 400, 400);
		predictScene = new Scene(predictPane, 400, 400);
		mainStage.setScene(homeScene);
		mainStage.show();
	}

	
	private void connectToServer() throws IOException {
		
			clientSocket = new Socket(ip, port);
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
	
	private void handlePredict(Label predictMessage, Label predictedValue, Button redo) {
		
		try {
			
			String toCheck;
			
			toCheck = (String)in.readObject();
			
			
			if(toCheck.equals("QUERY")) {
				
				predictMessage.setText((String)in.readObject());
			} else {
				
				predictedValue.setText(((Double)in.readObject()).toString());
				//Alert confirmation = new Alert(AlertType.CONFIRMATION);
				//confirmation.setContentText("Vuoi ripetere?");
				//confirmation.show();
				
				predictMessage.setText("Vuoi ricominciare?");
				redo.setDisable(false);
				
					
				//out.writeObject(-1);
				
			}
				
			
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
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
	private void updateSettingsPromptText(TextField[] ipAdd, TextField portField) {
		
		String[] currIpString = ip.split("\\.");
		for (int i = 0; i < 4; i++) {
			
			ipAdd[i].setPromptText(currIpString[i]);
		}
		
		portField.setPromptText(new Integer(port).toString());

	}
}
