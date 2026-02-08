package views;
import javax.swing.*;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import models.*;
import controllers.*;

public class exit extends JFrame{
    private Color blueColor = new Color(3, 78, 161);
    private Color redColor = new Color(255, 7, 7);
    private Color whiteGreyColor = new Color(238,241,241);
    private Color greenColor = new Color(46, 204, 113);
    private Font headerFont = new Font("SansSerif", Font.BOLD, 30);
    private Font contentFont = new Font("SansSerif", Font.BOLD, 20);
    
    private JTextField licensePlate;
    private JTextArea detailsArea;
    private JButton searchButton;
    private JButton processPaymentButton;
    private JButton cancelButton;

    private String vehicleType;
    private String cardHolder;
    private String vehiclePlateNumber;
    private String time;
    
    private vehiclerecord currentVehicle;
    private double parkingFee;
    private double unpaidFines;
    private double totalDue;
    private long hoursParked;
    private parkingcontroller controller;
   
    public exit()
    {
        controller = new parkingcontroller();
        
        setTitle("Exit");
        setSize(1300, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(blueColor);
        JLabel title = new JLabel("Thank you. Come again next time!");
        title.setForeground(Color.WHITE);
        title.setFont(headerFont);
        header.add(title);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(whiteGreyColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);  

    // enter license plate for system to search
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0; //left column
        gbc.gridy = 0; //first row
        gbc.gridwidth = 1; //span one column
        JLabel licensePlateLabel = new JLabel("Enter your license plate: ");
        licensePlateLabel.setFont(contentFont);
        content.add(licensePlateLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        licensePlate = new JTextField(50);
        content.add(licensePlate, gbc);
    
    // finds the vehicle and its entry time, calculates parking duration in hours , calculates the fee based on the spot type and duration
        gbc.gridwidth = 3;
        gbc.gridy++; //go to next row
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        detailsArea = new JTextArea(20, 60);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        detailsArea.setEditable(false);
        detailsArea.setBackground(Color.WHITE);
        detailsArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(blueColor, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JScrollPane scrollPane = new JScrollPane(detailsArea);
        content.add(scrollPane, gbc);

        gbc.gridy++; //go to next row
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;

        // have two buttons here
        JButton submit = new JButton("Generate Bill");
        processPaymentButton = new JButton("Process Payment");
        JButton cancel = new JButton("Back to home"); 

        submit.setPreferredSize(new Dimension(200, 50));
        processPaymentButton.setPreferredSize(new Dimension(200, 50));
        cancel.setPreferredSize(new Dimension(200, 50));

        // set font for the text in the button
        submit.setFont(contentFont);
        processPaymentButton.setFont(contentFont);
        cancel.setFont(contentFont);
        
        processPaymentButton.setEnabled(false);

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String plate = licensePlate.getText().trim();
                // Search for the car
                searchVehicle();
            }
        });
        
        processPaymentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                processPayment();
            }
        });

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                new dashboard().setVisible(true);
                dispose();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonPanel.setBackground(whiteGreyColor);
        
        buttonPanel.add(submit);
        buttonPanel.add(processPaymentButton);
        buttonPanel.add(cancel);

        content.add(buttonPanel, gbc);

        add(content, BorderLayout.CENTER);
    }

    private void searchVehicle() {
        String plate = licensePlate.getText().trim().toUpperCase();
        
        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a license plate number!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentVehicle = controller.findVehicleByPlate(plate);
        
        if (currentVehicle == null) {
            detailsArea.setText("Vehicle not found in parking lot!\n\nPlease check the license plate number.");
            processPaymentButton.setEnabled(false);
            return;
        }

        hoursParked = controller.calculateHours(currentVehicle.getEntryTime());
        parkingFee = controller.calculateParkingFee(currentVehicle);
        unpaidFines = controller.getUnpaidFines(plate);
        
        long overstayHours = hoursParked > 24 ? hoursParked : 0;
        double currentFine = 0;
        double reservedMisuseFine = 0;
        
        if (overstayHours > 24) {
            currentFine = controller.calculateFine(plate, hoursParked);
        }
        
        if (controller.isReservedSpotMisuse(currentVehicle)) {
            reservedMisuseFine = 100.0;
        }
        
        totalDue = parkingFee + unpaidFines + currentFine + reservedMisuseFine;

        parkinglot lot = parkinglot.getInstance();
        parkingspot spot = lot.findSpotByID(currentVehicle.getSpotID());
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String exitTime = sdf.format(new java.util.Date());

        StringBuilder details = new StringBuilder();
        details.append("╔════════════════════════════════════════════════════════════════════╗\n");
        details.append("║                         PARKING BILL                               ║\n");
        details.append("╚════════════════════════════════════════════════════════════════════╝\n\n");
        
        details.append("VEHICLE INFORMATION:\n");
        details.append("─────────────────────────────────────────────────────────────────────\n");
        details.append(String.format("  Customer Name      : %s\n", currentVehicle.getName()));
        details.append(String.format("  License Plate      : %s\n", currentVehicle.getLicensePlate()));
        details.append(String.format("  Vehicle Type       : %s\n", currentVehicle.getVehicleType()));
        details.append(String.format("  Parking Spot       : %s\n\n", currentVehicle.getSpotID()));
        
        details.append("PARKING DETAILS:\n");
        details.append("─────────────────────────────────────────────────────────────────────\n");
        details.append(String.format("  Entry Time         : %s\n", currentVehicle.getEntryTime()));
        details.append(String.format("  Exit Time          : %s\n", exitTime));
        details.append(String.format("  Duration           : %d hour(s)\n", hoursParked));
        details.append(String.format("  Hourly Rate        : RM %.2f/hour\n", spot != null ? spot.getHourlyRate() : 0.0));
        details.append(String.format("  Parking Fee        : RM %.2f\n\n", parkingFee));
        
        if (unpaidFines > 0) {
            details.append("PREVIOUS FINES:\n");
            details.append("─────────────────────────────────────────────────────────────────────\n");
            details.append(String.format("  Unpaid Fines       : RM %.2f\n\n", unpaidFines));
        }
        
        if (currentFine > 0) {
            details.append("CURRENT FINES:\n");
            details.append("─────────────────────────────────────────────────────────────────────\n");
            details.append(String.format("  Overstay Fine      : RM %.2f\n", currentFine));
            details.append(String.format("  (Over 24 hours - %d hours total)\n\n", hoursParked));
        }
        
        if (reservedMisuseFine > 0) {
            if (currentFine == 0) {
                details.append("CURRENT FINES:\n");
                details.append("─────────────────────────────────────────────────────────────────────\n");
            }
            details.append(String.format("  Reserved Spot Fine : RM %.2f\n", reservedMisuseFine));
            details.append("  (Non-VIP parking in reserved spot)\n\n");
        }
        
        details.append("═════════════════════════════════════════════════════════════════════\n");
        details.append(String.format("  TOTAL AMOUNT DUE   : RM %.2f\n", totalDue));
        details.append("═════════════════════════════════════════════════════════════════════\n\n");
        
        if (currentVehicle.getVehicleType().equalsIgnoreCase("Handicapped") && 
            currentVehicle.getHandicappedCard().equalsIgnoreCase("Yes") && 
            spot != null && spot.getType().equalsIgnoreCase("Handicapped")) {
            details.append("  * Handicapped vehicle in handicapped spot - FREE parking\n");
        }

        detailsArea.setText(details.toString());
        processPaymentButton.setEnabled(true);
    }

    private void processPayment() {
        if (currentVehicle == null) {
            return;
        }

        String[] paymentOptions = {"Cash", "Card"};
        int paymentChoice = JOptionPane.showOptionDialog(
            this,
            "Total Amount Due: RM " + String.format("%.2f", totalDue) + "\n\nSelect Payment Method:",
            "Payment Method",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            paymentOptions,
            paymentOptions[0]
        );

        if (paymentChoice == -1) {
            return;
        }

        String paymentMethod = paymentOptions[paymentChoice];
        double amountPaid = 0;
        double change = 0;

        if (paymentMethod.equals("Cash")) {
            String input = JOptionPane.showInputDialog(
                this,
                "Total Due: RM " + String.format("%.2f", totalDue) + "\n\nEnter cash amount received:",
                "Cash Payment",
                JOptionPane.QUESTION_MESSAGE
            );

            if (input == null || input.trim().isEmpty()) {
                return;
            }

            try {
                amountPaid = Double.parseDouble(input.trim());
                
                if (amountPaid < totalDue) {
                    JOptionPane.showMessageDialog(this,
                        "Insufficient payment!\nAmount due: RM " + String.format("%.2f", totalDue) +
                        "\nAmount received: RM " + String.format("%.2f", amountPaid),
                        "Payment Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                change = amountPaid - totalDue;
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount entered!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            amountPaid = totalDue;
            change = 0;
        }

        boolean exitSuccess = controller.exitVehicle(currentVehicle.getLicensePlate());
        
        if (!exitSuccess) {
            JOptionPane.showMessageDialog(this, "Error processing exit. Please try again.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        controller.recordRevenue(totalDue, "Parking fee for " + currentVehicle.getLicensePlate());

        if (unpaidFines > 0) {
            controller.markFinesPaid(currentVehicle.getLicensePlate());
        }

        long overstayHours = hoursParked > 24 ? hoursParked : 0;
        if (overstayHours > 24) {
            double currentFine = controller.calculateFine(currentVehicle.getLicensePlate(), hoursParked);
            controller.addFine(currentVehicle.getLicensePlate(), currentFine, "Overstay fine");
            controller.markFinesPaid(currentVehicle.getLicensePlate());
        }
        
        if (controller.isReservedSpotMisuse(currentVehicle)) {
            controller.addFine(currentVehicle.getLicensePlate(), 100.0, "Reserved spot misuse");
            controller.markFinesPaid(currentVehicle.getLicensePlate());
        }

        generateReceipt(paymentMethod, amountPaid, change);
    }

    private void generateReceipt(String paymentMethod, double amountPaid, double change) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String exitTime = sdf.format(new java.util.Date());

        parkinglot lot = parkinglot.getInstance();
        parkingspot spot = lot.findSpotByID(currentVehicle.getSpotID());

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
        receipt.append(String.format("  Parking Fee        : RM %.2f\n\n", parkingFee));
        
        if (unpaidFines > 0) {
            receipt.append(String.format("  Previous Fines     : RM %.2f\n", unpaidFines));
        }
        
        if (hoursParked > 24) {
            double currentFine = controller.calculateFine(currentVehicle.getLicensePlate(), hoursParked);
            receipt.append(String.format("  Overstay Fine      : RM %.2f\n", currentFine));
        }
        
        if (controller.isReservedSpotMisuse(currentVehicle)) {
            receipt.append(String.format("  Reserved Spot Fine : RM 100.00\n"));
        }
        
        receipt.append("─────────────────────────────────────────────────────────────────────\n");
        receipt.append(String.format("  TOTAL AMOUNT       : RM %.2f\n\n", totalDue));
        
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

        saveReceiptToFile(receipt.toString(), currentVehicle.getLicensePlate(), exitTime);

        JTextArea receiptArea = new JTextArea(receipt.toString());
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        receiptArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(receiptArea);
        scrollPane.setPreferredSize(new Dimension(700, 500));

        JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "Payment Successful - Receipt",
            JOptionPane.INFORMATION_MESSAGE
        );

        new dashboard().setVisible(true);
        dispose();
    }

    private void saveReceiptToFile(String receiptContent, String licensePlate, String exitTime) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("receipts.txt", true))) {
            bw.write("================================================================================\n");
            bw.write("Receipt for: " + licensePlate + " | Date: " + exitTime + "\n");
            bw.write("================================================================================\n");
            bw.write(receiptContent);
            bw.write("\n\n");
        } catch (IOException e) {
            System.err.println("Error saving receipt: " + e.getMessage());
        }
    }

    public boolean readParkingDetails(String searchPlate)
    {
        String filePath = "parking.txt"; // Make sure this file exists in your project directory
        String line;
        String delimiter = ",";

        // Use try-with-resources to ensure the reader is closed automatically
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                // Split the line by the comma delimiter
                String[] data = line.split(delimiter);

                if (data.length >= 5)
                {
                    if (data[3].trim().equalsIgnoreCase(searchPlate))
                    {
                        vehicleType = data[1];
                        cardHolder = data[2];
                        time = data[4];
                        return true;
                    }
                }
                System.out.println();
                
            }
        } catch (IOException e) {
            // Handle exceptions such as file not found or read errors
            System.err.println("An error occurred while reading the file: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}