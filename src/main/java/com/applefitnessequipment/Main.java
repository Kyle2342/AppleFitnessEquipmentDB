package com.applefitnessequipment;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.applefitnessequipment.ui.DatabaseManagementApp;

/**
 * Main entry point for the Apple Fitness Equipment Database Management System.
 * 
 * Simply run this file to start the application.
 * 
 * Database Configuration:
 * - URL: jdbc:mysql://localhost:3306/applefitnessequipmentdb?useSSL=false&serverTimezone=UTC
 * - User: root
 * - Password: sTILLsINK8678
 * 
 * Make sure MySQL is running and the database is set up before starting the application.
 */
public class Main {
    public static void main(String[] args) {
        // Set the look and feel to match the system
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }
        
        // Create and display the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseManagementApp app = new DatabaseManagementApp();
                app.setVisible(true);
                app.setLocationRelativeTo(null); 
                app.setSize(1920, 1080);
                System.out.println("Apple Fitness Equipment Database Management System started successfully!");
                System.out.println("Database: applefitnessequipmentdb");
                System.out.println("User: root");
            } catch (Exception e) {
                System.err.println("Error starting application: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}