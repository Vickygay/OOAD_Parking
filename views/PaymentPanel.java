package views;

import javax.swing.*;
import java.awt.*;
import models.VehicleRecord;
import controllers.ParkingController;

public class PaymentPanel extends JPanel {
    private ParkingController controller;
    private VehicleRecord currentVehicle;
    private double parkingFee;
    private double unpaidFines;
    private double currentSessionFines;
    private double totalDue;
    private int hoursParked;
    private JFrame parentFrame;

    public PaymentPanel(JFrame parentFrame, ParkingController controller, VehicleRecord currentVehicle, 
                       double parkingFee, double unpaidFines, double currentSessionFines, 
                       double totalDue, int hoursParked) {
        this.parentFrame = parentFrame;
        this.controller = controller;
        this.currentVehicle = currentVehicle;
        this.parkingFee = parkingFee;
        this.unpaidFines = unpaidFines;
        this.currentSessionFines = currentSessionFines;
        this.totalDue = totalDue;
        this.hoursParked = hoursParked;
        
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);
    }

    public void processPayment() {
        if (currentVehicle == null) {
            JOptionPane.showMessageDialog(this, "No vehicle selected!",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double totalFines = unpaidFines + currentSessionFines;
        boolean mustPayFines = totalFines > 500.0;
        
        String[] paymentChoices;
        
        if (mustPayFines) {
            // FORCE customer to pay all fines if the fine amount more then RM500 (no orher otptions) to prevent the fine keep on accumalating 
            JOptionPane.showMessageDialog(this,
                String.format("WARNING ⚠\n\nYour total unpaid fines (RM %.2f) exceed RM 500.\n\nYou MUST pay all dues to exit the parking lot.",
                    totalFines),
                "Payment Required",
                JOptionPane.WARNING_MESSAGE);
            
            paymentChoices = new String[]{
                "Pay All Dues (RM " + String.format("%.2f", totalDue) + ")"
            };
        } else if (totalFines > 0) {
            // optional payment if fines ≤ RM 500
            paymentChoices = new String[]{
                "Pay Parking Fee Only (RM " + String.format("%.2f", parkingFee) + ")",
                "Pay All Dues (RM " + String.format("%.2f", totalDue) + ")"
            };
        } else {
            // no fines, just parking fee
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

        // Generate and display receipt
        Receipt receipt = new Receipt(currentVehicle, controller, parkingFee, unpaidFines, 
                                     currentSessionFines, hoursParked, paymentMethod, 
                                     amountPaid, change, payFinesNow);
        receipt.display(parentFrame);
        
        // Return to dashboard
        new Dashboard().setVisible(true);
        parentFrame.dispose();
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
        JDialog processingDialog = new JDialog(parentFrame, "Processing Payment", true);
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
                String.format("Card payment successful!\n\nAmount charged: RM %.2f", amountToPay),
                "Payment Approved",
                JOptionPane.INFORMATION_MESSAGE);
        });
        timer.setRepeats(false);
        timer.start();
        
        processingDialog.setVisible(true);
    }
}