import java.util.*;

// Reservation (Represents booking intent)
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

    @Override
    public String toString() {
        return "Guest: " + guestName +
                ", Room Type: " + roomType +
                ", Nights: " + nights;
    }
}

// Booking Request Queue (FIFO)
class BookingRequestQueue {
    private Queue<Reservation> queue;

    public BookingRequestQueue() {
        queue = new LinkedList<>();
    }

    // Add request to queue (enqueue)
    public void addRequest(Reservation reservation) {
        queue.offer(reservation);
        System.out.println("Booking request added: " + reservation);
    }

    // View all requests (read-only)
    public void viewRequests() {
        if (queue.isEmpty()) {
            System.out.println("No booking requests in queue.");
            return;
        }

        System.out.println("\nBooking Requests in Queue (FIFO Order):\n");

        for (Reservation r : queue) {
            System.out.println(r);
        }
    }

    // Peek next request (without removing)
    public Reservation peekNextRequest() {
        return queue.peek();
    }

    // Get queue size
    public int getQueueSize() {
        return queue.size();
    }
}

// Main Class
public class UseCase5BookingRequestQueue {

    public static void main(String[] args) {

        // Step 1: Create Booking Queue
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // Step 2: Simulate Guest Booking Requests
        Reservation r1 = new Reservation("Alice", "Single", 2);
        Reservation r2 = new Reservation("Bob", "Suite", 3);
        Reservation r3 = new Reservation("Charlie", "Double", 1);

        // Step 3: Add Requests (FIFO Order)
        bookingQueue.addRequest(r1);
        bookingQueue.addRequest(r2);
        bookingQueue.addRequest(r3);

        // Step 4: View Queue
        bookingQueue.viewRequests();

        // Step 5: Peek next request (without removing)
        System.out.println("\nNext request to process:");
        System.out.println(bookingQueue.peekNextRequest());

        // Step 6: Queue size
        System.out.println("\nTotal requests in queue: " + bookingQueue.getQueueSize());
    }
}