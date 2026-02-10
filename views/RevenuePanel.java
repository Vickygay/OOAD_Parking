package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import controllers.ParkingController;

public class RevenuePanel extends JPanel {
    private ParkingController parkingController;
    
    private Color blueColor = new Color(3, 78, 161);
    private Color greenColor = new Color(46, 204, 113);
    private Color redColor = new Color(231, 76, 60);
    private Color whiteGreyColor = new Color(238, 241, 241);
    private Color white = Color.WHITE;

    public RevenuePanel(ParkingController controller, JTabbedPane tabbedPane, int index) {
        this.parkingController = controller;
        setLayout(new BorderLayout());
        buildPanel();
    }

    private void buildPanel() {
        this.removeAll();
        
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(whiteGreyColor);

        // get Data from Controller
        double totalRevenue = parkingController.getTotalRevenue();
        double totalUnpaidFines = parkingController.getTotalUnpaidFines();

        // Top Summary Cards
        JPanel summaryPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        summaryPanel.setBackground(whiteGreyColor);

        // Revenue Card
        JPanel revenueCard = new JPanel(new BorderLayout(10, 10));
        revenueCard.setBackground(Color.WHITE);
        revenueCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(greenColor, 3),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        JLabel revenueLabel = new JLabel("Total Revenue Collected");
        revenueLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        revenueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel revenueAmount = new JLabel(String.format("RM %.2f", totalRevenue));
        revenueAmount.setFont(new Font("SansSerif", Font.BOLD, 42));
        revenueAmount.setForeground(greenColor);
        revenueAmount.setHorizontalAlignment(SwingConstants.CENTER);

        revenueCard.add(revenueLabel, BorderLayout.NORTH);
        revenueCard.add(revenueAmount, BorderLayout.CENTER);

        // Unpaid Card
        JPanel unpaidCard = new JPanel(new BorderLayout(10, 10));
        unpaidCard.setBackground(Color.WHITE);
        unpaidCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(redColor, 3),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        JLabel unpaidLabel = new JLabel("Total Unpaid Fines");
        unpaidLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        unpaidLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel unpaidAmount = new JLabel(String.format("RM %.2f", totalUnpaidFines));
        unpaidAmount.setFont(new Font("SansSerif", Font.BOLD, 42));
        unpaidAmount.setForeground(redColor);
        unpaidAmount.setHorizontalAlignment(SwingConstants.CENTER);

        unpaidCard.add(unpaidLabel, BorderLayout.NORTH);
        unpaidCard.add(unpaidAmount, BorderLayout.CENTER);

        summaryPanel.add(revenueCard);
        summaryPanel.add(unpaidCard);
        panel.add(summaryPanel, BorderLayout.NORTH);

        // TABLE 
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(whiteGreyColor);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JLabel tableTitle = new JLabel("Revenue Transactions");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        String[] columns = {"Date & Time", "License Plate", "Parking Fee", "Fine", "Fine Amount", "Fine Paid", "Outstanding"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14)); 
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        
        // Setting Column Widths
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(60);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);

        List<String[]> transactions = parkingController.getAllRevenueTransactionsWithFines();
        for (String[] transaction : transactions) {
            model.addRow(new Object[]{
                transaction[0], // data & time 
                transaction[1], // license plate 
                "RM " + transaction[2], // parking fee 
                transaction[3], // fine reason (if yes show reason, else show -)
                transaction[4], // fine amount (if yes show RMxxx, else show -)
                transaction[5], // fine paid status (Yes/No)
                transaction[6] // any outstanding amount (if yes show RMxxx, else show -) 
            });
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        JLabel transactionCount = new JLabel(String.format("Total Transactions: %d", transactions.size()));
        transactionCount.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tablePanel.add(transactionCount, BorderLayout.SOUTH);

        panel.add(tablePanel, BorderLayout.CENTER);

        // refresh button 
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
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }
}