package com.applefitnessequipment.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.applefitnessequipment.db.DBConnection;
import com.applefitnessequipment.model.PreventiveMaintenanceAgreement;

public class PMAgreementDAO {
    
    public List<PreventiveMaintenanceAgreement> getAllAgreements() throws SQLException {
        List<PreventiveMaintenanceAgreement> agreements = new ArrayList<>();
        String sql = "SELECT * FROM preventivemaintenanceagreements ORDER BY StartDate DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                agreements.add(extractFromResultSet(rs));
            }
        }
        return agreements;
    }
    
    public PreventiveMaintenanceAgreement getAgreementById(int pmaId) throws SQLException {
        String sql = "SELECT * FROM preventivemaintenanceagreements WHERE PreventiveMaintenanceAgreementID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, pmaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }
        }
        return null;
    }
    
    public boolean addAgreement(PreventiveMaintenanceAgreement pma) throws SQLException {
        String sql = "INSERT INTO preventivemaintenanceagreements (ClientID, BillingLocationID, JobLocationID, " +
                    "PropertyName, FacilityName, AddressLine, City, State, ZIPCode, ContactName, ContactEmail, " +
                    "PhoneNumber1, PhoneNumber2, AgreementNumber, StartDate, EndDate, " +
                    "VisitFrequency, Status, ChargePerMile, ChargePerHour, VisitPrice, TaxRatePercent, " +
                    "RequiresAdditionalInsurance, CancelationNoticeDays, PaymentDueAfterWorkDays, LateFeePercentage) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pma.getClientId());
            pstmt.setInt(2, pma.getBillingLocationId());
            pstmt.setInt(3, pma.getJobLocationId());
            pstmt.setString(4, pma.getPropertyName());
            pstmt.setString(5, pma.getFacilityName());
            pstmt.setString(6, pma.getAddressLine());
            pstmt.setString(7, pma.getCity());
            pstmt.setString(8, pma.getState());
            pstmt.setString(9, pma.getZipCode());
            pstmt.setString(10, pma.getContactName());
            pstmt.setString(11, pma.getContactEmail());
            pstmt.setString(12, pma.getPhoneNumber1());
            pstmt.setString(13, pma.getPhoneNumber2());
            pstmt.setString(14, pma.getAgreementNumber());
            // pstmt.setString(15, pma.getCoverageText());
            pstmt.setDate(15, java.sql.Date.valueOf(pma.getStartDate()));
            pstmt.setDate(16, java.sql.Date.valueOf(pma.getEndDate()));
            pstmt.setString(17, pma.getVisitFrequency());
            pstmt.setString(18, pma.getStatus());
            pstmt.setBigDecimal(19, pma.getChargePerMile());
            pstmt.setBigDecimal(20, pma.getChargePerHour());
            pstmt.setBigDecimal(21, pma.getVisitPrice());
            pstmt.setBigDecimal(22, pma.getTaxRatePercent());
            pstmt.setBoolean(23, pma.getRequiresAdditionalInsurance() != null ? pma.getRequiresAdditionalInsurance() : false);
            pstmt.setInt(24, pma.getCancelationNoticeDays() != null ? pma.getCancelationNoticeDays() : 30);
            pstmt.setInt(25, pma.getPaymentDueAfterWorkDays() != null ? pma.getPaymentDueAfterWorkDays() : 30);
            pstmt.setBigDecimal(26, pma.getLateFeePercentage());

            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateAgreement(PreventiveMaintenanceAgreement pma) throws SQLException {
        String sql = "UPDATE preventivemaintenanceagreements SET ClientID = ?, BillingLocationID = ?, JobLocationID = ?, " +
                    "PropertyName = ?, FacilityName = ?, AddressLine = ?, City = ?, State = ?, ZIPCode = ?, " +
                    "ContactName = ?, ContactEmail = ?, PhoneNumber1 = ?, PhoneNumber2 = ?, AgreementNumber = ?, " +
                    "StartDate = ?, EndDate = ?, VisitFrequency = ?, Status = ?, " +
                    "ChargePerMile = ?, ChargePerHour = ?, VisitPrice = ?, TaxRatePercent = ?, " +
                    "RequiresAdditionalInsurance = ?, CancelationNoticeDays = ?, PaymentDueAfterWorkDays = ?, " +
                    "LateFeePercentage = ? WHERE PreventiveMaintenanceAgreementID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pma.getClientId());
            pstmt.setInt(2, pma.getBillingLocationId());
            pstmt.setInt(3, pma.getJobLocationId());
            pstmt.setString(4, pma.getPropertyName());
            pstmt.setString(5, pma.getFacilityName());
            pstmt.setString(6, pma.getAddressLine());
            pstmt.setString(7, pma.getCity());
            pstmt.setString(8, pma.getState());
            pstmt.setString(9, pma.getZipCode());
            pstmt.setString(10, pma.getContactName());
            pstmt.setString(11, pma.getContactEmail());
            pstmt.setString(12, pma.getPhoneNumber1());
            pstmt.setString(13, pma.getPhoneNumber2());
            pstmt.setString(14, pma.getAgreementNumber());
            // pstmt.setString(15, pma.getCoverageText());
            pstmt.setDate(15, java.sql.Date.valueOf(pma.getStartDate()));
            pstmt.setDate(16, java.sql.Date.valueOf(pma.getEndDate()));
            pstmt.setString(17, pma.getVisitFrequency());
            pstmt.setString(18, pma.getStatus());
            pstmt.setBigDecimal(19, pma.getChargePerMile());
            pstmt.setBigDecimal(20, pma.getChargePerHour());
            pstmt.setBigDecimal(21, pma.getVisitPrice());
            pstmt.setBigDecimal(22, pma.getTaxRatePercent());
            pstmt.setBoolean(23, pma.getRequiresAdditionalInsurance() != null ? pma.getRequiresAdditionalInsurance() : false);
            pstmt.setInt(24, pma.getCancelationNoticeDays() != null ? pma.getCancelationNoticeDays() : 30);
            pstmt.setInt(25, pma.getPaymentDueAfterWorkDays() != null ? pma.getPaymentDueAfterWorkDays() : 30);
            pstmt.setBigDecimal(26, pma.getLateFeePercentage());
            pstmt.setInt(27, pma.getPmaId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteAgreement(int pmaId) throws SQLException {
        String sql = "DELETE FROM preventivemaintenanceagreements WHERE PreventiveMaintenanceAgreementID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, pmaId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    private PreventiveMaintenanceAgreement extractFromResultSet(ResultSet rs) throws SQLException {
        PreventiveMaintenanceAgreement pma = new PreventiveMaintenanceAgreement();
        pma.setPmaId(rs.getInt("PreventiveMaintenanceAgreementID"));
        pma.setClientId(rs.getInt("ClientID"));
        pma.setBillingLocationId(rs.getInt("BillingLocationID"));
        pma.setJobLocationId(rs.getInt("JobLocationID"));
        pma.setPropertyName(rs.getString("PropertyName"));
        pma.setFacilityName(rs.getString("FacilityName"));
        pma.setAddressLine(rs.getString("AddressLine"));
        pma.setCity(rs.getString("City"));
        pma.setState(rs.getString("State"));
        pma.setZipCode(rs.getString("ZIPCode"));
        pma.setContactName(rs.getString("ContactName"));
        pma.setContactEmail(rs.getString("ContactEmail"));
        pma.setPhoneNumber1(rs.getString("PhoneNumber1"));
        pma.setPhoneNumber2(rs.getString("PhoneNumber2"));
        pma.setAgreementNumber(rs.getString("AgreementNumber"));
        // pma.setCoverageText(rs.getString("CoverageText"));
        
        if (rs.getDate("StartDate") != null) {
            pma.setStartDate(rs.getDate("StartDate").toLocalDate());
        }
        if (rs.getDate("EndDate") != null) {
            pma.setEndDate(rs.getDate("EndDate").toLocalDate());
        }
        
        pma.setVisitFrequency(rs.getString("VisitFrequency"));
        pma.setStatus(rs.getString("Status"));
        pma.setChargePerMile(rs.getBigDecimal("ChargePerMile"));
        pma.setChargePerHour(rs.getBigDecimal("ChargePerHour"));
        pma.setVisitPrice(rs.getBigDecimal("VisitPrice"));
        pma.setTaxRatePercent(rs.getBigDecimal("TaxRatePercent"));
        pma.setRequiresAdditionalInsurance(rs.getBoolean("RequiresAdditionalInsurance"));
        pma.setCancelationNoticeDays(rs.getInt("CancelationNoticeDays"));
        pma.setPaymentDueAfterWorkDays(rs.getInt("PaymentDueAfterWorkDays"));
        pma.setLateFeePercentage(rs.getBigDecimal("LateFeePercentage"));
        pma.setFacilityAgentSignature(rs.getString("FacilityAgentSignature"));
        
        if (rs.getDate("SignatureDate") != null) {
            pma.setSignatureDate(rs.getDate("SignatureDate").toLocalDate());
        }
        
        return pma;
    }
}
