import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
		
		
	}

}
