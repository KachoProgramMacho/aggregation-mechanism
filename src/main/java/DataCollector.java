import javafx.util.Pair;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class DataCollector {
    ArrayList<Pair<Date,Double>> collectedData;
    ArrayList<Double> varianceData;
    int calculateVarianceWindowSize;

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

    public void createChart(boolean varianceThresholdReached){
        double[] xData = new double[collectedData.size()];
        double[] yData = new double[collectedData.size()];
        for(int i =0;i<collectedData.size();i++){
            xData[i] = i;
            yData[i] = collectedData.get(i).getValue();
        }

        // Create Chart
        double[] varxData = new double[varianceData.size()];
        double[] varyData = new double[varianceData.size()];
        for(int i =0;i<varianceData.size();i++){
            varxData[i] = i;
            varyData[i] = varianceData.get(i);
        }

        if(varianceThresholdReached){
            XYChart chartVar = QuickChart.getChart("Variance", "X", "Y", "y(x)", varxData, varyData);
            JFrame jFrame2 = new SwingWrapper(chartVar).displayChart();
            jFrame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }else{
            XYChart chart = QuickChart.getChart("values", "X", "Y", "y(x)", xData, yData);
            JFrame jFrame1 = new SwingWrapper(chart).displayChart();
            jFrame1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
    }


    public void deleteCollectedData() {
        this.collectedData.clear();
        this.varianceData.clear();
    }

    public ArrayList<Pair<Date, Double>> getCollectedData() {
        return collectedData;
    }

    public void addNewDataPoint(Pair<Date,Double> dataPoint){
        collectedData.add(dataPoint);
        if(collectedData.size() % this.calculateVarianceWindowSize ==0 && collectedData.size()>=calculateVarianceWindowSize){
            this.gatherVarianceDataFromLastEntries(calculateVarianceWindowSize);
        }
    }
}
