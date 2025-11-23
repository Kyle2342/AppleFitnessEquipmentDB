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

    public boolean addQuote(EquipmentQuote quote) throws SQLException {
        String sql = "INSERT INTO equipmentquotes (ClientID, BillingLocationID, JobAtLocationID, " +
                    "FromCompanyName, FromStreetAddress, FromCity, FromState, FromZIPCode, FromPhone, FromFax, " +
                    "ClientTypeSnapshot, ClientCompanyNameSnapshot, ClientFirstNameSnapshot, ClientLastNameSnapshot, " +
                    "BillToCompanyName, BillToContactName, BillToStreetAddress, BillToBuildingName, " +
                    "BillToSuite, BillToRoomNumber, BillToDepartment, BillToCounty, BillToCity, " +
                    "BillToState, BillToZIPCode, BillToCountry, BillToPhone, BillToFax, BillToPONumber, " +
                    "JobAtCompanyName, JobAtContactName, JobAtStreetAddress, JobAtBuildingName, " +
                    "JobAtSuite, JobAtRoomNumber, JobAtDepartment, JobAtCounty, JobAtCity, " +
                    "JobAtState, JobAtZIPCode, JobAtCountry, JobAtEmail, JobAtPONumber, " +
                    "QuoteNumber, QuoteDate, Status, ContactName, SalespersonName, ShipVia, FreightTerms, PaymentTerms, FOBLocation, " +
                    "SubtotalAmount, TotalDiscountAmount, FreightAmount, ExtendedTotalAmount, " +
                    "SalesTaxRatePercent, SalesTaxAmount, QuoteTotalAmount, Notes, TermsAndConditions, ExtraInfo, SignatureBoolean) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int idx = 1;
            pstmt.setInt(idx++, quote.getClientId());
            pstmt.setInt(idx++, quote.getBillingLocationId());
            pstmt.setInt(idx++, quote.getJobAtLocationId());

            // FROM fields
            pstmt.setString(idx++, quote.getFromCompanyName());
            pstmt.setString(idx++, quote.getFromStreetAddress());
            pstmt.setString(idx++, quote.getFromCity());
            pstmt.setString(idx++, quote.getFromState());
            pstmt.setString(idx++, quote.getFromZIPCode());
            pstmt.setString(idx++, quote.getFromPhone());
            pstmt.setString(idx++, quote.getFromFax());

            // Client snapshot fields
            pstmt.setString(idx++, quote.getClientTypeSnapshot());
            pstmt.setString(idx++, quote.getClientCompanyNameSnapshot());
            pstmt.setString(idx++, quote.getClientFirstNameSnapshot());
            pstmt.setString(idx++, quote.getClientLastNameSnapshot());

            // BILL TO fields
            pstmt.setString(idx++, quote.getBillToCompanyName());
            pstmt.setString(idx++, quote.getBillToContactName());
            pstmt.setString(idx++, quote.getBillToStreetAddress());
            pstmt.setString(idx++, quote.getBillToBuildingName());
            pstmt.setString(idx++, quote.getBillToSuite());
            pstmt.setString(idx++, quote.getBillToRoomNumber());
            pstmt.setString(idx++, quote.getBillToDepartment());
            pstmt.setString(idx++, quote.getBillToCounty());
            pstmt.setString(idx++, quote.getBillToCity());
            pstmt.setString(idx++, quote.getBillToState());
            pstmt.setString(idx++, quote.getBillToZIPCode());
            pstmt.setString(idx++, quote.getBillToCountry());
            pstmt.setString(idx++, quote.getBillToPhone());
            pstmt.setString(idx++, quote.getBillToFax());
            pstmt.setString(idx++, quote.getBillToPONumber());

            // JOB AT fields
            pstmt.setString(idx++, quote.getJobAtCompanyName());
            pstmt.setString(idx++, quote.getJobAtContactName());
            pstmt.setString(idx++, quote.getJobAtStreetAddress());
            pstmt.setString(idx++, quote.getJobAtBuildingName());
            pstmt.setString(idx++, quote.getJobAtSuite());
            pstmt.setString(idx++, quote.getJobAtRoomNumber());
            pstmt.setString(idx++, quote.getJobAtDepartment());
            pstmt.setString(idx++, quote.getJobAtCounty());
            pstmt.setString(idx++, quote.getJobAtCity());
            pstmt.setString(idx++, quote.getJobAtState());
            pstmt.setString(idx++, quote.getJobAtZIPCode());
            pstmt.setString(idx++, quote.getJobAtCountry());
            pstmt.setString(idx++, quote.getJobAtEmail());
            pstmt.setString(idx++, quote.getJobAtPONumber());

            // Core quote info
            pstmt.setString(idx++, quote.getQuoteNumber());
            pstmt.setDate(idx++, java.sql.Date.valueOf(quote.getQuoteDate()));
            pstmt.setString(idx++, quote.getStatus());

            // Sales/logistics fields
            pstmt.setString(idx++, quote.getContactName());
            pstmt.setString(idx++, quote.getSalespersonName());
            pstmt.setString(idx++, quote.getShipVia());
            pstmt.setString(idx++, quote.getFreightTerms());
            pstmt.setString(idx++, quote.getPaymentTerms());
            pstmt.setString(idx++, quote.getFobLocation());

            // Money fields
            pstmt.setBigDecimal(idx++, quote.getSubtotalAmount());
            pstmt.setBigDecimal(idx++, quote.getTotalDiscountAmount());
            pstmt.setBigDecimal(idx++, quote.getFreightAmount());
            pstmt.setBigDecimal(idx++, quote.getExtendedTotalAmount());
            pstmt.setBigDecimal(idx++, quote.getSalesTaxRatePercent());
            pstmt.setBigDecimal(idx++, quote.getSalesTaxAmount());
            pstmt.setBigDecimal(idx++, quote.getQuoteTotalAmount());

            // Notes and terms
            pstmt.setString(idx++, quote.getNotes());
            pstmt.setString(idx++, quote.getTermsAndConditions());
            pstmt.setString(idx++, quote.getExtraInfo());
            pstmt.setBoolean(idx++, quote.getSignatureBoolean() != null ? quote.getSignatureBoolean() : false);

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateQuote(EquipmentQuote quote) throws SQLException {
        String sql = "UPDATE equipmentquotes SET ClientID = ?, BillingLocationID = ?, JobAtLocationID = ?, " +
                    "FromCompanyName = ?, FromStreetAddress = ?, FromCity = ?, FromState = ?, FromZIPCode = ?, FromPhone = ?, FromFax = ?, " +
                    "ClientTypeSnapshot = ?, ClientCompanyNameSnapshot = ?, ClientFirstNameSnapshot = ?, ClientLastNameSnapshot = ?, " +
                    "BillToCompanyName = ?, BillToContactName = ?, BillToStreetAddress = ?, BillToBuildingName = ?, " +
                    "BillToSuite = ?, BillToRoomNumber = ?, BillToDepartment = ?, BillToCounty = ?, BillToCity = ?, " +
                    "BillToState = ?, BillToZIPCode = ?, BillToCountry = ?, BillToPhone = ?, BillToFax = ?, BillToPONumber = ?, " +
                    "JobAtCompanyName = ?, JobAtContactName = ?, JobAtStreetAddress = ?, JobAtBuildingName = ?, " +
                    "JobAtSuite = ?, JobAtRoomNumber = ?, JobAtDepartment = ?, JobAtCounty = ?, JobAtCity = ?, " +
                    "JobAtState = ?, JobAtZIPCode = ?, JobAtCountry = ?, JobAtEmail = ?, JobAtPONumber = ?, " +
                    "QuoteNumber = ?, QuoteDate = ?, Status = ?, ContactName = ?, SalespersonName = ?, ShipVia = ?, FreightTerms = ?, PaymentTerms = ?, FOBLocation = ?, " +
                    "SubtotalAmount = ?, TotalDiscountAmount = ?, FreightAmount = ?, ExtendedTotalAmount = ?, " +
                    "SalesTaxRatePercent = ?, SalesTaxAmount = ?, QuoteTotalAmount = ?, Notes = ?, TermsAndConditions = ?, ExtraInfo = ?, SignatureBoolean = ? " +
                    "WHERE EquipmentQuoteID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int idx = 1;
            pstmt.setInt(idx++, quote.getClientId());
            pstmt.setInt(idx++, quote.getBillingLocationId());
            pstmt.setInt(idx++, quote.getJobAtLocationId());

            // FROM fields
            pstmt.setString(idx++, quote.getFromCompanyName());
            pstmt.setString(idx++, quote.getFromStreetAddress());
            pstmt.setString(idx++, quote.getFromCity());
            pstmt.setString(idx++, quote.getFromState());
            pstmt.setString(idx++, quote.getFromZIPCode());
            pstmt.setString(idx++, quote.getFromPhone());
            pstmt.setString(idx++, quote.getFromFax());

            // Client snapshot fields
            pstmt.setString(idx++, quote.getClientTypeSnapshot());
            pstmt.setString(idx++, quote.getClientCompanyNameSnapshot());
            pstmt.setString(idx++, quote.getClientFirstNameSnapshot());
            pstmt.setString(idx++, quote.getClientLastNameSnapshot());

            // BILL TO fields
            pstmt.setString(idx++, quote.getBillToCompanyName());
            pstmt.setString(idx++, quote.getBillToContactName());
            pstmt.setString(idx++, quote.getBillToStreetAddress());
            pstmt.setString(idx++, quote.getBillToBuildingName());
            pstmt.setString(idx++, quote.getBillToSuite());
            pstmt.setString(idx++, quote.getBillToRoomNumber());
            pstmt.setString(idx++, quote.getBillToDepartment());
            pstmt.setString(idx++, quote.getBillToCounty());
            pstmt.setString(idx++, quote.getBillToCity());
            pstmt.setString(idx++, quote.getBillToState());
            pstmt.setString(idx++, quote.getBillToZIPCode());
            pstmt.setString(idx++, quote.getBillToCountry());
            pstmt.setString(idx++, quote.getBillToPhone());
            pstmt.setString(idx++, quote.getBillToFax());
            pstmt.setString(idx++, quote.getBillToPONumber());

            // JOB AT fields
            pstmt.setString(idx++, quote.getJobAtCompanyName());
            pstmt.setString(idx++, quote.getJobAtContactName());
            pstmt.setString(idx++, quote.getJobAtStreetAddress());
            pstmt.setString(idx++, quote.getJobAtBuildingName());
            pstmt.setString(idx++, quote.getJobAtSuite());
            pstmt.setString(idx++, quote.getJobAtRoomNumber());
            pstmt.setString(idx++, quote.getJobAtDepartment());
            pstmt.setString(idx++, quote.getJobAtCounty());
            pstmt.setString(idx++, quote.getJobAtCity());
            pstmt.setString(idx++, quote.getJobAtState());
            pstmt.setString(idx++, quote.getJobAtZIPCode());
            pstmt.setString(idx++, quote.getJobAtCountry());
            pstmt.setString(idx++, quote.getJobAtEmail());
            pstmt.setString(idx++, quote.getJobAtPONumber());

            // Core quote info
            pstmt.setString(idx++, quote.getQuoteNumber());
            pstmt.setDate(idx++, java.sql.Date.valueOf(quote.getQuoteDate()));
            pstmt.setString(idx++, quote.getStatus());

            // Sales/logistics fields
            pstmt.setString(idx++, quote.getContactName());
            pstmt.setString(idx++, quote.getSalespersonName());
            pstmt.setString(idx++, quote.getShipVia());
            pstmt.setString(idx++, quote.getFreightTerms());
            pstmt.setString(idx++, quote.getPaymentTerms());
            pstmt.setString(idx++, quote.getFobLocation());

            // Money fields
            pstmt.setBigDecimal(idx++, quote.getSubtotalAmount());
            pstmt.setBigDecimal(idx++, quote.getTotalDiscountAmount());
            pstmt.setBigDecimal(idx++, quote.getFreightAmount());
            pstmt.setBigDecimal(idx++, quote.getExtendedTotalAmount());
            pstmt.setBigDecimal(idx++, quote.getSalesTaxRatePercent());
            pstmt.setBigDecimal(idx++, quote.getSalesTaxAmount());
            pstmt.setBigDecimal(idx++, quote.getQuoteTotalAmount());

            // Notes and terms
            pstmt.setString(idx++, quote.getNotes());
            pstmt.setString(idx++, quote.getTermsAndConditions());
            pstmt.setString(idx++, quote.getExtraInfo());
            pstmt.setBoolean(idx++, quote.getSignatureBoolean() != null ? quote.getSignatureBoolean() : false);

            pstmt.setInt(idx++, quote.getEquipmentQuoteId());

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

    private EquipmentQuote extractFromResultSet(ResultSet rs) throws SQLException {
        EquipmentQuote quote = new EquipmentQuote();
        quote.setEquipmentQuoteId(rs.getInt("EquipmentQuoteID"));
        quote.setClientId(rs.getInt("ClientID"));
        quote.setBillingLocationId(rs.getInt("BillingLocationID"));
        quote.setJobAtLocationId(rs.getInt("JobAtLocationID"));

        // FROM fields
        quote.setFromCompanyName(rs.getString("FromCompanyName"));
        quote.setFromStreetAddress(rs.getString("FromStreetAddress"));
        quote.setFromCity(rs.getString("FromCity"));
        quote.setFromState(rs.getString("FromState"));
        quote.setFromZIPCode(rs.getString("FromZIPCode"));
        quote.setFromPhone(rs.getString("FromPhone"));
        quote.setFromFax(rs.getString("FromFax"));

        // Client snapshot fields
        quote.setClientTypeSnapshot(rs.getString("ClientTypeSnapshot"));
        quote.setClientCompanyNameSnapshot(rs.getString("ClientCompanyNameSnapshot"));
        quote.setClientFirstNameSnapshot(rs.getString("ClientFirstNameSnapshot"));
        quote.setClientLastNameSnapshot(rs.getString("ClientLastNameSnapshot"));

        // BILL TO fields
        quote.setBillToCompanyName(rs.getString("BillToCompanyName"));
        quote.setBillToContactName(rs.getString("BillToContactName"));
        quote.setBillToStreetAddress(rs.getString("BillToStreetAddress"));
        quote.setBillToBuildingName(rs.getString("BillToBuildingName"));
        quote.setBillToSuite(rs.getString("BillToSuite"));
        quote.setBillToRoomNumber(rs.getString("BillToRoomNumber"));
        quote.setBillToDepartment(rs.getString("BillToDepartment"));
        quote.setBillToCounty(rs.getString("BillToCounty"));
        quote.setBillToCity(rs.getString("BillToCity"));
        quote.setBillToState(rs.getString("BillToState"));
        quote.setBillToZIPCode(rs.getString("BillToZIPCode"));
        quote.setBillToCountry(rs.getString("BillToCountry"));
        quote.setBillToPhone(rs.getString("BillToPhone"));
        quote.setBillToFax(rs.getString("BillToFax"));
        quote.setBillToPONumber(rs.getString("BillToPONumber"));

        // JOB AT fields
        quote.setJobAtCompanyName(rs.getString("JobAtCompanyName"));
        quote.setJobAtContactName(rs.getString("JobAtContactName"));
        quote.setJobAtStreetAddress(rs.getString("JobAtStreetAddress"));
        quote.setJobAtBuildingName(rs.getString("JobAtBuildingName"));
        quote.setJobAtSuite(rs.getString("JobAtSuite"));
        quote.setJobAtRoomNumber(rs.getString("JobAtRoomNumber"));
        quote.setJobAtDepartment(rs.getString("JobAtDepartment"));
        quote.setJobAtCounty(rs.getString("JobAtCounty"));
        quote.setJobAtCity(rs.getString("JobAtCity"));
        quote.setJobAtState(rs.getString("JobAtState"));
        quote.setJobAtZIPCode(rs.getString("JobAtZIPCode"));
        quote.setJobAtCountry(rs.getString("JobAtCountry"));
        quote.setJobAtEmail(rs.getString("JobAtEmail"));
        quote.setJobAtPONumber(rs.getString("JobAtPONumber"));

        // Core quote info
        quote.setQuoteNumber(rs.getString("QuoteNumber"));
        if (rs.getDate("QuoteDate") != null) {
            quote.setQuoteDate(rs.getDate("QuoteDate").toLocalDate());
        }
        quote.setStatus(rs.getString("Status"));

        // Sales/logistics fields
        quote.setContactName(rs.getString("ContactName"));
        quote.setSalespersonName(rs.getString("SalespersonName"));
        quote.setShipVia(rs.getString("ShipVia"));
        quote.setFreightTerms(rs.getString("FreightTerms"));
        quote.setPaymentTerms(rs.getString("PaymentTerms"));
        quote.setFobLocation(rs.getString("FOBLocation"));

        // Money fields
        quote.setSubtotalAmount(rs.getBigDecimal("SubtotalAmount"));
        quote.setTotalDiscountAmount(rs.getBigDecimal("TotalDiscountAmount"));
        quote.setFreightAmount(rs.getBigDecimal("FreightAmount"));
        quote.setExtendedTotalAmount(rs.getBigDecimal("ExtendedTotalAmount"));
        quote.setSalesTaxRatePercent(rs.getBigDecimal("SalesTaxRatePercent"));
        quote.setSalesTaxAmount(rs.getBigDecimal("SalesTaxAmount"));
        quote.setQuoteTotalAmount(rs.getBigDecimal("QuoteTotalAmount"));

        // Notes and terms
        quote.setNotes(rs.getString("Notes"));
        quote.setTermsAndConditions(rs.getString("TermsAndConditions"));
        quote.setExtraInfo(rs.getString("ExtraInfo"));
        quote.setSignatureBoolean(rs.getBoolean("SignatureBoolean"));

        if (rs.getTimestamp("CreatedAt") != null) {
            quote.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
        }
        if (rs.getTimestamp("UpdatedAt") != null) {
            quote.setUpdatedAt(rs.getTimestamp("UpdatedAt").toLocalDateTime());
        }

        return quote;
    }
}
