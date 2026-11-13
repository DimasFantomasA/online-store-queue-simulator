import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public final class EventCalendar {
    private final PriorityQueue<Event> events = new PriorityQueue<>();

    public void scheduleArrival(double time, OrderSource source) {
        events.add(Event.arrival(time, source));
    }

    public void scheduleCompletion(Server server, Order order, double time) {
        events.add(Event.completion(time, server, order));
    }

    public Event popNextEvent() { return events.poll(); }
    public boolean isEmpty() { return events.isEmpty(); }

    public void cancelFutureArrivals() {
        events.removeIf(e -> e.getType() == EventType.ORDER_ARRIVAL);
    }

    public List<Event> getPendingEvents() {
        List<Event> result = new ArrayList<>(events);
        result.sort(null);
        return result;
    }
}