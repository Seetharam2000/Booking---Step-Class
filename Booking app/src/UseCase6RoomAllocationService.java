// File: UseCase6RoomAllocationService.java

import java.util.*;

// Domain Model: Reservation
class Reservation {
    private String guestName;
    private String roomType;
    private String roomId; // Assigned upon confirmation

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void assignRoomId(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "Confirmed Reservation -> Guest: " + guestName +
                ", Room Type: " + roomType +
                ", Room ID: " + roomId;
    }
}

// Inventory Service
class InventoryService {
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

    public Map<String, Integer> getAvailabilitySnapshot() {
        return new HashMap<>(availability);
    }
}

// Booking Service
class BookingService {
    private Queue<Reservation> requestQueue;
    private InventoryService inventory;
    private Map<String, Set<String>> allocatedRooms;

    public BookingService(InventoryService inventory) {
        this.inventory = inventory;
        this.requestQueue = new LinkedList<>();
        this.allocatedRooms = new HashMap<>();
    }

    // Add booking request
    public void addRequest(Reservation reservation) {
        requestQueue.offer(reservation);
        System.out.println("Request queued: Guest " + reservation.getGuestName() +
                " wants " + reservation.getRoomType());
    }

    // Process requests in FIFO order
    public void processRequests() {
        System.out.println("\nProcessing Booking Requests...");
        while (!requestQueue.isEmpty()) {
            Reservation reservation = requestQueue.poll();
            String type = reservation.getRoomType();

            if (inventory.isAvailable(type)) {
                // Generate unique room ID
                String roomId = generateUniqueRoomId(type);

                // Assign room ID
                reservation.assignRoomId(roomId);

                // Record allocation
                allocatedRooms.putIfAbsent(type, new HashSet<>());
                allocatedRooms.get(type).add(roomId);

                // Update inventory
                inventory.decrementAvailability(type);

                System.out.println(reservation);
            } else {
                System.out.println("Reservation failed for Guest: " +
                        reservation.getGuestName() +
                        " (Room Type: " + type + " unavailable)");
            }
        }
    }

    // Generate unique room ID
    private String generateUniqueRoomId(String type) {
        String roomId;
        do {
            roomId = type.substring(0, 2).toUpperCase() + "-" + UUID.randomUUID().toString().substring(0, 6);
        } while (allocatedRooms.containsKey(type) && allocatedRooms.get(type).contains(roomId));
        return roomId;
    }
}

// Main class
public class UseCase6RoomAllocationService {
    public static void main(String[] args) {
        // Setup inventory
        InventoryService inventory = new InventoryService();
        inventory.addRoomType("Deluxe", 2);
        inventory.addRoomType("Standard", 1);
        inventory.addRoomType("Suite", 0); // unavailable

        // Setup booking service
        BookingService bookingService = new BookingService(inventory);

        // Guests submit requests
        bookingService.addRequest(new Reservation("Alice", "Deluxe"));
        bookingService.addRequest(new Reservation("Bob", "Standard"));
        bookingService.addRequest(new Reservation("Charlie", "Suite"));
        bookingService.addRequest(new Reservation("Diana", "Deluxe"));
        bookingService.addRequest(new Reservation("Ethan", "Deluxe")); // exceeds availability

        // Process requests
        bookingService.processRequests();
    }
}
