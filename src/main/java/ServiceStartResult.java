public final class ServiceStartResult {
    private final Server server;
    private final Order order;
    private final double completionTime;

    public ServiceStartResult(Server server, Order order, double completionTime) {
        this.server = server;
        this.order = order;
        this.completionTime = completionTime;
    }

    public Server getServer() { return server; }
    public Order getOrder() { return order; }
    public double getCompletionTime() { return completionTime; }
}