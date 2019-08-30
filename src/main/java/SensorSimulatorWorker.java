import javafx.util.Pair;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.Random;

public class SensorSimulatorWorker extends Thread {

    Socket socket;
    ObjectOutputStream dataOutputStream;
    boolean continueRunning = true;
    int id;
    double standartDeviationMultiplier = 1;
    double multiplierIncrease;
    int dataGenerationInterval;

    public SensorSimulatorWorker(int id, double multiplierIncrease, int dataGenerationInterval){
        this.id = id;
        this.multiplierIncrease = multiplierIncrease;
        this.dataGenerationInterval = dataGenerationInterval;
        System.out.println("Worker "+id+" spawned!");
    }

    public void run(){
        try {
            socket = new Socket("localhost",7000);
            dataOutputStream = new ObjectOutputStream(socket.getOutputStream());
            while(continueRunning){
                Thread.sleep(dataGenerationInterval);

                //System.out.println("Worker"+this.id+" sent data.");

                //dataOutputStream.flush();

                //System.out.println("dai muu");
                dataOutputStream.writeObject(this.generateDataPointPair());
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Pair<Date,Double> generateDataPointPair(){
        Random rand = new Random();
        Double value = rand.nextGaussian()*standartDeviationMultiplier;
        standartDeviationMultiplier += multiplierIncrease;
        //System.out.println(standartDeviationMultiplier);
        Pair<Date,Double> newDataPoint = new Pair<Date,Double>(new Date(),value);
        return newDataPoint;
    }
}
