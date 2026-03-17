/**
 * UseCase2RoomInitialization
 *
 * This program demonstrates object modeling using abstraction,
 * inheritance, and polymorphism for the Book My Stay application.
 * It initializes different room types and displays their availability.
 *
 * @author Bhumika
 * @version 2.1
 */

// Abstract class
abstract class Room {
    protected String roomType;
    protected double price;
    protected int capacity;

    // Constructor
    public Room(String roomType, double price, int capacity) {
        this.roomType = roomType;
        this.price = price;
        this.capacity = capacity;
    }

    // Abstract method
    public abstract void displayDetails();
}

// Single Room class
class SingleRoom extends Room {

    public SingleRoom() {
        super("Single Room", 2000.0, 1);
    }

    @Override
    public void displayDetails() {
        System.out.println("Room Type: " + roomType);
        System.out.println("Price: ₹" + price);
        System.out.println("Capacity: " + capacity + " person");
    }
}

// Double Room class
class DoubleRoom extends Room {

    public DoubleRoom() {
        super("Double Room", 3500.0, 2);
    }

    @Override
    public void displayDetails() {
        System.out.println("Room Type: " + roomType);
        System.out.println("Price: ₹" + price);
        System.out.println("Capacity: " + capacity + " persons");
    }
}

// Suite Room class
class SuiteRoom extends Room {

    public SuiteRoom() {
        super("Suite Room", 6000.0, 4);
    }

    @Override
    public void displayDetails() {
        System.out.println("Room Type: " + roomType);
        System.out.println("Price: ₹" + price);
        System.out.println("Capacity: " + capacity + " persons");
    }
}

// Main class
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("=======================================");
        System.out.println("        Welcome to Book My Stay        ");
        System.out.println("   Hotel Booking Management System     ");
        System.out.println("             Version 2.1               ");
        System.out.println("=======================================\n");

        // Creating room objects using polymorphism
        Room single = new SingleRoom();
        Room doubleroom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Static availability variables
        int singleAvailable = 10;
        int doubleAvailable = 5;
        int suiteAvailable = 2;

        // Display details
        System.out.println("----- Room Details & Availability -----\n");

        single.displayDetails();
        System.out.println("Available Rooms: " + singleAvailable);
        System.out.println("---------------------------------------");

        doubleroom.displayDetails();
        System.out.println("Available Rooms: " + doubleAvailable);
        System.out.println("---------------------------------------");

        suite.displayDetails();
        System.out.println("Available Rooms: " + suiteAvailable);
        System.out.println("---------------------------------------");

        System.out.println("\nApplication Terminated Successfully!");
    }
}