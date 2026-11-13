import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class SimulationEngine {
    private double currentTime;
    private final int maxOrders;
    private int generatedCount;
    private int stepNumber;
    private String lastAction = "Модель инициализирована";

    private final List<OrderSource> sources;
    private final EventCalendar calendar;
    private final PlacementDispatcher placement;
    private final SelectionDispatcher selection;
    private final CircularBuffer buffer;
    private final List<Server> servers;
    private final StatisticsCollector statistics;

    public SimulationEngine(int maxOrders, List<OrderSource> sources, List<Server> servers,
                            CircularBuffer buffer) {
        if (maxOrders <= 0 || sources.isEmpty() || servers.isEmpty()) {
            throw new IllegalArgumentException("Invalid simulation configuration");
        }

        this.maxOrders = maxOrders;
        this.sources = new ArrayList<>(sources);
        this.sources.sort(Comparator.comparingInt(OrderSource::getSourceId));
        this.servers = new ArrayList<>(servers);
        this.servers.sort(Comparator.comparingInt(Server::getId));
        this.buffer = buffer;
        this.calendar = new EventCalendar();
        this.statistics = new StatisticsCollector(this.sources);
        this.selection = new SelectionDispatcher(this.servers, this.buffer);
        this.placement = new PlacementDispatcher(this.selection, this.buffer);

        initialize();
    }

    private void initialize() {
        for (OrderSource source : sources) {
            calendar.scheduleArrival(source.nextInterarrival(), source);
        }
    }

    public boolean step() {
        Event event = calendar.popNextEvent();
        if (event == null) {
            return false;
        }

        currentTime = event.getTime();
        stepNumber++;

        if (event.getType() == EventType.ORDER_ARRIVAL) {
            OrderSource source = event.getSource();
            Order order = source.createOrder(currentTime);
            generatedCount++;
            statistics.recordGenerated(order);

            if (generatedCount < maxOrders) {
                calendar.scheduleArrival(currentTime + source.nextInterarrival(), source);
            } else {
                calendar.cancelFutureArrivals();
            }

            ArrivalResult result = placement.handleArrival(order, currentTime);

            if (result.getStartedService() != null) {
                ServiceStartResult started = result.getStartedService();
                statistics.recordServiceStart(order);
                calendar.scheduleCompletion(started.getServer(), order, started.getCompletionTime());

                lastAction = "ORDER_ARRIVAL: " + order + " сразу на П" +
                        started.getServer().getId() + " (Д2П1, ПЗ2), завершение в t=" +
                        fmt(started.getCompletionTime());
            } else {
                PlacementResult placed = result.getBufferPlacement();

                if (placed.wasReplacement()) {
                    Order evicted = placed.getEvictedOrder();
                    evicted.reject(currentTime);
                    statistics.recordRejected(evicted);

                    lastAction = "ORDER_ARRIVAL: " + order + " заменил " + evicted +
                            " в ячейке " + (placed.getInsertedIndex() + 1) +
                            "; " + evicted + " получил отказ (Д1ОО1)";
                } else {
                    lastAction = "ORDER_ARRIVAL: " + order + " записан в ячейку " +
                            (placed.getInsertedIndex() + 1) + " (Д1ОЗ1)";
                }
            }
        } else {
            Server server = event.getServer();
            Order finished = server.completeService(currentTime);
            statistics.recordCompleted(finished);

            ServiceStartResult started = selection.dispatchNext(currentTime);
            if (started != null) {
                statistics.recordServiceStart(started.getOrder());
                calendar.scheduleCompletion(started.getServer(), started.getOrder(),
                        started.getCompletionTime());

                lastAction = "SERVICE_COMPLETED: " + finished + " завершён на П" +
                        server.getId() + "; по FIFO выбран " + started.getOrder() +
                        " и назначен на П" + started.getServer().getId();
            } else {
                lastAction = "SERVICE_COMPLETED: " + finished + " завершён на П" +
                        server.getId() + "; буфер пуст, сервер свободен";
            }
        }

        verifyInvariants();
        return true;
    }

    private void verifyInvariants() {
        int inService = 0;
        for (Server server : servers) {
            if (server.isBusy()) {
                inService++;
            }
        }

        int accounted = statistics.getRejectedCount() + statistics.getCompletedCount() +
                buffer.getSize() + inService;

        if (accounted != generatedCount) {
            throw new IllegalStateException("Order accounting mismatch: " + accounted +
                    " != " + generatedCount);
        }
    }

    private static String fmt(double n) {
        return String.format(java.util.Locale.US, "%.3f", n);
    }

    public boolean isFinished() { return calendar.isEmpty(); }
    public int getGeneratedCount() { return generatedCount; }
    public int getRejectedCount() { return statistics.getRejectedCount(); }
    public int getCompletedCount() { return statistics.getCompletedCount(); }

    public SimulationSnapshot getSnapshot() {
        return new SimulationSnapshot(stepNumber, currentTime, lastAction, sources,
                new ArrayList<>(statistics.getSourceStatistics()), buffer.getSlotsSnapshot(),
                buffer.getPointer(), buffer.getSize(), servers, maxOrders);
    }
}