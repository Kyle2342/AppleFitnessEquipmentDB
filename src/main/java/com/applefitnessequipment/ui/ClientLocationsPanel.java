package com.applefitnessequipment.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.applefitnessequipment.dao.ClientDAO;
import com.applefitnessequipment.dao.ClientLocationDAO;
import com.applefitnessequipment.model.Client;
import com.applefitnessequipment.model.ClientLocation;

public class ClientLocationsPanel extends JPanel {
    private ClientLocationDAO locationDAO;
    private ClientDAO clientDAO;
    private JTable locationsTable;
    private DefaultTableModel tableModel;
    private JTextField clientSearchField;  // For searching clients by name
    private JComboBox<String> locationTypeFilterCombo;  // For filtering by type
    private JComboBox<Client> formClientCombo;  // For form
    private JComboBox<String> locationTypeCombo;
    private JTextField streetAddressField;
    private JTextField buildingField, roomField;
    private JTextField cityField, stateField, zipField, countryField;
    private JTextField phoneField, faxField, emailField;
    private JButton addButton, updateButton, deleteButton, clearButton;
    private ClientLocation selectedLocation;
    private List<ClientLocation> allLocations;  // Cache for filtering

    public ClientLocationsPanel() {
        locationDAO = new ClientLocationDAO();
        clientDAO = new ClientDAO();
        initComponents();
        loadClients();
        loadLocations();
        
        // Add component listener to refresh clients when tab is shown
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadClients();
            }
        });
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel - Filters (bigger and cleaner like ClientsPanel)
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        filterPanel.add(new JLabel("Search:"));
        clientSearchField = new JTextField(35);  // Match ClientsPanel size
        clientSearchField.setFont(ModernUIHelper.NORMAL_FONT);
        clientSearchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new java.awt.Color(180, 180, 180), 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        clientSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterLocations(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterLocations(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterLocations(); }
        });
        filterPanel.add(clientSearchField);

        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(new JLabel("Filter by Type:"));
        locationTypeFilterCombo = new JComboBox<>(new String[]{"Show All", "Billing", "Job"});
        locationTypeFilterCombo.setFont(ModernUIHelper.NORMAL_FONT);
        locationTypeFilterCombo.setPreferredSize(new java.awt.Dimension(120, 38));  // Make it bigger
        locationTypeFilterCombo.addActionListener(e -> filterLocations());  // Auto-filter on change
        filterPanel.add(locationTypeFilterCombo);

        add(filterPanel, BorderLayout.NORTH);

        // Center Panel - Table
        String[] columns = {"ID", "Client ID", "Type", "Address", "City", "State", "Phone"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        locationsTable = new JTable(tableModel);
        locationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Apply modern styling
        ModernUIHelper.styleTable(locationsTable);
        ModernUIHelper.addTableToggleBehavior(locationsTable, () -> clearForm());
        
        // Hide the ID columns
        locationsTable.getColumnModel().getColumn(0).setMinWidth(0);
        locationsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        locationsTable.getColumnModel().getColumn(0).setWidth(0);
        locationsTable.getColumnModel().getColumn(1).setMinWidth(0);
        locationsTable.getColumnModel().getColumn(1).setMaxWidth(0);
        locationsTable.getColumnModel().getColumn(1).setWidth(0);

        locationsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = locationsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadSelectedLocation();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(locationsTable);

        // Allow deselection by clicking on empty space in the scroll pane viewport (not on the table itself)
        scrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                // Only deselect if clicking in the viewport area (the gray empty space around the table)
                if (e.getComponent() == scrollPane && !locationsTable.getBounds().contains(e.getPoint())) {
                    locationsTable.clearSelection();
                    clearForm();
                }
            }
        });

        add(scrollPane, BorderLayout.CENTER);

        // Right Panel - Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(ModernUIHelper.createModernBorder("Client Location Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Client (for creating new locations)
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Client:*"), gbc);
        gbc.gridx = 1;
        formClientCombo = new JComboBox<>();
        ModernUIHelper.styleComboBox(formClientCombo);
        formPanel.add(formClientCombo, gbc);
        row++;

        // Location Type
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Location Type:*"), gbc);
        gbc.gridx = 1;
        locationTypeCombo = new JComboBox<>(new String[]{"Billing", "Job"});
        ModernUIHelper.styleComboBox(locationTypeCombo);
        formPanel.add(locationTypeCombo, gbc);
        row++;

        // Building
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Building Name:"), gbc);
        gbc.gridx = 1;
        buildingField = new JTextField(20);
        ModernUIHelper.styleTextField(buildingField);
        formPanel.add(buildingField, gbc);
        row++;

        // Room Number
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Room Number:"), gbc);
        gbc.gridx = 1;
        roomField = new JTextField(20);
        ModernUIHelper.styleTextField(roomField);
        formPanel.add(roomField, gbc);
        row++;

        // Street Address
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Street Address:*"), gbc);
        gbc.gridx = 1;
        streetAddressField = new JTextField(20);
        ModernUIHelper.styleTextField(streetAddressField);
        formPanel.add(streetAddressField, gbc);
        row++;

        // City
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("City:*"), gbc);
        gbc.gridx = 1;
        cityField = new JTextField(20);
        ModernUIHelper.styleTextField(cityField);
        formPanel.add(cityField, gbc);
        row++;

        // State
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("State:*"), gbc);
        gbc.gridx = 1;
        stateField = new JTextField(20);
        stateField.setText("PA");
        ModernUIHelper.styleTextField(stateField);
        formPanel.add(stateField, gbc);
        row++;

        // ZIP
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("ZIP Code:*"), gbc);
        gbc.gridx = 1;
        zipField = new JTextField(20);
        ModernUIHelper.styleTextField(zipField);
        formPanel.add(zipField, gbc);
        row++;

        // Country
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Country:*"), gbc);
        gbc.gridx = 1;
        countryField = new JTextField(20);
        countryField.setText("USA");
        ModernUIHelper.styleTextField(countryField);
        formPanel.add(countryField, gbc);
        row++;

        // Phone
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        ModernUIHelper.styleTextField(phoneField);
        formPanel.add(phoneField, gbc);
        row++;

        // Fax
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Fax:"), gbc);
        gbc.gridx = 1;
        faxField = new JTextField(20);
        ModernUIHelper.styleTextField(faxField);
        formPanel.add(faxField, gbc);
        row++;

        // Email
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        ModernUIHelper.styleTextField(emailField);
        formPanel.add(emailField, gbc);
        row++;

        // Buttons
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        addButton = new JButton("Add");
        addButton.addActionListener(e -> addLocation());
        ModernUIHelper.styleButton(addButton, "success");
        buttonPanel.add(addButton);

        updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateLocation());
        updateButton.setEnabled(false);
        ModernUIHelper.styleButton(updateButton, "warning");
        buttonPanel.add(updateButton);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteLocation());
        deleteButton.setEnabled(false);
        ModernUIHelper.styleButton(deleteButton, "danger");
        buttonPanel.add(deleteButton);

        clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearForm());
        ModernUIHelper.styleButton(clearButton, "secondary");
        buttonPanel.add(clearButton);

        formPanel.add(buttonPanel, gbc);

        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(formScrollPane, BorderLayout.EAST);
    }

    private void loadClients() {
        try {
            List<Client> clients = clientDAO.getAllClients();

            // Populate form combo
            formClientCombo.removeAllItems();
            for (Client client : clients) {
                formClientCombo.addItem(client);
            }
            formClientCombo.setSelectedIndex(-1);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading clients: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadLocations() {
        try {
            allLocations = locationDAO.getAllClientLocations();
            filterLocations();  // Apply current filters
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading locations: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterLocations() {
        if (allLocations == null) return;

        String searchText = clientSearchField.getText().toLowerCase().trim();
        String typeFilter = (String) locationTypeFilterCombo.getSelectedItem();

        tableModel.setRowCount(0);
        for (ClientLocation loc : allLocations) {
            // Apply client search filter
            boolean clientMatches = searchText.isEmpty();
            if (!clientMatches) {
                // Search in client name
                try {
                    Client client = clientDAO.getClientById(loc.getClientId());
                    if (client != null) {
                        String clientName = (client.getCompanyName() != null ? client.getCompanyName() :
                                           client.getFirstName() + " " + client.getLastName()).toLowerCase();
                        clientMatches = clientName.contains(searchText);
                    }
                } catch (SQLException e) {
                    // Skip this location if error
                }
            }

            // Apply type filter
            boolean typeMatches = "Show All".equals(typeFilter) ||
                                 typeFilter.equals(loc.getLocationType());

            if (clientMatches && typeMatches) {
                tableModel.addRow(new Object[]{
                    loc.getClientLocationId(),
                    loc.getClientId(),
                    loc.getLocationType(),
                    loc.getStreetAddress(),
                    loc.getCity(),
                    loc.getState(),
                    loc.getPhone()
                });
            }
        }
    }

    private void loadSelectedLocation() {
        int selectedRow = locationsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int locationId = (Integer) tableModel.getValueAt(selectedRow, 0);
            try {
                selectedLocation = locationDAO.getClientLocationById(locationId);
                if (selectedLocation != null) {
                    // Find and select the client
                    for (int i = 0; i < formClientCombo.getItemCount(); i++) {
                        Client c = formClientCombo.getItemAt(i);
                        if (c != null && c.getClientId().equals(selectedLocation.getClientId())) {
                            formClientCombo.setSelectedIndex(i);
                            break;
                        }
                    }

                    locationTypeCombo.setSelectedItem(selectedLocation.getLocationType());
                    streetAddressField.setText(selectedLocation.getStreetAddress());
                    buildingField.setText(selectedLocation.getBuildingName());
                    roomField.setText(selectedLocation.getRoomNumber());
                    cityField.setText(selectedLocation.getCity());
                    stateField.setText(selectedLocation.getState());
                    zipField.setText(selectedLocation.getZipCode());
                    countryField.setText(selectedLocation.getCountry());
                    phoneField.setText(selectedLocation.getPhone());
                    faxField.setText(selectedLocation.getFax());
                    emailField.setText(selectedLocation.getEmail());

                    updateButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error loading location details: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addLocation() {
        if (!validateForm()) return;

        ClientLocation location = new ClientLocation();
        populateLocationFromForm(location);

        try {
            if (locationDAO.addClientLocation(location)) {
                JOptionPane.showMessageDialog(this, "Location added successfully!");
                clearForm();
                loadLocations();  // Auto-refresh
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add location.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding location: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateLocation() {
        if (selectedLocation == null) return;
        if (!validateForm()) return;

        populateLocationFromForm(selectedLocation);

        try {
            if (locationDAO.updateClientLocation(selectedLocation)) {
                JOptionPane.showMessageDialog(this, "Location updated successfully!");
                clearForm();
                loadLocations();  // Auto-refresh
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update location.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating location: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteLocation() {
        if (selectedLocation == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this location?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (locationDAO.deleteClientLocation(selectedLocation.getClientLocationId())) {
                    JOptionPane.showMessageDialog(this, "Location deleted successfully!");
                    clearForm();
                    loadLocations();  // Auto-refresh
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete location.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting location: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void populateLocationFromForm(ClientLocation location) {
        Client selectedClient = (Client) formClientCombo.getSelectedItem();
        location.setClientId(selectedClient.getClientId());
        location.setLocationType((String) locationTypeCombo.getSelectedItem());
        location.setStreetAddress(streetAddressField.getText().trim());
        location.setBuildingName(buildingField.getText().trim());
        location.setRoomNumber(roomField.getText().trim());
        location.setCity(cityField.getText().trim());
        location.setState(stateField.getText().trim());
        location.setZipCode(zipField.getText().trim());
        location.setCountry(countryField.getText().trim());
        location.setPhone(phoneField.getText().trim());
        location.setFax(faxField.getText().trim());
        location.setEmail(emailField.getText().trim());
    }

    private void clearForm() {
        formClientCombo.setSelectedIndex(-1);
        locationTypeCombo.setSelectedIndex(0);
        streetAddressField.setText("");
        buildingField.setText("");
        roomField.setText("");
        cityField.setText("");
        stateField.setText("PA");
        zipField.setText("");
        countryField.setText("USA");
        phoneField.setText("");
        faxField.setText("");
        emailField.setText("");
        selectedLocation = null;
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        locationsTable.clearSelection();
    }

    private boolean validateForm() {
        if (formClientCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a client.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (streetAddressField.getText().trim().isEmpty() || cityField.getText().trim().isEmpty() ||
            stateField.getText().trim().isEmpty() || zipField.getText().trim().isEmpty() ||
            countryField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Street address, city, state, ZIP, and country are required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    public void refreshData() {
        loadClients();
        loadLocations();
    }
}
