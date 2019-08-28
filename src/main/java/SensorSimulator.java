import java.util.ArrayList;

public class SensorSimulator {



    public static void main(String[] args){
        SensorSimulatorWorker worker1 = new SensorSimulatorWorker(1);
        worker1.start();

/*        SensorSimulatorWorker worker2 = new SensorSimulatorWorker(2);
        worker2.start();*/
    }

}
