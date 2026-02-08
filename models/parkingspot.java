package models;

public class parkingspot {
    private String spotID;
    private String type; // Compact, Regular, Handicapped, Reserved
    private boolean isOccupied;
    private String currentVehiclePlate;
    private double hourlyRate;

    public parkingspot(String spotID, String type) {
        this.spotID = spotID;
        this.type = type;
        this.isOccupied = false;
        this.currentVehiclePlate = null;
        setHourlyRate();
    }

    private void setHourlyRate() {
        switch (type.toLowerCase()) {
            case "compact":
                this.hourlyRate = 2.0;
                break;
            case "regular":
                this.hourlyRate = 5.0;
                break;
            case "handicapped":
                this.hourlyRate = 2.0;
                break;
            case "reserved":
                this.hourlyRate = 10.0;
                break;
            default:
                this.hourlyRate = 5.0;
        }
    }

    public String getSpotID() {
        return spotID;
    }

    public String getType() {
        return type;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public String getCurrentVehiclePlate() {
        return currentVehiclePlate;
    }

    public void setCurrentVehiclePlate(String plate) {
        this.currentVehiclePlate = plate;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void occupy(String vehiclePlate) {
        this.isOccupied = true;
        this.currentVehiclePlate = vehiclePlate;
    }

    public void vacate() {
        this.isOccupied = false;
        this.currentVehiclePlate = null;
    }
}