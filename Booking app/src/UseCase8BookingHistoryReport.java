// File: UseCase8BookingHistoryReport.java

import java.util.*;

// Domain Model: Reservation (from previous use cases)
class Reservation {
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

// Booking History (stores confirmed reservations)
class BookingHistory {
    private List<Reservation> confirmedBookings;

    public BookingHistory() {
        confirmedBookings = new ArrayList<>();
    }

    public void addReservation(Reservation reservation) {
        confirmedBookings.add(reservation);
        System.out.println("Reservation added to history: " + reservation);
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(confirmedBookings); // defensive copy
    }
}

// Booking Report Service
class BookingReportService {
    private BookingHistory history;

    public BookingReportService(BookingHistory history) {
        this.history = history;
    }

    // Generate a simple report
    public void generateReport() {
        List<Reservation> reservations = history.getAllReservations();
        System.out.println("\n--- Booking Report ---");
        System.out.println("Total Confirmed Reservations: " + reservations.size());

        Map<String, Integer> roomTypeCount = new HashMap<>();
        for (Reservation r : reservations) {
            roomTypeCount.put(r.getRoomType(), roomTypeCount.getOrDefault(r.getRoomType(), 0) + 1);
        }

        System.out.println("Breakdown by Room Type:");
        for (String type : roomTypeCount.keySet()) {
            System.out.println(" - " + type + ": " + roomTypeCount.get(type));
        }

        System.out.println("\nDetailed Reservations:");
        for (Reservation r : reservations) {
            System.out.println(r);
        }
    }
}

// Main class
public class UseCase8BookingHistoryReport {
    public static void main(String[] args) {
        // Setup booking history
        BookingHistory history = new BookingHistory();

        // Example confirmed reservations
        Reservation r1 = new Reservation("Alice", "Deluxe", "DE-123456");
        Reservation r2 = new Reservation("Bob", "Standard", "ST-654321");
        Reservation r3 = new Reservation("Charlie", "Deluxe", "DE-789012");

        // Add reservations to history
        history.addReservation(r1);
        history.addReservation(r2);
        history.addReservation(r3);

        // Generate report
        BookingReportService reportService = new BookingReportService(history);
        reportService.generateReport();
    }
}
