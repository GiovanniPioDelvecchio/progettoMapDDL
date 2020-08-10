import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class AppMain extends Application {
	
	private Socket clientSocket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String ip = "localhost";
	private int PORT = 8080;
	
	private Scene settingsScene;
	
	
	public static void main(String[] args) {
		
		launch(args);
	}
	
	public void start(Stage mainStage) {
		
		
		GridPane settingsPane = new GridPane();
		settingsScene = new Scene(settingsPane);
		settingsPane.setAlignment(Pos.CENTER);
		settingsPane.setHgap(10);
		settingsPane.setVgap(10);
		settingsPane.setPadding(new Insets(25, 50, 25, 25));
		
		Label ipLabel = new Label("Indirizzo IP");
		Label portLabel = new Label("Porta");
		
		TextField ipField = new TextField();
		ipField.setPromptText(ip);
		TextField portField = new TextField();
		portField.setPromptText(new Integer(PORT).toString());
		
		Button confirmButtonSettings = new Button("Conferma");
		HBox backLayoutSettings = new HBox();
		backLayoutSettings.setAlignment(Pos.CENTER_LEFT);
		// backLayoutSettings.getChildren().add(back);

		confirmButtonSettings.setOnAction( e -> {
			
			/*
			 * Controllo se l'ip inserito e' un indirizzo ip valido tramite un'espressione regolare
			 */
			String readIp = ipField.getText();
			String readPort = portField.getText();
			if (Pattern.matches("^(\\d{1,3}\\.){3}\\d{1,3}", readIp)) {

				ip = readIp;
			} else if (!(readIp.equals("localhost") || readIp.equals(""))) {
				
				Alert badIpFormat = new Alert(Alert.AlertType.ERROR);
				badIpFormat.setContentText("L'indirizzo IP inserito non è valido.");
				badIpFormat.show();
			}
			
			int intPort;
			try {

				intPort = Integer.parseInt(readPort);
				if (intPort > 0 && intPort < 65535) {
					
					PORT = intPort;
				} else {

					Alert invalidPort = new Alert(Alert.AlertType.ERROR);
					invalidPort.setContentText("Il numero di porta inserito non è valido.");
					invalidPort.show();
				}
			} catch(NumberFormatException f) {
				
				Alert badPortFormat = new Alert(Alert.AlertType.ERROR);
				badPortFormat.setContentText("La porta inserita non è valida.");
				badPortFormat.show();
			}
		});
		
		settingsPane.add(ipLabel, 1, 1);
		settingsPane.add(ipField, 2, 1);
		settingsPane.add(portLabel, 1, 2);
		settingsPane.add(portField, 2, 2);
		settingsPane.add(confirmButtonSettings, 1, 3);
		settingsPane.add(backLayoutSettings, 2, 3);
		
		
		// Test
		mainStage.setScene(settingsScene);
		mainStage.show();
	}

}
