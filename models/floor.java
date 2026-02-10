package models;

import java.util.ArrayList;
import java.util.List;

public class Floor {
    private int floorNumber;
    private List<parkingspot> spots;

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.spots = new ArrayList<>();
    }

    public void addSpot(parkingspot spot) {
        spots.add(spot);
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public List<parkingspot> getSpots() {
        return spots;
    }

    public List<parkingspot> getAvailableSpots(String type) {
        List<parkingspot> available = new ArrayList<>();
        for (parkingspot spot : spots) {
            if (!spot.isOccupied() && spot.getType().equalsIgnoreCase(type)) {
                available.add(spot);
            }
        }
        return available;
    }

    public int getOccupiedCount() {
        int count = 0;
        for (parkingspot spot : spots) {
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

    public parkingspot findSpotByID(String spotID) {
        for (parkingspot spot : spots) {
            if (spot.getSpotID().equals(spotID)) {
                return spot;
            }
        }
        return null;
    }
}