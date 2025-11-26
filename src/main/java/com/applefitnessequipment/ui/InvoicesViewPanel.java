package com.applefitnessequipment.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.applefitnessequipment.db.DBConnection;

public class InvoicesViewPanel extends JPanel {
    private JTable invoicesTable;
    private DefaultTableModel tableModel;

    public InvoicesViewPanel() {
        initComponents();
        loadInvoices();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel - Info
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Invoices - View Only"));
        add(topPanel, BorderLayout.NORTH);

        // Center Panel - Table
        String[] columns = {"Invoice #", "Client ID", "Invoice Date", "Due Date", "Status", 
                          "Subtotal", "Tax", "Total", "Balance Due"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        invoicesTable = new JTable(tableModel);
        invoicesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ModernUIHelper.addTableToggleBehavior(invoicesTable);

        JScrollPane scrollPane = new JScrollPane(invoicesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Info Panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));
        String info = "<html><body style='padding: 10px;'>" +
            "<p>This is a read-only view of invoices in the database.</p>" +
            "<p>Full invoice management functionality can be added in future updates.</p>" +
            "</body></html>";
        infoPanel.add(new JLabel(info), BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
    }

    private void loadInvoices() {
        String sql = "SELECT InvoiceNumber, ClientID, InvoiceDate, DueDate, Status, " +
                    "SubtotalAmount, TaxAmount, TotalAmount, BalanceDue " +
                    "FROM Invoices ORDER BY InvoiceDate DESC";
        
        tableModel.setRowCount(0);
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("InvoiceNumber"),
                    rs.getInt("ClientID"),
                    rs.getDate("InvoiceDate"),
                    rs.getDate("DueDate"),
                    rs.getString("Status"),
                    rs.getBigDecimal("SubtotalAmount"),
                    rs.getBigDecimal("TaxAmount"),
                    rs.getBigDecimal("TotalAmount"),
                    rs.getBigDecimal("BalanceDue")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading invoices: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
