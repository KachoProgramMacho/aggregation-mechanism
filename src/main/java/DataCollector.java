import javafx.util.Pair;
import org.knowm.xchart.*;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class DataCollector {
    ArrayList<Pair<Date,Double>> collectedData;
    ArrayList<Double> varianceData;
    int calculateVarianceWindowSize;
    int histogramBucketSize;
    LogisticRegression logisticRegression;

    public DataCollector(int calculateVarianceWindowSize, int histogramBucketSize){
        this.collectedData = new ArrayList<Pair<Date,Double>>();
        this.varianceData = new ArrayList<Double>();
        this.histogramBucketSize = histogramBucketSize;
        this.calculateVarianceWindowSize = calculateVarianceWindowSize;
        logisticRegression = new LogisticRegression(3);
        List<LogisticRegression.Instance> instances = null;
        try {
            instances = logisticRegression.readDataSet("src\\main\\resources\\dataset.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        logisticRegression.train(instances);
    }

/*    public double calculateVariance(){
        //calculate mean
        double mean = 0;

        //iterator avoid concurrency problems
        Iterator<Pair<Date,Double>> iter = collectedData.iterator();
        while (iter.hasNext()) {
            mean += iter.next().getValue();
        }
        mean /= collectedData.size();

        //calculate variance
        double variance = 0;
        for(Pair<Date,Double> dataPoint : collectedData){
            variance += (dataPoint.getValue() - mean) * (dataPoint.getValue() - mean);
        }
        variance /= collectedData.size();

        return variance;
    }*/

    public synchronized double forecastNextVarianceMovingAverage(int n){
        if(varianceData.size()<n)
            return 0;
        List<Double> subList = varianceData.subList(varianceData.size()-n,varianceData.size());
        Iterator<Double> iter = subList.iterator();
        double averageVarianceOfWindow = 0;
        while(iter.hasNext()){
            averageVarianceOfWindow += iter.next();
        }
        averageVarianceOfWindow /= n;
        return averageVarianceOfWindow;
    }

    public void gatherVarianceDataFromLastEntries(int n){
        List<Pair<Date,Double>> subList = collectedData.subList(collectedData.size()-n,collectedData.size());
        Iterator<Pair<Date,Double>> iter = subList.iterator();
        double mean = 0;
        while (iter.hasNext()) {
            mean += iter.next().getValue();
        }
        mean /= subList.size();

        //calculate variance
        double variance = 0;
        iter = subList.iterator();
        while (iter.hasNext()) {
            double nextValue = iter.next().getValue();
            variance += (nextValue - mean) * (nextValue - mean);
        }
        variance /= subList.size();
        this.varianceData.add(variance);
        //System.out.println("new variance " + variance);
    }

    public void createChart(int memoryUtilization, int CPUUtilization){
        int collectedDataSize = collectedData.size();


        long timeWindowOfData = collectedData.get(collectedDataSize-1).getKey().getTime() - collectedData.get(0).getKey().getTime();
        double averageDataDelay = (double)timeWindowOfData / (double) collectedDataSize;
        double frequency= 1000/averageDataDelay;

        System.out.println("FREQUENCY : "+frequency + " datapoints per second");
        System.out.println(collectedData.size());
        double logisticRegressionOutput = logisticRegression.classify(new int[]{(int)frequency,CPUUtilization,memoryUtilization});
        System.out.println("Logistic Regression: "+logisticRegressionOutput);



        if(logisticRegressionOutput<0.5) {
/*            double[] xData = new double[varianceData.size()];
            double[] yData = new double[varianceData.size()];
            for (int i = 0; i < varianceData.size(); i++) {
                xData[i] = i;
                yData[i] = varianceData.get(i);
            }*/

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
            try {
                BitmapEncoder.saveBitmapWithDPI(chart, "src\\main\\resources\\generated-charts\\"+chartNameTimeSeries, BitmapEncoder.BitmapFormat.PNG, 300);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JFrame jFrame1 = new SwingWrapper(chart).displayChart();
            jFrame1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }else {

            //Histogram
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
            try {
                BitmapEncoder.saveBitmapWithDPI(histogramChart, "src\\main\\resources\\generated-charts\\"+chartNameHistogram, BitmapEncoder.BitmapFormat.PNG, 300);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JFrame jFrame2 = new SwingWrapper(histogramChart).displayChart();
            jFrame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
    }


    public void deleteCollectedData() {
        this.collectedData.clear();
        this.varianceData = new ArrayList<Double>(this.varianceData.subList(varianceData.size()-calculateVarianceWindowSize,varianceData.size()));
    }

    public ArrayList<Pair<Date, Double>> getCollectedData() {
        return collectedData;
    }

    public synchronized void addNewDataPoint(Pair<Date,Double> dataPoint){
        collectedData.add(dataPoint);
        if(collectedData.size() % this.calculateVarianceWindowSize ==0 && collectedData.size()>=calculateVarianceWindowSize){
            this.gatherVarianceDataFromLastEntries(calculateVarianceWindowSize);
        }
    }
}
