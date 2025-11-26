package com.applefitnessequipment.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.applefitnessequipment.db.DBConnection;
import com.applefitnessequipment.model.PMAgreementEquipment;

/**
 * DAO for PreventiveMaintenanceAgreementsEquipments table.
 * @SCHEMA_SINGLE_SOURCE_OF_TRUTH: applefitnessequipmentdb_schema.sql
 */
public class PMAgreementEquipmentDAO {

    public List<PMAgreementEquipment> getEquipmentByAgreementId(int agreementId) throws SQLException {
        List<PMAgreementEquipment> equipment = new ArrayList<>();
        String sql = "SELECT * FROM PreventiveMaintenanceAgreementsEquipments " +
                    "WHERE PreventiveMaintenanceAgreementID = ? ORDER BY RowNumber";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, agreementId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    equipment.add(extractFromResultSet(rs));
                }
            }
        }
        return equipment;
    }

    public PMAgreementEquipment getEquipmentById(int equipmentId) throws SQLException {
        String sql = "SELECT * FROM PreventiveMaintenanceAgreementsEquipments " +
                    "WHERE PreventiveMaintenanceAgreementEquipmentID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, equipmentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public boolean addEquipment(PMAgreementEquipment equipment) throws SQLException {
        String sql = "INSERT INTO PreventiveMaintenanceAgreementsEquipments " +
                    "(PreventiveMaintenanceAgreementID, RowNumber, EquipmentType, Make, Model, SerialNumber) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, equipment.getPreventiveMaintenanceAgreementId());
            pstmt.setInt(2, equipment.getRowNumber());
            pstmt.setString(3, equipment.getEquipmentType());
            pstmt.setString(4, equipment.getMake());
            pstmt.setString(5, equipment.getModel());
            pstmt.setString(6, equipment.getSerialNumber());

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        equipment.setPreventiveMaintenanceAgreementEquipmentId(keys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public boolean updateEquipment(PMAgreementEquipment equipment) throws SQLException {
        String sql = "UPDATE PreventiveMaintenanceAgreementsEquipments SET " +
                    "PreventiveMaintenanceAgreementID = ?, RowNumber = ?, EquipmentType = ?, " +
                    "Make = ?, Model = ?, SerialNumber = ? " +
                    "WHERE PreventiveMaintenanceAgreementEquipmentID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, equipment.getPreventiveMaintenanceAgreementId());
            pstmt.setInt(2, equipment.getRowNumber());
            pstmt.setString(3, equipment.getEquipmentType());
            pstmt.setString(4, equipment.getMake());
            pstmt.setString(5, equipment.getModel());
            pstmt.setString(6, equipment.getSerialNumber());
            pstmt.setInt(7, equipment.getPreventiveMaintenanceAgreementEquipmentId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteEquipment(int equipmentId) throws SQLException {
        String sql = "DELETE FROM PreventiveMaintenanceAgreementsEquipments " +
                    "WHERE PreventiveMaintenanceAgreementEquipmentID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, equipmentId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteEquipmentByAgreementId(int agreementId) throws SQLException {
        String sql = "DELETE FROM PreventiveMaintenanceAgreementsEquipments " +
                    "WHERE PreventiveMaintenanceAgreementID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, agreementId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private PMAgreementEquipment extractFromResultSet(ResultSet rs) throws SQLException {
        PMAgreementEquipment equipment = new PMAgreementEquipment();
        equipment.setPreventiveMaintenanceAgreementEquipmentId(
            rs.getInt("PreventiveMaintenanceAgreementEquipmentID"));
        equipment.setPreventiveMaintenanceAgreementId(
            rs.getInt("PreventiveMaintenanceAgreementID"));
        equipment.setRowNumber(rs.getInt("RowNumber"));
        equipment.setEquipmentType(rs.getString("EquipmentType"));
        equipment.setMake(rs.getString("Make"));
        equipment.setModel(rs.getString("Model"));
        equipment.setSerialNumber(rs.getString("SerialNumber"));

        return equipment;
    }
}
