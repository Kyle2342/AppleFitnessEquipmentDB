package com.applefitnessequipment.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.applefitnessequipment.db.DBConnection;
import com.applefitnessequipment.model.EquipmentQuoteItem;

/**
 * DAO for EquipmentQuotesItems table.
 * @SCHEMA_SINGLE_SOURCE_OF_TRUTH: applefitnessequipmentdb_schema.sql
 */
public class EquipmentQuoteItemDAO {

    public List<EquipmentQuoteItem> getItemsByQuoteId(int quoteId) throws SQLException {
        List<EquipmentQuoteItem> items = new ArrayList<>();
        String sql = "SELECT * FROM EquipmentQuotesItems WHERE EquipmentQuoteID = ? ORDER BY RowNumber";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quoteId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(extractFromResultSet(rs));
                }
            }
        }
        return items;
    }

    public EquipmentQuoteItem getItemById(int itemId) throws SQLException {
        String sql = "SELECT * FROM EquipmentQuotesItems WHERE EquipmentQuoteItemID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, itemId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public boolean addItem(EquipmentQuoteItem item) throws SQLException {
        String sql = "INSERT INTO EquipmentQuotesItems " +
                    "(EquipmentQuoteID, RowNumber, Qty, Model, Description, UnitCost, DiscountUnitPrice) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, item.getEquipmentQuoteId());
            pstmt.setInt(2, item.getRowNumber());
            pstmt.setBigDecimal(3, item.getQty());
            pstmt.setString(4, item.getModel());
            pstmt.setString(5, item.getDescription());
            pstmt.setBigDecimal(6, item.getUnitCost());
            pstmt.setBigDecimal(7, item.getDiscountUnitPrice());

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        item.setEquipmentQuoteItemId(keys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public boolean updateItem(EquipmentQuoteItem item) throws SQLException {
        String sql = "UPDATE EquipmentQuotesItems SET " +
                    "EquipmentQuoteID = ?, RowNumber = ?, Qty = ?, Model = ?, " +
                    "Description = ?, UnitCost = ?, DiscountUnitPrice = ? " +
                    "WHERE EquipmentQuoteItemID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, item.getEquipmentQuoteId());
            pstmt.setInt(2, item.getRowNumber());
            pstmt.setBigDecimal(3, item.getQty());
            pstmt.setString(4, item.getModel());
            pstmt.setString(5, item.getDescription());
            pstmt.setBigDecimal(6, item.getUnitCost());
            pstmt.setBigDecimal(7, item.getDiscountUnitPrice());
            pstmt.setInt(8, item.getEquipmentQuoteItemId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteItem(int itemId) throws SQLException {
        String sql = "DELETE FROM EquipmentQuotesItems WHERE EquipmentQuoteItemID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, itemId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteItemsByQuoteId(int quoteId) throws SQLException {
        String sql = "DELETE FROM EquipmentQuotesItems WHERE EquipmentQuoteID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quoteId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private EquipmentQuoteItem extractFromResultSet(ResultSet rs) throws SQLException {
        EquipmentQuoteItem item = new EquipmentQuoteItem();
        item.setEquipmentQuoteItemId(rs.getInt("EquipmentQuoteItemID"));
        item.setEquipmentQuoteId(rs.getInt("EquipmentQuoteID"));
        item.setRowNumber(rs.getInt("RowNumber"));
        item.setQty(rs.getBigDecimal("Qty"));
        item.setModel(rs.getString("Model"));
        item.setDescription(rs.getString("Description"));
        item.setUnitCost(rs.getBigDecimal("UnitCost"));
        item.setDiscountUnitPrice(rs.getBigDecimal("DiscountUnitPrice"));

        // Read GENERATED column (read-only)
        item.setUnitTotal(rs.getBigDecimal("UnitTotal"));

        return item;
    }
}
