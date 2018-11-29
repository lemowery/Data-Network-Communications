import java.io.*;
import java.net.*;
import java.util.*;

public final class WebServer {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		// Set port number
		int port = 6789;
		
		// Establish the listen socket
		ServerSocket welcomeSocket = new ServerSocket(6789);
		
		// Process the HTTP service requests in infinite loop
		while(true) {
			// Listen for a TCP connection request
			Socket connectionSocket = welcomeSocket.accept();
			
			// Construct an object to process the HTTP request message
			HttpRequest request = new HttpRequest(connectionSocket);
			
			// Create a new thread to process the request
			Thread thread  = new Thread(request);
			
			// Start the thread
			thread.start();
		}
	}

}
