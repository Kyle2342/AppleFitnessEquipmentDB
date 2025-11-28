package com.applefitnessequipment.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
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
import com.applefitnessequipment.dao.InvoiceDAO;
import com.applefitnessequipment.dao.InvoiceItemDAO;
import com.applefitnessequipment.dao.PMAgreementDAO;
import com.applefitnessequipment.model.Client;
import com.applefitnessequipment.model.ClientLocation;
import com.applefitnessequipment.model.EquipmentQuote;
import com.applefitnessequipment.model.Invoice;
import com.applefitnessequipment.model.InvoiceItem;
import com.applefitnessequipment.model.PreventiveMaintenanceAgreement;

/**
 * Invoice management UI aligned to applefitnessequipmentdb_schema.sql.
 */
public class InvoicesPanel extends JPanel {
    private final InvoiceDAO invoiceDAO;
    private final InvoiceItemDAO invoiceItemDAO;
    private final ClientDAO clientDAO;
    private final ClientLocationDAO locationDAO;
    private final EquipmentQuoteDAO quoteDAO;
    private final PMAgreementDAO pmaDAO;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private boolean isUpdatingDate = false;

    private JTable invoicesTable;
    private DefaultTableModel invoiceTableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;
    private JComboBox<String> totalFilterCombo;
    private JComboBox<String> balanceDueFilterCombo;

    private JComboBox<Client> clientCombo;
    private JComboBox<ClientLocation> billLocationCombo;
    private JComboBox<ClientLocation> jobLocationCombo;
    private JComboBox<EquipmentQuote> equipmentQuoteCombo;
    private JComboBox<PreventiveMaintenanceAgreement> pmaCombo;

    private JTextField invoiceNumberField;
    private JTextField poNumberField;
    private JTextField invoiceDateField;
    private JTextField dueDateField;
    private JTextField termsField;
    private JTextField paidDateField;
    private JComboBox<String> statusCombo;

    private JTextField subtotalAmountField;
    private JTextField taxRateField;
    private JTextField taxAmountField;
    private JTextField totalAmountField;
    private JTextField paymentsAppliedField;
    private JTextField balanceDueField;

    private JTable invoiceItemsTable;
    private DefaultTableModel itemsTableModel;
    private final List<InvoiceItem> invoiceItems = new ArrayList<>();

    private Invoice selectedInvoice;
    private List<EquipmentQuote> allQuotes = new ArrayList<>();
    private List<PreventiveMaintenanceAgreement> allAgreements = new ArrayList<>();

