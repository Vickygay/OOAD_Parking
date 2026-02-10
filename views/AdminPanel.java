package views;

import javax.swing.*;
import java.awt.*;
import controllers.*;
import models.*;
import java.util.List;

public class AdminPanel extends JFrame {
    private Color blueColor = new Color(3, 78, 161);
    private Color greenColor = new Color(46, 204, 113);
    private Color redColor = new Color(231, 76, 60);
    private Color whiteGreyColor = new Color(238, 241, 241);
    private Color purpleColor = new Color(155, 89, 182);
    private Color yellowColor = new Color(255, 255, 51);
    private Color lightBlueColor = new Color(52, 152, 219);
    private Color white = new Color(255, 255, 255);
    
    private Font headerFont = new Font("SansSerif", Font.BOLD, 24);
    private Font contentFont = new Font("SansSerif", Font.PLAIN, 14);
    
    private ParkingController parkingController;
    private JTabbedPane tabbedPane;

    public AdminPanel(String adminID) {
        parkingController = new ParkingController();

        setTitle("Parking Lot Management System Admin Dashboard signed in as: " + adminID);
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(blueColor);
        JLabel title = new JLabel("Admin Dashboard");
        title.setForeground(Color.WHITE);
        title.setFont(headerFont);
        header.add(title);
        add(header, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(contentFont);

        tabbedPane.addTab("Parking Overview", createOverviewPanel());
        tabbedPane.addTab("Current Vehicles", new CurrentVehiclesPanel(parkingController));
        tabbedPane.addTab("Revenue Report", new RevenuePanel(parkingController, tabbedPane, 2));
        tabbedPane.addTab("Fine Management", new FineManagementPanel(parkingController));
        tabbedPane.addTab("Settings", new SettingsPanel());

        add(tabbedPane, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        JButton backBtn = new JButton("Logout");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        backBtn.addActionListener(e -> {
            new Dashboard().setVisible(true);
            dispose();
        });
        backBtn.setBackground(new Color(255, 0, 0));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);

        footer.add(backBtn, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(whiteGreyColor);

        ParkingLot lot = ParkingLot.getInstance();
        
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setBackground(whiteGreyColor);

        JPanel totalSpotsCard = createStatCard("Total Spots", 
            String.valueOf(lot.getTotalSpots()), blueColor);
        JPanel availableCard = createStatCard("Available", 
            String.valueOf(lot.getTotalSpots() - lot.getTotalOccupied()), greenColor);
        JPanel occupiedCard = createStatCard("Occupied", 
            String.valueOf(lot.getTotalOccupied()), redColor);

        statsPanel.add(totalSpotsCard);
        statsPanel.add(availableCard);
        statsPanel.add(occupiedCard);

        panel.add(statsPanel, BorderLayout.NORTH);

        JPanel floorsPanel = new JPanel();
        floorsPanel.setLayout(new BoxLayout(floorsPanel, BoxLayout.Y_AXIS));
        floorsPanel.setBackground(whiteGreyColor);

        for (Floor f : lot.getFloors()) {
            JPanel floorPanel = createFloorPanel(f);
            floorsPanel.add(floorPanel);
            floorsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scrollPane = new JScrollPane(floorsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        refreshBtn.setBackground(blueColor);
        refreshBtn.setForeground(white);

        refreshBtn.addActionListener(e -> {
            tabbedPane.setComponentAt(0, createOverviewPanel());
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(whiteGreyColor);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStatCard(String label, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("SansSerif", Font.PLAIN, 16));
        labelText.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(labelText, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createFloorPanel(Floor f) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel floorLabel = new JLabel("Floor " + f.getFloorNumber());
        floorLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        double occupancyRate = f.getTotalSpots() > 0 ? 
            (double) f.getOccupiedCount() / f.getTotalSpots() * 100 : 0;
        JLabel statsLabel = new JLabel(String.format(
            "Occupancy: %.1f%% | Available: %d | Occupied: %d | Total: %d",
            occupancyRate, f.getAvailableCount(), f.getOccupiedCount(), f.getTotalSpots()
        ));
        statsLabel.setFont(contentFont);

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.add(floorLabel);
        headerPanel.add(statsLabel);

        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel spotsPanel = new JPanel(new GridLayout(0, 15, 3, 3));
        spotsPanel.setBackground(Color.WHITE);

        List<ParkingSpot> spots = f.getSpots();
        
        for (ParkingSpot spot : spots) {
            JButton spotBtn = new JButton("<html><center>" + spot.getSpotID().substring(spot.getSpotID().lastIndexOf("-") + 1) + "<br>" + 
                                          spot.getType().substring(0, 1) + "</center></html>");
            spotBtn.setFont(new Font("SansSerif", Font.BOLD, 9));
            spotBtn.setPreferredSize(new Dimension(50, 40));
            
            Color spotColor = getSpotColor(spot.getType(), spot.isOccupied());
            spotBtn.setBackground(spotColor);
            spotBtn.setOpaque(true);
            spotBtn.setBorderPainted(true);
            spotBtn.setForeground(spot.isOccupied() ? Color.WHITE : Color.BLACK);
            
            String tooltip = spot.getSpotID() + " - " + spot.getType();
            if (spot.isOccupied()) {
                tooltip += " (Occupied: " + spot.getCurrentVehiclePlate() + ")";
            } else {
                tooltip += " (Available)";
            }
            spotBtn.setToolTipText(tooltip);
            
            spotBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (evt.getClickCount() == 2) {
                        showSpotDetails(spot);
                    }
                }
            });
            
            spotsPanel.add(spotBtn);
        }

        JScrollPane scrollPane = new JScrollPane(spotsPanel);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        legendPanel.setBackground(Color.WHITE);
        legendPanel.add(createLegendItem("Reserved", purpleColor, new Color(75, 0, 130)));
        legendPanel.add(createLegendItem("Handicapped", lightBlueColor, new Color(21, 67, 96)));
        legendPanel.add(createLegendItem("Compact", greenColor, new Color(22, 160, 133)));
        legendPanel.add(createLegendItem("Regular", yellowColor, new Color(210, 210, 51) ));
        
        JLabel hintLabel = new JLabel("(Double-click spot for details)");
        hintLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hintLabel.setForeground(Color.GRAY);
        legendPanel.add(hintLabel);
        
        panel.add(legendPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showSpotDetails(ParkingSpot spot) {
        JDialog detailsDialog = new JDialog(this, "Spot Details - " + spot.getSpotID(), true);
        detailsDialog.setSize(550, 550);
        detailsDialog.setLocationRelativeTo(this);
        detailsDialog.setLayout(new BorderLayout(15, 15));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Parking Spot Information");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(blueColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        addDetailRow(contentPanel, "Spot ID:", spot.getSpotID());
        addDetailRow(contentPanel, "Spot Type:", spot.getType());
        addDetailRow(contentPanel, "Hourly Rate:", "RM " + String.format("%.2f", spot.getHourlyRate()) + " /hour");
        
        Color statusColor = spot.isOccupied() ? redColor : greenColor;
        String statusText = spot.isOccupied() ? "OCCUPIED" : "AVAILABLE";
        JLabel statusLabel = new JLabel("Status: " + statusText);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setForeground(statusColor);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(statusLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        if (spot.isOccupied()) {
            JSeparator separator = new JSeparator();
            separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            contentPanel.add(separator);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            
            JLabel occupancyTitle = new JLabel("Current Vehicle Details:");
            occupancyTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
            occupancyTitle.setForeground(blueColor);
            occupancyTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(occupancyTitle);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            
            VehicleRecord vehicle = parkingController.findVehicleByPlate(spot.getCurrentVehiclePlate());
            
            if (vehicle != null) {
                addDetailRow(contentPanel, "License Plate:", vehicle.getLicensePlate());
                addDetailRow(contentPanel, "Customer Name:", vehicle.getName());
                addDetailRow(contentPanel, "Vehicle Type:", vehicle.getVehicleType());
                addDetailRow(contentPanel, "Handicapped Card:", vehicle.getHandicappedCard());
                addDetailRow(contentPanel, "Entry Time:", vehicle.getEntryTime());
                
                long hoursParked = parkingController.calculateHours(vehicle.getEntryTime());
                addDetailRow(contentPanel, "Hours Parked:", hoursParked + " hour(s)");
                
                // calculate current fee with handicapped discount applied
                double hourlyRate = spot.getHourlyRate();
                
                // Apply handicapped card holder discount (RM 2/hr off)
                if (vehicle.getVehicleType().equalsIgnoreCase("Handicapped") && 
                    vehicle.getHandicappedCard().equalsIgnoreCase("Yes")) {
                    
                    // Subtract RM 2 discount from hourly rate
                    hourlyRate = hourlyRate - 2.0;
                    
                    // Cannot go below 0
                    if (hourlyRate < 0) {
                        hourlyRate = 0.0;
                    }
                }
                
                double currentFee = hoursParked * hourlyRate;
                
                addDetailRow(contentPanel, "Current Fee:", "RM " + String.format("%.2f", currentFee));
                
                contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                JSeparator separator2 = new JSeparator();
                separator2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                contentPanel.add(separator2);
                contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                
                JLabel fineTitle = new JLabel("Fine Details:");
                fineTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
                fineTitle.setForeground(blueColor);
                fineTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
                contentPanel.add(fineTitle);
                contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                
                boolean hasFines = false;
                double totalFines = 0.0;
                
                if (hoursParked > 24) {
                    hasFines = true;
                    double overstayFine = parkingController.calculateFine(vehicle.getLicensePlate(), hoursParked);
                    totalFines += overstayFine;
                    
                    JLabel fineLabel = new JLabel("• Overstay Fine: RM " + String.format("%.2f", overstayFine));
                    fineLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    fineLabel.setForeground(redColor);
                    fineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    contentPanel.add(fineLabel);
                    
                    JLabel fineReason = new JLabel("  (Vehicle stayed more than 24 hours)");
                    fineReason.setFont(new Font("SansSerif", Font.ITALIC, 12));
                    fineReason.setForeground(Color.GRAY);
                    fineReason.setAlignmentX(Component.LEFT_ALIGNMENT);
                    contentPanel.add(fineReason);
                    contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                }
                
                if (parkingController.isReservedSpotMisuse(vehicle)) {
                    hasFines = true;
                    totalFines += 100.0;
                    
                    JLabel fineLabel = new JLabel("• Reserved Spot Fine: RM 100.00");
                    fineLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    fineLabel.setForeground(redColor);
                    fineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    contentPanel.add(fineLabel);
                    
                    JLabel fineReason = new JLabel("  (Parked in reserved spot without reservation)");
                    fineReason.setFont(new Font("SansSerif", Font.ITALIC, 12));
                    fineReason.setForeground(Color.GRAY);
                    fineReason.setAlignmentX(Component.LEFT_ALIGNMENT);
                    contentPanel.add(fineReason);
                    contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                }
                
                double unpaidFines = parkingController.getUnpaidFines(vehicle.getLicensePlate());
                if (unpaidFines > 0) {
                    hasFines = true;
                    totalFines += unpaidFines;
                    
                    JLabel fineLabel = new JLabel("• Unpaid Previous Fines: RM " + String.format("%.2f", unpaidFines));
                    fineLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    fineLabel.setForeground(redColor);
                    fineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    contentPanel.add(fineLabel);
                    
                    JLabel fineReason = new JLabel("  (From previous parking sessions)");
                    fineReason.setFont(new Font("SansSerif", Font.ITALIC, 12));
                    fineReason.setForeground(Color.GRAY);
                    fineReason.setAlignmentX(Component.LEFT_ALIGNMENT);
                    contentPanel.add(fineReason);
                    contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                }
                
                if (!hasFines) {
                    JLabel noFineLabel = new JLabel("No fines");
                    noFineLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    noFineLabel.setForeground(greenColor);
                    noFineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    contentPanel.add(noFineLabel);
                } else {
                    contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                    JLabel totalFineLabel = new JLabel("Total Fines: RM " + String.format("%.2f", totalFines));
                    totalFineLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
                    totalFineLabel.setForeground(redColor);
                    totalFineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    contentPanel.add(totalFineLabel);
                }
                
            } else {
                addDetailRow(contentPanel, "License Plate:", spot.getCurrentVehiclePlate());
                JLabel noDataLabel = new JLabel("(Vehicle data not found in records)");
                noDataLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
                noDataLabel.setForeground(Color.GRAY);
                noDataLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                contentPanel.add(noDataLabel);
            }
        } else {
            JLabel availableNote = new JLabel("This spot is currently available for parking.");
            availableNote.setFont(new Font("SansSerif", Font.ITALIC, 14));
            availableNote.setForeground(Color.GRAY);
            availableNote.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(availableNote);
        }
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        detailsDialog.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(whiteGreyColor);
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(contentFont);
        closeBtn.addActionListener(e -> detailsDialog.dispose());
        buttonPanel.add(closeBtn);
        detailsDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        detailsDialog.setVisible(true);
    }
    
    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("SansSerif", Font.BOLD, 14));
        labelComponent.setPreferredSize(new Dimension(150, 20));
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        rowPanel.add(labelComponent);
        rowPanel.add(valueComponent);
        
        panel.add(rowPanel);
    }

    private JPanel createLegendItem(String label, Color availableColor, Color occupiedColor) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setBackground(Color.WHITE);
        
        JPanel colorBox1 = new JPanel();
        colorBox1.setPreferredSize(new Dimension(15, 15));
        colorBox1.setBackground(availableColor);
        colorBox1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        JPanel colorBox2 = new JPanel();
        colorBox2.setPreferredSize(new Dimension(15, 15));
        colorBox2.setBackground(occupiedColor);
        colorBox2.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        JLabel text = new JLabel(label + ":");
        text.setFont(new Font("SansSerif", Font.PLAIN, 11));
        
        item.add(text);
        item.add(colorBox1);
        item.add(new JLabel("/"));
        item.add(colorBox2);
        
        return item;
    }

    // setting spot type colour
    private Color getSpotColor(String type, boolean isOccupied) {
        Color baseColor;
        switch (type.toLowerCase()) {
            case "reserved":
                baseColor = isOccupied ? new Color(75, 0, 130) : purpleColor;
                break;
            case "handicapped":
                baseColor = isOccupied ? new Color(21, 67, 96) : lightBlueColor;
                break;
            case "compact":
                baseColor = isOccupied ? new Color(22, 160, 133) : greenColor;
                break;
            case "regular":
                baseColor = isOccupied ? new Color(210, 210, 51) : yellowColor;
                break;
            default:
                baseColor = Color.GRAY;
        }
        return baseColor;
    }

}