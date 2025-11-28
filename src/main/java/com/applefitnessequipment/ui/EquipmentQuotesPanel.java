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
import com.applefitnessequipment.dao.EquipmentQuoteDAO;
import com.applefitnessequipment.dao.EquipmentQuoteItemDAO;
import com.applefitnessequipment.model.Client;
import com.applefitnessequipment.model.ClientLocation;
import com.applefitnessequipment.model.EquipmentQuote;
import com.applefitnessequipment.model.EquipmentQuoteItem;

/**
 * UI for EquipmentQuotes aligned to applefitnessequipmentdb_schema.sql.
 */
public class EquipmentQuotesPanel extends JPanel {
    private final EquipmentQuoteDAO quoteDAO;
    private final EquipmentQuoteItemDAO quoteItemDAO;
    private final ClientDAO clientDAO;
    private final ClientLocationDAO locationDAO;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private boolean isUpdatingDate = false;

    private JTable quotesTable;
    private DefaultTableModel tableModel;

    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;
    private JComboBox<String> totalFilterCombo;

    // NOTE: now a plain JComboBox, not AutocompleteComboBox
    private JComboBox<Client> clientCombo;
    private JComboBox<ClientLocation> billLocationCombo;
    private JComboBox<ClientLocation> jobLocationCombo;
    private JTextField quoteNumberField;
    private JTextField quoteDateField;
    private JComboBox<String> statusCombo;
    private JTextField contactNameField;
    private JTextField salespersonField;
    private JTextField shipViaField;
    private JTextField freightTermsField;
    private JTextField paymentTermsField;
    private JTextField fobField;
    private JTextField subtotalField;
    private JTextField discountField;
    private JTextField freightField;
    private JTextField taxRateField;
    private JTextField extendedTotalField;
    private JTextField taxAmountField;
    private JTextField quoteTotalField;
    private JCheckBox signatureCheckbox;

    private JTable quoteItemsTable;
    private DefaultTableModel itemsTableModel;
    private final List<EquipmentQuoteItem> quoteItems = new ArrayList<>();

    private EquipmentQuote selectedQuote;

    public EquipmentQuotesPanel() {
        this.quoteDAO = new EquipmentQuoteDAO();
        this.quoteItemDAO = new EquipmentQuoteItemDAO();
        this.clientDAO = new ClientDAO();
        this.locationDAO = new ClientLocationDAO();

        initComponents();
        loadClients();
        loadQuotes();
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
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterQuotes(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterQuotes(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterQuotes(); }
        });
        topPanel.add(searchField);

        topPanel.add(javax.swing.Box.createHorizontalStrut(20));

        // Filter by Status
        topPanel.add(new JLabel("Filter by Status:"));
        statusFilterCombo = new JComboBox<>(new String[]{"Show All", "Draft", "Sent", "Expired", "Active", "Declined", "Canceled", "Completed"});
        statusFilterCombo.setFont(ModernUIHelper.NORMAL_FONT);
        statusFilterCombo.setPreferredSize(new java.awt.Dimension(120, 38));
        statusFilterCombo.addActionListener(e -> filterQuotes());
        topPanel.add(statusFilterCombo);

        // Filter by Quote Total
        topPanel.add(new JLabel("Filter by Quote Total:"));
        totalFilterCombo = new JComboBox<>(new String[]{"Show All", "Most", "Least"});
        totalFilterCombo.setFont(ModernUIHelper.NORMAL_FONT);
        totalFilterCombo.setPreferredSize(new java.awt.Dimension(120, 38));
        totalFilterCombo.addActionListener(e -> filterQuotes());
        topPanel.add(totalFilterCombo);

        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Quote #", "Client", "Job Location", "Quote Date", "Quote Total", "Status", "Bill Location"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        quotesTable = new JTable(tableModel);
        quotesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ModernUIHelper.styleTable(quotesTable);
        ModernUIHelper.addTableToggleBehavior(quotesTable, this::clearForm);

