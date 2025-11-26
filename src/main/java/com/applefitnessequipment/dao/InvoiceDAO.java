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

/**
 * DAO for Invoices table.
 * @SCHEMA_SINGLE_SOURCE_OF_TRUTH: applefitnessequipmentdb_schema.sql
 */
public class InvoiceDAO {

    public List<Invoice> getAllInvoices() throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM Invoices ORDER BY InvoiceDate DESC";

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
        String sql = "SELECT * FROM Invoices WHERE InvoiceID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
        String sql = "INSERT INTO Invoices " +
                    "(ClientID, ClientBillingLocationID, ClientJobLocationID, " +
                    "PreventiveMaintenanceAgreementID, EquipmentQuoteID, " +
                    "InvoiceDate, InvoiceNumber, PONumber, Terms, DueDate, Status, " +
                    "SubtotalAmount, TaxRatePercent, PaymentsApplied, PaidDate) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, invoice.getClientId());
            pstmt.setInt(2, invoice.getClientBillingLocationId());
            pstmt.setInt(3, invoice.getClientJobLocationId());

            if (invoice.getPreventiveMaintenanceAgreementId() != null) {
                pstmt.setInt(4, invoice.getPreventiveMaintenanceAgreementId());
            } else {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            }

            if (invoice.getEquipmentQuoteId() != null) {
                pstmt.setInt(5, invoice.getEquipmentQuoteId());
            } else {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            }

            pstmt.setDate(6, java.sql.Date.valueOf(invoice.getInvoiceDate()));
            pstmt.setString(7, invoice.getInvoiceNumber());
            pstmt.setString(8, invoice.getPoNumber());
            pstmt.setString(9, invoice.getTerms());
            pstmt.setDate(10, java.sql.Date.valueOf(invoice.getDueDate()));
            pstmt.setString(11, invoice.getStatus());
            pstmt.setBigDecimal(12, invoice.getSubtotalAmount());
            pstmt.setBigDecimal(13, invoice.getTaxRatePercent());
            pstmt.setBigDecimal(14, invoice.getPaymentsApplied());

            if (invoice.getPaidDate() != null) {
                pstmt.setDate(15, java.sql.Date.valueOf(invoice.getPaidDate()));
            } else {
                pstmt.setNull(15, java.sql.Types.DATE);
            }

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        invoice.setInvoiceId(keys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public boolean updateInvoice(Invoice invoice) throws SQLException {
        String sql = "UPDATE Invoices SET " +
                    "ClientID = ?, ClientBillingLocationID = ?, ClientJobLocationID = ?, " +
                    "PreventiveMaintenanceAgreementID = ?, EquipmentQuoteID = ?, " +
                    "InvoiceDate = ?, InvoiceNumber = ?, PONumber = ?, Terms = ?, DueDate = ?, Status = ?, " +
                    "SubtotalAmount = ?, TaxRatePercent = ?, PaymentsApplied = ?, PaidDate = ? " +
                    "WHERE InvoiceID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, invoice.getClientId());
            pstmt.setInt(2, invoice.getClientBillingLocationId());
            pstmt.setInt(3, invoice.getClientJobLocationId());

            if (invoice.getPreventiveMaintenanceAgreementId() != null) {
                pstmt.setInt(4, invoice.getPreventiveMaintenanceAgreementId());
            } else {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            }

            if (invoice.getEquipmentQuoteId() != null) {
                pstmt.setInt(5, invoice.getEquipmentQuoteId());
            } else {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            }

            pstmt.setDate(6, java.sql.Date.valueOf(invoice.getInvoiceDate()));
            pstmt.setString(7, invoice.getInvoiceNumber());
            pstmt.setString(8, invoice.getPoNumber());
            pstmt.setString(9, invoice.getTerms());
            pstmt.setDate(10, java.sql.Date.valueOf(invoice.getDueDate()));
            pstmt.setString(11, invoice.getStatus());
            pstmt.setBigDecimal(12, invoice.getSubtotalAmount());
            pstmt.setBigDecimal(13, invoice.getTaxRatePercent());
            pstmt.setBigDecimal(14, invoice.getPaymentsApplied());

            if (invoice.getPaidDate() != null) {
                pstmt.setDate(15, java.sql.Date.valueOf(invoice.getPaidDate()));
            } else {
                pstmt.setNull(15, java.sql.Types.DATE);
            }

            pstmt.setInt(16, invoice.getInvoiceId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteInvoice(int invoiceId) throws SQLException {
        String sql = "DELETE FROM Invoices WHERE InvoiceID = ?";

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
        invoice.setClientBillingLocationId(rs.getInt("ClientBillingLocationID"));
        invoice.setClientJobLocationId(rs.getInt("ClientJobLocationID"));

        int pmaId = rs.getInt("PreventiveMaintenanceAgreementID");
        if (!rs.wasNull()) {
            invoice.setPreventiveMaintenanceAgreementId(pmaId);
        }

        int eqId = rs.getInt("EquipmentQuoteID");
        if (!rs.wasNull()) {
            invoice.setEquipmentQuoteId(eqId);
        }

        if (rs.getDate("InvoiceDate") != null) {
            invoice.setInvoiceDate(rs.getDate("InvoiceDate").toLocalDate());
        }

        invoice.setInvoiceNumber(rs.getString("InvoiceNumber"));
        invoice.setPoNumber(rs.getString("PONumber"));
        invoice.setTerms(rs.getString("Terms"));

        if (rs.getDate("DueDate") != null) {
            invoice.setDueDate(rs.getDate("DueDate").toLocalDate());
        }

        invoice.setStatus(rs.getString("Status"));
        invoice.setSubtotalAmount(rs.getBigDecimal("SubtotalAmount"));
        invoice.setTaxRatePercent(rs.getBigDecimal("TaxRatePercent"));

        // Read GENERATED columns (read-only)
        invoice.setTaxAmount(rs.getBigDecimal("TaxAmount"));
        invoice.setTotalAmount(rs.getBigDecimal("TotalAmount"));

        invoice.setPaymentsApplied(rs.getBigDecimal("PaymentsApplied"));

        // Read GENERATED column (read-only)
        invoice.setBalanceDue(rs.getBigDecimal("BalanceDue"));

        if (rs.getDate("PaidDate") != null) {
            invoice.setPaidDate(rs.getDate("PaidDate").toLocalDate());
        }

        return invoice;
    }
}
