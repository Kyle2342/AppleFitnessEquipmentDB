package com.applefitnessequipment.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.applefitnessequipment.dao.ClientDAO;
import com.applefitnessequipment.dao.ClientLocationDAO;
import com.applefitnessequipment.dao.PMAgreementDAO;
import com.applefitnessequipment.model.Client;
import com.applefitnessequipment.model.ClientLocation;
import com.applefitnessequipment.model.PreventiveMaintenanceAgreement;

/**
 * UI for PreventiveMaintenanceAgreements aligned to applefitnessequipmentdb_schema.sql.
 */
public class PreventiveMaintenancePanel extends JPanel {
    private final PMAgreementDAO pmaDAO;
    private final ClientDAO clientDAO;
    private final ClientLocationDAO locationDAO;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private JTable pmaTable;
    private DefaultTableModel tableModel;

    private JComboBox<Client> clientCombo;
    private JComboBox<ClientLocation> billLocationCombo;
    private JComboBox<ClientLocation> jobLocationCombo;
    private JTextField agreementNumberField;
    private JTextField startDateField;
    private JTextField endDateField;
    private JComboBox<String> visitFrequencyCombo;
    private JComboBox<String> statusCombo;
    private JTextField visitPriceField;
    private JTextField taxRateField;
    private JTextField taxAmountField;
    private JTextField pricePerYearField;
    private JCheckBox signatureCheckbox;

    private PreventiveMaintenanceAgreement selectedPMA;

