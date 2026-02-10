package controllers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class logincontroller {
    private static final String ADMIN_FILE = "users.txt";

    public boolean validateLogin(String userID, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(ADMIN_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] credentials = line.split(",");
                if (credentials.length >= 2) {
                    String storedUserID = credentials[0].trim();
                    String storedPassword = credentials[1].trim();
                    
                    if (storedUserID.equals(userID) && storedPassword.equals(password)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading admin file: " + e.getMessage());
        }
        
        return false;
    }
}