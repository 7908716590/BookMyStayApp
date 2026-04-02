import java.util.*;

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

// Booking History (Data Storage)
class BookingHistory {
    private List<Reservation> reservations = new ArrayList<>();

    // Store confirmed booking
    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    // Retrieve all bookings
    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations); // defensive copy
    }
}

// Reporting Service (Separate Concern)
class BookingReportService {

    // Generate full report
    public void generateFullReport(List<Reservation> reservations) {
        System.out.println("\n--- Booking History Report ---");

        if (reservations.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (Reservation r : reservations) {
            System.out.println(r);
        }
    }

    // Generate summary report
    public void generateSummaryReport(List<Reservation> reservations) {
        System.out.println("\n--- Booking Summary ---");

        int totalBookings = reservations.size();
        int totalNights = 0;

        for (Reservation r : reservations) {
            totalNights += r.getNights();
        }

        System.out.println("Total Bookings: " + totalBookings);
        System.out.println("Total Nights Booked: " + totalNights);
    }
}

// Main Class
public class UseCase8BookingHistoryReport {

    public static void main(String[] args) {

        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService();

        // Simulate confirmed bookings
        Reservation r1 = new Reservation("Alice", "Deluxe", 3);
        Reservation r2 = new Reservation("Bob", "Standard", 2);
        Reservation r3 = new Reservation("Charlie", "Suite", 5);

        // Store in booking history
        history.addReservation(r1);
        history.addReservation(r2);
        history.addReservation(r3);

        // Admin retrieves booking history
        List<Reservation> storedBookings = history.getAllReservations();

        // Generate reports
        reportService.generateFullReport(storedBookings);
        reportService.generateSummaryReport(storedBookings);
    }
}