    public PreventiveMaintenancePanel() {
        this.pmaDAO = new PMAgreementDAO();
        this.clientDAO = new ClientDAO();
        this.locationDAO = new ClientLocationDAO();

        initComponents();
        loadClients();
        loadAgreements();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Agreement #", "Client", "Start", "End", "Status", "Visit Price", "Price/Year"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        pmaTable = new JTable(tableModel);
        pmaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ModernUIHelper.styleTable(pmaTable);
        ModernUIHelper.addTableToggleBehavior(pmaTable, this::clearForm);

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
        add(scrollPane, BorderLayout.CENTER);

        // Form
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

        // Billing location
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Billing Location:*"), gbc);
        gbc.gridx = 1;
        billLocationCombo = new JComboBox<>();
        ModernUIHelper.styleComboBox(billLocationCombo);
        formPanel.add(billLocationCombo, gbc);
        row++;

        // Job location
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Job Location:*"), gbc);
        gbc.gridx = 1;
        jobLocationCombo = new JComboBox<>();
        ModernUIHelper.styleComboBox(jobLocationCombo);
        formPanel.add(jobLocationCombo, gbc);
        row++;

        // Agreement #
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Agreement #:*"), gbc);
        gbc.gridx = 1;
        agreementNumberField = new JTextField(20);
        ModernUIHelper.styleTextField(agreementNumberField);
        formPanel.add(agreementNumberField, gbc);
        row++;

        // Dates
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Start Date (MM/dd/yyyy):*"), gbc);
        gbc.gridx = 1;
        startDateField = new JTextField(20);
        ModernUIHelper.styleTextField(startDateField);
        formPanel.add(startDateField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("End Date (MM/dd/yyyy):*"), gbc);
        gbc.gridx = 1;
        endDateField = new JTextField(20);
        ModernUIHelper.styleTextField(endDateField);
        formPanel.add(endDateField, gbc);
        row++;

        // Frequency & Status
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Visit Frequency:*"), gbc);
        gbc.gridx = 1;
        visitFrequencyCombo = new JComboBox<>(new String[]{"Monthly", "Quarterly", "Semi-Annual", "Annual"});
        visitFrequencyCombo.addActionListener(e -> updateDerivedFields());
        ModernUIHelper.styleComboBox(visitFrequencyCombo);
        formPanel.add(visitFrequencyCombo, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Status:*"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(new String[]{"Draft", "Sent", "Expired", "Active", "Declined", "Canceled", "Completed"});
        ModernUIHelper.styleComboBox(statusCombo);
        formPanel.add(statusCombo, gbc);
        row++;

        // Money
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Visit Price:*"), gbc);
        gbc.gridx = 1;
        visitPriceField = new JTextField(20);
        ModernUIHelper.styleTextField(visitPriceField);
        formPanel.add(visitPriceField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Tax Rate %:*"), gbc);
        gbc.gridx = 1;
        taxRateField = new JTextField(20);
        ModernUIHelper.styleTextField(taxRateField);
        formPanel.add(taxRateField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Tax Amount (calc):"), gbc);
        gbc.gridx = 1;
        taxAmountField = new JTextField(20);
        taxAmountField.setEditable(false);
        taxAmountField.setBackground(java.awt.Color.WHITE);
        ModernUIHelper.styleTextField(taxAmountField);
        formPanel.add(taxAmountField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Price Per Year (calc):"), gbc);
        gbc.gridx = 1;
        pricePerYearField = new JTextField(20);
        pricePerYearField.setEditable(false);
        pricePerYearField.setBackground(java.awt.Color.WHITE);
        ModernUIHelper.styleTextField(pricePerYearField);
        formPanel.add(pricePerYearField, gbc);
        row++;

        // Signature
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Client Signed:"), gbc);
        gbc.gridx = 1;
        signatureCheckbox = new JCheckBox();
        formPanel.add(signatureCheckbox, gbc);
        row++;

        // Buttons
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addAgreement());
        ModernUIHelper.styleButton(addButton, "success");
        buttonPanel.add(addButton);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateAgreement());
        ModernUIHelper.styleButton(updateButton, "warning");
        buttonPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteAgreement());
        ModernUIHelper.styleButton(deleteButton, "danger");
        buttonPanel.add(deleteButton);

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearForm());
        ModernUIHelper.styleButton(clearButton, "secondary");
        buttonPanel.add(clearButton);

        formPanel.add(buttonPanel, gbc);

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(formScroll, BorderLayout.EAST);

        // Recalculate derived fields when price or tax changes
        DocumentListener derivedListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateDerivedFields(); }
            public void removeUpdate(DocumentEvent e) { updateDerivedFields(); }
            public void changedUpdate(DocumentEvent e) { updateDerivedFields(); }
        };
        visitPriceField.getDocument().addDocumentListener(derivedListener);
        taxRateField.getDocument().addDocumentListener(derivedListener);

        clearForm();
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
        billLocationCombo.removeAllItems();
        jobLocationCombo.removeAllItems();

        if (selected == null) {
            return;
        }

        try {
            List<ClientLocation> locations = locationDAO.getLocationsByClientId(selected.getClientId());
            for (ClientLocation loc : locations) {
                if ("Billing".equalsIgnoreCase(loc.getLocationType())) {
                    billLocationCombo.addItem(loc);
                }
                if ("Job".equalsIgnoreCase(loc.getLocationType())) {
                    jobLocationCombo.addItem(loc);
                }
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
                    pma.getPreventiveMaintenanceAgreementId(),
                    pma.getAgreementNumber(),
                    getClientName(pma.getClientId()),
                    formatDate(pma.getStartDate()),
                    formatDate(pma.getEndDate()),
                    pma.getStatus(),
                    pma.getVisitPrice(),
                    pma.getPricePerYear()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading agreements: " + ex.getMessage());
        }
    }

    private void loadSelectedAgreement() {
        int row = pmaTable.getSelectedRow();
        if (row < 0) return;

        Integer id = (Integer) tableModel.getValueAt(row, 0);
        try {
            selectedPMA = pmaDAO.getAgreementById(id);
            if (selectedPMA == null) return;

            selectClientById(selectedPMA.getClientId());
            loadLocationsForClient();
            selectLocation(billLocationCombo, selectedPMA.getClientBillingLocationId());
            selectLocation(jobLocationCombo, selectedPMA.getClientJobLocationId());

            agreementNumberField.setText(selectedPMA.getAgreementNumber());
            startDateField.setText(formatDate(selectedPMA.getStartDate()));
            endDateField.setText(formatDate(selectedPMA.getEndDate()));
            visitFrequencyCombo.setSelectedItem(selectedPMA.getVisitFrequency());
            statusCombo.setSelectedItem(selectedPMA.getStatus());
            visitPriceField.setText(valueOrDefault(selectedPMA.getVisitPrice(), "0.00"));
            taxRateField.setText(valueOrDefault(selectedPMA.getTaxRatePercent(), "6.00"));
            signatureCheckbox.setSelected(Boolean.TRUE.equals(selectedPMA.getClientSignatureBoolean()));

            // Derived values from DB (generated columns)
            taxAmountField.setText(valueOrDefault(selectedPMA.getTaxAmount(), ""));
            pricePerYearField.setText(valueOrDefault(selectedPMA.getPricePerYear(), ""));
            updateDerivedFields();
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
        if (selectedPMA == null) return;
        if (!validateForm()) return;

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
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (pmaDAO.deleteAgreement(selectedPMA.getPreventiveMaintenanceAgreementId())) {
                JOptionPane.showMessageDialog(this, "Agreement deleted successfully!");
                clearForm();
                loadAgreements();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting agreement: " + ex.getMessage());
        }
    }

    private void populateFromForm(PreventiveMaintenanceAgreement pma) {
        Client client = (Client) clientCombo.getSelectedItem();
        ClientLocation bill = (ClientLocation) billLocationCombo.getSelectedItem();
        ClientLocation job = (ClientLocation) jobLocationCombo.getSelectedItem();

        pma.setClientId(client.getClientId());
        pma.setClientBillingLocationId(bill.getClientLocationId());
        pma.setClientJobLocationId(job.getClientLocationId());
        pma.setAgreementNumber(agreementNumberField.getText().trim());
        pma.setStartDate(LocalDate.parse(startDateField.getText().trim(), dateFormatter));
        pma.setEndDate(LocalDate.parse(endDateField.getText().trim(), dateFormatter));
        pma.setVisitFrequency((String) visitFrequencyCombo.getSelectedItem());
        pma.setStatus((String) statusCombo.getSelectedItem());
        pma.setVisitPrice(new BigDecimal(visitPriceField.getText().trim()));
        pma.setTaxRatePercent(new BigDecimal(taxRateField.getText().trim()));
        pma.setClientSignatureBoolean(signatureCheckbox.isSelected());
    }

    private boolean validateForm() {
        if (clientCombo.getSelectedItem() == null ||
            billLocationCombo.getSelectedItem() == null ||
            jobLocationCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select client, billing, and job locations.");
            return false;
        }

        if (agreementNumberField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Agreement number is required.");
            return false;
        }

        try {
            LocalDate start = LocalDate.parse(startDateField.getText().trim(), dateFormatter);
            LocalDate end = LocalDate.parse(endDateField.getText().trim(), dateFormatter);
            if (end.isBefore(start)) {
                JOptionPane.showMessageDialog(this, "End date cannot be before start date.");
                return false;
            }
            new BigDecimal(visitPriceField.getText().trim());
            new BigDecimal(taxRateField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date or number format. Use MM/dd/yyyy for dates.");
            return false;
        }

        return true;
    }

    private void clearForm() {
        clientCombo.setSelectedIndex(-1);
        billLocationCombo.removeAllItems();
        jobLocationCombo.removeAllItems();
        agreementNumberField.setText("");
        startDateField.setText(LocalDate.now().format(dateFormatter));
        endDateField.setText(LocalDate.now().plusYears(1).format(dateFormatter));
        visitFrequencyCombo.setSelectedItem("Monthly");
        statusCombo.setSelectedItem("Draft");
        visitPriceField.setText("0.00");
        taxRateField.setText("6.00");
        taxAmountField.setText("");
        pricePerYearField.setText("");
        signatureCheckbox.setSelected(false);
        selectedPMA = null;
        pmaTable.clearSelection();
        updateDerivedFields();
    }

    private void updateDerivedFields() {
        try {
            BigDecimal price = new BigDecimal(visitPriceField.getText().trim());
            BigDecimal taxRate = new BigDecimal(taxRateField.getText().trim());
            BigDecimal taxAmount = price.multiply(taxRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            taxAmountField.setText(taxAmount.toString());

            int visitsPerYear = switch ((String) visitFrequencyCombo.getSelectedItem()) {
                case "Monthly" -> 12;
                case "Quarterly" -> 4;
                case "Semi-Annual" -> 2;
                default -> 1;
            };

            BigDecimal pricePerYear = price
                    .multiply(BigDecimal.valueOf(visitsPerYear))
                    .multiply(BigDecimal.ONE.add(taxRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)))
                    .setScale(2, RoundingMode.HALF_UP);
            pricePerYearField.setText(pricePerYear.toString());
        } catch (Exception e) {
            taxAmountField.setText("");
            pricePerYearField.setText("");
        }
    }

    private void selectClientById(Integer clientId) {
        if (clientId == null) return;
        for (int i = 0; i < clientCombo.getItemCount(); i++) {
            Client c = clientCombo.getItemAt(i);
            if (c != null && clientId.equals(c.getClientId())) {
                clientCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void selectLocation(JComboBox<ClientLocation> combo, Integer id) {
        if (id == null) return;
        for (int i = 0; i < combo.getItemCount(); i++) {
            ClientLocation loc = combo.getItemAt(i);
            if (loc != null && id.equals(loc.getClientLocationId())) {
                combo.setSelectedIndex(i);
                break;
            }
        }
    }

    private String getClientName(Integer clientId) {
        if (clientId == null) return "Unknown";
        try {
            Client client = clientDAO.getClientById(clientId);
            if (client == null) return "Unknown";
            return client.toString();
        } catch (SQLException e) {
            return "Unknown";
        }
    }

    private String formatDate(LocalDate date) {
        return date == null ? "" : date.format(dateFormatter);
    }

    private String valueOrDefault(BigDecimal value, String defaultValue) {
        return value == null ? defaultValue : value.toString();
    }

    public void refreshData() {
        loadClients();
        loadAgreements();
    }
}
