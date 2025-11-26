package com.applefitnessequipment.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.DefaultListCellRenderer;
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
import com.applefitnessequipment.dao.EquipmentQuoteCompleteDAO;
import com.applefitnessequipment.model.Client;
import com.applefitnessequipment.model.ClientLocation;
import com.applefitnessequipment.model.EquipmentQuoteComplete;

public class EquipmentQuotesPanel extends JPanel {
    private EquipmentQuoteCompleteDAO quoteDAO;
    private ClientDAO clientDAO;
    private ClientLocationDAO locationDAO;
    private JTable quotesTable;
    private DefaultTableModel tableModel;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Form fields
    private JComboBox<Client> clientCombo;
    private JComboBox<ClientLocation> billLocationCombo, jobLocationCombo;
    private JTextField quoteNumberField, quoteDateField, contactNameField, salespersonField;
    private JComboBox<String> statusCombo;
    private JTextField shipViaField, freightTermsField, paymentTermsField, fobField;
    
    // Amounts
    private JTextField discountField, subtotalField, freightField, extendedTotalField;
    private JTextField taxRateField, taxAmountField, quoteTotalField;
    private JCheckBox signatureCheckbox;
    
    private JButton addButton, updateButton, deleteButton, clearButton;
    private EquipmentQuoteComplete selectedQuote;

