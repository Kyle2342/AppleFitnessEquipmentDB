package com.applefitnessequipment.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.applefitnessequipment.dao.ClientDAO;
import com.applefitnessequipment.model.Client;

public class ClientsPanel extends JPanel {
    private ClientDAO clientDAO;
    private JTable clientsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JTextField firstNameField, lastNameField, companyNameField;
    private JTextField phoneField, emailField;
    private JTextArea notesArea;
    private JComboBox<String> clientTypeCombo;
    private JComboBox<String> filterTypeCombo;
    private JButton addButton, updateButton, deleteButton, clearButton;
    private Client selectedClient;
    private List<Client> allClients;
    private boolean isUpdatingPhone = false;
    private boolean isFormAutoPopulated = false;  // Track if form was populated from table selection

    public ClientsPanel() {
        clientDAO = new ClientDAO();
        initComponents();
        loadClients();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel - Search and Filter
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        topPanel.add(new JLabel("Search:"));
        searchField = new JTextField(35);  // Bigger search field
        searchField.setFont(ModernUIHelper.NORMAL_FONT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new java.awt.Color(180, 180, 180), 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        // Auto-search as user types
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filterAndSearch(); }
            public void removeUpdate(DocumentEvent e) { filterAndSearch(); }
            public void insertUpdate(DocumentEvent e) { filterAndSearch(); }
        });
        topPanel.add(searchField);
        
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(new JLabel("Filter by Type:"));
        filterTypeCombo = new JComboBox<>(new String[]{"Show All", "Individual", "Business"});
        filterTypeCombo.setFont(ModernUIHelper.NORMAL_FONT);
        filterTypeCombo.setPreferredSize(new java.awt.Dimension(120, 38));  // Make it bigger
        filterTypeCombo.addActionListener(e -> filterAndSearch());
        topPanel.add(filterTypeCombo);

        add(topPanel, BorderLayout.NORTH);

        // Center Panel - Table
        String[] columns = {"ID", "Company", "Name", "Type", "Phone", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        clientsTable = new JTable(tableModel);
        clientsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Apply modern styling
        ModernUIHelper.styleTable(clientsTable);
        ModernUIHelper.addTableToggleBehavior(clientsTable, () -> clearForm());
        
        // Hide the ID column
        clientsTable.getColumnModel().getColumn(0).setMinWidth(0);
        clientsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        clientsTable.getColumnModel().getColumn(0).setWidth(0);

        clientsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = clientsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadSelectedClient();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(clientsTable);

        // Allow deselection by clicking on empty space in the scroll pane viewport (not on the table itself)
        scrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                // Only deselect if clicking in the viewport area (the gray empty space around the table)
                if (e.getComponent() == scrollPane && !clientsTable.getBounds().contains(e.getPoint())) {
                    clientsTable.clearSelection();
                    clearForm();
                }
            }
        });

        add(scrollPane, BorderLayout.CENTER);

        // Right Panel - Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(ModernUIHelper.createModernBorder("Client Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Match invoice panel sizing/colors for inputs
        JTextField sizingField = new JTextField(20);
        ModernUIHelper.styleTextField(sizingField);
        Dimension standardInputSize = sizingField.getPreferredSize();
        java.awt.Color standardFieldBackground = sizingField.getBackground();

        int row = 0;

        // Client Type - Make dropdown bigger like text fields
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Client Type:"), gbc);
        gbc.gridx = 1;
        clientTypeCombo = new JComboBox<>(new String[]{"Individual", "Business"});
        clientTypeCombo.setFont(ModernUIHelper.NORMAL_FONT);
        clientTypeCombo.setPreferredSize(standardInputSize);
        clientTypeCombo.setBackground(standardFieldBackground);
        clientTypeCombo.setOpaque(true);
        formPanel.add(clientTypeCombo, gbc);
        row++;

        // First Name
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        firstNameField = new JTextField(20);
        ModernUIHelper.styleTextField(firstNameField);
        formPanel.add(firstNameField, gbc);
        row++;

        // Last Name
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        lastNameField = new JTextField(20);
        ModernUIHelper.styleTextField(lastNameField);
        formPanel.add(lastNameField, gbc);
        row++;

        // Company Name
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Company Name:"), gbc);
        gbc.gridx = 1;
        companyNameField = new JTextField(20);
        ModernUIHelper.styleTextField(companyNameField);
        formPanel.add(companyNameField, gbc);
        row++;

        // Phone - with formatting
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        ModernUIHelper.styleTextField(phoneField);
        setupPhoneFormatting(phoneField);
        formPanel.add(phoneField, gbc);
        row++;

        // Email
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        ModernUIHelper.styleTextField(emailField);
        formPanel.add(emailField, gbc);
        row++;

        // Notes - Shorter height
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setFont(ModernUIHelper.NORMAL_FONT);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setPreferredSize(new Dimension(250, 70));  // Fixed smaller height
        formPanel.add(notesScroll, gbc);
        row++;

        // Buttons - Centered with even spacing
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        
        addButton = new JButton("Add");
        addButton.addActionListener(e -> addClient());
        ModernUIHelper.styleButton(addButton, "success");
        buttonPanel.add(addButton);
        
        buttonPanel.add(Box.createHorizontalStrut(10));
        
        updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateClient());
        updateButton.setEnabled(false);
        ModernUIHelper.styleButton(updateButton, "warning");
        buttonPanel.add(updateButton);
        
        buttonPanel.add(Box.createHorizontalStrut(10));
        
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteClient());
        deleteButton.setEnabled(false);
        ModernUIHelper.styleButton(deleteButton, "danger");
        buttonPanel.add(deleteButton);
        
        buttonPanel.add(Box.createHorizontalStrut(10));
        
        clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearForm());
        ModernUIHelper.styleButton(clearButton, "secondary");
        buttonPanel.add(clearButton);

        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.EAST);
    }

    /**
     * Setup phone number formatting: displays as (123) 456-7890 but saves as 1234567890
     */
    private void setupPhoneFormatting(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (isUpdatingPhone) {
                    super.replace(fb, offset, length, text, attrs);
                    return;
                }
                
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + 
                                currentText.substring(offset + length);
                
                // Remove all non-digits
                String digitsOnly = newText.replaceAll("[^0-9]", "");
                
                // Limit to 10 digits
                if (digitsOnly.length() > 10) {
                    return;
                }
                
                // Format the number
                String formatted = formatPhoneNumber(digitsOnly);
                
                isUpdatingPhone = true;
                fb.remove(0, fb.getDocument().getLength());
                fb.insertString(0, formatted, attrs);
                isUpdatingPhone = false;
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                if (isUpdatingPhone) {
                    super.remove(fb, offset, length);
                    return;
                }
                
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + 
                                currentText.substring(offset + length);
                
                String digitsOnly = newText.replaceAll("[^0-9]", "");
                String formatted = formatPhoneNumber(digitsOnly);
                
                isUpdatingPhone = true;
                fb.remove(0, fb.getDocument().getLength());
                fb.insertString(0, formatted, null);
                isUpdatingPhone = false;
            }
        });
    }

    /**
     * Format phone number: 1234567890 -> (123) 456-7890
     */
    private String formatPhoneNumber(String digits) {
        if (digits.length() == 0) return "";
        if (digits.length() <= 3) return "(" + digits;
        if (digits.length() <= 6) return "(" + digits.substring(0, 3) + ") " + digits.substring(3);
        return "(" + digits.substring(0, 3) + ") " + digits.substring(3, 6) + "-" + digits.substring(6);
    }

    /**
     * Extract just the digits from a formatted phone number
     */
    private String getPhoneDigits(String formatted) {
        return formatted.replaceAll("[^0-9]", "");
    }

    private void loadClients() {
        try {
            allClients = clientDAO.getAllClients();
            filterAndSearch();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading clients: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterAndSearch() {
        if (allClients == null) return;
        
        String searchTerm = searchField.getText().trim().toLowerCase();
        String filterType = (String) filterTypeCombo.getSelectedItem();
        
        tableModel.setRowCount(0);
        for (Client client : allClients) {
            // Apply type filter
            boolean typeMatches = "Show All".equals(filterType) || 
                                 filterType.equals(client.getClientType());
            
            // Apply search filter
            boolean searchMatches = searchTerm.isEmpty() ||
                (client.getFirstName() != null && client.getFirstName().toLowerCase().contains(searchTerm)) ||
                (client.getLastName() != null && client.getLastName().toLowerCase().contains(searchTerm)) ||
                (client.getCompanyName() != null && client.getCompanyName().toLowerCase().contains(searchTerm)) ||
                (client.getEmail() != null && client.getEmail().toLowerCase().contains(searchTerm)) ||
                (client.getPhoneNumber() != null && client.getPhoneNumber().contains(searchTerm));
            
            if (typeMatches && searchMatches) {
                // Format phone number for display
                String phoneDisplay = client.getPhoneNumber();
                if (phoneDisplay != null && !phoneDisplay.isEmpty()) {
                    phoneDisplay = formatPhoneNumber(phoneDisplay);
                }

                String nameDisplay = ((client.getFirstName() != null ? client.getFirstName() : "") + " " +
                        (client.getLastName() != null ? client.getLastName() : "")).trim();

                tableModel.addRow(new Object[]{
                    client.getClientId(),
                    client.getCompanyName(),
                    nameDisplay,
                    client.getClientType(),
                    phoneDisplay,
                    client.getEmail()
                });
            }
        }
    }

    private void loadSelectedClient() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int clientId = (Integer) tableModel.getValueAt(selectedRow, 0);
            try {
                selectedClient = clientDAO.getClientById(clientId);
                if (selectedClient != null) {
                    clientTypeCombo.setSelectedItem(selectedClient.getClientType());
                    firstNameField.setText(selectedClient.getFirstName());
                    lastNameField.setText(selectedClient.getLastName());
                    companyNameField.setText(selectedClient.getCompanyName());
                    
                    // Format phone for display
                    String phone = selectedClient.getPhoneNumber();
                    if (phone != null && !phone.isEmpty()) {
                        phoneField.setText(formatPhoneNumber(phone));
                    } else {
                        phoneField.setText("");
                    }
                    
                    emailField.setText(selectedClient.getEmail());
                    notesArea.setText(selectedClient.getNotes());
                    isFormAutoPopulated = true;  // Mark as auto-populated from selection
                    updateButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error loading client details: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addClient() {
        if (!validateForm()) return;

        Client client = new Client();
        String clientType = (String) clientTypeCombo.getSelectedItem();
        client.setClientType(clientType);
        client.setFirstName(firstNameField.getText().trim());
        client.setLastName(lastNameField.getText().trim());
        // Set CompanyName to null for Individual clients (database constraint)
        String companyName = companyNameField.getText().trim();
        client.setCompanyName("Individual".equals(clientType) || companyName.isEmpty() ? null : companyName);
        client.setPhoneNumber(getPhoneDigits(phoneField.getText()));  // Save only digits
        client.setEmail(emailField.getText().trim());
        client.setNotes(notesArea.getText().trim());

        try {
            if (clientDAO.addClient(client)) {
                JOptionPane.showMessageDialog(this, "Client added successfully!");
                clearForm();
                loadClients();  // Auto-refresh
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add client.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding client: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateClient() {
        if (selectedClient == null) return;
        if (!validateForm()) return;

        String clientType = (String) clientTypeCombo.getSelectedItem();
        selectedClient.setClientType(clientType);
        selectedClient.setFirstName(firstNameField.getText().trim());
        selectedClient.setLastName(lastNameField.getText().trim());
        // Set CompanyName to null for Individual clients (database constraint)
        String companyName = companyNameField.getText().trim();
        selectedClient.setCompanyName("Individual".equals(clientType) || companyName.isEmpty() ? null : companyName);
        selectedClient.setPhoneNumber(getPhoneDigits(phoneField.getText()));  // Save only digits
        selectedClient.setEmail(emailField.getText().trim());
        selectedClient.setNotes(notesArea.getText().trim());

        try {
            if (clientDAO.updateClient(selectedClient)) {
                JOptionPane.showMessageDialog(this, "Client updated successfully!");
                clearForm();
                loadClients();  // Auto-refresh
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update client.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating client: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteClient() {
        if (selectedClient == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this client? This will also delete all associated locations.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (clientDAO.deleteClient(selectedClient.getClientId())) {
                    JOptionPane.showMessageDialog(this, "Client deleted successfully!");
                    clearForm();
                    loadClients();  // Auto-refresh
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete client.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting client: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        clientTypeCombo.setSelectedIndex(0);
        firstNameField.setText("");
        lastNameField.setText("");
        companyNameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        notesArea.setText("");
        selectedClient = null;
        isFormAutoPopulated = false;  // Reset flag when form is cleared
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        clientsTable.clearSelection();
    }

    private boolean validateForm() {
        String clientType = (String) clientTypeCombo.getSelectedItem();
        
        if ("Individual".equals(clientType)) {
            if (firstNameField.getText().trim().isEmpty() || lastNameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "First name and last name are required for individual clients.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } else {
            if (companyNameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Company name is required for business clients.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        
        return true;
    }

    public void refreshData() {
        loadClients();
    }
}
