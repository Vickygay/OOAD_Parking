package views;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import controllers.LoginController;

public class adminLogin extends JFrame {
    private JTextField userIDField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private LoginController controller; // Add Controller variable
    private Font contentFont = new Font("SansSerif", 1, 20);

    public adminLogin() {
        this.controller = new LoginController(); // Initialize Controller

        setTitle("Login System");
        setSize(1300, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // User ID field
        JLabel userIDLabel = new JLabel("Admin ID:");
        userIDField = new JTextField(20);
        userIDField.setPreferredSize(new Dimension(500, 40));
        userIDLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        userIDField.setFont(new Font("Arial", Font.PLAIN, 16));

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 5, 10);
        panel.add(userIDLabel, gbc);

        gbc.gridx = 1;
        panel.add(userIDField, gbc);

        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(500, 40));
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.insets = new Insets(5, 10, 5, 10);
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Login button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(loginButton, gbc);

        getRootPane().setDefaultButton(loginButton);
        add(panel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userID = userIDField.getText();
                String password = new String(passwordField.getPassword());
                
                boolean loginSuccess = controller.validateLogin(userID, password);

                if (loginSuccess) {
                    JOptionPane.showMessageDialog(null, "Login Successful! ");
                    
                } else {
                    showErrorMessage("Invalid credentials or user does not exist!");
                    clearFields();
                }
            }
        });

        JButton back = new JButton("Back to dashboard");
        back.setFont(contentFont);

        back.addActionListener(e -> {

            new dashboard().setVisible(true); //go back to dashboard
            dispose(); //close login
        }
        );

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(back, gbc);


    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Login Error", JOptionPane.ERROR_MESSAGE);
    }

    public void clearFields() {
        userIDField.setText("");
        passwordField.setText("");
    }
}