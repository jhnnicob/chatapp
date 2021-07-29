
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
	private Socket socket;
	private int port;
	private Set<String> userNames = new HashSet<>();
	private Set<UserThread> userThreads = new HashSet<>();
	private List<String> logs = new ArrayList<String>();

	public ChatServer(int port) {
		this.port = port;
	}

	public void execute() throws IOException {

		ServerSocket serverSocket = new ServerSocket(port);

		System.out.println("Chat Server is listening on port " + port);

		while (true) {
			try {
				socket = serverSocket.accept();
				System.out.println("New user connected: " + socket.getRemoteSocketAddress());

				DataInputStream reader = new DataInputStream(socket.getInputStream());
				DataOutputStream writer = new DataOutputStream(socket.getOutputStream());

				UserThread newUser = new UserThread(this, socket, reader, writer);
				userThreads.add(newUser);
				newUser.start();

			} catch (Exception e) {
				serverSocket.close();
				socket.close();
				e.printStackTrace();
			}
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
	 * Save all the logs into a text file
	 */

	List<String> getLogs() {
		return logs;
	}

	void setLogs(String log) {
		this.logs.add(log);
		System.out.println(this.logs);
	}

	public synchronized void saveLogs() {

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

			System.out.println("File Written Successfully");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				System.out.println("This is the logs.");
				if (fos != null) {
					fos.close();
				}
			} catch (IOException ioe) {
				System.out.println("Error in closing the Stream");
				ioe.printStackTrace();
			}
		}

	}

}
