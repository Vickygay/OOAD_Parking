package views;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;

import models.*;
import controllers.*;

public class Exit extends JFrame{
    private Color blueColor = new Color(3, 78, 161);
    private Color whiteGreyColor = new Color(238,241,241);
    private Font headerFont = new Font("SansSerif", Font.BOLD, 30);
    private Font contentFont = new Font("SansSerif", Font.BOLD, 20);
    
    private JTextField licensePlate;
    private JTextArea detailsArea;
    private JButton processPaymentButton;
    
    private VehicleRecord currentVehicle;
    private double parkingFee;
    private double unpaidFines;
    private double currentSessionFines;
    private double totalDue;
    private long hoursParked;
    private ParkingController controller;
   
    public Exit()
    {
        controller = new ParkingController();
        
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
                // Open payment processing
                openPaymentPanel();
            }
        });

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                new Dashboard().setVisible(true);
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

        ParkingLot lot = ParkingLot.getInstance();
        ParkingSpot spot = lot.findSpotByID(currentVehicle.getSpotID());
        
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

    private void openPaymentPanel() {
        if (currentVehicle == null) {
            JOptionPane.showMessageDialog(this, "Please generate a bill first!",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create PaymentPanel and trigger payment process
        PaymentPanel paymentPanel = new PaymentPanel(
            this,
            controller,
            currentVehicle,
            parkingFee,
            unpaidFines,
            currentSessionFines,
            totalDue,
            (int) hoursParked
        );
        
        // Trigger the payment process immediately
        paymentPanel.processPayment();
    }

}