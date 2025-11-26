package com.applefitnessequipment.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Employee {

    // =========================================================
    // FIELDS (aligned with CREATE TABLE Employees)
    // =========================================================

    // PK
    private Integer employeeId;

    // Personal info
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String email;
    private String phoneNumber;

    // Address
    private String buildingName;
    private String suiteNumber;
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    // Employment details
    private String positionTitle;
    private String employmentType;
    private String payType;
    private BigDecimal payRate;
    private LocalDate hireDate;
    private LocalDate terminationDate;
    private Boolean activeStatus;

    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    public Employee() {
        // no-arg constructor for frameworks / DAO
    }

    /**
     * Convenience constructor for creating new employees (no ID yet).
     * You can use this in your UI/service layer if you want.
     */
    public Employee(
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            String gender,
            String email,
            String phoneNumber,
            String buildingName,
            String suiteNumber,
            String streetAddress,
            String city,
            String state,
            String zipCode,
            String country,
            String positionTitle,
            String employmentType,
            String payType,
            BigDecimal payRate,
            LocalDate hireDate,
            LocalDate terminationDate,
            Boolean activeStatus
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.buildingName = buildingName;
        this.suiteNumber = suiteNumber;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
        this.positionTitle = positionTitle;
        this.employmentType = employmentType;
        this.payType = payType;
        this.payRate = payRate;
        this.hireDate = hireDate;
        this.terminationDate = terminationDate;
        this.activeStatus = activeStatus;
    }

    // =========================================================
    // GETTERS & SETTERS
    // =========================================================

    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }

    public String getSuiteNumber() { return suiteNumber; }
    public void setSuiteNumber(String suiteNumber) { this.suiteNumber = suiteNumber; }

    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPositionTitle() { return positionTitle; }
    public void setPositionTitle(String positionTitle) { this.positionTitle = positionTitle; }

    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public String getPayType() { return payType; }
    public void setPayType(String payType) { this.payType = payType; }

    public BigDecimal getPayRate() { return payRate; }
    public void setPayRate(BigDecimal payRate) { this.payRate = payRate; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public LocalDate getTerminationDate() { return terminationDate; }
    public void setTerminationDate(LocalDate terminationDate) { this.terminationDate = terminationDate; }

    public Boolean getActiveStatus() { return activeStatus; }
    public void setActiveStatus(Boolean activeStatus) { this.activeStatus = activeStatus; }

    // =========================================================
    // CONVENIENCE METHODS
    // =========================================================

    public boolean isActive() {
        // fall back to DB default = true if null
        return activeStatus == null || Boolean.TRUE.equals(activeStatus);
    }

    public String getFullName() {
        String first = firstName != null ? firstName.trim() : "";
        String last = lastName != null ? lastName.trim() : "";
        String full = (first + " " + last).trim();
        return full.isEmpty() ? null : full;
    }

    @Override
    public String toString() {
        String name = getFullName();
        String positionSuffix = positionTitle != null && !positionTitle.trim().isEmpty()
                ? " (" + positionTitle.trim() + ")"
                : "";
        return name == null ? "Unnamed Employee" : name + positionSuffix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee that)) return false;
        // Use ID for equality
        return Objects.equals(employeeId, that.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId);
    }
}
