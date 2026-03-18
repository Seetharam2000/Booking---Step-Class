// File: UseCase10BookingCancellation.java

import java.util.*;

// Domain Model: Reservation
class Reservation {
    private String guestName;
    private String roomType;
    private String roomId;
    private boolean isCancelled;

    public Reservation(String guestName, String roomType, String roomId) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.isCancelled = false;
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

    public boolean isCancelled() {
        return isCancelled;
    }

    public void cancel() {
        this.isCancelled = true;
    }

    @Override
    public String toString() {
        return "Reservation -> Guest: " + guestName +
                ", Room Type: " + roomType +
                ", Room ID: " + roomId +
                (isCancelled ? " [CANCELLED]" : "");
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

    public void incrementAvailability(String type) {
        availability.put(type, availability.getOrDefault(type, 0) + 1);
    }

    public Map<String, Integer> getAvailabilitySnapshot() {
        return new HashMap<>(availability);
    }
}

// Booking History
class BookingHistory {
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

    public Reservation findReservationById(String roomId) {
        for (Reservation r : confirmedBookings) {
            if (r.getRoomId().equals(roomId)) {
                return r;
            }
        }
        return null;
    }
}

// Cancellation Service
class CancellationService {
    private InventoryService inventory;
    private BookingHistory history;
    private Stack<String> releasedRoomIds; // rollback stack

    public CancellationService(InventoryService inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
        this.releasedRoomIds = new Stack<>();
    }

    public void cancelReservation(String roomId) {
        Reservation reservation = history.findReservationById(roomId);

        if (reservation == null) {
            System.out.println("Cancellation failed: Reservation not found for Room ID " + roomId);
            return;
        }

        if (reservation.isCancelled()) {
            System.out.println("Cancellation failed: Reservation already cancelled for Room ID " + roomId);
            return;
        }

        // Perform rollback
        reservation.cancel();
        inventory.incrementAvailability(reservation.getRoomType());
        releasedRoomIds.push(roomId);

        System.out.println("Cancellation successful: " + reservation);
        System.out.println("Inventory rolled back for Room Type: " + reservation.getRoomType());
    }

    public void viewRollbackStack() {
        System.out.println("\nRollback Stack (Recently Released Room IDs): " + releasedRoomIds);
    }
}

// Main class
public class UseCase10BookingCancellation {
    public static void main(String[] args) {
        // Setup inventory
        InventoryService inventory = new InventoryService();
        inventory.addRoomType("Deluxe", 1);
        inventory.addRoomType("Standard", 0);

        // Setup booking history
        BookingHistory history = new BookingHistory();

        // Example confirmed reservations
        Reservation r1 = new Reservation("Alice", "Deluxe", "DE-123456");
        Reservation r2 = new Reservation("Bob", "Standard", "ST-654321");

        history.addReservation(r1);
        history.addReservation(r2);

        // Setup cancellation service
        CancellationService cancellationService = new CancellationService(inventory, history);

        // Attempt cancellations
        cancellationService.cancelReservation("DE-123456"); // valid
        cancellationService.cancelReservation("DE-123456"); // duplicate
        cancellationService.cancelReservation("XX-999999"); // non-existent

        // View rollback stack
        cancellationService.viewRollbackStack();

        // View updated booking history
        System.out.println("\nUpdated Booking History:");
        for (Reservation r : history.getAllReservations()) {
            System.out.println(r);
        }

        // View updated inventory
        System.out.println("\nUpdated Inventory Snapshot: " + inventory.getAvailabilitySnapshot());
    }
}
