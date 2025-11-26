package com.applefitnessequipment.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import com.applefitnessequipment.db.DBConnection;
import com.applefitnessequipment.model.EmployeeTimeLog;

public class EmployeeTimeLogDAO {

    // =========================================================
    // Public API
    // =========================================================

    public List<EmployeeTimeLog> getAllTimeLogs() throws SQLException {
        List<EmployeeTimeLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM EmployeesTimeLogs ORDER BY LogDate DESC";

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
        String sql = "SELECT * FROM EmployeesTimeLogs " +
                     "WHERE EmployeeID = ? ORDER BY LogDate DESC";

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

    public EmployeeTimeLog getTimeLogById(int employeeTimeLogId) throws SQLException {
        String sql = "SELECT * FROM EmployeesTimeLogs WHERE EmployeeTimeLogID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, employeeTimeLogId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractTimeLogFromResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Adds a new time log.
     * Throws IllegalArgumentException for validation issues (invalid times,
     * weekday mismatch, duplicate (EmployeeID, LogDate), etc.).
     */
    public boolean addTimeLog(EmployeeTimeLog log) throws SQLException {
        validateTimeLogForInsertOrUpdate(log, false);

        String sql = "INSERT INTO EmployeesTimeLogs " +
                     "(EmployeeID, DayOfWeek, LogDate, TimeIn, TimeOut, Miles) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, log.getEmployeeId());
            pstmt.setString(2, log.getDayOfWeek());
            pstmt.setDate(3, Date.valueOf(log.getLogDate()));
            pstmt.setTime(4, Time.valueOf(log.getTimeIn()));
            pstmt.setTime(5, Time.valueOf(log.getTimeOut()));

            if (log.getMiles() != null) {
                pstmt.setBigDecimal(6, log.getMiles());
            } else {
                pstmt.setNull(6, java.sql.Types.DECIMAL);
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        log.setEmployeeTimeLogId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Updates an existing time log.
     * Throws IllegalArgumentException for validation issues (invalid times,
     * weekday mismatch, duplicate (EmployeeID, LogDate), etc.).
     */
    public boolean updateTimeLog(EmployeeTimeLog log) throws SQLException {
        if (log.getEmployeeTimeLogId() == null) {
            throw new IllegalArgumentException("Time log ID must not be null for update.");
        }

        validateTimeLogForInsertOrUpdate(log, true);

        String sql = "UPDATE EmployeesTimeLogs SET " +
                     "EmployeeID = ?, DayOfWeek = ?, LogDate = ?, " +
                     "TimeIn = ?, TimeOut = ?, Miles = ? " +
                     "WHERE EmployeeTimeLogID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, log.getEmployeeId());
            pstmt.setString(2, log.getDayOfWeek());
            pstmt.setDate(3, Date.valueOf(log.getLogDate()));
            pstmt.setTime(4, Time.valueOf(log.getTimeIn()));
            pstmt.setTime(5, Time.valueOf(log.getTimeOut()));

            if (log.getMiles() != null) {
                pstmt.setBigDecimal(6, log.getMiles());
            } else {
                pstmt.setNull(6, java.sql.Types.DECIMAL);
            }

            pstmt.setInt(7, log.getEmployeeTimeLogId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteTimeLog(int employeeTimeLogId) throws SQLException {
        String sql = "DELETE FROM EmployeesTimeLogs WHERE EmployeeTimeLogID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, employeeTimeLogId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // =========================================================
    // Internal helpers
    // =========================================================

    private EmployeeTimeLog extractTimeLogFromResultSet(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("EmployeeTimeLogID");
        Integer employeeId = rs.getInt("EmployeeID");
        String dayOfWeek = rs.getString("DayOfWeek");
        java.time.LocalDate logDate = rs.getDate("LogDate").toLocalDate();
        java.time.LocalTime timeIn = rs.getTime("TimeIn").toLocalTime();
        java.time.LocalTime timeOut = rs.getTime("TimeOut").toLocalTime();
        BigDecimal totalHours = rs.getBigDecimal("TotalHours");
        BigDecimal miles = rs.getBigDecimal("Miles");

        return new EmployeeTimeLog(
                id,
                employeeId,
                dayOfWeek,
                logDate,
                timeIn,
                timeOut,
                totalHours,
                miles
        );
    }

    /**
     * Validate business rules that mirror your DB constraints and UI expectations:
     * - Required fields not null
     * - TimeOut > TimeIn
     * - DayOfWeek matches LogDate (chk_weekday_matches_logdate)
     * - No duplicate (EmployeeID, LogDate) (uq_employee_logdate)
     * - Recalculate totalHours for UI consistency
     */
    private void validateTimeLogForInsertOrUpdate(EmployeeTimeLog log, boolean isUpdate) throws SQLException {
        if (log.getEmployeeId() == null) {
            throw new IllegalArgumentException("Employee is required.");
        }
        if (log.getLogDate() == null) {
            throw new IllegalArgumentException("Log date is required.");
        }
        if (log.getTimeIn() == null || log.getTimeOut() == null) {
            throw new IllegalArgumentException("Both Time In and Time Out are required.");
        }

        // Recalculate hours in Java so UI always matches DB logic
        log.recalculateTotalHours();

        if (!log.hasValidTimeRange()) {
            throw new IllegalArgumentException("Time out must be after time in.");
        }

        // Validate weekday vs. log date (DAYNAME(LogDate) = DayOfWeek)
        String expectedDayName = toMySqlDayName(log.getLogDate().getDayOfWeek());
        if (log.getDayOfWeek() == null || !expectedDayName.equals(log.getDayOfWeek())) {
            throw new IllegalArgumentException(
                    "Day of week (" + log.getDayOfWeek() + ") does not match the log date (" +
                            log.getLogDate() + "). Expected: " + expectedDayName);
        }

        // Enforce unique (EmployeeID, LogDate) rule at the DAO level
        if (existsTimeLogForEmployeeOnDate(log.getEmployeeId(), log.getLogDate(),
                isUpdate ? log.getEmployeeTimeLogId() : null)) {
            throw new IllegalArgumentException(
                    "A time log for this employee on " + log.getLogDate() + " already exists.");
        }

        // Miles >= 0 (mirrors chk_miles_nonnegative)
        if (log.getMiles() != null && log.getMiles().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Miles cannot be negative.");
        }
    }

    /**
     * Check if a time log already exists for this (EmployeeID, LogDate).
     * If excludeId is not null, that ID will be ignored (for updates).
     */
    private boolean existsTimeLogForEmployeeOnDate(int employeeId,
                                                   java.time.LocalDate logDate,
                                                   Integer excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM EmployeesTimeLogs " +
                     "WHERE EmployeeID = ? AND LogDate = ?";

        // Exclude the current row when updating
        if (excludeId != null) {
            sql += " AND EmployeeTimeLogID <> ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, employeeId);
            pstmt.setDate(2, Date.valueOf(logDate));

            if (excludeId != null) {
                pstmt.setInt(3, excludeId);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Convert Java DayOfWeek to the exact strings used in your ENUM:
     * 'Monday','Tuesday','Wednesday','Thursday','Friday'
     */
    private String toMySqlDayName(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:    return "Monday";
            case TUESDAY:   return "Tuesday";
            case WEDNESDAY: return "Wednesday";
            case THURSDAY:  return "Thursday";
            case FRIDAY:    return "Friday";
            case SATURDAY:  return "Saturday";
            case SUNDAY:    return "Sunday";
            default:        throw new IllegalArgumentException("Unknown day of week: " + dayOfWeek);
        }
    }
}
