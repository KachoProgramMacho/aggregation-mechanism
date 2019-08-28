import javafx.util.Pair;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class DeciderServer {

    ServerSocket serverSocket;
    MonitorThread monitorThread;
    DataCollector dataCollector;
    ArrayList<ServerConnection> connections;
    boolean continueRunning = true;



    public static void main(String[] args){
        new DeciderServer();
    }

    public DeciderServer(){
        try {
            connections = new ArrayList<ServerConnection>();
            dataCollector = new DataCollector();

            // start monitoring
            monitorThread = new MonitorThread(dataCollector);
            monitorThread.start();

            // await socket connection
            serverSocket = new ServerSocket(7000);
            while(continueRunning){
                System.out.println("Waiting for new connections.");
                Socket s = serverSocket.accept();
                ServerConnection newConnection = new ServerConnection(s,dataCollector);
                newConnection.start();
                connections.add(newConnection);
                System.out.println("Accepted connection! Currently open connections: " + connections.size());
        }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
