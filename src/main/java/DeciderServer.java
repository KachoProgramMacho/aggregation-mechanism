import javafx.util.Pair;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class DeciderServer {

    ServerSocket serverSocket;
    MonitoringService monitoringService;
    DataCollector dataCollector;
    ArrayList<ServerConnection> connections;



    boolean continueRunning = true;
    final int calculateVarianceWindowSize = 10;
    final int monitoringIntervalInMilli = 1000;
    final int movingAverageWindowSize = 3;


    public static void main(String[] args){
        new DeciderServer();
    }

    public DeciderServer(){
        try {
            connections = new ArrayList<ServerConnection>();
            dataCollector = new DataCollector(calculateVarianceWindowSize);

            // start monitoring
            monitoringService = new MonitoringService(dataCollector,monitoringIntervalInMilli,movingAverageWindowSize);
            monitoringService.startMonitoring();

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
