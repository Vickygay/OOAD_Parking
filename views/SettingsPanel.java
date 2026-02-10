// Option A: Fixed Fine Scheme (CHEAPEST ONE) (LESS REVENUE)
// Logic: A flat fine of RM 50.00 is charged if a vehicle stays longer than 24 hours.
// Code Implementation: When Fixed Fine is selected, the system sets the internal state to "fixed". 
//  In the ParkingController, this triggers a simple check: if hours > 24, the return value is 50.0.

// Option B: Progressive Fine Scheme
// Logic: This tiered system increases the penalty based on the duration of overstay:
// 24 to 48 hours: RM 50.00.
// 48 to 72 hours: RM 150.00.
// Above 72 hours: RM 300.00.
// Code Implementation: The ParkingController uses a switch or if-else block to check the duration against these specific time milestones when the "progressive" scheme is active.

// Option C: Hourly Fine Scheme MOST EXPENSIVE ONE (MOST REVENUE)
// Logic: Charges RM 20.00 for every hour exceeded beyond the initial 24-hour period.
// Code Implementation: The calculation used in the controller is (hours - 24) * 20.0.

package views;

import javax.swing.*;
import java.awt.*;
import models.ParkingLot;

public class SettingsPanel extends JPanel {
    private Color whiteGreyColor = new Color(238, 241, 241);
    private Color blueColor = new Color(3, 78, 161);
    private Font contentFont = new Font("SansSerif", Font.BOLD, 16);

    public SettingsPanel() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(whiteGreyColor);

        JPanel settingsContainer = new JPanel();
        settingsContainer.setLayout(new BoxLayout(settingsContainer, BoxLayout.Y_AXIS));
        settingsContainer.setBackground(Color.WHITE);
        settingsContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel("Fine Scheme Configuration");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel infoLabel = new JLabel("Select the fine calculation scheme (applies to future entries only):");
        infoLabel.setFont(contentFont);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel schemeButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        schemeButtonPanel.setBackground(Color.WHITE);
        schemeButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ButtonGroup schemeGroup = new ButtonGroup();
        JRadioButton fixedScheme = new JRadioButton("Fixed Fine (RM 50)");
        JRadioButton progressiveScheme = new JRadioButton("Progressive Fine");
        JRadioButton hourlyScheme = new JRadioButton("Hourly Fine (RM 20/hour)");

        // Styling
        fixedScheme.setFont(contentFont);
        progressiveScheme.setFont(contentFont);
        hourlyScheme.setFont(contentFont);
        fixedScheme.setBackground(Color.WHITE);
        progressiveScheme.setBackground(Color.WHITE);
        hourlyScheme.setBackground(Color.WHITE);

        schemeGroup.add(fixedScheme);
        schemeGroup.add(progressiveScheme);
        schemeGroup.add(hourlyScheme);

        ParkingLot lot = ParkingLot.getInstance();
        String currentSavedScheme = lot.getFineScheme();
        
        if (currentSavedScheme.equalsIgnoreCase("progressive")) {
            progressiveScheme.setSelected(true);
        } else if (currentSavedScheme.equalsIgnoreCase("hourly")) {
            hourlyScheme.setSelected(true);
        } else {
            fixedScheme.setSelected(true); // default to fixed if unknown
        }

        schemeButtonPanel.add(fixedScheme);
        schemeButtonPanel.add(progressiveScheme);
        schemeButtonPanel.add(hourlyScheme);

        JButton saveBtn = new JButton("Save Fine Scheme");
        saveBtn.setFont(contentFont);
        saveBtn.setBackground(blueColor);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        saveBtn.addActionListener(e -> {
            if (fixedScheme.isSelected()) {
                lot.setFineScheme("fixed");
            } else if (progressiveScheme.isSelected()) {
                lot.setFineScheme("progressive");
            } else if (hourlyScheme.isSelected()) {
                lot.setFineScheme("hourly");
            }
            
            JOptionPane.showMessageDialog(this, 
                "Fine scheme updated successfully!\nCurrent Scheme: " + lot.getFineScheme().toUpperCase(), 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        settingsContainer.add(titleLabel);
        settingsContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        settingsContainer.add(infoLabel);
        settingsContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        settingsContainer.add(schemeButtonPanel);
        settingsContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        settingsContainer.add(saveBtn);

        add(settingsContainer, BorderLayout.NORTH);
    }
}