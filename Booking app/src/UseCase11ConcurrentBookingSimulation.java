// File: UseCase11ConcurrentBookingSimulation.java

import java.util.*;

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

// Inventory Service with synchronized methods
class InventoryService {
    private Map<String, Integer> availability;

    public InventoryService() {
        availability = new HashMap<>();
    }

    public synchronized void addRoomType(String type, int count) {
        availability.put(type, count);
    }

    public synchronized boolean isAvailable(String type) {
        return availability.getOrDefault(type, 0) > 0;
    }

    public synchronized boolean allocateRoom(String type) {
        if (isAvailable(type)) {
            availability.put(type, availability.get(type) - 1);
            return true;
        }
        return false;
    }

    public synchronized Map<String, Integer> getAvailabilitySnapshot() {
        return new HashMap<>(availability);
    }
}

// Concurrent Booking Processor
class BookingProcessor implements Runnable {
    private Queue<Reservation> requestQueue;
    private InventoryService inventory;

    public BookingProcessor(Queue<Reservation> requestQueue, InventoryService inventory) {
        this.requestQueue = requestQueue;
        this.inventory = inventory;
    }

    @Override
    public void run() {
        while (true) {
            Reservation reservation;
            synchronized (requestQueue) {
                if (requestQueue.isEmpty()) {
                    break;
                }
                reservation = requestQueue.poll();
            }

            if (reservation != null) {
                processReservation(reservation);
            }
        }
    }

    private void processReservation(Reservation reservation) {
        synchronized (inventory) {
            if (inventory.allocateRoom(reservation.getRoomType())) {
                System.out.println("Booking confirmed: " + reservation);
            } else {
                System.out.println("Booking failed (No availability): " + reservation);
            }
        }
    }
}

// Main class
public class UseCase11ConcurrentBookingSimulation {
    public static void main(String[] args) {
        // Setup inventory
        InventoryService inventory = new InventoryService();
        inventory.addRoomType("Deluxe", 2);
        inventory.addRoomType("Standard", 1);

        // Setup booking requests
        Queue<Reservation> requestQueue = new LinkedList<>();
        requestQueue.add(new Reservation("Alice", "Deluxe"));
        requestQueue.add(new Reservation("Bob", "Standard"));
        requestQueue.add(new Reservation("Charlie", "Deluxe"));
        requestQueue.add(new Reservation("Diana", "Deluxe")); // exceeds availability

        // Create multiple threads simulating concurrent guests
        Thread t1 = new Thread(new BookingProcessor(requestQueue, inventory), "Processor-1");
        Thread t2 = new Thread(new BookingProcessor(requestQueue, inventory), "Processor-2");

        // Start threads
        t1.start();
        t2.start();

        // Wait for threads to finish
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Final inventory snapshot
        System.out.println("\nFinal Inventory Snapshot: " + inventory.getAvailabilitySnapshot());
    }
}
