
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;

public class AppMain extends Application {
	
	private Socket clientSocket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String ip = "localhost";
	private int port = 8080;
	private String dataName = "servo";
	
	public static void main(String[] args) {
		
		launch(args);
	}
	
	public void start(Stage mainStage) {
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
		 * descrizione componenti per la vbox centrale
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
		
		load.setOnAction(e->{
			
			tryConnection();
			loadFromFile();
		
		});
		/*
		 * Se il tasto "Crea" viene premuto, ci si connette al server e si comunica di caricare
		 * da un database la tabella da cui verrà ricavato l'albero di regressione
		 */
		create.setOnAction(e->{
				
			tryConnection();
			createFromDB();
		});
		
		/*
		 * chiamata ai metodi per mostrare la scena principale
		 */
		
		
		Scene homeScene = new Scene(pane, 400, 400);
		mainStage.setScene(homeScene);
		mainStage.show();
	}

	
	void tryConnection() {
		
		try {
			clientSocket = new Socket(ip, port);
			System.out.println(clientSocket);		
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			in = new ObjectInputStream(clientSocket.getInputStream());
			
		} catch(IOException e) {
			
			/*
			 * Se qualcosa va storto con la creazione del collegamento si mostra un alert
			 */
			showAlert("Non è stato possibile connettersi al server selezionato");
		}
	}
	
	void loadFromFile() {
		
		try {
			
			out.writeObject(2);
			out.writeObject(dataName);
			
		} catch(IOException e) {
			
			/*
			 * Se qualcosa va storto con l'invio dei messaggi al server si mostra un alert
			 */

			showAlert("Non è stato possibile comunicare l'operazione al server selezionato");
		}	
		
	}
	
	void createFromDB() {
		
		try {
			
			out.writeObject(0);
			out.writeObject(dataName);
			
		} catch(IOException e) {
			
			/*
			 * Se qualcosa va storto con l'invio dei messaggi al server si mostra un alert
			 */
			
			showAlert("Non è stato possibile comunicare l'operazione al server selezionato");
		}
		
	}
	
	void showAlert(String message) {
		
		
		/*
		 * Se qualcosa va storto è possibile mostrare una finestra di dialogo con un messaggio
		 */
		
		Alert toShow = new Alert(Alert.AlertType.ERROR);
		toShow.setContentText(message);
		toShow.show();
	}
}
