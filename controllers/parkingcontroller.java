package controllers;

import models.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class parkingcontroller {
    private static final String PARKING_FILE = "parking.txt";
    private static final String FINE_FILE = "fines.txt";
    private static final String REVENUE_FILE = "revenue.txt";

    public List<vehiclerecord> getAllParkedVehicles() {
        List<vehiclerecord> vehicles = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PARKING_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                vehiclerecord record = vehiclerecord.fromFileString(line);
                if (record != null) {
                    vehicles.add(record);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading parking file: " + e.getMessage());
        }
        return vehicles;
    }

    public vehiclerecord findVehicleByPlate(String plate) {
        for (vehiclerecord record : getAllParkedVehicles()) {
            if (record.getLicensePlate().equalsIgnoreCase(plate)) {
                return record;
            }
        }
        return null;
    }

    public boolean parkVehicle(vehiclerecord record) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PARKING_FILE, true))) {
            bw.write(record.toFileString());
            bw.newLine();
            
            parkinglot lot = parkinglot.getInstance();
            parkingspot spot = lot.findSpotByID(record.getSpotID());
            if (spot != null) {
                spot.occupy(record.getLicensePlate());
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Error saving parking record: " + e.getMessage());
            return false;
        }
    }

    public boolean exitVehicle(String plate) {
        List<vehiclerecord> allVehicles = getAllParkedVehicles();
        List<vehiclerecord> remaining = new ArrayList<>();
        vehiclerecord exitingVehicle = null;

        for (vehiclerecord record : allVehicles) {
            if (record.getLicensePlate().equalsIgnoreCase(plate)) {
                exitingVehicle = record;
            } else {
                remaining.add(record);
            }
        }

        if (exitingVehicle == null) {
            return false;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PARKING_FILE))) {
            for (vehiclerecord record : remaining) {
                bw.write(record.toFileString());
                bw.newLine();
            }

            parkinglot lot = parkinglot.getInstance();
            parkingspot spot = lot.findSpotByID(exitingVehicle.getSpotID());
            if (spot != null) {
                spot.vacate();
            }

            return true;
        } catch (IOException e) {
            System.err.println("Error updating parking file: " + e.getMessage());
            return false;
        }
    }

    public long calculateHours(String entryTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date entry = sdf.parse(entryTime);
            Date current = new Date();
            long diff = current.getTime() - entry.getTime();
            long hours = diff / (1000 * 60 * 60);
            
            if (diff % (1000 * 60 * 60) > 0) {
                hours++;
            }
            return hours;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public double calculateParkingFee(vehiclerecord record) {
        parkinglot lot = parkinglot.getInstance();
        parkingspot spot = lot.findSpotByID(record.getSpotID());
        
        if (spot == null) {
            return 0;
        }

        long hours = calculateHours(record.getEntryTime());
        double rate = spot.getHourlyRate();

        if (record.getVehicleType().equalsIgnoreCase("Handicapped") && 
            record.getHandicappedCard().equalsIgnoreCase("Yes") &&
            spot.getType().equalsIgnoreCase("Handicapped")) {
            rate = 0;
        }

        return hours * rate;
    }
    
    public boolean isReservedSpotMisuse(vehiclerecord record) {
        parkinglot lot = parkinglot.getInstance();
        parkingspot spot = lot.findSpotByID(record.getSpotID());
        
        if (spot == null || !spot.getType().equalsIgnoreCase("Reserved")) {
            return false;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader("reserved.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().equalsIgnoreCase(record.getLicensePlate())) {
                    return false;
                }
            }
        } catch (IOException e) {
        }
        
        return true;
    }

    public double calculateFine(String plate, long hours) {
        parkinglot lot = parkinglot.getInstance();
        String scheme = lot.getFineScheme();
        
        if (hours <= 24) {
            return 0;
        }

        switch (scheme.toLowerCase()) {
            case "fixed":
                return 50.0;
            case "progressive":
                if (hours <= 48) {
                    return 50.0;
                } else if (hours <= 72) {
                    return 150.0;
                } else {
                    return 500.0;
                }
            case "hourly":
                return (hours - 24) * 20.0;
            default:
                return 50.0;
        }
    }

    public double getUnpaidFines(String plate) {
        double total = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(FINE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                finerecord fine = finerecord.fromFileString(line);
                if (fine != null && fine.getLicensePlate().equalsIgnoreCase(plate) && !fine.isPaid()) {
                    total += fine.getAmount();
                }
            }
        } catch (IOException e) {
        }
        return total;
    }

    public void addFine(String plate, double amount, String reason) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FINE_FILE, true))) {
            finerecord fine = new finerecord(plate, amount, reason);
            bw.write(fine.toFileString());
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error saving fine: " + e.getMessage());
        }
    }

    public void markFinesPaid(String plate) {
        List<finerecord> allFines = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(FINE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                finerecord fine = finerecord.fromFileString(line);
                if (fine != null) {
                    if (fine.getLicensePlate().equalsIgnoreCase(plate)) {
                        fine.setPaid(true);
                    }
                    allFines.add(fine);
                }
            }
        } catch (IOException e) {
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FINE_FILE))) {
            for (finerecord fine : allFines) {
                bw.write(fine.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error updating fines: " + e.getMessage());
        }
    }

    public void recordRevenue(double amount, String description) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(REVENUE_FILE, true))) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = sdf.format(new Date());
            bw.write(timestamp + "," + amount + "," + description);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error recording revenue: " + e.getMessage());
        }
    }

    public double getTotalRevenue() {
        double total = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(REVENUE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    total += Double.parseDouble(parts[1].trim());
                }
            }
        } catch (IOException e) {
        }
        return total;
    }

    public List<finerecord> getAllUnpaidFines() {
        List<finerecord> unpaid = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FINE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                finerecord fine = finerecord.fromFileString(line);
                if (fine != null && !fine.isPaid()) {
                    unpaid.add(fine);
                }
            }
        } catch (IOException e) {
        }
        return unpaid;
    }
}