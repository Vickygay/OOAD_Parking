package views;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;

import models.*;
import controllers.*;

public class exit extends JFrame{
    private Color blueColor = new Color(3, 78, 161);
    private Color whiteGreyColor = new Color(238,241,241);
    private Font headerFont = new Font("SansSerif", Font.BOLD, 30);
    private Font contentFont = new Font("SansSerif", Font.BOLD, 20);
    
    private JTextField licensePlate;
    private JTextArea detailsArea;
    private JButton processPaymentButton;
    
    private vehiclerecord currentVehicle;
    private double parkingFee;
    private double unpaidFines;
    private double currentSessionFines;
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
        
        double overstayFine = 0;
        double reservedMisuseFine = 0;
        
        if (hoursParked > 24) {
            overstayFine = controller.calculateFine(plate, hoursParked);
        }
        
        if (controller.isReservedSpotMisuse(currentVehicle)) {
            reservedMisuseFine = 100.0;
        }
        
        currentSessionFines = overstayFine + reservedMisuseFine;
        totalDue = parkingFee + unpaidFines + currentSessionFines;

        parkinglot lot = parkinglot.getInstance();
        parkingspot spot = lot.findSpotByID(currentVehicle.getSpotID());
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String exitTime = sdf.format(new java.util.Date());

        StringBuilder details = new StringBuilder();
        details.append("═══════════════════════════════════════════════════════════════════\n");
        details.append("                        PARKING BILL                               \n");
        details.append("═══════════════════════════════════════════════════════════════════\n\n");
        
        details.append("VEHICLE INFORMATION:\n");
        details.append("─────────────────────────────────────────────────────────────────────\n");
        details.append(String.format("  License Plate      : %s\n", currentVehicle.getLicensePlate()));
        details.append(String.format("  Customer Name      : %s\n", currentVehicle.getName()));
        details.append(String.format("  Vehicle Type       : %s\n", currentVehicle.getVehicleType()));
        details.append(String.format("  Parking Spot       : %s\n\n", currentVehicle.getSpotID()));
        
        details.append("PARKING DETAILS:\n");
        details.append("─────────────────────────────────────────────────────────────────────\n");
        details.append(String.format("  Entry Time         : %s\n", currentVehicle.getEntryTime()));
        details.append(String.format("  Current Time       : %s\n", exitTime));
        details.append(String.format("  Duration           : %d hour(s)\n", hoursParked));
        details.append(String.format("  Hourly Rate        : RM %.2f/hour\n", spot != null ? spot.getHourlyRate() : 0.0));
        details.append(String.format("  Parking Fee        : RM %.2f\n\n", parkingFee));
        
        if (currentSessionFines > 0 || unpaidFines > 0) {
            details.append("FINES:\n");
            details.append("─────────────────────────────────────────────────────────────────────\n");
            
            if (hoursParked > 24) {
                details.append(String.format("  Overstay Fine      : RM %.2f (Parked > 24 hours)\n", overstayFine));
            }
            
            if (controller.isReservedSpotMisuse(currentVehicle)) {
                details.append(String.format("  Reserved Spot Fine : RM 100.00 (Unauthorized parking)\n"));
            }
            
            if (unpaidFines > 0) {
                details.append(String.format("  Previous Unpaid    : RM %.2f\n", unpaidFines));
            }
            
            details.append(String.format("\n  Total Fines        : RM %.2f\n\n", unpaidFines + currentSessionFines));
        }
        
        details.append("═══════════════════════════════════════════════════════════════════\n");
        details.append(String.format("  TOTAL AMOUNT DUE   : RM %.2f\n", totalDue));
        details.append("═══════════════════════════════════════════════════════════════════\n");

