import java.io.Serializable;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Classe per modellare le informazioni di connessione ad un server
 * a cui si puo' connettere un client. 
 * 
 * La classe ha visibilita' pubblica, nonostante sia utilizata solo nel package, poiche'
 * e' richiesto per creare strutture dati a partire da sue istanze tali che alla loro
 * modifica, vengano modificati anche elementi della GUI.
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
@SuppressWarnings("serial")
public class ServerInformation implements Serializable {

	/*
	 * Gli attributi sono trattati come implementazioni Property, dato che tale interfaccia
	 * descrive oggetti il cui cambiamento di stato si riflette su altre strutture relative
	 * all'interfaccia grafica.
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
	
	
	/*
	 * Dichiaro i metodi relativi agli attributi Property che ne permettono il recupero dall'esterno 
	 */
	public StringProperty idProperty() {
		
		return id;
	}
	
	public StringProperty ipProperty() {
		
		return ip;
	}

	public IntegerProperty portProperty() {
		
		return port;
	}
	
	public String toString() {
		
		return "Id: " + id + " Ip: " + ip + " Porta: " + port;
	}
	
	public boolean equals(Object other) {
		
		if (other instanceof ServerInformation) {
			
			return id.equals(((ServerInformation) other).id);
		} else {
			
			return false;
		}
	}
}
