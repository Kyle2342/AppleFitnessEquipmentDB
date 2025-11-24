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
                    "PreventiveMaintenanceAgreementID, EquipmentQuoteID, InvoiceNumber, QuoteNumber, " +
                    "InvoiceDate, DueDate, PONumber, Terms, Status, " +
                    "SubtotalAmount, TaxRatePercent, PaymentsApplied, PaidDate, " +
                    "ReturnedCheckFee, InterestPercent, InterestStartDays, InterestIntervalDays, " +
                    "FromCompanyName, FromStreetAddress, FromCity, FromState, FromZIPCode, FromPhone, FromFax, " +
                    "ClientTypeSnapshot, ClientCompanyNameSnapshot, ClientFirstNameSnapshot, ClientLastNameSnapshot, " +
                    "BillToCompanyName, BillToContactName, BillToStreetAddress, BillToBuildingName, " +
                    "BillToSuite, BillToRoomNumber, BillToDepartment, BillToCity, BillToCounty, " +
                    "BillToState, BillToZIPCode, BillToCountry, BillToPhone, BillToPONumber, " +
                    "JobAtCompanyName, JobAtContactName, JobAtStreetAddress, JobAtBuildingName, " +
                    "JobAtSuite, JobAtRoomNumber, JobAtDepartment, JobAtCity, JobAtCounty, " +
                    "JobAtState, JobAtZIPCode, JobAtCountry, JobAtPhone, JobAtPONumber) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int idx = 1;
            pstmt.setInt(idx++, invoice.getClientId());
            pstmt.setInt(idx++, invoice.getBillingLocationId());
            pstmt.setInt(idx++, invoice.getJobLocationId());

            if (invoice.getPreventiveMaintenanceAgreementId() != null) {
                pstmt.setInt(idx++, invoice.getPreventiveMaintenanceAgreementId());
            } else {
                pstmt.setNull(idx++, java.sql.Types.INTEGER);
            }

            if (invoice.getEquipmentQuoteId() != null) {
                pstmt.setInt(idx++, invoice.getEquipmentQuoteId());
            } else {
                pstmt.setNull(idx++, java.sql.Types.INTEGER);
            }

            pstmt.setString(idx++, invoice.getInvoiceNumber());
            pstmt.setString(idx++, invoice.getQuoteNumber());
            pstmt.setDate(idx++, java.sql.Date.valueOf(invoice.getInvoiceDate()));
            pstmt.setDate(idx++, java.sql.Date.valueOf(invoice.getDueDate()));
            pstmt.setString(idx++, invoice.getPoNumber());
            pstmt.setString(idx++, invoice.getTerms());
            pstmt.setString(idx++, invoice.getStatus());
            pstmt.setBigDecimal(idx++, invoice.getSubtotalAmount());
            pstmt.setBigDecimal(idx++, invoice.getTaxRatePercent());
            pstmt.setBigDecimal(idx++, invoice.getPaymentsApplied());

            if (invoice.getPaidDate() != null) {
                pstmt.setDate(idx++, java.sql.Date.valueOf(invoice.getPaidDate()));
            } else {
                pstmt.setNull(idx++, java.sql.Types.DATE);
            }

            pstmt.setBigDecimal(idx++, invoice.getReturnedCheckFee());
            pstmt.setBigDecimal(idx++, invoice.getInterestPercent());
            pstmt.setInt(idx++, invoice.getInterestStartDays());
            pstmt.setInt(idx++, invoice.getInterestIntervalDays());

            // FROM fields
            pstmt.setString(idx++, invoice.getFromCompanyName());
            pstmt.setString(idx++, invoice.getFromStreetAddress());
            pstmt.setString(idx++, invoice.getFromCity());
            pstmt.setString(idx++, invoice.getFromState());
            pstmt.setString(idx++, invoice.getFromZIPCode());
            pstmt.setString(idx++, invoice.getFromPhone());
            pstmt.setString(idx++, invoice.getFromFax());

            // Client snapshot fields
            pstmt.setString(idx++, invoice.getClientTypeSnapshot());
            pstmt.setString(idx++, invoice.getClientCompanyNameSnapshot());
            pstmt.setString(idx++, invoice.getClientFirstNameSnapshot());
            pstmt.setString(idx++, invoice.getClientLastNameSnapshot());

            // BILL TO fields
            pstmt.setString(idx++, invoice.getBillToCompanyName());
            pstmt.setString(idx++, invoice.getBillToContactName());
            pstmt.setString(idx++, invoice.getBillToStreetAddress());
            pstmt.setString(idx++, invoice.getBillToBuildingName());
            pstmt.setString(idx++, invoice.getBillToSuite());
            pstmt.setString(idx++, invoice.getBillToRoomNumber());
            pstmt.setString(idx++, invoice.getBillToDepartment());
            pstmt.setString(idx++, invoice.getBillToCity());
            pstmt.setString(idx++, invoice.getBillToCounty());
            pstmt.setString(idx++, invoice.getBillToState());
            pstmt.setString(idx++, invoice.getBillToZIPCode());
            pstmt.setString(idx++, invoice.getBillToCountry());
            pstmt.setString(idx++, invoice.getBillToPhone());
            pstmt.setString(idx++, invoice.getBillToPONumber());

            // JOB AT fields
            pstmt.setString(idx++, invoice.getJobAtCompanyName());
            pstmt.setString(idx++, invoice.getJobAtContactName());
            pstmt.setString(idx++, invoice.getJobAtStreetAddress());
            pstmt.setString(idx++, invoice.getJobAtBuildingName());
            pstmt.setString(idx++, invoice.getJobAtSuite());
            pstmt.setString(idx++, invoice.getJobAtRoomNumber());
            pstmt.setString(idx++, invoice.getJobAtDepartment());
            pstmt.setString(idx++, invoice.getJobAtCity());
            pstmt.setString(idx++, invoice.getJobAtCounty());
            pstmt.setString(idx++, invoice.getJobAtState());
            pstmt.setString(idx++, invoice.getJobAtZIPCode());
            pstmt.setString(idx++, invoice.getJobAtCountry());
            pstmt.setString(idx++, invoice.getJobAtPhone());
            pstmt.setString(idx++, invoice.getJobAtPONumber());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateInvoice(Invoice invoice) throws SQLException {
        String sql = "UPDATE invoices SET ClientID = ?, BillingLocationID = ?, JobLocationID = ?, " +
                    "PreventiveMaintenanceAgreementID = ?, EquipmentQuoteID = ?, InvoiceNumber = ?, QuoteNumber = ?, " +
                    "InvoiceDate = ?, DueDate = ?, PONumber = ?, Terms = ?, Status = ?, " +
                    "SubtotalAmount = ?, TaxRatePercent = ?, " +
                    "PaymentsApplied = ?, PaidDate = ?, " +
                    "ReturnedCheckFee = ?, InterestPercent = ?, InterestStartDays = ?, InterestIntervalDays = ?, " +
                    "FromCompanyName = ?, FromStreetAddress = ?, FromCity = ?, FromState = ?, FromZIPCode = ?, FromPhone = ?, FromFax = ?, " +
                    "ClientTypeSnapshot = ?, ClientCompanyNameSnapshot = ?, ClientFirstNameSnapshot = ?, ClientLastNameSnapshot = ?, " +
                    "BillToCompanyName = ?, BillToContactName = ?, BillToStreetAddress = ?, BillToBuildingName = ?, " +
                    "BillToSuite = ?, BillToRoomNumber = ?, BillToDepartment = ?, BillToCity = ?, BillToCounty = ?, " +
                    "BillToState = ?, BillToZIPCode = ?, BillToCountry = ?, BillToPhone = ?, BillToPONumber = ?, " +
                    "JobAtCompanyName = ?, JobAtContactName = ?, JobAtStreetAddress = ?, JobAtBuildingName = ?, " +
                    "JobAtSuite = ?, JobAtRoomNumber = ?, JobAtDepartment = ?, JobAtCity = ?, JobAtCounty = ?, " +
                    "JobAtState = ?, JobAtZIPCode = ?, JobAtCountry = ?, JobAtPhone = ?, JobAtPONumber = ? " +
                    "WHERE InvoiceID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int idx = 1;
            pstmt.setInt(idx++, invoice.getClientId());
            pstmt.setInt(idx++, invoice.getBillingLocationId());
            pstmt.setInt(idx++, invoice.getJobLocationId());

            if (invoice.getPreventiveMaintenanceAgreementId() != null) {
                pstmt.setInt(idx++, invoice.getPreventiveMaintenanceAgreementId());
            } else {
                pstmt.setNull(idx++, java.sql.Types.INTEGER);
            }

            if (invoice.getEquipmentQuoteId() != null) {
                pstmt.setInt(idx++, invoice.getEquipmentQuoteId());
            } else {
                pstmt.setNull(idx++, java.sql.Types.INTEGER);
            }

            pstmt.setString(idx++, invoice.getInvoiceNumber());
            pstmt.setString(idx++, invoice.getQuoteNumber());
            pstmt.setDate(idx++, java.sql.Date.valueOf(invoice.getInvoiceDate()));
            pstmt.setDate(idx++, java.sql.Date.valueOf(invoice.getDueDate()));
            pstmt.setString(idx++, invoice.getPoNumber());
            pstmt.setString(idx++, invoice.getTerms());
            pstmt.setString(idx++, invoice.getStatus());
            pstmt.setBigDecimal(idx++, invoice.getSubtotalAmount());
            pstmt.setBigDecimal(idx++, invoice.getTaxRatePercent());
            pstmt.setBigDecimal(idx++, invoice.getPaymentsApplied());

            if (invoice.getPaidDate() != null) {
                pstmt.setDate(idx++, java.sql.Date.valueOf(invoice.getPaidDate()));
            } else {
                pstmt.setNull(idx++, java.sql.Types.DATE);
            }

            pstmt.setBigDecimal(idx++, invoice.getReturnedCheckFee());
            pstmt.setBigDecimal(idx++, invoice.getInterestPercent());
            pstmt.setInt(idx++, invoice.getInterestStartDays());
            pstmt.setInt(idx++, invoice.getInterestIntervalDays());

            // FROM fields
            pstmt.setString(idx++, invoice.getFromCompanyName());
            pstmt.setString(idx++, invoice.getFromStreetAddress());
            pstmt.setString(idx++, invoice.getFromCity());
            pstmt.setString(idx++, invoice.getFromState());
            pstmt.setString(idx++, invoice.getFromZIPCode());
            pstmt.setString(idx++, invoice.getFromPhone());
            pstmt.setString(idx++, invoice.getFromFax());

            // Client snapshot fields
            pstmt.setString(idx++, invoice.getClientTypeSnapshot());
            pstmt.setString(idx++, invoice.getClientCompanyNameSnapshot());
            pstmt.setString(idx++, invoice.getClientFirstNameSnapshot());
            pstmt.setString(idx++, invoice.getClientLastNameSnapshot());

            // BILL TO fields
            pstmt.setString(idx++, invoice.getBillToCompanyName());
            pstmt.setString(idx++, invoice.getBillToContactName());
            pstmt.setString(idx++, invoice.getBillToStreetAddress());
            pstmt.setString(idx++, invoice.getBillToBuildingName());
            pstmt.setString(idx++, invoice.getBillToSuite());
            pstmt.setString(idx++, invoice.getBillToRoomNumber());
            pstmt.setString(idx++, invoice.getBillToDepartment());
            pstmt.setString(idx++, invoice.getBillToCity());
            pstmt.setString(idx++, invoice.getBillToCounty());
            pstmt.setString(idx++, invoice.getBillToState());
            pstmt.setString(idx++, invoice.getBillToZIPCode());
            pstmt.setString(idx++, invoice.getBillToCountry());
            pstmt.setString(idx++, invoice.getBillToPhone());
            pstmt.setString(idx++, invoice.getBillToPONumber());

            // JOB AT fields
            pstmt.setString(idx++, invoice.getJobAtCompanyName());
            pstmt.setString(idx++, invoice.getJobAtContactName());
            pstmt.setString(idx++, invoice.getJobAtStreetAddress());
            pstmt.setString(idx++, invoice.getJobAtBuildingName());
            pstmt.setString(idx++, invoice.getJobAtSuite());
            pstmt.setString(idx++, invoice.getJobAtRoomNumber());
            pstmt.setString(idx++, invoice.getJobAtDepartment());
            pstmt.setString(idx++, invoice.getJobAtCity());
            pstmt.setString(idx++, invoice.getJobAtCounty());
            pstmt.setString(idx++, invoice.getJobAtState());
            pstmt.setString(idx++, invoice.getJobAtZIPCode());
            pstmt.setString(idx++, invoice.getJobAtCountry());
            pstmt.setString(idx++, invoice.getJobAtPhone());
            pstmt.setString(idx++, invoice.getJobAtPONumber());

            pstmt.setInt(idx++, invoice.getInvoiceId());

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

        int pmaId = rs.getInt("PreventiveMaintenanceAgreementID");
        if (!rs.wasNull()) {
            invoice.setPreventiveMaintenanceAgreementId(pmaId);
        }

        int eqId = rs.getInt("EquipmentQuoteID");
        if (!rs.wasNull()) {
            invoice.setEquipmentQuoteId(eqId);
        }

        invoice.setInvoiceNumber(rs.getString("InvoiceNumber"));
        invoice.setQuoteNumber(rs.getString("QuoteNumber"));

        if (rs.getDate("InvoiceDate") != null) {
            invoice.setInvoiceDate(rs.getDate("InvoiceDate").toLocalDate());
        }
        if (rs.getDate("DueDate") != null) {
            invoice.setDueDate(rs.getDate("DueDate").toLocalDate());
        }
        if (rs.getDate("PaidDate") != null) {
            invoice.setPaidDate(rs.getDate("PaidDate").toLocalDate());
        }

        invoice.setPoNumber(rs.getString("PONumber"));
        invoice.setTerms(rs.getString("Terms"));
        invoice.setStatus(rs.getString("Status"));
        invoice.setSubtotalAmount(rs.getBigDecimal("SubtotalAmount"));
        invoice.setTaxRatePercent(rs.getBigDecimal("TaxRatePercent"));
        invoice.setTaxAmount(rs.getBigDecimal("TaxAmount"));
        invoice.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        invoice.setPaymentsApplied(rs.getBigDecimal("PaymentsApplied"));
        invoice.setBalanceDue(rs.getBigDecimal("BalanceDue"));  // Generated column from database
        invoice.setReturnedCheckFee(rs.getBigDecimal("ReturnedCheckFee"));
        invoice.setInterestPercent(rs.getBigDecimal("InterestPercent"));
        invoice.setInterestStartDays(rs.getInt("InterestStartDays"));
        invoice.setInterestIntervalDays(rs.getInt("InterestIntervalDays"));

        // FROM fields
        invoice.setFromCompanyName(rs.getString("FromCompanyName"));
        invoice.setFromStreetAddress(rs.getString("FromStreetAddress"));
        invoice.setFromCity(rs.getString("FromCity"));
        invoice.setFromState(rs.getString("FromState"));
        invoice.setFromZIPCode(rs.getString("FromZIPCode"));
        invoice.setFromPhone(rs.getString("FromPhone"));
        invoice.setFromFax(rs.getString("FromFax"));

        // Client snapshot fields
        invoice.setClientTypeSnapshot(rs.getString("ClientTypeSnapshot"));
        invoice.setClientCompanyNameSnapshot(rs.getString("ClientCompanyNameSnapshot"));
        invoice.setClientFirstNameSnapshot(rs.getString("ClientFirstNameSnapshot"));
        invoice.setClientLastNameSnapshot(rs.getString("ClientLastNameSnapshot"));

        // BILL TO fields
        invoice.setBillToCompanyName(rs.getString("BillToCompanyName"));
        invoice.setBillToContactName(rs.getString("BillToContactName"));
        invoice.setBillToStreetAddress(rs.getString("BillToStreetAddress"));
        invoice.setBillToBuildingName(rs.getString("BillToBuildingName"));
        invoice.setBillToSuite(rs.getString("BillToSuite"));
        invoice.setBillToRoomNumber(rs.getString("BillToRoomNumber"));
        invoice.setBillToDepartment(rs.getString("BillToDepartment"));
        invoice.setBillToCity(rs.getString("BillToCity"));
        invoice.setBillToCounty(rs.getString("BillToCounty"));
        invoice.setBillToState(rs.getString("BillToState"));
        invoice.setBillToZIPCode(rs.getString("BillToZIPCode"));
        invoice.setBillToCountry(rs.getString("BillToCountry"));
        invoice.setBillToPhone(rs.getString("BillToPhone"));
        invoice.setBillToPONumber(rs.getString("BillToPONumber"));

        // JOB AT fields
        invoice.setJobAtCompanyName(rs.getString("JobAtCompanyName"));
        invoice.setJobAtContactName(rs.getString("JobAtContactName"));
        invoice.setJobAtStreetAddress(rs.getString("JobAtStreetAddress"));
        invoice.setJobAtBuildingName(rs.getString("JobAtBuildingName"));
        invoice.setJobAtSuite(rs.getString("JobAtSuite"));
        invoice.setJobAtRoomNumber(rs.getString("JobAtRoomNumber"));
        invoice.setJobAtDepartment(rs.getString("JobAtDepartment"));
        invoice.setJobAtCity(rs.getString("JobAtCity"));
        invoice.setJobAtCounty(rs.getString("JobAtCounty"));
        invoice.setJobAtState(rs.getString("JobAtState"));
        invoice.setJobAtZIPCode(rs.getString("JobAtZIPCode"));
        invoice.setJobAtCountry(rs.getString("JobAtCountry"));
        invoice.setJobAtPhone(rs.getString("JobAtPhone"));
        invoice.setJobAtPONumber(rs.getString("JobAtPONumber"));

        if (rs.getTimestamp("CreatedAt") != null) {
            invoice.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
        }
        if (rs.getTimestamp("UpdatedAt") != null) {
            invoice.setUpdatedAt(rs.getTimestamp("UpdatedAt").toLocalDateTime());
        }

        return invoice;
    }
}
