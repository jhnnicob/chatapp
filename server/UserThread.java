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

    public UserThread(ChatServer server, Socket socket, DataInputStream reader, DataOutputStream writer) {
        this.socket = socket;
        this.server = server;
        this.reader = reader;
        this.writer = writer;
    }

    public void run() {

        try {

            String userName = reader.readUTF(); // get user from the client

            server.addUserName(userName);

            String serverMessage = "New user connected: " + userName;

            server.broadcast(serverMessage, this);

            String clientMessage;

            while ((clientMessage = reader.readUTF()) != null) {
                if (!(clientMessage.equalsIgnoreCase("bye"))) {

                    serverMessage = "[" + userName + "]: " + clientMessage;
                    server.broadcast(serverMessage, this);

                    String destination = getDestination(userName).toString().replaceAll("(^\\[|\\]$)", "");
                    String to = !destination.isEmpty() ? " To " + destination + ": " : ": ";
                    String date = new Date().toString();

                    String logs = "\n" + date + " " + userName + to + clientMessage;
                    System.out.println(logs);

                    this.logsList.add(logs);

                } else {
                    server.removeUser(userName, this);
                    serverMessage = userName + " has quitted.";
                    server.broadcast(serverMessage, this);

                    Scanner scanner = new Scanner(System.in);
                    System.out.println("Do you want to save logs?(Y/N) ");
                    String txtLogs = scanner.nextLine();
                    if (txtLogs.equalsIgnoreCase("y")) {
                        saveLogs();
                    }
                    scanner.close();
                    socket.close();
                    break;
                }
            }

        } catch (

        IOException ex) {
            System.out.println("Error in user thread " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Sends a message to the client.
     */
    void sendMessage(String message) throws IOException {
        writer.writeUTF(message);
    }

    Set<String> getDestination(String currentUser) {
        for (String username : server.getUserNames()) {
            if (username != currentUser) {
                usernames.add(username);
            }
        }
        return usernames;
    }

    public synchronized void saveLogs() {
        File file;
        FileOutputStream fos = null;
        System.out.println("This is the logs.");

        try {

            file = new File("serverlogs.txt");
            fos = new FileOutputStream(file, true);

            if (!file.exists()) {
                file.createNewFile();
            }

            byte[] bytesArray = this.logsList.toString().replaceAll("(^\\[|\\]$)", "").getBytes();

            fos.write(bytesArray);
            fos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ioe) {
                System.out.println("Error in closing the Stream");
            }
        }
    }
}