        detailsArea.setText(details.toString());
        processPaymentButton.setEnabled(true);
    }

    private void processPayment() {
        if (currentVehicle == null) {
            JOptionPane.showMessageDialog(this, "No vehicle selected!",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double totalFines = unpaidFines + currentSessionFines;
        boolean mustPayFines = totalFines > 500.0;
        
        String[] paymentChoices;
        
        if (mustPayFines) {
            // FORCE payment if fines > RM 500
            JOptionPane.showMessageDialog(this,
                String.format("⚠ WARNING ⚠\n\nYour total unpaid fines (RM %.2f) exceed RM 500.\n\nYou MUST pay all dues to exit the parking lot.",
                    totalFines),
                "Payment Required",
                JOptionPane.WARNING_MESSAGE);
            
            paymentChoices = new String[]{
                "Pay All Dues (RM " + String.format("%.2f", totalDue) + ")"
            };
        } else if (totalFines > 0) {
            // Optional payment if fines ≤ RM 500
            paymentChoices = new String[]{
                "Pay Parking Fee Only (RM " + String.format("%.2f", parkingFee) + ")",
                "Pay All Dues (RM " + String.format("%.2f", totalDue) + ")"
            };
        } else {
            // No fines, just parking fee
            paymentChoices = new String[]{
                "Pay (RM " + String.format("%.2f", parkingFee) + ")"
            };
        }
        
        int paymentChoice = JOptionPane.showOptionDialog(
            this,
            "Select payment option:",
            "Payment Options",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            paymentChoices,
            paymentChoices[0]
        );

        if (paymentChoice == -1) {
            return;
        }

        boolean payFinesNow;
        double amountToPay;
        
        if (mustPayFines) {
            // Must pay everything
            payFinesNow = true;
            amountToPay = totalDue;
        } else if (totalFines > 0) {
            // Optional - customer chose option 0 or 1
            payFinesNow = (paymentChoice == 1);
            amountToPay = payFinesNow ? totalDue : parkingFee;
        } else {
            // No fines
            payFinesNow = false;
            amountToPay = parkingFee;
        }

        String[] paymentMethods = {"Cash", "Card"};
        int methodChoice = JOptionPane.showOptionDialog(
            this,
            "Amount to pay: RM " + String.format("%.2f", amountToPay) + "\n\nSelect payment method:",
            "Payment Method",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            paymentMethods,
            paymentMethods[0]
        );

        if (methodChoice == -1) {
            return;
        }

        String paymentMethod = paymentMethods[methodChoice];
        double amountPaid = 0;
        double change = 0;

        if (paymentMethod.equals("Cash")) {
            Double result = processCashPayment(amountToPay);
            if (result == null) {
                return;  // Payment cancelled
            }
            amountPaid = result;
            change = amountPaid - amountToPay;
        } else {
            processCardPayment(amountToPay);
            amountPaid = amountToPay;
            change = 0;
        }

        boolean exitSuccess = controller.exitVehicle(currentVehicle.getLicensePlate());
        
        if (!exitSuccess) {
            JOptionPane.showMessageDialog(this, "Error processing exit. Please try again.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        controller.recordRevenue(amountPaid, "Parking fee for " + currentVehicle.getLicensePlate());

        if (payFinesNow) {
            if (unpaidFines > 0) {
                controller.markFinesPaid(currentVehicle.getLicensePlate());
            }
        }
        
        if (currentSessionFines > 0) {
            if (hoursParked > 24) {
                double overstayFine = controller.calculateFine(currentVehicle.getLicensePlate(), hoursParked);
                controller.addFine(currentVehicle.getLicensePlate(), overstayFine, "Overstay fine");
                if (payFinesNow) {
                    controller.markFinesPaid(currentVehicle.getLicensePlate());
                }
            }
            
            if (controller.isReservedSpotMisuse(currentVehicle)) {
                controller.addFine(currentVehicle.getLicensePlate(), 100.0, "Reserved spot misuse");
                if (payFinesNow) {
                    controller.markFinesPaid(currentVehicle.getLicensePlate());
                }
            }
        }

        generateReceipt(paymentMethod, amountPaid, change, payFinesNow);
    }

    private Double processCashPayment(double amountToPay) {
        double remaining = amountToPay;
        double totalPaid = 0;
        
        while (remaining > 0.01) {
            String message;
            if (totalPaid == 0) {
                message = String.format("Amount due: RM %.2f\n\nEnter cash amount:", amountToPay);
            } else {
                message = String.format("Amount due: RM %.2f\nTotal paid so far: RM %.2f\nRemaining: RM %.2f\n\nEnter cash amount:",
                    amountToPay, totalPaid, remaining);
            }
            
            String input = JOptionPane.showInputDialog(
                this,
                message,
                "Cash Payment",
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (input == null) {
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Payment not complete. Cancel transaction?",
                    "Confirm Cancel",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    return null;
                }
                continue;
            }
            
            try {
                double payment = Double.parseDouble(input.trim());
                
                if (payment <= 0) {
                    JOptionPane.showMessageDialog(this,
                        "Please enter a positive amount!",
                        "Invalid Amount",
                        JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                
                totalPaid += payment;
                remaining = amountToPay - totalPaid;
                
                if (remaining > 0.01) {
                    JOptionPane.showMessageDialog(this,
                        String.format("Payment received: RM %.2f\n\nRemaining balance: RM %.2f\n\nPlease continue payment...",
                            payment, remaining),
                        "Partial Payment",
                        JOptionPane.INFORMATION_MESSAGE);
                } else if (remaining < -0.01) {
                    double changeAmount = Math.abs(remaining);
                    JOptionPane.showMessageDialog(this,
                        String.format("Payment received: RM %.2f\n\nChange to return: RM %.2f",
                            payment, changeAmount),
                        "Payment Complete",
                        JOptionPane.INFORMATION_MESSAGE);
                    return totalPaid;
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Payment complete!\n\nExact amount received.",
                        "Payment Complete",
                        JOptionPane.INFORMATION_MESSAGE);
                    return totalPaid;
                }
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Invalid amount entered!\n\nPlease enter a valid number.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        
        return totalPaid;
    }

    private void processCardPayment(double amountToPay) {
        JDialog processingDialog = new JDialog(this, "Processing Payment", true);
        processingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        processingDialog.setSize(450, 180);
        processingDialog.setLocationRelativeTo(this);
        processingDialog.setUndecorated(false);
        
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(3, 78, 161), 3),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setBackground(Color.WHITE);
        
        JLabel messageLabel = new JLabel(
            String.format("<html><center><b>Processing card payment...</b><br><br>Amount: RM %.2f<br><br>Please wait...</center></html>",
                amountToPay),
            SwingConstants.CENTER
        );
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(400, 25));
        
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);
        
        processingDialog.add(panel);
        
        Timer timer = new Timer(2000, e -> {
            processingDialog.dispose();
            JOptionPane.showMessageDialog(this,
                String.format("✓ Card payment successful!\n\nAmount charged: RM %.2f", amountToPay),
                "Payment Approved",
                JOptionPane.INFORMATION_MESSAGE);
        });
        timer.setRepeats(false);
        timer.start();
        
        processingDialog.setVisible(true);
    }

    private void generateReceipt(String paymentMethod, double amountPaid, double change, boolean paidAllFines) {
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
}