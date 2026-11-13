public final class SourceStatistics {
    private final int sourceId;
    private int generatedCount;
    private int rejectedCount;
    private int startedCount;
    private int completedCount;
    private double waitingTimeSum;
    private double waitingTimeSquareSum;
    private double serviceTimeSum;
    private double serviceTimeSquareSum;
    private double timeInSystemSum;

    public SourceStatistics(int sourceId) { this.sourceId = sourceId; }
    public int getSourceId() { return sourceId; }
    public int getGeneratedCount() { return generatedCount; }
    public int getRejectedCount() { return rejectedCount; }
    public int getCompletedCount() { return completedCount; }
    public int getStartedCount() { return startedCount; }

    public void recordGenerated() { generatedCount++; }
    public void recordRejected() { rejectedCount++; }

    public void recordServiceStart(double wait) {
        startedCount++;
        waitingTimeSum += wait;
        waitingTimeSquareSum += wait * wait;
    }

    public void recordCompleted(double service, double total) {
        completedCount++;
        serviceTimeSum += service;
        serviceTimeSquareSum += service * service;
        timeInSystemSum += total;
    }

    public double getRejectionPercent() {
        return generatedCount == 0 ? 0 : 100.0 * rejectedCount / generatedCount;
    }

    public double getMeanWaitingTime() {
        return startedCount == 0 ? 0 : waitingTimeSum / startedCount;
    }

    public double getWaitingVariance() {
        return startedCount == 0 ? 0 : Math.max(0,
                waitingTimeSquareSum / startedCount - Math.pow(getMeanWaitingTime(), 2));
    }

    public double getMeanServiceTime() {
        return completedCount == 0 ? 0 : serviceTimeSum / completedCount;
    }

    public double getServiceVariance() {
        return completedCount == 0 ? 0 : Math.max(0,
                serviceTimeSquareSum / completedCount - Math.pow(getMeanServiceTime(), 2));
    }

    public double getMeanTimeInSystem() {
        return completedCount == 0 ? 0 : timeInSystemSum / completedCount;
    }
}