public final class Order {
    private final int id;
    private final int sourceId;
    private final double arrivalTime;
    private Double serviceStartTime;
    private Double completionTime;
    private Double rejectionTime;
    private OrderStatus status = OrderStatus.WAITING;

    public Order(int id, int sourceId, double arrivalTime) {
        this.id = id;
        this.sourceId = sourceId;
        this.arrivalTime = arrivalTime;
    }

    public int getId() { return id; }
    public int getSourceId() { return sourceId; }
    public double getArrivalTime() { return arrivalTime; }
    public Double getServiceStartTime() { return serviceStartTime; }
    public Double getCompletionTime() { return completionTime; }
    public Double getRejectionTime() { return rejectionTime; }
    public OrderStatus getStatus() { return status; }

    public Double getWaitingTime() {
        return serviceStartTime == null ? null : serviceStartTime - arrivalTime;
    }

    public Double getServiceTime() {
        return completionTime == null || serviceStartTime == null
                ? null : completionTime - serviceStartTime;
    }

    public Double getTimeInSystem() {
        return completionTime == null ? null : completionTime - arrivalTime;
    }

    public void startService(double time) {
        if (status != OrderStatus.WAITING || time < arrivalTime) {
            throw new IllegalStateException("Order cannot start service");
        }
        serviceStartTime = time;
        status = OrderStatus.PROCESSING;
    }

    public void complete(double time) {
        if (status != OrderStatus.PROCESSING || time < serviceStartTime) {
            throw new IllegalStateException("Order cannot complete service");
        }
        completionTime = time;
        status = OrderStatus.COMPLETED;
    }

    public void reject(double time) {
        if (status != OrderStatus.WAITING || time < arrivalTime) {
            throw new IllegalStateException("Only waiting orders can be rejected");
        }
        rejectionTime = time;
        status = OrderStatus.REJECTED;
    }

    @Override
    public String toString() {
        return "#" + id + "(И" + sourceId + ")";
    }
}