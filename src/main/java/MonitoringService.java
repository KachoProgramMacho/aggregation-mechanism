import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.*;

public class MonitoringService{
    private DataCollector dataCollector;
    private int monitoringIntervalInMilli;
    private int movingAverageWindowSize;
    private boolean overVarianceThreshold = false;
    private double varianceThreshold;


    public MonitoringService(DataCollector dataCollector, int monitoringIntervalInMilli, int movingAverageWindowSize, double varianceThreshold){
        this.dataCollector = dataCollector;
        this.monitoringIntervalInMilli = monitoringIntervalInMilli;
        this.movingAverageWindowSize = movingAverageWindowSize;
        this.varianceThreshold = varianceThreshold;
    }

    public void startMonitoring(){

        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                double forecastValue = dataCollector.forecastNextVarianceMovingAverage(movingAverageWindowSize);
                if((forecastValue > varianceThreshold && !overVarianceThreshold)||(forecastValue < varianceThreshold && overVarianceThreshold)){
                    System.out.println("Forecast Variance Value: " + forecastValue);
                    System.out.println("Mem: " +getMemoryUtilization());
                    System.out.println("CPU: "+getCPUUtilization());
                    dataCollector.createChart(getMemoryUtilization(),getCPUUtilization());
                    dataCollector.deleteCollectedData();
                    overVarianceThreshold = !overVarianceThreshold;
                }
            }
        },monitoringIntervalInMilli,monitoringIntervalInMilli);


    }
    public int getMemoryUtilization(){
        return (int)((double)(( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())) / Runtime.getRuntime().totalMemory() * 100);
    }

    //From stackoverflow on 19.10.2019: https://stackoverflow.com/questions/18489273/how-to-get-percentage-of-cpu-usage-of-os-from-java/21962037
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
