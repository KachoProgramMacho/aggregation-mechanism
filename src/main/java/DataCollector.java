import javafx.util.Pair;
import org.knowm.xchart.*;

import javax.swing.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class DataCollector {
    ArrayList<Pair<Date,Double>> collectedData;
    ArrayList<Double> varianceData;
    int calculateVarianceWindowSize;
    //in milliseconds
    int histogramBucketSize = 5000;
    int frequencyThreshold= 100;

    public DataCollector(int calculateVarianceWindowSize){
        this.collectedData = new ArrayList<Pair<Date,Double>>();
        this.varianceData = new ArrayList<Double>();
        this.calculateVarianceWindowSize = calculateVarianceWindowSize;
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

    public double forecastNextVarianceMovingAverage(int n){
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

    public void createChart(){
        int collectedDataSize = collectedData.size();


        long timeWindowOfData = collectedData.get(collectedDataSize-1).getKey().getTime() - collectedData.get(0).getKey().getTime();
        double averageDataDelay = timeWindowOfData / collectedDataSize;
        double frequency= 1000/averageDataDelay;

        System.out.println("FREQUENCY : "+frequency + " datapoints per second");
        System.out.println(collectedData.size());

        if(frequency < frequencyThreshold) {
            double[] xData = new double[collectedDataSize];
            double[] yData = new double[collectedDataSize];
            for (int i = 0; i < collectedDataSize; i++) {
                xData[i] = i;
                yData[i] = collectedData.get(i).getValue();
            }

            XYChart chart = QuickChart.getChart("values", "X", "Y", "y(x)", xData, yData);
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

            CategoryChart histogramChart = new CategoryChartBuilder().width(800).height(600).title("Score Histogram").xAxisTitle("Score").yAxisTitle("Number").build();
            histogramChart.getStyler().setDatePattern("HH:mm:ss");
            // Customize Chart
/*
        chart.getStyler().setLegendPosition(LegendPosition.InsideNW);
        chart.getStyler().setHasAnnotations(true);
*/

            // Series
            histogramChart.addSeries("test 1", buckets, numberOfDatapoints);
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
