package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import controllers.ParkingController; 
import models.FineRecord; 
import models.ParkingLot; 

public class FineManagementPanel extends JPanel {
    private ParkingController parkingController;
    private Color blueColor = new Color(3, 78, 161);
    private Color redColor = new Color(231, 76, 60);
    private Color whiteGreyColor = new Color(238, 241, 241);
    private Font contentFont = new Font("SansSerif", Font.BOLD, 14);

    public FineManagementPanel(ParkingController controller) {
        this.parkingController = controller;
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(whiteGreyColor);
        buildPanel();
    }

    private void buildPanel() {
        this.removeAll();

        ParkingLot lot = ParkingLot.getInstance(); 
        
        // --- Top Scheme Display ---
        JPanel schemePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        schemePanel.setBackground(Color.WHITE);
        schemePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel schemeLabel = new JLabel("Current Active Fine Scheme: ");
        schemeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        // Accessing fine scheme from ParkingLot instance
        JLabel currentScheme = new JLabel(lot.getFineScheme().toUpperCase());
        currentScheme.setFont(new Font("SansSerif", Font.PLAIN, 16));
        currentScheme.setForeground(blueColor);

        schemePanel.add(schemeLabel);
        schemePanel.add(currentScheme);
        add(schemePanel, BorderLayout.NORTH);

        // --- Table Section ---
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(whiteGreyColor);

        JLabel tableTitle = new JLabel("Outstanding Unpaid Fines");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        String[] columns = {"License Plate", "Fine Amount", "Reason"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { 
                return false; 
            }
        };

        // Corrected: Use the instance 'parkingController' to get the list
        List<FineRecord> unpaidFines = parkingController.getAllUnpaidFines();
        double totalUnpaid = 0.0;

        for (FineRecord fine : unpaidFines) {
            model.addRow(new Object[]{
                fine.getLicensePlate(),
                String.format("RM %.2f", fine.getAmount()),
                fine.getReason()
            });
            totalUnpaid += fine.getAmount();
        }

        JTable table = new JTable(model);
        table.setFont(contentFont);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        JLabel totalLabel = new JLabel(String.format("Total Outstanding Fines: RM %.2f", totalUnpaid));
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalLabel.setForeground(redColor);
        tablePanel.add(totalLabel, BorderLayout.SOUTH);

        add(tablePanel, BorderLayout.CENTER);

        // --- Refresh Button ---
        JButton refreshBtn = new JButton("Refresh Fine Data");
        refreshBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        refreshBtn.setBackground(blueColor);
        refreshBtn.setForeground(Color.WHITE);
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