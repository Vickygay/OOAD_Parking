package views;
import javax.swing.*;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import models.*;
import controllers.*;

public class entry extends JFrame{
    private Color blueColor = new Color(3, 78, 161);
    private Color redColor = new Color(255, 7, 7);
    private Color whiteGreyColor = new Color(238,241,241);
    private Color greenColor = new Color(46, 204, 113);
    private Color purpleColor = new Color(155, 89, 182);
    private Color yellowColor = new Color(241, 196, 15);
    private Color lightBlueColor = new Color(52, 152, 219);
    private Font headerFont = new Font("SansSerif", Font.BOLD, 30);
    private Font contentFont = new Font("SansSerif", Font.BOLD, 20);
    
    private JTextField name;
    private JComboBox<String> vehicleType;
    private JComboBox<String> handicappedCard;
    private JTextField licensePlate;
    private JFormattedTextField dateField;
    private JTabbedPane floorTabs;
    private JLabel selectedSpotLabel;
    private String selectedSpotID = null;
    private String ticket;
    private parkingcontroller controller;
    
    public entry()
    {
        controller = new parkingcontroller();
        
        setTitle("Entry");
        setSize(1300, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(blueColor);
        JLabel title = new JLabel("Please fill up the details to secure a spot");
        title.setForeground(Color.WHITE);
        title.setFont(headerFont);
        header.add(title);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(whiteGreyColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);  

    // enter name
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        JLabel nameLabel = new JLabel("Enter your name: ");
        nameLabel.setFont(contentFont);
        content.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        name = new JTextField(50);
        content.add(name, gbc);

    // choose vehicle type
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel vehicleTypLabel = new JLabel("Choose your vehicle type: ");
        vehicleTypLabel.setFont(contentFont);
        content.add(vehicleTypLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        String[] vehicle = {"Compact", "Regular", "Handicapped", "Reserved"};
        vehicleType = new JComboBox<>(vehicle);
        vehicleType.addActionListener(e -> updateAvailableSpots());
        content.add(vehicleType, gbc);

    // handicapped card holder 
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel handicappedCardLabel = new JLabel("Handicapped card holder : ");
        handicappedCardLabel.setFont(contentFont);
        content.add(handicappedCardLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        String[] card = {"Yes", "No"};
        handicappedCard = new JComboBox<>(card);
        handicappedCard.addActionListener(e -> updateAvailableSpots());
        content.add(handicappedCard, gbc);

    // enter car plate
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel licensePlateLabel = new JLabel("Enter your license plate: ");
        licensePlateLabel.setFont(contentFont);
        content.add(licensePlateLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        licensePlate = new JTextField(50);
        content.add(licensePlate, gbc);


    // enter time (filled up automatically)
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel timeLabel = new JLabel("Time: ");
        timeLabel.setFont(contentFont);
        content.add(timeLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormatter dateFormatter = new DateFormatter(sdf);
        dateField = new JFormattedTextField(new DefaultFormatterFactory(dateFormatter));
        dateField.setColumns(20);
        dateField.setValue(new Date());
        content.add(dateField, gbc);

    // choose spot - floor tabs
        gbc.gridwidth = 2;
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel spotSelectionPanel = new JPanel(new BorderLayout(10, 10));
        spotSelectionPanel.setBackground(whiteGreyColor);
        spotSelectionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(blueColor, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel spotSectionLabel = new JLabel("Select Your Parking Spot:");
        spotSectionLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        spotSectionLabel.setForeground(blueColor);
        spotSelectionPanel.add(spotSectionLabel, BorderLayout.NORTH);
        
        floorTabs = new JTabbedPane();
        floorTabs.setFont(new Font("SansSerif", Font.PLAIN, 14));
        floorTabs.setPreferredSize(new Dimension(900, 250));
        spotSelectionPanel.add(floorTabs, BorderLayout.CENTER);
        
        selectedSpotLabel = new JLabel("No spot selected");
        selectedSpotLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        selectedSpotLabel.setForeground(redColor);
        spotSelectionPanel.add(selectedSpotLabel, BorderLayout.SOUTH);
        
        content.add(spotSelectionPanel, gbc);
        
        updateAvailableSpots();

        gbc.gridwidth = 2;
        gbc.gridy++;
        gbc.gridx = 0;

        JButton submit = new JButton("Submit");
        JButton cancel = new JButton("Cancel"); 

        submit.setPreferredSize(new Dimension(150, 50));
        cancel.setPreferredSize(new Dimension(150, 50));

        submit.setFont(contentFont);
        cancel.setFont(contentFont);

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (!validateBeforeSubmit()) {
                    return;
                }
                
                String date = dateField.getText();
                String licensePlateText = licensePlate.getText().trim().toUpperCase();
                ticket = "T-" + licensePlateText + "-" + date;
                
                if (save()) {
                    JOptionPane.showMessageDialog(null, "Thank you. \n Your Ticket: \n" + ticket);
                    new dashboard().setVisible(true);
                    dispose();
                }
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
        buttonPanel.add(cancel);

        content.add(buttonPanel, gbc);

        add(content, BorderLayout.CENTER);
    }

    private boolean validateBeforeSubmit() {
        String nameText = name.getText().trim();
        String licensePlateText = licensePlate.getText().trim().toUpperCase();
        String selectedType = (String) vehicleType.getSelectedItem();
        String hasHandicappedCard = (String) handicappedCard.getSelectedItem();
        
        if (nameText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your name!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (licensePlateText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter license plate!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (selectedSpotID == null) {
            JOptionPane.showMessageDialog(this, "Please select a parking spot!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (selectedType.equalsIgnoreCase("Handicapped")) {
            if (!hasHandicappedCard.equalsIgnoreCase("Yes")) {
                JOptionPane.showMessageDialog(this, 
                    "Handicapped card holder must select 'Yes'!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            
            if (!isHandicappedCardHolder(licensePlateText)) {
                JOptionPane.showMessageDialog(this, 
                    "Your license plate is not registered as handicapped card holder!\nPlease contact admin to register.", 
                    "Access Denied", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        
        parkinglot lot = parkinglot.getInstance();
        parkingspot selectedSpot = lot.findSpotByID(selectedSpotID);
        
        if (selectedSpot != null && selectedSpot.getType().equalsIgnoreCase("Handicapped")) {
            if (!hasHandicappedCard.equalsIgnoreCase("Yes")) {
                JOptionPane.showMessageDialog(this, 
                    "You cannot park in a handicapped spot without a valid handicapped card!", 
                    "Invalid Selection", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        
        return true;
    }
    
    private boolean isVIPMember(String plate) {
        try (BufferedReader br = new BufferedReader(new FileReader("reserved.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().equalsIgnoreCase(plate)) {
                    return true;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }
    
    private boolean isHandicappedCardHolder(String plate) {
        try (BufferedReader br = new BufferedReader(new FileReader("handicapped.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().equalsIgnoreCase(plate)) {
                    return true;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    private void updateAvailableSpots() {
        floorTabs.removeAll();
        selectedSpotID = null;
        selectedSpotLabel.setText("No spot selected");
        selectedSpotLabel.setForeground(redColor);
        
        String selectedType = (String) vehicleType.getSelectedItem();
        String hasHandicappedCard = (String) handicappedCard.getSelectedItem();
        parkinglot lot = parkinglot.getInstance();
        
        for (floor f : lot.getFloors()) {
            List<parkingspot> availableSpots = f.getAvailableSpots(selectedType);
            
            JPanel floorPanel = new JPanel(new BorderLayout(10, 10));
            floorPanel.setBackground(Color.WHITE);
            
            if (availableSpots.isEmpty()) {
                JLabel noSpotsLabel = new JLabel("No available " + selectedType + " spots on this floor");
                noSpotsLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
                noSpotsLabel.setForeground(redColor);
                noSpotsLabel.setHorizontalAlignment(SwingConstants.CENTER);
                floorPanel.add(noSpotsLabel, BorderLayout.CENTER);
            } else {
                JLabel countLabel = new JLabel("Available: " + availableSpots.size() + " spots");
                countLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
                countLabel.setForeground(greenColor);
                floorPanel.add(countLabel, BorderLayout.NORTH);
                
                JPanel gridPanel = new JPanel(new GridLayout(0, 15, 3, 3));
                gridPanel.setBackground(Color.WHITE);
                
                for (parkingspot spot : availableSpots) {
                    JButton spotBtn = new JButton("<html><center>" + 
                        spot.getSpotID().substring(spot.getSpotID().lastIndexOf("-") + 1) + 
                        "<br>" + spot.getType().substring(0, 1) + "</center></html>");
                    spotBtn.setFont(new Font("SansSerif", Font.BOLD, 10));
                    spotBtn.setPreferredSize(new Dimension(55, 45));
                    
                    Color spotColor = getSpotColorByType(spot.getType());
                    spotBtn.setBackground(spotColor);
                    spotBtn.setOpaque(true);
                    spotBtn.setBorderPainted(true);
                    spotBtn.setForeground(Color.BLACK);
                    
                    double displayRate = spot.getHourlyRate();
                    String rateText = "RM " + displayRate + "/hr";
                    
                    if (spot.getType().equalsIgnoreCase("Handicapped") && hasHandicappedCard.equalsIgnoreCase("Yes")) {
                        displayRate = 0.0;
                        rateText = "RM 0.00/hr (FREE)";
                    }
                    
                    String tooltip = spot.getSpotID() + " - " + spot.getType() + " (" + rateText + ")";
                    spotBtn.setToolTipText(tooltip);
                    
                    final double finalDisplayRate = displayRate;
                    final String finalRateText = rateText;
                    
                    spotBtn.addActionListener(e -> {
                        selectedSpotID = spot.getSpotID();
                        selectedSpotLabel.setText("Selected: " + spot.getSpotID() + " (" + spot.getType() + " - " + finalRateText + ")");
                        selectedSpotLabel.setForeground(greenColor);
                    });
                    
                    gridPanel.add(spotBtn);
                }
                
                JScrollPane scrollPane = new JScrollPane(gridPanel);
                scrollPane.setBorder(BorderFactory.createEmptyBorder());
                floorPanel.add(scrollPane, BorderLayout.CENTER);
            }
            
            floorTabs.addTab("Floor " + f.getFloorNumber(), floorPanel);
        }
    }
    
    private Color getSpotColorByType(String type) {
        switch (type.toLowerCase()) {
            case "reserved":
                return purpleColor;
            case "handicapped":
                return lightBlueColor;
            case "compact":
                return greenColor;
            case "regular":
                return yellowColor;
            default:
                return Color.GRAY;
        }
    }

    private boolean save() {
        String nameText = name.getText();
        String licensePlateText = licensePlate.getText().toUpperCase();
        String date = dateField.getText();
        String fileName = "parking.txt";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
            bw.write(nameText + "," + (String) vehicleType.getSelectedItem() + "," + (String) handicappedCard.getSelectedItem() + "," + licensePlateText + "," + date + "," + selectedSpotID);
            bw.newLine(); 
            
            parkinglot lot = parkinglot.getInstance();
            parkingspot spot = lot.findSpotByID(selectedSpotID);
            if (spot != null) {
                spot.occupy(licensePlateText);
            }
            
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

}