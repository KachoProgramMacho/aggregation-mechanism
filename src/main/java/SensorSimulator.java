import java.util.ArrayList;

public class SensorSimulator {



    public static void main(String[] args){
        SensorSimulatorWorker worker1 = new SensorSimulatorWorker(1,0.1,200);
        worker1.start();

        //SensorSimulatorWorker worker2 = new SensorSimulatorWorker(2,0,100);
        //worker2.start();
    }

}
