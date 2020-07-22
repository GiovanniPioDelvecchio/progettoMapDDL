

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import utility.Keyboard;


public class MainTest {

	public static void main(String[] args){
		
		// addr viene dichiarata ma poi viene utilizzato direttamente args[0] nel costruttore di Socket(?)
		InetAddress addr;
		try {
			
			addr = InetAddress.getByName(args[0]);
		} catch (UnknownHostException e) {
			
			System.out.println(e.toString());
			return;
			
		}
		Socket socket=null;
		ObjectOutputStream out=null;
		ObjectInputStream in=null;
		try {
			
			socket = new Socket(args[0], Integer.parseInt(args[1]));
			System.out.println(socket);		
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());		// stream con richieste del client
			
		}  catch (IOException e) {
			
			System.out.println(e.toString());
			return;
		}

		String answer="";
		
		int decision=0;
		do{
		
			System.out.println("Learn Regression Tree from data [1]");
			System.out.println("Load Regression Tree from archive [2]");
			decision=Keyboard.readInt();
		}while(!(decision==1) && !(decision ==2));
		
		String tableName="";
		System.out.println("File name:");
		tableName=Keyboard.readString();
		try{
		
			if(decision==1) {
				System.out.println("Starting data acquisition phase!");
				
				
				out.writeObject(0);
				out.writeObject(tableName);
				answer=in.readObject().toString();
				
				if(!answer.equals("OK")) {
					System.out.println(answer);
					return;
				}
					
			
				System.out.println("Starting learning phase!");
				out.writeObject(1);
				
			
			} else {
				
				out.writeObject(2);
				out.writeObject(tableName);	
			}
			
			answer=in.readObject().toString();
			
			if(!answer.equals("OK")) {
				System.out.println(answer);
				return;
			}
	
			char risp='y';
			
			do {
				out.writeObject(3);
				
				System.out.println("Starting prediction phase!");
				answer=in.readObject().toString();
			
				
				while(answer.equals("QUERY")){
					// Formualting query, reading answer
					answer=in.readObject().toString();
					System.out.println(answer);
					int path=Keyboard.readInt();
					out.writeObject(path);
					answer=in.readObject().toString();
				}
			
				// Reading prediction
				if(answer.equals("OK")) {
					answer=in.readObject().toString();
					System.out.println("Predicted class:"+answer);
					
				}
				else {
	
					// Printing error message
					System.out.println(answer);
				}
			
				System.out.println("Would you repeat ? (y/n)");
				risp=Keyboard.readChar();
					
			} while(Character.toUpperCase(risp)=='Y');
		}
		catch(IOException | ClassNotFoundException e){
			System.out.println(e.toString());
			
		} finally {
			
			try {

				// invio il codice -1 al server per indicare la chiusura delle comunicazioni
				out.writeObject(-1);
				if(in.readObject().toString().equals("OK")) {

					socket.close();
				}
			} catch (IOException | ClassNotFoundException e) {

				System.out.println("Error closing connection with the server: " + e.getClass().getName() + " : " + e.getMessage());
			}
		}
	}

}
