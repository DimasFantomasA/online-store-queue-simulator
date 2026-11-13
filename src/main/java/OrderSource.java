import java.util.Random;

public final class OrderSource {
    private static int nextGlobalOrderId = 1;
    private final int sourceId;
    private final double arrivalRate;
    private final Random random;

    public OrderSource(int sourceId, double arrivalRate, Random random) {
        if (sourceId <= 0 || arrivalRate <= 0 || !Double.isFinite(arrivalRate)) {
            throw new IllegalArgumentException("Invalid source parameters");
        }
        this.sourceId = sourceId;
        this.arrivalRate = arrivalRate;
        this.random = random;
    }

    public int getSourceId() { return sourceId; }
    public double getArrivalRate() { return arrivalRate; }

    public double nextInterarrival() {
        double u = 1.0 - random.nextDouble();
        return -Math.log(u) / arrivalRate;
    }

    public Order createOrder(double arrivalTime) {
        return new Order(nextGlobalOrderId++, sourceId, arrivalTime);
    }
}