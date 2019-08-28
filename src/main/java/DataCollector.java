import javafx.util.Pair;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;

public class DataCollector {
    ArrayList<Pair<Date,Double>> collectedData;

    public DataCollector(){
        this.collectedData = new ArrayList<Pair<Date,Double>>();
    }

    public double calculateVariance(){
        //calculate mean
        double mean = 0;
        for(Pair<Date,Double> dataPoint : collectedData){
            mean += dataPoint.getValue();
        }
        mean /= collectedData.size();

        //calculate variance
        double variance = 0;
        for(Pair<Date,Double> dataPoint : collectedData){
            variance += (dataPoint.getValue() - mean) * (dataPoint.getValue() - mean);
        }
        variance /= collectedData.size();

        return variance;
    }

    public void createChart(){
        double[] xData = new double[collectedData.size()];
        double[] yData = new double[collectedData.size()];
        for(int i =0;i<collectedData.size();i++){
            xData[i] = i;
            yData[i] = collectedData.get(i).getValue();
        }

        // Create Chart
        XYChart chart = QuickChart.getChart("Variance: "+ this.calculateVariance(), "X", "Y", "y(x)", xData, yData);
        // Show it
        new SwingWrapper(chart).displayChart().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }


    public void deleteCollectedData() {
        this.collectedData = new ArrayList<Pair<Date,Double>>();
    }

    public ArrayList<Pair<Date, Double>> getCollectedData() {
        return collectedData;
    }

    public void addNewDataPoint(Pair<Date,Double> dataPoint){
        collectedData.add(dataPoint);
    }
}
