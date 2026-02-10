package models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParkingLot {
    private static ParkingLot instance;
    private List<Floor> floors;
    private String fineScheme; // "fixed", "progressive", "hourly"

    private ParkingLot() {
        floors = new ArrayList<>();
        fineScheme = "fixed"; // default
        initializeFloors();
        loadOccupiedSpots();
    }

    public static ParkingLot getInstance() {
        if (instance == null) {
            instance = new ParkingLot();
        }
        return instance;
    }

    private void initializeFloors() {
        // Floor 1: Reserved 53, Handicapped 3
        Floor floor1 = new Floor(1);
        for (int i = 1; i <= 53; i++) {
            floor1.addSpot(new parkingspot("F1-R" + ((i-1)/10 + 1) + "-S" + i, "Reserved"));
        }
        for (int i = 54; i <= 56; i++) {
            floor1.addSpot(new parkingspot("F1-R6-S" + i, "Handicapped"));
        }
        floors.add(floor1);

        // Floor 2: Compact 46, Regular 47, Handicapped 3
        Floor floor2 = new Floor(2);
        for (int i = 1; i <= 46; i++) {
            floor2.addSpot(new parkingspot("F2-R" + ((i-1)/10 + 1) + "-S" + i, "Compact"));
        }
        for (int i = 47; i <= 93; i++) {
            floor2.addSpot(new parkingspot("F2-R" + ((i-1)/10 + 1) + "-S" + i, "Regular"));
        }
        for (int i = 94; i <= 96; i++) {
            floor2.addSpot(new parkingspot("F2-R10-S" + i, "Handicapped"));
        }
        floors.add(floor2);

        // Floor 3: Compact 46, Regular 47, Handicapped 3
        Floor floor3 = new Floor(3);
        for (int i = 1; i <= 46; i++) {
            floor3.addSpot(new parkingspot("F3-R" + ((i-1)/10 + 1) + "-S" + i, "Compact"));
        }
        for (int i = 47; i <= 93; i++) {
            floor3.addSpot(new parkingspot("F3-R" + ((i-1)/10 + 1) + "-S" + i, "Regular"));
        }
        for (int i = 94; i <= 96; i++) {
            floor3.addSpot(new parkingspot("F3-R10-S" + i, "Handicapped"));
        }
        floors.add(floor3);
    }

    private void loadOccupiedSpots() {
        try (BufferedReader br = new BufferedReader(new FileReader("parking.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 6) {
                    String licensePlate = data[3].trim();
                    String spotID = data[5].trim();
                    
                    parkingspot spot = findSpotByID(spotID);
                    if (spot != null) {
                        spot.occupy(licensePlate);
                    }
                }
            }
        } catch (IOException e) {
        }
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public List<parkingspot> getAllAvailableSpots(String type) {
        List<parkingspot> allAvailable = new ArrayList<>();
        for (Floor f : floors) {
            allAvailable.addAll(f.getAvailableSpots(type));
        }
        return allAvailable;
    }

    public parkingspot findSpotByID(String spotID) {
        for (Floor f : floors) {
            parkingspot spot = f.findSpotByID(spotID);
            if (spot != null) {
                return spot;
            }
        }
        return null;
    }

    public int getTotalOccupied() {
        int total = 0;
        for (Floor f : floors) {
            total += f.getOccupiedCount();
        }
        return total;
    }

    public int getTotalSpots() {
        int total = 0;
        for (Floor f : floors) {
            total += f.getTotalSpots();
        }
        return total;
    }

    public String getFineScheme() {
        return fineScheme;
    }

    public void setFineScheme(String scheme) {
        this.fineScheme = scheme;
    }
}