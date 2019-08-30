import javafx.util.Pair;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Date;
import java.util.SimpleTimeZone;

public class ServerConnection extends Thread{

    Socket socket;
    DataCollector dataCollector;
    ObjectInputStream dataInputStream;
    boolean continueRunning = true;

    public ServerConnection(Socket socket, DataCollector dataCollector){
        this.socket = socket;
        this.dataCollector = dataCollector;
    }

    public void run(){
        try {
            dataInputStream = new ObjectInputStream(socket.getInputStream());
            while(continueRunning){
                Pair<Date,Double> newDataPoint = (Pair<Date,Double>)dataInputStream.readObject();
                //System.out.println("New data point: "+ newDataPoint);
                dataCollector.addNewDataPoint(newDataPoint);

            }
            dataInputStream.close();
            socket.close();
            System.out.println("Close connection");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}
