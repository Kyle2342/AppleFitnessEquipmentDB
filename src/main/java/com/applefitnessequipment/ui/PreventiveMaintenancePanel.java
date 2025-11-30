package com.applefitnessequipment.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.applefitnessequipment.dao.ClientDAO;
import com.applefitnessequipment.dao.ClientLocationDAO;
import com.applefitnessequipment.dao.PMAgreementDAO;
import com.applefitnessequipment.dao.PMAgreementEquipmentDAO;
import com.applefitnessequipment.model.Client;
import com.applefitnessequipment.model.ClientLocation;
import com.applefitnessequipment.model.PMAgreementEquipment;
import com.applefitnessequipment.model.PreventiveMaintenanceAgreement;

/**
 * UI for PreventiveMaintenanceAgreements aligned to applefitnessequipmentdb_schema.sql.
 */
public class PreventiveMaintenancePanel extends JPanel {
    private final PMAgreementDAO pmaDAO;
    private final PMAgreementEquipmentDAO equipmentDAO;
    private final ClientDAO clientDAO;
    private final ClientLocationDAO locationDAO;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private boolean isUpdatingDate = false;

    private JTable pmaTable;
    private DefaultTableModel tableModel;

    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;
    private JComboBox<String> visitFrequencyFilterCombo;
    private JComboBox<String> agreementTotalFilterCombo;

    // changed to plain JComboBox
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

    private JTable equipmentsTable;
    private DefaultTableModel equipmentsTableModel;
    private final List<PMAgreementEquipment> equipments = new ArrayList<>();

    private PreventiveMaintenanceAgreement selectedPMA;

