import javafx.util.Pair;
import org.knowm.xchart.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

public class DiagramGenerationRuntimeFIXEDNUMBERANDBUCKETTest {
    static Double aDouble = 1.0;
    static Date date = new Date();
    static int index = 1;
    public static void main(String... args) throws InterruptedException {


        DataCollector dataCollector = new DataCollector(10);
        int counter = 0;
        int[] numberOfEntries = new int[100];
        int[] histogramBucketGenTime = new int[100];
        int[] timeSeriesGenTime = new int[100];
        for (int i = 0; i < 500; i++)
            dataCollector.addNewDataPoint(getNewDataPoint());
        createTimeSeriesChart(dataCollector);
        createHistogram(dataCollector,1000);

        for (int i = 0; i < 20000; i++)
            dataCollector.addNewDataPoint(getNewDataPoint());
        while(counter<20) {


            long startTime = System.currentTimeMillis();
            createTimeSeriesChart(dataCollector);
            long endTime = System.currentTimeMillis();
            long timeElapsed = endTime - startTime;
            timeSeriesGenTime[counter] = Math.toIntExact(timeElapsed);
            System.out.println("timeseries: "+timeElapsed);

            startTime = System.currentTimeMillis();
            createHistogram(dataCollector,150);
            endTime = System.currentTimeMillis();
            timeElapsed = endTime - startTime;
            System.out.println("histogram: "+timeElapsed);
            System.out.println("---------"+counter);
            numberOfEntries[counter]=counter+1;
            histogramBucketGenTime[counter]=Math.toIntExact(timeElapsed);
            counter++;
        }
        //XYChart chart = QuickChart.getChart("Evaluation", "Number of entries", "Time in milliseconds","Time series",numberOfEntries,histogramGenTime);
        XYChart chart = new XYChartBuilder().width(800).height(600).title("Generation times of Histogram and Time Series chart").xAxisTitle("Attempt").yAxisTitle("Time in milliseconds").build();
        chart.getStyler().setChartBackgroundColor(Color.WHITE);
        //chart.getStyler().setXAxisDecimalPattern("###k");

        numberOfEntries = Arrays.copyOfRange(numberOfEntries,0,counter);
        histogramBucketGenTime = Arrays.copyOfRange(histogramBucketGenTime,0,counter);
        timeSeriesGenTime = Arrays.copyOfRange(timeSeriesGenTime,0,counter);

        XYSeries series = chart.addSeries("Time Series", numberOfEntries,timeSeriesGenTime);
        XYSeries series2 = chart.addSeries("Histogram", numberOfEntries,histogramBucketGenTime);
        JFrame jFrame2 = new SwingWrapper(chart).displayChart();
        try {
            BitmapEncoder.saveBitmapWithDPI(chart, "src\\main\\resources\\chartsoutput\\"+"evaluationFIXEDNUMEBANDBUCKETS", BitmapEncoder.BitmapFormat.PNG, 300);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try
        {
            PrintWriter pr = new PrintWriter("fixedTestData.txt");

            for (int i=0; i<timeSeriesGenTime.length ; i++)
            {
                pr.println(timeSeriesGenTime[i]+ " "+histogramBucketGenTime[i]);
            }
            pr.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("No such file exists.");
        }
    }

    public static void createHistogram(DataCollector dataCollector,int bucketSize){
        int collectedDataSize = dataCollector.getCollectedData().size();
        List<Pair<Date,Double>> collectedData = dataCollector.getCollectedData();
        int histogramBucketSize = bucketSize;//collectedDataSize / 20;

        Date[] keyData = new Date[collectedDataSize];
        double[] valueData = new double[collectedDataSize];
        for (int i = 0; i < collectedDataSize; i++) {
            keyData[i] = collectedData.get(i).getKey();
            valueData[i] = collectedData.get(i).getValue();
        }
        ArrayList<Date> buckets = new ArrayList<Date>();
        ArrayList<Integer> numberOfDatapoints = new ArrayList<Integer>();
        ArrayList<Integer> bucketsNumbered = new ArrayList<Integer>();

        buckets.add(keyData[0]);
        bucketsNumbered.add(0);
        numberOfDatapoints.add(0);
        long currentBucketBoundary = keyData[0].getTime() + histogramBucketSize;
        for (int i = 0; i < keyData.length; i++) {
            if (keyData[i].getTime() >= currentBucketBoundary) {
                currentBucketBoundary += histogramBucketSize;
                buckets.add(keyData[i]);
                bucketsNumbered.add(bucketsNumbered.size());
                numberOfDatapoints.add(0);
            }
            numberOfDatapoints.set(numberOfDatapoints.size() - 1, numberOfDatapoints.get(numberOfDatapoints.size() - 1) + 1);

        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String dateStartString = format.format(collectedData.get(0).getKey());
        String dateEndString = format.format(collectedData.get(collectedDataSize-1).getKey());
        String chartNameHistogram = dateStartString +"---"+ dateEndString + "--HISTOGRAM";

        CategoryChart histogramChart = new CategoryChartBuilder().width(800).height(600).title("Histogram: "+dateStartString+"---"+dateEndString).xAxisTitle("Time period").yAxisTitle("Number").build();
        histogramChart.getStyler().setDatePattern("HH:mm:ss");
        // Customize Chart
/*
        chart.getStyler().setLegendPosition(LegendPosition.InsideNW);
        chart.getStyler().setHasAnnotations(true);
*/

        // Series
        histogramChart.addSeries("test 1", buckets, numberOfDatapoints);
        histogramChart.getStyler().setChartBackgroundColor(Color.WHITE);
        try {
            BitmapEncoder.saveBitmapWithDPI(histogramChart, "src\\main\\resources\\chartsoutput\\"+chartNameHistogram, BitmapEncoder.BitmapFormat.PNG, 300);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void createTimeSeriesChart(DataCollector dataCollector){
        int collectedDataSize = dataCollector.getCollectedData().size();
        List<Pair<Date,Double>> collectedData = dataCollector.getCollectedData();

        double[] xData = new double[collectedDataSize];
        double[] yData = new double[collectedDataSize];
        for (int i = 0; i < collectedDataSize; i++) {
            xData[i] = collectedData.get(i).getKey().getTime() - collectedData.get(0).getKey().getTime();
            yData[i] = collectedData.get(i).getValue();
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String dateStartString = format.format(collectedData.get(0).getKey());
        String dateEndString = format.format(collectedData.get(collectedDataSize-1).getKey());
        String chartNameTimeSeries = dateStartString +"---"+ dateEndString + "--TIMESERIES";
        XYChart chart = QuickChart.getChart("Time series chart: "+dateStartString+"---"+dateEndString, "Time in milliseconds", "value", "Time series chart", xData, yData);
        chart.getStyler().setChartBackgroundColor(Color.WHITE);
        chart.addSeries("asd", yData);
        try {
            BitmapEncoder.saveBitmapWithDPI(chart, "src\\main\\resources\\chartsoutput\\"+chartNameTimeSeries, BitmapEncoder.BitmapFormat.PNG, 300);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Pair<Date,Double> getNewDataPoint(){
        return new Pair<Date, Double>(new Date(date.getTime()+index++),new Random().nextDouble());
    }
}
