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

/**
 * DAO for PreventiveMaintenanceAgreements table.
 * @SCHEMA_SINGLE_SOURCE_OF_TRUTH: applefitnessequipmentdb_schema.sql
 */
public class PMAgreementDAO {

    public List<PreventiveMaintenanceAgreement> getAllAgreements() throws SQLException {
        List<PreventiveMaintenanceAgreement> agreements = new ArrayList<>();
        String sql = "SELECT * FROM PreventiveMaintenanceAgreements ORDER BY StartDate DESC";

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
        String sql = "SELECT * FROM PreventiveMaintenanceAgreements WHERE PreventiveMaintenanceAgreementID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
        String sql = "INSERT INTO PreventiveMaintenanceAgreements " +
                    "(ClientID, ClientBillingLocationID, ClientJobLocationID, " +
                    "AgreementNumber, StartDate, EndDate, VisitFrequency, Status, " +
                    "VisitPrice, TaxRatePercent, ClientSignatureBoolean) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, pma.getClientId());
            pstmt.setInt(2, pma.getClientBillingLocationId());
            pstmt.setInt(3, pma.getClientJobLocationId());
            pstmt.setString(4, pma.getAgreementNumber());
            pstmt.setDate(5, java.sql.Date.valueOf(pma.getStartDate()));
            pstmt.setDate(6, java.sql.Date.valueOf(pma.getEndDate()));
            pstmt.setString(7, pma.getVisitFrequency());
            pstmt.setString(8, pma.getStatus());
            pstmt.setBigDecimal(9, pma.getVisitPrice());
            pstmt.setBigDecimal(10, pma.getTaxRatePercent());
            pstmt.setBoolean(11, pma.getClientSignatureBoolean());

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        pma.setPreventiveMaintenanceAgreementId(keys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public boolean updateAgreement(PreventiveMaintenanceAgreement pma) throws SQLException {
        String sql = "UPDATE PreventiveMaintenanceAgreements SET " +
                    "ClientID = ?, ClientBillingLocationID = ?, ClientJobLocationID = ?, " +
                    "AgreementNumber = ?, StartDate = ?, EndDate = ?, VisitFrequency = ?, " +
                    "Status = ?, VisitPrice = ?, TaxRatePercent = ?, ClientSignatureBoolean = ? " +
                    "WHERE PreventiveMaintenanceAgreementID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pma.getClientId());
            pstmt.setInt(2, pma.getClientBillingLocationId());
            pstmt.setInt(3, pma.getClientJobLocationId());
            pstmt.setString(4, pma.getAgreementNumber());
            pstmt.setDate(5, java.sql.Date.valueOf(pma.getStartDate()));
            pstmt.setDate(6, java.sql.Date.valueOf(pma.getEndDate()));
            pstmt.setString(7, pma.getVisitFrequency());
            pstmt.setString(8, pma.getStatus());
            pstmt.setBigDecimal(9, pma.getVisitPrice());
            pstmt.setBigDecimal(10, pma.getTaxRatePercent());
            pstmt.setBoolean(11, pma.getClientSignatureBoolean());
            pstmt.setInt(12, pma.getPreventiveMaintenanceAgreementId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteAgreement(int pmaId) throws SQLException {
        String sql = "DELETE FROM PreventiveMaintenanceAgreements WHERE PreventiveMaintenanceAgreementID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pmaId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private PreventiveMaintenanceAgreement extractFromResultSet(ResultSet rs) throws SQLException {
        PreventiveMaintenanceAgreement pma = new PreventiveMaintenanceAgreement();
        pma.setPreventiveMaintenanceAgreementId(rs.getInt("PreventiveMaintenanceAgreementID"));
        pma.setClientId(rs.getInt("ClientID"));
        pma.setClientBillingLocationId(rs.getInt("ClientBillingLocationID"));
        pma.setClientJobLocationId(rs.getInt("ClientJobLocationID"));
        pma.setAgreementNumber(rs.getString("AgreementNumber"));

        if (rs.getDate("StartDate") != null) {
            pma.setStartDate(rs.getDate("StartDate").toLocalDate());
        }
        if (rs.getDate("EndDate") != null) {
            pma.setEndDate(rs.getDate("EndDate").toLocalDate());
        }

        pma.setVisitFrequency(rs.getString("VisitFrequency"));
        pma.setStatus(rs.getString("Status"));
        pma.setVisitPrice(rs.getBigDecimal("VisitPrice"));
        pma.setTaxRatePercent(rs.getBigDecimal("TaxRatePercent"));

        // Read GENERATED columns (read-only)
        pma.setTaxAmount(rs.getBigDecimal("TaxAmount"));
        pma.setPricePerYear(rs.getBigDecimal("PricePerYear"));

        pma.setClientSignatureBoolean(rs.getBoolean("ClientSignatureBoolean"));

        return pma;
    }
}
