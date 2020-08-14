import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Classe per modellare le informazioni di connessione ad un server
 * a cui si puo' connettere un client. 
 * 
 * @author Domenico Dell'Olio, Giovanni Pio Delvecchio, Giuseppe Lamantea
 *
 */
class ServerInformation implements Serializable {

	private String ip;
	private int port;
	private String id;
	
	ServerInformation(String newIp, int newPort, String newId) {
		
		ip = newIp;
		port = newPort;
		id = newId;
	}
	
	String getIp() { return ip; }
	int getPort() { return  port; }
	String getId() { return id; }
	
	void Save() throws FileNotFoundException, IOException {
		
		FileOutputStream out = new FileOutputStream(this.id + ".info");
		ObjectOutputStream outStream = new ObjectOutputStream(out);
		outStream.writeObject(this);
		outStream.close();
		out.close();
	}
	
	static ServerInformation Load(String id) throws FileNotFoundException, IOException, ClassNotFoundException {
		
		FileInputStream in = new FileInputStream(id + ".info");
		ObjectInputStream inStream = new ObjectInputStream(in);
		ServerInformation loaded = (ServerInformation) inStream.readObject();
		inStream.close();
		in.close();
		
		return loaded;
	}
}
