public final class PlacementDispatcher {
    private final SelectionDispatcher selection;
    private final CircularBuffer buffer;

    public PlacementDispatcher(SelectionDispatcher selection, CircularBuffer buffer) {
        this.selection = selection;
        this.buffer = buffer;
    }

    public ArrivalResult handleArrival(Order order, double time) {
        Server server = selection.findFreeServer();
        if (server != null) {
            double finishTime = server.startService(order, time);
            return ArrivalResult.started(new ServiceStartResult(server, order, finishTime));
        }
        return ArrivalResult.buffered(buffer.put(order));
    }
}