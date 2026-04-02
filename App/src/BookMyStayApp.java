import java.util.*;

// Booking Request
class BookingRequest {
    String customerName;
    String roomType;
    int nights;

    public BookingRequest(String customerName, String roomType, int nights) {
        this.customerName = customerName;
        this.roomType = roomType;
        this.nights = nights;
    }
}

// Reservation
class Reservation {
    private static int counter = 1;

    private int id;
    private String customerName;
    private String roomType;

    public Reservation(String customerName, String roomType) {
        this.id = counter++;
        this.customerName = customerName;
        this.roomType = roomType;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + id +
                ", Customer: " + customerName +
                ", Room: " + roomType;
    }
}

// Shared Booking System
class BookingSystem {

    private Map<String, Integer> inventory = new HashMap<>();
    private Queue<BookingRequest> requestQueue = new LinkedList<>();

    public BookingSystem() {
        inventory.put("Deluxe", 2);
        inventory.put("Standard", 1);
    }

    // Add request (producer)
    public synchronized void addRequest(BookingRequest request) {
        requestQueue.add(request);
        notify(); // wake waiting threads
    }

    // Get request (consumer)
    public synchronized BookingRequest getRequest() {
        while (requestQueue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return requestQueue.poll();
    }

    // Critical Section (Thread-safe booking)
    public synchronized void processBooking(BookingRequest request) {

        String roomType = request.roomType;

        System.out.println(Thread.currentThread().getName() +
                " processing " + request.customerName);

        if (!inventory.containsKey(roomType)) {
            System.out.println("Invalid room type for " + request.customerName);
            return;
        }

        int available = inventory.get(roomType);

        if (available > 0) {
            // Simulate delay (to expose race conditions if unsynchronized)
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            inventory.put(roomType, available - 1);

            Reservation r = new Reservation(request.customerName, roomType);
            System.out.println("SUCCESS: " + r);
        } else {
            System.out.println("FAILED: No rooms for " + request.customerName);
        }
    }

    public void printInventory() {
        System.out.println("\nFinal Inventory:");
        for (String key : inventory.keySet()) {
            System.out.println(key + ": " + inventory.get(key));
        }
    }
}

// Worker Thread
class BookingProcessor extends Thread {

    private BookingSystem system;

    public BookingProcessor(BookingSystem system, String name) {
        super(name);
        this.system = system;
    }

    @Override
    public void run() {
        for (int i = 0; i < 3; i++) { // each thread processes 3 requests
            BookingRequest request = system.getRequest();
            system.processBooking(request);
        }
    }
}

// Main Class
public class UseCase11ConcurrentBookingSimulation {

    public static void main(String[] args) {

        BookingSystem system = new BookingSystem();

        // Create worker threads
        BookingProcessor t1 = new BookingProcessor(system, "Thread-1");
        BookingProcessor t2 = new BookingProcessor(system, "Thread-2");

        t1.start();
        t2.start();

        // Simulate concurrent guest requests
        system.addRequest(new BookingRequest("Alice", "Deluxe", 2));
        system.addRequest(new BookingRequest("Bob", "Deluxe", 1));
        system.addRequest(new BookingRequest("Charlie", "Standard", 1));
        system.addRequest(new BookingRequest("David", "Standard", 1));
        system.addRequest(new BookingRequest("Eve", "Deluxe", 1));
        system.addRequest(new BookingRequest("Frank", "Deluxe", 1));

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        system.printInventory();
    }
}