    public PreventiveMaintenancePanel() {
        this.pmaDAO = new PMAgreementDAO();
        this.equipmentDAO = new PMAgreementEquipmentDAO();
        this.clientDAO = new ClientDAO();
        this.locationDAO = new ClientLocationDAO();

        initComponents();
        loadClients();
        loadAgreements();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search and filter panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        topPanel.add(new JLabel("Search:"));
        searchField = new JTextField(25);
        searchField.setFont(ModernUIHelper.NORMAL_FONT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new java.awt.Color(180, 180, 180), 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterAgreements(); }
            public void removeUpdate(DocumentEvent e) { filterAgreements(); }
            public void changedUpdate(DocumentEvent e) { filterAgreements(); }
        });
        topPanel.add(searchField);

        topPanel.add(javax.swing.Box.createHorizontalStrut(20));

        // Filter by Status
        topPanel.add(new JLabel("Filter by Status:"));
        statusFilterCombo = new JComboBox<>(new String[]{"Show All", "Draft", "Sent", "Expired", "Active", "Declined", "Canceled", "Completed"});
        statusFilterCombo.setFont(ModernUIHelper.NORMAL_FONT);
        statusFilterCombo.setPreferredSize(new java.awt.Dimension(120, 38));
        statusFilterCombo.addActionListener(e -> filterAgreements());
        topPanel.add(statusFilterCombo);

        // Filter by Visit Frequency
        topPanel.add(new JLabel("Filter by Visit Frequency:"));
        visitFrequencyFilterCombo = new JComboBox<>(new String[]{"Show All", "Monthly", "Quarterly", "Semi-Annual", "Annual"});
        visitFrequencyFilterCombo.setFont(ModernUIHelper.NORMAL_FONT);
        visitFrequencyFilterCombo.setPreferredSize(new java.awt.Dimension(120, 38));
        visitFrequencyFilterCombo.addActionListener(e -> filterAgreements());
        topPanel.add(visitFrequencyFilterCombo);

        // Filter by Agreement Total
        topPanel.add(new JLabel("Filter by Agreement Total:"));
        agreementTotalFilterCombo = new JComboBox<>(new String[]{"Show All", "Most", "Least"});
        agreementTotalFilterCombo.setFont(ModernUIHelper.NORMAL_FONT);
        agreementTotalFilterCombo.setPreferredSize(new java.awt.Dimension(120, 38));
        agreementTotalFilterCombo.addActionListener(e -> filterAgreements());
        topPanel.add(agreementTotalFilterCombo);

        add(topPanel, BorderLayout.NORTH);

        // Table - reordered columns: Agreement #, Client, Job Location, Start Date, Visit Frequency, End Date, Agreement Total, Status, Bill Location
        String[] columns = {"ID", "Agreement #", "Client", "Job Location", "Start Date", "Visit Frequency", "End Date", "Agreement Total", "Status", "Bill Location"};
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

        // Set column widths: Agreement # (11), Start Date (10), End Date (10), Status (7), Visit Frequency (15)
        pmaTable.getColumnModel().getColumn(1).setPreferredWidth(110); // Agreement #
        pmaTable.getColumnModel().getColumn(1).setMaxWidth(120);
        pmaTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Start Date
        pmaTable.getColumnModel().getColumn(4).setMaxWidth(110);
        pmaTable.getColumnModel().getColumn(5).setPreferredWidth(130); // Visit Frequency
        pmaTable.getColumnModel().getColumn(5).setMaxWidth(140);
        pmaTable.getColumnModel().getColumn(6).setPreferredWidth(100); // End Date
        pmaTable.getColumnModel().getColumn(6).setMaxWidth(110);
        pmaTable.getColumnModel().getColumn(7).setPreferredWidth(125); // Agreement Total
        pmaTable.getColumnModel().getColumn(7).setMaxWidth(135);
        pmaTable.getColumnModel().getColumn(8).setPreferredWidth(70);  // Status
        pmaTable.getColumnModel().getColumn(8).setMaxWidth(80);

        pmaTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && pmaTable.getSelectedRow() >= 0) {
                loadSelectedAgreement();
            }
        });

        JScrollPane scrollPane = new JScrollPane(pmaTable);
        add(scrollPane, BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(ModernUIHelper.createModernBorder("Preventive Maintenance Agreements Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        addSectionLabel(formPanel, gbc, row++, "CLIENT INFO");

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

        addSectionLabel(formPanel, gbc, row++, "AGREEMENT INFO");

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
        setupDateFormatting(startDateField);
        formPanel.add(startDateField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("End Date (MM/dd/yyyy):*"), gbc);
        gbc.gridx = 1;
        endDateField = new JTextField(20);
        ModernUIHelper.styleTextField(endDateField);
        setupDateFormatting(endDateField);
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

        addSectionLabel(formPanel, gbc, row++, "AMOUNTS");

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
        formPanel.add(new JLabel("Tax Amount:"), gbc);
        gbc.gridx = 1;
        taxAmountField = new JTextField(20);
        taxAmountField.setEditable(false);
        taxAmountField.setFocusable(false);
        taxAmountField.setBackground(new java.awt.Color(240, 240, 240));
        ModernUIHelper.styleTextField(taxAmountField);
        formPanel.add(taxAmountField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Price Per Year:"), gbc);
        gbc.gridx = 1;
        pricePerYearField = new JTextField(20);
        pricePerYearField.setEditable(false);
        pricePerYearField.setFocusable(false);
        pricePerYearField.setBackground(new java.awt.Color(240, 240, 240));
        ModernUIHelper.styleTextField(pricePerYearField);
        formPanel.add(pricePerYearField, gbc);
        row++;

        addSectionLabel(formPanel, gbc, row++, "AGREEMENT ITEMS");

        equipmentsTableModel = new DefaultTableModel(new String[]{"Row #", "Equipment Type", "Make", "Model", "Serial Number"}, 0) {
            @Override
            public boolean isCellEditable(int rowIdx, int column) {
                return false;
            }
        };
        equipmentsTable = new JTable(equipmentsTableModel);
        ModernUIHelper.addTableToggleBehavior(equipmentsTable);
        JScrollPane equipmentScroll = new JScrollPane(equipmentsTable);
        equipmentScroll.setPreferredSize(new Dimension(450, 150));
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(equipmentScroll, gbc);
        gbc.gridwidth = 1;
        row++;

        JPanel equipmentButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addEquipmentButton = new JButton("Add Item");
        addEquipmentButton.addActionListener(e -> addEquipment());
        ModernUIHelper.styleButton(addEquipmentButton, "success");
        equipmentButtons.add(addEquipmentButton);

        JButton editEquipmentButton = new JButton("Edit Item");
        editEquipmentButton.addActionListener(e -> editEquipment());
        ModernUIHelper.styleButton(editEquipmentButton, "primary");
        equipmentButtons.add(editEquipmentButton);

        JButton deleteEquipmentButton = new JButton("Delete Item");
        deleteEquipmentButton.addActionListener(e -> deleteEquipment());
        ModernUIHelper.styleButton(deleteEquipmentButton, "danger");
        equipmentButtons.add(deleteEquipmentButton);

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(equipmentButtons, gbc);
        gbc.gridwidth = 1;
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
        formScroll.getVerticalScrollBar().setUnitIncrement(16);

        // Add mouse listener to clear table selection when clicking on form panel background
        // This allows you to work on the form without the table interfering
        formPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                pmaTable.clearSelection();
                // Don't clear the form - let user keep their typed data
            }
        });

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
            for (Client c : clients) {
                clientCombo.addItem(c);
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

        billLocationCombo.setSelectedIndex(-1);
        jobLocationCombo.setSelectedIndex(-1);
    }

    private void loadAgreements() {
        filterAgreements();
    }

    private void filterAgreements() {
        String searchText = searchField.getText().toLowerCase().trim();
        String statusFilter = (String) statusFilterCombo.getSelectedItem();
        String visitFrequencyFilter = (String) visitFrequencyFilterCombo.getSelectedItem();
        String agreementTotalFilter = (String) agreementTotalFilterCombo.getSelectedItem();

        try {
            List<PreventiveMaintenanceAgreement> agreements = pmaDAO.getAllAgreements();
            List<PreventiveMaintenanceAgreement> filteredAgreements = new ArrayList<>();

            for (PreventiveMaintenanceAgreement pma : agreements) {
                // Search across multiple fields
                String agreementNumber = pma.getAgreementNumber() != null ? pma.getAgreementNumber().toLowerCase() : "";
                String clientName = getClientName(pma.getClientId()).toLowerCase();
                String status = pma.getStatus() != null ? pma.getStatus().toLowerCase() : "";
                String jobLocation = getLocationName(pma.getClientJobLocationId()).toLowerCase();
                String billLocation = getLocationName(pma.getClientBillingLocationId()).toLowerCase();
                String visitFrequency = pma.getVisitFrequency() != null ? pma.getVisitFrequency().toLowerCase() : "";

                boolean searchMatches = searchText.isEmpty() ||
                    agreementNumber.contains(searchText) ||
                    clientName.contains(searchText) ||
                    status.contains(searchText) ||
                    jobLocation.contains(searchText) ||
                    billLocation.contains(searchText) ||
                    visitFrequency.contains(searchText);

                // Apply status filter
                boolean statusMatches = "Show All".equals(statusFilter) ||
                    statusFilter.equalsIgnoreCase(pma.getStatus());

                // Apply visit frequency filter
                boolean frequencyMatches = "Show All".equals(visitFrequencyFilter) ||
                    visitFrequencyFilter.equalsIgnoreCase(pma.getVisitFrequency());

                if (searchMatches && statusMatches && frequencyMatches) {
                    filteredAgreements.add(pma);
                }
            }

            // Sort by Agreement Total if needed
            if ("Most".equals(agreementTotalFilter)) {
                filteredAgreements.sort((a1, a2) -> {
                    BigDecimal t1 = a1.getPricePerYear() != null ? a1.getPricePerYear() : BigDecimal.ZERO;
                    BigDecimal t2 = a2.getPricePerYear() != null ? a2.getPricePerYear() : BigDecimal.ZERO;
                    return t2.compareTo(t1); // Descending
                });
            } else if ("Least".equals(agreementTotalFilter)) {
                filteredAgreements.sort((a1, a2) -> {
                    BigDecimal t1 = a1.getPricePerYear() != null ? a1.getPricePerYear() : BigDecimal.ZERO;
                    BigDecimal t2 = a2.getPricePerYear() != null ? a2.getPricePerYear() : BigDecimal.ZERO;
                    return t1.compareTo(t2); // Ascending
                });
            }

            // Populate table with filtered and sorted results
            // Columns: Agreement #, Client, Job Location, Start Date, Visit Frequency, End Date, Agreement Total, Status, Bill Location
            tableModel.setRowCount(0);
            for (PreventiveMaintenanceAgreement pma : filteredAgreements) {
                tableModel.addRow(new Object[]{
                    pma.getPreventiveMaintenanceAgreementId(),
                    pma.getAgreementNumber(),
                    getClientName(pma.getClientId()),
                    getLocationName(pma.getClientJobLocationId()),
                    formatDate(pma.getStartDate()),
                    pma.getVisitFrequency(),
                    formatDate(pma.getEndDate()),
                    pma.getPricePerYear(),
                    pma.getStatus(),
                    getLocationName(pma.getClientBillingLocationId())
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

            // Load equipment
            equipments.clear();
            equipments.addAll(equipmentDAO.getEquipmentByAgreementId(selectedPMA.getPreventiveMaintenanceAgreementId()));
            refreshEquipmentsTable();

            updateDerivedFields();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading quote: " + ex.getMessage());
        }
    }

    private void addAgreement() {
        if (!validateForm()) return;

        PreventiveMaintenanceAgreement pma = new PreventiveMaintenanceAgreement();
        populateFromForm(pma);

        try {
            if (pmaDAO.addAgreement(pma)) {
                saveEquipments(pma.getPreventiveMaintenanceAgreementId());
                JOptionPane.showMessageDialog(this, "Preventive Maintenance Quote added successfully!");
                clearForm();
                loadAgreements();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding quote: " + ex.getMessage());
        }
    }

    private void updateAgreement() {
        if (selectedPMA == null) return;
        if (!validateForm()) return;

        populateFromForm(selectedPMA);

        try {
            if (pmaDAO.updateAgreement(selectedPMA)) {
                saveEquipments(selectedPMA.getPreventiveMaintenanceAgreementId());
                JOptionPane.showMessageDialog(this, "Preventive Maintenance Quote updated successfully!");
                clearForm();
                loadAgreements();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating quote: " + ex.getMessage());
        }
    }

    private void deleteAgreement() {
        if (selectedPMA == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this Preventive Maintenance Quote?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (pmaDAO.deleteAgreement(selectedPMA.getPreventiveMaintenanceAgreementId())) {
                JOptionPane.showMessageDialog(this, "Preventive Maintenance Quote deleted successfully!");
                clearForm();
                loadAgreements();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting quote: " + ex.getMessage());
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
            JOptionPane.showMessageDialog(this, "Quote number is required.");
            return false;
        }

        if (equipments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Add at least one equipment.");
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
        billLocationCombo.setSelectedIndex(-1);
        jobLocationCombo.setSelectedIndex(-1);
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
        equipments.clear();
        refreshEquipmentsTable();
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
                return;
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

    private String getLocationName(Integer locationId) {
        if (locationId == null) return "Unknown";
        try {
            ClientLocation location = locationDAO.getClientLocationById(locationId);
            return location == null ? "Unknown" : location.toString();
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

    private void addSectionLabel(JPanel panel, GridBagConstraints gbc, int row, String text) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(java.awt.Font.BOLD, 13f));
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 0, 4, 0)
        ));
        panel.add(label, gbc);
        gbc.gridwidth = 1;
    }

    private void addEquipment() {
        JTextField equipmentTypeField = new JTextField(30);
        JTextField makeField = new JTextField(30);
        JTextField modelField = new JTextField(30);
        JTextField serialNumberField = new JTextField(30);

        JPanel equipmentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        equipmentPanel.add(new JLabel("Equipment Type:*"), gbc);
        gbc.gridx = 1;
        equipmentPanel.add(equipmentTypeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        equipmentPanel.add(new JLabel("Make:*"), gbc);
        gbc.gridx = 1;
        equipmentPanel.add(makeField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        equipmentPanel.add(new JLabel("Model:*"), gbc);
        gbc.gridx = 1;
        equipmentPanel.add(modelField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        equipmentPanel.add(new JLabel("Serial Number:*"), gbc);
        gbc.gridx = 1;
        equipmentPanel.add(serialNumberField, gbc);

        int result = JOptionPane.showConfirmDialog(this, equipmentPanel, "Add Equipment", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String equipmentType = equipmentTypeField.getText().trim();
            String make = makeField.getText().trim();
            String model = modelField.getText().trim();
            String serialNumber = serialNumberField.getText().trim();

            if (equipmentType.isEmpty() || make.isEmpty() || model.isEmpty() || serialNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.");
                return;
            }

            PMAgreementEquipment equipment = new PMAgreementEquipment();
            equipment.setRowNumber(equipments.size() + 1);
            equipment.setEquipmentType(equipmentType);
            equipment.setMake(make);
            equipment.setModel(model);
            equipment.setSerialNumber(serialNumber);
            equipments.add(equipment);
            refreshEquipmentsTable();
        }
    }

    private void editEquipment() {
        int selectedRow = equipmentsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select an equipment to edit.");
            return;
        }
        PMAgreementEquipment equipment = equipments.get(selectedRow);

        JTextField equipmentTypeField = new JTextField(equipment.getEquipmentType(), 30);
        JTextField makeField = new JTextField(equipment.getMake(), 30);
        JTextField modelField = new JTextField(equipment.getModel(), 30);
        JTextField serialNumberField = new JTextField(equipment.getSerialNumber(), 30);

        JPanel equipmentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        equipmentPanel.add(new JLabel("Equipment Type:*"), gbc);
        gbc.gridx = 1;
        equipmentPanel.add(equipmentTypeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        equipmentPanel.add(new JLabel("Make:*"), gbc);
        gbc.gridx = 1;
        equipmentPanel.add(makeField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        equipmentPanel.add(new JLabel("Model:*"), gbc);
        gbc.gridx = 1;
        equipmentPanel.add(modelField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        equipmentPanel.add(new JLabel("Serial Number:*"), gbc);
        gbc.gridx = 1;
        equipmentPanel.add(serialNumberField, gbc);

        int result = JOptionPane.showConfirmDialog(this, equipmentPanel, "Edit Equipment", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String equipmentType = equipmentTypeField.getText().trim();
            String make = makeField.getText().trim();
            String model = modelField.getText().trim();
            String serialNumber = serialNumberField.getText().trim();

            if (equipmentType.isEmpty() || make.isEmpty() || model.isEmpty() || serialNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.");
                return;
            }

            equipment.setEquipmentType(equipmentType);
            equipment.setMake(make);
            equipment.setModel(model);
            equipment.setSerialNumber(serialNumber);
            refreshEquipmentsTable();
        }
    }

    private void deleteEquipment() {
        int selectedRow = equipmentsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select an equipment to delete.");
            return;
        }
        equipments.remove(selectedRow);
        for (int i = 0; i < equipments.size(); i++) {
            equipments.get(i).setRowNumber(i + 1);
        }
        refreshEquipmentsTable();
    }

    private void refreshEquipmentsTable() {
        equipmentsTableModel.setRowCount(0);
        for (PMAgreementEquipment equipment : equipments) {
            equipmentsTableModel.addRow(new Object[]{
                equipment.getRowNumber(),
                equipment.getEquipmentType(),
                equipment.getMake(),
                equipment.getModel(),
                equipment.getSerialNumber()
            });
        }
    }

    private void saveEquipments(Integer agreementId) throws SQLException {
        if (agreementId == null) return;
        equipmentDAO.deleteEquipmentByAgreementId(agreementId);
        for (PMAgreementEquipment equipment : equipments) {
            equipment.setPreventiveMaintenanceAgreementId(agreementId);
            equipmentDAO.addEquipment(equipment);
        }
    }

    public void refreshData() {
        loadClients();
        loadAgreements();
    }

    /**
     * Auto-format MM/dd/yyyy as user types and cap at 8 digits.
     */
    private void setupDateFormatting(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                applyDateFormatting(fb, offset, length, text, attrs);
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                applyDateFormatting(fb, offset, length, "", null);
            }

            private void applyDateFormatting(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (isUpdatingDate) {
                    super.replace(fb, offset, length, text, attrs);
                    return;
                }

                String current = fb.getDocument().getText(0, fb.getDocument().getLength());
                String next = current.substring(0, offset) + (text == null ? "" : text) +
                              current.substring(offset + length);
                String digits = next.replaceAll("[^0-9]", "");
                if (digits.length() > 8) {
                    digits = digits.substring(0, 8);
                }

                String formatted = formatDateDigits(digits);
                isUpdatingDate = true;
                fb.remove(0, fb.getDocument().getLength());
                fb.insertString(0, formatted, attrs);
                isUpdatingDate = false;
            }
        });
    }

    private String formatDateDigits(String digits) {
        if (digits.isEmpty()) return "";
        if (digits.length() <= 2) return digits;
        if (digits.length() <= 4) {
            return digits.substring(0, 2) + "/" + digits.substring(2);
        }
        return digits.substring(0, 2) + "/" + digits.substring(2, 4) + "/" + digits.substring(4);
    }
}
