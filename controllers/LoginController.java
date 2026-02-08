package controllers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class logincontroller {

    public logincontroller() {
    }

    public boolean validateLogin(String userID, String password) {
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(","); 
                
                if (data.length >= 2) {
                    String fUser = data[0].trim();
                    String fPass = data[1].trim();
                    
                    if (fUser.equals(userID) && fPass.equals(password)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading users.txt file.");
        }
        return false;
    }
}