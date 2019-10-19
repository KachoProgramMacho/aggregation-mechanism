import javafx.util.Pair;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Opens socket connection to DeciderServer, starts generation data points and sends them to DeviderServer.
 * Has two distinct states: low and high variance
 */
public class SensorSimulatorWorker extends Thread {

    private Socket socket;
    private ObjectOutputStream dataOutputStream;
    private boolean continueRunning = true;


    private int id;
    int nextPause;
    private double standartDeviationMultiplier;
    private int dataGenerationDelay;

    private String host;
    private int port;
    private double lowVarianceState;
    private double highVarianceState;
    private int dataGenerationDelayLowVariance;
    private int dataGenerationDelayHighVariance;
    private int stateShiftInterval;


    public SensorSimulatorWorker(int id, String host, int port, double lowVarianceState, double highVarianceState,
                                 int dataGenerationDelayLowVariance, int dataGenerationDelayHighVariance, int stateShiftInterval){
        this.id = id;
        this.host = host;
        this.port = port;
        this.lowVarianceState = lowVarianceState;
        this.highVarianceState = highVarianceState;
        this.dataGenerationDelayLowVariance = dataGenerationDelayLowVariance;
        this.dataGenerationDelayHighVariance = dataGenerationDelayHighVariance;
        this.dataGenerationDelay = dataGenerationDelayLowVariance;
        this.standartDeviationMultiplier = this.lowVarianceState;
        this.stateShiftInterval = stateShiftInterval;
        this.nextPause = dataGenerationDelayLowVariance;

        System.out.println("Worker "+id+" spawned!");

        //Shift variance state
        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                if(standartDeviationMultiplier ==lowVarianceState){
                    standartDeviationMultiplier = highVarianceState;
                    dataGenerationDelay = 50;
                }else{
                    standartDeviationMultiplier = lowVarianceState;
                    dataGenerationDelay = 5;
                }

            }
        },stateShiftInterval,stateShiftInterval);
    }

    public void run(){
        try {
            socket = new Socket(this.host,this.port);
            dataOutputStream = new ObjectOutputStream(socket.getOutputStream());
            while(continueRunning){
                Thread.sleep(this.nextPause);
                this.nextPause = new Random().nextInt(dataGenerationDelay);
                dataOutputStream.writeObject(this.generateDataPointPair());
            }

            socket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * generates random values from a normal distribution
     */
    public Pair<Date,Double> generateDataPointPair(){
        Random rand = new Random();
        Double value = rand.nextGaussian()*standartDeviationMultiplier;
        Pair<Date,Double> newDataPoint = new Pair<Date,Double>(new Date(),value);
        return newDataPoint;
    }


}
