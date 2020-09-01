import java.io.Serializable;

/**
 * Controparte modificabile di ServerInformation.
 * 
 * @see ServerInformation
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
@SuppressWarnings("serial")
class MutableServerInformation implements Serializable {

	/**
	 * Identificativo del server
	 */
	private String id;
	
	/**
	 * Indirizzo IP del server
	 */
	private String ip;
	
	/**
	 * Porta su cui risiede il server
	 */
	private Integer port;

	/**
	 * Costruttore di MutableServerInformation.
	 * 
	 * @param newIp Indirizzo Ip da memorizzare.
	 * @param newPort Porta da associare all'indirizzo IP.
	 * @param newId Identificatore del server.
	 */
	MutableServerInformation(String newIp, int newPort, String newId) {
		
		id = newId;
		ip = newIp;
		port = newPort;
	}
	
	/**
	 * Getter per l'identificatore del server.
	 * 
	 * @return Una stringa contenente l'identificatore del server.
	 */
	String getId() {
		
		return id;
	}
	
	/**
	 * Getter per l'indirizzo ip del server.
	 * 
	 * @return Una stringa contennete l'indirizzo ip del server rappresentato dall'oggetto.
	 */
	String getIp() {
		
		return ip;
	}
	
	/**
	 * Getter per la porta su cui connettersi al server.
	 * 
	 * @return La porta del server a cui connettersi.
	 */
	Integer getPort() {
		
		return port;
	}
	
	/**
	 * Setter per l'identificatore del server.
	 * 
	 * @param newId Nuovo identificatore del server.
	 */
	void setId(String newId) {
		
		id = newId;
	}
	
	/**
	 * Setter per l'indirizzo Ip del server.
	 * 
	 * @param newIp Nuovo indirizzo Ip del server.
	 */
	void setIp(String newIp) {
		
		ip = newIp;
	}
	
	/**
	 * Setter per la porta su cui effettaure la connessione al server.
	 * 
	 * @param newPort Nuovo numero di porta del server a cui connettersi.
	 */
	void setPort(int newPort) {
		
		port = newPort;
	}
	
	/**
	 * Metodo utilizzato per convertire l'istanza corrente di MutableServerInformation
	 * in un'istanza di ServerInformation.
	 * 
	 * @return Un'istanza di ServerInformation contenente le informazioni analoghe a quelle
	 * 		   memorizzate nell'oggetto corrente.
	 */
	ServerInformation toServerInformation() {
		
		return new ServerInformation(ip, port, id);
	}
}
