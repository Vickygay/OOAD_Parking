package models;

public class VehicleRecord {
    private String name;
    private String vehicleType;
    private String handicappedCard;
    private String licensePlate;
    private String entryTime;
    private String spotID;
    private double unpaidFine;

    public VehicleRecord(String name, String vehicleType, String handicappedCard, 
                        String licensePlate, String entryTime, String spotID) {
        this.name = name;
        this.vehicleType = vehicleType;
        this.handicappedCard = handicappedCard;
        this.licensePlate = licensePlate;
        this.entryTime = entryTime;
        this.spotID = spotID;
        this.unpaidFine = 0;
    }

    // Getters
    public String getName() { return name; }
    public String getVehicleType() { return vehicleType; }
    public String getHandicappedCard() { return handicappedCard; }
    public String getLicensePlate() { return licensePlate; }
    public String getEntryTime() { return entryTime; }
    public String getSpotID() { return spotID; }
    public double getUnpaidFine() { return unpaidFine; }

    // Setters
    public void setUnpaidFine(double fine) { this.unpaidFine = fine; }

    public String toFileString() {
        return name + "," + vehicleType + "," + handicappedCard + "," + 
               licensePlate + "," + entryTime + "," + spotID;
    }

    public static VehicleRecord fromFileString(String line) {
        String[] data = line.split(",");
        if (data.length >= 6) {
            return new VehicleRecord(data[0].trim(), data[1].trim(), data[2].trim(),
                                    data[3].trim(), data[4].trim(), data[5].trim());
        }
        return null;
    }
}