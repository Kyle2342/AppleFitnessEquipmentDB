package com.applefitnessequipment.ui;

import java.awt.BorderLayout;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
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
        setLayout(new BorderLayout());

        // Set to maximized state
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Also set bounds to screen size as fallback
        java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        java.awt.Rectangle bounds = ge.getMaximumWindowBounds();
        setBounds(bounds);

        // Create header panel with Apple Fitness branding
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(java.awt.Color.WHITE); // White background for logo visibility
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(204, 34, 41)), // Red bottom border
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Use styled text to match the logo
        JPanel logoTextPanel = new JPanel();
        logoTextPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
        logoTextPanel.setOpaque(false);

        // Apple icon from PNG - scale to 50px height
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/img/apple_fitness_logo_3.png"));
            int newHeight = 50;
            int newWidth = (originalIcon.getIconWidth() * newHeight) / originalIcon.getIconHeight();
            Image scaledImage = originalIcon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(scaledImage));
            logoTextPanel.add(iconLabel);
        } catch (Exception e) {
            JLabel iconLabel = new JLabel("\u2764");
            iconLabel.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 40));
            iconLabel.setForeground(new java.awt.Color(204, 34, 41));
            logoTextPanel.add(iconLabel);
        }

        logoTextPanel.add(javax.swing.Box.createHorizontalStrut(10));

        // APPLE text
        JLabel appleLabel = new JLabel("APPLE");
        appleLabel.setFont(new java.awt.Font("Times New Roman", java.awt.Font.BOLD, 42));
        appleLabel.setForeground(new java.awt.Color(204, 34, 41));
        logoTextPanel.add(appleLabel);

        logoTextPanel.add(javax.swing.Box.createHorizontalStrut(15));

        // FITNESS EQUIPMENT text
        JLabel fitnessLabel = new JLabel("FITNESS EQUIPMENT");
        fitnessLabel.setFont(new java.awt.Font("Times New Roman", java.awt.Font.PLAIN, 24));
        fitnessLabel.setForeground(new java.awt.Color(0, 0, 0));
        logoTextPanel.add(fitnessLabel);

        headerPanel.add(logoTextPanel);

        add(headerPanel, BorderLayout.NORTH);

        // Create tabbed pane with modern styling
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 15));
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setBackground(java.awt.Color.WHITE);
        tabbedPane.setOpaque(true);

        // Set UI properties for modern look with bigger tabs and more padding
        javax.swing.UIManager.put("TabbedPane.selected", java.awt.Color.WHITE);
        javax.swing.UIManager.put("TabbedPane.contentAreaColor", java.awt.Color.WHITE);
        javax.swing.UIManager.put("TabbedPane.tabAreaBackground", java.awt.Color.WHITE);
        javax.swing.UIManager.put("TabbedPane.unselectedBackground", new java.awt.Color(245, 245, 245));
        javax.swing.UIManager.put("TabbedPane.tabInsets", new java.awt.Insets(12, 20, 12, 20));
        javax.swing.UIManager.put("TabbedPane.selectedTabPadInsets", new java.awt.Insets(0, 0, 0, 0));
        javax.swing.UIManager.put("TabbedPane.tabAreaInsets", new java.awt.Insets(8, 8, 0, 8));

        // Apply custom UI for rounded corners
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                tabInsets = new java.awt.Insets(12, 20, 12, 20);
                selectedTabPadInsets = new java.awt.Insets(0, 0, 0, 0);
                tabAreaInsets = new java.awt.Insets(8, 8, 0, 8);
                contentBorderInsets = new java.awt.Insets(0, 0, 0, 0);
            }

            @Override
            protected void paintTabBorder(java.awt.Graphics g, int tabPlacement, int tabIndex,
                    int x, int y, int w, int h, boolean isSelected) {
                // Don't paint default border - we'll use rounded corners
            }

            @Override
            protected void paintTabBackground(java.awt.Graphics g, int tabPlacement, int tabIndex,
                    int x, int y, int w, int h, boolean isSelected) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 12;
                if (isSelected) {
                    g2.setColor(java.awt.Color.WHITE);
                    g2.fillRoundRect(x + 2, y + 2, w - 4, h - 2, arc, arc);
                    // Add subtle shadow for selected tab
                    g2.setColor(new java.awt.Color(204, 34, 41, 40));
                    g2.drawRoundRect(x + 2, y + 2, w - 4, h - 2, arc, arc);
                } else {
                    g2.setColor(new java.awt.Color(245, 245, 245));
                    g2.fillRoundRect(x + 2, y + 2, w - 4, h - 4, arc, arc);
                }
                g2.dispose();
            }

            @Override
            protected void paintContentBorder(java.awt.Graphics g, int tabPlacement, int selectedIndex) {
                // Paint a simple top border line
                g.setColor(new java.awt.Color(230, 230, 230));
                java.awt.Rectangle bounds = tabPane.getBounds();
                int y = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                g.drawLine(0, y, bounds.width, y);
            }

            @Override
            protected void paintFocusIndicator(java.awt.Graphics g, int tabPlacement,
                    java.awt.Rectangle[] rects, int tabIndex, java.awt.Rectangle iconRect,
                    java.awt.Rectangle textRect, boolean isSelected) {
                // Don't paint focus indicator
            }
        });

        // Use dark text on all tabs, highlight selected with red and underline effect
        tabbedPane.addChangeListener(e -> {
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                if (i == tabbedPane.getSelectedIndex()) {
                    // Selected tab: red text, bold
                    tabbedPane.setForegroundAt(i, new java.awt.Color(204, 34, 41));
                } else {
                    // Unselected tabs: dark gray text
                    tabbedPane.setForegroundAt(i, new java.awt.Color(100, 100, 100));
                }
            }
        });

        // Add more spacing around tabs
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 15));

        // Add Dashboard as default first tab
        tabbedPane.addTab("Dashboard", new DashboardPanel(tabbedPane));

        // Create Clients category with nested tabs
        JTabbedPane clientsTabs = createSubTabbedPane();
        ClientsPanel clientsPanel = new ClientsPanel();
        ClientLocationsPanel clientLocationsPanel = new ClientLocationsPanel();
        clientsTabs.addTab("Clients", clientsPanel);
        clientsTabs.addTab("Client Locations", clientLocationsPanel);
        tabbedPane.addTab("Clients", clientsTabs);

        // Create Employees category with nested tabs
        JTabbedPane employeesTabs = createSubTabbedPane();
        EmployeesPanel employeesPanel = new EmployeesPanel();
        EmployeeTimeLogsPanel timeLogsPanel = new EmployeeTimeLogsPanel();
        employeesTabs.addTab("Employees", employeesPanel);
        employeesTabs.addTab("Time Logs", timeLogsPanel);
        tabbedPane.addTab("Employees", employeesTabs);

        // Create Sales & Service category with nested tabs
        JTabbedPane salesServiceTabs = createSubTabbedPane();
        InvoicesPanel invoicesPanel = new InvoicesPanel();
        EquipmentQuotesPanel quotesPanel = new EquipmentQuotesPanel();
        PreventiveMaintenancePanel pmPanel = new PreventiveMaintenancePanel();
        salesServiceTabs.addTab("Invoices", invoicesPanel);
        salesServiceTabs.addTab("Equipment Quotes", quotesPanel);
        salesServiceTabs.addTab("Preventive Maintenance", pmPanel);
        tabbedPane.addTab("Sales & Service", salesServiceTabs);

        // Add change listeners to refresh data when tabs are selected
        clientsTabs.addChangeListener(e -> {
            int index = clientsTabs.getSelectedIndex();
            if (index == 0) clientsPanel.refreshData();
            else if (index == 1) clientLocationsPanel.refreshData();
        });

        employeesTabs.addChangeListener(e -> {
            int index = employeesTabs.getSelectedIndex();
            if (index == 0) employeesPanel.refreshData();
            else if (index == 1) timeLogsPanel.refreshData();
        });

        salesServiceTabs.addChangeListener(e -> {
            int index = salesServiceTabs.getSelectedIndex();
            if (index == 0) invoicesPanel.refreshData();
            else if (index == 1) quotesPanel.refreshData();
            else if (index == 2) pmPanel.refreshData();
        });

        // Refresh when main tab changes
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            if (index == 1) { // Clients tab
                int subIndex = clientsTabs.getSelectedIndex();
                if (subIndex == 0) clientsPanel.refreshData();
                else if (subIndex == 1) clientLocationsPanel.refreshData();
            } else if (index == 2) { // Employees tab
                int subIndex = employeesTabs.getSelectedIndex();
                if (subIndex == 0) employeesPanel.refreshData();
                else if (subIndex == 1) timeLogsPanel.refreshData();
            } else if (index == 3) { // Sales & Service tab
                int subIndex = salesServiceTabs.getSelectedIndex();
                if (subIndex == 0) invoicesPanel.refreshData();
                else if (subIndex == 1) quotesPanel.refreshData();
                else if (subIndex == 2) pmPanel.refreshData();
            }
        });

        // Info tab
        JPanel infoPanel = createInfoPanel();
        tabbedPane.addTab("About", infoPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Create footer panel
        JPanel footerPanel = new JPanel();
        footerPanel.add(new JLabel("© 2025 Apple Fitness Equipment"));
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JTabbedPane createSubTabbedPane() {
        JTabbedPane subTabs = new JTabbedPane();
        subTabs.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        subTabs.setBackground(java.awt.Color.WHITE);
        subTabs.setOpaque(true);
        subTabs.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));

        // Style the sub-tabs with simpler look
        subTabs.addChangeListener(e -> {
            for (int i = 0; i < subTabs.getTabCount(); i++) {
                if (i == subTabs.getSelectedIndex()) {
                    subTabs.setForegroundAt(i, new java.awt.Color(204, 34, 41));
                } else {
                    subTabs.setForegroundAt(i, new java.awt.Color(100, 100, 100));
                }
            }
        });

        return subTabs;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new java.awt.GridBagLayout());
        panel.setBackground(java.awt.Color.WHITE);

        // Create content panel that will be centered
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new javax.swing.BoxLayout(contentPanel, javax.swing.BoxLayout.Y_AXIS));
        contentPanel.setBackground(java.awt.Color.WHITE);

        // Logo at top, centered
        JPanel logoPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
        logoPanel.setOpaque(false);
        logoPanel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/img/apple_fitness_logo_3.png"));
            int newHeight = 100;
            int newWidth = (originalIcon.getIconWidth() * newHeight) / originalIcon.getIconHeight();
            Image scaledImage = originalIcon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
            logoPanel.add(logoLabel);
        } catch (Exception e) {
            // Fallback - no logo
        }
        contentPanel.add(logoPanel);

        String info = "<html><body style='font-family: Segoe UI; font-size: 13px; text-align: center; width: 700px;'>" +
            "<h1 style='color: #CC2229; font-size: 26px; margin: 10px 0 5px 0;'>Apple Fitness Equipment</h1>" +
            "<h2 style='color: #333; font-size: 18px; font-weight: normal; margin: 0 0 5px 0;'>Database Management System</h2>" +
            "<p style='color: #666; font-size: 14px; margin: 0 0 15px 0;'>Version 1.0</p>" +
            "<hr style='border: 1px solid #eee; margin: 15px 0;'>" +
            "<p style='font-size: 13px; margin-bottom: 15px;'>This application provides a comprehensive interface for managing the Apple Fitness Equipment database.</p>" +
            "<table style='width: 100%; text-align: left;'><tr><td style='vertical-align: top; padding-right: 20px;'>" +
            "<h3 style='color: #CC2229; font-size: 15px; margin: 10px 0;'>Features:</h3>" +
            "<ul style='font-size: 12px; line-height: 1.6; margin: 0; padding-left: 20px;'>" +
            "<li><b>Client Management:</b> Add, edit, delete, and search clients</li>" +
            "<li><b>Location Management:</b> Manage billing and job locations</li>" +
            "<li><b>Employee Management:</b> Track employee info and pay rates</li>" +
            "<li><b>Time Tracking:</b> Log work hours, mileage, and PTO</li>" +
            "<li><b>Invoicing:</b> Create and manage invoices</li>" +
            "<li><b>Preventive Maintenance:</b> Service agreements</li>" +
            "<li><b>Equipment Quotes:</b> Generate sales quotes</li>" +
            "</ul>" +
            "</td><td style='vertical-align: top;'>" +
            "<h3 style='color: #CC2229; font-size: 15px; margin: 10px 0;'>Usage Instructions:</h3>" +
            "<ul style='font-size: 12px; line-height: 1.6; margin: 0; padding-left: 20px;'>" +
            "<li><b>Navigation:</b> Use tabs to switch sections</li>" +
            "<li><b>Adding:</b> Fill form fields, click 'Add'</li>" +
            "<li><b>Editing:</b> Select row, modify, click 'Update'</li>" +
            "<li><b>Deleting:</b> Select row, click 'Delete'</li>" +
            "<li><b>Searching:</b> Type to filter automatically</li>" +
            "<li><b>Required:</b> Fields with (*) are required</li>" +
            "</ul>" +
            "</td></tr></table>" +
            "<p style='font-size: 11px; color: #666; margin-top: 15px;'>Database: localhost:3306/applefitnessequipmentdb</p>" +
            "</body></html>";

        JLabel infoLabel = new JLabel(info);
        infoLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        contentPanel.add(infoLabel);

        // Wrap in scroll pane to handle overflow
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Use GridBagConstraints to center content both horizontally and vertically
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = java.awt.GridBagConstraints.CENTER;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        panel.add(contentPanel, gbc);

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
            // Maximize after visible to ensure it works on Windows
            app.setExtendedState(JFrame.MAXIMIZED_BOTH);
        });
    }
}
