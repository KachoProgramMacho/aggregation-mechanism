import javafx.util.Pair;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SensorSimulatorWorker extends Thread {

    Socket socket;
    ObjectOutputStream dataOutputStream;
    boolean continueRunning = true;
    int id;
    double standartDeviationMultiplier = 1;
    double multiplierIncrease;
    int dataGenerationInterval;
    int dataGenerationDelay = 100;

    public SensorSimulatorWorker(int id, double multiplierIncrease, int dataGenerationInterval){
        this.id = id;
        this.multiplierIncrease = multiplierIncrease;
        this.dataGenerationInterval = dataGenerationInterval;
        System.out.println("Worker "+id+" spawned!");
        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                if(standartDeviationMultiplier ==10){
                    standartDeviationMultiplier = 70;
                    dataGenerationDelay = 50;
                }else{
                    standartDeviationMultiplier = 10;
                    dataGenerationDelay = 2;
                }

            }
        },0,1*30*1000);// TODO: MOVE
    }

    public void run(){
        try {
            socket = new Socket("localhost",7000); //TODO: MOVE
            dataOutputStream = new ObjectOutputStream(socket.getOutputStream());
            while(continueRunning){
                Thread.sleep(dataGenerationInterval);
                dataGenerationInterval = new Random().nextInt(dataGenerationDelay);
                if(new Random().nextInt(3000)==45){
                    Thread.sleep(2000);
                }
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
        //standartDeviationMultiplier += multiplierIncrease;
        //System.out.println(standartDeviationMultiplier);
        Pair<Date,Double> newDataPoint = new Pair<Date,Double>(new Date(),value);
        return newDataPoint;
    }


}
