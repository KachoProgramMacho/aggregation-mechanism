import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class DeciderServer {

    ServerSocket serverSocket;
    MonitoringService monitoringService;
    DataCollector dataCollector;
    ArrayList<ServerConnection> connections;



    private boolean continueRunning = true;
    private int port = 7000;

    //DataCollector config constants
    private final int calculateVarianceWindowSize = 10;
    private final int histogramBucketSize = 5000;

    //Monitoring config constants
    private final int monitoringIntervalInMilli = 1000;
    private final int movingAverageWindowSize = 3;
    private final double varianceThreshold = 1000.0;


    public static void main(String[] args){
        new DeciderServer();
    }

    public DeciderServer(){
        try {
            connections = new ArrayList<ServerConnection>();

            //Init dataCollector
            dataCollector = new DataCollector(calculateVarianceWindowSize, histogramBucketSize);

            // start monitoring
            monitoringService = new MonitoringService(dataCollector,monitoringIntervalInMilli,movingAverageWindowSize,varianceThreshold);
            monitoringService.startMonitoring();

            // await socket connection
            serverSocket = new ServerSocket(this.port);
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
