package views;

import javax.swing.*;
import java.awt.*;
import controllers.LoginController;

/**
 * AdminLogin.java
 * 
 * PURPOSE:
 * - Provides login interface for administrators
 * - Validates admin credentials against users.txt
 * - Redirects to AdminPanel upon successful login
 * 
 * FEATURES:
 * - Username and password input fields
 * - Login validation through logincontroller
 * - Back button to return to main dashboard
 * - Clear fields on failed login attempt
 */
public class AdminLogin extends JFrame {
    private JTextField userIDField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private LoginController controller;
    private Font contentFont = new Font("SansSerif", Font.BOLD, 20);

    public AdminLogin() {
        this.controller = new LoginController();

        setTitle("Admin Login");
        setSize(1300, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        add(panel, BorderLayout.NORTH);

        JLabel userIDLabel = new JLabel("Admin ID:");
        userIDField = new JTextField(20);
        userIDField.setPreferredSize(new Dimension(500, 40));
        userIDLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        userIDField.setFont(new Font("Arial", Font.PLAIN, 16));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(userIDLabel, gbc);

        gbc.gridx = 1;
        panel.add(userIDField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(500, 40));
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(loginButton, gbc);

        getRootPane().setDefaultButton(loginButton);

        loginButton.addActionListener(e -> {
            String userID = userIDField.getText();
            String password = new String(passwordField.getPassword());

            boolean loginSuccess = controller.validateLogin(userID, password);

            if (loginSuccess) {
                JOptionPane.showMessageDialog(null, "Login Successful!");
                new AdminPanel(userID).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!",
                        "Login Error", JOptionPane.ERROR_MESSAGE);
                clearFields();
            }
        });

        JButton back = new JButton("Back to Dashboard");
        back.setFont(contentFont);
        back.addActionListener(e -> {
            new Dashboard().setVisible(true);
            dispose();
        });

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(back, gbc);
    }

    public void clearFields() {
        userIDField.setText("");
        passwordField.setText("");
    }
}