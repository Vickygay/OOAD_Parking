package views;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import models.VehicleRecord;
import models.ParkingLot;
import models.ParkingSpot;
import controllers.ParkingController;

public class Receipt {
    private VehicleRecord currentVehicle;
    private ParkingController controller;
    private double parkingFee;
    private double unpaidFines;
    private double currentSessionFines;
    private int hoursParked;
    private String paymentMethod;
    private double amountPaid;
    private double change;
    private boolean paidAllFines;
    private String exitTime;

    public Receipt(VehicleRecord currentVehicle, ParkingController controller, double parkingFee,
                   double unpaidFines, double currentSessionFines, int hoursParked,
                   String paymentMethod, double amountPaid, double change, boolean paidAllFines) {
        this.currentVehicle = currentVehicle;
        this.controller = controller;
        this.parkingFee = parkingFee;
        this.unpaidFines = unpaidFines;
        this.currentSessionFines = currentSessionFines;
        this.hoursParked = hoursParked;
        this.paymentMethod = paymentMethod;
        this.amountPaid = amountPaid;
        this.change = change;
        this.paidAllFines = paidAllFines;
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.exitTime = sdf.format(new java.util.Date());
    }

    public String generateReceiptText() {
        ParkingLot lot = ParkingLot.getInstance();
        ParkingSpot spot = lot.findSpotByID(currentVehicle.getSpotID());

        StringBuilder receipt = new StringBuilder();
        receipt.append("╔════════════════════════════════════════════════════════════════════╗\n");
        receipt.append("║                       PAYMENT RECEIPT                              ║\n");
        receipt.append("╚════════════════════════════════════════════════════════════════════╝\n\n");
        
        receipt.append("TRANSACTION DETAILS:\n");
        receipt.append("─────────────────────────────────────────────────────────────────────\n");
        receipt.append(String.format("  Receipt Date       : %s\n", exitTime));
        receipt.append(String.format("  License Plate      : %s\n", currentVehicle.getLicensePlate()));
        receipt.append(String.format("  Customer Name      : %s\n", currentVehicle.getName()));
        receipt.append(String.format("  Parking Spot       : %s\n\n", currentVehicle.getSpotID()));
        
        receipt.append("PARKING SUMMARY:\n");
        receipt.append("─────────────────────────────────────────────────────────────────────\n");
        receipt.append(String.format("  Entry Time         : %s\n", currentVehicle.getEntryTime()));
        receipt.append(String.format("  Exit Time          : %s\n", exitTime));
        receipt.append(String.format("  Duration           : %d hour(s)\n", hoursParked));
        receipt.append(String.format("  Hourly Rate        : RM %.2f/hour\n", spot != null ? spot.getHourlyRate() : 0.0));
        receipt.append(String.format("  Parking Fee        : RM %.2f (PAID)\n\n", parkingFee));
        
        if (paidAllFines && currentSessionFines > 0) {
            if (hoursParked > 24) {
                double overstayFine = controller.calculateFine(currentVehicle.getLicensePlate(), hoursParked);
                receipt.append(String.format("  Overstay Fine      : RM %.2f (PAID)\n", overstayFine));
            }
            
            if (controller.isReservedSpotMisuse(currentVehicle)) {
                receipt.append(String.format("  Reserved Spot Fine : RM 100.00 (PAID)\n"));
            }
        }
        
        if (paidAllFines && unpaidFines > 0) {
            receipt.append(String.format("  Previous Fines     : RM %.2f (PAID)\n", unpaidFines));
        }
        
        if (!paidAllFines && (currentSessionFines > 0 || unpaidFines > 0)) {
            double unpaidTotal = currentSessionFines + unpaidFines;
            receipt.append("\n⚠ UNPAID FINES:\n");
            receipt.append(String.format("  Unpaid Amount      : RM %.2f\n", unpaidTotal));
            receipt.append("  (Will be charged at next exit)\n");
        }
        
        receipt.append("─────────────────────────────────────────────────────────────────────\n");
        receipt.append(String.format("  TOTAL AMOUNT PAID  : RM %.2f\n\n", amountPaid));
        
        receipt.append("PAYMENT INFORMATION:\n");
        receipt.append("─────────────────────────────────────────────────────────────────────\n");
        receipt.append(String.format("  Payment Method     : %s\n", paymentMethod));
        receipt.append(String.format("  Amount Paid        : RM %.2f\n", amountPaid));
        
        if (change > 0) {
            receipt.append(String.format("  Change             : RM %.2f\n", change));
        }
        
        receipt.append("═════════════════════════════════════════════════════════════════════\n\n");
        receipt.append("                   Thank you for parking with us!\n");
        receipt.append("                        Drive safely!\n");

        return receipt.toString();
    }

    public void display(JFrame parentFrame) {
        saveReceiptToFile();
        
        JTextArea receiptArea = new JTextArea(generateReceiptText());
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        receiptArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(receiptArea);
        scrollPane.setPreferredSize(new Dimension(700, 500));

        JOptionPane.showMessageDialog(
            parentFrame,
            scrollPane,
            "Payment Successful - Receipt",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void saveReceiptToFile() {
        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(
                new java.io.FileWriter("receipts.txt", true))) {
            
            // Add separator
            bw.write("================================================================================");
            bw.newLine();
            bw.write("Receipt for: " + currentVehicle.getLicensePlate() + " | Date: " + exitTime);
            bw.newLine();
            bw.write("================================================================================");
            bw.newLine();
            
            // Write the full receipt
            bw.write(generateReceiptText());
            bw.newLine();
            bw.newLine();
            bw.newLine();
            
        } catch (java.io.IOException e) {
            System.err.println("Error saving receipt to file: " + e.getMessage());
        }
    }

    public void printReceipt() {
        saveReceiptToFile(); 
        System.out.println(generateReceiptText());
    }

    public String getExitTime() {
        return exitTime;
    }

    public double getTotalAmountPaid() {
        return amountPaid;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}