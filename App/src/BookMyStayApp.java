import java.util.*;

// Reservation (Booking Intent)
class Reservation {
    private String guestName;
    private String roomType;
    private int nights;

    public Reservation(String guestName, String roomType, int nights) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.nights = nights;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getNights() {
        return nights;
    }
}

// Inventory Service (State Holder + Updater)
class InventoryService {
    private Map<String, Integer> availability;

    public InventoryService() {
        availability = new HashMap<>();
    }

    public void addRoom(String type, int count) {
        availability.put(type, count);
    }

    public int getAvailableRooms(String type) {
        return availability.getOrDefault(type, 0);
    }

    // Update inventory after allocation
    public void decrementRoom(String type) {
        availability.put(type, availability.get(type) - 1);
    }
}

// Booking Request Queue (FIFO)
class BookingRequestQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.offer(r);
    }

    public Reservation getNextRequest() {
        return queue.poll(); // FIFO removal
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

// Booking Service (Core Allocation Logic)
class BookingService {

    private InventoryService inventory;

    // Track allocated room IDs per type
    private Map<String, Set<String>> allocatedRooms;

    // Global set to ensure uniqueness
    private Set<String> allAllocatedRoomIds;

    public BookingService(InventoryService inventory) {
        this.inventory = inventory;
        this.allocatedRooms = new HashMap<>();
        this.allAllocatedRoomIds = new HashSet<>();
    }

    public void processBooking(Reservation reservation) {

        String type = reservation.getRoomType();

        System.out.println("\nProcessing booking for: " + reservation.getGuestName());

        // Step 1: Check availability
        if (inventory.getAvailableRooms(type) <= 0) {
            System.out.println("No rooms available for type: " + type);
            return;
        }

        // Step 2: Generate unique room ID
        String roomId = generateRoomId(type);

        // Step 3: Ensure uniqueness (defensive check)
        if (allAllocatedRoomIds.contains(roomId)) {
            System.out.println("Error: Duplicate room ID detected!");
            return;
        }

        // Step 4: Assign room (atomic logical operation)
        allAllocatedRoomIds.add(roomId);

        allocatedRooms
                .computeIfAbsent(type, k -> new HashSet<>())
                .add(roomId);

        // Step 5: Update inventory immediately
        inventory.decrementRoom(type);

        // Step 6: Confirm reservation
        System.out.println("Booking Confirmed!");
        System.out.println("Guest: " + reservation.getGuestName());
        System.out.println("Room Type: " + type);
        System.out.println("Assigned Room ID: " + roomId);
    }

    // Simple unique ID generator
    private String generateRoomId(String type) {
        return type.substring(0, 2).toUpperCase() + "-" + UUID.randomUUID().toString().substring(0, 5);
    }
}

// Main Class
public class UseCase6RoomAllocationService {

    public static void main(String[] args) {

        // Step 1: Setup Inventory
        InventoryService inventory = new InventoryService();
        inventory.addRoom("Single", 2);
        inventory.addRoom("Suite", 1);

        // Step 2: Setup Booking Queue
        BookingRequestQueue queue = new BookingRequestQueue();

        queue.addRequest(new Reservation("Alice", "Single", 2));
        queue.addRequest(new Reservation("Bob", "Suite", 3));
        queue.addRequest(new Reservation("Charlie", "Single", 1));
        queue.addRequest(new Reservation("David", "Suite", 1)); // should fail

        // Step 3: Booking Service
        BookingService bookingService = new BookingService(inventory);

        // Step 4: Process Requests FIFO
        while (!queue.isEmpty()) {
            Reservation request = queue.getNextRequest();
            bookingService.processBooking(request);
        }
    }
}