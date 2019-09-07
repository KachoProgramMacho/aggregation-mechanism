import java.util.ArrayList;
import java.util.Random;

public class SensorSimulator {



    public static void main(String[] args){
        SensorSimulatorWorker worker1 = new SensorSimulatorWorker(1,0.1,20);
        worker1.start();

/*        SensorSimulatorWorker worker2 = new SensorSimulatorWorker(2,0,100);
        worker2.start();

        SensorSimulatorWorker worker3 = new SensorSimulatorWorker(2,0,100);
        worker3.start();*/
    }

}
