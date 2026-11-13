import java.util.List;

public final class SimulationSnapshot {
    private final int stepNumber;
    private final double currentTime;
    private final String lastAction;
    private final List<OrderSource> sources;
    private final List<SourceStatistics> sourceStatistics;
    private final Order[] bufferSlots;
    private final int bufferPointer;
    private final int bufferSize;
    private final List<Server> servers;
    private final int maxOrders;

    public SimulationSnapshot(
            int stepNumber, double currentTime, String lastAction,
            List<OrderSource> sources, List<SourceStatistics> sourceStatistics,
            Order[] bufferSlots, int bufferPointer, int bufferSize,
            List<Server> servers, int maxOrders) {
        this.stepNumber = stepNumber;
        this.currentTime = currentTime;
        this.lastAction = lastAction;
        this.sources = List.copyOf(sources);
        this.sourceStatistics = List.copyOf(sourceStatistics);
        this.bufferSlots = bufferSlots.clone();
        this.bufferPointer = bufferPointer;
        this.bufferSize = bufferSize;
        this.servers = List.copyOf(servers);
        this.maxOrders = maxOrders;
    }

    public int getStepNumber() { return stepNumber; }
    public double getCurrentTime() { return currentTime; }
    public String getLastAction() { return lastAction; }
    public List<OrderSource> getSources() { return sources; }
    public List<SourceStatistics> getSourceStatistics() { return sourceStatistics; }
    public Order[] getBufferSlots() { return bufferSlots.clone(); }
    public int getBufferPointer() { return bufferPointer; }
    public int getBufferSize() { return bufferSize; }
    public List<Server> getServers() { return servers; }
    public int getMaxOrders() { return maxOrders; }
}