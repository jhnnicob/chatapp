import java.io.*;
import java.net.*;

/**
 * This thread is responsible for reading server's input and printing it to the
 * console. It runs in an infinite loop until the client disconnects from the
 * server.
 *
 */

public class ReadThread extends Thread {

    private DataInputStream reader;
    private Socket socket;
    private ChatClient client;

    public ReadThread(ChatClient client, Socket socket, DataInputStream reader) {
        this.socket = socket;
        this.client = client;
        this.reader = reader;
    }

    public void run() {

        String response;

        try {

            while ((response = reader.readUTF()) != null) {
                System.out.println("\n" + response);

                // prints the username after displaying the server's message
                if (client.getUserName() != null) {
                    System.out.print("[" + client.getUserName() + "]: ");
                }
            }

            reader.close();
            socket.close();

        } catch (IOException ex) {
            // ex.printStackTrace();
        }
    }
}
