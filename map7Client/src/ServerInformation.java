import java.io.Serializable;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Classe per modellare le informazioni di connessione ad un server
 * a cui si puo' connettere un client. 
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
@SuppressWarnings("serial")
class ServerInformation implements Serializable {

	/*
	 * Gli attributi sono trattati come implementazioni Property, per facilitare
	 * la costruzione della tabella dei server conosciuti
	 */
	private SimpleStringProperty ip;
	private SimpleIntegerProperty port;
	private SimpleStringProperty id;
	
	ServerInformation(String newIp, int newPort, String newId) {
		
		ip = new SimpleStringProperty(newIp);
		port = new SimpleIntegerProperty(newPort);
		id = new SimpleStringProperty(newId);
	}
	
	String getIp() { return ip.get(); }
	int getPort() { return  port.get(); }
	String getId() { return id.get(); }
	
	public String toString() {
		
		return "Id: " + id + " Ip: " + ip + " Porta: " + port;
	}
}
