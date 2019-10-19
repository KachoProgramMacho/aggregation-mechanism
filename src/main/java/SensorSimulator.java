/**
 * Generates sensor simulators with selected configurations.
 */
public class SensorSimulator {

    // Configuration constants
    private static final String host = "localhost";
    private static final int port = 7000;
    private static final double lowVarianceState = 10.0;
    private static final double highVarianceState = 70.0;
    private static final int dataGenerationDelayLowVariance = 2;
    private static final int dataGenerationDelayHighVariance = 1000;
    private static final int stateShiftInterval = 30*1000;

    public static void main(String[] args) {
        SensorSimulatorWorker worker1 = new SensorSimulatorWorker(1, host, port, lowVarianceState, highVarianceState,
                dataGenerationDelayLowVariance, dataGenerationDelayHighVariance, stateShiftInterval);
        worker1.start();
    }

}
