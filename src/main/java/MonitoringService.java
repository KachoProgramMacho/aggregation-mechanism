import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MonitoringService{
    DataCollector dataCollector;
    int monitoringIntervalInMilli;
    int movingAverageWindowSize;
    //int gatherVarianceDataPeriodinMilliseconds = 600;

    final int reportTimeWindow = 1 * 60 * 1000;
    boolean overVarianceThreshold = false;
    double varianceThreshold = 1000.0;

    //console colors
    public static final String GREEN = "\033[0;32m";
    public static final String RESET = "\033[0m";


    public MonitoringService(DataCollector dataCollector, int monitoringIntervalInMilli, int movingAverageWindowSize){
        this.dataCollector = dataCollector;
        this.monitoringIntervalInMilli = monitoringIntervalInMilli;
        this.movingAverageWindowSize = movingAverageWindowSize;
    }

    public void startMonitoring(){


        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                double forecastValue = dataCollector.forecastNextVarianceMovingAverage(movingAverageWindowSize);
                if((forecastValue > varianceThreshold && !overVarianceThreshold)||(forecastValue < varianceThreshold && overVarianceThreshold)){
                    dataCollector.createChart();
                    dataCollector.deleteCollectedData();
                    overVarianceThreshold = !overVarianceThreshold;
                }
                System.out.println("Forecast Variance Value: " + forecastValue);

            }
        },monitoringIntervalInMilli,monitoringIntervalInMilli);

/*        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                dataCollector.createChart(varianceThresholdReached);
                varianceThresholdReached = false;
                dataCollector.deleteCollectedData();
            }
        },reportTimeWindow,reportTimeWindow);*/


/*        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                dataCollector.gatherVarianceData();
            }
        },1000,gatherVarianceDataPeriodinMilliseconds);*/
/*        while(continueMonitoring){
            try {
                Thread.sleep(monitorIntervalInMilliseconds);

                System.out.println(GREEN + dataCollector.getCollectedData().toString() + RESET);
                System.out.println(GREEN + "Variance: "+dataCollector.calculateVariance() + RESET);
                dataCollector.createChart();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

    }
}
