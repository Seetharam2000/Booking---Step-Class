// File: UseCase9ErrorHandlingValidation.java

import java.util.*;

// Custom Exception for Invalid Booking
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// Domain Model: Reservation
class Reservation {
    private String guestName;
    private String roomType;

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

    @Override
    public String toString() {
        return "Reservation Request -> Guest: " + guestName + ", Room Type: " + roomType;
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

    public boolean isValidRoomType(String type) {
        return availability.containsKey(type);
    }

    public boolean isAvailable(String type) {
        return availability.getOrDefault(type, 0) > 0;
    }

    public void decrementAvailability(String type) throws InvalidBookingException {
        if (!isValidRoomType(type)) {
            throw new InvalidBookingException("Invalid room type: " + type);
        }
        int current = availability.get(type);
        if (current <= 0) {
            throw new InvalidBookingException("No availability for room type: " + type);
        }
        availability.put(type, current - 1);
    }

    public Map<String, Integer> getAvailabilitySnapshot() {
        return new HashMap<>(availability);
    }
}

// Booking Service with Validation
class BookingService {
    private InventoryService inventory;

    public BookingService(InventoryService inventory) {
        this.inventory = inventory;
    }

    public void confirmReservation(Reservation reservation) {
        try {
            validateReservation(reservation);
            inventory.decrementAvailability(reservation.getRoomType());
            System.out.println("Booking confirmed: " + reservation);
        } catch (InvalidBookingException e) {
            System.out.println("Booking failed: " + e.getMessage());
        }
    }

    // Validation logic
    private void validateReservation(Reservation reservation) throws InvalidBookingException {
        if (reservation.getGuestName() == null || reservation.getGuestName().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }
        if (!inventory.isValidRoomType(reservation.getRoomType())) {
            throw new InvalidBookingException("Invalid room type selected: " + reservation.getRoomType());
        }
        if (!inventory.isAvailable(reservation.getRoomType())) {
            throw new InvalidBookingException("Room type unavailable: " + reservation.getRoomType());
        }
    }
}

// Main class
public class UseCase9ErrorHandlingValidation {
    public static void main(String[] args) {
        // Setup inventory
        InventoryService inventory = new InventoryService();
        inventory.addRoomType("Deluxe", 1);
        inventory.addRoomType("Standard", 0); // unavailable

        // Setup booking service
        BookingService bookingService = new BookingService(inventory);

        // Valid booking
        Reservation r1 = new Reservation("Alice", "Deluxe");
        bookingService.confirmReservation(r1);

        // Invalid booking: unavailable room
        Reservation r2 = new Reservation("Bob", "Standard");
        bookingService.confirmReservation(r2);

        // Invalid booking: unknown room type
        Reservation r3 = new Reservation("Charlie", "Suite");
        bookingService.confirmReservation(r3);

        // Invalid booking: empty guest name
        Reservation r4 = new Reservation("", "Deluxe");
        bookingService.confirmReservation(r4);

        // Attempt to book Deluxe again (inventory exhausted)
        Reservation r5 = new Reservation("Diana", "Deluxe");
        bookingService.confirmReservation(r5);
    }
}
