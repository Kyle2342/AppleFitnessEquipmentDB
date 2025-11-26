package com.applefitnessequipment.dao;

import com.applefitnessequipment.db.DBConnection;
import com.applefitnessequipment.model.InvoiceItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceItemDAO {

    // Get all items for a specific invoice
    public List<InvoiceItem> getItemsByInvoiceId(Integer invoiceId) throws SQLException {
        List<InvoiceItem> items = new ArrayList<>();
        String sql = "SELECT * FROM InvoicesItems WHERE InvoiceID = ? ORDER BY RowNumber";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, invoiceId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                items.add(mapResultSetToInvoiceItem(rs));
            }
        }

        return items;
    }

    // Add a new invoice item
    public boolean addInvoiceItem(InvoiceItem item) throws SQLException {
        String sql = "INSERT INTO InvoicesItems (InvoiceID, RowNumber, Description, Qty, Rate) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, item.getInvoiceId());
            stmt.setInt(2, item.getRowNumber());
            stmt.setString(3, item.getDescription());
            stmt.setBigDecimal(4, item.getQty());
            stmt.setBigDecimal(5, item.getRate());

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    item.setInvoiceItemId(generatedKeys.getInt(1));
                }
                return true;
            }
        }

        return false;
    }

    // Update an existing invoice item
    public boolean updateInvoiceItem(InvoiceItem item) throws SQLException {
        String sql = "UPDATE InvoicesItems SET RowNumber = ?, Description = ?, Qty = ?, Rate = ? " +
                     "WHERE InvoiceItemID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, item.getRowNumber());
            stmt.setString(2, item.getDescription());
            stmt.setBigDecimal(3, item.getQty());
            stmt.setBigDecimal(4, item.getRate());
            stmt.setInt(5, item.getInvoiceItemId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Delete an invoice item
    public boolean deleteInvoiceItem(Integer invoiceItemId) throws SQLException {
        String sql = "DELETE FROM InvoicesItems WHERE InvoiceItemID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, invoiceItemId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Delete all items for an invoice
    public boolean deleteAllItemsByInvoiceId(Integer invoiceId) throws SQLException {
        String sql = "DELETE FROM InvoicesItems WHERE InvoiceID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, invoiceId);
            return stmt.executeUpdate() >= 0;  // Can be 0 if invoice had no items
        }
    }

    // Helper method to map ResultSet to InvoiceItem
    private InvoiceItem mapResultSetToInvoiceItem(ResultSet rs) throws SQLException {
        InvoiceItem item = new InvoiceItem();
        item.setInvoiceItemId(rs.getInt("InvoiceItemID"));
        item.setInvoiceId(rs.getInt("InvoiceID"));
        item.setRowNumber(rs.getInt("RowNumber"));
        item.setDescription(rs.getString("Description"));
        item.setQty(rs.getBigDecimal("Qty"));
        item.setRate(rs.getBigDecimal("Rate"));
        item.setTotalAmount(rs.getBigDecimal("TotalAmount"));  // Generated column
        return item;
    }
}
