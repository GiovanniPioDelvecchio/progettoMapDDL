import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.application.Application;
import javafx.event.ActionEvent;

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
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import javafx.stage.Stage;

public class AppMain extends Application {
	
	private Socket clientSocket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String ip = "localhost";
	private int port = 8080;

	private Scene selectionScene, homeScene;
	
	public static void main(String[] args) {
		
		launch(args);
	}
	
	public void start(Stage mainStage) {
		
		/** HOME **/

		mainStage.setTitle("Client");
		BorderPane pane = new BorderPane();

		/*
		 * descrizione componenti per la barra degli strumenti superiore
		 */

		ToolBar tools = new ToolBar();
		Image gear = new Image("gear.png", 30, 30, true, true);
		ImageView gearV = new ImageView(gear);
		Image questionMark = new Image("questionMark.png", 30, 30, true, true);
		ImageView questionMarkV = new ImageView(questionMark);
		
		Button opt = new Button("Opzioni");
		Button help = new Button("Aiuto");
		opt.setGraphic(gearV);
		help.setGraphic(questionMarkV);
		
		tools.getItems().addAll(opt, new Separator(), help);
		pane.setTop(tools);		
		
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
		pane.setCenter(centralPanel);
		
		/*
		 * Se il tasto "Carica" viene premuto, ci si connette al server e si comunica di caricare
		 * dal file l'albero corrispondente al nome del file scelto
		 */
		
		load.setOnAction(e -> {
			
			tryConnection();
			try {

				out.writeObject(2);
				mainStage.setScene(selectionScene);
			} catch(IOException e1) {
				
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
		create.setOnAction(e->{
				
			tryConnection();
			try {

				out.writeObject(0);
				mainStage.setScene(selectionScene);
			} catch(IOException e1) {
				
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
					
					Alert missingTable = new Alert(Alert.AlertType.ERROR);
					missingTable.setContentText(ans);
					missingTable.show();
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
			
			if (clientSocket != null || clientSocket.isConnected()) {
				
				try {

					// se è in corso una comunicazione col server, si notifica che si sta tornando alla home
					out.writeObject("ABORT");
					clientSocket.close();
				} catch (IOException e1) {

					showAlert("Errore durante la connessione al server");
				}
				mainStage.setScene(homeScene);
			}
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

		selectionScene = new Scene(selectionPane);

		/*
		 * Chiamata ai metodi per mostrare la scena principale
		 */
		
		
		homeScene = new Scene(pane, 400, 400);
		mainStage.setScene(homeScene);
		mainStage.show();
	}

	
	private void tryConnection() {
		
		try {

			clientSocket = new Socket(ip, port);
			System.out.println(clientSocket);		
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			in = new ObjectInputStream(clientSocket.getInputStream());
			
		} catch(IOException e) {
			
			/*
			 * Se qualcosa va storto con la creazione del collegamento si mostra un alert
			 */
			showAlert("Non e' stato possibile connettersi al server selezionato");
		}
	}
	
	private void showAlert(String message) {

		/*
		 * Se qualcosa va storto e' possibile mostrare una finestra di dialogo con un messaggio
		 */
		
		Alert toShow = new Alert(Alert.AlertType.ERROR);
		toShow.setContentText(message);
		toShow.show();
	}
}
