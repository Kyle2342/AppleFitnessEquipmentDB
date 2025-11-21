package com.applefitnessequipment.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class EmployeeTimeLog {
    private Integer timeLogId;
    private Integer employeeId;
    private String dayOfWeek;
    private LocalDate logDate;
    private LocalTime timeInFirst;
    private LocalTime timeOutFirst;
    private LocalTime timeInSecond;
    private LocalTime timeOutSecond;
    private BigDecimal totalHours;
    private BigDecimal miles;
    private BigDecimal ptoHours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EmployeeTimeLog() {}

    public Integer getTimeLogId() {
        return timeLogId;
    }

    public void setTimeLogId(Integer timeLogId) {
        this.timeLogId = timeLogId;
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

    public LocalTime getTimeInFirst() {
        return timeInFirst;
    }

    public void setTimeInFirst(LocalTime timeInFirst) {
        this.timeInFirst = timeInFirst;
    }

    public LocalTime getTimeOutFirst() {
        return timeOutFirst;
    }

    public void setTimeOutFirst(LocalTime timeOutFirst) {
        this.timeOutFirst = timeOutFirst;
    }

    public LocalTime getTimeInSecond() {
        return timeInSecond;
    }

    public void setTimeInSecond(LocalTime timeInSecond) {
        this.timeInSecond = timeInSecond;
    }

    public LocalTime getTimeOutSecond() {
        return timeOutSecond;
    }

    public void setTimeOutSecond(LocalTime timeOutSecond) {
        this.timeOutSecond = timeOutSecond;
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

    public BigDecimal getPtoHours() {
        return ptoHours;
    }

    public void setPtoHours(BigDecimal ptoHours) {
        this.ptoHours = ptoHours;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return logDate != null ? logDate.toString() : "New Time Log";
    }
}
