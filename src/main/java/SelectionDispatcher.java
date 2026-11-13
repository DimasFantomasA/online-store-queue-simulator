import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class SelectionDispatcher {
    private final List<Server> servers;
    private final CircularBuffer buffer;

    public SelectionDispatcher(List<Server> servers, CircularBuffer buffer) {
        this.servers = new ArrayList<>(servers);
        this.servers.sort(Comparator.comparingInt(Server::getId));
        this.buffer = buffer;
    }

    public Server findFreeServer() {
        for (Server server : servers) {
            if (!server.isBusy()) return server;
        }
        return null;
    }

    public ServiceStartResult dispatchNext(double time) {
        if (buffer.isEmpty()) return null;
        Server free = findFreeServer();
        if (free == null) return null;
        Order oldest = buffer.takeOldestOrder();
        double finishTime = free.startService(oldest, time);
        return new ServiceStartResult(free, oldest, finishTime);
    }
}