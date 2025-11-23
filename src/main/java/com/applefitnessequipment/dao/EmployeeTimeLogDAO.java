package com.applefitnessequipment.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.applefitnessequipment.db.DBConnection;
import com.applefitnessequipment.model.EmployeeTimeLog;

public class EmployeeTimeLogDAO {
    
    public List<EmployeeTimeLog> getAllTimeLogs() throws SQLException {
        List<EmployeeTimeLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM employeestimelogs ORDER BY LogDate DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                logs.add(extractTimeLogFromResultSet(rs));
            }
        }
        return logs;
    }
    
    public List<EmployeeTimeLog> getTimeLogsByEmployeeId(int employeeId) throws SQLException {
        List<EmployeeTimeLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM employeestimelogs WHERE EmployeeID = ? ORDER BY LogDate DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(extractTimeLogFromResultSet(rs));
                }
            }
        }
        return logs;
    }
    
    public EmployeeTimeLog getTimeLogById(int timeLogId) throws SQLException {
        String sql = "SELECT * FROM employeestimelogs WHERE TimeLogID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, timeLogId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractTimeLogFromResultSet(rs);
                }
            }
        }
        return null;
    }
    
    public boolean addTimeLog(EmployeeTimeLog log) throws SQLException {
        String sql = "INSERT INTO employeestimelogs (EmployeeID, DayOfWeek, LogDate, TimeInFirst, " +
                    "TimeOutFirst, TimeInSecond, TimeOutSecond, Miles) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, log.getEmployeeId());
            pstmt.setString(2, log.getDayOfWeek());
            pstmt.setDate(3, java.sql.Date.valueOf(log.getLogDate()));
            pstmt.setTime(4, java.sql.Time.valueOf(log.getTimeInFirst()));
            pstmt.setTime(5, java.sql.Time.valueOf(log.getTimeOutFirst()));

            if (log.getTimeInSecond() != null) {
                pstmt.setTime(6, java.sql.Time.valueOf(log.getTimeInSecond()));
            } else {
                pstmt.setNull(6, java.sql.Types.TIME);
            }

            if (log.getTimeOutSecond() != null) {
                pstmt.setTime(7, java.sql.Time.valueOf(log.getTimeOutSecond()));
            } else {
                pstmt.setNull(7, java.sql.Types.TIME);
            }

            // TotalHours is auto-calculated by database

            if (log.getMiles() != null) {
                pstmt.setBigDecimal(8, log.getMiles());
            } else {
                pstmt.setNull(8, java.sql.Types.DECIMAL);
            }

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateTimeLog(EmployeeTimeLog log) throws SQLException {
        String sql = "UPDATE employeestimelogs SET EmployeeID = ?, DayOfWeek = ?, LogDate = ?, " +
                    "TimeInFirst = ?, TimeOutFirst = ?, TimeInSecond = ?, TimeOutSecond = ?, " +
                    "Miles = ? WHERE TimeLogID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, log.getEmployeeId());
            pstmt.setString(2, log.getDayOfWeek());
            pstmt.setDate(3, java.sql.Date.valueOf(log.getLogDate()));
            pstmt.setTime(4, java.sql.Time.valueOf(log.getTimeInFirst()));
            pstmt.setTime(5, java.sql.Time.valueOf(log.getTimeOutFirst()));

            if (log.getTimeInSecond() != null) {
                pstmt.setTime(6, java.sql.Time.valueOf(log.getTimeInSecond()));
            } else {
                pstmt.setNull(6, java.sql.Types.TIME);
            }

            if (log.getTimeOutSecond() != null) {
                pstmt.setTime(7, java.sql.Time.valueOf(log.getTimeOutSecond()));
            } else {
                pstmt.setNull(7, java.sql.Types.TIME);
            }

            // TotalHours is auto-calculated by database

            if (log.getMiles() != null) {
                pstmt.setBigDecimal(8, log.getMiles());
            } else {
                pstmt.setNull(8, java.sql.Types.DECIMAL);
            }

            pstmt.setInt(9, log.getTimeLogId());

            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteTimeLog(int timeLogId) throws SQLException {
        String sql = "DELETE FROM employeestimelogs WHERE TimeLogID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, timeLogId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    private EmployeeTimeLog extractTimeLogFromResultSet(ResultSet rs) throws SQLException {
        EmployeeTimeLog log = new EmployeeTimeLog();
        log.setTimeLogId(rs.getInt("TimeLogID"));
        log.setEmployeeId(rs.getInt("EmployeeID"));
        log.setDayOfWeek(rs.getString("DayOfWeek"));
        log.setLogDate(rs.getDate("LogDate").toLocalDate());
        log.setTimeInFirst(rs.getTime("TimeInFirst").toLocalTime());
        log.setTimeOutFirst(rs.getTime("TimeOutFirst").toLocalTime());
        
        if (rs.getTime("TimeInSecond") != null) {
            log.setTimeInSecond(rs.getTime("TimeInSecond").toLocalTime());
        }
        if (rs.getTime("TimeOutSecond") != null) {
            log.setTimeOutSecond(rs.getTime("TimeOutSecond").toLocalTime());
        }
        
        BigDecimal totalHours = rs.getBigDecimal("TotalHours");
        if (totalHours != null) {
            log.setTotalHours(totalHours);
        }
        
        BigDecimal miles = rs.getBigDecimal("Miles");
        if (miles != null) {
            log.setMiles(miles);
        }
        
        // BigDecimal ptoHours = rs.getBigDecimal("PTOHours");
        // if (ptoHours != null) {
        //     log.setPtoHours(ptoHours);
        // }
        
        log.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
        log.setUpdatedAt(rs.getTimestamp("UpdatedAt").toLocalDateTime());
        
        return log;
    }
}
