package com.applefitnessequipment.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
// import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.applefitnessequipment.dao.ClientDAO;
import com.applefitnessequipment.dao.ClientLocationDAO;
import com.applefitnessequipment.dao.PMAgreementDAO;
import com.applefitnessequipment.model.Client;
import com.applefitnessequipment.model.ClientLocation;
import com.applefitnessequipment.model.PreventiveMaintenanceAgreement;

public class PreventiveMaintenancePanel extends JPanel {
    private PMAgreementDAO pmaDAO;
    private ClientDAO clientDAO;
    private ClientLocationDAO locationDAO;
    private JTable pmaTable;
    private DefaultTableModel tableModel;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Form fields
    private JComboBox<Client> clientCombo;
    private JComboBox<ClientLocation> billLocationCombo, jobLocationCombo;
    private JTextField agreementNumberField, propertyNameField, facilityNameField;
    private JTextField addressLineField, cityField, stateField, zipCodeField;
    private JTextField contactNameField, contactEmailField, phone1Field, phone2Field;
    // private JTextArea coverageTextArea;
    private JTextField startDateField, endDateField;
    private JComboBox<String> visitFrequencyCombo, statusCombo;
    private JTextField chargePerMileField, chargePerHourField, visitPriceField, taxRateField;
    private JCheckBox insuranceCheckbox;
    private JTextField cancelationDaysField, paymentDaysField, lateFeeField;
    private JButton addButton, updateButton, deleteButton, clearButton;
    private PreventiveMaintenanceAgreement selectedPMA;

