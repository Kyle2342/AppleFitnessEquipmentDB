package com.applefitnessequipment.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.applefitnessequipment.dao.ClientDAO;
import com.applefitnessequipment.dao.ClientLocationDAO;
import com.applefitnessequipment.dao.EquipmentQuoteDAO;
import com.applefitnessequipment.dao.InvoiceDAO;
import com.applefitnessequipment.dao.InvoiceItemDAO;
import com.applefitnessequipment.dao.PMAgreementDAO;
import com.applefitnessequipment.model.Client;
import com.applefitnessequipment.model.ClientLocation;
import com.applefitnessequipment.model.EquipmentQuote;
import com.applefitnessequipment.model.Invoice;
import com.applefitnessequipment.model.InvoiceItem;
import com.applefitnessequipment.model.PreventiveMaintenanceAgreement;

public class InvoicesPanel extends JPanel {
    private InvoiceDAO invoiceDAO;
    private InvoiceItemDAO invoiceItemDAO;
    private ClientDAO clientDAO;
    private ClientLocationDAO locationDAO;
    private EquipmentQuoteDAO quoteDAO;
    private PMAgreementDAO pmaDAO;

    private JTable invoicesTable;
    private DefaultTableModel tableModel;
    private DateTimeFormatter displayDateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    // Form fields - Client
    private JComboBox<Client> clientCombo;
    private JComboBox<ClientLocation> billLocationCombo, jobLocationCombo;
    private JComboBox<EquipmentQuote> equipmentQuoteCombo;
    private JComboBox<PreventiveMaintenanceAgreement> pmaCombo;

    // Form fields - Invoice details
    private JTextField invoiceNumberField, poNumberField;
    private JTextField invoiceDateField, dueDateField, paidDateField;
    private JTextField termsField;
    private JComboBox<String> statusCombo;

    // Form fields - Financial
    private JTextField subtotalAmountField, taxRateField, taxAmountField;
    private JTextField totalAmountField, paymentsAppliedField, balanceDueField;

    // Form fields - Fees and interest
    private JTextField returnedCheckFeeField, interestPercentField;
    private JTextField interestStartDaysField, interestIntervalDaysField;

    // Invoice items
    private JTable invoiceItemsTable;
    private DefaultTableModel itemsTableModel;
    private List<InvoiceItem> invoiceItems;

    // Buttons
    private JButton addButton, updateButton, deleteButton, clearButton;
    private Invoice selectedInvoice;

    // Tracking
    private String originalPaymentsApplied = "0.00";
    private List<Client> allClients;
    private List<EquipmentQuote> allQuotes;
    private List<PreventiveMaintenanceAgreement> allAgreements;
    private List<ClientLocation> allBillLocations;
    private List<ClientLocation> allJobLocations;

