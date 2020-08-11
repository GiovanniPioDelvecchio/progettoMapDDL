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
import javafx.stage.Stage;

public class AppMain extends Application {
	
	private Socket clientSocket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String ip = "localhost";
	private int PORT = 8080;
	
	private Scene selectionScene;
	
	public static void main(String[] args) {
		
		launch(args);
	}
	
	public void start(Stage mainStage) {
		
		// Finestra di notifica in caso di errore di connessione al server.
		Alert connErr = new Alert(Alert.AlertType.ERROR);
		connErr.setContentText("Errore durante la connessione al server");
		
		// Finestra di selezione della tabella //

		// Si viene a creare un layout a griglia
		GridPane selectionPane = new GridPane();
		selectionPane.setAlignment(Pos.CENTER);
		selectionPane.setHgap(40);
		selectionPane.setVgap(30);
		selectionPane.setPadding(new Insets(30,30,30,30));
		
		//Sulla prima riga, per due colonne, si estende il label di richiesta.
		Label selLabel = new Label("Inserisci il nome della tabella");
		selectionPane.add(selLabel, 0, 0, 2, 1);
		
		//Sulla seconda riga, per due colonne, si estende il campo per inserire il testo contenente il nome della tabella
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
				/* si presuppone che la connessione sia già stata stabilita
				 * e pertanto non si prevede di gestire una NullPointerException
				 */
				connErr.show();
			} 
		});
		selectionPane.add(confirm, 0, 2);
		
		// si crea anche un bottone per tornare indietro alla home
		Button back = new Button("Indietro");
		back.setOnAction(e -> { 
			if (clientSocket != null) {
				
				try {
					// se è in corso una comunicazione col server, si notifica che si sta tornando alla home
					out.writeObject("ABORT");
				} catch (IOException e1) {
					
					connErr.show();
				}
		//		mainStage.setScene(homeScene);
				
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
		mainStage.setScene(selectionScene);
		mainStage.show();
	}

}
