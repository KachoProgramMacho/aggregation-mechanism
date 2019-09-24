import javafx.util.Pair;
import sun.plugin2.gluegen.runtime.CPU;

import javax.management.*;
import java.io.FileNotFoundException;
import java.lang.management.ManagementFactory;
import java.util.*;

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
                    dataCollector.createChart(getMemoryUtilization(),getCPUUtilization());
                    dataCollector.deleteCollectedData();
                    overVarianceThreshold = !overVarianceThreshold;
                }
                System.out.println("Forecast Variance Value: " + forecastValue);
                System.out.println("Mem: " +getMemoryUtilization());
                System.out.println("CPU: "+getCPUUtilization());
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
    public int getMemoryUtilization(){
        return (int)((double)(( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())) / Runtime.getRuntime().totalMemory() * 100);
    }

    //TODO: LOOK THROUGH ONCE MORE
    public int getCPUUtilization(){
        try {

            MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
            ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
            AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });

            if (list.isEmpty())     return 0;

            Attribute att = (Attribute)list.get(0);
            Double value  = (Double)att.getValue();

            // usually takes a couple of seconds before we get real values
            if (value == -1.0)      return 0;
            // returns a percentage value with 1 decimal point precision
            return (int)((int)(value * 1000) / 10.0);
        }catch (Exception e){
            return 0;
        }
    }

}