        quotesTable.getColumnModel().getColumn(0).setMinWidth(0);
        quotesTable.getColumnModel().getColumn(0).setMaxWidth(0);
        quotesTable.getColumnModel().getColumn(0).setWidth(0);

        // Set column widths: Quote # (9), Quote Date (10), Status (7)
        quotesTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        quotesTable.getColumnModel().getColumn(1).setMaxWidth(90);
        quotesTable.getColumnModel().getColumn(4).setPreferredWidth(100);  // Quote Date
        quotesTable.getColumnModel().getColumn(4).setMaxWidth(110);
        quotesTable.getColumnModel().getColumn(5).setPreferredWidth(95);  // Quote Total
        quotesTable.getColumnModel().getColumn(5).setMaxWidth(105);
        quotesTable.getColumnModel().getColumn(6).setPreferredWidth(70);  // Status
        quotesTable.getColumnModel().getColumn(6).setMaxWidth(80);

        quotesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && quotesTable.getSelectedRow() >= 0) {
                loadSelectedQuote();
            }
        });

        JScrollPane tableScroll = new JScrollPane(quotesTable);
        add(tableScroll, BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(ModernUIHelper.createModernBorder("Equipment Quote Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        addSectionLabel(formPanel, gbc, row++, "CLIENT INFO");

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Client:*"), gbc);
        gbc.gridx = 1;
        clientCombo = new JComboBox<>();
        clientCombo.setEditable(false); // non-typable dropdown
        clientCombo.addActionListener(e -> loadLocationsForClient());
        ModernUIHelper.styleComboBox(clientCombo);
        formPanel.add(clientCombo, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Billing Location:*"), gbc);
        gbc.gridx = 1;
        billLocationCombo = new JComboBox<>();
        ModernUIHelper.styleComboBox(billLocationCombo);
        formPanel.add(billLocationCombo, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Job Location:*"), gbc);
        gbc.gridx = 1;
        jobLocationCombo = new JComboBox<>();
        ModernUIHelper.styleComboBox(jobLocationCombo);
        formPanel.add(jobLocationCombo, gbc);
        row++;

        addSectionLabel(formPanel, gbc, row++, "QUOTE INFO");

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Quote #*"), gbc);
        gbc.gridx = 1;
        quoteNumberField = new JTextField(20);
        ModernUIHelper.styleTextField(quoteNumberField);
        formPanel.add(quoteNumberField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Quote Date (MM/dd/yyyy):*"), gbc);
        gbc.gridx = 1;
        quoteDateField = new JTextField(20);
        ModernUIHelper.styleTextField(quoteDateField);
        setupDateFormatting(quoteDateField);
        formPanel.add(quoteDateField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Status:*"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(new String[]{"Draft", "Sent", "Expired", "Active", "Declined", "Canceled", "Completed"});
        ModernUIHelper.styleComboBox(statusCombo);
        formPanel.add(statusCombo, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Contact Name:*"), gbc);
        gbc.gridx = 1;
        contactNameField = new JTextField(20);
        ModernUIHelper.styleTextField(contactNameField);
        formPanel.add(contactNameField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Salesperson:*"), gbc);
        gbc.gridx = 1;
        salespersonField = new JTextField(20);
        ModernUIHelper.styleTextField(salespersonField);
        formPanel.add(salespersonField, gbc);
        row++;

        addSectionLabel(formPanel, gbc, row++, "SHIPPING & PAYMENT");

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Ship Via:*"), gbc);
        gbc.gridx = 1;
        shipViaField = new JTextField(20);
        ModernUIHelper.styleTextField(shipViaField);
        formPanel.add(shipViaField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Freight Terms:*"), gbc);
        gbc.gridx = 1;
        freightTermsField = new JTextField(20);
        ModernUIHelper.styleTextField(freightTermsField);
        formPanel.add(freightTermsField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Payment Terms:*"), gbc);
        gbc.gridx = 1;
        paymentTermsField = new JTextField(20);
        ModernUIHelper.styleTextField(paymentTermsField);
        formPanel.add(paymentTermsField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("FOB Location:*"), gbc);
        gbc.gridx = 1;
        fobField = new JTextField(20);
        ModernUIHelper.styleTextField(fobField);
        formPanel.add(fobField, gbc);
        row++;

        addSectionLabel(formPanel, gbc, row++, "AMOUNTS");

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Subtotal:"), gbc);
        gbc.gridx = 1;
        subtotalField = new JTextField(20);
        subtotalField.setEditable(false);
        subtotalField.setFocusable(true);
        subtotalField.setBackground(new java.awt.Color(240, 240, 240));
        ModernUIHelper.styleTextField(subtotalField);
        formPanel.add(subtotalField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Total Discount:"), gbc);
        gbc.gridx = 1;
        discountField = new JTextField(20);
        discountField.setEditable(false);
        discountField.setFocusable(true);
        discountField.setBackground(new java.awt.Color(240, 240, 240));
        ModernUIHelper.styleTextField(discountField);
        formPanel.add(discountField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Freight:"), gbc);
        gbc.gridx = 1;
        freightField = new JTextField(20);
        ModernUIHelper.styleTextField(freightField);
        formPanel.add(freightField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Tax Rate %:*"), gbc);
        gbc.gridx = 1;
        taxRateField = new JTextField(20);
        ModernUIHelper.styleTextField(taxRateField);
        formPanel.add(taxRateField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Extended Total:"), gbc);
        gbc.gridx = 1;
        extendedTotalField = new JTextField(20);
        extendedTotalField.setEditable(false);
        extendedTotalField.setFocusable(true);
        extendedTotalField.setBackground(new java.awt.Color(240, 240, 240));
        ModernUIHelper.styleTextField(extendedTotalField);
        formPanel.add(extendedTotalField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Sales Tax:"), gbc);
        gbc.gridx = 1;
        taxAmountField = new JTextField(20);
        taxAmountField.setEditable(false);
        taxAmountField.setFocusable(true);
        taxAmountField.setBackground(new java.awt.Color(240, 240, 240));
        ModernUIHelper.styleTextField(taxAmountField);
        formPanel.add(taxAmountField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Quote Total:"), gbc);
        gbc.gridx = 1;
        quoteTotalField = new JTextField(20);
        quoteTotalField.setEditable(false);
        quoteTotalField.setFocusable(true);
        quoteTotalField.setBackground(new java.awt.Color(240, 240, 240));
        ModernUIHelper.styleTextField(quoteTotalField);
        formPanel.add(quoteTotalField, gbc);
        row++;

        // Items section
        addSectionLabel(formPanel, gbc, row++, "QUOTE ITEMS");

        itemsTableModel = new DefaultTableModel(
                new String[]{"Row #", "Qty", "Model", "Description", "Unit Cost", "Disc Price", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int rowIdx, int column) {
                return false;
            }
        };
        quoteItemsTable = new JTable(itemsTableModel);
        ModernUIHelper.addTableToggleBehavior(quoteItemsTable);
        JScrollPane itemsScroll = new JScrollPane(quoteItemsTable);
        itemsScroll.setPreferredSize(new Dimension(450, 150));
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(itemsScroll, gbc);
        gbc.gridwidth = 1;
        row++;

        JPanel itemButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addItemButton = new JButton("Add Item");
        addItemButton.addActionListener(e -> addQuoteItem());
        ModernUIHelper.styleButton(addItemButton, "success");
        itemButtons.add(addItemButton);

        JButton editItemButton = new JButton("Edit Item");
        editItemButton.addActionListener(e -> editQuoteItem());
        ModernUIHelper.styleButton(editItemButton, "primary");
        itemButtons.add(editItemButton);

        JButton deleteItemButton = new JButton("Delete Item");
        deleteItemButton.addActionListener(e -> deleteQuoteItem());
        ModernUIHelper.styleButton(deleteItemButton, "danger");
        itemButtons.add(deleteItemButton);

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(itemButtons, gbc);
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
        addButton.addActionListener(e -> addQuote());
        ModernUIHelper.styleButton(addButton, "success");
        buttonPanel.add(addButton);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateQuote());
        ModernUIHelper.styleButton(updateButton, "warning");
        buttonPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteQuote());
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

        DocumentListener calcListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { recalculateDerivedTotals(); }
            public void removeUpdate(DocumentEvent e) { recalculateDerivedTotals(); }
            public void changedUpdate(DocumentEvent e) { recalculateDerivedTotals(); }
        };
        freightField.getDocument().addDocumentListener(calcListener);
        taxRateField.getDocument().addDocumentListener(calcListener);

        clearForm();
    }

    private void loadClients() {
        try {
            List<Client> clients = clientDAO.getAllClients();
            clientCombo.removeAllItems();
            for (Client c : clients) {
                clientCombo.addItem(c);
            }
            // clear selection
            clientCombo.setSelectedItem(null);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading clients: " + ex.getMessage());
        }
    }

    private void loadLocationsForClient() {
        Client client = (Client) clientCombo.getSelectedItem();
        billLocationCombo.removeAllItems();
        jobLocationCombo.removeAllItems();

        if (client == null) return;

        try {
            List<ClientLocation> locations = locationDAO.getLocationsByClientId(client.getClientId());
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

    private void loadQuotes() {
        filterQuotes();
    }

    private void filterQuotes() {
        String searchText = searchField.getText().toLowerCase().trim();
        String statusFilter = (String) statusFilterCombo.getSelectedItem();
        String totalFilter = (String) totalFilterCombo.getSelectedItem();

        try {
            List<EquipmentQuote> quotes = quoteDAO.getAllQuotes();
            List<EquipmentQuote> filteredQuotes = new ArrayList<>();

            for (EquipmentQuote quote : quotes) {
                // Search across multiple fields
                String quoteNumber = quote.getQuoteNumber() != null ? quote.getQuoteNumber().toLowerCase() : "";
                String clientName = getClientName(quote.getClientId()).toLowerCase();
                String status = quote.getStatus() != null ? quote.getStatus().toLowerCase() : "";
                String jobLocation = getLocationName(quote.getClientJobLocationId()).toLowerCase();
                String billLocation = getLocationName(quote.getClientBillingLocationId()).toLowerCase();

                boolean searchMatches = searchText.isEmpty() ||
                    quoteNumber.contains(searchText) ||
                    clientName.contains(searchText) ||
                    status.contains(searchText) ||
                    jobLocation.contains(searchText) ||
                    billLocation.contains(searchText);

                // Apply status filter
                boolean statusMatches = "Show All".equals(statusFilter) ||
                    statusFilter.equalsIgnoreCase(quote.getStatus());

                if (searchMatches && statusMatches) {
                    filteredQuotes.add(quote);
                }
            }

            // Sort by Quote Total if needed
            if ("Most".equals(totalFilter)) {
                filteredQuotes.sort((q1, q2) -> {
                    BigDecimal t1 = q1.getQuoteTotalAmount() != null ? q1.getQuoteTotalAmount() : BigDecimal.ZERO;
                    BigDecimal t2 = q2.getQuoteTotalAmount() != null ? q2.getQuoteTotalAmount() : BigDecimal.ZERO;
                    return t2.compareTo(t1); // Descending
                });
            } else if ("Least".equals(totalFilter)) {
                filteredQuotes.sort((q1, q2) -> {
                    BigDecimal t1 = q1.getQuoteTotalAmount() != null ? q1.getQuoteTotalAmount() : BigDecimal.ZERO;
                    BigDecimal t2 = q2.getQuoteTotalAmount() != null ? q2.getQuoteTotalAmount() : BigDecimal.ZERO;
                    return t1.compareTo(t2); // Ascending
                });
            }

            // Populate table with filtered and sorted results
            tableModel.setRowCount(0);
            for (EquipmentQuote quote : filteredQuotes) {
                tableModel.addRow(new Object[]{
                        quote.getEquipmentQuoteId(),
                        quote.getQuoteNumber(),
                        getClientName(quote.getClientId()),
                        getLocationName(quote.getClientJobLocationId()),
                        quote.getQuoteDate() != null ? quote.getQuoteDate().format(dateFormatter) : "",
                        quote.getQuoteTotalAmount(),
                        quote.getStatus(),
                        getLocationName(quote.getClientBillingLocationId())
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading quotes: " + ex.getMessage());
        }
    }

    private void loadSelectedQuote() {
        int row = quotesTable.getSelectedRow();
        if (row < 0) return;

        Integer id = (Integer) tableModel.getValueAt(row, 0);
        try {
            selectedQuote = quoteDAO.getQuoteById(id);
            if (selectedQuote == null) return;

            selectClientById(selectedQuote.getClientId());
            loadLocationsForClient();
            selectLocation(billLocationCombo, selectedQuote.getClientBillingLocationId());
            selectLocation(jobLocationCombo, selectedQuote.getClientJobLocationId());

            quoteNumberField.setText(selectedQuote.getQuoteNumber());
            quoteDateField.setText(selectedQuote.getQuoteDate() != null ? selectedQuote.getQuoteDate().format(dateFormatter) : "");
            statusCombo.setSelectedItem(selectedQuote.getStatus());
            contactNameField.setText(selectedQuote.getContactName());
            salespersonField.setText(selectedQuote.getSalespersonName());
            shipViaField.setText(selectedQuote.getShipVia());
            freightTermsField.setText(selectedQuote.getFreightTerms());
            paymentTermsField.setText(selectedQuote.getPaymentTerms());
            fobField.setText(selectedQuote.getFobLocation());
            subtotalField.setText("0.00");
            discountField.setText("0.00");
            freightField.setText(valueOrDefault(selectedQuote.getFreightAmount(), "0.00"));
            taxRateField.setText(valueOrDefault(selectedQuote.getSalesTaxRatePercent(), "6.00"));
            signatureCheckbox.setSelected(Boolean.TRUE.equals(selectedQuote.getClientSignatureBoolean()));

            // Generated columns from DB
            extendedTotalField.setText(valueOrDefault(selectedQuote.getExtendedTotalAmount(), ""));
            taxAmountField.setText(valueOrDefault(selectedQuote.getSalesTaxAmount(), ""));
            quoteTotalField.setText(valueOrDefault(selectedQuote.getQuoteTotalAmount(), ""));

            // Load quote items
            quoteItems.clear();
            quoteItems.addAll(quoteItemDAO.getItemsByQuoteId(selectedQuote.getEquipmentQuoteId()));
            refreshItemsTable();

            recalculateSubtotalFromItems();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading quote: " + ex.getMessage());
        }
    }

    private void addQuote() {
        if (!validateForm()) return;

        EquipmentQuote quote = new EquipmentQuote();
        populateFromForm(quote);

        try {
            if (quoteDAO.addQuote(quote)) {
                saveQuoteItems(quote.getEquipmentQuoteId());
                JOptionPane.showMessageDialog(this, "Quote added successfully!");
                clearForm();
                loadQuotes();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding quote: " + ex.getMessage());
        }
    }

    private void updateQuote() {
        if (selectedQuote == null) return;
        if (!validateForm()) return;

        populateFromForm(selectedQuote);

        try {
            if (quoteDAO.updateQuote(selectedQuote)) {
                saveQuoteItems(selectedQuote.getEquipmentQuoteId());
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
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (quoteDAO.deleteQuote(selectedQuote.getEquipmentQuoteId())) {
                JOptionPane.showMessageDialog(this, "Quote deleted successfully!");
                clearForm();
                loadQuotes();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting quote: " + ex.getMessage());
        }
    }

    private void populateFromForm(EquipmentQuote quote) {
        Client client = (Client) clientCombo.getSelectedItem();
        ClientLocation bill = (ClientLocation) billLocationCombo.getSelectedItem();
        ClientLocation job = (ClientLocation) jobLocationCombo.getSelectedItem();

        quote.setClientId(client.getClientId());
        quote.setClientBillingLocationId(bill.getClientLocationId());
        quote.setClientJobLocationId(job.getClientLocationId());
        quote.setQuoteNumber(quoteNumberField.getText().trim());
        quote.setQuoteDate(LocalDate.parse(quoteDateField.getText().trim(), dateFormatter));
        quote.setStatus((String) statusCombo.getSelectedItem());
        quote.setContactName(contactNameField.getText().trim());
        quote.setSalespersonName(salespersonField.getText().trim());
        quote.setShipVia(shipViaField.getText().trim());
        quote.setFreightTerms(freightTermsField.getText().trim());
        quote.setPaymentTerms(paymentTermsField.getText().trim());
        quote.setFobLocation(fobField.getText().trim());
        quote.setSubtotalAmount(calculateSubtotalFromItems());
        quote.setTotalDiscountAmount(calculateDiscountFromItems());
        quote.setFreightAmount(new BigDecimal(freightField.getText().trim()));
        quote.setSalesTaxRatePercent(new BigDecimal(taxRateField.getText().trim()));
        quote.setClientSignatureBoolean(signatureCheckbox.isSelected());
    }

    private boolean validateForm() {
        recalculateSubtotalFromItems();

        if (clientCombo.getSelectedItem() == null ||
            billLocationCombo.getSelectedItem() == null ||
            jobLocationCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select client and locations.");
            return false;
        }
        if (quoteNumberField.getText().trim().isEmpty() ||
            quoteDateField.getText().trim().isEmpty() ||
            contactNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Quote number, date, and contact name are required.");
            return false;
        }
        if (quoteItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Add at least one quote item.");
            return false;
        }
        try {
            LocalDate.parse(quoteDateField.getText().trim(), dateFormatter);
            new BigDecimal(subtotalField.getText().trim());
            new BigDecimal(freightField.getText().trim());
            new BigDecimal(taxRateField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date or number format. Use MM/dd/yyyy for dates.");
            return false;
        }
        return true;
    }

    private void clearForm() {
        clientCombo.setSelectedItem(null);
        billLocationCombo.removeAllItems();
        jobLocationCombo.removeAllItems();
        quoteNumberField.setText("");
        quoteDateField.setText(LocalDate.now().format(dateFormatter));
        statusCombo.setSelectedItem("Draft");
        contactNameField.setText("");
        salespersonField.setText("Greg Bartram");
        shipViaField.setText("AFE Truck/Trailer");
        freightTermsField.setText("Ppd & Add");
        paymentTermsField.setText("See Notes");
        fobField.setText("Truck Curbside");
        subtotalField.setText("0.00");
        discountField.setText("0.00");
        freightField.setText("0.00");
        taxRateField.setText("6.00");
        extendedTotalField.setText("");
        taxAmountField.setText("");
        quoteTotalField.setText("");
        signatureCheckbox.setSelected(false);
        quoteItems.clear();
        refreshItemsTable();
        selectedQuote = null;
        quotesTable.clearSelection();
        recalculateDerivedTotals();
        billLocationCombo.setSelectedIndex(-1);
        jobLocationCombo.setSelectedIndex(-1);
    }

    private void recalculateDerivedTotals() {
        try {
            BigDecimal subtotal = new BigDecimal(subtotalField.getText().trim());
            BigDecimal freight = new BigDecimal(freightField.getText().trim());
            BigDecimal taxRate = new BigDecimal(taxRateField.getText().trim());

            // Extended Total = Subtotal + Freight (discount already applied in subtotal)
            BigDecimal extendedTotal = subtotal.add(freight);
            BigDecimal taxAmount = extendedTotal.multiply(taxRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal quoteTotal = extendedTotal.add(taxAmount);

            extendedTotalField.setText(extendedTotal.setScale(2, RoundingMode.HALF_UP).toString());
            taxAmountField.setText(taxAmount.toString());
            quoteTotalField.setText(quoteTotal.setScale(2, RoundingMode.HALF_UP).toString());
        } catch (Exception e) {
            extendedTotalField.setText("");
            taxAmountField.setText("");
            quoteTotalField.setText("");
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
            return client == null ? "Unknown" : client.toString();
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

    private void addQuoteItem() {
        JTextField qtyField = new JTextField(10);
        JTextField modelField = new JTextField(30);
        JTextField descriptionField = new JTextField(30);
        JTextField unitCostField = new JTextField(10);
        JTextField discPriceField = new JTextField(10);

        qtyField.setText("1.00");
        unitCostField.setText("0.00");
        discPriceField.setText("0.00");

        JPanel itemPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        itemPanel.add(new JLabel("Qty:*"), gbc);
        gbc.gridx = 1;
        itemPanel.add(qtyField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        itemPanel.add(new JLabel("Model:"), gbc);
        gbc.gridx = 1;
        itemPanel.add(modelField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        itemPanel.add(new JLabel("Description:*"), gbc);
        gbc.gridx = 1;
        itemPanel.add(descriptionField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        itemPanel.add(new JLabel("Unit Cost:*"), gbc);
        gbc.gridx = 1;
        itemPanel.add(unitCostField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        itemPanel.add(new JLabel("Disc Unit Price:*"), gbc);
        gbc.gridx = 1;
        itemPanel.add(discPriceField, gbc);

        int result = JOptionPane.showConfirmDialog(this, itemPanel, "Add Quote Item", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String description = descriptionField.getText().trim();
                if (description.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Description is required.");
                    return;
                }
                BigDecimal qty = new BigDecimal(qtyField.getText().trim());
                String model = modelField.getText().trim();
                BigDecimal unitCost = new BigDecimal(unitCostField.getText().trim());
                BigDecimal discPrice = new BigDecimal(discPriceField.getText().trim());

                // Validate that discounted price is not greater than unit cost
                if (discPrice.compareTo(unitCost) > 0) {
                    JOptionPane.showMessageDialog(this, "Disc Unit Price cannot be greater than Unit Cost.");
                    return;
                }

                BigDecimal total = qty.multiply(discPrice);

                EquipmentQuoteItem item = new EquipmentQuoteItem();
                item.setRowNumber(quoteItems.size() + 1);
                item.setQty(qty);
                item.setModel(model.isEmpty() ? null : model);
                item.setDescription(description);
                item.setUnitCost(unitCost);
                item.setDiscountUnitPrice(discPrice);
                item.setUnitTotal(total);
                quoteItems.add(item);
                refreshItemsTable();
                recalculateSubtotalFromItems();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity or price.");
            }
        }
    }

    private void editQuoteItem() {
        int selectedRow = quoteItemsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select an item to edit.");
            return;
        }
        EquipmentQuoteItem item = quoteItems.get(selectedRow);

        JTextField qtyField = new JTextField(item.getQty().toString(), 10);
        JTextField modelField = new JTextField(item.getModel() != null ? item.getModel() : "", 30);
        JTextField descriptionField = new JTextField(item.getDescription(), 30);
        JTextField unitCostField = new JTextField(item.getUnitCost().toString(), 10);
        JTextField discPriceField = new JTextField(item.getDiscountUnitPrice().toString(), 10);

        JPanel itemPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        itemPanel.add(new JLabel("Qty:*"), gbc);
        gbc.gridx = 1;
        itemPanel.add(qtyField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        itemPanel.add(new JLabel("Model:"), gbc);
        gbc.gridx = 1;
        itemPanel.add(modelField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        itemPanel.add(new JLabel("Description:*"), gbc);
        gbc.gridx = 1;
        itemPanel.add(descriptionField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        itemPanel.add(new JLabel("Unit Cost:*"), gbc);
        gbc.gridx = 1;
        itemPanel.add(unitCostField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        itemPanel.add(new JLabel("Disc Unit Price:*"), gbc);
        gbc.gridx = 1;
        itemPanel.add(discPriceField, gbc);

        int result = JOptionPane.showConfirmDialog(this, itemPanel, "Edit Quote Item", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String description = descriptionField.getText().trim();
                if (description.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Description is required.");
                    return;
                }

                BigDecimal unitCost = new BigDecimal(unitCostField.getText().trim());
                BigDecimal discPrice = new BigDecimal(discPriceField.getText().trim());

                // Validate that discounted price is not greater than unit cost
                if (discPrice.compareTo(unitCost) > 0) {
                    JOptionPane.showMessageDialog(this, "Disc Unit Price cannot be greater than Unit Cost.");
                    return;
                }

                item.setQty(new BigDecimal(qtyField.getText().trim()));
                item.setModel(modelField.getText().trim().isEmpty() ? null : modelField.getText().trim());
                item.setDescription(description);
                item.setUnitCost(unitCost);
                item.setDiscountUnitPrice(discPrice);
                item.setUnitTotal(item.getQty().multiply(item.getDiscountUnitPrice()));
                refreshItemsTable();
                recalculateSubtotalFromItems();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity or price.");
            }
        }
    }

    private void deleteQuoteItem() {
        int selectedRow = quoteItemsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select an item to delete.");
            return;
        }
        quoteItems.remove(selectedRow);
        for (int i = 0; i < quoteItems.size(); i++) {
            quoteItems.get(i).setRowNumber(i + 1);
        }
        refreshItemsTable();
        recalculateSubtotalFromItems();
    }

    private void refreshItemsTable() {
        itemsTableModel.setRowCount(0);
        for (EquipmentQuoteItem item : quoteItems) {
            itemsTableModel.addRow(new Object[]{
                    item.getRowNumber(),
                    item.getQty(),
                    item.getModel(),
                    item.getDescription(),
                    item.getUnitCost(),
                    item.getDiscountUnitPrice(),
                    item.getUnitTotal()
            });
        }
    }

    private void saveQuoteItems(Integer quoteId) throws SQLException {
        if (quoteId == null) return;
        quoteItemDAO.deleteItemsByQuoteId(quoteId);
        for (EquipmentQuoteItem item : quoteItems) {
            item.setEquipmentQuoteId(quoteId);
            quoteItemDAO.addItem(item);
        }
    }

    private BigDecimal calculateSubtotalFromItems() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (EquipmentQuoteItem item : quoteItems) {
            if (item.getUnitTotal() != null) {
                subtotal = subtotal.add(item.getUnitTotal());
            }
        }
        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDiscountFromItems() {
        BigDecimal discount = BigDecimal.ZERO;
        for (EquipmentQuoteItem item : quoteItems) {
            if (item.getQty() == null || item.getUnitCost() == null || item.getDiscountUnitPrice() == null) {
                continue;
            }
            BigDecimal diff = item.getUnitCost().subtract(item.getDiscountUnitPrice());
            if (diff.compareTo(BigDecimal.ZERO) < 0) {
                diff = BigDecimal.ZERO;
            }
            discount = discount.add(diff.multiply(item.getQty()));
        }
        return discount.setScale(2, RoundingMode.HALF_UP);
    }

    private void recalculateSubtotalFromItems() {
        BigDecimal subtotal = calculateSubtotalFromItems();
        BigDecimal discount = calculateDiscountFromItems();
        subtotalField.setText(subtotal.toString());
        discountField.setText(discount.toString());
        recalculateDerivedTotals();
    }

    public void refreshData() {
        loadClients();
        loadQuotes();
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
