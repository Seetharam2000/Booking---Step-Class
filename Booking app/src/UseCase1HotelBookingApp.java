// File: UseCase12DataPersistenceRecovery.java

import java.io.*;
import java.util.*;

// Domain Model: Reservation
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String guestName;
    private String roomType;
    private String roomId;

    public Reservation(String guestName, String roomType, String roomId) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getRoomId() {
        return roomId;
    }

    @Override
    public String toString() {
        return "Reservation -> Guest: " + guestName +
                ", Room Type: " + roomType +
                ", Room ID: " + roomId;
    }
}

// Inventory Service
class InventoryService implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Integer> availability;

    public InventoryService() {
        availability = new HashMap<>();
    }

    public void addRoomType(String type, int count) {
        availability.put(type, count);
    }

    public boolean isAvailable(String type) {
        return availability.getOrDefault(type, 0) > 0;
    }

    public void decrementAvailability(String type) {
        if (isAvailable(type)) {
            availability.put(type, availability.get(type) - 1);
        }
    }

    public void incrementAvailability(String type) {
        availability.put(type, availability.getOrDefault(type, 0) + 1);
    }

    public Map<String, Integer> getAvailabilitySnapshot() {
        return new HashMap<>(availability);
    }

    @Override
    public String toString() {
        return "Inventory Snapshot: " + availability;
    }
}

// Booking History
class BookingHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Reservation> confirmedBookings;

    public BookingHistory() {
        confirmedBookings = new ArrayList<>();
    }

    public void addReservation(Reservation reservation) {
        confirmedBookings.add(reservation);
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(confirmedBookings);
    }

    @Override
    public String toString() {
        return "Booking History: " + confirmedBookings;
    }
}

// Persistence Service
class PersistenceService {
    private static final String FILE_NAME = "system_state.ser";

    public static void saveState(InventoryService inventory, BookingHistory history) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(inventory);
            oos.writeObject(history);
            System.out.println("System state saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving system state: " + e.getMessage());
        }
    }

    public static Object[] loadState() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            InventoryService inventory = (InventoryService) ois.readObject();
            BookingHistory history = (BookingHistory) ois.readObject();
            System.out.println("System state loaded successfully.");
            return new Object[]{inventory, history};
        } catch (FileNotFoundException e) {
            System.out.println("No saved state found. Starting fresh.");
            return new Object[]{new InventoryService(), new BookingHistory()};
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading system state: " + e.getMessage());
            return new Object[]{new InventoryService(), new BookingHistory()};
        }
    }
}

// Main class
public class UseCase12DataPersistenceRecovery {
    public static void main(String[] args) {
        // Load previous state if available
        Object[] state = PersistenceService.loadState();
        InventoryService inventory = (InventoryService) state[0];
        BookingHistory history = (BookingHistory) state[1];

        // Setup inventory if fresh start
        if (inventory.getAvailabilitySnapshot().isEmpty()) {
            inventory.addRoomType("Deluxe", 2);
            inventory.addRoomType("Standard", 1);
        }

        // Add new reservations
        Reservation r1 = new Reservation("Alice", "Deluxe", "DE-123456");
        Reservation r2 = new Reservation("Bob", "Standard", "ST-654321");

        if (inventory.isAvailable(r1.getRoomType())) {
            inventory.decrementAvailability(r1.getRoomType());
            history.addReservation(r1);
        }

        if (inventory.isAvailable(r2.getRoomType())) {
            inventory.decrementAvailability(r2.getRoomType());
            history.addReservation(r2);
        }

        // Display current state
        System.out.println("\nCurrent Inventory: " + inventory.getAvailabilitySnapshot());
        System.out.println("Current Booking History:");
        for (Reservation r : history.getAllReservations()) {
            System.out.println(r);
        }

        // Save state before shutdown
        PersistenceService.saveState(inventory, history);
    }
}
