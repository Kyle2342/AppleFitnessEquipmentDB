package com.applefitnessequipment.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.applefitnessequipment.db.DBConnection;
import com.applefitnessequipment.model.PreventiveMaintenance;

public class PreventiveMaintenanceDAO {
    
    public List<PreventiveMaintenance> getAllAgreements() throws SQLException {
        List<PreventiveMaintenance> agreements = new ArrayList<>();
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
    
    public PreventiveMaintenance getAgreementById(int pmaId) throws SQLException {
        String sql = "SELECT * FROM preventivemaintenanceagreements WHERE PMAID = ?";
        
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
    
    public boolean addAgreement(PreventiveMaintenance pma) throws SQLException {
        String sql = "INSERT INTO preventivemaintenanceagreements (ClientID, PropertyLocationID, " +
                    "AgreementNumber, StartDate, EndDate, BillingFrequency, Status, MonthlyRate, AnnualRate) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, pma.getClientId());
            pstmt.setInt(2, pma.getPropertyLocationId());
            pstmt.setString(3, pma.getAgreementNumber());
            pstmt.setDate(4, java.sql.Date.valueOf(pma.getStartDate()));
            pstmt.setDate(5, java.sql.Date.valueOf(pma.getEndDate()));
            pstmt.setString(6, pma.getBillingFrequency());
            pstmt.setString(7, pma.getStatus());
            pstmt.setBigDecimal(8, pma.getMonthlyRate());
            pstmt.setBigDecimal(9, pma.getAnnualRate());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateAgreement(PreventiveMaintenance pma) throws SQLException {
        String sql = "UPDATE preventivemaintenanceagreements SET ClientID = ?, PropertyLocationID = ?, " +
                    "AgreementNumber = ?, StartDate = ?, EndDate = ?, BillingFrequency = ?, " +
                    "Status = ?, MonthlyRate = ?, AnnualRate = ? WHERE PMAID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, pma.getClientId());
            pstmt.setInt(2, pma.getPropertyLocationId());
            pstmt.setString(3, pma.getAgreementNumber());
            pstmt.setDate(4, java.sql.Date.valueOf(pma.getStartDate()));
            pstmt.setDate(5, java.sql.Date.valueOf(pma.getEndDate()));
            pstmt.setString(6, pma.getBillingFrequency());
            pstmt.setString(7, pma.getStatus());
            pstmt.setBigDecimal(8, pma.getMonthlyRate());
            pstmt.setBigDecimal(9, pma.getAnnualRate());
            pstmt.setInt(10, pma.getPmaId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteAgreement(int pmaId) throws SQLException {
        String sql = "DELETE FROM preventivemaintenanceagreements WHERE PMAID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, pmaId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    private PreventiveMaintenance extractFromResultSet(ResultSet rs) throws SQLException {
        PreventiveMaintenance pma = new PreventiveMaintenance();
        pma.setPmaId(rs.getInt("PMAID"));
        pma.setClientId(rs.getInt("ClientID"));
        pma.setPropertyLocationId(rs.getInt("PropertyLocationID"));
        pma.setAgreementNumber(rs.getString("AgreementNumber"));
        
        if (rs.getDate("StartDate") != null) {
            pma.setStartDate(rs.getDate("StartDate").toLocalDate());
        }
        if (rs.getDate("EndDate") != null) {
            pma.setEndDate(rs.getDate("EndDate").toLocalDate());
        }
        
        pma.setBillingFrequency(rs.getString("BillingFrequency"));
        pma.setStatus(rs.getString("Status"));
        pma.setMonthlyRate(rs.getBigDecimal("MonthlyRate"));
        pma.setAnnualRate(rs.getBigDecimal("AnnualRate"));
        
        return pma;
    }
}
