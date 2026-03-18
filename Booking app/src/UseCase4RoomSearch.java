// File: UseCase4RoomSearch.java

import java.util.*;

// Domain Model: Room
class Room {
    private String type;
    private double price;
    private String amenities;

    public Room(String type, double price, String amenities) {
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

    public String getAmenities() {
        return amenities;
    }

    @Override
    public String toString() {
        return "Room Type: " + type +
                ", Price: " + price +
                ", Amenities: " + amenities;
    }
}

// Inventory acts as state holder
class Inventory {
    private Map<String, Integer> availability;

    public Inventory() {
        availability = new HashMap<>();
    }

    public void addRoomType(String type, int count) {
        availability.put(type, count);
    }

    public int getAvailability(String type) {
        return availability.getOrDefault(type, 0);
    }

    public Map<String, Integer> getAllAvailability() {
        // Defensive copy to ensure read-only access
        return new HashMap<>(availability);
    }
}

// Search Service (read-only)
class SearchService {
    private Inventory inventory;
    private Map<String, Room> roomDetails;

    public SearchService(Inventory inventory, Map<String, Room> roomDetails) {
        this.inventory = inventory;
        this.roomDetails = roomDetails;
    }

    public List<Room> searchAvailableRooms() {
        List<Room> availableRooms = new ArrayList<>();
        Map<String, Integer> currentAvailability = inventory.getAllAvailability();

        for (String type : currentAvailability.keySet()) {
            int count = currentAvailability.get(type);
            if (count > 0 && roomDetails.containsKey(type)) {
                availableRooms.add(roomDetails.get(type));
            }
        }
        return availableRooms;
    }
}

// Main class
public class UseCase4RoomSearch {
    public static void main(String[] args) {
        // Setup inventory
        Inventory inventory = new Inventory();
        inventory.addRoomType("Deluxe", 3);
        inventory.addRoomType("Suite", 0); // unavailable
        inventory.addRoomType("Standard", 5);

        // Setup room details
        Map<String, Room> roomDetails = new HashMap<>();
        roomDetails.put("Deluxe", new Room("Deluxe", 150.0, "WiFi, TV, Mini-bar"));
        roomDetails.put("Suite", new Room("Suite", 300.0, "WiFi, TV, Mini-bar, Jacuzzi"));
        roomDetails.put("Standard", new Room("Standard", 100.0, "WiFi, TV"));

        // Search service
        SearchService searchService = new SearchService(inventory, roomDetails);

        // Guest initiates search
        System.out.println("Available Rooms:");
        List<Room> availableRooms = searchService.searchAvailableRooms();
        for (Room room : availableRooms) {
            System.out.println(room);
        }
    }
}
