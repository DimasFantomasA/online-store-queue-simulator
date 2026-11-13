import java.util.concurrent.atomic.AtomicLong;

public final class Event implements Comparable<Event> {
    private static final AtomicLong NEXT_SEQUENCE = new AtomicLong();
    private final long sequence = NEXT_SEQUENCE.getAndIncrement();
    private final EventType type;
    private final double time;
    private final OrderSource source;
    private final Order order;
    private final Server server;

    private Event(EventType type, double time, OrderSource source, Order order, Server server) {
        if (!Double.isFinite(time) || time < 0) {
            throw new IllegalArgumentException("Invalid event time");
        }
        this.type = type;
        this.time = time;
        this.source = source;
        this.order = order;
        this.server = server;
    }

    public static Event arrival(double time, OrderSource source) {
        return new Event(EventType.ORDER_ARRIVAL, time, source, null, null);
    }

    public static Event completion(double time, Server server, Order order) {
        return new Event(EventType.SERVICE_COMPLETED, time, null, order, server);
    }

    public EventType getType() { return type; }
    public double getTime() { return time; }
    public OrderSource getSource() { return source; }
    public Order getOrder() { return order; }
    public Server getServer() { return server; }

    @Override
    public int compareTo(Event other) {
        int byTime = Double.compare(time, other.time);
        if (byTime != 0) return byTime;

        int thisPriority = type == EventType.SERVICE_COMPLETED ? 0 : 1;
        int otherPriority = other.type == EventType.SERVICE_COMPLETED ? 0 : 1;
        int byPriority = Integer.compare(thisPriority, otherPriority);

        return byPriority != 0 ? byPriority : Long.compare(sequence, other.sequence);
    }
}