    public InvoicesPanel() {
        invoiceDAO = new InvoiceDAO();
        invoiceItemDAO = new InvoiceItemDAO();
        clientDAO = new ClientDAO();
        locationDAO = new ClientLocationDAO();
        quoteDAO = new EquipmentQuoteDAO();
        pmaDAO = new PMAgreementDAO();
        invoiceItems = new ArrayList<>();

        initComponents();
        loadClients();
        loadQuotesAndAgreements();
        loadInvoices();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel - empty for now
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        add(topPanel, BorderLayout.NORTH);

        // Center Panel - Table
        String[] columns = {"ID", "Invoice #", "Client", "Invoice Date", "Due Date", "Status", "Total", "Balance Due"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        invoicesTable = new JTable(tableModel);
        invoicesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ModernUIHelper.styleTable(invoicesTable);
        ModernUIHelper.addTableToggleBehavior(invoicesTable, () -> clearForm());

        // Hide ID
        invoicesTable.getColumnModel().getColumn(0).setMinWidth(0);
        invoicesTable.getColumnModel().getColumn(0).setMaxWidth(0);
        invoicesTable.getColumnModel().getColumn(0).setWidth(0);

        invoicesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && invoicesTable.getSelectedRow() >= 0) {
                loadSelectedInvoice();
            }
        });

        JScrollPane scrollPane = new JScrollPane(invoicesTable);

        // Allow deselection by clicking on empty space in the scroll pane viewport (not on the table itself)
        scrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                // Only deselect if clicking in the viewport area (the gray empty space around the table)
                if (e.getComponent() == scrollPane && !invoicesTable.getBounds().contains(e.getPoint())) {
                    invoicesTable.clearSelection();
                    clearForm();
                }
            }
        });

        add(scrollPane, BorderLayout.CENTER);

        // Right Panel - Scrollable Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(ModernUIHelper.createModernBorder("Invoice Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        // ===== CLIENT INFORMATION =====
        addSectionLabel(formPanel, gbc, row++, "CLIENT INFORMATION");

        // Client
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Client:*"), gbc);
        gbc.gridx = 1;
        clientCombo = new JComboBox<>();
        clientCombo.setEditable(true);

        JTextField clientTextField = (JTextField) clientCombo.getEditor().getEditorComponent();
        clientTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER ||
                    e.getKeyCode() == java.awt.event.KeyEvent.VK_TAB) {
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    String text = clientTextField.getText();
                    filterClientCombo(text);
                });
            }
        });

        clientCombo.addActionListener(e -> {
            if (e != null && clientCombo.getSelectedItem() instanceof Client) {
                loadLocationsForClient();
                filterQuotesAndAgreementsForClient();
            }
        });

        ModernUIHelper.styleComboBox(clientCombo);
        formPanel.add(clientCombo, gbc);
        row++;

        // Bill To Location
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Bill To Location:*"), gbc);
        gbc.gridx = 1;
        billLocationCombo = new JComboBox<>();
        billLocationCombo.setEditable(true);
        ModernUIHelper.styleComboBox(billLocationCombo);
        formPanel.add(billLocationCombo, gbc);
        row++;

        // Job At Location
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Job At Location:*"), gbc);
        gbc.gridx = 1;
        jobLocationCombo = new JComboBox<>();
        jobLocationCombo.setEditable(true);
        ModernUIHelper.styleComboBox(jobLocationCombo);
        formPanel.add(jobLocationCombo, gbc);
        row++;

        // Equipment Quote
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Equipment Quote:"), gbc);
        gbc.gridx = 1;
        equipmentQuoteCombo = new JComboBox<>();
        equipmentQuoteCombo.setEditable(true);
        equipmentQuoteCombo.addActionListener(e -> {
            if (equipmentQuoteCombo.getSelectedItem() != null &&
                equipmentQuoteCombo.getSelectedItem() instanceof EquipmentQuote) {
                pmaCombo.setSelectedIndex(-1);
            }
        });
        ModernUIHelper.styleComboBox(equipmentQuoteCombo);
        formPanel.add(equipmentQuoteCombo, gbc);
        row++;

        // PMA
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("PM Agreement:"), gbc);
        gbc.gridx = 1;
        pmaCombo = new JComboBox<>();
        pmaCombo.setEditable(true);
        pmaCombo.addActionListener(e -> {
            if (pmaCombo.getSelectedItem() != null &&
                pmaCombo.getSelectedItem() instanceof PreventiveMaintenanceAgreement) {
                equipmentQuoteCombo.setSelectedIndex(-1);
            }
        });
        ModernUIHelper.styleComboBox(pmaCombo);
        formPanel.add(pmaCombo, gbc);
        row++;

        // ===== INVOICE DETAILS =====
        addSectionLabel(formPanel, gbc, row++, "INVOICE DETAILS");

        row = addField(formPanel, gbc, row, "Invoice Number:*", invoiceNumberField = new JTextField(20));
        row = addField(formPanel, gbc, row, "PO Number:", poNumberField = new JTextField(20));
        row = addField(formPanel, gbc, row, "Invoice Date (MM/dd/yyyy):*", invoiceDateField = new JTextField(20));
        row = addField(formPanel, gbc, row, "Due Date (MM/dd/yyyy):*", dueDateField = new JTextField(20));
        row = addField(formPanel, gbc, row, "Terms:*", termsField = new JTextField(20));

        // Status
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Status:*"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(new String[]{"Draft", "Open", "Paid", "Overdue", "Void"});
        statusCombo.addActionListener(e -> {
            updatePaidDate();
            String selectedStatus = (String) statusCombo.getSelectedItem();

            if ("Paid".equals(selectedStatus)) {
                paymentsAppliedField.setEditable(false);
                paymentsAppliedField.setBackground(java.awt.Color.WHITE);
                String totalText = totalAmountField.getText().trim();
                if (!totalText.isEmpty()) {
                    paymentsAppliedField.setText(totalText);
                }
            } else {
                paymentsAppliedField.setEditable(true);
                paymentsAppliedField.setText(originalPaymentsApplied);
            }
            calculateBalanceDue();
        });
        ModernUIHelper.styleComboBox(statusCombo);
        formPanel.add(statusCombo, gbc);
        row++;

        row = addField(formPanel, gbc, row, "Paid Date (MM/dd/yyyy):", paidDateField = new JTextField(20));

        // ===== FINANCIAL DETAILS =====
        addSectionLabel(formPanel, gbc, row++, "FINANCIAL DETAILS");

        subtotalAmountField = new JTextField(20);
        subtotalAmountField.setEditable(false);
        subtotalAmountField.setBackground(java.awt.Color.WHITE);
        row = addField(formPanel, gbc, row, "Subtotal Amount:", subtotalAmountField);

        row = addField(formPanel, gbc, row, "Tax Rate %:", taxRateField = new JTextField(20));

        taxAmountField = new JTextField(20);
        taxAmountField.setEditable(false);
        taxAmountField.setBackground(java.awt.Color.WHITE);
        row = addField(formPanel, gbc, row, "Tax Amount:", taxAmountField);

        totalAmountField = new JTextField(20);
        totalAmountField.setEditable(false);
        totalAmountField.setBackground(java.awt.Color.WHITE);
        row = addField(formPanel, gbc, row, "Total Amount:", totalAmountField);

        paymentsAppliedField = new JTextField(20);
        paymentsAppliedField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculateBalanceDue(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculateBalanceDue(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculateBalanceDue(); }
        });
        row = addField(formPanel, gbc, row, "Payments Applied:", paymentsAppliedField);

        balanceDueField = new JTextField(20);
        balanceDueField.setEditable(false);
        balanceDueField.setBackground(java.awt.Color.WHITE);
        row = addField(formPanel, gbc, row, "Balance Due:", balanceDueField);

        // ===== FEES & INTEREST =====
        addSectionLabel(formPanel, gbc, row++, "FEES & INTEREST");

        row = addField(formPanel, gbc, row, "Returned Check Fee:*", returnedCheckFeeField = new JTextField(20));
        row = addField(formPanel, gbc, row, "Interest Percent:*", interestPercentField = new JTextField(20));
        row = addField(formPanel, gbc, row, "Interest Start Days:*", interestStartDaysField = new JTextField(20));
        row = addField(formPanel, gbc, row, "Interest Interval Days:*", interestIntervalDaysField = new JTextField(20));

        // ===== INVOICE ITEMS =====
        addSectionLabel(formPanel, gbc, row++, "INVOICE ITEMS");

        itemsTableModel = new DefaultTableModel(new String[]{"Row#", "Description", "Qty", "Rate", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        invoiceItemsTable = new JTable(itemsTableModel);
        ModernUIHelper.addTableToggleBehavior(invoiceItemsTable);
        invoiceItemsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        invoiceItemsTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        invoiceItemsTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        invoiceItemsTable.getColumnModel().getColumn(3).setPreferredWidth(70);
        invoiceItemsTable.getColumnModel().getColumn(4).setPreferredWidth(100);

        JScrollPane itemsScrollPane = new JScrollPane(invoiceItemsTable);
        itemsScrollPane.setPreferredSize(new Dimension(500, 150));
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.3;
        gbc.insets = new Insets(10, 10, 10, 10);
        formPanel.add(itemsScrollPane, gbc);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        row++;

        // Item buttons
        JPanel itemButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addItemButton = new JButton("Add Item");
        JButton editItemButton = new JButton("Edit Item");
        JButton deleteItemButton = new JButton("Delete Item");

        addItemButton.addActionListener(e -> addInvoiceItem());
        editItemButton.addActionListener(e -> editInvoiceItem());
        deleteItemButton.addActionListener(e -> deleteInvoiceItem());

        ModernUIHelper.styleButton(addItemButton, "success");
        ModernUIHelper.styleButton(editItemButton, "primary");
        ModernUIHelper.styleButton(deleteItemButton, "danger");

        itemButtonPanel.add(addItemButton);
        itemButtonPanel.add(editItemButton);
        itemButtonPanel.add(deleteItemButton);

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(itemButtonPanel, gbc);
        gbc.gridwidth = 1;
        row++;

        // Main action buttons
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());

        addButton = new JButton("Add");
        addButton.addActionListener(e -> addInvoice());
        ModernUIHelper.styleButton(addButton, "success");
        buttonPanel.add(addButton);

        updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateInvoice());
        updateButton.setEnabled(false);
        ModernUIHelper.styleButton(updateButton, "warning");
        buttonPanel.add(updateButton);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteInvoice());
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

    private void addSectionLabel(JPanel panel, GridBagConstraints gbc, int row, String text) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(java.awt.Font.BOLD, 13f));
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 0, 5, 0)
        ));
        panel.add(label, gbc);
        gbc.gridwidth = 1;
    }

    private int addField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JTextField field) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        ModernUIHelper.styleTextField(field);
        panel.add(field, gbc);
        return row + 1;
    }

    private void calculateBalanceDue() {
        try {
            String totalText = totalAmountField.getText().trim();
            String paymentsText = paymentsAppliedField.getText().trim();

            if (!totalText.isEmpty() && !paymentsText.isEmpty()) {
                BigDecimal total = new BigDecimal(totalText);
                BigDecimal payments = new BigDecimal(paymentsText);
                BigDecimal balance = total.subtract(payments);

                String status = (String) statusCombo.getSelectedItem();
                if ("Void".equals(status)) {
                    balanceDueField.setText("0.00");
                } else {
                    balanceDueField.setText(balance.setScale(2, java.math.RoundingMode.HALF_UP).toString());
                }
            }
        } catch (Exception e) {
            // Invalid input, ignore
        }
    }

    private void updatePaidDate() {
        String status = (String) statusCombo.getSelectedItem();
        if ("Paid".equals(status) && paidDateField.getText().trim().isEmpty()) {
            paidDateField.setText(LocalDate.now().format(displayDateFormatter));
        }
    }

    private void loadClients() {
        try {
            allClients = clientDAO.getAllClients();
            clientCombo.removeAllItems();
            for (Client client : allClients) {
                clientCombo.addItem(client);
            }
            clientCombo.setSelectedIndex(-1);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading clients: " + ex.getMessage());
        }
    }

    private void filterClientCombo(String searchText) {
        if (allClients == null) return;

        String search = searchText.toLowerCase().trim();

        java.awt.event.ActionListener[] listeners = clientCombo.getActionListeners();
        for (java.awt.event.ActionListener listener : listeners) {
            clientCombo.removeActionListener(listener);
        }

        clientCombo.removeAllItems();

        for (Client client : allClients) {
            String clientName = client.toString();
            if (search.isEmpty() || clientName.toLowerCase().contains(search)) {
                clientCombo.addItem(client);
            }
        }

        for (java.awt.event.ActionListener listener : listeners) {
            clientCombo.addActionListener(listener);
        }

        JTextField editor = (JTextField) clientCombo.getEditor().getEditorComponent();
        editor.setText(searchText);

        if (!search.isEmpty() && clientCombo.getItemCount() > 0) {
            clientCombo.showPopup();
        }
    }

    private void loadLocationsForClient() {
        Client selected = (Client) clientCombo.getSelectedItem();
        if (selected == null) return;

        try {
            List<ClientLocation> locations = locationDAO.getLocationsByClientId(selected.getClientId());

            allBillLocations = new ArrayList<>();
            allJobLocations = new ArrayList<>();

            billLocationCombo.removeAllItems();
            jobLocationCombo.removeAllItems();

            for (ClientLocation loc : locations) {
                if ("Billing".equals(loc.getLocationType())) {
                    allBillLocations.add(loc);
                    billLocationCombo.addItem(loc);
                } else if ("Job".equals(loc.getLocationType())) {
                    allJobLocations.add(loc);
                    jobLocationCombo.addItem(loc);
                }
            }

            billLocationCombo.setSelectedIndex(-1);
            jobLocationCombo.setSelectedIndex(-1);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading locations: " + ex.getMessage());
        }
    }

    private void loadQuotesAndAgreements() {
        try {
            allQuotes = quoteDAO.getAllQuotes();
            allAgreements = pmaDAO.getAllAgreements();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading quotes/agreements: " + ex.getMessage());
        }
    }

    private void filterQuotesAndAgreementsForClient() {
        Client selected = (Client) clientCombo.getSelectedItem();
        if (selected == null) {
            equipmentQuoteCombo.removeAllItems();
            pmaCombo.removeAllItems();
            return;
        }

        equipmentQuoteCombo.removeAllItems();
        if (allQuotes != null) {
            for (EquipmentQuote quote : allQuotes) {
                if (quote.getClientId() != null && quote.getClientId().equals(selected.getClientId())) {
                    equipmentQuoteCombo.addItem(quote);
                }
            }
        }
        equipmentQuoteCombo.setSelectedIndex(-1);

        pmaCombo.removeAllItems();
        if (allAgreements != null) {
            for (PreventiveMaintenanceAgreement pma : allAgreements) {
                if (pma.getClientId() != null && pma.getClientId().equals(selected.getClientId())) {
                    pmaCombo.addItem(pma);
                }
            }
        }
        pmaCombo.setSelectedIndex(-1);
    }

    private void loadInvoices() {
        try {
            List<Invoice> invoices = invoiceDAO.getAllInvoices();
            tableModel.setRowCount(0);
            for (Invoice inv : invoices) {
                tableModel.addRow(new Object[]{
                    inv.getInvoiceId(),
                    inv.getInvoiceNumber(),
                    getClientNameForInvoice(inv),
                    inv.getInvoiceDate() != null ? inv.getInvoiceDate().format(displayDateFormatter) : "",
                    inv.getDueDate() != null ? inv.getDueDate().format(displayDateFormatter) : "",
                    inv.getStatus(),
                    inv.getTotalAmount(),
                    inv.getBalanceDue() != null ? inv.getBalanceDue() : BigDecimal.ZERO
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading invoices: " + ex.getMessage());
        }
    }

    private String getClientNameForInvoice(Invoice inv) {
        if (inv.getClientId() != null) {
            try {
                Client client = clientDAO.getClientById(inv.getClientId());
                if (client != null) {
                    return client.toString();
                }
            } catch (SQLException e) {
                // Fall through
            }
        }

        if (inv.getBillToCompanyName() != null && !inv.getBillToCompanyName().isEmpty()) {
            return inv.getBillToCompanyName();
        } else if (inv.getBillToContactName() != null && !inv.getBillToContactName().isEmpty()) {
            return inv.getBillToContactName();
        }

        return "Unknown";
    }

    private void loadSelectedInvoice() {
        int row = invoicesTable.getSelectedRow();
        if (row < 0) return;

        int invoiceId = (Integer) tableModel.getValueAt(row, 0);
        try {
            selectedInvoice = invoiceDAO.getInvoiceById(invoiceId);
            if (selectedInvoice != null) {
                populateForm(selectedInvoice);
                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading invoice: " + ex.getMessage());
        }
    }

    private void populateForm(Invoice invoice) {
        try {
            // Select client
            for (int i = 0; i < clientCombo.getItemCount(); i++) {
                if (clientCombo.getItemAt(i).getClientId().equals(invoice.getClientId())) {
                    clientCombo.setSelectedIndex(i);
                    break;
                }
            }

            // Load locations for selected client
            loadLocationsForClient();

            // Select locations
            for (int i = 0; i < billLocationCombo.getItemCount(); i++) {
                if (billLocationCombo.getItemAt(i).getClientLocationId().equals(invoice.getBillingLocationId())) {
                    billLocationCombo.setSelectedIndex(i);
                    break;
                }
            }
            for (int i = 0; i < jobLocationCombo.getItemCount(); i++) {
                if (jobLocationCombo.getItemAt(i).getClientLocationId().equals(invoice.getJobLocationId())) {
                    jobLocationCombo.setSelectedIndex(i);
                    break;
                }
            }

            // Select quote/agreement
            if (invoice.getEquipmentQuoteId() != null) {
                for (int i = 0; i < equipmentQuoteCombo.getItemCount(); i++) {
                    EquipmentQuote q = equipmentQuoteCombo.getItemAt(i);
                    if (q.getEquipmentQuoteId() != null && q.getEquipmentQuoteId().equals(invoice.getEquipmentQuoteId())) {
                        equipmentQuoteCombo.setSelectedIndex(i);
                        break;
                    }
                }
            } else if (invoice.getPreventiveMaintenanceAgreementId() != null) {
                for (int i = 0; i < pmaCombo.getItemCount(); i++) {
                    PreventiveMaintenanceAgreement pma = pmaCombo.getItemAt(i);
                    if (pma.getPmaId() != null && pma.getPmaId().equals(invoice.getPreventiveMaintenanceAgreementId())) {
                        pmaCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }

            invoiceNumberField.setText(invoice.getInvoiceNumber());
            poNumberField.setText(invoice.getPoNumber() != null ? invoice.getPoNumber() : "");
            invoiceDateField.setText(invoice.getInvoiceDate().format(displayDateFormatter));
            dueDateField.setText(invoice.getDueDate().format(displayDateFormatter));
            termsField.setText(invoice.getTerms());
            statusCombo.setSelectedItem(invoice.getStatus());
            paidDateField.setText(invoice.getPaidDate() != null ? invoice.getPaidDate().format(displayDateFormatter) : "");

            subtotalAmountField.setText(invoice.getSubtotalAmount() != null ? invoice.getSubtotalAmount().toString() : "0.00");
            taxRateField.setText(invoice.getTaxRatePercent().toString());
            taxAmountField.setText(invoice.getTaxAmount().toString());
            totalAmountField.setText(invoice.getTotalAmount().toString());

            originalPaymentsApplied = invoice.getPaymentsApplied().toString();
            paymentsAppliedField.setText(originalPaymentsApplied);

            balanceDueField.setText(invoice.getBalanceDue() != null ? invoice.getBalanceDue().toString() : "0.00");

            returnedCheckFeeField.setText(invoice.getReturnedCheckFee() != null ? invoice.getReturnedCheckFee().toString() : "40.00");
            interestPercentField.setText(invoice.getInterestPercent() != null ? invoice.getInterestPercent().toString() : "10.00");
            interestStartDaysField.setText(invoice.getInterestStartDays() != null ? invoice.getInterestStartDays().toString() : "90");
            interestIntervalDaysField.setText(invoice.getInterestIntervalDays() != null ? invoice.getInterestIntervalDays().toString() : "30");

            // Load invoice items
            loadInvoiceItemsForInvoice(invoice.getInvoiceId());

            // Set field editability
            String status = invoice.getStatus();
            if ("Paid".equals(status)) {
                paymentsAppliedField.setEditable(false);
                paymentsAppliedField.setBackground(java.awt.Color.WHITE);
            } else {
                paymentsAppliedField.setEditable(true);
            }

            calculateBalanceDue();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error populating form: " + ex.getMessage());
        }
    }

    private void loadInvoiceItemsForInvoice(Integer invoiceId) {
        if (invoiceId == null) {
            invoiceItems.clear();
            refreshItemsTable();
            return;
        }

        try {
            invoiceItems.clear();
            invoiceItems.addAll(invoiceItemDAO.getItemsByInvoiceId(invoiceId));
            refreshItemsTable();
            recalculateTotals();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading invoice items: " + ex.getMessage());
        }
    }

    private void addInvoiceItem() {
        JTextField descriptionField = new JTextField(30);
        JTextField qtyField = new JTextField(10);
        JTextField rateField = new JTextField(10);

        qtyField.setText("1.00");
        rateField.setText("0.00");

        JPanel itemPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        itemPanel.add(new JLabel("Description:*"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        itemPanel.add(descriptionField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        itemPanel.add(new JLabel("Quantity:*"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        itemPanel.add(qtyField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        itemPanel.add(new JLabel("Rate:*"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        itemPanel.add(rateField, gbc);

        int result = JOptionPane.showConfirmDialog(this, itemPanel, "Add Invoice Item", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String description = descriptionField.getText().trim();
                if (description.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Description is required.");
                    return;
                }

                BigDecimal qty = new BigDecimal(qtyField.getText().trim());
                BigDecimal rate = new BigDecimal(rateField.getText().trim());
                BigDecimal total = qty.multiply(rate);

                int rowNumber = invoiceItems.size() + 1;

                InvoiceItem item = new InvoiceItem();
                item.setRowNumber(rowNumber);
                item.setDescription(description);
                item.setQty(qty);
                item.setRate(rate);
                item.setTotalAmount(total);

                invoiceItems.add(item);
                refreshItemsTable();
                recalculateTotals();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity or rate format.");
            }
        }
    }

    private void editInvoiceItem() {
        int selectedRow = invoiceItemsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item to edit.");
            return;
        }

        InvoiceItem item = invoiceItems.get(selectedRow);

        JTextField descriptionField = new JTextField(30);
        JTextField qtyField = new JTextField(10);
        JTextField rateField = new JTextField(10);

        descriptionField.setText(item.getDescription());
        qtyField.setText(item.getQty().toString());
        rateField.setText(item.getRate().toString());

        JPanel itemPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        itemPanel.add(new JLabel("Description:*"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        itemPanel.add(descriptionField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        itemPanel.add(new JLabel("Quantity:*"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        itemPanel.add(qtyField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        itemPanel.add(new JLabel("Rate:*"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        itemPanel.add(rateField, gbc);

        int result = JOptionPane.showConfirmDialog(this, itemPanel, "Edit Invoice Item", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String description = descriptionField.getText().trim();
                if (description.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Description is required.");
                    return;
                }

                BigDecimal qty = new BigDecimal(qtyField.getText().trim());
                BigDecimal rate = new BigDecimal(rateField.getText().trim());
                BigDecimal total = qty.multiply(rate);

                item.setDescription(description);
                item.setQty(qty);
                item.setRate(rate);
                item.setTotalAmount(total);

                refreshItemsTable();
                recalculateTotals();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity or rate format.");
            }
        }
    }

    private void deleteInvoiceItem() {
        int selectedRow = invoiceItemsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this item?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            invoiceItems.remove(selectedRow);

            for (int i = 0; i < invoiceItems.size(); i++) {
                invoiceItems.get(i).setRowNumber(i + 1);
            }

            refreshItemsTable();
            recalculateTotals();
        }
    }

    private void refreshItemsTable() {
        itemsTableModel.setRowCount(0);

        for (InvoiceItem item : invoiceItems) {
            itemsTableModel.addRow(new Object[]{
                item.getRowNumber(),
                item.getDescription(),
                item.getQty(),
                item.getRate(),
                item.getTotalAmount()
            });
        }
    }

    private void recalculateTotals() {
        try {
            BigDecimal subtotal = BigDecimal.ZERO;
            for (InvoiceItem item : invoiceItems) {
                subtotal = subtotal.add(item.getTotalAmount());
            }

            subtotalAmountField.setText(subtotal.setScale(2, java.math.RoundingMode.HALF_UP).toString());

            String taxRateText = taxRateField.getText().trim();
            if (!taxRateText.isEmpty()) {
                BigDecimal taxRate = new BigDecimal(taxRateText);
                BigDecimal taxAmount = subtotal.multiply(taxRate).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
                taxAmountField.setText(taxAmount.toString());

                BigDecimal totalAmount = subtotal.add(taxAmount);
                totalAmountField.setText(totalAmount.toString());

                String paymentsText = paymentsAppliedField.getText().trim();
                if (!paymentsText.isEmpty()) {
                    BigDecimal payments = new BigDecimal(paymentsText);
                    BigDecimal balance = totalAmount.subtract(payments);

                    String status = (String) statusCombo.getSelectedItem();
                    if ("Void".equals(status)) {
                        balanceDueField.setText("0.00");
                    } else {
                        balanceDueField.setText(balance.setScale(2, java.math.RoundingMode.HALF_UP).toString());
                    }
                }
            }
        } catch (Exception e) {
            // Invalid input, ignore
        }
    }

    private void addInvoice() {
        if (!validateForm()) return;

        Invoice invoice = new Invoice();
        populateInvoiceFromForm(invoice);

        try {
            if (invoiceDAO.addInvoice(invoice)) {
                // Save invoice items
                saveInvoiceItems(invoice.getInvoiceId());
                JOptionPane.showMessageDialog(this, "Invoice created successfully!");
                clearForm();
                loadInvoices();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding invoice: " + ex.getMessage());
        }
    }

    private void updateInvoice() {
        if (selectedInvoice == null || !validateForm()) return;

        populateInvoiceFromForm(selectedInvoice);

        try {
            if (invoiceDAO.updateInvoice(selectedInvoice)) {
                // Save invoice items
                saveInvoiceItems(selectedInvoice.getInvoiceId());
                JOptionPane.showMessageDialog(this, "Invoice updated successfully!");
                clearForm();
                loadInvoices();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating invoice: " + ex.getMessage());
        }
    }

    private void deleteInvoice() {
        if (selectedInvoice == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this invoice?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (invoiceDAO.deleteInvoice(selectedInvoice.getInvoiceId())) {
                    JOptionPane.showMessageDialog(this, "Invoice deleted successfully!");
                    clearForm();
                    loadInvoices();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting invoice: " + ex.getMessage());
            }
        }
    }

    private void populateInvoiceFromForm(Invoice invoice) {
        Client client = (Client) clientCombo.getSelectedItem();
        ClientLocation billLoc = (ClientLocation) billLocationCombo.getSelectedItem();
        ClientLocation jobLoc = (ClientLocation) jobLocationCombo.getSelectedItem();

        invoice.setClientId(client.getClientId());
        invoice.setBillingLocationId(billLoc.getClientLocationId());
        invoice.setJobLocationId(jobLoc.getClientLocationId());

        if (equipmentQuoteCombo.getSelectedItem() != null &&
            equipmentQuoteCombo.getSelectedItem() instanceof EquipmentQuote) {
            EquipmentQuote quote = (EquipmentQuote) equipmentQuoteCombo.getSelectedItem();
            invoice.setEquipmentQuoteId(quote.getEquipmentQuoteId());
            invoice.setPreventiveMaintenanceAgreementId(null);
            invoice.setQuoteNumber(quote.getQuoteNumber());
        } else if (pmaCombo.getSelectedItem() != null &&
                   pmaCombo.getSelectedItem() instanceof PreventiveMaintenanceAgreement) {
            PreventiveMaintenanceAgreement pma = (PreventiveMaintenanceAgreement) pmaCombo.getSelectedItem();
            invoice.setPreventiveMaintenanceAgreementId(pma.getPmaId());
            invoice.setEquipmentQuoteId(null);
            invoice.setQuoteNumber(pma.getAgreementNumber());
        } else {
            invoice.setEquipmentQuoteId(null);
            invoice.setPreventiveMaintenanceAgreementId(null);
            invoice.setQuoteNumber(null);
        }

        invoice.setInvoiceNumber(invoiceNumberField.getText().trim());
        invoice.setPoNumber(poNumberField.getText().trim().isEmpty() ? null : poNumberField.getText().trim());

        invoice.setInvoiceDate(LocalDate.parse(invoiceDateField.getText().trim(), displayDateFormatter));
        invoice.setDueDate(LocalDate.parse(dueDateField.getText().trim(), displayDateFormatter));

        String paidDateText = paidDateField.getText().trim();
        invoice.setPaidDate(paidDateText.isEmpty() ? null : LocalDate.parse(paidDateText, displayDateFormatter));

        invoice.setTerms(termsField.getText().trim());
        invoice.setStatus((String) statusCombo.getSelectedItem());

        BigDecimal subtotal = BigDecimal.ZERO;
        for (InvoiceItem item : invoiceItems) {
            subtotal = subtotal.add(item.getTotalAmount());
        }
        invoice.setSubtotalAmount(subtotal);
        invoice.setTaxRatePercent(new BigDecimal(taxRateField.getText().trim()));
        invoice.setPaymentsApplied(new BigDecimal(paymentsAppliedField.getText().trim()));

        invoice.setReturnedCheckFee(new BigDecimal(returnedCheckFeeField.getText().trim()));
        invoice.setInterestPercent(new BigDecimal(interestPercentField.getText().trim()));
        invoice.setInterestStartDays(Integer.parseInt(interestStartDaysField.getText().trim()));
        invoice.setInterestIntervalDays(Integer.parseInt(interestIntervalDaysField.getText().trim()));

        // Capture snapshots
        invoice.setClientTypeSnapshot(client.getClientType());
        invoice.setClientCompanyNameSnapshot(client.getCompanyName());
        invoice.setClientFirstNameSnapshot(client.getFirstName());
        invoice.setClientLastNameSnapshot(client.getLastName());

        invoice.setBillToCompanyName(billLoc.getCompanyName());
        invoice.setBillToContactName(billLoc.getContactName());
        invoice.setBillToStreetAddress(billLoc.getStreetAddress());
        invoice.setBillToBuildingName(billLoc.getBuildingName());
        invoice.setBillToSuite(billLoc.getSuite());
        invoice.setBillToRoomNumber(billLoc.getRoomNumber());
        invoice.setBillToDepartment(billLoc.getDepartment());
        invoice.setBillToCity(billLoc.getCity());
        invoice.setBillToCounty(billLoc.getCounty());
        invoice.setBillToState(billLoc.getState());
        invoice.setBillToZIPCode(billLoc.getZipCode());
        invoice.setBillToCountry(billLoc.getCountry());
        invoice.setBillToPhone(billLoc.getPhone());
        invoice.setBillToPONumber(poNumberField.getText().trim().isEmpty() ? null : poNumberField.getText().trim());

        invoice.setJobAtCompanyName(jobLoc.getCompanyName());
        invoice.setJobAtContactName(jobLoc.getContactName());
        invoice.setJobAtStreetAddress(jobLoc.getStreetAddress());
        invoice.setJobAtBuildingName(jobLoc.getBuildingName());
        invoice.setJobAtSuite(jobLoc.getSuite());
        invoice.setJobAtRoomNumber(jobLoc.getRoomNumber());
        invoice.setJobAtDepartment(jobLoc.getDepartment());
        invoice.setJobAtCity(jobLoc.getCity());
        invoice.setJobAtCounty(jobLoc.getCounty());
        invoice.setJobAtState(jobLoc.getState());
        invoice.setJobAtZIPCode(jobLoc.getZipCode());
        invoice.setJobAtCountry(jobLoc.getCountry());
        invoice.setJobAtPhone(jobLoc.getPhone());
        invoice.setJobAtPONumber(poNumberField.getText().trim().isEmpty() ? null : poNumberField.getText().trim());

        invoice.setFromCompanyName("Apple Fitness Equipment");
        invoice.setFromStreetAddress("1412 Majestic View Dr.");
        invoice.setFromCity("State College");
        invoice.setFromState("PA");
        invoice.setFromZIPCode("16801");
        invoice.setFromPhone("8148262922");
        invoice.setFromFax("8148262933");
    }

    private void saveInvoiceItems(Integer invoiceId) throws SQLException {
        if (invoiceId == null) return;

        invoiceItemDAO.deleteAllItemsByInvoiceId(invoiceId);

        for (InvoiceItem item : invoiceItems) {
            item.setInvoiceId(invoiceId);
            invoiceItemDAO.addInvoiceItem(item);
        }
    }

    private void clearForm() {
        clientCombo.setSelectedIndex(-1);
        billLocationCombo.removeAllItems();
        jobLocationCombo.removeAllItems();
        equipmentQuoteCombo.removeAllItems();
        pmaCombo.removeAllItems();

        invoiceNumberField.setText("");
        poNumberField.setText("");
        invoiceDateField.setText(LocalDate.now().format(displayDateFormatter));
        dueDateField.setText(LocalDate.now().plusDays(30).format(displayDateFormatter));
        termsField.setText("Net 30");
        statusCombo.setSelectedItem("Draft");
        paidDateField.setText("");

        subtotalAmountField.setText("0.00");
        taxRateField.setText("6.00");
        taxAmountField.setText("0.00");
        totalAmountField.setText("0.00");
        paymentsAppliedField.setText("0.00");
        balanceDueField.setText("0.00");

        returnedCheckFeeField.setText("40.00");
        interestPercentField.setText("10.00");
        interestStartDaysField.setText("90");
        interestIntervalDaysField.setText("30");

        invoiceItems.clear();
        refreshItemsTable();

        originalPaymentsApplied = "0.00";
        selectedInvoice = null;
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        invoicesTable.clearSelection();
    }

    private boolean validateForm() {
        if (clientCombo.getSelectedItem() == null || billLocationCombo.getSelectedItem() == null ||
            jobLocationCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select client and locations.");
            return false;
        }
        if (invoiceNumberField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invoice number is required.");
            return false;
        }
        if (invoiceDateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invoice date is required.");
            return false;
        }
        try {
            LocalDate.parse(invoiceDateField.getText().trim(), displayDateFormatter);
            LocalDate.parse(dueDateField.getText().trim(), displayDateFormatter);

            String paidDateText = paidDateField.getText().trim();
            if (!paidDateText.isEmpty()) {
                LocalDate.parse(paidDateText, displayDateFormatter);
            }

            new BigDecimal(taxRateField.getText().trim());
            new BigDecimal(paymentsAppliedField.getText().trim());
            new BigDecimal(returnedCheckFeeField.getText().trim());
            new BigDecimal(interestPercentField.getText().trim());
            Integer.parseInt(interestStartDaysField.getText().trim());
            Integer.parseInt(interestIntervalDaysField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date or number format.\nDates should be MM/dd/yyyy");
            return false;
        }
        return true;
    }

    public void refreshData() {
        loadClients();
        loadQuotesAndAgreements();
        loadInvoices();
    }
}
