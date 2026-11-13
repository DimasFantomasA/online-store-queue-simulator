public final class ArrivalResult {
    private final ServiceStartResult startedService;
    private final PlacementResult bufferPlacement;

    private ArrivalResult(ServiceStartResult startedService, PlacementResult bufferPlacement) {
        this.startedService = startedService;
        this.bufferPlacement = bufferPlacement;
    }

    public static ArrivalResult started(ServiceStartResult started) {
        return new ArrivalResult(started, null);
    }

    public static ArrivalResult buffered(PlacementResult placement) {
        return new ArrivalResult(null, placement);
    }

    public ServiceStartResult getStartedService() { return startedService; }
    public PlacementResult getBufferPlacement() { return bufferPlacement; }
}