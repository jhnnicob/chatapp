
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
	private Socket socket;
	private ServerSocket serverSocket;
	private int port;
	private Set<String> userNames = new HashSet<>();
	private Set<UserThread> userThreads = new HashSet<>();
	private List<String> logs = new ArrayList<String>();

	public ChatServer(int port) {
		this.port = port;
	}

	/**
	 * wrap the execution of the socket inside this method
	 */
	public void execute() throws IOException {

		serverSocket = new ServerSocket(port);

		System.out.println("Chat Server is listening on port " + port);

		try {
			while (true) {

				socket = serverSocket.accept();
				System.out.println("New user connected: " + socket.getRemoteSocketAddress());

				DataInputStream reader = new DataInputStream(socket.getInputStream());
				DataOutputStream writer = new DataOutputStream(socket.getOutputStream());

				UserThread newUser = new UserThread(this, socket, reader, writer);
				userThreads.add(newUser);
				newUser.start();
			}
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			serverSocket.close();
			socket.close();
		}

	}

	public static void main(String[] args) throws Exception {

		if (args.length < 1) {
			System.out.println("Syntax: java ChatServer <port-number>");
			System.exit(0);
		}

		int port = Integer.parseInt(args[0]);

		ChatServer server = new ChatServer(port);
		server.execute();

	}

	/**
	 * Delivers a message from one user to others (broadcasting)
	 */
	void broadcast(String message, UserThread excludeUser) throws IOException {
		for (UserThread aUser : userThreads) {
			if (aUser != excludeUser) {
				aUser.sendMessage(message);
			}
		}
	}

	void close() throws IOException {
		serverSocket.close();
	}

	/**
	 * Stores username of the newly connected client.
	 */
	void addUserName(String userName) {
		userNames.add(userName);
	}

	/**
	 * When a client is disconneted, removes the associated username and UserThread
	 */
	void removeUser(String userName, UserThread aUser) {
		boolean removed = userNames.remove(userName);
		if (removed) {
			userThreads.remove(aUser);
			System.out.println("The user " + userName + " quitted");
		}
	}

	/**
	 * get the user names of the clients
	 */
	Set<String> getUserNames() {
		return this.userNames;
	}

	/**
	 * Returns true if there are other users connected (not count the currently
	 * connected user)
	 */
	boolean hasUsers() {
		return !this.userNames.isEmpty();
	}

	/**
	 * get all logs
	 */
	List<String> getLogs() {
		return logs;
	}

	/**
	 * set logs into the array list
	 */
	void setLogs(String log) {
		this.logs.add(log);
	}

	/**
	 * Save all logs into a text file
	 */
	public void saveLogs() {

		FileOutputStream fos = null;
		File file;

		try {
			file = new File("serverlogs.txt");
			fos = new FileOutputStream(file);

			if (!file.exists()) {
				file.createNewFile();
			}

			byte[] bytesArray = this.logs.toString().replaceAll("(^\\[|\\]$)", "").getBytes();

			fos.write(bytesArray);
			fos.flush();

			if (fos != null) {
				fos.close();
			}

		} catch (IOException e) {
			// e.printStackTrace();
		} finally {
			System.out.println("Save logs successfully");
			System.out.println("Connection terminated");
		}
	}

}
