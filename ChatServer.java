import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private static List<PrintWriter> clientWriters = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(12345); // Choose a port
            System.out.println("Chat Server is running on port 12345...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                clientWriters.add(out);

                new Thread(new ClientHandler(clientSocket, out)).start(); //ini memanggil line 29 -> 33

                // Notify the server (customize this message)
                System.out.println("Client has joined the chat");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;

        public ClientHandler(Socket socket, PrintWriter out) {
            this.socket = socket;
            this.out = out;
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("/exit")) {
                        // Notify all clients that this client is leaving
                        broadcastMessage("Client has left the chat");
                        break;
                    }
                    broadcastMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                clientWriters.remove(out);
            }
        }
    }

    private static void broadcastMessage(String message) {
        for (PrintWriter writer : clientWriters) {
            writer.println(message);
        }
    }
}
