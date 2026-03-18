// File: UseCase7AddOnServiceSelection.java

import java.util.*;

// Domain Model: Reservation (from Use Case 6)
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

// Domain Model: Add-On Service
class AddOnService {
    private String name;
    private double cost;

    public AddOnService(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return name + " (Cost: " + cost + ")";
    }
}

// Add-On Service Manager
class AddOnServiceManager {
    private Map<String, List<AddOnService>> reservationServices;

    public AddOnServiceManager() {
        reservationServices = new HashMap<>();
    }

    // Attach services to a reservation
    public void addServicesToReservation(Reservation reservation, List<AddOnService> services) {
        reservationServices.putIfAbsent(reservation.getRoomId(), new ArrayList<>());
        reservationServices.get(reservation.getRoomId()).addAll(services);
        System.out.println("Services added for Reservation ID: " + reservation.getRoomId());
    }

    // View services for a reservation
    public void viewServicesForReservation(Reservation reservation) {
        List<AddOnService> services = reservationServices.getOrDefault(reservation.getRoomId(), new ArrayList<>());
        System.out.println("\nReservation: " + reservation);
        if (services.isEmpty()) {
            System.out.println("No add-on services selected.");
        } else {
            System.out.println("Selected Add-On Services:");
            for (AddOnService service : services) {
                System.out.println(" - " + service);
            }
            System.out.println("Total Additional Cost: " + calculateTotalCost(services));
        }
    }

    // Calculate total cost of services
    private double calculateTotalCost(List<AddOnService> services) {
        double total = 0;
        for (AddOnService service : services) {
            total += service.getCost();
        }
        return total;
    }
}

// Main class
public class UseCase7AddOnServiceSelection {
    public static void main(String[] args) {
        // Example confirmed reservations (from Use Case 6)
        Reservation r1 = new Reservation("Alice", "Deluxe", "DE-123456");
        Reservation r2 = new Reservation("Bob", "Standard", "ST-654321");

        // Define available services
        AddOnService breakfast = new AddOnService("Breakfast", 20.0);
        AddOnService airportPickup = new AddOnService("Airport Pickup", 50.0);
        AddOnService spaAccess = new AddOnService("Spa Access", 40.0);

        // Service Manager
        AddOnServiceManager manager = new AddOnServiceManager();

        // Guests select services
        manager.addServicesToReservation(r1, Arrays.asList(breakfast, spaAccess));
        manager.addServicesToReservation(r2, Arrays.asList(airportPickup));

        // View services for each reservation
        manager.viewServicesForReservation(r1);
        manager.viewServicesForReservation(r2);
    }
}