    public PreventiveMaintenancePanel() {
        pmaDAO = new PMAgreementDAO();
        clientDAO = new ClientDAO();
        locationDAO = new ClientLocationDAO();
        initComponents();
        loadClients();
        loadAgreements();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel - removed refresh button, data loads automatically
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        add(topPanel, BorderLayout.NORTH);

        // Center Panel - Table
        String[] columns = {"ID", "Agreement #", "Client", "Property", "Start", "End", "Frequency", "Status", "Price"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        pmaTable = new JTable(tableModel);
        pmaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Apply modern styling
        ModernUIHelper.styleTable(pmaTable);
        ModernUIHelper.addTableToggleBehavior(pmaTable, () -> clearForm());
        
        // Hide ID
        pmaTable.getColumnModel().getColumn(0).setMinWidth(0);
        pmaTable.getColumnModel().getColumn(0).setMaxWidth(0);
        pmaTable.getColumnModel().getColumn(0).setWidth(0);
        
        pmaTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && pmaTable.getSelectedRow() >= 0) {
                loadSelectedAgreement();
            }
        });

        JScrollPane scrollPane = new JScrollPane(pmaTable);

        // Allow deselection by clicking on empty space in the scroll pane viewport (not on the table itself)
        scrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                // Only deselect if clicking in the viewport area (the gray empty space around the table)
                if (e.getComponent() == scrollPane && !pmaTable.getBounds().contains(e.getPoint())) {
                    pmaTable.clearSelection();
                    clearForm();
                }
            }
        });

        add(scrollPane, BorderLayout.CENTER);

        // Right Panel - Scrollable Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(ModernUIHelper.createModernBorder("Agreement Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        // Client
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Client:*"), gbc);
        gbc.gridx = 1;
        clientCombo = new JComboBox<>();
        clientCombo.addActionListener(e -> loadLocationsForClient());
        ModernUIHelper.styleComboBox(clientCombo);
        formPanel.add(clientCombo, gbc);
        row++;

        // Billing Location
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Bill Location:*"), gbc);
        gbc.gridx = 1;
        billLocationCombo = new JComboBox<>();
        ModernUIHelper.styleComboBox(billLocationCombo);
        formPanel.add(billLocationCombo, gbc);
        row++;

        // Job Location
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Job Location:*"), gbc);
        gbc.gridx = 1;
        jobLocationCombo = new JComboBox<>();
        ModernUIHelper.styleComboBox(jobLocationCombo);
        formPanel.add(jobLocationCombo, gbc);
        row++;

        // Agreement Number
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Agreement #:*"), gbc);
        gbc.gridx = 1;
        agreementNumberField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(agreementNumberField);
        formPanel.add(agreementNumberField, gbc);
        row++;

        // Property Name
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Property Name:"), gbc);
        gbc.gridx = 1;
        propertyNameField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(propertyNameField);
        formPanel.add(propertyNameField, gbc);
        row++;

        // Facility Name
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Facility Name:"), gbc);
        gbc.gridx = 1;
        facilityNameField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(facilityNameField);
        formPanel.add(facilityNameField, gbc);
        row++;

        // Address Line
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Address:*"), gbc);
        gbc.gridx = 1;
        addressLineField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(addressLineField);
        formPanel.add(addressLineField, gbc);
        row++;

        // City
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("City:*"), gbc);
        gbc.gridx = 1;
        cityField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(cityField);
        formPanel.add(cityField, gbc);
        row++;

        // State
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("State:*"), gbc);
        gbc.gridx = 1;
        stateField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        stateField.setText("PA");
        ModernUIHelper.styleTextField(stateField);
        formPanel.add(stateField, gbc);
        row++;

        // ZIP
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("ZIP:*"), gbc);
        gbc.gridx = 1;
        zipCodeField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(zipCodeField);
        formPanel.add(zipCodeField, gbc);
        row++;

        // Contact Name
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Contact:*"), gbc);
        gbc.gridx = 1;
        contactNameField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(contactNameField);
        formPanel.add(contactNameField, gbc);
        row++;

        // Contact Email
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        contactEmailField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(contactEmailField);
        formPanel.add(contactEmailField, gbc);
        row++;

        // Phone 1
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Phone 1:"), gbc);
        gbc.gridx = 1;
        phone1Field = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(phone1Field);
        formPanel.add(phone1Field, gbc);
        row++;

        // Phone 2
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Phone 2:"), gbc);
        gbc.gridx = 1;
        phone2Field = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(phone2Field);
        formPanel.add(phone2Field, gbc);
        row++;

        // Coverage Text - commented out
        // gbc.gridx = 0; gbc.gridy = row;
        // gbc.anchor = GridBagConstraints.NORTH;
        // formPanel.add(new JLabel("Coverage:*"), gbc);
        // gbc.gridx = 1;
        // gbc.fill = GridBagConstraints.BOTH;
        // coverageTextArea = new JTextArea(3, 15);
        // coverageTextArea.setLineWrap(true);
        // formPanel.add(new JScrollPane(coverageTextArea), gbc);
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        // gbc.anchor = GridBagConstraints.CENTER;
        // row++;

        // Start Date
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Start Date:*"), gbc);
        gbc.gridx = 1;
        startDateField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        startDateField.setText(LocalDate.now().format(dateFormatter));
        ModernUIHelper.styleTextField(startDateField);
        formPanel.add(startDateField, gbc);
        row++;

        // End Date
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("End Date:*"), gbc);
        gbc.gridx = 1;
        endDateField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        endDateField.setText(LocalDate.now().plusYears(1).format(dateFormatter));
        ModernUIHelper.styleTextField(endDateField);
        formPanel.add(endDateField, gbc);
        row++;

        // Visit Frequency
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Frequency:*"), gbc);
        gbc.gridx = 1;
        visitFrequencyCombo = new JComboBox<>(new String[]{"Monthly", "Quarterly", "Semi-Annual", "Annual"});
        ModernUIHelper.styleComboBox(visitFrequencyCombo);
        formPanel.add(visitFrequencyCombo, gbc);
        row++;

        // Status
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Status:*"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(new String[]{"Draft", "Sent", "Expired", "Active", "Declined", "Canceled", "Completed"});
        ModernUIHelper.styleComboBox(statusCombo);
        formPanel.add(statusCombo, gbc);
        row++;

        // Charge Per Mile
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("$/Mile:*"), gbc);
        gbc.gridx = 1;
        chargePerMileField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        chargePerMileField.setText("0.75");
        ModernUIHelper.styleTextField(chargePerMileField);
        formPanel.add(chargePerMileField, gbc);
        row++;

        // Charge Per Hour
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("$/Hour:*"), gbc);
        gbc.gridx = 1;
        chargePerHourField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        chargePerHourField.setText("80.00");
        ModernUIHelper.styleTextField(chargePerHourField);
        formPanel.add(chargePerHourField, gbc);
        row++;

        // Visit Price
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Visit Price:*"), gbc);
        gbc.gridx = 1;
        visitPriceField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        visitPriceField.setText("0.00");
        ModernUIHelper.styleTextField(visitPriceField);
        formPanel.add(visitPriceField, gbc);
        row++;

        // Tax Rate
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Tax %:*"), gbc);
        gbc.gridx = 1;
        taxRateField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        taxRateField.setText("6.00");
        ModernUIHelper.styleTextField(taxRateField);
        formPanel.add(taxRateField, gbc);
        row++;

        // Insurance Required
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Extra Insurance:"), gbc);
        gbc.gridx = 1;
        insuranceCheckbox = new JCheckBox();
        formPanel.add(insuranceCheckbox, gbc);
        row++;

        // Cancelation Days
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Cancel Days:"), gbc);
        gbc.gridx = 1;
        cancelationDaysField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        cancelationDaysField.setText("30");
        ModernUIHelper.styleTextField(cancelationDaysField);
        formPanel.add(cancelationDaysField, gbc);
        row++;

        // Payment Days
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Payment Days:"), gbc);
        gbc.gridx = 1;
        paymentDaysField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        paymentDaysField.setText("30");
        ModernUIHelper.styleTextField(paymentDaysField);
        formPanel.add(paymentDaysField, gbc);
        row++;

        // Late Fee
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Late Fee %:"), gbc);
        gbc.gridx = 1;
        lateFeeField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        lateFeeField.setText("10.00");
        ModernUIHelper.styleTextField(lateFeeField);
        formPanel.add(lateFeeField, gbc);
        row++;

        // Buttons
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        addButton = new JButton("Add");
        addButton.addActionListener(e -> addAgreement());
        ModernUIHelper.styleButton(addButton, "success");
        buttonPanel.add(addButton);

        updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateAgreement());
        updateButton.setEnabled(false);
        ModernUIHelper.styleButton(updateButton, "warning");
        buttonPanel.add(updateButton);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteAgreement());
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
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading clients: " + ex.getMessage());
        }
    }

    private void loadLocationsForClient() {
        Client selected = (Client) clientCombo.getSelectedItem();
        if (selected == null) return;
        
        try {
            List<ClientLocation> locations = locationDAO.getLocationsByClientId(selected.getClientId());
            billLocationCombo.removeAllItems();
            jobLocationCombo.removeAllItems();
            for (ClientLocation loc : locations) {
                billLocationCombo.addItem(loc);
                jobLocationCombo.addItem(loc);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading locations: " + ex.getMessage());
        }
    }

    private void loadAgreements() {
        try {
            List<PreventiveMaintenanceAgreement> agreements = pmaDAO.getAllAgreements();
            tableModel.setRowCount(0);
            for (PreventiveMaintenanceAgreement pma : agreements) {
                tableModel.addRow(new Object[]{
                    pma.getPmaId(),
                    pma.getAgreementNumber(),
                    pma.getClientId(),
                    pma.getPropertyName(),
                    pma.getStartDate(),
                    pma.getEndDate(),
                    pma.getVisitFrequency(),
                    pma.getStatus(),
                    pma.getVisitPrice()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading agreements: " + ex.getMessage());
        }
    }

    private void loadSelectedAgreement() {
        int row = pmaTable.getSelectedRow();
        if (row < 0) return;
        
        int pmaId = (Integer) tableModel.getValueAt(row, 0);
        try {
            selectedPMA = pmaDAO.getAgreementById(pmaId);
            if (selectedPMA != null) {
                // Select client and load locations
                for (int i = 0; i < clientCombo.getItemCount(); i++) {
                    if (clientCombo.getItemAt(i).getClientId().equals(selectedPMA.getClientId())) {
                        clientCombo.setSelectedIndex(i);
                        break;
                    }
                }
                
                loadLocationsForClient();
                
                // Select locations
                for (int i = 0; i < billLocationCombo.getItemCount(); i++) {
                    if (billLocationCombo.getItemAt(i).getClientLocationId().equals(selectedPMA.getBillingLocationId())) {
                        billLocationCombo.setSelectedIndex(i);
                        break;
                    }
                }
                for (int i = 0; i < jobLocationCombo.getItemCount(); i++) {
                    if (jobLocationCombo.getItemAt(i).getClientLocationId().equals(selectedPMA.getJobLocationId())) {
                        jobLocationCombo.setSelectedIndex(i);
                        break;
                    }
                }
                
                agreementNumberField.setText(selectedPMA.getAgreementNumber());
                propertyNameField.setText(selectedPMA.getPropertyName());
                facilityNameField.setText(selectedPMA.getFacilityName());
                addressLineField.setText(selectedPMA.getAddressLine());
                cityField.setText(selectedPMA.getCity());
                stateField.setText(selectedPMA.getState());
                zipCodeField.setText(selectedPMA.getZipCode());
                contactNameField.setText(selectedPMA.getContactName());
                contactEmailField.setText(selectedPMA.getContactEmail());
                phone1Field.setText(selectedPMA.getPhoneNumber1());
                phone2Field.setText(selectedPMA.getPhoneNumber2());
                // coverageTextArea.setText(selectedPMA.getCoverageText());
                startDateField.setText(selectedPMA.getStartDate().format(dateFormatter));
                endDateField.setText(selectedPMA.getEndDate().format(dateFormatter));
                visitFrequencyCombo.setSelectedItem(selectedPMA.getVisitFrequency());
                statusCombo.setSelectedItem(selectedPMA.getStatus());
                chargePerMileField.setText(selectedPMA.getChargePerMile().toString());
                chargePerHourField.setText(selectedPMA.getChargePerHour().toString());
                visitPriceField.setText(selectedPMA.getVisitPrice().toString());
                taxRateField.setText(selectedPMA.getTaxRatePercent().toString());
                insuranceCheckbox.setSelected(selectedPMA.getRequiresAdditionalInsurance() != null ? selectedPMA.getRequiresAdditionalInsurance() : false);
                cancelationDaysField.setText(String.valueOf(selectedPMA.getCancelationNoticeDays()));
                paymentDaysField.setText(String.valueOf(selectedPMA.getPaymentDueAfterWorkDays()));
                lateFeeField.setText(selectedPMA.getLateFeePercentage().toString());
                
                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading agreement: " + ex.getMessage());
        }
    }

    private void addAgreement() {
        if (!validateForm()) return;

        PreventiveMaintenanceAgreement pma = new PreventiveMaintenanceAgreement();
        populateFromForm(pma);

        try {
            if (pmaDAO.addAgreement(pma)) {
                JOptionPane.showMessageDialog(this, "Agreement added successfully!");
                clearForm();
                loadAgreements();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding agreement: " + ex.getMessage());
        }
    }

    private void updateAgreement() {
        if (selectedPMA == null || !validateForm()) return;

        populateFromForm(selectedPMA);

        try {
            if (pmaDAO.updateAgreement(selectedPMA)) {
                JOptionPane.showMessageDialog(this, "Agreement updated successfully!");
                clearForm();
                loadAgreements();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating agreement: " + ex.getMessage());
        }
    }

    private void deleteAgreement() {
        if (selectedPMA == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this agreement?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (pmaDAO.deleteAgreement(selectedPMA.getPmaId())) {
                    JOptionPane.showMessageDialog(this, "Agreement deleted successfully!");
                    clearForm();
                    loadAgreements();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting agreement: " + ex.getMessage());
            }
        }
    }

    private void populateFromForm(PreventiveMaintenanceAgreement pma) {
        Client client = (Client) clientCombo.getSelectedItem();
        ClientLocation billLoc = (ClientLocation) billLocationCombo.getSelectedItem();
        ClientLocation jobLoc = (ClientLocation) jobLocationCombo.getSelectedItem();
        
        pma.setClientId(client.getClientId());
        pma.setBillingLocationId(billLoc.getClientLocationId());
        pma.setJobLocationId(jobLoc.getClientLocationId());
        pma.setAgreementNumber(agreementNumberField.getText().trim());
        pma.setPropertyName(propertyNameField.getText().trim());
        pma.setFacilityName(facilityNameField.getText().trim());
        pma.setAddressLine(addressLineField.getText().trim());
        pma.setCity(cityField.getText().trim());
        pma.setState(stateField.getText().trim());
        pma.setZipCode(zipCodeField.getText().trim());
        pma.setContactName(contactNameField.getText().trim());
        pma.setContactEmail(contactEmailField.getText().trim());
        pma.setPhoneNumber1(phone1Field.getText().trim());
        pma.setPhoneNumber2(phone2Field.getText().trim());
        // pma.setCoverageText(coverageTextArea.getText().trim());
        pma.setStartDate(LocalDate.parse(startDateField.getText().trim(), dateFormatter));
        pma.setEndDate(LocalDate.parse(endDateField.getText().trim(), dateFormatter));
        pma.setVisitFrequency((String) visitFrequencyCombo.getSelectedItem());
        pma.setStatus((String) statusCombo.getSelectedItem());
        pma.setChargePerMile(new BigDecimal(chargePerMileField.getText().trim()));
        pma.setChargePerHour(new BigDecimal(chargePerHourField.getText().trim()));
        pma.setVisitPrice(new BigDecimal(visitPriceField.getText().trim()));
        pma.setTaxRatePercent(new BigDecimal(taxRateField.getText().trim()));
        pma.setRequiresAdditionalInsurance(insuranceCheckbox.isSelected());
        pma.setCancelationNoticeDays(Integer.parseInt(cancelationDaysField.getText().trim()));
        pma.setPaymentDueAfterWorkDays(Integer.parseInt(paymentDaysField.getText().trim()));
        pma.setLateFeePercentage(new BigDecimal(lateFeeField.getText().trim()));
    }

    private void clearForm() {
        if (clientCombo.getItemCount() > 0) clientCombo.setSelectedIndex(0);
        agreementNumberField.setText("");
        propertyNameField.setText("");
        facilityNameField.setText("");
        addressLineField.setText("");
        cityField.setText("");
        stateField.setText("PA");
        zipCodeField.setText("");
        contactNameField.setText("");
        contactEmailField.setText("");
        phone1Field.setText("");
        phone2Field.setText("");
        // coverageTextArea.setText("");
        startDateField.setText(LocalDate.now().format(dateFormatter));
        endDateField.setText(LocalDate.now().plusYears(1).format(dateFormatter));
        visitFrequencyCombo.setSelectedIndex(0);
        statusCombo.setSelectedIndex(0);
        chargePerMileField.setText("0.75");
        chargePerHourField.setText("80.00");
        visitPriceField.setText("0.00");
        taxRateField.setText("6.00");
        insuranceCheckbox.setSelected(false);
        cancelationDaysField.setText("30");
        paymentDaysField.setText("30");
        lateFeeField.setText("10.00");
        selectedPMA = null;
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        pmaTable.clearSelection();
    }

    private boolean validateForm() {
        if (clientCombo.getSelectedItem() == null || billLocationCombo.getSelectedItem() == null ||
            jobLocationCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select client and locations.");
            return false;
        }
        if (agreementNumberField.getText().trim().isEmpty() || addressLineField.getText().trim().isEmpty() ||
            cityField.getText().trim().isEmpty() || contactNameField.getText().trim().isEmpty()
            /* || coverageTextArea.getText().trim().isEmpty() */) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields.");
            return false;
        }
        try {
            LocalDate.parse(startDateField.getText().trim(), dateFormatter);
            LocalDate.parse(endDateField.getText().trim(), dateFormatter);
            new BigDecimal(chargePerMileField.getText().trim());
            new BigDecimal(chargePerHourField.getText().trim());
            new BigDecimal(visitPriceField.getText().trim());
            new BigDecimal(taxRateField.getText().trim());
            Integer.parseInt(cancelationDaysField.getText().trim());
            Integer.parseInt(paymentDaysField.getText().trim());
            new BigDecimal(lateFeeField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date or number format.");
            return false;
        }
        return true;
    }

    public void refreshData() {
        loadClients();
        loadAgreements();
    }
}
