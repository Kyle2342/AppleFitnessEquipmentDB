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

public class EquipmentQuoteDAO {
    
    public List<EquipmentQuote> getAllQuotes() throws SQLException {
        List<EquipmentQuote> quotes = new ArrayList<>();
        String sql = "SELECT * FROM equipmentquotes ORDER BY QuoteDate DESC";
        
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
        String sql = "SELECT * FROM equipmentquotes WHERE QuoteID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
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
        String sql = "INSERT INTO equipmentquotes (ClientID, QuoteNumber, QuoteDate, ContactName, " +
                    "SalespersonName, Status, SubtotalAmount, TaxAmount, TotalAmount) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quote.getClientId());
            pstmt.setString(2, quote.getQuoteNumber());
            pstmt.setDate(3, java.sql.Date.valueOf(quote.getQuoteDate()));
            pstmt.setString(4, quote.getContactName());
            pstmt.setString(5, quote.getSalespersonName());
            pstmt.setString(6, quote.getStatus());
            pstmt.setBigDecimal(7, quote.getSubtotalAmount());
            pstmt.setBigDecimal(8, quote.getTaxAmount());
            pstmt.setBigDecimal(9, quote.getTotalAmount());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateQuote(EquipmentQuote quote) throws SQLException {
        String sql = "UPDATE equipmentquotes SET ClientID = ?, QuoteNumber = ?, QuoteDate = ?, " +
                    "ContactName = ?, SalespersonName = ?, Status = ?, SubtotalAmount = ?, " +
                    "TaxAmount = ?, TotalAmount = ? WHERE QuoteID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quote.getClientId());
            pstmt.setString(2, quote.getQuoteNumber());
            pstmt.setDate(3, java.sql.Date.valueOf(quote.getQuoteDate()));
            pstmt.setString(4, quote.getContactName());
            pstmt.setString(5, quote.getSalespersonName());
            pstmt.setString(6, quote.getStatus());
            pstmt.setBigDecimal(7, quote.getSubtotalAmount());
            pstmt.setBigDecimal(8, quote.getTaxAmount());
            pstmt.setBigDecimal(9, quote.getTotalAmount());
            pstmt.setInt(10, quote.getQuoteId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteQuote(int quoteId) throws SQLException {
        String sql = "DELETE FROM equipmentquotes WHERE QuoteID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quoteId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    private EquipmentQuote extractFromResultSet(ResultSet rs) throws SQLException {
        EquipmentQuote quote = new EquipmentQuote();
        quote.setQuoteId(rs.getInt("QuoteID"));
        quote.setClientId(rs.getInt("ClientID"));
        quote.setQuoteNumber(rs.getString("QuoteNumber"));
        
        if (rs.getDate("QuoteDate") != null) {
            quote.setQuoteDate(rs.getDate("QuoteDate").toLocalDate());
        }
        
        quote.setContactName(rs.getString("ContactName"));
        quote.setSalespersonName(rs.getString("SalespersonName"));
        quote.setStatus(rs.getString("Status"));
        quote.setSubtotalAmount(rs.getBigDecimal("SubtotalAmount"));
        quote.setTaxAmount(rs.getBigDecimal("TaxAmount"));
        quote.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        
        return quote;
    }
}
