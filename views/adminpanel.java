package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import controllers.*;
import models.*;
import java.util.List;

public class adminpanel extends JFrame {
    private Color blueColor = new Color(3, 78, 161);
    private Color greenColor = new Color(46, 204, 113);
    private Color redColor = new Color(231, 76, 60);
    private Color whiteGreyColor = new Color(238, 241, 241);
    private Color purpleColor = new Color(155, 89, 182);
    private Color yellowColor = new Color(241, 196, 15);
    private Color lightBlueColor = new Color(52, 152, 219);
    
    private Font headerFont = new Font("SansSerif", Font.BOLD, 24);
    private Font contentFont = new Font("SansSerif", Font.PLAIN, 14);
    
    private parkingcontroller parkingController;
    private JTabbedPane tabbedPane;

    public adminpanel() {
        parkingController = new parkingcontroller();

        setTitle("Admin Dashboard");
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
        tabbedPane.addTab("Current Vehicles", createCurrentVehiclesPanel());
        tabbedPane.addTab("Revenue Report", createRevenuePanel());
        tabbedPane.addTab("Fine Management", createFinePanel());
        tabbedPane.addTab("Settings", createSettingsPanel());

        add(tabbedPane, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        JButton backBtn = new JButton("Logout");
        backBtn.setFont(contentFont);
        backBtn.addActionListener(e -> {
            new dashboard().setVisible(true);
            dispose();
        });
        footer.add(backBtn, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(whiteGreyColor);

        parkinglot lot = parkinglot.getInstance();
        
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

        for (floor f : lot.getFloors()) {
            JPanel floorPanel = createFloorPanel(f);
            floorsPanel.add(floorPanel);
            floorsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scrollPane = new JScrollPane(floorsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(contentFont);
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

    private JPanel createFloorPanel(floor f) {
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

        List<parkingspot> spots = f.getSpots();
        
        for (parkingspot spot : spots) {
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
        legendPanel.add(createLegendItem("Regular", yellowColor, new Color(183, 149, 11)));
        
        JLabel hintLabel = new JLabel("(Double-click spot for details)");
        hintLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hintLabel.setForeground(Color.GRAY);
        legendPanel.add(hintLabel);
        
        panel.add(legendPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showSpotDetails(parkingspot spot) {
        JDialog detailsDialog = new JDialog(this, "Spot Details - " + spot.getSpotID(), true);
        detailsDialog.setSize(500, 400);
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
            
            vehiclerecord vehicle = parkingController.findVehicleByPlate(spot.getCurrentVehiclePlate());
            
            if (vehicle != null) {
                addDetailRow(contentPanel, "License Plate:", vehicle.getLicensePlate());
                addDetailRow(contentPanel, "Customer Name:", vehicle.getName());
                addDetailRow(contentPanel, "Vehicle Type:", vehicle.getVehicleType());
                addDetailRow(contentPanel, "Entry Time:", vehicle.getEntryTime());
                
                long hoursParked = parkingController.calculateHours(vehicle.getEntryTime());
                addDetailRow(contentPanel, "Hours Parked:", hoursParked + " hour(s)");
                
                double currentFee = hoursParked * spot.getHourlyRate();
                
                if (vehicle.getVehicleType().equalsIgnoreCase("Handicapped") && 
                    vehicle.getHandicappedCard().equalsIgnoreCase("Yes") &&
                    spot.getType().equalsIgnoreCase("Handicapped")) {
                    currentFee = 0.0;
                }
                
                addDetailRow(contentPanel, "Current Fee:", "RM " + String.format("%.2f", currentFee));
                
                if (hoursParked > 24) {
                    JLabel overstayWarning = new JLabel("Overstay detected! Fine will apply.");
                    overstayWarning.setFont(new Font("SansSerif", Font.BOLD, 14));
                    overstayWarning.setForeground(redColor);
                    overstayWarning.setAlignmentX(Component.LEFT_ALIGNMENT);
                    contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                    contentPanel.add(overstayWarning);
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
                baseColor = isOccupied ? new Color(183, 149, 11) : yellowColor;
                break;
            default:
                baseColor = Color.GRAY;
        }
        return baseColor;
    }

    private JPanel createCurrentVehiclesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(whiteGreyColor);

        String[] columns = {"License Plate", "Customer Name", "Vehicle Type", "Spot ID", "Spot Type", "Entry Time", "Hours Parked"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFont(contentFont);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        List<vehiclerecord> activeVehicles = parkingController.getAllParkedVehicles();
        parkinglot lot = parkinglot.getInstance();

        for (vehiclerecord vehicle : activeVehicles) {
            long hoursSoFar = parkingController.calculateHours(vehicle.getEntryTime());
            parkingspot spot = lot.findSpotByID(vehicle.getSpotID());
            String spotType = spot != null ? spot.getType() : "Unknown";
            
            model.addRow(new Object[]{
                vehicle.getLicensePlate(),
                vehicle.getName(),
                vehicle.getVehicleType(),
                vehicle.getSpotID(),
                spotType,
                vehicle.getEntryTime(),
                hoursSoFar + " hrs"
            });
        }

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JLabel summary = new JLabel("Total vehicles currently parked: " + activeVehicles.size());
        summary.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(summary, BorderLayout.NORTH);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(contentFont);
        refreshBtn.addActionListener(e -> {
            tabbedPane.setComponentAt(1, createCurrentVehiclesPanel());
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(whiteGreyColor);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRevenuePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(whiteGreyColor);

        JPanel revenueCard = new JPanel(new BorderLayout(10, 10));
        revenueCard.setBackground(Color.WHITE);
        revenueCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(greenColor, 3),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        JLabel revenueLabel = new JLabel("Total Revenue Collected");
        revenueLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        revenueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel revenueAmount = new JLabel(String.format("RM %.2f", parkingController.getTotalRevenue()));
        revenueAmount.setFont(new Font("SansSerif", Font.BOLD, 48));
        revenueAmount.setForeground(greenColor);
        revenueAmount.setHorizontalAlignment(SwingConstants.CENTER);

        revenueCard.add(revenueLabel, BorderLayout.NORTH);
        revenueCard.add(revenueAmount, BorderLayout.CENTER);

        panel.add(revenueCard, BorderLayout.NORTH);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(contentFont);
        refreshBtn.addActionListener(e -> {
            tabbedPane.setComponentAt(2, createRevenuePanel());
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(whiteGreyColor);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFinePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(whiteGreyColor);

        parkinglot lot = parkinglot.getInstance();
        JPanel schemePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        schemePanel.setBackground(Color.WHITE);
        schemePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel schemeLabel = new JLabel("Current Fine Scheme: ");
        schemeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        JLabel currentScheme = new JLabel(lot.getFineScheme().toUpperCase());
        currentScheme.setFont(new Font("SansSerif", Font.PLAIN, 16));
        currentScheme.setForeground(blueColor);

        schemePanel.add(schemeLabel);
        schemePanel.add(currentScheme);

        panel.add(schemePanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(whiteGreyColor);

        JLabel tableTitle = new JLabel("Unpaid Fines");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        String[] columns = {"License Plate", "Fine Amount", "Reason"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFont(contentFont);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        List<finerecord> unpaidFines = parkingController.getAllUnpaidFines();
        double totalUnpaid = 0.0;

        for (finerecord fine : unpaidFines) {
            model.addRow(new Object[]{
                fine.getLicensePlate(),
                String.format("RM %.2f", fine.getAmount()),
                fine.getReason()
            });
            totalUnpaid += fine.getAmount();
        }

        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        JLabel totalLabel = new JLabel(String.format("Total Unpaid Fines: RM %.2f", totalUnpaid));
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalLabel.setForeground(redColor);
        tablePanel.add(totalLabel, BorderLayout.SOUTH);

        panel.add(tablePanel, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(contentFont);
        refreshBtn.addActionListener(e -> {
            tabbedPane.setComponentAt(3, createFinePanel());
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(whiteGreyColor);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(whiteGreyColor);

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBackground(Color.WHITE);
        settingsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel schemeTitleLabel = new JLabel("Fine Scheme Configuration");
        schemeTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        schemeTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel schemeInfoLabel = new JLabel("Select the fine calculation scheme:");
        schemeInfoLabel.setFont(contentFont);
        schemeInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel schemeButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        schemeButtonPanel.setBackground(Color.WHITE);
        schemeButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ButtonGroup schemeGroup = new ButtonGroup();

        JRadioButton fixedScheme = new JRadioButton("Fixed Fine (RM 50)");
        JRadioButton progressiveScheme = new JRadioButton("Progressive Fine");
        JRadioButton hourlyScheme = new JRadioButton("Hourly Fine (RM 20/hour)");

        fixedScheme.setFont(contentFont);
        progressiveScheme.setFont(contentFont);
        hourlyScheme.setFont(contentFont);

        fixedScheme.setBackground(Color.WHITE);
        progressiveScheme.setBackground(Color.WHITE);
        hourlyScheme.setBackground(Color.WHITE);

        schemeGroup.add(fixedScheme);
        schemeGroup.add(progressiveScheme);
        schemeGroup.add(hourlyScheme);

        parkinglot lot = parkinglot.getInstance();
        String currentSchemeName = lot.getFineScheme();
        if (currentSchemeName.equalsIgnoreCase("fixed")) {
            fixedScheme.setSelected(true);
        } else if (currentSchemeName.equalsIgnoreCase("progressive")) {
            progressiveScheme.setSelected(true);
        } else if (currentSchemeName.equalsIgnoreCase("hourly")) {
            hourlyScheme.setSelected(true);
        }

        schemeButtonPanel.add(fixedScheme);
        schemeButtonPanel.add(progressiveScheme);
        schemeButtonPanel.add(hourlyScheme);

        JButton saveSchemeBtn = new JButton("Save Fine Scheme");
        saveSchemeBtn.setFont(contentFont);
        saveSchemeBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        saveSchemeBtn.addActionListener(e -> {
            if (fixedScheme.isSelected()) {
                lot.setFineScheme("fixed");
            } else if (progressiveScheme.isSelected()) {
                lot.setFineScheme("progressive");
            } else if (hourlyScheme.isSelected()) {
                lot.setFineScheme("hourly");
            }
            JOptionPane.showMessageDialog(this, 
                "Fine scheme updated successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        settingsPanel.add(schemeTitleLabel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        settingsPanel.add(schemeInfoLabel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        settingsPanel.add(schemeButtonPanel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        settingsPanel.add(saveSchemeBtn);

        panel.add(settingsPanel, BorderLayout.NORTH);

        return panel;
    }
}