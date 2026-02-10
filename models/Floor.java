package models;

import java.util.ArrayList;
import java.util.List;

public class Floor {
    private int floorNumber;
    private List<ParkingSpot> spots;

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.spots = new ArrayList<>();
    }

    public void addSpot(ParkingSpot spot) {
        spots.add(spot);
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public List<ParkingSpot> getSpots() {
        return spots;
    }

    public List<ParkingSpot> getAvailableSpots(String type) {
        List<ParkingSpot> available = new ArrayList<>();
        for (ParkingSpot spot : spots) {
            if (!spot.isOccupied() && spot.getType().equalsIgnoreCase(type)) {
                available.add(spot);
            }
        }
        return available;
    }

    public int getOccupiedCount() {
        int count = 0;
        for (ParkingSpot spot : spots) {
            if (spot.isOccupied()) {
                count++;
            }
        }
        return count;
    }

    public int getTotalSpots() {
        return spots.size();
    }

    public int getAvailableCount() {
        return getTotalSpots() - getOccupiedCount();
    }

    public ParkingSpot findSpotByID(String spotID) {
        for (ParkingSpot spot : spots) {
            if (spot.getSpotID().equals(spotID)) {
                return spot;
            }
        }
        return null;
    }
}