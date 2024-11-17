import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) throws IOException {
        // STEP1: Get the host and the port from the command-line
        String hostName = "localhost";
        int portNumber = 8080; //Client port
        int portNumberServer = 5555; //Server port
        String CHARSET = "UTF8";
        boolean kick = false;


        // STEP2: Open a client socket, blocking while connecting to the server
        Socket clientSocket = new Socket(hostName, portNumberServer);

        // STEP3: Setup input and output streams
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        System.out.println("Connected");


        //Username
        System.out.println("Welcome! Choose your nickname:");
        Scanner scanner = new Scanner(System.in);
        String nameCheck = scanner.next();
        while (nameCheck == "" || nameCheck == "\n") {
            scanner = new Scanner(System.in);
            nameCheck = scanner.next();
            System.out.println("Insert valid username");
        }
        String name = nameCheck;
        System.out.println("You are now known as " + name);
        out.println(name);

        //STEP4: MAKE THEM THREADS WOOOOOOOO
        //THE SENDER/WRITER
        class ClientSender implements Runnable {

            @Override
            public void run() {
                while (true) {
                    if (kick) {
                        try {
                            out.close();
                            in.close();
                            clientSocket.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    //System.out.println("Enter the message");
                    Scanner scanner = new Scanner(System.in);
                    if (!kick) {
                        String message = scanner.nextLine();
                        out.println(message);
                        if ((message.equalsIgnoreCase("/quit"))) {
                            System.out.println("You left the chat");
                        }
                    }
                }
            }
        }
        ClientSender sender = new ClientSender();
        Thread t1 = new Thread(sender);
        t1.start();


        //THE READER/LISTENER
        while (true) {
            String serverMessage;
            try {
                serverMessage = in.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (serverMessage != null) {
                System.out.println(serverMessage);
            }
        }
    }
}
