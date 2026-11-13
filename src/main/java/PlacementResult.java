public final class PlacementResult {
    private final int insertedIndex;
    private final Order evictedOrder;
    private final PlacementStatus status;

    public PlacementResult(int insertedIndex, Order evictedOrder) {
        this.insertedIndex = insertedIndex;
        this.evictedOrder = evictedOrder;
        this.status = evictedOrder == null ? PlacementStatus.STORED : PlacementStatus.REPLACED;
    }

    public int getInsertedIndex() { return insertedIndex; }
    public Order getEvictedOrder() { return evictedOrder; }
    public PlacementStatus getStatus() { return status; }
    public boolean wasReplacement() { return status == PlacementStatus.REPLACED; }
}