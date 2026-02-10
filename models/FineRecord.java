package models;

public class FineRecord {
    private String licensePlate;
    private double amount;
    private String reason;
    private boolean isPaid;

    public FineRecord(String licensePlate, double amount, String reason) {
        this.licensePlate = licensePlate;
        this.amount = amount;
        this.reason = reason;
        this.isPaid = false;
    }

    public String getLicensePlate() { return licensePlate; }
    public double getAmount() { return amount; }
    public String getReason() { return reason; }
    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }

    public String toFileString() {
        return licensePlate + "," + amount + "," + reason + "," + (isPaid ? "paid" : "unpaid");
    }

    public static FineRecord fromFileString(String line) {
        String[] data = line.split(",");
        if (data.length >= 4) {
            FineRecord record = new FineRecord(data[0].trim(), 
                                              Double.parseDouble(data[1].trim()), 
                                              data[2].trim());
            record.setPaid(data[3].trim().equals("paid"));
            return record;
        }
        return null;
    }
}