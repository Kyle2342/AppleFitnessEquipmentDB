package com.applefitnessequipment.ui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.applefitnessequipment.db.DBConnection;

public class DatabaseManagementApp extends JFrame {
    private JTabbedPane tabbedPane;

    public DatabaseManagementApp() {
        super("Apple Fitness Equipment - Database Management System");
        initializeUI();
        testDatabaseConnection();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen
        setLayout(new BorderLayout());

        // Create modern header panel with styling
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new java.awt.Color(41, 128, 185)); // Modern blue
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel titleLabel = new JLabel("Apple Fitness Equipment");
        titleLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28));
        titleLabel.setForeground(java.awt.Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Create tabbed pane with bigger tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        
        // Add tabs for each entity
        tabbedPane.addTab("Clients", new ClientsPanel());
        tabbedPane.addTab("Client Locations", new ClientLocationsPanel());
        tabbedPane.addTab("Employees", new EmployeesPanel());
        tabbedPane.addTab("Employee Time Logs", new EmployeeTimeLogsPanel());
        tabbedPane.addTab("Invoices", new InvoicesPanel());
        tabbedPane.addTab("Preventive Maintenance", new PreventiveMaintenancePanel());
        tabbedPane.addTab("Equipment Quotes", new EquipmentQuotesPanel());
        
        // Info tab
        JPanel infoPanel = createInfoPanel();
        tabbedPane.addTab("About", infoPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Create footer panel
        JPanel footerPanel = new JPanel();
        footerPanel.add(new JLabel("© 2025 Apple Fitness Equipment"));
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        String info = "<html><body style='width: 600px; padding: 20px;'>" +
            "<h1>Apple Fitness Equipment Database Management System</h1>" +
            "<h2>Version 1.0</h2>" +
            "<p>This application provides a comprehensive interface for managing the Apple Fitness Equipment database.</p>" +
            "<h3>Features:</h3>" +
            "<ul>" +
            "<li><b>Client Management:</b> Add, edit, delete, and search for individual and business clients</li>" +
            "<li><b>Location Management:</b> Manage billing and job locations for clients</li>" +
            "<li><b>Employee Management:</b> Track employee information, positions, and pay rates</li>" +
            "</ul>" +
            "<h3>Database Tables:</h3>" +
            "<ul>" +
            "<li>clients - Customer information</li>" +
            "<li>clientslocations - Billing and job site addresses</li>" +
            "<li>employees - Staff member details</li>" +
            "<li>employeestimelogs - Time tracking for employees</li>" +
            "<li>equipmentquotes - Sales quotes for equipment</li>" +
            "<li>equipmentquotesitems - Line items for quotes</li>" +
            "<li>preventivemaintenanceagreements - Service contracts</li>" +
            "<li>preventivemaintenanceagreementsequipments - Equipment covered by agreements</li>" +
            "<li>invoices - Billing records</li>" +
            "<li>invoicesitems - Invoice line items</li>" +
            "<li>company - Company information</li>" +
            "</ul>" +
            "<h3>Usage Instructions:</h3>" +
            "<p><b>Navigation:</b> Use the tabs at the top to switch between different data management sections.</p>" +
            "<p><b>Adding Records:</b> Fill in the form fields on the right and click 'Add'.</p>" +
            "<p><b>Editing Records:</b> Select a row from the table, modify the form fields, and click 'Update'.</p>" +
            "<p><b>Deleting Records:</b> Select a row from the table and click 'Delete'. You will be asked to confirm.</p>" +
            "<p><b>Searching:</b> Use the search field to filter records. Click 'Refresh All' to show all records again.</p>" +
            "<p><b>Required Fields:</b> Fields marked with an asterisk (*) are required.</p>" +
            "<h3>Database Connection:</h3>" +
            "<p>Ensure your MySQL database is running and properly configured in DBConnection.java</p>" +
            "<p>Default connection: localhost:3306/applefitnessequipmentdb</p>" +
            "</body></html>";
        
        JLabel infoLabel = new JLabel(info);
        panel.add(infoLabel, BorderLayout.CENTER);
        
        return panel;
    }

    private void testDatabaseConnection() {
        try {
            DBConnection.getConnection();
            // Connection successful
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Failed to connect to the database.\n" +
                "Please ensure MySQL is running and the database is properly configured.\n\n" +
                "Error: " + e.getMessage(),
                "Database Connection Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create and show the application
        SwingUtilities.invokeLater(() -> {
            DatabaseManagementApp app = new DatabaseManagementApp();
            app.setVisible(true);
        });
    }
}
