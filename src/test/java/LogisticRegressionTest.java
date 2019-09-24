import java.io.FileNotFoundException;
import java.util.List;

public class LogisticRegressionTest {
    public static void main(String... args) throws FileNotFoundException {
        LogisticRegression logisticRegression = new LogisticRegression(3);
        List<LogisticRegression.Instance> instances = logisticRegression.readDataSet("src\\main\\resources\\dataset.txt");
        logisticRegression.train(instances);
        int[] x = {900, 89, 90};
        System.out.println("prob(900, 89, 90) = " + logisticRegression.classify(x));

        int[] x7 = {600, 70, 70};
        System.out.println("prob(600, 70, 70) = " + logisticRegression.classify(x7));

        int[] x4 = {400, 60, 70};
        System.out.println("prob(400, 60, 70) = " + logisticRegression.classify(x4));

        int[] x1 = {500, 50, 50};

        System.out.println("prob(500, 50, 50) = " + logisticRegression.classify(x1));
        System.out.println(logisticRegression.sigmoid(0));

        int[] x90 = {150, 95, 43};
        System.out.println("prob(150, 95, 43) = " + logisticRegression.classify(x90));

        int[] x9 = {300, 30, 43};
        System.out.println("prob(300, 30, 43) = " + logisticRegression.classify(x9));

        int[] x2 = {10, 10, 10};
        System.out.println("prob(10, 10, 10) = " + logisticRegression.classify(x2));

    }
}
