import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This thread handles connection for each connected client, so the server can
 * handle multiple clients at the same time.
 *
 */

public class UserThread extends Thread {
    private Socket socket;
    private ChatServer server;
    private DataInputStream reader;
    private DataOutputStream writer;
    private Set<String> usernames = new HashSet<>();
    List<String> logsList = new ArrayList<String>();
    private Set<String> destination = new HashSet<String>();

    public UserThread(ChatServer server, Socket socket, DataInputStream reader, DataOutputStream writer) {
        this.socket = socket;
        this.server = server;
        this.reader = reader;
        this.writer = writer;
    }

    public void run() {

        try {

            String userName = reader.readUTF(); // get user from the client
            this.destination = getDestination(userName);
            server.addUserName(userName);
            String serverMessage = "New user connected: " + userName;
            server.broadcast(serverMessage, this);

            String clientMessage;
            while ((clientMessage = reader.readUTF()) != null) {

                if (clientMessage.equalsIgnoreCase("bye")) {

                    server.removeUser(userName, this);
                    serverMessage = userName + " has quitted.";
                    server.broadcast(serverMessage, this);

                    if (server.getUserNames().isEmpty()) {
                        Scanner scanner = new Scanner(System.in);
                        System.out.print("Do you want to save the logs?(Y/N): ");
                        String text = scanner.nextLine();

                        if (text.equalsIgnoreCase("y")) {
                            server.saveLogs();
                            server.close();
                            break;
                        } else if (text.equalsIgnoreCase("n")) {
                            server.close();
                            break;
                        }
                        scanner.close();
                    } else {
                        break;
                    }

                } else {

                    serverMessage = "[" + userName + "]: " + clientMessage;
                    server.broadcast(serverMessage, this);

                    this.destination = getDestination(userName);

                    String strDestination = destination.toString().replaceAll("(^\\[|\\]$)", "");
                    String date = new Date().toString();
                    String to = !destination.isEmpty() ? " To " + strDestination + ": " : ": ";
                    String logs = "\n" + date + " " + userName + to + clientMessage;

                    System.out.print(logs);

                    server.setLogs(logs);

                }
            }

        } catch (SocketException se) {
            // se.printStackTrace();
        } catch (IOException ex) {
            // ex.printStackTrace();
        }
    }

    /**
     * Sends a message to the client.
     */
    void sendMessage(String message) throws IOException {
        writer.writeUTF(message);
    }

    /**
     * Get the destination or user that the message is going to
     */
    Set<String> getDestination(String currentUser) {
        for (String strUserName : server.getUserNames()) {
            if (strUserName != currentUser) {
                usernames.add(strUserName);
            }
        }
        return usernames;
    }
}
