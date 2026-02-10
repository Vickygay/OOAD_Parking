package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import controllers.ParkingController;
import models.VehicleRecord;
import models.ParkingLot;
import models.ParkingSpot;

public class CurrentVehiclesPanel extends JPanel {
    private ParkingController parkingController;
    private Color blueColor = new Color(3, 78, 161);
    private Color whiteGreyColor = new Color(238, 241, 241);
    private Color white = Color.WHITE;
    private Font contentFont = new Font("SansSerif", Font.PLAIN, 14);

    public CurrentVehiclesPanel(ParkingController controller) {
        this.parkingController = controller;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(whiteGreyColor);
        buildPanel();
    }

    private void buildPanel() {
        this.removeAll();

        // 1. creatinf table model
        String[] columns = {"License Plate", "Customer Name", "Vehicle Type", "Spot ID", "Spot Type", "Entry Time", "Hours Parked"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // 2. setup the table UI
        JTable table = new JTable(model);
        table.setFont(contentFont);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        // 3. Fetch Data from Controller and ParkingLot
        List<VehicleRecord> activeVehicles = parkingController.getAllParkedVehicles();
        ParkingLot lot = ParkingLot.getInstance();

        // 4. Fill the table using loop logic
        for (VehicleRecord vehicle : activeVehicles) {
            long hoursSoFar = parkingController.calculateHours(vehicle.getEntryTime());
            ParkingSpot spot = lot.findSpotByID(vehicle.getSpotID());
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

        // 5. Build the UI layout
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JLabel summary = new JLabel("Total vehicles currently parked: " + activeVehicles.size());
        summary.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(summary, BorderLayout.NORTH);

        // 6. Refresh buttom 
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        refreshBtn.setBackground(blueColor);
        refreshBtn.setForeground(white);

        refreshBtn.addActionListener(e -> {
            buildPanel();
            revalidate();
            repaint();
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(whiteGreyColor);
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}