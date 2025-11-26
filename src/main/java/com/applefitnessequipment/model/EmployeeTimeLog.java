package com.applefitnessequipment.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class EmployeeTimeLog {

    private Integer employeeTimeLogId; // EmployeeTimeLogID (PK)
    private Integer employeeId;        // EmployeeID (FK)
    private String dayOfWeek;          // ENUM('Monday',...)
    private LocalDate logDate;         // LogDate
    private LocalTime timeIn;          // TimeIn
    private LocalTime timeOut;         // TimeOut
    private BigDecimal totalHours;     // TotalHours (generated in DB)
    private BigDecimal miles;          // Miles

    // ----- Constructors -----

    public EmployeeTimeLog() {
        // no-arg constructor for frameworks / reflection
    }

    /** Convenience constructor for a *new* time log before it has a DB ID. */
    public EmployeeTimeLog(Integer employeeId,
                           String dayOfWeek,
                           LocalDate logDate,
                           LocalTime timeIn,
                           LocalTime timeOut,
                           BigDecimal miles) {
        this(null, employeeId, dayOfWeek, logDate, timeIn, timeOut, null, miles);
    }

    /** Full constructor matching all columns. */
    public EmployeeTimeLog(Integer employeeTimeLogId,
                           Integer employeeId,
                           String dayOfWeek,
                           LocalDate logDate,
                           LocalTime timeIn,
                           LocalTime timeOut,
                           BigDecimal totalHours,
                           BigDecimal miles) {
        this.employeeTimeLogId = employeeTimeLogId;
        this.employeeId = employeeId;
        this.dayOfWeek = dayOfWeek;
        this.logDate = logDate;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.totalHours = totalHours;
        this.miles = miles;
    }

    // ----- Getters / Setters -----

    public Integer getEmployeeTimeLogId() {
        return employeeTimeLogId;
    }

    public void setEmployeeTimeLogId(Integer employeeTimeLogId) {
        this.employeeTimeLogId = employeeTimeLogId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalDate getLogDate() {
        return logDate;
    }

    public void setLogDate(LocalDate logDate) {
        this.logDate = logDate;
    }

    public LocalTime getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(LocalTime timeIn) {
        this.timeIn = timeIn;
    }

    public LocalTime getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(LocalTime timeOut) {
        this.timeOut = timeOut;
    }

    public BigDecimal getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(BigDecimal totalHours) {
        this.totalHours = totalHours;
    }

    public BigDecimal getMiles() {
        return miles;
    }

    public void setMiles(BigDecimal miles) {
        this.miles = miles;
    }

    // ----- Convenience / Business Logic -----

    /**
     * Recalculate totalHours in Java using the same logic as the DB:
     * (TIME_TO_SEC(TimeOut) - TIME_TO_SEC(TimeIn)) / 3600
     * Clamped to >= 0 and scaled to 2 decimal places.
     */
    public void recalculateTotalHours() {
        if (timeIn == null || timeOut == null) {
            this.totalHours = null;
            return;
        }

        long seconds = Duration.between(timeIn, timeOut).getSeconds();
        if (seconds < 0) {
            // Mirror chk_timeout_after_timein; caller/validator should prevent this,
            // but defensively clamp to zero so UI doesn't show negative hours.
            this.totalHours = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            return;
        }

        BigDecimal hours = BigDecimal.valueOf(seconds)
                .divide(BigDecimal.valueOf(3600), 2, RoundingMode.HALF_UP);

        this.totalHours = hours.max(BigDecimal.ZERO);
    }

    /** Helper that mirrors chk_timeout_after_timein from the DB constraint. */
    public boolean hasValidTimeRange() {
        return timeIn != null && timeOut != null && timeOut.isAfter(timeIn);
    }

    // ----- Object overrides -----

    @Override
    public String toString() {
        if (logDate == null) {
            return "New Time Log";
        }
        StringBuilder sb = new StringBuilder(logDate.toString());
        if (dayOfWeek != null) {
            sb.append(" (").append(dayOfWeek).append(")");
        }
        if (timeIn != null && timeOut != null) {
            sb.append(" ").append(timeIn).append(" - ").append(timeOut);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeeTimeLog)) return false;
        EmployeeTimeLog that = (EmployeeTimeLog) o;

        // If both have a DB ID, use it
        if (employeeTimeLogId != null && that.employeeTimeLogId != null) {
            return Objects.equals(employeeTimeLogId, that.employeeTimeLogId);
        }

        // Otherwise fall back to natural key (EmployeeID + LogDate)
        return Objects.equals(employeeId, that.employeeId)
                && Objects.equals(logDate, that.logDate);
    }

    @Override
    public int hashCode() {
        if (employeeTimeLogId != null) {
            return Objects.hash(employeeTimeLogId);
        }
        return Objects.hash(employeeId, logDate);
    }
}
