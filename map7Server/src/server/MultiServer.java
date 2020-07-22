package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;

public class MultiServer {

private int PORT = 8080;
	
	public MultiServer(int port) {
		PORT = port;
		try {
			
			this.run();
			System.out.println("Started server at" + Instant.now());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private void run() throws IOException {
		ServerSocket ssocket = new ServerSocket(PORT);
		try {
			while(true) {
				
				Socket csocket = ssocket.accept();
				try {

					new ServerOneClient(csocket);
				} catch (IOException e) {

					csocket.close();
					System.out.println("Connection to client failed :" + e.getMessage());
				}
			}
		} finally {
			ssocket.close();
		}
	}
	
}
