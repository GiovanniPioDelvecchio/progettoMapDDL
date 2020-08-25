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
 * La classe e' di sola lettura, e la sua controparte modificabile e' <code>MutableServerInformation</code>.
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
@SuppressWarnings("serial")
public class ServerInformation {

	/*
	 * Gli attributi sono trattati come implementazioni Property, dato che tale interfaccia
	 * descrive oggetti il cui cambiamento di stato si riflette su altre strutture relative
	 * all'interfaccia grafica.
	 */
	private SimpleStringProperty ip;
	private SimpleIntegerProperty port;
	private SimpleStringProperty id;
	
	/**
	 * Costruttore di <code>ServerInformation</code>.
	 * 
	 * @param newIp Stringa contenente un indirizzo ip valido a cui il client puo' connettersi.
	 * @param newPort Intero rappresentante la porta a cui il client dovra' connettersi.
	 * @param newId Stringa identificatrice della coppia Indirizzo Ip, Porta
	 */
	ServerInformation(String newIp, int newPort, String newId) {
		
		ip = new SimpleStringProperty(newIp);
		port = new SimpleIntegerProperty(newPort);
		id = new SimpleStringProperty(newId);
	}
	
	/**
	 * Getter per l'indirizzo Ip del server.
	 * 
	 * @return L'indirizzo Ip del server rappresentato dall'oggetto.
	 */
	String getIp() {

		return ip.get();
	}
	
	/**
	 * Getter per la porta del server a cui connettersi.
	 * 
	 * @return Il numero di porta associato all'indirizzo Ip del server.
	 */
	int getPort() {
		
		return  port.get();
	}
	
	/**
	 * Getter per l'identificatore del server.
	 * 
	 * @return Una stringa contenente l'identificatore del server rappresentato dall'oggetto.
	 */
	String getId() {
		
		return id.get();
	}
	
	
	/**
	 * Metodo che permette l'accesso in tempo reale da parte degli elementi di interfaccia
	 * grafica all'identificatore del server.
	 * 
	 * @return L'identificatore del server modellato dall'oggetto.
	 */
	public StringProperty idProperty() {
		
		return id;
	}
	
	/**
	 * Metodo che permette l'accesso in tempo reale all'attributo rappresentante
	 * l'indirizzo ip del server.
	 * 
	 * @return L'indirizzo IP del server modellato dall'oggetto.
	 */
	public StringProperty ipProperty() {
		
		return ip;
	}

	/**
	 * Metodo che permette l'accesso in tempo reale all'attributo rappresentante
	 * la porta del server.
	 * 
	 * @return Il numero di porta associato al server modellato dall'oggetto.
	 */
	public IntegerProperty portProperty() {
		
		return port;
	}
	
	/**
	 * Sovrascrittura del metodo <code>equals</code>.
	 * 
	 * Due istanze di <code>ServerInformation</code> sono considerate uguali quando
	 * sono identificate dalla stessa stringa.
	 * 
	 * @return Vero se l'istanza dell'oggetto e' uguale a <code>other</code>, falso altrimenti.
	 */
	@Override
	public boolean equals(Object other) {
		
		if (other instanceof ServerInformation) {
			
			return id.get().equals(((ServerInformation) other).id.get());
		} else {
			
			return false;
		}
	}
	
	/**
	 * Metodo utilizzato per convertire l'istanza corrente di <code>ServerInformation</code> in
	 * un oggetto modificabile istanza di <code>MutableServerinformation</code>.
	 * 
	 * @return Un oggetto di classe <code>MutableServerInformation</code> contenente le informazioni
	 * analoghe a quelle memorizzate nell'oggetto corrente.
	 */
	MutableServerInformation toMutableServerInformation() {
		
		return new MutableServerInformation(ip.get(), port.get(), id.get());
	}
}
