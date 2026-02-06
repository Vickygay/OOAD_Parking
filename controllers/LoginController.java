package controllers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LoginController {

    public LoginController() {
    }

    // Returns the ROLE if login is successful, or NULL if failed
    public boolean validateLogin(String userID, String password) {
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(","); 
                
                // Check if line has enough data (ID, Pass)
                if (data.length >= 2) {
                    String fUser = data[0].trim();
                    String fPass = data[1].trim();
                    
                    // Check Credentials
                    if (fUser.equals(userID) && fPass.equals(password)) {
                        return true; // Success
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading users.txt file.");
        }
        return false; // Failure: User not found or password mismatch
    }
}