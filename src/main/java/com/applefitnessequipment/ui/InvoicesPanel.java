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
import com.applefitnessequipment.dao.InvoiceDAO;
import com.applefitnessequipment.model.Client;
import com.applefitnessequipment.model.ClientLocation;
import com.applefitnessequipment.model.Invoice;

public class InvoicesPanel extends JPanel {
    private InvoiceDAO invoiceDAO;
    private ClientDAO clientDAO;
    private ClientLocationDAO locationDAO;
    private JTable invoicesTable;
    private DefaultTableModel tableModel;
    private JTextField invoiceNumberField, invoiceDateField, dueDateField, termsField;
    private JTextField subtotalField, taxRateField, taxAmountField, totalAmountField, paymentsField;
    private JComboBox<Client> clientCombo;
    private JTextField searchField;  // For filtering table
    private JComboBox<ClientLocation> billLocationCombo, jobLocationCombo;
    private JComboBox<String> statusCombo;
    private JButton addButton, updateButton, deleteButton, clearButton;
    private Invoice selectedInvoice;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private List<Invoice> allInvoices;  // Cache all invoices for filtering

    public InvoicesPanel() {
        invoiceDAO = new InvoiceDAO();
        clientDAO = new ClientDAO();
        locationDAO = new ClientLocationDAO();
        initComponents();
        loadClients();
        loadInvoices();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel with Search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search Client:"));

        searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterInvoices(); }
            public void removeUpdate(DocumentEvent e) { filterInvoices(); }
            public void changedUpdate(DocumentEvent e) { filterInvoices(); }
        });
        ModernUIHelper.styleTextField(searchField);
        topPanel.add(searchField);

        add(topPanel, BorderLayout.NORTH);

        // Center Panel - Table
        String[] columns = {"ID", "Invoice #", "Client", "Invoice Date", "Due Date", "Status", "Total", "Balance"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        invoicesTable = new JTable(tableModel);
        invoicesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Apply modern styling
        ModernUIHelper.styleTable(invoicesTable);

        // Hide ID column only
        invoicesTable.getColumnModel().getColumn(0).setMinWidth(0);
        invoicesTable.getColumnModel().getColumn(0).setMaxWidth(0);
        invoicesTable.getColumnModel().getColumn(0).setWidth(0);
        
        invoicesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && invoicesTable.getSelectedRow() >= 0) {
                loadSelectedInvoice();
            }
        });
        
        invoicesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (invoicesTable.rowAtPoint(e.getPoint()) == -1) clearForm();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(invoicesTable);

        // Clear form when clicking on scroll pane background
        scrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                invoicesTable.clearSelection();
                clearForm();
            }
        });

        add(scrollPane, BorderLayout.CENTER);

        // Right Panel - Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(ModernUIHelper.createModernBorder("Invoice Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Client:*"), gbc);
        gbc.gridx = 1;
        clientCombo = new JComboBox<>();
        clientCombo.addActionListener(e -> loadLocationsForClient());
        ModernUIHelper.styleComboBox(clientCombo);
        formPanel.add(clientCombo, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Bill Location:*"), gbc);
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
        formPanel.add(new JLabel("Invoice #:*"), gbc);
        gbc.gridx = 1;
        invoiceNumberField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(invoiceNumberField);
        formPanel.add(invoiceNumberField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Invoice Date:*"), gbc);
        gbc.gridx = 1;
        invoiceDateField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        ModernUIHelper.styleTextField(invoiceDateField);
        formPanel.add(invoiceDateField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Due Date:*"), gbc);
        gbc.gridx = 1;
        dueDateField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        dueDateField.setText(LocalDate.now().plusDays(30).format(dateFormatter));
        ModernUIHelper.styleTextField(dueDateField);
        formPanel.add(dueDateField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Terms:*"), gbc);
        gbc.gridx = 1;
        termsField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        termsField.setText("Net 30");
        ModernUIHelper.styleTextField(termsField);
        formPanel.add(termsField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Status:*"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(new String[]{"Draft", "Open", "Paid", "Overdue", "Void"});
        ModernUIHelper.styleComboBox(statusCombo);
        formPanel.add(statusCombo, gbc);
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
        formPanel.add(new JLabel("Total:*"), gbc);
        gbc.gridx = 1;
        totalAmountField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        totalAmountField.setText("0.00");
        ModernUIHelper.styleTextField(totalAmountField);
        formPanel.add(totalAmountField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Payments:"), gbc);
        gbc.gridx = 1;
        paymentsField = new JTextField(ModernUIHelper.STANDARD_FIELD_WIDTH);
        paymentsField.setText("0.00");
        ModernUIHelper.styleTextField(paymentsField);
        formPanel.add(paymentsField, gbc);
        row++;

        // Buttons
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

        // Clear selection when clicking on form panel background
        formPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                invoicesTable.clearSelection();
                clearForm();
            }
        });

        JScrollPane formScrollPane = new JScrollPane(formPanel);
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

    private void loadInvoices() {
        try {
            allInvoices = invoiceDAO.getAllInvoices();
            filterInvoices();  // Apply current filter
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading invoices: " + ex.getMessage());
        }
    }

    private void filterInvoices() {
        if (allInvoices == null) return;

        tableModel.setRowCount(0);
        String searchText = searchField.getText().toLowerCase().trim();

        for (Invoice inv : allInvoices) {
            // Get client name for filtering
            String clientName = getClientNameForInvoice(inv);

            // Filter by search text (matches client name)
            boolean include = searchText.isEmpty() || clientName.toLowerCase().contains(searchText);

            if (include) {
                BigDecimal balance = inv.getTotalAmount().subtract(inv.getPaymentsApplied());

                tableModel.addRow(new Object[]{
                    inv.getInvoiceId(),
                    inv.getInvoiceNumber(),
                    clientName,
                    inv.getInvoiceDate(),
                    inv.getDueDate(),
                    inv.getStatus(),
                    inv.getTotalAmount(),
                    balance
                });
            }
        }
    }

    private String getClientNameForInvoice(Invoice inv) {
        // Try to get client by ID first
        if (inv.getClientId() != null) {
            try {
                Client client = clientDAO.getClientById(inv.getClientId());
                if (client != null) {
                    // Use company name if available, otherwise contact name
                    if (client.getCompanyName() != null && !client.getCompanyName().isEmpty()) {
                        return client.getCompanyName();
                    } else {
                        return client.getFirstName() + " " + client.getLastName();
                    }
                }
            } catch (SQLException e) {
                // Fall through to use stored bill-to info
            }
        }

        // Fall back to stored bill-to information
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
                // Try to select client - check if client still exists
                boolean clientFound = false;
                if (selectedInvoice.getClientId() != null) {
                    for (int i = 0; i < clientCombo.getItemCount(); i++) {
                        if (clientCombo.getItemAt(i).getClientId().equals(selectedInvoice.getClientId())) {
                            clientCombo.setSelectedIndex(i);
                            clientFound = true;
                            break;
                        }
                    }
                }

                // If client not found, show stored bill-to info in a message or disable editing
                if (!clientFound) {
                    // Get the stored client name for display
                    String storedClientName = "";
                    if (selectedInvoice.getBillToCompanyName() != null && !selectedInvoice.getBillToCompanyName().isEmpty()) {
                        storedClientName = selectedInvoice.getBillToCompanyName();
                    } else if (selectedInvoice.getBillToContactName() != null && !selectedInvoice.getBillToContactName().isEmpty()) {
                        storedClientName = selectedInvoice.getBillToContactName();
                    } else {
                        storedClientName = "Unknown Client";
                    }

                    // Clear selection and show warning
                    if (clientCombo.getItemCount() > 0) {
                        clientCombo.setSelectedIndex(-1);
                    }

                    // Show info about the original client
                    JOptionPane.showMessageDialog(this,
                        "Original client no longer exists.\n" +
                        "Stored Bill To: " + storedClientName + "\n" +
                        "Address: " + (selectedInvoice.getBillToStreetAddress() != null ? selectedInvoice.getBillToStreetAddress() : "N/A"),
                        "Client Not Found", JOptionPane.INFORMATION_MESSAGE);
                }

                // Load and select locations (only if client was found)
                if (clientFound) {
                    loadLocationsForClient();
                    for (int i = 0; i < billLocationCombo.getItemCount(); i++) {
                        if (billLocationCombo.getItemAt(i).getClientLocationId().equals(selectedInvoice.getBillingLocationId())) {
                            billLocationCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                    for (int i = 0; i < jobLocationCombo.getItemCount(); i++) {
                        if (jobLocationCombo.getItemAt(i).getClientLocationId().equals(selectedInvoice.getJobLocationId())) {
                            jobLocationCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                }

                invoiceNumberField.setText(selectedInvoice.getInvoiceNumber());
                invoiceDateField.setText(selectedInvoice.getInvoiceDate().format(dateFormatter));
                dueDateField.setText(selectedInvoice.getDueDate().format(dateFormatter));
                termsField.setText(selectedInvoice.getTerms());
                statusCombo.setSelectedItem(selectedInvoice.getStatus());
                subtotalField.setText(selectedInvoice.getSubtotalAmount().toString());
                taxRateField.setText(selectedInvoice.getTaxRatePercent().toString());
                taxAmountField.setText(selectedInvoice.getTaxAmount().toString());
                totalAmountField.setText(selectedInvoice.getTotalAmount().toString());
                paymentsField.setText(selectedInvoice.getPaymentsApplied().toString());

                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading invoice: " + ex.getMessage());
        }
    }

    private void addInvoice() {
        if (!validateForm()) return;

        Invoice invoice = new Invoice();
        populateInvoiceFromForm(invoice);

        try {
            if (invoiceDAO.addInvoice(invoice)) {
                JOptionPane.showMessageDialog(this, "Invoice added successfully!");
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
        invoice.setInvoiceNumber(invoiceNumberField.getText().trim());
        invoice.setInvoiceDate(LocalDate.parse(invoiceDateField.getText().trim(), dateFormatter));
        invoice.setDueDate(LocalDate.parse(dueDateField.getText().trim(), dateFormatter));
        invoice.setTerms(termsField.getText().trim());
        invoice.setStatus((String) statusCombo.getSelectedItem());
        invoice.setSubtotalAmount(new BigDecimal(subtotalField.getText().trim()));
        invoice.setTaxRatePercent(new BigDecimal(taxRateField.getText().trim()));
        invoice.setTaxAmount(new BigDecimal(taxAmountField.getText().trim()));
        invoice.setTotalAmount(new BigDecimal(totalAmountField.getText().trim()));
        invoice.setPaymentsApplied(new BigDecimal(paymentsField.getText().trim()));
    }

    private void clearForm() {
        if (clientCombo.getItemCount() > 0) clientCombo.setSelectedIndex(0);
        invoiceNumberField.setText("");  // No auto-generation
        invoiceDateField.setText("");  // No auto-generation
        dueDateField.setText(LocalDate.now().plusDays(30).format(dateFormatter));
        termsField.setText("Net 30");
        statusCombo.setSelectedIndex(0);
        subtotalField.setText("0.00");
        taxRateField.setText("6.00");
        taxAmountField.setText("0.00");
        totalAmountField.setText("0.00");
        paymentsField.setText("0.00");
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
            LocalDate.parse(invoiceDateField.getText().trim(), dateFormatter);
            LocalDate.parse(dueDateField.getText().trim(), dateFormatter);
            new BigDecimal(subtotalField.getText().trim());
            new BigDecimal(taxRateField.getText().trim());
            new BigDecimal(taxAmountField.getText().trim());
            new BigDecimal(totalAmountField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date or number format.");
            return false;
        }
        return true;
    }

    public void refreshData() {
        loadClients();
        loadInvoices();
    }
}
