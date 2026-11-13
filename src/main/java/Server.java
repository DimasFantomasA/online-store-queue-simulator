import java.util.Random;

public final class Server {
    private final int id;
    private Order currentOrder;
    private Double serviceStartTime;
    private Double plannedCompletionTime;
    private double totalBusyTime;
    private final double minServiceTime;
    private final double maxServiceTime;
    private final Random random;

    public Server(int id, double minServiceTime, double maxServiceTime, Random random) {
        if (id <= 0 || minServiceTime <= 0 || maxServiceTime < minServiceTime) {
            throw new IllegalArgumentException("Invalid server parameters");
        }
        this.id = id;
        this.minServiceTime = minServiceTime;
        this.maxServiceTime = maxServiceTime;
        this.random = random;
    }

    public int getId() { return id; }
    public boolean isBusy() { return currentOrder != null; }
    public Order getCurrentOrder() { return currentOrder; }
    public Double getPlannedCompletionTime() { return plannedCompletionTime; }

    public double startService(Order order, double time) {
        if (isBusy()) throw new IllegalStateException("Server is busy");
        currentOrder = order;
        serviceStartTime = time;
        order.startService(time);
        double duration = minServiceTime + random.nextDouble() * (maxServiceTime - minServiceTime);
        plannedCompletionTime = time + duration;
        return plannedCompletionTime;
    }

    public Order completeService(double time) {
        if (!isBusy() || plannedCompletionTime == null ||
                Math.abs(time - plannedCompletionTime) > 1e-8) {
            throw new IllegalStateException("Invalid service completion event");
        }
        Order completed = currentOrder;
        completed.complete(time);
        totalBusyTime += time - serviceStartTime;
        currentOrder = null;
        serviceStartTime = null;
        plannedCompletionTime = null;
        return completed;
    }

    public double getBusyTime(double time) {
        return totalBusyTime + (isBusy() ? Math.max(0.0, time - serviceStartTime) : 0.0);
    }

    public double getUtilization(double totalTime) {
        return totalTime <= 0 ? 0 : getBusyTime(totalTime) / totalTime;
    }
}