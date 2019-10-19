import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Date;

/**
 * Handles a single socket connection.
 */
public class ServerConnection extends Thread{

    private Socket socket;
    private DataCollector dataCollector;
    private ObjectInputStream dataInputStream;
    private boolean continueRunning = true;

    public ServerConnection(Socket socket, DataCollector dataCollector){
        this.socket = socket;
        this.dataCollector = dataCollector;
    }

    public void run(){
        try {
            dataInputStream = new ObjectInputStream(socket.getInputStream());
            while(continueRunning){
                Pair<Date,Double> newDataPoint = (Pair<Date,Double>)dataInputStream.readObject();
                dataCollector.addNewDataPoint(newDataPoint);

            }
            dataInputStream.close();
            socket.close();
            System.out.println("Close connection");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
