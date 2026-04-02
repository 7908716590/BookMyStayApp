import java.util.*;

// Room Domain Model
class Room {
    private String type;
    private double price;
    private List<String> amenities;

    public Room(String type, double price, List<String> amenities) {
        this.type = type;
        this.price = price;
        this.amenities = amenities;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public List<String> getAmenities() {
        return amenities;
    }
}

// Inventory (State Holder)
class Inventory {
    private Map<String, Integer> roomAvailability;

    public Inventory() {
        roomAvailability = new HashMap<>();
    }

    public void addRoom(String type, int count) {
        roomAvailability.put(type, count);
    }

    // Read-only access
    public int getAvailability(String type) {
        return roomAvailability.getOrDefault(type, 0);
    }

    public Map<String, Integer> getAllAvailability() {
        return Collections.unmodifiableMap(roomAvailability); // prevents modification
    }
}

// Search Service (Read-Only Logic)
class SearchService {

    public void searchAvailableRooms(Inventory inventory, Map<String, Room> roomCatalog) {

        System.out.println("Available Rooms:\n");

        for (Map.Entry<String, Integer> entry : inventory.getAllAvailability().entrySet()) {

            String roomType = entry.getKey();
            int availableCount = entry.getValue();

            // Validation: Only show available rooms
            if (availableCount > 0) {

                Room room = roomCatalog.get(roomType);

                // Defensive check
                if (room != null) {
                    System.out.println("Room Type: " + room.getType());
                    System.out.println("Price: $" + room.getPrice());
                    System.out.println("Amenities: " + String.join(", ", room.getAmenities()));
                    System.out.println("Available: " + availableCount);
                    System.out.println("---------------------------");
                }
            }
        }
    }
}

// Main Class
public class UseCase4RoomSearch {

    public static void main(String[] args) {

        // Step 1: Create Room Catalog
        Map<String, Room> roomCatalog = new HashMap<>();

        roomCatalog.put("Single",
                new Room("Single", 1000,
                        Arrays.asList("WiFi", "TV")));

        roomCatalog.put("Double",
                new Room("Double", 2000,
                        Arrays.asList("WiFi", "TV", "AC")));

        roomCatalog.put("Suite",
                new Room("Suite", 5000,
                        Arrays.asList("WiFi", "TV", "AC", "Mini Bar")));

        // Step 2: Setup Inventory
        Inventory inventory = new Inventory();
        inventory.addRoom("Single", 5);
        inventory.addRoom("Double", 0); // should not appear
        inventory.addRoom("Suite", 2);

        // Step 3: Search
        SearchService searchService = new SearchService();
        searchService.searchAvailableRooms(inventory, roomCatalog);
    }
}