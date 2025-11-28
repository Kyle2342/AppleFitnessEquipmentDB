package com.applefitnessequipment.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.applefitnessequipment.db.DBConnection;
import com.applefitnessequipment.model.EquipmentQuote;

/**
 * DAO for EquipmentQuotes table.
 * @SCHEMA_SINGLE_SOURCE_OF_TRUTH: applefitnessequipmentdb_schema.sql
 */
public class EquipmentQuoteDAO {

    public List<EquipmentQuote> getAllQuotes() throws SQLException {
        List<EquipmentQuote> quotes = new ArrayList<>();
        String sql = "SELECT * FROM EquipmentQuotes ORDER BY QuoteDate DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                quotes.add(extractFromResultSet(rs));
            }
        }
        return quotes;
    }

    public EquipmentQuote getQuoteById(int quoteId) throws SQLException {
        String sql = "SELECT * FROM EquipmentQuotes WHERE EquipmentQuoteID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, quoteId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public boolean addQuote(EquipmentQuote quote) throws SQLException {
        String sql = "INSERT INTO EquipmentQuotes " +
                    "(ClientID, ClientBillingLocationID, ClientJobLocationID, " +
                    "QuoteDate, QuoteNumber, Status, " +
                    "ContactName, SalespersonName, ShipVia, FreightTerms, PaymentTerms, FOBLocation, " +
                    "SubtotalAmount, TotalDiscountAmount, FreightAmount, " +
                    "SalesTaxRatePercent, ClientSignatureBoolean) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, quote.getClientId());
            pstmt.setInt(2, quote.getClientBillingLocationId());
            pstmt.setInt(3, quote.getClientJobLocationId());
            pstmt.setDate(4, java.sql.Date.valueOf(quote.getQuoteDate()));
            pstmt.setString(5, quote.getQuoteNumber());
            pstmt.setString(6, quote.getStatus());
            pstmt.setString(7, quote.getContactName());
            pstmt.setString(8, quote.getSalespersonName());
            pstmt.setString(9, quote.getShipVia());
            pstmt.setString(10, quote.getFreightTerms());
            pstmt.setString(11, quote.getPaymentTerms());
            pstmt.setString(12, quote.getFobLocation());
            pstmt.setBigDecimal(13, quote.getSubtotalAmount());
            pstmt.setBigDecimal(14, quote.getTotalDiscountAmount());
            pstmt.setBigDecimal(15, quote.getFreightAmount());
            pstmt.setBigDecimal(16, quote.getSalesTaxRatePercent());
            pstmt.setBoolean(17, quote.getClientSignatureBoolean());

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        quote.setEquipmentQuoteId(keys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public boolean updateQuote(EquipmentQuote quote) throws SQLException {
        String sql = "UPDATE EquipmentQuotes SET " +
                    "ClientID = ?, ClientBillingLocationID = ?, ClientJobLocationID = ?, " +
                    "QuoteDate = ?, QuoteNumber = ?, Status = ?, " +
                    "ContactName = ?, SalespersonName = ?, ShipVia = ?, FreightTerms = ?, PaymentTerms = ?, FOBLocation = ?, " +
                    "SubtotalAmount = ?, TotalDiscountAmount = ?, FreightAmount = ?, " +
                    "SalesTaxRatePercent = ?, ClientSignatureBoolean = ? " +
                    "WHERE EquipmentQuoteID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quote.getClientId());
            pstmt.setInt(2, quote.getClientBillingLocationId());
            pstmt.setInt(3, quote.getClientJobLocationId());
            pstmt.setDate(4, java.sql.Date.valueOf(quote.getQuoteDate()));
            pstmt.setString(5, quote.getQuoteNumber());
            pstmt.setString(6, quote.getStatus());
            pstmt.setString(7, quote.getContactName());
            pstmt.setString(8, quote.getSalespersonName());
            pstmt.setString(9, quote.getShipVia());
            pstmt.setString(10, quote.getFreightTerms());
            pstmt.setString(11, quote.getPaymentTerms());
            pstmt.setString(12, quote.getFobLocation());
            pstmt.setBigDecimal(13, quote.getSubtotalAmount());
            pstmt.setBigDecimal(14, quote.getTotalDiscountAmount());
            pstmt.setBigDecimal(15, quote.getFreightAmount());
            pstmt.setBigDecimal(16, quote.getSalesTaxRatePercent());
            pstmt.setBoolean(17, quote.getClientSignatureBoolean());
            pstmt.setInt(18, quote.getEquipmentQuoteId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteQuote(int quoteId) throws SQLException {
        String sql = "DELETE FROM EquipmentQuotes WHERE EquipmentQuoteID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quoteId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private EquipmentQuote extractFromResultSet(ResultSet rs) throws SQLException {
        EquipmentQuote quote = new EquipmentQuote();
        quote.setEquipmentQuoteId(rs.getInt("EquipmentQuoteID"));
        quote.setClientId(rs.getInt("ClientID"));
        quote.setClientBillingLocationId(rs.getInt("ClientBillingLocationID"));
        quote.setClientJobLocationId(rs.getInt("ClientJobLocationID"));

        if (rs.getDate("QuoteDate") != null) {
            quote.setQuoteDate(rs.getDate("QuoteDate").toLocalDate());
        }

        quote.setQuoteNumber(rs.getString("QuoteNumber"));
        quote.setStatus(rs.getString("Status"));
        quote.setContactName(rs.getString("ContactName"));
        quote.setSalespersonName(rs.getString("SalespersonName"));
        quote.setShipVia(rs.getString("ShipVia"));
        quote.setFreightTerms(rs.getString("FreightTerms"));
        quote.setPaymentTerms(rs.getString("PaymentTerms"));
        quote.setFobLocation(rs.getString("FOBLocation"));
        quote.setSubtotalAmount(rs.getBigDecimal("SubtotalAmount"));
        quote.setTotalDiscountAmount(rs.getBigDecimal("TotalDiscountAmount"));
        quote.setFreightAmount(rs.getBigDecimal("FreightAmount"));

        // Read GENERATED columns (read-only)
        quote.setExtendedTotalAmount(rs.getBigDecimal("ExtendedTotalAmount"));
        quote.setSalesTaxAmount(rs.getBigDecimal("SalesTaxAmount"));
        quote.setQuoteTotalAmount(rs.getBigDecimal("QuoteTotalAmount"));

        quote.setSalesTaxRatePercent(rs.getBigDecimal("SalesTaxRatePercent"));
        quote.setClientSignatureBoolean(rs.getBoolean("ClientSignatureBoolean"));

        return quote;
    }
}