    public EquipmentQuotesPanel() {
        quoteDAO = new EquipmentQuoteCompleteDAO();
        clientDAO = new ClientDAO();
        locationDAO = new ClientLocationDAO();
        initComponents();
        loadClients();
        loadQuotes();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel - removed refresh button, data loads automatically
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        add(topPanel, BorderLayout.NORTH);

        // Center Panel - Table
        String[] columns = {"ID", "Quote #", "Client", "Date", "Status", "Contact", "Total"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        quotesTable = new JTable(tableModel);
        quotesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Apply modern styling
        ModernUIHelper.styleTable(quotesTable);
        ModernUIHelper.addTableToggleBehavior(quotesTable, () -> clearForm());
        
        // Hide ID
        quotesTable.getColumnModel().getColumn(0).setMinWidth(0);
        quotesTable.getColumnModel().getColumn(0).setMaxWidth(0);
        quotesTable.getColumnModel().getColumn(0).setWidth(0);
        
        quotesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && quotesTable.getSelectedRow() >= 0) {
                loadSelectedQuote();
            }
        });

        JScrollPane scrollPane = new JScrollPane(quotesTable);

        // Allow deselection by clicking on empty space in the scroll pane viewport (not on the table itself)
        scrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                // Only deselect if clicking in the viewport area (the gray empty space around the table)
                if (e.getComponent() == scrollPane && !quotesTable.getBounds().contains(e.getPoint())) {
                    quotesTable.clearSelection();
                    clearForm();
                }
            }
        });

        add(scrollPane, BorderLayout.CENTER);

        // Right Panel - Scrollable Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(ModernUIHelper.createModernBorder("Quote Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        // Establish a standard input size and background to keep combos aligned with text fields
        JTextField sizingField = new JTextField(20);
        ModernUIHelper.styleTextField(sizingField);
        java.awt.Dimension standardInputSize = sizingField.getPreferredSize();
        Color standardFieldBackground = sizingField.getBackground();

        // ===== CLIENT INFORMATION =====
        addSectionLabel(formPanel, gbc, row++, "CLIENT INFORMATION");

        // Client
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Client:*"), gbc);
        gbc.gridx = 1;
        clientCombo = new JComboBox<>();
        clientCombo.setEditable(true);
        clientCombo.addActionListener(e -> loadLocationsForClient());
        clientCombo.setPreferredSize(standardInputSize);
        clientCombo.setBackground(standardFieldBackground);
        ModernUIHelper.styleComboBox(clientCombo);
        formPanel.add(clientCombo, gbc);
        row++;

        // Billing Location
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Bill Location:*"), gbc);
        gbc.gridx = 1;
        billLocationCombo = new JComboBox<>();
        billLocationCombo.setEditable(true);
        billLocationCombo.setPreferredSize(standardInputSize);
        billLocationCombo.setBackground(standardFieldBackground);
        ModernUIHelper.styleComboBox(billLocationCombo);
        billLocationCombo.addActionListener(e -> applyBillLocationDetails());
        formPanel.add(billLocationCombo, gbc);
        row++;

        // Job Location
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Job Location:*"), gbc);
        gbc.gridx = 1;
        jobLocationCombo = new JComboBox<>();
        jobLocationCombo.setEditable(true);
        jobLocationCombo.setPreferredSize(standardInputSize);
        jobLocationCombo.setBackground(standardFieldBackground);
        ModernUIHelper.styleComboBox(jobLocationCombo);
        jobLocationCombo.addActionListener(e -> applyJobLocationDetails());
        formPanel.add(jobLocationCombo, gbc);
        row++;

        // ===== QUOTE DETAILS =====
        addSectionLabel(formPanel, gbc, row++, "QUOTE DETAILS");

        // Quote Number
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Quote #:*"), gbc);
        gbc.gridx = 1;
        quoteNumberField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(quoteNumberField);
        formPanel.add(quoteNumberField, gbc);
        row++;

        // Quote Date
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Quote Date:*"), gbc);
        gbc.gridx = 1;
        quoteDateField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        quoteDateField.setText(LocalDate.now().format(dateFormatter));
        ModernUIHelper.styleTextField(quoteDateField);
        formPanel.add(quoteDateField, gbc);
        row++;

        // Status
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Status:*"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(new String[]{"Draft", "Sent", "Expired", "Active", "Declined", "Canceled", "Completed"});
        statusCombo.setPreferredSize(standardInputSize);
        statusCombo.setBackground(standardFieldBackground);
        statusCombo.setOpaque(true);
        statusCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (!isSelected) {
                    comp.setBackground(Color.WHITE);
                }
                return comp;
            }
        });
        ModernUIHelper.styleComboBox(statusCombo);
        formPanel.add(statusCombo, gbc);
        row++;

        // Contact Name (auto-filled from selected Bill location)
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Contact (auto):"), gbc);
        gbc.gridx = 1;
        contactNameField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        contactNameField.setEditable(false);
        ModernUIHelper.styleTextField(contactNameField);
        formPanel.add(contactNameField, gbc);
        row++;

        // Salesperson
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Salesperson:*"), gbc);
        gbc.gridx = 1;
        salespersonField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        salespersonField.setText("Greg Bartram");
        ModernUIHelper.styleTextField(salespersonField);
        formPanel.add(salespersonField, gbc);
        row++;

        // ===== SHIPPING & PAYMENT =====
        addSectionLabel(formPanel, gbc, row++, "SHIPPING & PAYMENT");

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Ship Via:*"), gbc);
        gbc.gridx = 1;
        shipViaField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        shipViaField.setText("AFE Truck/Trailer");
        ModernUIHelper.styleTextField(shipViaField);
        formPanel.add(shipViaField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Freight Terms:*"), gbc);
        gbc.gridx = 1;
        freightTermsField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        freightTermsField.setText("Ppd & Add");
        ModernUIHelper.styleTextField(freightTermsField);
        formPanel.add(freightTermsField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Payment Terms:*"), gbc);
        gbc.gridx = 1;
        paymentTermsField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        paymentTermsField.setText("See Notes");
        ModernUIHelper.styleTextField(paymentTermsField);
        formPanel.add(paymentTermsField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("FOB:*"), gbc);
        gbc.gridx = 1;
        fobField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        fobField.setText("Truck Curbside");
        ModernUIHelper.styleTextField(fobField);
        formPanel.add(fobField, gbc);
        row++;

        // ===== AMOUNTS =====
        addSectionLabel(formPanel, gbc, row++, "AMOUNTS");

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Discount:"), gbc);
        gbc.gridx = 1;
        discountField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        discountField.setText("0.00");
        ModernUIHelper.styleTextField(discountField);
        formPanel.add(discountField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Subtotal:*"), gbc);
        gbc.gridx = 1;
        subtotalField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        subtotalField.setText("0.00");
        ModernUIHelper.styleTextField(subtotalField);
        formPanel.add(subtotalField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Freight:"), gbc);
        gbc.gridx = 1;
        freightField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        freightField.setText("0.00");
        ModernUIHelper.styleTextField(freightField);
        formPanel.add(freightField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Extended Total:*"), gbc);
        gbc.gridx = 1;
        extendedTotalField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        extendedTotalField.setText("0.00");
        ModernUIHelper.styleTextField(extendedTotalField);
        formPanel.add(extendedTotalField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Tax Rate %:*"), gbc);
        gbc.gridx = 1;
        taxRateField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        taxRateField.setText("6.00");
        ModernUIHelper.styleTextField(taxRateField);
        formPanel.add(taxRateField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Tax Amount:*"), gbc);
        gbc.gridx = 1;
        taxAmountField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        taxAmountField.setText("0.00");
        ModernUIHelper.styleTextField(taxAmountField);
        formPanel.add(taxAmountField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Quote Total:*"), gbc);
        gbc.gridx = 1;
        quoteTotalField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        quoteTotalField.setText("0.00");
        ModernUIHelper.styleTextField(quoteTotalField);
        formPanel.add(quoteTotalField, gbc);
        row++;

        // Signature
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Signature:"), gbc);
        gbc.gridx = 1;
        signatureCheckbox = new JCheckBox();
        formPanel.add(signatureCheckbox, gbc);
        row++;

        // Buttons
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        addButton = new JButton("Add");
        addButton.addActionListener(e -> addQuote());
        ModernUIHelper.styleButton(addButton, "success");
        buttonPanel.add(addButton);

        updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateQuote());
        updateButton.setEnabled(false);
        ModernUIHelper.styleButton(updateButton, "warning");
        buttonPanel.add(updateButton);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteQuote());
        deleteButton.setEnabled(false);
        ModernUIHelper.styleButton(deleteButton, "danger");
        buttonPanel.add(deleteButton);

        clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearForm());
        ModernUIHelper.styleButton(clearButton, "secondary");
        buttonPanel.add(clearButton);

        formPanel.add(buttonPanel, gbc);

        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(formScrollPane, BorderLayout.EAST);
    }

    private void loadClients() {
        try {
            List<Client> clients = clientDAO.getAllClients();
            clientCombo.removeAllItems();
            for (Client client : clients) {
                clientCombo.addItem(client);
            }
            clientCombo.setSelectedIndex(-1);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading clients: " + ex.getMessage());
        }
    }

    private void loadLocationsForClient() {
        Client selected = (Client) clientCombo.getSelectedItem();
        if (selected == null) {
            billLocationCombo.removeAllItems();
            jobLocationCombo.removeAllItems();
            contactNameField.setText("");
            return;
        }

        try {
            List<ClientLocation> locations = locationDAO.getLocationsByClientId(selected.getClientId());
            billLocationCombo.removeAllItems();
            jobLocationCombo.removeAllItems();
            for (ClientLocation loc : locations) {
                if ("Billing".equalsIgnoreCase(loc.getLocationType())) {
                    billLocationCombo.addItem(loc);
                } else if ("Job".equalsIgnoreCase(loc.getLocationType())) {
                    jobLocationCombo.addItem(loc);
                }
            }
            billLocationCombo.setSelectedIndex(-1);
            jobLocationCombo.setSelectedIndex(-1);
            contactNameField.setText("");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading locations: " + ex.getMessage());
        }
    }

    private void loadQuotes() {
        try {
            List<EquipmentQuoteComplete> quotes = quoteDAO.getAllQuotes();
            tableModel.setRowCount(0);
            for (EquipmentQuoteComplete quote : quotes) {
                tableModel.addRow(new Object[]{
                    quote.getQuoteId(),
                    quote.getQuoteNumber(),
                    getClientNameForQuote(quote),
                    quote.getQuoteDate(),
                    quote.getStatus(),
                    quote.getContactName(),
                    quote.getQuoteTotalAmount()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading quotes: " + ex.getMessage());
        }
    }

    private String getClientNameForQuote(EquipmentQuoteComplete quote) {
        if (quote.getClientId() != null) {
            try {
                Client client = clientDAO.getClientById(quote.getClientId());
                if (client != null) {
                    if (client.getCompanyName() != null && !client.getCompanyName().isEmpty()) {
                        return client.getCompanyName();
                    } else {
                        return client.getFirstName() + " " + client.getLastName();
                    }
                }
            } catch (SQLException e) {
                // Fall through
            }
        }
        return "Unknown";
    }

    private void loadSelectedQuote() {
        int row = quotesTable.getSelectedRow();
        if (row < 0) return;
        
        int quoteId = (Integer) tableModel.getValueAt(row, 0);
        try {
            selectedQuote = quoteDAO.getQuoteById(quoteId);
            if (selectedQuote != null) {
                // Select client and load locations
                for (int i = 0; i < clientCombo.getItemCount(); i++) {
                    if (clientCombo.getItemAt(i).getClientId().equals(selectedQuote.getClientId())) {
                        clientCombo.setSelectedIndex(i);
                        break;
                    }
                }
                
                loadLocationsForClient();
                
                // Select locations
                for (int i = 0; i < billLocationCombo.getItemCount(); i++) {
                    if (billLocationCombo.getItemAt(i).getClientLocationId().equals(selectedQuote.getBillingLocationId())) {
                        billLocationCombo.setSelectedIndex(i);
                        break;
                    }
                }
                for (int i = 0; i < jobLocationCombo.getItemCount(); i++) {
                    if (jobLocationCombo.getItemAt(i).getClientLocationId().equals(selectedQuote.getJobLocationId())) {
                        jobLocationCombo.setSelectedIndex(i);
                        break;
                    }
                }
                
                quoteNumberField.setText(selectedQuote.getQuoteNumber());
                quoteDateField.setText(selectedQuote.getQuoteDate().format(dateFormatter));
                statusCombo.setSelectedItem(selectedQuote.getStatus());
                contactNameField.setText(selectedQuote.getContactName());
                salespersonField.setText(selectedQuote.getSalespersonName());
                
                // Contact from bill location
                contactNameField.setText(selectedQuote.getContactName());
                
                shipViaField.setText(selectedQuote.getShipVia());
                freightTermsField.setText(selectedQuote.getFreightTerms());
                paymentTermsField.setText(selectedQuote.getPaymentTerms());
                fobField.setText(selectedQuote.getFobLocation());
                
                discountField.setText(selectedQuote.getTotalDiscountAmount().toString());
                subtotalField.setText(selectedQuote.getSubtotalAmount().toString());
                freightField.setText(selectedQuote.getFreightAmount().toString());
                extendedTotalField.setText(selectedQuote.getExtendedTotalAmount().toString());
                taxRateField.setText(selectedQuote.getSalesTaxRatePercent().toString());
                taxAmountField.setText(selectedQuote.getSalesTaxAmount().toString());
                quoteTotalField.setText(selectedQuote.getQuoteTotalAmount().toString());
                signatureCheckbox.setSelected(selectedQuote.getClientSignatureBoolean() != null ? selectedQuote.getClientSignatureBoolean() : false);
                
                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading quote: " + ex.getMessage());
        }
    }

    private void addQuote() {
        if (!validateForm()) return;

        EquipmentQuoteComplete quote = new EquipmentQuoteComplete();
        populateFromForm(quote);

        try {
            if (quoteDAO.addQuote(quote)) {
                JOptionPane.showMessageDialog(this, "Quote added successfully!");
                clearForm();
                loadQuotes();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding quote: " + ex.getMessage());
        }
    }

    private void updateQuote() {
        if (selectedQuote == null || !validateForm()) return;

        populateFromForm(selectedQuote);

        try {
            if (quoteDAO.updateQuote(selectedQuote)) {
                JOptionPane.showMessageDialog(this, "Quote updated successfully!");
                clearForm();
                loadQuotes();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating quote: " + ex.getMessage());
        }
    }

    private void deleteQuote() {
        if (selectedQuote == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this quote?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (quoteDAO.deleteQuote(selectedQuote.getQuoteId())) {
                    JOptionPane.showMessageDialog(this, "Quote deleted successfully!");
                    clearForm();
                    loadQuotes();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting quote: " + ex.getMessage());
            }
        }
    }

    private void populateFromForm(EquipmentQuoteComplete quote) {
        Client client = (Client) clientCombo.getSelectedItem();
        ClientLocation billLoc = (ClientLocation) billLocationCombo.getSelectedItem();
        ClientLocation jobLoc = (ClientLocation) jobLocationCombo.getSelectedItem();
        
        quote.setClientId(client.getClientId());
        quote.setBillingLocationId(billLoc.getClientLocationId());
        quote.setJobLocationId(jobLoc.getClientLocationId());
        quote.setClientTypeSnapshot(client.getClientType());
        quote.setClientCompanyNameSnapshot(client.getCompanyName());
        quote.setClientFirstNameSnapshot(client.getFirstName());
        quote.setClientLastNameSnapshot(client.getLastName());
        quote.setQuoteNumber(quoteNumberField.getText().trim());
        quote.setQuoteDate(LocalDate.parse(quoteDateField.getText().trim(), dateFormatter));
        quote.setStatus((String) statusCombo.getSelectedItem());
        quote.setContactName(billLoc.getContactName());
        quote.setSalespersonName(salespersonField.getText().trim());
        
        quote.setBillToCompanyName(billLoc.getCompanyName());
        quote.setBillToContactName(billLoc.getContactName());
        quote.setBillToStreetAddress(billLoc.getStreetAddress());
        quote.setBillToBuildingName(billLoc.getBuildingName());
        quote.setBillToRoomNumber(billLoc.getRoomNumber());
        quote.setBillToCity(billLoc.getCity());
        quote.setBillToState(billLoc.getState());
        quote.setBillToZipCode(billLoc.getZipCode());
        quote.setBillToCountry(billLoc.getCountry());
        quote.setBillToPhone(billLoc.getPhone());
        quote.setBillToFax(billLoc.getFax());
        // BillToPONumber removed from schema
        
        quote.setJobAtCompanyName(jobLoc.getCompanyName());
        quote.setJobAtContactName(jobLoc.getContactName());
        quote.setJobAtStreetAddress(jobLoc.getStreetAddress());
        quote.setJobAtBuildingName(jobLoc.getBuildingName());
        quote.setJobAtRoomNumber(jobLoc.getRoomNumber());
        quote.setJobAtCity(jobLoc.getCity());
        quote.setJobAtState(jobLoc.getState());
        quote.setJobAtZipCode(jobLoc.getZipCode());
        quote.setJobAtCountry(jobLoc.getCountry());
        quote.setJobAtEmail(jobLoc.getEmail());
        // JobAtPONumber removed from schema
        
        quote.setShipVia(shipViaField.getText().trim());
        quote.setFreightTerms(freightTermsField.getText().trim());
        quote.setPaymentTerms(paymentTermsField.getText().trim());
        quote.setFobLocation(fobField.getText().trim());
        
        quote.setTotalDiscountAmount(new BigDecimal(discountField.getText().trim()));
        quote.setSubtotalAmount(new BigDecimal(subtotalField.getText().trim()));
        quote.setFreightAmount(new BigDecimal(freightField.getText().trim()));
        quote.setSalesTaxRatePercent(new BigDecimal(taxRateField.getText().trim()));
        quote.setClientSignatureBoolean(signatureCheckbox.isSelected());
    }

    private void clearForm() {
        clientCombo.setSelectedIndex(-1);
        billLocationCombo.removeAllItems();
        jobLocationCombo.removeAllItems();
        quoteNumberField.setText("");
        quoteDateField.setText(LocalDate.now().format(dateFormatter));
        statusCombo.setSelectedIndex(0);
        contactNameField.setText("");
        salespersonField.setText("Greg Bartram");
        
        shipViaField.setText("AFE Truck/Trailer");
        freightTermsField.setText("Ppd & Add");
        paymentTermsField.setText("See Notes");
        fobField.setText("Truck Curbside");
        
        discountField.setText("0.00");
        subtotalField.setText("0.00");
        freightField.setText("0.00");
        extendedTotalField.setText("0.00");
        taxRateField.setText("6.00");
        taxAmountField.setText("0.00");
        quoteTotalField.setText("0.00");
        signatureCheckbox.setSelected(false);
        
        selectedQuote = null;
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        quotesTable.clearSelection();
    }

    private boolean validateForm() {
        if (clientCombo.getSelectedItem() == null || billLocationCombo.getSelectedItem() == null ||
            jobLocationCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select client and locations.");
            return false;
        }
        if (quoteNumberField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields.");
            return false;
        }
        try {
            LocalDate.parse(quoteDateField.getText().trim(), dateFormatter);
            new BigDecimal(subtotalField.getText().trim());
            new BigDecimal(extendedTotalField.getText().trim());
            new BigDecimal(taxRateField.getText().trim());
            new BigDecimal(taxAmountField.getText().trim());
            new BigDecimal(quoteTotalField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date or number format.");
            return false;
        }
        return true;
    }

    public void refreshData() {
        loadClients();
        loadQuotes();
    }

    private void applyBillLocationDetails() {
        Object sel = billLocationCombo.getSelectedItem();
        if (!(sel instanceof ClientLocation)) return;
        ClientLocation loc = (ClientLocation) sel;
        contactNameField.setText(loc.getContactName());
    }

    private void applyJobLocationDetails() {
        // No-op for now since address fields are no longer editable/displayed
    }

    private void addSectionLabel(JPanel panel, GridBagConstraints gbc, int row, String text) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 0, 5, 0)
        ));
        panel.add(label, gbc);
        gbc.gridwidth = 1;
    }
}
