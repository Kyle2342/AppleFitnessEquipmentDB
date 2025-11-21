package com.applefitnessequipment.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.applefitnessequipment.db.DBConnection;
import com.applefitnessequipment.model.Invoice;

public class InvoiceDAO {
    
    public List<Invoice> getAllInvoices() throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoices ORDER BY InvoiceDate DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                invoices.add(extractInvoiceFromResultSet(rs));
            }
        }
        return invoices;
    }
    
    public Invoice getInvoiceById(int invoiceId) throws SQLException {
        String sql = "SELECT * FROM invoices WHERE InvoiceID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, invoiceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractInvoiceFromResultSet(rs);
                }
            }
        }
        return null;
    }
    
    public boolean addInvoice(Invoice invoice) throws SQLException {
        String sql = "INSERT INTO invoices (ClientID, BillingLocationID, JobLocationID, " +
                    "InvoiceNumber, InvoiceDate, DueDate, Terms, Status, SubtotalAmount, " +
                    "TaxRatePercent, TaxAmount, TotalAmount) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, invoice.getClientId());
            pstmt.setInt(2, invoice.getBillingLocationId());
            pstmt.setInt(3, invoice.getJobLocationId());
            pstmt.setString(4, invoice.getInvoiceNumber());
            pstmt.setDate(5, java.sql.Date.valueOf(invoice.getInvoiceDate()));
            pstmt.setDate(6, java.sql.Date.valueOf(invoice.getDueDate()));
            pstmt.setString(7, invoice.getTerms());
            pstmt.setString(8, invoice.getStatus());
            pstmt.setBigDecimal(9, invoice.getSubtotalAmount());
            pstmt.setBigDecimal(10, invoice.getTaxRatePercent());
            pstmt.setBigDecimal(11, invoice.getTaxAmount());
            pstmt.setBigDecimal(12, invoice.getTotalAmount());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateInvoice(Invoice invoice) throws SQLException {
        String sql = "UPDATE invoices SET ClientID = ?, BillingLocationID = ?, JobLocationID = ?, " +
                    "InvoiceNumber = ?, InvoiceDate = ?, DueDate = ?, Terms = ?, Status = ?, " +
                    "SubtotalAmount = ?, TaxRatePercent = ?, TaxAmount = ?, TotalAmount = ?, " +
                    "PaymentsApplied = ? WHERE InvoiceID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, invoice.getClientId());
            pstmt.setInt(2, invoice.getBillingLocationId());
            pstmt.setInt(3, invoice.getJobLocationId());
            pstmt.setString(4, invoice.getInvoiceNumber());
            pstmt.setDate(5, java.sql.Date.valueOf(invoice.getInvoiceDate()));
            pstmt.setDate(6, java.sql.Date.valueOf(invoice.getDueDate()));
            pstmt.setString(7, invoice.getTerms());
            pstmt.setString(8, invoice.getStatus());
            pstmt.setBigDecimal(9, invoice.getSubtotalAmount());
            pstmt.setBigDecimal(10, invoice.getTaxRatePercent());
            pstmt.setBigDecimal(11, invoice.getTaxAmount());
            pstmt.setBigDecimal(12, invoice.getTotalAmount());
            pstmt.setBigDecimal(13, invoice.getPaymentsApplied());
            pstmt.setInt(14, invoice.getInvoiceId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteInvoice(int invoiceId) throws SQLException {
        String sql = "DELETE FROM invoices WHERE InvoiceID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, invoiceId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    private Invoice extractInvoiceFromResultSet(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(rs.getInt("InvoiceID"));
        invoice.setClientId(rs.getInt("ClientID"));
        invoice.setBillingLocationId(rs.getInt("BillingLocationID"));
        invoice.setJobLocationId(rs.getInt("JobLocationID"));
        invoice.setInvoiceNumber(rs.getString("InvoiceNumber"));
        
        if (rs.getDate("InvoiceDate") != null) {
            invoice.setInvoiceDate(rs.getDate("InvoiceDate").toLocalDate());
        }
        if (rs.getDate("DueDate") != null) {
            invoice.setDueDate(rs.getDate("DueDate").toLocalDate());
        }
        
        invoice.setTerms(rs.getString("Terms"));
        invoice.setStatus(rs.getString("Status"));
        invoice.setSubtotalAmount(rs.getBigDecimal("SubtotalAmount"));
        invoice.setTaxRatePercent(rs.getBigDecimal("TaxRatePercent"));
        invoice.setTaxAmount(rs.getBigDecimal("TaxAmount"));
        invoice.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        invoice.setPaymentsApplied(rs.getBigDecimal("PaymentsApplied"));
        
        return invoice;
    }
}
