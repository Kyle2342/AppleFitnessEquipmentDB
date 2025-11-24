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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

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

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class InvoiceDialog extends JDialog {
    private InvoiceDAO invoiceDAO;
    private InvoiceItemDAO invoiceItemDAO;
    private ClientDAO clientDAO;
    private ClientLocationDAO locationDAO;
    private EquipmentQuoteDAO quoteDAO;
    private PMAgreementDAO pmaDAO;
    private Invoice invoice;
    private boolean saved = false;

    // Invoice items
    private JTable invoiceItemsTable;
    private DefaultTableModel itemsTableModel;
    private List<InvoiceItem> invoiceItems;

    // Link to Quote/Agreement
    private JComboBox<EquipmentQuote> equipmentQuoteCombo;
    private JComboBox<PreventiveMaintenanceAgreement> pmaCombo;

    // Core invoice fields
    private JTextField invoiceNumberField, poNumberField;
    private JTextField invoiceDateField, dueDateField, paidDateField;
    private JTextField termsField;
    private JComboBox<String> statusCombo;

    // Money fields
    private JTextField subtotalAmountField, taxRateField, taxAmountField;
    private JTextField totalAmountField, paymentsAppliedField, balanceDueField;

    // Fee and interest fields
    private JTextField returnedCheckFeeField, interestPercentField;
    private JTextField interestStartDaysField, interestIntervalDaysField;

    // Client selection
    private JComboBox<Client> clientCombo;
    private JComboBox<ClientLocation> billLocationCombo, jobLocationCombo;

    private DateTimeFormatter displayDateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private List<Client> allClients;
    private List<EquipmentQuote> allQuotes;
    private List<PreventiveMaintenanceAgreement> allAgreements;
    private List<ClientLocation> allBillLocations;
    private List<ClientLocation> allJobLocations;

    // Track original payments applied value from database
    private String originalPaymentsApplied = "0.00";

    public InvoiceDialog(JFrame parent, Invoice invoice) {
        super(parent, invoice == null ? "Create Invoice" : "Edit Invoice", true);
        this.invoice = invoice;
        this.invoiceDAO = new InvoiceDAO();
        this.invoiceItemDAO = new InvoiceItemDAO();
        this.clientDAO = new ClientDAO();
        this.locationDAO = new ClientLocationDAO();
        this.quoteDAO = new EquipmentQuoteDAO();
        this.pmaDAO = new PMAgreementDAO();
        this.invoiceItems = new ArrayList<>();

        initComponents();
        loadClients();
        loadQuotesAndAgreements();

        if (invoice != null) {
            populateForm();
        } else {
            setDefaultValues();
        }

        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(900, 600));
        // Limit dialog height to 90% of screen height to ensure buttons are visible
        setSize(new Dimension(900, Math.min(getHeight(), (int)(java.awt.Toolkit.getDefaultToolkit().getScreenSize().height * 0.9))));
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        // ===== CLIENT SELECTION =====
        addSectionLabel(formPanel, gbc, row++, "CLIENT INFORMATION");

        // Client
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Client:*"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        clientCombo = new JComboBox<>();
        clientCombo.setEditable(true);

        JTextField textField = (JTextField) clientCombo.getEditor().getEditorComponent();
        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER ||
                    e.getKeyCode() == java.awt.event.KeyEvent.VK_TAB) {
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    String text = textField.getText();
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

        // Bill Location
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Bill To Location:*"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        billLocationCombo = new JComboBox<>();
        billLocationCombo.setEditable(true);

        JTextField billLocationTextField = (JTextField) billLocationCombo.getEditor().getEditorComponent();
        billLocationTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER ||
                    e.getKeyCode() == java.awt.event.KeyEvent.VK_TAB) {
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    String text = billLocationTextField.getText();
                    filterBillLocationCombo(text);
                });
            }
        });

        ModernUIHelper.styleComboBox(billLocationCombo);
        formPanel.add(billLocationCombo, gbc);
        row++;

        // Job Location
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Job At Location:*"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        jobLocationCombo = new JComboBox<>();
        jobLocationCombo.setEditable(true);

        JTextField jobLocationTextField = (JTextField) jobLocationCombo.getEditor().getEditorComponent();
        jobLocationTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER ||
                    e.getKeyCode() == java.awt.event.KeyEvent.VK_TAB) {
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    String text = jobLocationTextField.getText();
                    filterJobLocationCombo(text);
                });
            }
        });

        ModernUIHelper.styleComboBox(jobLocationCombo);
        formPanel.add(jobLocationCombo, gbc);
        row++;

        // Equipment Quote
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Equipment Quote:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        equipmentQuoteCombo = new JComboBox<>();
        equipmentQuoteCombo.setEditable(true);
        equipmentQuoteCombo.addActionListener(e -> {
            // If equipment quote is selected, clear PMA (but allow both to be blank)
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
        gbc.weightx = 0.3;
        formPanel.add(createLabel("PM Agreement:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        pmaCombo = new JComboBox<>();
        pmaCombo.setEditable(true);
        pmaCombo.addActionListener(e -> {
            // If PMA is selected, clear Equipment Quote (but allow both to be blank)
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

        // Invoice Number
        row = addField(formPanel, gbc, row, "Invoice Number:*", invoiceNumberField = new JTextField(20));

        // PO Number
        row = addField(formPanel, gbc, row, "PO Number:", poNumberField = new JTextField(20));

        // Invoice Date
        row = addField(formPanel, gbc, row, "Invoice Date (MM/dd/yyyy):*", invoiceDateField = new JTextField(20));

        // Due Date
        row = addField(formPanel, gbc, row, "Due Date (MM/dd/yyyy):*", dueDateField = new JTextField(20));

        // Terms
        row = addField(formPanel, gbc, row, "Terms:*", termsField = new JTextField(20));

        // Status
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Status:*"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        statusCombo = new JComboBox<>(new String[]{"Draft", "Open", "Paid", "Overdue", "Void"});
        statusCombo.addActionListener(e -> {
            updatePaidDate();
            String selectedStatus = (String) statusCombo.getSelectedItem();

            // When status is Paid, force payments to equal total amount
            if ("Paid".equals(selectedStatus)) {
                // Lock payments applied field when status is Paid
                paymentsAppliedField.setEditable(false);
                paymentsAppliedField.setBackground(java.awt.Color.WHITE);

                // Force payments applied to equal total amount (Paid = fully paid)
                String totalText = totalAmountField.getText().trim();
                if (!totalText.isEmpty()) {
                    paymentsAppliedField.setText(totalText);
                }
            } else {
                // Make payments applied field editable for other statuses
                paymentsAppliedField.setEditable(true);

                // Restore original payments applied value from database (don't keep the auto-filled value)
                paymentsAppliedField.setText(originalPaymentsApplied);
            }
            calculateBalanceDue();
        });
        ModernUIHelper.styleComboBox(statusCombo);
        formPanel.add(statusCombo, gbc);
        row++;

        // Paid Date
        row = addField(formPanel, gbc, row, "Paid Date (MM/dd/yyyy):", paidDateField = new JTextField(20));

        // ===== FINANCIAL DETAILS =====
        addSectionLabel(formPanel, gbc, row++, "FINANCIAL DETAILS");

        // Subtotal Amount (calculated from invoice items)
        subtotalAmountField = new JTextField(20);
        subtotalAmountField.setEditable(false);
        subtotalAmountField.setBackground(java.awt.Color.WHITE);
        row = addField(formPanel, gbc, row, "Subtotal Amount:", subtotalAmountField);

        // Tax Rate
        row = addField(formPanel, gbc, row, "Tax Rate %:", taxRateField = new JTextField(20));

        // Tax Amount (auto-calculated by database)
        taxAmountField = new JTextField(20);
        taxAmountField.setEditable(false);
        taxAmountField.setBackground(java.awt.Color.WHITE);
        row = addField(formPanel, gbc, row, "Tax Amount:", taxAmountField);

        // Total Amount (auto-calculated by database)
        totalAmountField = new JTextField(20);
        totalAmountField.setEditable(false);
        totalAmountField.setBackground(java.awt.Color.WHITE);
        row = addField(formPanel, gbc, row, "Total Amount:", totalAmountField);

        // Payments Applied
        paymentsAppliedField = new JTextField(20);
        paymentsAppliedField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculateBalanceDue(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculateBalanceDue(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculateBalanceDue(); }
        });
        row = addField(formPanel, gbc, row, "Payments Applied:", paymentsAppliedField);

        // Balance Due (calculated in UI)
        balanceDueField = new JTextField(20);
        balanceDueField.setEditable(false);
        balanceDueField.setBackground(java.awt.Color.WHITE);
        row = addField(formPanel, gbc, row, "Balance Due:", balanceDueField);

        // ===== FEES AND INTEREST =====
        addSectionLabel(formPanel, gbc, row++, "FEES & INTEREST");

        // Returned Check Fee
        row = addField(formPanel, gbc, row, "Returned Check Fee:*", returnedCheckFeeField = new JTextField(20));

        // Interest Percent
        row = addField(formPanel, gbc, row, "Interest Percent:*", interestPercentField = new JTextField(20));

        // Interest Start Days
        row = addField(formPanel, gbc, row, "Interest Start Days:*", interestStartDaysField = new JTextField(20));

        // Interest Interval Days
        row = addField(formPanel, gbc, row, "Interest Interval Days:*", interestIntervalDaysField = new JTextField(20));

        // ===== INVOICE ITEMS =====
        addSectionLabel(formPanel, gbc, row++, "INVOICE ITEMS");

        // Create table model for invoice items
        itemsTableModel = new DefaultTableModel(new String[]{"Row#", "Description", "Qty", "Rate", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table is read-only; use buttons to edit
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
        itemsScrollPane.setPreferredSize(new Dimension(800, 200));
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.3;
        gbc.insets = new Insets(10, 10, 10, 10);
        formPanel.add(itemsScrollPane, gbc);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        gbc.insets = new Insets(8, 10, 8, 10);
        row++;

        // Buttons for managing items
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

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton saveOnlyButton = new JButton("Save");
        saveOnlyButton.addActionListener(e -> saveInvoiceOnly());
        ModernUIHelper.styleButton(saveOnlyButton, "primary");
        buttonPanel.add(saveOnlyButton);

        JButton saveCloseButton = new JButton(invoice == null ? "Create & Close" : "Save & Close");
        saveCloseButton.addActionListener(e -> saveInvoice());
        ModernUIHelper.styleButton(saveCloseButton, "success");
        buttonPanel.add(saveCloseButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        ModernUIHelper.styleButton(cancelButton, "secondary");
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadQuotesAndAgreements() {
        try {
            // Load all equipment quotes and agreements into memory
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

        // Filter equipment quotes for this client
        equipmentQuoteCombo.removeAllItems();
        if (allQuotes != null) {
            for (EquipmentQuote quote : allQuotes) {
                if (quote.getClientId() != null && quote.getClientId().equals(selected.getClientId())) {
                    equipmentQuoteCombo.addItem(quote);
                }
            }
        }
        // Don't auto-select - leave it blank
        equipmentQuoteCombo.setSelectedIndex(-1);

        // Filter PMA agreements for this client
        pmaCombo.removeAllItems();
        if (allAgreements != null) {
            for (PreventiveMaintenanceAgreement pma : allAgreements) {
                if (pma.getClientId() != null && pma.getClientId().equals(selected.getClientId())) {
                    pmaCombo.addItem(pma);
                }
            }
        }
        // Don't auto-select - leave it blank
        pmaCombo.setSelectedIndex(-1);
    }


    private void addSectionLabel(JPanel panel, GridBagConstraints gbc, int row, String text) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(java.awt.Font.BOLD, 15f));
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(15, 0, 10, 0)
        ));
        panel.add(label, gbc);
        gbc.gridwidth = 1;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(14f)); // Make label text bigger
        return label;
    }

    private int addField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JTextField field) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(createLabel(labelText), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        ModernUIHelper.styleTextField(field);
        panel.add(field, gbc);
        return row + 1;
    }

    private void calculateBalanceDue() {
        try {
            // Calculate balance due = Total Amount - Payments Applied
            String totalText = totalAmountField.getText().trim();
            String paymentsText = paymentsAppliedField.getText().trim();

            if (!totalText.isEmpty() && !paymentsText.isEmpty()) {
                BigDecimal total = new BigDecimal(totalText);
                BigDecimal payments = new BigDecimal(paymentsText);
                BigDecimal balance = total.subtract(payments);

                // Only Void status sets balance to 0
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

            // Store locations in lists for filtering
            allBillLocations = new ArrayList<>();
            allJobLocations = new ArrayList<>();

            billLocationCombo.removeAllItems();
            jobLocationCombo.removeAllItems();

            // Separate locations by type
            for (ClientLocation loc : locations) {
                if ("Billing".equals(loc.getLocationType())) {
                    allBillLocations.add(loc);
                    billLocationCombo.addItem(loc);
                } else if ("Job".equals(loc.getLocationType())) {
                    allJobLocations.add(loc);
                    jobLocationCombo.addItem(loc);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading locations: " + ex.getMessage());
        }
    }

    private void filterBillLocationCombo(String searchText) {
        if (allBillLocations == null) return;

        String search = searchText.toLowerCase().trim();

        billLocationCombo.removeAllItems();

        for (ClientLocation loc : allBillLocations) {
            String locName = loc.toString();
            if (search.isEmpty() || locName.toLowerCase().contains(search)) {
                billLocationCombo.addItem(loc);
            }
        }

        JTextField editor = (JTextField) billLocationCombo.getEditor().getEditorComponent();
        editor.setText(searchText);

        if (!search.isEmpty() && billLocationCombo.getItemCount() > 0) {
            billLocationCombo.showPopup();
        }
    }

    private void filterJobLocationCombo(String searchText) {
        if (allJobLocations == null) return;

        String search = searchText.toLowerCase().trim();

        jobLocationCombo.removeAllItems();

        for (ClientLocation loc : allJobLocations) {
            String locName = loc.toString();
            if (search.isEmpty() || locName.toLowerCase().contains(search)) {
                jobLocationCombo.addItem(loc);
            }
        }

        JTextField editor = (JTextField) jobLocationCombo.getEditor().getEditorComponent();
        editor.setText(searchText);

        if (!search.isEmpty() && jobLocationCombo.getItemCount() > 0) {
            jobLocationCombo.showPopup();
        }
    }

    private void setDefaultValues() {
        // Don't set default client - leave it empty
        clientCombo.setSelectedIndex(-1);

        // Clear location combos since no client is selected
        billLocationCombo.removeAllItems();
        jobLocationCombo.removeAllItems();

        // Clear quote/agreement combos since no client is selected
        equipmentQuoteCombo.removeAllItems();
        pmaCombo.removeAllItems();

        invoiceDateField.setText(LocalDate.now().format(displayDateFormatter));
        dueDateField.setText(LocalDate.now().plusDays(30).format(displayDateFormatter));
        termsField.setText("Net 30");
        statusCombo.setSelectedItem("Draft");
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
    }

    private void populateForm() {
        try {
            // Select client first (this will trigger filtering of quotes/agreements)
            boolean clientFound = false;
            if (invoice.getClientId() != null) {
                for (int i = 0; i < clientCombo.getItemCount(); i++) {
                    if (clientCombo.getItemAt(i).getClientId().equals(invoice.getClientId())) {
                        clientCombo.setSelectedIndex(i);
                        clientFound = true;
                        break;
                    }
                }
            }

            // After client is selected and combos are filtered, select quote/agreement
            if (invoice.getEquipmentQuoteId() != null) {
                // Try to select the linked quote
                for (int i = 0; i < equipmentQuoteCombo.getItemCount(); i++) {
                    EquipmentQuote q = equipmentQuoteCombo.getItemAt(i);
                    if (q.getEquipmentQuoteId() != null && q.getEquipmentQuoteId().equals(invoice.getEquipmentQuoteId())) {
                        equipmentQuoteCombo.setSelectedIndex(i);
                        break;
                    }
                }
            } else if (invoice.getPreventiveMaintenanceAgreementId() != null) {
                // Try to select the linked PMA
                for (int i = 0; i < pmaCombo.getItemCount(); i++) {
                    PreventiveMaintenanceAgreement pma = pmaCombo.getItemAt(i);
                    if (pma.getPmaId() != null && pma.getPmaId().equals(invoice.getPreventiveMaintenanceAgreementId())) {
                        pmaCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }

            if (!clientFound) {
                String storedClientName = "";
                if (invoice.getBillToCompanyName() != null && !invoice.getBillToCompanyName().isEmpty()) {
                    storedClientName = invoice.getBillToCompanyName();
                } else if (invoice.getBillToContactName() != null && !invoice.getBillToContactName().isEmpty()) {
                    storedClientName = invoice.getBillToContactName();
                } else {
                    storedClientName = "Unknown Client";
                }

                JOptionPane.showMessageDialog(this,
                    "Original client no longer exists.\n" +
                    "Stored Bill To: " + storedClientName,
                    "Client Not Found", JOptionPane.INFORMATION_MESSAGE);
            }

            // Load and select locations
            if (clientFound) {
                loadLocationsForClient();
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

            // Store and display original payments applied value from database
            originalPaymentsApplied = invoice.getPaymentsApplied().toString();
            paymentsAppliedField.setText(originalPaymentsApplied);

            // Use the balance due from the database (GENERATED column handles void status)
            balanceDueField.setText(invoice.getBalanceDue() != null ? invoice.getBalanceDue().toString() : "0.00");

            returnedCheckFeeField.setText(invoice.getReturnedCheckFee() != null ? invoice.getReturnedCheckFee().toString() : "40.00");
            interestPercentField.setText(invoice.getInterestPercent() != null ? invoice.getInterestPercent().toString() : "10.00");
            interestStartDaysField.setText(invoice.getInterestStartDays() != null ? invoice.getInterestStartDays().toString() : "90");
            interestIntervalDaysField.setText(invoice.getInterestIntervalDays() != null ? invoice.getInterestIntervalDays().toString() : "30");

            // Load invoice items
            loadInvoiceItems();

            // Set field editability based on status after loading all data
            String status = invoice.getStatus();
            if ("Paid".equals(status)) {
                paymentsAppliedField.setEditable(false);
                paymentsAppliedField.setBackground(java.awt.Color.WHITE);
            } else {
                paymentsAppliedField.setEditable(true);
            }

            // Recalculate balance due in case values changed
            calculateBalanceDue();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error populating form: " + ex.getMessage());
        }
    }

    private void saveInvoiceOnly() {
        if (!validateForm()) return;

        try {
            if (invoice == null) {
                invoice = new Invoice();
            }

            populateInvoiceFromForm();

            if (invoice.getInvoiceId() == null) {
                if (invoiceDAO.addInvoice(invoice)) {
                    // Save invoice items after invoice is created (now has ID)
                    saveInvoiceItems();
                    saved = true;
                    // Reload the invoice to get the GENERATED column values
                    invoice = invoiceDAO.getInvoiceById(invoice.getInvoiceId());
                    populateForm(); // Refresh the form with updated values
                    JOptionPane.showMessageDialog(this, "Invoice created successfully!");
                }
            } else {
                if (invoiceDAO.updateInvoice(invoice)) {
                    // Save invoice items
                    saveInvoiceItems();
                    saved = true;
                    // Reload the invoice to get the GENERATED column values
                    invoice = invoiceDAO.getInvoiceById(invoice.getInvoiceId());
                    populateForm(); // Refresh the form with updated values
                    JOptionPane.showMessageDialog(this, "Invoice updated successfully!");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving invoice: " + ex.getMessage());
        }
    }

    private void saveInvoice() {
        if (!validateForm()) return;

        try {
            if (invoice == null) {
                invoice = new Invoice();
            }

            populateInvoiceFromForm();

            if (invoice.getInvoiceId() == null) {
                if (invoiceDAO.addInvoice(invoice)) {
                    // Save invoice items after invoice is created (now has ID)
                    saveInvoiceItems();
                    saved = true;
                    JOptionPane.showMessageDialog(this, "Invoice created successfully!");
                    dispose();
                }
            } else {
                if (invoiceDAO.updateInvoice(invoice)) {
                    // Save invoice items
                    saveInvoiceItems();
                    saved = true;
                    JOptionPane.showMessageDialog(this, "Invoice updated successfully!");
                    dispose();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving invoice: " + ex.getMessage());
        }
    }

    private void populateInvoiceFromForm() {
        Client client = (Client) clientCombo.getSelectedItem();
        ClientLocation billLoc = (ClientLocation) billLocationCombo.getSelectedItem();
        ClientLocation jobLoc = (ClientLocation) jobLocationCombo.getSelectedItem();

        invoice.setClientId(client.getClientId());
        invoice.setBillingLocationId(billLoc.getClientLocationId());
        invoice.setJobLocationId(jobLoc.getClientLocationId());

        // Set quote/agreement FK based on combo selection, and extract quote number
        if (equipmentQuoteCombo.getSelectedItem() != null &&
            equipmentQuoteCombo.getSelectedItem() instanceof EquipmentQuote) {
            EquipmentQuote quote = (EquipmentQuote) equipmentQuoteCombo.getSelectedItem();
            invoice.setEquipmentQuoteId(quote.getEquipmentQuoteId());
            invoice.setPreventiveMaintenanceAgreementId(null);
            // Store the quote number from the Equipment Quote
            invoice.setQuoteNumber(quote.getQuoteNumber());
        } else if (pmaCombo.getSelectedItem() != null &&
                   pmaCombo.getSelectedItem() instanceof PreventiveMaintenanceAgreement) {
            PreventiveMaintenanceAgreement pma = (PreventiveMaintenanceAgreement) pmaCombo.getSelectedItem();
            invoice.setPreventiveMaintenanceAgreementId(pma.getPmaId());
            invoice.setEquipmentQuoteId(null);
            // Store the agreement number from the PMA as quote number
            invoice.setQuoteNumber(pma.getAgreementNumber());
        } else {
            // Neither selected - both null
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

        // Calculate subtotal from invoice items
        BigDecimal subtotal = BigDecimal.ZERO;
        for (InvoiceItem item : invoiceItems) {
            subtotal = subtotal.add(item.getTotalAmount());
        }
        invoice.setSubtotalAmount(subtotal);
        invoice.setTaxRatePercent(new BigDecimal(taxRateField.getText().trim()));
        // Note: TaxAmount and TotalAmount are GENERATED columns - don't set them
        invoice.setPaymentsApplied(new BigDecimal(paymentsAppliedField.getText().trim()));

        invoice.setReturnedCheckFee(new BigDecimal(returnedCheckFeeField.getText().trim()));
        invoice.setInterestPercent(new BigDecimal(interestPercentField.getText().trim()));
        invoice.setInterestStartDays(Integer.parseInt(interestStartDaysField.getText().trim()));
        invoice.setInterestIntervalDays(Integer.parseInt(interestIntervalDaysField.getText().trim()));

        // Capture Client snapshot
        invoice.setClientTypeSnapshot(client.getClientType());
        invoice.setClientCompanyNameSnapshot(client.getCompanyName());
        invoice.setClientFirstNameSnapshot(client.getFirstName());
        invoice.setClientLastNameSnapshot(client.getLastName());

        // Capture BILL TO location snapshot
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

        // Capture JOB AT location snapshot
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

        // FROM company fields - using defaults from database schema
        invoice.setFromCompanyName("Apple Fitness Equipment");
        invoice.setFromStreetAddress("1412 Majestic View Dr.");
        invoice.setFromCity("State College");
        invoice.setFromState("PA");
        invoice.setFromZIPCode("16801");
        invoice.setFromPhone("8148262922");
        invoice.setFromFax("8148262933");
    }

    private boolean validateForm() {
        if (clientCombo.getSelectedItem() == null ||
            billLocationCombo.getSelectedItem() == null ||
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

    // ===== INVOICE ITEMS MANAGEMENT =====

    private void addInvoiceItem() {
        // Prompt for item details
        JTextField descriptionField = new JTextField(30);
        JTextField qtyField = new JTextField(10);
        JTextField rateField = new JTextField(10);

        // Set defaults
        qtyField.setText("1.00");
        rateField.setText("0.00");

        JPanel itemPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        itemPanel.add(createLabel("Description:*"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        itemPanel.add(descriptionField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        itemPanel.add(createLabel("Quantity:*"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        itemPanel.add(qtyField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        itemPanel.add(createLabel("Rate:*"), gbc);
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

                // Calculate next row number
                int rowNumber = invoiceItems.size() + 1;

                // Create new item
                InvoiceItem item = new InvoiceItem();
                item.setRowNumber(rowNumber);
                item.setDescription(description);
                item.setQty(qty);
                item.setRate(rate);
                item.setTotalAmount(total);

                // Add to list
                invoiceItems.add(item);

                // Update table
                refreshItemsTable();

                // Recalculate totals
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

        // Populate fields with current values
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
        itemPanel.add(createLabel("Description:*"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        itemPanel.add(descriptionField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        itemPanel.add(createLabel("Quantity:*"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        itemPanel.add(qtyField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        itemPanel.add(createLabel("Rate:*"), gbc);
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

                // Update item
                item.setDescription(description);
                item.setQty(qty);
                item.setRate(rate);
                item.setTotalAmount(total);

                // Update table
                refreshItemsTable();

                // Recalculate totals
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

            // Renumber remaining items
            for (int i = 0; i < invoiceItems.size(); i++) {
                invoiceItems.get(i).setRowNumber(i + 1);
            }

            // Update table
            refreshItemsTable();

            // Recalculate totals
            recalculateTotals();
        }
    }

    private void refreshItemsTable() {
        // Clear table
        itemsTableModel.setRowCount(0);

        // Populate table with current items
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
            // Calculate subtotal from items
            BigDecimal subtotal = BigDecimal.ZERO;
            for (InvoiceItem item : invoiceItems) {
                subtotal = subtotal.add(item.getTotalAmount());
            }

            // Update subtotal field
            subtotalAmountField.setText(subtotal.setScale(2, java.math.RoundingMode.HALF_UP).toString());

            // Calculate tax amount
            String taxRateText = taxRateField.getText().trim();
            if (!taxRateText.isEmpty()) {
                BigDecimal taxRate = new BigDecimal(taxRateText);
                BigDecimal taxAmount = subtotal.multiply(taxRate).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
                taxAmountField.setText(taxAmount.toString());

                // Calculate total amount
                BigDecimal totalAmount = subtotal.add(taxAmount);
                totalAmountField.setText(totalAmount.toString());

                // Calculate balance due
                String paymentsText = paymentsAppliedField.getText().trim();
                if (!paymentsText.isEmpty()) {
                    BigDecimal payments = new BigDecimal(paymentsText);
                    BigDecimal balance = totalAmount.subtract(payments);

                    // Check void status for UI display
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

    private void loadInvoiceItems() {
        if (invoice == null || invoice.getInvoiceId() == null) {
            return;
        }

        try {
            invoiceItems.clear();
            invoiceItems.addAll(invoiceItemDAO.getItemsByInvoiceId(invoice.getInvoiceId()));
            refreshItemsTable();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading invoice items: " + ex.getMessage());
        }
    }

    private void saveInvoiceItems() throws SQLException {
        if (invoice == null || invoice.getInvoiceId() == null) {
            return;
        }

        // Delete all existing items for this invoice
        invoiceItemDAO.deleteAllItemsByInvoiceId(invoice.getInvoiceId());

        // Insert all current items
        for (InvoiceItem item : invoiceItems) {
            item.setInvoiceId(invoice.getInvoiceId());
            invoiceItemDAO.addInvoiceItem(item);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
