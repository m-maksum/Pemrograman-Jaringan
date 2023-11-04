import java.io.*;
import java.net.*;

public class ChatClient {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Hint: java ChatClient (username)");
            return;
        }

        String username = args[0];

        try {
            Socket socket = new Socket("localhost", 12345); // Connect to the server on the same machine
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Create a separate thread for reading messages from the server
            Thread readerThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                        if (message.equalsIgnoreCase("/exit")) {
                            System.out.println("Client has left the chat");
                            System.exit(0);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            readerThread.start();

            // Notify the client that it has successfully joined the chat
            System.out.println("You have joined the chat as: " + username);

            String input;
            while (true) {
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                input = userInput.readLine();
                if (input.equalsIgnoreCase("quit")) {
                    out.println("/exit"); // Send /exit to the server
                    System.exit(0);
                }
                out.println(username + ": " + input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
