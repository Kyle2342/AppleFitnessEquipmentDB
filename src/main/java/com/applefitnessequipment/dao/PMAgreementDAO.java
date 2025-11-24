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
                    "FromCompanyName, FromStreetAddress, FromCity, FromState, FromZIPCode, FromPhone, FromFax, " +
                    "ClientTypeSnapshot, ClientCompanyNameSnapshot, ClientFirstNameSnapshot, ClientLastNameSnapshot, " +
                    "BillToCompanyName, BillToContactName, BillToStreetAddress, BillToBuildingName, " +
                    "BillToSuite, BillToRoomNumber, BillToDepartment, BillToCounty, BillToCity, " +
                    "BillToState, BillToZIPCode, BillToCountry, BillToPhone, BillToFax, BillToPONumber, " +
                    "JobAtCompanyName, JobAtContactName, JobAtStreetAddress, JobAtBuildingName, " +
                    "JobAtSuite, JobAtRoomNumber, JobAtDepartment, JobAtCounty, JobAtCity, " +
                    "JobAtState, JobAtZIPCode, JobAtCountry, JobAtEmail, JobAtPONumber, " +
                    "PropertyName, FacilityName, AddressLine, City, State, ZIPCode, ContactName, ContactEmail, " +
                    "PhoneNumber1, PhoneNumber2, AgreementNumber, AgreementTerms, StartDate, EndDate, " +
                    "VisitFrequency, Status, ChargePerMile, ChargePerHour, VisitPrice, TaxRatePercent, " +
                    "RequiresAdditionalInsurance, CancelationNoticeDays, PaymentDueAfterWorkDays, LateFeePercentage, " +
                    "ClientSignatureBoolean) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int idx = 1;
            pstmt.setInt(idx++, pma.getClientId());
            pstmt.setInt(idx++, pma.getBillingLocationId());
            pstmt.setInt(idx++, pma.getJobLocationId());

            // FROM fields
            pstmt.setString(idx++, pma.getFromCompanyName());
            pstmt.setString(idx++, pma.getFromStreetAddress());
            pstmt.setString(idx++, pma.getFromCity());
            pstmt.setString(idx++, pma.getFromState());
            pstmt.setString(idx++, pma.getFromZIPCode());
            pstmt.setString(idx++, pma.getFromPhone());
            pstmt.setString(idx++, pma.getFromFax());

            // Client snapshot fields
            pstmt.setString(idx++, pma.getClientTypeSnapshot());
            pstmt.setString(idx++, pma.getClientCompanyNameSnapshot());
            pstmt.setString(idx++, pma.getClientFirstNameSnapshot());
            pstmt.setString(idx++, pma.getClientLastNameSnapshot());

            // BILL TO fields
            pstmt.setString(idx++, pma.getBillToCompanyName());
            pstmt.setString(idx++, pma.getBillToContactName());
            pstmt.setString(idx++, pma.getBillToStreetAddress());
            pstmt.setString(idx++, pma.getBillToBuildingName());
            pstmt.setString(idx++, pma.getBillToSuite());
            pstmt.setString(idx++, pma.getBillToRoomNumber());
            pstmt.setString(idx++, pma.getBillToDepartment());
            pstmt.setString(idx++, pma.getBillToCounty());
            pstmt.setString(idx++, pma.getBillToCity());
            pstmt.setString(idx++, pma.getBillToState());
            pstmt.setString(idx++, pma.getBillToZIPCode());
            pstmt.setString(idx++, pma.getBillToCountry());
            pstmt.setString(idx++, pma.getBillToPhone());
            pstmt.setString(idx++, pma.getBillToFax());
            pstmt.setString(idx++, pma.getBillToPONumber());

            // JOB AT fields
            pstmt.setString(idx++, pma.getJobAtCompanyName());
            pstmt.setString(idx++, pma.getJobAtContactName());
            pstmt.setString(idx++, pma.getJobAtStreetAddress());
            pstmt.setString(idx++, pma.getJobAtBuildingName());
            pstmt.setString(idx++, pma.getJobAtSuite());
            pstmt.setString(idx++, pma.getJobAtRoomNumber());
            pstmt.setString(idx++, pma.getJobAtDepartment());
            pstmt.setString(idx++, pma.getJobAtCounty());
            pstmt.setString(idx++, pma.getJobAtCity());
            pstmt.setString(idx++, pma.getJobAtState());
            pstmt.setString(idx++, pma.getJobAtZIPCode());
            pstmt.setString(idx++, pma.getJobAtCountry());
            pstmt.setString(idx++, pma.getJobAtEmail());
            pstmt.setString(idx++, pma.getJobAtPONumber());

            // Header snapshot fields
            pstmt.setString(idx++, pma.getPropertyName());
            pstmt.setString(idx++, pma.getFacilityName());
            pstmt.setString(idx++, pma.getAddressLine());
            pstmt.setString(idx++, pma.getCity());
            pstmt.setString(idx++, pma.getState());
            pstmt.setString(idx++, pma.getZipCode());
            pstmt.setString(idx++, pma.getContactName());
            pstmt.setString(idx++, pma.getContactEmail());
            pstmt.setString(idx++, pma.getPhoneNumber1());
            pstmt.setString(idx++, pma.getPhoneNumber2());

            // Agreement details
            pstmt.setString(idx++, pma.getAgreementNumber());
            pstmt.setString(idx++, pma.getAgreementTerms());
            pstmt.setDate(idx++, java.sql.Date.valueOf(pma.getStartDate()));
            pstmt.setDate(idx++, java.sql.Date.valueOf(pma.getEndDate()));
            pstmt.setString(idx++, pma.getVisitFrequency());
            pstmt.setString(idx++, pma.getStatus());

            // Financial terms
            pstmt.setBigDecimal(idx++, pma.getChargePerMile());
            pstmt.setBigDecimal(idx++, pma.getChargePerHour());
            pstmt.setBigDecimal(idx++, pma.getVisitPrice());
            pstmt.setBigDecimal(idx++, pma.getTaxRatePercent());
            pstmt.setBoolean(idx++, pma.getRequiresAdditionalInsurance() != null ? pma.getRequiresAdditionalInsurance() : false);
            pstmt.setInt(idx++, pma.getCancelationNoticeDays() != null ? pma.getCancelationNoticeDays() : 30);
            pstmt.setInt(idx++, pma.getPaymentDueAfterWorkDays() != null ? pma.getPaymentDueAfterWorkDays() : 30);
            pstmt.setBigDecimal(idx++, pma.getLateFeePercentage());

            // Client Signature
            pstmt.setBoolean(idx++, pma.getClientSignatureBoolean() != null ? pma.getClientSignatureBoolean() : false);

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateAgreement(PreventiveMaintenanceAgreement pma) throws SQLException {
        String sql = "UPDATE preventivemaintenanceagreements SET ClientID = ?, BillingLocationID = ?, JobLocationID = ?, " +
                    "FromCompanyName = ?, FromStreetAddress = ?, FromCity = ?, FromState = ?, FromZIPCode = ?, FromPhone = ?, FromFax = ?, " +
                    "ClientTypeSnapshot = ?, ClientCompanyNameSnapshot = ?, ClientFirstNameSnapshot = ?, ClientLastNameSnapshot = ?, " +
                    "BillToCompanyName = ?, BillToContactName = ?, BillToStreetAddress = ?, BillToBuildingName = ?, " +
                    "BillToSuite = ?, BillToRoomNumber = ?, BillToDepartment = ?, BillToCounty = ?, BillToCity = ?, " +
                    "BillToState = ?, BillToZIPCode = ?, BillToCountry = ?, BillToPhone = ?, BillToFax = ?, BillToPONumber = ?, " +
                    "JobAtCompanyName = ?, JobAtContactName = ?, JobAtStreetAddress = ?, JobAtBuildingName = ?, " +
                    "JobAtSuite = ?, JobAtRoomNumber = ?, JobAtDepartment = ?, JobAtCounty = ?, JobAtCity = ?, " +
                    "JobAtState = ?, JobAtZIPCode = ?, JobAtCountry = ?, JobAtEmail = ?, JobAtPONumber = ?, " +
                    "PropertyName = ?, FacilityName = ?, AddressLine = ?, City = ?, State = ?, ZIPCode = ?, ContactName = ?, ContactEmail = ?, " +
                    "PhoneNumber1 = ?, PhoneNumber2 = ?, AgreementNumber = ?, AgreementTerms = ?, StartDate = ?, EndDate = ?, " +
                    "VisitFrequency = ?, Status = ?, ChargePerMile = ?, ChargePerHour = ?, VisitPrice = ?, TaxRatePercent = ?, " +
                    "RequiresAdditionalInsurance = ?, CancelationNoticeDays = ?, PaymentDueAfterWorkDays = ?, LateFeePercentage = ?, " +
                    "ClientSignatureBoolean = ? " +
                    "WHERE PreventiveMaintenanceAgreementID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int idx = 1;
            pstmt.setInt(idx++, pma.getClientId());
            pstmt.setInt(idx++, pma.getBillingLocationId());
            pstmt.setInt(idx++, pma.getJobLocationId());

            // FROM fields
            pstmt.setString(idx++, pma.getFromCompanyName());
            pstmt.setString(idx++, pma.getFromStreetAddress());
            pstmt.setString(idx++, pma.getFromCity());
            pstmt.setString(idx++, pma.getFromState());
            pstmt.setString(idx++, pma.getFromZIPCode());
            pstmt.setString(idx++, pma.getFromPhone());
            pstmt.setString(idx++, pma.getFromFax());

            // Client snapshot fields
            pstmt.setString(idx++, pma.getClientTypeSnapshot());
            pstmt.setString(idx++, pma.getClientCompanyNameSnapshot());
            pstmt.setString(idx++, pma.getClientFirstNameSnapshot());
            pstmt.setString(idx++, pma.getClientLastNameSnapshot());

            // BILL TO fields
            pstmt.setString(idx++, pma.getBillToCompanyName());
            pstmt.setString(idx++, pma.getBillToContactName());
            pstmt.setString(idx++, pma.getBillToStreetAddress());
            pstmt.setString(idx++, pma.getBillToBuildingName());
            pstmt.setString(idx++, pma.getBillToSuite());
            pstmt.setString(idx++, pma.getBillToRoomNumber());
            pstmt.setString(idx++, pma.getBillToDepartment());
            pstmt.setString(idx++, pma.getBillToCounty());
            pstmt.setString(idx++, pma.getBillToCity());
            pstmt.setString(idx++, pma.getBillToState());
            pstmt.setString(idx++, pma.getBillToZIPCode());
            pstmt.setString(idx++, pma.getBillToCountry());
            pstmt.setString(idx++, pma.getBillToPhone());
            pstmt.setString(idx++, pma.getBillToFax());
            pstmt.setString(idx++, pma.getBillToPONumber());

            // JOB AT fields
            pstmt.setString(idx++, pma.getJobAtCompanyName());
            pstmt.setString(idx++, pma.getJobAtContactName());
            pstmt.setString(idx++, pma.getJobAtStreetAddress());
            pstmt.setString(idx++, pma.getJobAtBuildingName());
            pstmt.setString(idx++, pma.getJobAtSuite());
            pstmt.setString(idx++, pma.getJobAtRoomNumber());
            pstmt.setString(idx++, pma.getJobAtDepartment());
            pstmt.setString(idx++, pma.getJobAtCounty());
            pstmt.setString(idx++, pma.getJobAtCity());
            pstmt.setString(idx++, pma.getJobAtState());
            pstmt.setString(idx++, pma.getJobAtZIPCode());
            pstmt.setString(idx++, pma.getJobAtCountry());
            pstmt.setString(idx++, pma.getJobAtEmail());
            pstmt.setString(idx++, pma.getJobAtPONumber());

            // Header snapshot fields
            pstmt.setString(idx++, pma.getPropertyName());
            pstmt.setString(idx++, pma.getFacilityName());
            pstmt.setString(idx++, pma.getAddressLine());
            pstmt.setString(idx++, pma.getCity());
            pstmt.setString(idx++, pma.getState());
            pstmt.setString(idx++, pma.getZipCode());
            pstmt.setString(idx++, pma.getContactName());
            pstmt.setString(idx++, pma.getContactEmail());
            pstmt.setString(idx++, pma.getPhoneNumber1());
            pstmt.setString(idx++, pma.getPhoneNumber2());

            // Agreement details
            pstmt.setString(idx++, pma.getAgreementNumber());
            pstmt.setString(idx++, pma.getAgreementTerms());
            pstmt.setDate(idx++, java.sql.Date.valueOf(pma.getStartDate()));
            pstmt.setDate(idx++, java.sql.Date.valueOf(pma.getEndDate()));
            pstmt.setString(idx++, pma.getVisitFrequency());
            pstmt.setString(idx++, pma.getStatus());

            // Financial terms
            pstmt.setBigDecimal(idx++, pma.getChargePerMile());
            pstmt.setBigDecimal(idx++, pma.getChargePerHour());
            pstmt.setBigDecimal(idx++, pma.getVisitPrice());
            pstmt.setBigDecimal(idx++, pma.getTaxRatePercent());
            pstmt.setBoolean(idx++, pma.getRequiresAdditionalInsurance() != null ? pma.getRequiresAdditionalInsurance() : false);
            pstmt.setInt(idx++, pma.getCancelationNoticeDays() != null ? pma.getCancelationNoticeDays() : 30);
            pstmt.setInt(idx++, pma.getPaymentDueAfterWorkDays() != null ? pma.getPaymentDueAfterWorkDays() : 30);
            pstmt.setBigDecimal(idx++, pma.getLateFeePercentage());

            // Client Signature
            pstmt.setBoolean(idx++, pma.getClientSignatureBoolean() != null ? pma.getClientSignatureBoolean() : false);

            pstmt.setInt(idx++, pma.getPmaId());

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

        // FROM fields
        pma.setFromCompanyName(rs.getString("FromCompanyName"));
        pma.setFromStreetAddress(rs.getString("FromStreetAddress"));
        pma.setFromCity(rs.getString("FromCity"));
        pma.setFromState(rs.getString("FromState"));
        pma.setFromZIPCode(rs.getString("FromZIPCode"));
        pma.setFromPhone(rs.getString("FromPhone"));
        pma.setFromFax(rs.getString("FromFax"));

        // Client snapshot fields
        pma.setClientTypeSnapshot(rs.getString("ClientTypeSnapshot"));
        pma.setClientCompanyNameSnapshot(rs.getString("ClientCompanyNameSnapshot"));
        pma.setClientFirstNameSnapshot(rs.getString("ClientFirstNameSnapshot"));
        pma.setClientLastNameSnapshot(rs.getString("ClientLastNameSnapshot"));

        // BILL TO fields
        pma.setBillToCompanyName(rs.getString("BillToCompanyName"));
        pma.setBillToContactName(rs.getString("BillToContactName"));
        pma.setBillToStreetAddress(rs.getString("BillToStreetAddress"));
        pma.setBillToBuildingName(rs.getString("BillToBuildingName"));
        pma.setBillToSuite(rs.getString("BillToSuite"));
        pma.setBillToRoomNumber(rs.getString("BillToRoomNumber"));
        pma.setBillToDepartment(rs.getString("BillToDepartment"));
        pma.setBillToCounty(rs.getString("BillToCounty"));
        pma.setBillToCity(rs.getString("BillToCity"));
        pma.setBillToState(rs.getString("BillToState"));
        pma.setBillToZIPCode(rs.getString("BillToZIPCode"));
        pma.setBillToCountry(rs.getString("BillToCountry"));
        pma.setBillToPhone(rs.getString("BillToPhone"));
        pma.setBillToFax(rs.getString("BillToFax"));
        pma.setBillToPONumber(rs.getString("BillToPONumber"));

        // JOB AT fields
        pma.setJobAtCompanyName(rs.getString("JobAtCompanyName"));
        pma.setJobAtContactName(rs.getString("JobAtContactName"));
        pma.setJobAtStreetAddress(rs.getString("JobAtStreetAddress"));
        pma.setJobAtBuildingName(rs.getString("JobAtBuildingName"));
        pma.setJobAtSuite(rs.getString("JobAtSuite"));
        pma.setJobAtRoomNumber(rs.getString("JobAtRoomNumber"));
        pma.setJobAtDepartment(rs.getString("JobAtDepartment"));
        pma.setJobAtCounty(rs.getString("JobAtCounty"));
        pma.setJobAtCity(rs.getString("JobAtCity"));
        pma.setJobAtState(rs.getString("JobAtState"));
        pma.setJobAtZIPCode(rs.getString("JobAtZIPCode"));
        pma.setJobAtCountry(rs.getString("JobAtCountry"));
        pma.setJobAtEmail(rs.getString("JobAtEmail"));
        pma.setJobAtPONumber(rs.getString("JobAtPONumber"));

        // Header snapshot fields
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

        // Agreement details
        pma.setAgreementNumber(rs.getString("AgreementNumber"));
        pma.setAgreementTerms(rs.getString("AgreementTerms"));

        if (rs.getDate("StartDate") != null) {
            pma.setStartDate(rs.getDate("StartDate").toLocalDate());
        }
        if (rs.getDate("EndDate") != null) {
            pma.setEndDate(rs.getDate("EndDate").toLocalDate());
        }

        pma.setVisitFrequency(rs.getString("VisitFrequency"));
        pma.setStatus(rs.getString("Status"));

        // Financial terms
        pma.setChargePerMile(rs.getBigDecimal("ChargePerMile"));
        pma.setChargePerHour(rs.getBigDecimal("ChargePerHour"));
        pma.setVisitPrice(rs.getBigDecimal("VisitPrice"));
        pma.setTaxRatePercent(rs.getBigDecimal("TaxRatePercent"));
        pma.setRequiresAdditionalInsurance(rs.getBoolean("RequiresAdditionalInsurance"));
        pma.setCancelationNoticeDays(rs.getInt("CancelationNoticeDays"));
        pma.setPaymentDueAfterWorkDays(rs.getInt("PaymentDueAfterWorkDays"));
        pma.setLateFeePercentage(rs.getBigDecimal("LateFeePercentage"));

        // Client Signature
        pma.setClientSignatureBoolean(rs.getBoolean("ClientSignatureBoolean"));

        if (rs.getTimestamp("CreatedAt") != null) {
            pma.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
        }
        if (rs.getTimestamp("UpdatedAt") != null) {
            pma.setUpdatedAt(rs.getTimestamp("UpdatedAt").toLocalDateTime());
        }

        return pma;
    }
}
