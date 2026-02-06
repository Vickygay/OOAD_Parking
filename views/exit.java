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

public class exit extends JFrame{
    private Color blueColor = new Color(3, 78, 161);
    private Color redColor = new Color(255, 7, 7);
    private Color whiteGreyColor = new Color(238,241,241);
    private Font headerFont = new Font("SansSerif", Font.BOLD, 30);
    private Font contentFont = new Font("SansSerif", Font.BOLD, 20);
    
    private JTextField licensePlate;

    private String vehicleType;
    private String vehiclePlateNumber;
    private String time;
   
    public exit()
    {
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
        gbc.gridwidth = 1;
        gbc.gridy++; //go to next row
        gbc.gridx = 0;
        JButton submit = new JButton("Generate Bill");
        submit.setPreferredSize(new Dimension(250, 50));

        submit.setFont(contentFont);

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String plate = licensePlate.getText().trim();
                // Search for the car
                if (readParkingDetails(plate)) {
                    showBill(); // Only show bill if car is found
                } else {
                    JOptionPane.showMessageDialog(null, "Vehicle not found!");
                }
            }
        });

        content.add(submit, gbc);

        add(content, BorderLayout.CENTER);
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

                if (data.length >= 4)
                {
                    if (data[2].trim().equalsIgnoreCase(searchPlate))
                    {
                        vehicleType = data[1];
                        time = data[3];
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

    public void showBill()
    {
        JDialog billDialog = new JDialog(this, "Bill Details", true);
        billDialog.setSize(400, 300);
        billDialog.setLayout(new GridBagLayout());
        billDialog.setLocationRelativeTo(this); //center it to middle of screen

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;  

// • Hours parked 
// • Parking fee 
// • Any unpaid fines 
// • Total payment due
        long hours = 0;
        double totalFee = 0;
        double ratePerHour = 0;
// TODO: calculate rateperhour
        if (this.vehicleType == "Compact")
        {
            ratePerHour = 2;
        }
        else if (this.vehicleType == "Regular")
        {
            ratePerHour = 5;
        }
        else if (this.vehicleType == "Handicapped")
        {
            ratePerHour = 5;
        }
        
        else
        {
            ratePerHour = 5;
        }
        // calculate the hours and fees
        try
        {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date entryDate = sdf.parse(this.time);
        java.util.Date currentDate = new java.util.Date();
        long diff = currentDate.getTime() - entryDate.getTime();

        hours = diff / (1000 * 60 * 60);

        if (diff % (1000 * 60 * 60) > 0) {
            hours++;
        }

        totalFee = hours * ratePerHour;

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error parsing date: " + this.time);
            return; 
        }
        
        gbc.gridx = 0; //left column
        gbc.gridy = 0; //first row
        gbc.gridwidth = 1; //span one column
        
        JLabel hoursParkedLabel = new JLabel("Hours parked: " + hours + "hours");
        hoursParkedLabel.setFont(contentFont);
        billDialog.add(hoursParkedLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++; //go to next row
        gbc.gridx = 0;
        
        JLabel parkingFeeLabel = new JLabel("Parking Fee: " + totalFee);
        parkingFeeLabel.setFont(contentFont);
        billDialog.add(parkingFeeLabel, gbc); 
        
        gbc.gridwidth = 1;
        gbc.gridy++; //go to next row
        gbc.gridx = 0;
        
        JLabel unpaidFeeLabel = new JLabel("Unpaid Fee: ");
        unpaidFeeLabel.setFont(contentFont);
        billDialog.add(unpaidFeeLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy++; //go to next row
        gbc.gridx = 0;
        
        JLabel totalPaymentDueLabel = new JLabel("Total Payment Due: ");
        totalPaymentDueLabel.setFont(contentFont);
        billDialog.add(totalPaymentDueLabel, gbc);

        billDialog.setVisible(true);

    }
    
}
