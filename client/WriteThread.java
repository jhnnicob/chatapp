import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This thread is responsible for reading user's/ client's input and send it to
 * the server. It runs in an infinite loop until the user types 'bye' to quit.
 *
 */

public class WriteThread extends Thread {

    private DataOutputStream writer;
    private Socket socket;
    private ChatClient client;

    public WriteThread(ChatClient client, Socket socket, DataOutputStream writer) {
        this.socket = socket;
        this.client = client;
        this.writer = writer;
    }

    public void run() {

        try {

            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your name: ");

            String userName = scanner.nextLine();
            writer.writeUTF(userName); // send user to the server
            client.setUserName(userName);

            String text;

            while (true) {
                System.out.print("[" + userName + "]:");
                text = scanner.nextLine();
                if (!(text.equalsIgnoreCase("bye"))) {
                    writer.writeUTF(text); // it sends message to the server
                } else {
                    writer.writeUTF(text);
                    System.out.println("Connection terminated");
                    break;
                }
            }
            writer.close();
            socket.close();
        } catch (IOException ex) {
            System.out.println("Error writing to server: " + ex.getMessage());
        }
    }
}