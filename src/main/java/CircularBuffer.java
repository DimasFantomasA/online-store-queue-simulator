public final class CircularBuffer {
    private final Order[] slots;
    private final int capacity;
    private int size;
    private int pointer;

    public CircularBuffer(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Buffer capacity must be > 0");
        this.capacity = capacity;
        this.slots = new Order[capacity];
    }

    public PlacementResult put(Order order) {
        if (order.getStatus() != OrderStatus.WAITING) {
            throw new IllegalArgumentException("Only waiting orders can enter buffer");
        }
        int index = findFreeSlot();
        if (index >= 0) {
            slots[index] = order;
            size++;
            pointer = (index + 1) % capacity;
            return new PlacementResult(index, null);
        }
        Order evicted = slots[pointer];
        int insertedIndex = pointer;
        slots[pointer] = order;
        advancePointer();
        return new PlacementResult(insertedIndex, evicted);
    }

    public Order takeOldestOrder() {
        int index = findOldestSlot();
        if (index == -1) return null;
        Order chosen = slots[index];
        slots[index] = null;
        size--;
        return chosen;
    }

    public boolean isFull() { return size == capacity; }
    public boolean isEmpty() { return size == 0; }
    public int getSize() { return size; }
    public int getCapacity() { return capacity; }
    public int getPointer() { return pointer; }
    public Order[] getSlotsSnapshot() { return slots.clone(); }

    private int findFreeSlot() {
        for (int i = 0; i < capacity; i++) {
            int index = (pointer + i) % capacity;
            if (slots[index] == null) return index;
        }
        return -1;
    }

    private int findOldestSlot() {
        int oldest = -1;
        for (int i = 0; i < capacity; i++) {
            if (slots[i] == null) continue;
            if (oldest == -1 ||
                    slots[i].getArrivalTime() < slots[oldest].getArrivalTime() ||
                    (slots[i].getArrivalTime() == slots[oldest].getArrivalTime() &&
                            slots[i].getId() < slots[oldest].getId())) {
                oldest = i;
            }
        }
        return oldest;
    }

    private void advancePointer() {
        pointer = (pointer + 1) % capacity;
    }
}