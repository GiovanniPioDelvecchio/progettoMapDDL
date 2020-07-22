package server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.time.Instant;

import data.Data;
import data.TrainingDataException;
import tree.RegressionTree;


public class ServerOneClient extends Thread {

		private Socket socket;
		private ObjectInputStream in;
		private ObjectOutputStream out;
		
		public ServerOneClient(Socket s) throws IOException {
			socket = s;
			in = new ObjectInputStream(s.getInputStream());
			out = new ObjectOutputStream(s.getOutputStream());
			this.start();
		}
		
		public void run() {
				
			Integer clientDecision = null;
			try {
				
				clientDecision = (Integer)in.readObject();
				RegressionTree tree = null;

				// learn from database
				if(clientDecision==0) {

					Data trainingSet=null;
					
					try{
						String trainingfileName = (String)in.readObject();
						trainingSet = new Data(trainingfileName);
						tree = new RegressionTree(trainingSet);
						
						
						out.writeObject("OK");
						
						clientDecision = (Integer)in.readObject();
						
						// save tree
						if (clientDecision == 1) {
							
							try {
								
								tree.salva(trainingfileName+".dmp");
								
								out.writeObject("OK");
								
							} catch (IOException e) {
								
								System.out.println(e.toString());
							}
							
							
						} 
					} catch(TrainingDataException e) {

						out.writeObject(e.toString());
						return;
					}
				} 
				
				// learn from file
				if(clientDecision == 2) {
					
					Data trainingSet=null;
					try{
						String trainingfileName = (String)in.readObject();
						tree = RegressionTree.carica(trainingfileName);
					} catch(FileNotFoundException e) {
						
						out.writeObject(e.toString());
						return;
						
					} catch (IOException e) {
						
						out.writeObject(e.toString());
						return;
					
					} catch(ClassNotFoundException e) {
						
						out.writeObject(e.toString());
						return;
					} 
					
					out.writeObject("OK");
					
				}
				
				// da risp y in poi
				clientDecision = (Integer)in.readObject();
				
				while(clientDecision == 3) {
					
					try {
						out.writeObject(tree.predictClass(out, in));
						clientDecision = (Integer)in.readObject();
					} catch (UnknownValueException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						out.writeObject(e.toString());
						
					}
				}
			} catch (IOException e) {
				
				System.out.println(e);
				return;
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			} finally {
				
				try {
					
				// la chiusura corretta delle comunicazioni con il client viene indicata dall'invio del codice -1
					if(clientDecision != null && clientDecision == -1) {
						
						// log di chiusura
						System.out.println("Closing connection with " + socket + " at " + Instant.now());
						out.writeObject("OK");
					} else {
						
						System.out.println("Aborted connection with " + socket + " at " + Instant.now());
					}
					socket.close();
				} catch(IOException e) {

					System.out.println("Error closing connection with " + socket + " : " + e.getClass().getName()
							+ " : " + e.getMessage());
				}
			}
		}
}
