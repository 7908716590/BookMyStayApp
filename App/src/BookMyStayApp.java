import java.util.*;

// Custom Exception
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// Reservation class
class Reservation {
    private static int counter = 1;

    private int reservationId;
    private String customerName;
    private String roomType;
    private int nights;
    private boolean isCancelled;

    public Reservation(String customerName, String roomType, int nights) {
        this.reservationId = counter++;
        this.customerName = customerName;
        this.roomType = roomType;
        this.nights = nights;
        this.isCancelled = false;
    }

    public int getReservationId() {
        return reservationId;
    }

    public String getRoomType() {
        return roomType;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void cancel() {
        this.isCancelled = true;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + reservationId +
                ", Customer: " + customerName +
                ", Room: " + roomType +
                ", Nights: " + nights +
                ", Status: " + (isCancelled ? "CANCELLED" : "CONFIRMED");
    }
}

// Booking History
class BookingHistory {
    private List<Reservation> reservations = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    public Reservation findById(int id) {
        for (Reservation r : reservations) {
            if (r.getReservationId() == id) {
                return r;
            }
        }
        return null;
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }
}

// Booking Service
class BookingService {

    private Map<String, Integer> inventory;
    private BookingHistory history;

    // Stack for rollback (LIFO)
    private Stack<String> rollbackStack = new Stack<>();

    public BookingService(Map<String, Integer> inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }

    // Booking
    public Reservation bookRoom(String customerName, String roomType, int nights)
            throws InvalidBookingException {

        if (!inventory.containsKey(roomType)) {
            throw new InvalidBookingException("Invalid room type.");
        }

        if (inventory.get(roomType) <= 0) {
            throw new InvalidBookingException("No rooms available.");
        }

        // Reduce inventory
        inventory.put(roomType, inventory.get(roomType) - 1);

        // Push into rollback stack
        rollbackStack.push(roomType);

        Reservation reservation = new Reservation(customerName, roomType, nights);
        history.addReservation(reservation);

        System.out.println("Booking successful: " + reservation);
        return reservation;
    }

    // Cancellation with rollback
    public void cancelBooking(int reservationId) {

        try {
            Reservation reservation = history.findById(reservationId);

            // Validation
            if (reservation == null) {
                throw new InvalidBookingException("Reservation not found.");
            }

            if (reservation.isCancelled()) {
                throw new InvalidBookingException("Reservation already cancelled.");
            }

            // Step 1: Mark as cancelled
            reservation.cancel();

            // Step 2: Rollback using stack (LIFO)
            if (!rollbackStack.isEmpty()) {
                String roomType = rollbackStack.pop();

                // Step 3: Restore inventory
                inventory.put(roomType, inventory.get(roomType) + 1);
            }

            System.out.println("Cancellation successful for Reservation ID: " + reservationId);

        } catch (InvalidBookingException e) {
            System.out.println("Cancellation failed: " + e.getMessage());
        }
    }

    public void printInventory() {
        System.out.println("\nCurrent Inventory:");
        for (String key : inventory.keySet()) {
            System.out.println(key + ": " + inventory.get(key));
        }
    }
}

// Main Class
public class UseCase10BookingCancellation {

    public static void main(String[] args) {

        Map<String, Integer> inventory = new HashMap<>();
        inventory.put("Deluxe", 2);
        inventory.put("Standard", 1);

        BookingHistory history = new BookingHistory();
        BookingService service = new BookingService(inventory, history);

        try {
            // Bookings
            Reservation r1 = service.bookRoom("Alice", "Deluxe", 2);
            Reservation r2 = service.bookRoom("Bob", "Standard", 1);

            service.printInventory();

            // Valid cancellation
            service.cancelBooking(r1.getReservationId());

            // Invalid cancellation (already cancelled)
            service.cancelBooking(r1.getReservationId());

            // Invalid cancellation (non-existent)
            service.cancelBooking(999);

            service.printInventory();

            // Display history
            System.out.println("\n--- Booking History ---");
            for (Reservation r : history.getAllReservations()) {
                System.out.println(r);
            }

        } catch (InvalidBookingException e) {
            System.out.println("Booking failed: " + e.getMessage());
        }
    }
}