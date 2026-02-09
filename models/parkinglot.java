package models;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class parkinglot {
    private static parkinglot instance;
    private List<floor> floors;
    private String fineScheme; // "fixed", "progressive", "hourly"

    private parkinglot() {
        floors = new ArrayList<>();
        fineScheme = loadFineSchemeFromFile();
        initializeFloors();
        loadOccupiedSpots();
    }

    public static parkinglot getInstance() {
        if (instance == null) {
            instance = new parkinglot();
        }
        return instance;
    }

    private void initializeFloors() {
        // Floor 1: Reserved 53, Handicapped 3
        floor floor1 = new floor(1);
        for (int i = 1; i <= 53; i++) {
            floor1.addSpot(new parkingspot("F1-R" + ((i-1)/10 + 1) + "-S" + i, "Reserved"));
        }
        for (int i = 54; i <= 56; i++) {
            floor1.addSpot(new parkingspot("F1-R6-S" + i, "Handicapped"));
        }
        floors.add(floor1);

        // Floor 2: Compact 46, Regular 47, Handicapped 3
        floor floor2 = new floor(2);
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
        floor floor3 = new floor(3);
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

    public List<floor> getFloors() {
        return floors;
    }

    public List<parkingspot> getAllAvailableSpots(String type) {
        List<parkingspot> allAvailable = new ArrayList<>();
        for (floor f : floors) {
            allAvailable.addAll(f.getAvailableSpots(type));
        }
        return allAvailable;
    }

    public parkingspot findSpotByID(String spotID) {
        for (floor f : floors) {
            parkingspot spot = f.findSpotByID(spotID);
            if (spot != null) {
                return spot;
            }
        }
        return null;
    }

    public int getTotalOccupied() {
        int total = 0;
        for (floor f : floors) {
            total += f.getOccupiedCount();
        }
        return total;
    }

    public int getTotalSpots() {
        int total = 0;
        for (floor f : floors) {
            total += f.getTotalSpots();
        }
        return total;
    }

    public String getFineScheme() {
        return fineScheme;
    }

    public void setFineScheme(String scheme) {
        this.fineScheme = scheme;
        saveFineSchemeToFile(scheme);
    }
    
    private void saveFineSchemeToFile(String scheme) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("fine_scheme.txt"))) {
            bw.write(scheme);
        } catch (IOException e) {
            System.err.println("Error saving fine scheme: " + e.getMessage());
        }
    }
    
    private String loadFineSchemeFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("fine_scheme.txt"))) {
            String line = br.readLine();
            if (line != null && !line.trim().isEmpty()) {
                return line.trim();
            }
        } catch (IOException e) {
            // File doesn't exist, use default
        }
        return "fixed";
    }
}