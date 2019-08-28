import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Date;

public class MonitorThread extends Thread {
    DataCollector dataCollector;
    boolean continueMonitoring = true;
    int monitorIntervalInMilliseconds = 10000;


    //console colors
    public static final String GREEN = "\033[0;32m";
    public static final String RESET = "\033[0m";


    public MonitorThread(DataCollector dataCollector){
        this.dataCollector = dataCollector;
    }

    public void run(){
        while(continueMonitoring){
            try {
                Thread.sleep(monitorIntervalInMilliseconds);

                System.out.println(GREEN + dataCollector.getCollectedData().toString() + RESET);
                System.out.println(GREEN + "Variance: "+dataCollector.calculateVariance() + RESET);
                dataCollector.createChart();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
