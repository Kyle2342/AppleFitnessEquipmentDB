package com.applefitnessequipment.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.applefitnessequipment.db.DBConnection;
import com.applefitnessequipment.model.EquipmentQuoteComplete;

public class EquipmentQuoteCompleteDAO {
    
    public List<EquipmentQuoteComplete> getAllQuotes() throws SQLException {
        List<EquipmentQuoteComplete> quotes = new ArrayList<>();
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
    
    public EquipmentQuoteComplete getQuoteById(int quoteId) throws SQLException {
        String sql = "SELECT * FROM equipmentquotes WHERE EquipmentQuoteID = ?";
        
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
    
    public boolean addQuote(EquipmentQuoteComplete quote) throws SQLException {
        String sql = "INSERT INTO equipmentquotes (ClientID, BillingLocationID, JobAtLocationID, " +
                    "BillToCounty, BillToCity, BillToState, BillToZIPCode, BillToCountry, " +
                    "JobAtCounty, JobAtCity, JobAtState, JobAtZIPCode, JobAtCountry, " +
                    "QuoteNumber, QuoteDate, Status, ContactName, SalespersonName, " +
                    "ShipVia, FreightTerms, PaymentTerms, FOBLocation, " +
                    "TotalDiscountAmount, SubtotalAmount, FreightAmount, ExtendedTotalAmount, " +
                    "SalesTaxRatePercent, SalesTaxAmount, QuoteTotalAmount, SignatureBoolean) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quote.getClientId());
            pstmt.setInt(2, quote.getBillingLocationId());
            pstmt.setInt(3, quote.getJobLocationId());
            pstmt.setString(4, quote.getBillToCounty());
            pstmt.setString(5, quote.getBillToCity());
            pstmt.setString(6, quote.getBillToState());
            pstmt.setString(7, quote.getBillToZipCode());
            pstmt.setString(8, quote.getBillToCountry());
            pstmt.setString(9, quote.getJobAtCounty());
            pstmt.setString(10, quote.getJobAtCity());
            pstmt.setString(11, quote.getJobAtState());
            pstmt.setString(12, quote.getJobAtZipCode());
            pstmt.setString(13, quote.getJobAtCountry());
            pstmt.setString(14, quote.getQuoteNumber());
            pstmt.setDate(15, java.sql.Date.valueOf(quote.getQuoteDate()));
            pstmt.setString(16, quote.getStatus());
            pstmt.setString(17, quote.getContactName());
            pstmt.setString(18, quote.getSalespersonName());
            pstmt.setString(19, quote.getShipVia());
            pstmt.setString(20, quote.getFreightTerms());
            pstmt.setString(21, quote.getPaymentTerms());
            pstmt.setString(22, quote.getFobLocation());
            pstmt.setBigDecimal(23, quote.getTotalDiscountAmount());
            pstmt.setBigDecimal(24, quote.getSubtotalAmount());
            pstmt.setBigDecimal(25, quote.getFreightAmount());
            pstmt.setBigDecimal(26, quote.getExtendedTotalAmount());
            pstmt.setBigDecimal(27, quote.getSalesTaxRatePercent());
            pstmt.setBigDecimal(28, quote.getSalesTaxAmount());
            pstmt.setBigDecimal(29, quote.getQuoteTotalAmount());
            pstmt.setBoolean(30, quote.getSignatureBoolean() != null ? quote.getSignatureBoolean() : false);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateQuote(EquipmentQuoteComplete quote) throws SQLException {
        String sql = "UPDATE equipmentquotes SET ClientID = ?, BillingLocationID = ?, JobAtLocationID = ?, " +
                    "BillToCounty = ?, BillToCity = ?, BillToState = ?, BillToZIPCode = ?, BillToCountry = ?, " +
                    "JobAtCounty = ?, JobAtCity = ?, JobAtState = ?, JobAtZIPCode = ?, JobAtCountry = ?, " +
                    "QuoteNumber = ?, QuoteDate = ?, Status = ?, ContactName = ?, SalespersonName = ?, " +
                    "ShipVia = ?, FreightTerms = ?, PaymentTerms = ?, FOBLocation = ?, " +
                    "TotalDiscountAmount = ?, SubtotalAmount = ?, FreightAmount = ?, ExtendedTotalAmount = ?, " +
                    "SalesTaxRatePercent = ?, SalesTaxAmount = ?, QuoteTotalAmount = ?, SignatureBoolean = ? " +
                    "WHERE EquipmentQuoteID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quote.getClientId());
            pstmt.setInt(2, quote.getBillingLocationId());
            pstmt.setInt(3, quote.getJobLocationId());
            pstmt.setString(4, quote.getBillToCounty());
            pstmt.setString(5, quote.getBillToCity());
            pstmt.setString(6, quote.getBillToState());
            pstmt.setString(7, quote.getBillToZipCode());
            pstmt.setString(8, quote.getBillToCountry());
            pstmt.setString(9, quote.getJobAtCounty());
            pstmt.setString(10, quote.getJobAtCity());
            pstmt.setString(11, quote.getJobAtState());
            pstmt.setString(12, quote.getJobAtZipCode());
            pstmt.setString(13, quote.getJobAtCountry());
            pstmt.setString(14, quote.getQuoteNumber());
            pstmt.setDate(15, java.sql.Date.valueOf(quote.getQuoteDate()));
            pstmt.setString(16, quote.getStatus());
            pstmt.setString(17, quote.getContactName());
            pstmt.setString(18, quote.getSalespersonName());
            pstmt.setString(19, quote.getShipVia());
            pstmt.setString(20, quote.getFreightTerms());
            pstmt.setString(21, quote.getPaymentTerms());
            pstmt.setString(22, quote.getFobLocation());
            pstmt.setBigDecimal(23, quote.getTotalDiscountAmount());
            pstmt.setBigDecimal(24, quote.getSubtotalAmount());
            pstmt.setBigDecimal(25, quote.getFreightAmount());
            pstmt.setBigDecimal(26, quote.getExtendedTotalAmount());
            pstmt.setBigDecimal(27, quote.getSalesTaxRatePercent());
            pstmt.setBigDecimal(28, quote.getSalesTaxAmount());
            pstmt.setBigDecimal(29, quote.getQuoteTotalAmount());
            pstmt.setBoolean(30, quote.getSignatureBoolean() != null ? quote.getSignatureBoolean() : false);
            pstmt.setInt(31, quote.getQuoteId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteQuote(int quoteId) throws SQLException {
        String sql = "DELETE FROM equipmentquotes WHERE EquipmentQuoteID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quoteId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    private EquipmentQuoteComplete extractFromResultSet(ResultSet rs) throws SQLException {
        EquipmentQuoteComplete quote = new EquipmentQuoteComplete();
        quote.setQuoteId(rs.getInt("EquipmentQuoteID"));
        quote.setClientId(rs.getInt("ClientID"));
        quote.setBillingLocationId(rs.getInt("BillingLocationID"));
        quote.setJobLocationId(rs.getInt("JobAtLocationID"));
        quote.setBillToCounty(rs.getString("BillToCounty"));
        quote.setBillToCity(rs.getString("BillToCity"));
        quote.setBillToState(rs.getString("BillToState"));
        quote.setBillToZipCode(rs.getString("BillToZIPCode"));
        quote.setBillToCountry(rs.getString("BillToCountry"));
        quote.setJobAtCounty(rs.getString("JobAtCounty"));
        quote.setJobAtCity(rs.getString("JobAtCity"));
        quote.setJobAtState(rs.getString("JobAtState"));
        quote.setJobAtZipCode(rs.getString("JobAtZIPCode"));
        quote.setJobAtCountry(rs.getString("JobAtCountry"));
        quote.setQuoteNumber(rs.getString("QuoteNumber"));
        
        if (rs.getDate("QuoteDate") != null) {
            quote.setQuoteDate(rs.getDate("QuoteDate").toLocalDate());
        }
        
        quote.setStatus(rs.getString("Status"));
        quote.setContactName(rs.getString("ContactName"));
        quote.setSalespersonName(rs.getString("SalespersonName"));
        quote.setShipVia(rs.getString("ShipVia"));
        quote.setFreightTerms(rs.getString("FreightTerms"));
        quote.setPaymentTerms(rs.getString("PaymentTerms"));
        quote.setFobLocation(rs.getString("FOBLocation"));
        quote.setTotalDiscountAmount(rs.getBigDecimal("TotalDiscountAmount"));
        quote.setSubtotalAmount(rs.getBigDecimal("SubtotalAmount"));
        quote.setFreightAmount(rs.getBigDecimal("FreightAmount"));
        quote.setExtendedTotalAmount(rs.getBigDecimal("ExtendedTotalAmount"));
        quote.setSalesTaxRatePercent(rs.getBigDecimal("SalesTaxRatePercent"));
        quote.setSalesTaxAmount(rs.getBigDecimal("SalesTaxAmount"));
        quote.setQuoteTotalAmount(rs.getBigDecimal("QuoteTotalAmount"));
        quote.setSignatureBoolean(rs.getBoolean("SignatureBoolean"));
        
        return quote;
    }
}
