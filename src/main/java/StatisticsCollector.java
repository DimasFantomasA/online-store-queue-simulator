import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class StatisticsCollector {
    private final Map<Integer, SourceStatistics> bySource = new LinkedHashMap<>();

    public StatisticsCollector(List<OrderSource> sources) {
        for (OrderSource source : sources) {
            int sourceId = source.getSourceId();
            bySource.put(sourceId, new SourceStatistics(sourceId));
        }
    }

    private SourceStatistics forOrder(Order order) {
        SourceStatistics stats = bySource.get(order.getSourceId());
        if (stats == null) {
            throw new IllegalArgumentException("Unknown source");
        }
        return stats;
    }

    public void recordGenerated(Order order) {
        forOrder(order).recordGenerated();
    }

    public void recordRejected(Order order) {
        forOrder(order).recordRejected();
    }

    public void recordServiceStart(Order order) {
        Double waitingTime = order.getWaitingTime();
        if (waitingTime == null) {
            throw new IllegalStateException("Service has not started for " + order);
        }
        forOrder(order).recordServiceStart(waitingTime);
    }

    public void recordCompleted(Order order) {
        Double serviceTime = order.getServiceTime();
        Double timeInSystem = order.getTimeInSystem();
        if (serviceTime == null || timeInSystem == null) {
            throw new IllegalStateException("Service has not completed for " + order);
        }
        forOrder(order).recordCompleted(serviceTime, timeInSystem);
    }

    public Collection<SourceStatistics> getSourceStatistics() {
        return List.copyOf(bySource.values());
    }

    public int getGeneratedCount() {
        return bySource.values().stream()
                .mapToInt(SourceStatistics::getGeneratedCount)
                .sum();
    }

    public int getRejectedCount() {
        return bySource.values().stream()
                .mapToInt(SourceStatistics::getRejectedCount)
                .sum();
    }

    public int getCompletedCount() {
        return bySource.values().stream()
                .mapToInt(SourceStatistics::getCompletedCount)
                .sum();
    }
}