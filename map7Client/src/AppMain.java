import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.regex.Pattern;

import com.sun.corba.se.impl.encoding.CodeSetComponentInfo.CodeSetComponent;
import com.sun.org.apache.bcel.internal.classfile.Code;

import javafx.application.Application;
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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class AppMain extends Application {
	
	private Socket clientSocket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	// L'indirizzo ip di default e' quello di loopback, che porta al localhost
	private String ip = "127.0.0.1";
	private int PORT = 8080;
	
	private Scene settingsScene;

	public static void main(String[] args) {
		
		launch(args);
	}
	
	public void start(Stage mainStage) {
		
		/*
		 * Finestra delle impostazioni
		 */
		GridPane settingsPane = new GridPane();
		settingsScene = new Scene(settingsPane);
		settingsPane.setAlignment(Pos.CENTER);
		settingsPane.setHgap(10);
		settingsPane.setVgap(10);
		settingsPane.setPadding(new Insets(25, 50, 25, 25));
		
		Label ipLabel = new Label("Indirizzo IP");
		Label portLabel = new Label("Porta");
		
		/*
		 * TextField ipField = new TextField();
		 * ipField.setPromptText(ip);
		 */
		
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
		 * Imposto a 3 il numero di cifre inseribili in ogni textfield dell'indirizzo ip.
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
		portField.setPrefColumnCount(5);

		/*
		 * Con questa chiamata a funzione aggiorno i testi di prompt dei campi testuali
		 * con i valori correnti dell'indirizzo ip e della porta.
		 */
		updateSettingsPromptText(ipAdd, portField);

		/*
		 * Infine si utilizzano due bottoni, uno di conferma e uno per tornare alla home del programma.
		 */
		Button confirmButtonSettings = new Button("Conferma");
		HBox backLayoutSettings = new HBox();
		backLayoutSettings.setAlignment(Pos.CENTER_LEFT);
		// backLayoutSettings.getChildren().add(back);

		/*
		 * Dichiaro un oggetto di tipo EventHandler<ActionEvent>, il cui metodo handle descrive il comportamento
		 * di conferma alla pressione del bottone confirmButtonSettings (o alla pressione del tasto enter).
		 */
		EventHandler<ActionEvent> confirmEvent = new EventHandler<ActionEvent>() {

			public void handle(ActionEvent e) {
				
				/*
				 * Controllo se l'ip inserito e' un indirizzo ip valido.
				 * Se i field sono vuoti, si lascia il valore dell'ultimo indirizzo ip utilizzato.
				 * Se l'indirizzo ip inserito e' mal formattato, viene visualizzato un errore, e non
				 * si modifica l'indirizzo ip corrente.
				 */
				Alert confirmationError = new Alert(Alert.AlertType.ERROR);
				
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
		
						confirmationError.setContentText("L'indirizzo IP inserito non è valido.\n"
								+ "I valori che compongono l'indirizzo devono essere interi da 0 a 255.");
						confirmationError.show();
					}
				}
				
				/*
				 * Si parsifica il numero di porta inserito nel field associato. Se il numero di porta
				 * e' valido, si aggiorna l'attributo PORT, altrimenti viene lasciato invariato (e si visualizza
				 * un errore).
				 */
				String readPort = portField.getText();
				
				if (!readPort.equals("")) {
				
					final String errorMessage = "Il numero di porta inserito non è valido.\n"
							+ "Il numero di porta deve essere un intero fra 1 e 65535.";
					int intPort;
					try {

						intPort = Integer.parseInt(readPort);
						if (intPort > 0 && intPort <= 65535) {

							PORT = intPort;
						} else {

							confirmationError.setContentText(errorMessage);
							confirmationError.show();
						}
					} catch (NumberFormatException f) {

						confirmationError.setContentText(errorMessage);
						confirmationError.show();
					}
				}
				
				// Infine si aggiornano le stringhe di prompt dei campi testuali con i valori correnti di indirizzo ip e porta
				updateSettingsPromptText(ipAdd, portField);
			}
		};

		/*
		 * Imposto l'EventHandler confirmEvent come comportamento da assumere in caso di pressione di confirmButtonSettings
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
		 * Aggiungo i nodi alla griglia di layout.
		 */
		settingsPane.add(ipLabel, 1, 1);
		settingsPane.add(ipLayout, 2, 1);
		settingsPane.add(portLabel, 1, 2);
		settingsPane.add(portField, 2, 2);
		settingsPane.add(confirmButtonSettings, 1, 3);
		settingsPane.add(backLayoutSettings, 2, 3);
		
		
		// Test
		mainStage.setScene(settingsScene);
		mainStage.show();
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
		ipAdd[0].setPromptText(currIpString[0]);
		ipAdd[1].setPromptText(currIpString[1]);
		ipAdd[2].setPromptText(currIpString[2]);
		ipAdd[3].setPromptText(currIpString[3]);
		
		portField.setPromptText(new Integer(PORT).toString());
	}

	
}
