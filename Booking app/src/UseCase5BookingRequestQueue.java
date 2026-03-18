// File: UseCase5BookingRequestQueue.java

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

// Booking Request Queue (FIFO)
class BookingRequestQueue {
    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        requestQueue = new LinkedList<>();
    }

    // Accept booking request
    public void addRequest(Reservation reservation) {
        requestQueue.offer(reservation);
        System.out.println("Request added: " + reservation);
    }

    // View all queued requests (without processing)
    public void viewRequests() {
        System.out.println("\nCurrent Booking Requests in Queue (FIFO order):");
        for (Reservation r : requestQueue) {
            System.out.println(r);
        }
    }

    // Peek at the next request (without removing)
    public Reservation peekNextRequest() {
        return requestQueue.peek();
    }
}

// Main class
public class UseCase5BookingRequestQueue {
    public static void main(String[] args) {
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // Guests submit booking requests
        bookingQueue.addRequest(new Reservation("Alice", "Deluxe"));
        bookingQueue.addRequest(new Reservation("Bob", "Standard"));
        bookingQueue.addRequest(new Reservation("Charlie", "Suite"));
        bookingQueue.addRequest(new Reservation("Diana", "Standard"));

        // View queued requests
        bookingQueue.viewRequests();

        // Show the next request to be processed (FIFO principle)
        System.out.println("\nNext request to process: " + bookingQueue.peekNextRequest());
    }
}
