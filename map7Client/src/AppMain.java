
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
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
	private int PORT = 8080;
	
	
	public static void main(String[] args) {
		
		launch(args);
	}
	
	public void start(Stage mainStage) {
		mainStage.setTitle("Client");
		BorderPane pane = new BorderPane();
		
		
		//inizio descrizione componenti per la barra degli strumenti superiore
		
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
		//inizio descrizione componenti per la vbox centrale
		
		VBox centralPanel = new VBox(50);
		centralPanel.setAlignment(Pos.CENTER);
		Label sel = new Label("Seleziona un'operazione");
		Button load = new Button("Carica");
		load.setMinSize(130, 20);
		Button create = new Button("Crea");
		create.setMinSize(130, 20);
		centralPanel.getChildren().addAll(sel, load,create);
		
		pane.setCenter(centralPanel);
		
		//fine aggiunta componenti
		
		Scene homeScene = new Scene(pane, 400, 400);
		mainStage.setScene(homeScene);
		mainStage.show();
	}

}
