import java.net.*;
import java.util.Scanner;
import java.io.*;

public class ChatClient {

	private String ipAddress;
	private int portNo;
	private String userName;
	private String confMsg;

	public ChatClient(String ipAddress, int portNo) {
		this.ipAddress = ipAddress;
		this.portNo = portNo;
	}

	/**
	 * wrap the execution of the socket inside this method
	 */
	public void execute() {
		try {
			Socket socket = new Socket(ipAddress, portNo); // for connecting to the server

			System.out.println("Connected to the chat server");
			DataInputStream reader = new DataInputStream(socket.getInputStream());
			DataOutputStream writer = new DataOutputStream(socket.getOutputStream());

			new WriteThread(this, socket, writer).start();
			new ReadThread(this, socket, reader).start();

		} catch (UnknownHostException ex) {
			System.out.println("Server not found: " + ex.getMessage());
		} catch (IOException ex) {
			System.out.println("I/O Error: " + ex.getMessage());
		}
	}

	/**
	 * Set username
	 */
	void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Get username
	 */
	String getUserName() {
		return this.userName;
	}

	public static void main(String[] args) {

		System.out.println("Enter the IP address followed by port number");
		System.out.println("Usage: ChatClient <IP address> <port number>");

		Scanner scanner = new Scanner(System.in);
		String socketAddress = scanner.nextLine();

		String[] tokens = socketAddress.split(" ");
		String ipAddress = "";
		int portNo = 0;
		if (tokens != null && tokens.length > 0) {
			ipAddress = tokens[0];
			portNo = Integer.parseInt(tokens[1]);
		}

		// Create an instance of the ChatClient class
		ChatClient client = new ChatClient(ipAddress, portNo);
		// use the instance to execute the socket in execute method
		client.execute();
	}
}