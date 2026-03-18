/**
 * UseCase3InventorySetup
 * Demonstrates centralized room inventory management using HashMap.
 * Refactored version of the Hotel Booking App.
 *
 * @author Your Name
 * @version 3.1
 */

import java.util.HashMap;
import java.util.Map;

// RoomInventory class encapsulates availability logic
class RoomInventory {
    private Map<String, Integer> availability;

    public RoomInventory() {
        availability = new HashMap<>();
    }

    // Register room type with initial availability
    public void addRoomType(String type, int count) {
        availability.put(type, count);
    }

    // Retrieve availability for a specific room type
    public int getAvailability(String type) {
        return availability.getOrDefault(type, 0);
    }

    // Update availability (controlled mutation)
    public void updateAvailability(String type, int newCount) {
        if (availability.containsKey(type)) {
            availability.put(type, newCount);
        } else {
            System.out.println("Room type not found: " + type);
        }
    }

    // Display current inventory state
    public void displayInventory() {
        System.out.println("\n--- Current Room Inventory ---");
        for (Map.Entry<String, Integer> entry : availability.entrySet()) {
            System.out.println("Room Type: " + entry.getKey() + " | Available: " + entry.getValue());
        }
    }
}

// Main Application Entry
public class UseCase3InventorySetup {
    public static void main(String[] args) {
        System.out.println("Welcome to Book My Stay!");
        System.out.println("Hotel Booking System v3.1");
        System.out.println("Centralized Room Inventory Management Initialized...\n");

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 5);
        inventory.addRoomType("Double Room", 3);
        inventory.addRoomType("Suite Room", 2);

        // Display initial inventory
        inventory.displayInventory();

        // Controlled update
        System.out.println("\nUpdating inventory...");
        inventory.updateAvailability("Double Room", 4);

        // Display updated inventory
        inventory.displayInventory();

        System.out.println("\nApplication terminated successfully.");
    }
}
