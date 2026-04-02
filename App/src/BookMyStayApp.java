import java.io.*;
import java.util.*;

// Reservation (Serializable)
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private static int counter = 1;

    private int id;
    private String customerName;
    private String roomType;

    public Reservation(String customerName, String roomType) {
        this.id = counter++;
        this.customerName = customerName;
        this.roomType = roomType;
    }

    public int getId() {
        return id;
    }

    public String getRoomType() {
        return roomType;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + id +
                ", Customer: " + customerName +
                ", Room: " + roomType;
    }
}

// Wrapper class for persistence
class SystemState implements Serializable {
    private static final long serialVersionUID = 1L;

    Map<String, Integer> inventory;
    List<Reservation> reservations;

    public SystemState(Map<String, Integer> inventory, List<Reservation> reservations) {
        this.inventory = inventory;
        this.reservations = reservations;
    }
}

// Persistence Service
class PersistenceService {

    private static final String FILE_NAME = "system_state.ser";

    // Save state
    public void save(SystemState state) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            oos.writeObject(state);
            System.out.println("System state saved successfully.");

        } catch (IOException e) {
            System.out.println("Error saving system state: " + e.getMessage());
        }
    }

    // Load state
    public SystemState load() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            System.out.println("System state loaded successfully.");
            return (SystemState) ois.readObject();

        } catch (FileNotFoundException e) {
            System.out.println("No previous state found. Starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading state. Starting with safe defaults.");
        }
        return null;
    }
}

// Booking System
class BookingSystem {

    private Map<String, Integer> inventory;
    private List<Reservation> reservations;

    public BookingSystem() {
        inventory = new HashMap<>();
        reservations = new ArrayList<>();

        inventory.put("Deluxe", 2);
        inventory.put("Standard", 1);
    }

    public void restore(SystemState state) {
        if (state != null) {
            this.inventory = state.inventory;
            this.reservations = state.reservations;
        }
    }

    public SystemState snapshot() {
        return new SystemState(inventory, reservations);
    }

    public void bookRoom(String customerName, String roomType) {
        if (!inventory.containsKey(roomType)) {
            System.out.println("Invalid room type.");
            return;
        }

        int available = inventory.get(roomType);

        if (available > 0) {
            inventory.put(roomType, available - 1);

            Reservation r = new Reservation(customerName, roomType);
            reservations.add(r);

            System.out.println("Booking successful: " + r);
        } else {
            System.out.println("No rooms available for " + roomType);
        }
    }

    public void printState() {
        System.out.println("\n--- Current Inventory ---");
        for (String key : inventory.keySet()) {
            System.out.println(key + ": " + inventory.get(key));
        }

        System.out.println("\n--- Booking History ---");
        for (Reservation r : reservations) {
            System.out.println(r);
        }
    }
}

// Main Class
public class UseCase12DataPersistenceRecovery {

    public static void main(String[] args) {

        PersistenceService persistence = new PersistenceService();
        BookingSystem system = new BookingSystem();

        // Step 1: Load previous state
        SystemState loadedState = persistence.load();
        system.restore(loadedState);

        // Step 2: Continue operations
        system.bookRoom("Alice", "Deluxe");
        system.bookRoom("Bob", "Standard");

        system.printState();

        // Step 3: Save state before shutdown
        persistence.save(system.snapshot());
    }
}