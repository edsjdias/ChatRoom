import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    public static void main(String[] args) throws IOException {

        // STEP1: Get parameters from command line arguments
        int portNumber = 5555;
        System.out.println("Waiting for client...");
        ServerSocket serverSocket = new ServerSocket(portNumber);
        List<Socket> clients = new ArrayList<>();
        List<ServerWorker> threads = new ArrayList<>();
        //List<String> messages = new ArrayList<>();

        while (true) {
            // STEP2: LETS CONNECT PEOPLEEEEE
            Socket clientSocket = serverSocket.accept();
            if (!clients.contains(clientSocket)) {
                clients.add(clientSocket);

                // STEP3: Setup input and output streams
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                System.out.println("Connecting to client...");
                ServerWorker worker = new ServerWorker(threads, clientSocket, out, in);
                threads.add(worker);
                Thread t1 = new Thread(worker);
                t1.start();
            }
        }
    }
}