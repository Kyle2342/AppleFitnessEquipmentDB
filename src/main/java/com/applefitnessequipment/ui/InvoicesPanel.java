package com.applefitnessequipment.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.applefitnessequipment.dao.ClientDAO;
import com.applefitnessequipment.dao.InvoiceDAO;
import com.applefitnessequipment.model.Client;
import com.applefitnessequipment.model.Invoice;

public class InvoicesPanel extends JPanel {
    private InvoiceDAO invoiceDAO;
    private ClientDAO clientDAO;
    private JTable invoicesTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private DateTimeFormatter displayDateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private List<Invoice> allInvoices;

    public InvoicesPanel() {
        invoiceDAO = new InvoiceDAO();
        clientDAO = new ClientDAO();
        initComponents();
        loadInvoices();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel with Search and Create button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton createButton = new JButton("Create Invoice");
        createButton.addActionListener(e -> openCreateDialog());
        ModernUIHelper.styleButton(createButton, "success");
        topPanel.add(createButton);

        topPanel.add(new JLabel("   Search Client:"));

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
        String[] columns = {"ID", "Invoice Number", "Client", "Invoice Date", "Due Date", "Status", "Total Amount", "Balance Due"};
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

        // Double-click to edit
        invoicesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && invoicesTable.getSelectedRow() >= 0) {
                    openEditDialog();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(invoicesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with action buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> openEditDialog());
        ModernUIHelper.styleButton(editButton, "warning");
        bottomPanel.add(editButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteInvoice());
        ModernUIHelper.styleButton(deleteButton, "danger");
        bottomPanel.add(deleteButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void openCreateDialog() {
        InvoiceDialog dialog = new InvoiceDialog((javax.swing.JFrame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadInvoices();
        }
    }

    private void openEditDialog() {
        int row = invoicesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an invoice to edit.");
            return;
        }

        int invoiceId = (Integer) tableModel.getValueAt(row, 0);
        try {
            Invoice invoice = invoiceDAO.getInvoiceById(invoiceId);
            if (invoice != null) {
                InvoiceDialog dialog = new InvoiceDialog((javax.swing.JFrame) SwingUtilities.getWindowAncestor(this), invoice);
                dialog.setVisible(true);

                if (dialog.isSaved()) {
                    loadInvoices();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading invoice: " + ex.getMessage());
        }
    }

    private void deleteInvoice() {
        int row = invoicesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an invoice to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this invoice?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int invoiceId = (Integer) tableModel.getValueAt(row, 0);
            try {
                if (invoiceDAO.deleteInvoice(invoiceId)) {
                    JOptionPane.showMessageDialog(this, "Invoice deleted successfully!");
                    loadInvoices();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting invoice: " + ex.getMessage());
            }
        }
    }

    private void loadInvoices() {
        try {
            allInvoices = invoiceDAO.getAllInvoices();
            filterInvoices();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading invoices: " + ex.getMessage());
        }
    }

    private void filterInvoices() {
        if (allInvoices == null) return;

        tableModel.setRowCount(0);
        String searchText = searchField.getText().toLowerCase().trim();

        for (Invoice inv : allInvoices) {
            String clientName = getClientNameForInvoice(inv);

            boolean include = searchText.isEmpty() || clientName.toLowerCase().contains(searchText);

            if (include) {
                // Calculate balance due - if paid, show 0
                BigDecimal balance = "Paid".equals(inv.getStatus()) ?
                    BigDecimal.ZERO :
                    inv.getTotalAmount().subtract(inv.getPaymentsApplied());

                tableModel.addRow(new Object[]{
                    inv.getInvoiceId(),
                    inv.getInvoiceNumber(),
                    clientName,
                    inv.getInvoiceDate() != null ? inv.getInvoiceDate().format(displayDateFormatter) : "",
                    inv.getDueDate() != null ? inv.getDueDate().format(displayDateFormatter) : "",
                    inv.getStatus(),
                    inv.getTotalAmount(),
                    balance
                });
            }
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

    public void refreshData() {
        loadInvoices();
    }
}
