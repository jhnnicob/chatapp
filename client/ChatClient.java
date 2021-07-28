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

	void setUserName(String userName) {
		this.userName = userName;
	}

	String getUserName() {
		return this.userName;
	}

	void setConfMsg(String confMsg) {
		this.confMsg = confMsg;
	}

	String getConfMsg() {
		return this.confMsg;
	}

	public static void main(String[] args) {

		System.out.println("Enter the IP address followed by port number");
		System.out.println("i.e: <IP address> <port number>");

		Scanner scanner = new Scanner(System.in);
		String socketAddress = scanner.nextLine();

		String[] tokens = socketAddress.split(" ");
		String ipAddress = "";
		int portNo = 0;
		if (tokens != null && tokens.length > 0) {
			ipAddress = tokens[0];
			portNo = Integer.parseInt(tokens[1]);
		}

		ChatClient client = new ChatClient(ipAddress, portNo);
		client.execute();
	}
}