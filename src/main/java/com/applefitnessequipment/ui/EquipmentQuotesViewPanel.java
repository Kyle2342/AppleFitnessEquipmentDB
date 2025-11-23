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

public class EquipmentQuotesViewPanel extends JPanel {
    private JTable quotesTable;
    private DefaultTableModel tableModel;

    public EquipmentQuotesViewPanel() {
        initComponents();
        loadQuotes();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel - Info
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Equipment Quotes - View Only"));
        add(topPanel, BorderLayout.NORTH);

        // Center Panel - Table
        String[] columns = {"Quote #", "Client ID", "Quote Date", "Contact", "Salesperson", 
                          "Status", "Subtotal", "Freight", "Tax", "Total"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        quotesTable = new JTable(tableModel);
        quotesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(quotesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Info Panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));
        String info = "<html><body style='padding: 10px;'>" +
            "<p>This is a read-only view of equipment quotes in the database.</p>" +
            "<p>Full equipment quote management functionality can be added in future updates.</p>" +
            "</body></html>";
        infoPanel.add(new JLabel(info), BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
    }

    private void loadQuotes() {
        String sql = "SELECT QuoteNumber, ClientID, QuoteDate, ContactName, SalespersonName, " +
                    "Status, SubtotalAmount, FreightAmount, SalesTaxAmount, QuoteTotalAmount " +
                    "FROM equipmentquotes ORDER BY QuoteDate DESC";
        
        tableModel.setRowCount(0);
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("QuoteNumber"),
                    rs.getInt("ClientID"),
                    rs.getDate("QuoteDate"),
                    rs.getString("ContactName"),
                    rs.getString("SalespersonName"),
                    rs.getString("Status"),
                    rs.getBigDecimal("SubtotalAmount"),
                    rs.getBigDecimal("FreightAmount"),
                    rs.getBigDecimal("SalesTaxAmount"),
                    rs.getBigDecimal("QuoteTotalAmount")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading quotes: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
