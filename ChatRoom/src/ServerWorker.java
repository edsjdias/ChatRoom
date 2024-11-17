import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class ServerWorker implements Runnable {

    private final PrintWriter out;
    private final BufferedReader in;
    private final List server;
    private Socket clientSocket;
    private String id;
    public boolean kick = false;


    public ServerWorker(List server, Socket clientSocket, PrintWriter out, BufferedReader in) {
        this.clientSocket = clientSocket;
        this.out = out;
        this.in = in;
        this.server = server;
        //System.out.println("Worker initialized");
    }

    @Override
    public void run() {
        String clientName;
        try {
            System.out.println("Getting name...");
            clientName = in.readLine();
            if (clientName == null) {
                try {
                    out.close();
                    in.close();
                    clientSocket.close();
                    System.out.println("Client manually terminated connection");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                id = clientName;
                System.out.println(clientName + " has entered the chat!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (clientName != null) {
            id = clientName;
            for (int i = 0; i < server.size(); i++) {
                ServerWorker sendTo = (ServerWorker) server.get(i);
                sendTo.out.println(clientName + " has entered the chat!");
            }
            while (true) {
                String clientMessage = null;
                if (kick) {
                    clientMessage = null;
                    System.out.println("Client kicked");
                    try {
                        out.close();
                        in.close();
                        clientSocket.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                } else {
                    try {
                        //System.out.println("I'm running");
                        clientMessage = in.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (clientMessage == null) {
                        System.out.println(clientName + " connection lost!");
                        for (int i = 0; i < server.size(); i++) {
                            ServerWorker sendTo = (ServerWorker) server.get(i);
                            sendTo.out.println(clientName + " connection lost!");
                        }
                        break;
                    }
                    //SENDING YALL MESSAGES
                    if (!kick) {
                        if (clientMessage.toLowerCase().indexOf(("/kick")) == -1) {
                            System.out.println(clientName + ": " + clientMessage);
                            for (int i = 0; i < server.size(); i++) {
                                ServerWorker sendTo = (ServerWorker) server.get(i);
                                sendTo.out.println(clientName + ": " + clientMessage);
                            }
                        }
                    }
                    //QUITTING
                    if ((clientMessage.equalsIgnoreCase("/quit"))) {
                        System.out.println(clientName + " has disconnected!");
                        for (int i = 0; i < server.size(); i++) {
                            ServerWorker sendTo = (ServerWorker) server.get(i);
                            sendTo.out.println(clientName + " has disconnected!");
                        }
                        try {
                            out.close();
                            in.close();
                            clientSocket.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    //KICKING
                    if (clientMessage.toLowerCase().indexOf(("/kick")) != -1) {
                        String kicked = clientMessage.replaceFirst("/kick", ""); //gets the name
                        kicked = kicked.replaceFirst("^\\s*", "");
                        for (int i = 0; i < server.size(); i++) {
                            ServerWorker sendTo = (ServerWorker) server.get(i);
                            if (sendTo.id.equalsIgnoreCase(kicked)) {
                                sendTo.kick = true;
                                sendTo.out.println("You were kicked");
                                server.remove(sendTo);
                                //sendTo.out.println("kick");
                            }
                            if (sendTo.kick != true) {
                                sendTo.out.println(kicked + " was kicked by " + clientName + "!");
                            }
                        }
                        System.out.println(clientName + " kicked " + kicked + "!");
                    }
                }
            }
        } else try {
            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
