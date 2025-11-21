package com.applefitnessequipment.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Employee {
    private Integer employeeId;
    private String firstName;
    private String lastName;
    private String middleInitial;
    private LocalDate dateOfBirth;
    private String gender;
    private String workEmail;
    private String personalEmail;
    private String workPhone;
    private String mobilePhone;
    private String homeBuildingName;
    private String homeSuiteNumber;
    private String homeStreetAddress;
    private String homeCity;
    private String homeState;
    private String homeZIPCode;
    private String homeCountry;
    private String positionTitle;
    private String employmentType;
    private LocalDate hireDate;
    private LocalDate terminationDate;
    private Boolean activeStatus;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;
    private String username;
    private String passwordHash;
    private String payType;
    private BigDecimal payRate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Employee() {}

    // Getters and Setters
    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getMiddleInitial() { return middleInitial; }
    public void setMiddleInitial(String middleInitial) { this.middleInitial = middleInitial; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getWorkEmail() { return workEmail; }
    public void setWorkEmail(String workEmail) { this.workEmail = workEmail; }

    public String getPersonalEmail() { return personalEmail; }
    public void setPersonalEmail(String personalEmail) { this.personalEmail = personalEmail; }

    public String getWorkPhone() { return workPhone; }
    public void setWorkPhone(String workPhone) { this.workPhone = workPhone; }

    public String getMobilePhone() { return mobilePhone; }
    public void setMobilePhone(String mobilePhone) { this.mobilePhone = mobilePhone; }

    public String getHomeBuildingName() { return homeBuildingName; }
    public void setHomeBuildingName(String homeBuildingName) { this.homeBuildingName = homeBuildingName; }

    public String getHomeSuiteNumber() { return homeSuiteNumber; }
    public void setHomeSuiteNumber(String homeSuiteNumber) { this.homeSuiteNumber = homeSuiteNumber; }

    public String getHomeStreetAddress() { return homeStreetAddress; }
    public void setHomeStreetAddress(String homeStreetAddress) { this.homeStreetAddress = homeStreetAddress; }

    public String getHomeCity() { return homeCity; }
    public void setHomeCity(String homeCity) { this.homeCity = homeCity; }

    public String getHomeState() { return homeState; }
    public void setHomeState(String homeState) { this.homeState = homeState; }

    public String getHomeZIPCode() { return homeZIPCode; }
    public void setHomeZIPCode(String homeZIPCode) { this.homeZIPCode = homeZIPCode; }

    public String getHomeCountry() { return homeCountry; }
    public void setHomeCountry(String homeCountry) { this.homeCountry = homeCountry; }

    public String getPositionTitle() { return positionTitle; }
    public void setPositionTitle(String positionTitle) { this.positionTitle = positionTitle; }

    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public LocalDate getTerminationDate() { return terminationDate; }
    public void setTerminationDate(LocalDate terminationDate) { this.terminationDate = terminationDate; }

    public Boolean getActiveStatus() { return activeStatus; }
    public void setActiveStatus(Boolean activeStatus) { this.activeStatus = activeStatus; }

    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }

    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }

    public String getEmergencyContactRelationship() { return emergencyContactRelationship; }
    public void setEmergencyContactRelationship(String emergencyContactRelationship) { this.emergencyContactRelationship = emergencyContactRelationship; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getPayType() { return payType; }
    public void setPayType(String payType) { this.payType = payType; }

    public BigDecimal getPayRate() { return payRate; }
    public void setPayRate(BigDecimal payRate) { this.payRate = payRate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        String name = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
        String position = positionTitle != null ? " (" + positionTitle + ")" : "";
        return name.trim().isEmpty() ? "Unnamed Employee" : name.trim() + position;
    }
}
