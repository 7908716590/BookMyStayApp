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

    public Reservation(String customerName, String roomType, int nights) {
        this.reservationId = counter++;
        this.customerName = customerName;
        this.roomType = roomType;
        this.nights = nights;
    }

    public int getReservationId() {
        return reservationId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getNights() {
        return nights;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + reservationId +
                ", Customer: " + customerName +
                ", Room: " + roomType +
                ", Nights: " + nights;
    }
}

// Booking History
class BookingHistory {
    private List<Reservation> reservations = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }
}

// Validator Class (Fail-Fast)
class BookingValidator {

    private Map<String, Integer> inventory;

    public BookingValidator(Map<String, Integer> inventory) {
        this.inventory = inventory;
    }

    public void validate(String customerName, String roomType, int nights)
            throws InvalidBookingException {

        // Validate customer name
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new InvalidBookingException("Customer name cannot be empty.");
        }

        // Validate room type
        if (!inventory.containsKey(roomType)) {
            throw new InvalidBookingException("Invalid room type: " + roomType);
        }

        // Validate nights
        if (nights <= 0) {
            throw new InvalidBookingException("Number of nights must be greater than zero.");
        }

        // Validate availability
        int available = inventory.get(roomType);
        if (available <= 0) {
            throw new InvalidBookingException("No rooms available for type: " + roomType);
        }
    }
}

// Booking Service
class BookingService {

    private Map<String, Integer> inventory;
    private BookingHistory history;
    private BookingValidator validator;

    public BookingService(Map<String, Integer> inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
        this.validator = new BookingValidator(inventory);
    }

    public void bookRoom(String customerName, String roomType, int nights) {
        try {
            // Step 1: Validate input (Fail-Fast)
            validator.validate(customerName, roomType, nights);

            // Step 2: Safe state update
            int available = inventory.get(roomType);
            inventory.put(roomType, available - 1);

            // Step 3: Create reservation
            Reservation reservation = new Reservation(customerName, roomType, nights);

            // Step 4: Store in history
            history.addReservation(reservation);

            System.out.println("Booking successful: " + reservation);

        } catch (InvalidBookingException e) {
            // Graceful failure
            System.out.println("Booking failed: " + e.getMessage());
        }
    }
}

// Main Class
public class UseCase9ErrorHandlingValidation {

    public static void main(String[] args) {

        // Room inventory
        Map<String, Integer> inventory = new HashMap<>();
        inventory.put("Deluxe", 2);
        inventory.put("Standard", 1);
        inventory.put("Suite", 0);

        BookingHistory history = new BookingHistory();
        BookingService service = new BookingService(inventory, history);

        // Test cases

        // ✅ Valid booking
        service.bookRoom("Alice", "Deluxe", 3);

        // ❌ Invalid room type
        service.bookRoom("Bob", "Premium", 2);

        // ❌ No availability
        service.bookRoom("Charlie", "Suite", 1);

        // ❌ Invalid nights
        service.bookRoom("David", "Standard", 0);

        // ❌ Empty name
        service.bookRoom("", "Deluxe", 2);

        // Show stored bookings (only valid ones)
        System.out.println("\n--- Booking History ---");
        for (Reservation r : history.getAllReservations()) {
            System.out.println(r);
        }
    }
}