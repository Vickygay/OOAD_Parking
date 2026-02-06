package views;
import javax.swing.*;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class entry extends JFrame{
    private Color blueColor = new Color(3, 78, 161);
    private Color redColor = new Color(255, 7, 7);
    private Color whiteGreyColor = new Color(238,241,241);
    private Font headerFont = new Font("SansSerif", Font.BOLD, 30);
    private Font contentFont = new Font("SansSerif", Font.BOLD, 20);
    
    private JTextField name;
    private JComboBox<String> vehicleType;
    private JTextField licensePlate;
    private JFormattedTextField dateField;
    private JComboBox<String> availableSpots;
    private String ticket;
    public entry()
    {
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
        gbc.gridx = 0; //left column
        gbc.gridy = 0; //first row
        gbc.gridwidth = 1; //span two column
        JLabel nameLabel = new JLabel("Enter your name: ");
        nameLabel.setFont(contentFont);
        content.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        name = new JTextField(50);
        content.add(name, gbc);

    // choose vehicle type
        gbc.gridwidth = 1;
        gbc.gridy++; //go to next row
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel vehicleTypLabel = new JLabel("Choose your vehicle type: ");
        vehicleTypLabel.setFont(contentFont);
        content.add(vehicleTypLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        String[] vehicle = {"Compact", "Regular", "Handicapped", "Reserved"};
        vehicleType = new JComboBox<>(vehicle);
        content.add(vehicleType, gbc);

    // enter car plate
        gbc.gridwidth = 1;
        gbc.gridy++; //go to next row
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
        gbc.gridy++; //go to next row
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

    // choose spot
        gbc.gridwidth = 1;
        gbc.gridy++; //go to next row
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel spotLabel = new JLabel("Choose your spot: ");
        spotLabel.setFont(contentFont);
        content.add(spotLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        String[] spots = {"F1-R1-S1", "F1-R1-S2", "F1-R1-S3", "F1-R1-S4"};
        availableSpots = new JComboBox<>(spots);
        content.add(availableSpots, gbc);

        gbc.gridwidth = 3;
        gbc.gridy++; //go to next row
        gbc.gridx = 1;

        // have two buttons here
        JButton submit = new JButton("Submit");
        JButton cancel = new JButton("Cancel"); 

        submit.setPreferredSize(new Dimension(150, 50));
        cancel.setPreferredSize(new Dimension(150, 50));

        // set font for the text in the button
        submit.setFont(contentFont);
        cancel.setFont(contentFont);

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String licensePlateText = licensePlate.getText().toUpperCase();
                String date = dateField.getText();
                ticket = "T-" + licensePlateText + "-" + date;
                JOptionPane.showMessageDialog(null, "Thank you. \n Your Ticket: \n" + ticket);
                save();
                new dashboard().setVisible(true);
                dispose();
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

    private void save() {
        String nameText = name.getText();
        String licensePlateText = licensePlate.getText().toUpperCase();
        String date = dateField.getText();
        String fileName = "parking.txt"; // The name of the file

        // Use a try-with-resources block for automatic resource management (Java 7+)
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
            bw.write(nameText + "," + (String) vehicleType.getSelectedItem() + "," + licensePlateText + "," + date + "," + (String) availableSpots.getSelectedItem());
            bw.newLine(); 
            
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