    public InvoicesPanel() {
        this.invoiceDAO = new InvoiceDAO();
        this.invoiceItemDAO = new InvoiceItemDAO();
        this.clientDAO = new ClientDAO();
        this.locationDAO = new ClientLocationDAO();
        this.quoteDAO = new EquipmentQuoteDAO();
        this.pmaDAO = new PMAgreementDAO();

        initComponents();
        loadClients();
        loadInvoices();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel at top
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(25);
        searchField.setFont(ModernUIHelper.NORMAL_FONT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new java.awt.Color(180, 180, 180), 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterInvoices(); }
            public void removeUpdate(DocumentEvent e) { filterInvoices(); }
            public void changedUpdate(DocumentEvent e) { filterInvoices(); }
        });
        searchPanel.add(searchField);

        searchPanel.add(javax.swing.Box.createHorizontalStrut(20));

        // Filter by Status
        searchPanel.add(new JLabel("Filter by Status:"));
        statusFilterCombo = new JComboBox<>(new String[]{"Show All", "Draft", "Open", "Paid", "Overdue", "Void"});
        statusFilterCombo.setFont(ModernUIHelper.NORMAL_FONT);
        statusFilterCombo.setPreferredSize(new java.awt.Dimension(120, 38));
        statusFilterCombo.addActionListener(e -> filterInvoices());
        searchPanel.add(statusFilterCombo);

        // Filter by Total
        searchPanel.add(new JLabel("Filter by Total:"));
        totalFilterCombo = new JComboBox<>(new String[]{"Show All", "Most", "Least"});
        totalFilterCombo.setFont(ModernUIHelper.NORMAL_FONT);
        totalFilterCombo.setPreferredSize(new java.awt.Dimension(120, 38));
        totalFilterCombo.addActionListener(e -> filterInvoices());
        searchPanel.add(totalFilterCombo);

        // Filter by Balance Due
        searchPanel.add(new JLabel("Filter by Balance Due:"));
        balanceDueFilterCombo = new JComboBox<>(new String[]{"Show All", "Most", "Least"});
        balanceDueFilterCombo.setFont(ModernUIHelper.NORMAL_FONT);
        balanceDueFilterCombo.setPreferredSize(new java.awt.Dimension(120, 38));
        balanceDueFilterCombo.addActionListener(e -> filterInvoices());
        searchPanel.add(balanceDueFilterCombo);

        add(searchPanel, BorderLayout.NORTH);

        // Invoice table
        String[] columns = {"ID", "Invoice #", "Client", "Job Location", "Invoice Date", "Total", "Balance Due", "Due Date", "Status", "Bill Location"};
        invoiceTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        invoicesTable = new JTable(invoiceTableModel);
        invoicesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ModernUIHelper.styleTable(invoicesTable);
        ModernUIHelper.addTableToggleBehavior(invoicesTable, this::clearForm);

        invoicesTable.getColumnModel().getColumn(0).setMinWidth(0);
        invoicesTable.getColumnModel().getColumn(0).setMaxWidth(0);
        invoicesTable.getColumnModel().getColumn(0).setWidth(0);

        // Set column widths: Invoice # (9), Invoice Date (10), Due Date (10), Status (7)
        // Amount and Balance Due should be smaller but readable
        invoicesTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        invoicesTable.getColumnModel().getColumn(1).setMaxWidth(90);
        invoicesTable.getColumnModel().getColumn(4).setPreferredWidth(100);  // Invoice Date
        invoicesTable.getColumnModel().getColumn(4).setMaxWidth(110);
        invoicesTable.getColumnModel().getColumn(5).setPreferredWidth(95);  // Total
        invoicesTable.getColumnModel().getColumn(5).setMaxWidth(105);
        invoicesTable.getColumnModel().getColumn(6).setPreferredWidth(105);  // Balance Due
        invoicesTable.getColumnModel().getColumn(6).setMaxWidth(115);
        invoicesTable.getColumnModel().getColumn(7).setPreferredWidth(100);  // Due Date
        invoicesTable.getColumnModel().getColumn(7).setMaxWidth(110);
        invoicesTable.getColumnModel().getColumn(8).setPreferredWidth(70);  // Status
        invoicesTable.getColumnModel().getColumn(8).setMaxWidth(80);

        invoicesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && invoicesTable.getSelectedRow() >= 0) {
                loadSelectedInvoice();
            }
        });

        JScrollPane tableScroll = new JScrollPane(invoicesTable);
        add(tableScroll, BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(ModernUIHelper.createModernBorder("Invoice Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        addSectionLabel(formPanel, gbc, row++, "CLIENT INFO");

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Client:*"), gbc);
        gbc.gridx = 1;
        clientCombo = new JComboBox<>();
        clientCombo.addActionListener(e -> onClientChanged());
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

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Equipment Quote:"), gbc);
        gbc.gridx = 1;
        equipmentQuoteCombo = new JComboBox<>();
        equipmentQuoteCombo.addActionListener(e -> {
            if (equipmentQuoteCombo.getSelectedItem() != null) {
                pmaCombo.setSelectedIndex(-1);
            }
        });
        ModernUIHelper.styleComboBox(equipmentQuoteCombo);
        formPanel.add(equipmentQuoteCombo, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("PM Agreement:"), gbc);
        gbc.gridx = 1;
        pmaCombo = new JComboBox<>();
        pmaCombo.addActionListener(e -> {
            if (pmaCombo.getSelectedItem() != null) {
                equipmentQuoteCombo.setSelectedIndex(-1);
            }
        });
        ModernUIHelper.styleComboBox(pmaCombo);
        formPanel.add(pmaCombo, gbc);
        row++;

        addSectionLabel(formPanel, gbc, row++, "INVOICE INFO");

        row = addField(formPanel, gbc, row, "Invoice #*:", invoiceNumberField = new JTextField(20));
        row = addField(formPanel, gbc, row, "PO Number:", poNumberField = new JTextField(20));
        row = addField(formPanel, gbc, row, "Invoice Date (MM/dd/yyyy):*", invoiceDateField = new JTextField(20));
        setupDateFormatting(invoiceDateField);
        row = addField(formPanel, gbc, row, "Due Date (MM/dd/yyyy):*", dueDateField = new JTextField(20));
        setupDateFormatting(dueDateField);
        row = addField(formPanel, gbc, row, "Terms:*", termsField = new JTextField(20));

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Status:*"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(new String[]{"Draft", "Open", "Paid", "Overdue", "Void"});
        statusCombo.addActionListener(e -> recalculateTotalsAndDisplay());
        ModernUIHelper.styleComboBox(statusCombo);
        formPanel.add(statusCombo, gbc);
        row++;

        row = addField(formPanel, gbc, row, "Paid Date (MM/dd/yyyy):", paidDateField = new JTextField(20));
        setupDateFormatting(paidDateField);

        addSectionLabel(formPanel, gbc, row++, "AMOUNTS");

        subtotalAmountField = new JTextField(20);
        subtotalAmountField.setEditable(false);
        subtotalAmountField.setFocusable(false);
        subtotalAmountField.setBackground(new Color(240, 240, 240));
        row = addField(formPanel, gbc, row, "Subtotal", subtotalAmountField);

        taxRateField = new JTextField(20);
        row = addField(formPanel, gbc, row, "Tax Rate %:*", taxRateField);

        taxAmountField = new JTextField(20);
        taxAmountField.setEditable(false);
        taxAmountField.setFocusable(false);
        taxAmountField.setBackground(new Color(240, 240, 240));
        row = addField(formPanel, gbc, row, "Tax Amount:", taxAmountField);

        totalAmountField = new JTextField(20);
        totalAmountField.setEditable(false);
        totalAmountField.setFocusable(false);
        totalAmountField.setBackground(new Color(240, 240, 240));
        row = addField(formPanel, gbc, row, "Total:", totalAmountField);

        paymentsAppliedField = new JTextField(20);
        row = addField(formPanel, gbc, row, "Payments Applied:*", paymentsAppliedField);

        balanceDueField = new JTextField(20);
        balanceDueField.setEditable(false);
        balanceDueField.setFocusable(false);
        balanceDueField.setBackground(new Color(240, 240, 240));
        row = addField(formPanel, gbc, row, "Balance Due:", balanceDueField);

        addSectionLabel(formPanel, gbc, row++, "INVOICE ITEMS");

        itemsTableModel = new DefaultTableModel(new String[]{"Row #", "Description", "Qty", "Rate", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        invoiceItemsTable = new JTable(itemsTableModel);
        ModernUIHelper.addTableToggleBehavior(invoiceItemsTable);
        JScrollPane itemsScroll = new JScrollPane(invoiceItemsTable);
        itemsScroll.setPreferredSize(new Dimension(450, 150));
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(itemsScroll, gbc);
        gbc.gridwidth = 1;
        row++;

        JPanel itemButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addItemButton = new JButton("Add Item");
        addItemButton.addActionListener(e -> addInvoiceItem());
        ModernUIHelper.styleButton(addItemButton, "success");
        itemButtons.add(addItemButton);

        JButton editItemButton = new JButton("Edit Item");
        editItemButton.addActionListener(e -> editInvoiceItem());
        ModernUIHelper.styleButton(editItemButton, "primary");
        itemButtons.add(editItemButton);

        JButton deleteItemButton = new JButton("Delete Item");
        deleteItemButton.addActionListener(e -> deleteInvoiceItem());
        ModernUIHelper.styleButton(deleteItemButton, "danger");
        itemButtons.add(deleteItemButton);

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(itemButtons, gbc);
        gbc.gridwidth = 1;
        row++;

        JPanel actionButtons = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addInvoice());
        ModernUIHelper.styleButton(addButton, "success");
        actionButtons.add(addButton);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateInvoice());
        ModernUIHelper.styleButton(updateButton, "warning");
        actionButtons.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteInvoice());
        ModernUIHelper.styleButton(deleteButton, "danger");
        actionButtons.add(deleteButton);

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearForm());
        ModernUIHelper.styleButton(clearButton, "secondary");
        actionButtons.add(clearButton);

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(actionButtons, gbc);

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(formScroll, BorderLayout.EAST);

        DocumentListener totalsListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { recalculateTotalsAndDisplay(); }
            public void removeUpdate(DocumentEvent e) { recalculateTotalsAndDisplay(); }
            public void changedUpdate(DocumentEvent e) { recalculateTotalsAndDisplay(); }
        };
        taxRateField.getDocument().addDocumentListener(totalsListener);
        paymentsAppliedField.getDocument().addDocumentListener(totalsListener);

        clearForm();
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

    private int addField(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        ModernUIHelper.styleTextField(field);
        panel.add(field, gbc);
        return row + 1;
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

    /**
     * Convert up to 8 digits into MM/dd/yyyy style string.
     */
    private String formatDateDigits(String digits) {
        if (digits.isEmpty()) return "";
        if (digits.length() <= 2) return digits;
        if (digits.length() <= 4) {
            return digits.substring(0, 2) + "/" + digits.substring(2);
        }
        return digits.substring(0, 2) + "/" + digits.substring(2, 4) + "/" + digits.substring(4);
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

    private void onClientChanged() {
        Client client = (Client) clientCombo.getSelectedItem();
        billLocationCombo.removeAllItems();
        jobLocationCombo.removeAllItems();
        equipmentQuoteCombo.removeAllItems();
        pmaCombo.removeAllItems();

        if (client == null) {
            return;
        }

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

        loadQuotesAndAgreementsForClient(client.getClientId());

        // Default to no selection
        billLocationCombo.setSelectedIndex(-1);
        jobLocationCombo.setSelectedIndex(-1);
        equipmentQuoteCombo.setSelectedIndex(-1);
        pmaCombo.setSelectedIndex(-1);
    }

    private void loadQuotesAndAgreementsForClient(int clientId) {
        try {
            allQuotes = quoteDAO.getAllQuotes();
            allAgreements = pmaDAO.getAllAgreements();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading quotes/agreements: " + ex.getMessage());
            return;
        }

        // Note: null option already added in onClientChanged()
        for (EquipmentQuote quote : allQuotes) {
            if (clientId == quote.getClientId()) {
                equipmentQuoteCombo.addItem(quote);
            }
        }

        for (PreventiveMaintenanceAgreement pma : allAgreements) {
            if (clientId == pma.getClientId()) {
                pmaCombo.addItem(pma);
            }
        }
    }

    private void loadInvoices() {
        try {
            List<Invoice> invoices = invoiceDAO.getAllInvoices();
            invoiceTableModel.setRowCount(0);
            for (Invoice invoice : invoices) {
                invoiceTableModel.addRow(new Object[]{
                    invoice.getInvoiceId(),
                    invoice.getInvoiceNumber(),
                    getClientName(invoice.getClientId()),
                    getLocationName(invoice.getClientJobLocationId()),
                    formatDate(invoice.getInvoiceDate()),
                    invoice.getTotalAmount(),
                    invoice.getBalanceDue(),
                    formatDate(invoice.getDueDate()),
                    invoice.getStatus(),
                    getLocationName(invoice.getClientBillingLocationId())
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading invoices: " + ex.getMessage());
        }
    }

    private void filterInvoices() {
        String searchText = searchField.getText().toLowerCase().trim();
        String statusFilter = (String) statusFilterCombo.getSelectedItem();
        String amountFilter = (String) amountFilterCombo.getSelectedItem();

        try {
            List<Invoice> invoices = invoiceDAO.getAllInvoices();
            invoiceTableModel.setRowCount(0);
            for (Invoice invoice : invoices) {
                // Search across multiple fields
                String invoiceNumber = invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber().toLowerCase() : "";
                String clientName = getClientName(invoice.getClientId()).toLowerCase();
                String status = invoice.getStatus() != null ? invoice.getStatus().toLowerCase() : "";
                String jobLocation = getLocationName(invoice.getClientJobLocationId()).toLowerCase();
                String billLocation = getLocationName(invoice.getClientBillingLocationId()).toLowerCase();

                boolean searchMatches = searchText.isEmpty() ||
                    invoiceNumber.contains(searchText) ||
                    clientName.contains(searchText) ||
                    status.contains(searchText) ||
                    jobLocation.contains(searchText) ||
                    billLocation.contains(searchText);

                // Apply status filter
                boolean statusMatches = "Show All".equals(statusFilter) ||
                    statusFilter.equalsIgnoreCase(invoice.getStatus());

                // Apply amount filter
                boolean amountMatches = true;
                if (!"Show All".equals(amountFilter) && invoice.getTotalAmount() != null) {
                    BigDecimal total = invoice.getTotalAmount();
                    if ("Over $1000".equals(amountFilter)) {
                        amountMatches = total.compareTo(new BigDecimal("1000")) > 0;
                    } else if ("Over $500".equals(amountFilter)) {
                        amountMatches = total.compareTo(new BigDecimal("500")) > 0;
                    } else if ("Under $500".equals(amountFilter)) {
                        amountMatches = total.compareTo(new BigDecimal("500")) < 0;
                    }
                }

                if (searchMatches && statusMatches && amountMatches) {
                    invoiceTableModel.addRow(new Object[]{
                        invoice.getInvoiceId(),
                        invoice.getInvoiceNumber(),
                        getClientName(invoice.getClientId()),
                        getLocationName(invoice.getClientJobLocationId()),
                        formatDate(invoice.getInvoiceDate()),
                        invoice.getTotalAmount(),
                        invoice.getBalanceDue(),
                        formatDate(invoice.getDueDate()),
                        invoice.getStatus(),
                        getLocationName(invoice.getClientBillingLocationId())
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error filtering invoices: " + ex.getMessage());
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

    private void selectQuote(Integer quoteId) {
        if (quoteId == null) return;
        for (int i = 0; i < equipmentQuoteCombo.getItemCount(); i++) {
            EquipmentQuote q = equipmentQuoteCombo.getItemAt(i);
            if (q != null && quoteId.equals(q.getEquipmentQuoteId())) {
                equipmentQuoteCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void selectPma(Integer pmaId) {
        if (pmaId == null) return;
        for (int i = 0; i < pmaCombo.getItemCount(); i++) {
            PreventiveMaintenanceAgreement pma = pmaCombo.getItemAt(i);
            if (pma != null && pmaId.equals(pma.getPreventiveMaintenanceAgreementId())) {
                pmaCombo.setSelectedIndex(i);
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

    private String formatDate(java.time.LocalDate date) {
        return date == null ? "" : date.format(dateFormatter);
    }

    private void loadSelectedInvoice() {
        int row = invoicesTable.getSelectedRow();
        if (row < 0) return;

        Integer id = (Integer) invoiceTableModel.getValueAt(row, 0);
        try {
            selectedInvoice = invoiceDAO.getInvoiceById(id);
            if (selectedInvoice == null) return;

            selectClientById(selectedInvoice.getClientId());
            onClientChanged();
            selectLocation(billLocationCombo, selectedInvoice.getClientBillingLocationId());
            selectLocation(jobLocationCombo, selectedInvoice.getClientJobLocationId());
            selectQuote(selectedInvoice.getEquipmentQuoteId());
            selectPma(selectedInvoice.getPreventiveMaintenanceAgreementId());

            invoiceNumberField.setText(selectedInvoice.getInvoiceNumber());
            poNumberField.setText(selectedInvoice.getPoNumber() != null ? selectedInvoice.getPoNumber() : "");
            invoiceDateField.setText(formatDate(selectedInvoice.getInvoiceDate()));
            dueDateField.setText(formatDate(selectedInvoice.getDueDate()));
            termsField.setText(selectedInvoice.getTerms());
            statusCombo.setSelectedItem(selectedInvoice.getStatus());
            paidDateField.setText(formatDate(selectedInvoice.getPaidDate()));
            taxRateField.setText(valueOrDefault(selectedInvoice.getTaxRatePercent(), "6.00"));
            paymentsAppliedField.setText(valueOrDefault(selectedInvoice.getPaymentsApplied(), "0.00"));

            invoiceItems.clear();
            invoiceItems.addAll(invoiceItemDAO.getItemsByInvoiceId(selectedInvoice.getInvoiceId()));
            refreshItemsTable();

            applyTotalsFromModel(selectedInvoice);
            recalculateTotalsAndDisplay();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading invoice: " + ex.getMessage());
        }
    }

    private void applyTotalsFromModel(Invoice invoice) {
        subtotalAmountField.setText(valueOrDefault(invoice.getSubtotalAmount(), "0.00"));
        taxAmountField.setText(valueOrDefault(invoice.getTaxAmount(), ""));
        totalAmountField.setText(valueOrDefault(invoice.getTotalAmount(), ""));
        balanceDueField.setText(valueOrDefault(invoice.getBalanceDue(), ""));
    }

    private void addInvoice() {
        if (!validateForm()) return;

        Invoice invoice = new Invoice();
        populateInvoiceFromForm(invoice);

        try {
            if (invoiceDAO.addInvoice(invoice)) {
                saveInvoiceItems(invoice.getInvoiceId());
                JOptionPane.showMessageDialog(this, "Invoice added successfully!");
                clearForm();
                loadInvoices();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding invoice: " + ex.getMessage());
        }
    }

    private void updateInvoice() {
        if (selectedInvoice == null) return;
        if (!validateForm()) return;

        populateInvoiceFromForm(selectedInvoice);

        try {
            if (invoiceDAO.updateInvoice(selectedInvoice)) {
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
        if (confirm != JOptionPane.YES_OPTION) return;

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

    private void populateInvoiceFromForm(Invoice invoice) {
        Client client = (Client) clientCombo.getSelectedItem();
        ClientLocation bill = (ClientLocation) billLocationCombo.getSelectedItem();
        ClientLocation job = (ClientLocation) jobLocationCombo.getSelectedItem();

        invoice.setClientId(client.getClientId());
        invoice.setClientBillingLocationId(bill.getClientLocationId());
        invoice.setClientJobLocationId(job.getClientLocationId());

        EquipmentQuote quote = (EquipmentQuote) equipmentQuoteCombo.getSelectedItem();
        PreventiveMaintenanceAgreement pma = (PreventiveMaintenanceAgreement) pmaCombo.getSelectedItem();
        invoice.setEquipmentQuoteId(quote != null ? quote.getEquipmentQuoteId() : null);
        invoice.setPreventiveMaintenanceAgreementId(pma != null ? pma.getPreventiveMaintenanceAgreementId() : null);

        invoice.setInvoiceNumber(invoiceNumberField.getText().trim());
        invoice.setPoNumber(poNumberField.getText().trim().isEmpty() ? null : poNumberField.getText().trim());
        invoice.setInvoiceDate(java.time.LocalDate.parse(invoiceDateField.getText().trim(), dateFormatter));
        invoice.setDueDate(java.time.LocalDate.parse(dueDateField.getText().trim(), dateFormatter));
        invoice.setTerms(termsField.getText().trim());
        invoice.setStatus((String) statusCombo.getSelectedItem());
        invoice.setPaidDate(paidDateField.getText().trim().isEmpty()
                ? null
                : java.time.LocalDate.parse(paidDateField.getText().trim(), dateFormatter));

        invoice.setSubtotalAmount(calculateSubtotal());
        invoice.setTaxRatePercent(new BigDecimal(taxRateField.getText().trim()));
        invoice.setPaymentsApplied(new BigDecimal(paymentsAppliedField.getText().trim()));
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
        if (invoiceItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Add at least one invoice item.");
            return false;
        }
        try {
            java.time.LocalDate invoiceDate = java.time.LocalDate.parse(invoiceDateField.getText().trim(), dateFormatter);
            java.time.LocalDate dueDate = java.time.LocalDate.parse(dueDateField.getText().trim(), dateFormatter);
            if (dueDate.isBefore(invoiceDate)) {
                JOptionPane.showMessageDialog(this, "Due date cannot be before invoice date.");
                return false;
            }
            if (!paidDateField.getText().trim().isEmpty()) {
                java.time.LocalDate.parse(paidDateField.getText().trim(), dateFormatter);
            }
            new BigDecimal(taxRateField.getText().trim());
            new BigDecimal(paymentsAppliedField.getText().trim());
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
        equipmentQuoteCombo.removeAllItems();
        pmaCombo.removeAllItems();
        billLocationCombo.setSelectedIndex(-1);
        jobLocationCombo.setSelectedIndex(-1);
        equipmentQuoteCombo.setSelectedIndex(-1);
        pmaCombo.setSelectedIndex(-1);
        invoiceNumberField.setText("");
        poNumberField.setText("");
        invoiceDateField.setText(java.time.LocalDate.now().format(dateFormatter));
        dueDateField.setText(java.time.LocalDate.now().plusDays(30).format(dateFormatter));
        termsField.setText("Net 30");
        statusCombo.setSelectedItem("Draft");
        paidDateField.setText("");
        taxRateField.setText("6.00");
        paymentsAppliedField.setText("0.00");
        subtotalAmountField.setText("0.00");
        taxAmountField.setText("0.00");
        totalAmountField.setText("0.00");
        balanceDueField.setText("0.00");
        invoiceItems.clear();
        refreshItemsTable();
        selectedInvoice = null;
        invoicesTable.clearSelection();
        recalculateTotalsAndDisplay();
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

        gbc.gridx = 0; gbc.gridy = 0;
        itemPanel.add(new JLabel("Description:*"), gbc);
        gbc.gridx = 1;
        itemPanel.add(descriptionField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        itemPanel.add(new JLabel("Quantity:*"), gbc);
        gbc.gridx = 1;
        itemPanel.add(qtyField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        itemPanel.add(new JLabel("Rate:*"), gbc);
        gbc.gridx = 1;
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

                InvoiceItem item = new InvoiceItem();
                item.setRowNumber(invoiceItems.size() + 1);
                item.setDescription(description);
                item.setQty(qty);
                item.setRate(rate);
                item.setTotalAmount(total);
                invoiceItems.add(item);
                refreshItemsTable();
                recalculateTotalsAndDisplay();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity or rate.");
            }
        }
    }

    private void editInvoiceItem() {
        int selectedRow = invoiceItemsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select an item to edit.");
            return;
        }
        InvoiceItem item = invoiceItems.get(selectedRow);

        JTextField descriptionField = new JTextField(item.getDescription(), 30);
        JTextField qtyField = new JTextField(item.getQty().toString(), 10);
        JTextField rateField = new JTextField(item.getRate().toString(), 10);

        JPanel itemPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        itemPanel.add(new JLabel("Description:*"), gbc);
        gbc.gridx = 1;
        itemPanel.add(descriptionField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        itemPanel.add(new JLabel("Quantity:*"), gbc);
        gbc.gridx = 1;
        itemPanel.add(qtyField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        itemPanel.add(new JLabel("Rate:*"), gbc);
        gbc.gridx = 1;
        itemPanel.add(rateField, gbc);

        int result = JOptionPane.showConfirmDialog(this, itemPanel, "Edit Invoice Item", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                item.setDescription(descriptionField.getText().trim());
                item.setQty(new BigDecimal(qtyField.getText().trim()));
                item.setRate(new BigDecimal(rateField.getText().trim()));
                item.setTotalAmount(item.getQty().multiply(item.getRate()));
                refreshItemsTable();
                recalculateTotalsAndDisplay();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity or rate.");
            }
        }
    }

    private void deleteInvoiceItem() {
        int selectedRow = invoiceItemsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select an item to delete.");
            return;
        }
        invoiceItems.remove(selectedRow);
        for (int i = 0; i < invoiceItems.size(); i++) {
            invoiceItems.get(i).setRowNumber(i + 1);
        }
        refreshItemsTable();
        recalculateTotalsAndDisplay();
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

    private void saveInvoiceItems(Integer invoiceId) throws SQLException {
        if (invoiceId == null) return;
        invoiceItemDAO.deleteAllItemsByInvoiceId(invoiceId);
        for (InvoiceItem item : invoiceItems) {
            item.setInvoiceId(invoiceId);
            invoiceItemDAO.addInvoiceItem(item);
        }
    }

    private BigDecimal calculateSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (InvoiceItem item : invoiceItems) {
            if (item.getTotalAmount() != null) {
                subtotal = subtotal.add(item.getTotalAmount());
            }
        }
        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    private void recalculateTotalsAndDisplay() {
        try {
            BigDecimal subtotal = calculateSubtotal();
            BigDecimal taxRate = new BigDecimal(taxRateField.getText().trim());
            BigDecimal paymentsApplied = new BigDecimal(paymentsAppliedField.getText().trim());

            BigDecimal taxAmount = subtotal.multiply(taxRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal totalAmount = subtotal.add(taxAmount);

            BigDecimal balanceDue;
            if ("Void".equalsIgnoreCase((String) statusCombo.getSelectedItem())) {
                balanceDue = BigDecimal.ZERO;
            } else {
                balanceDue = totalAmount.subtract(paymentsApplied);
            }

            subtotalAmountField.setText(subtotal.toString());
            taxAmountField.setText(taxAmount.toString());
            totalAmountField.setText(totalAmount.toString());
            balanceDueField.setText(balanceDue.setScale(2, RoundingMode.HALF_UP).toString());
        } catch (Exception e) {
            // ignore parse issues while typing
        }
    }

    private String valueOrDefault(BigDecimal value, String defaultValue) {
        return value == null ? defaultValue : value.toString();
    }

    public void refreshData() {
        loadClients();
        loadInvoices();
    }
}
