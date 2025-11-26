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

public class PreventiveMaintenanceViewPanel extends JPanel {
    private JTable pmaTable;
    private DefaultTableModel tableModel;

    public PreventiveMaintenanceViewPanel() {
        initComponents();
        loadAgreements();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel - Info
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Preventive Maintenance Agreements - View Only"));
        add(topPanel, BorderLayout.NORTH);

        // Center Panel - Table
        String[] columns = {"Agreement #", "Client ID", "Start Date", "End Date",
                          "Visit Frequency", "Status", "Visit Price", "Price/Year"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pmaTable = new JTable(tableModel);
        pmaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ModernUIHelper.addTableToggleBehavior(pmaTable);

        JScrollPane scrollPane = new JScrollPane(pmaTable);
        add(scrollPane, BorderLayout.CENTER);

        // Info Panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));
        String info = "<html><body style='padding: 10px;'>" +
            "<p>This is a read-only view of preventive maintenance agreements in the database.</p>" +
            "<p>Full PMA management functionality can be added in future updates.</p>" +
            "</body></html>";
        infoPanel.add(new JLabel(info), BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
    }

    private void loadAgreements() {
        String sql = "SELECT AgreementNumber, ClientID, StartDate, EndDate, " +
                    "VisitFrequency, Status, VisitPrice, PricePerYear " +
                    "FROM PreventiveMaintenanceAgreements ORDER BY StartDate DESC";
        
        tableModel.setRowCount(0);
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("AgreementNumber"),
                    rs.getInt("ClientID"),
                    rs.getDate("StartDate"),
                    rs.getDate("EndDate"),
                    rs.getString("VisitFrequency"),
                    rs.getString("Status"),
                    rs.getBigDecimal("VisitPrice"),
                    rs.getBigDecimal("PricePerYear")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading agreements: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
