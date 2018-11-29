import java.io.*;
import java.net.Socket;
import java.util.*;

final class HttpRequest implements Runnable{
	
	final static String CRLF = "\r\n";
	Socket socket;
	
	// Constructor
	public HttpRequest(Socket socket) throws Exception {
		this.socket = socket;
	}
	
	
	// Implement run method
	public void run() {
		try {
			processRequest();
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
	
	private void processRequest() throws Exception {
		// Get a reference to the socket's input and output stream
		InputStream is = socket.getInputStream();
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());	
		
		// Setup input stream filters
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		
		// Get the request line of the HTTP request
		String requestLine = br.readLine();
		
		// Display the request line
		System.out.println();
		System.out.println(requestLine);
		
		// Get and display header lines
		String headerLine = null;
		while((headerLine = br.readLine()).length() != 0) {
			System.out.println(headerLine);
		}
		
		// Extract the filename from the request line
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken();
		String fileName = tokens.nextToken();
		
		// Prepend a '.' so the request in within the current directory
		fileName = "." + fileName;

		
		// Open the requested file
		FileInputStream fis = null;
		boolean fileExists = true;
		try {
			fis = new FileInputStream(fileName);
		}
		catch (FileNotFoundException e){
			fileExists = false;
		}
		
		// Construct the response message
		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;
		if (fileExists) {
			statusLine = "HTTP/1.1 200 OK ";
			contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
		}
		else {
			statusLine = "HTTP/1.1 404 NOT FOUND ";
			contentTypeLine = "Content-Type: text/html";
			entityBody = "";
		}
		
		// Send the status line
		os.writeBytes(statusLine);
		
		// Send the content type line
		os.writeBytes(contentTypeLine);
		
		// Send a blank line to indicate the end of the header lines
		os.writeBytes("\n");
		
		// Send the entity body
		if (fileExists) {
			sendBytes(fis, os);
			fis.close();
		}
		else {
			os.writeBytes(entityBody);
		}
		
		// Close streams and socket
		os.close();
		br.close();
		socket.close();
	}
	
	private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception{
		// Construct a 1K buffer to hold bytes on their way to the socket
		byte[] buffer = new byte [1024];
		int bytes = 0;
		
		// Copy requested file into the socket's output stream
		while ((bytes = fis.read(buffer)) != -1) {			
			os.write(buffer, 0, bytes);
		}
	}
	
	private static String contentType(String fileName) {
		if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
			return "text/html";
		}
		else {
			return "application/octet-stream";
		}
	}

}
