package com.applefitnessequipment.model;

import java.time.LocalDateTime;

public class Client {
    private Integer clientId;
    private String clientType; // Individual, Business
    private String firstName;
    private String lastName;
    private String companyName;
    private String phoneNumber;
    private String email;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Client() {}

    public Client(Integer clientId, String clientType, String firstName, String lastName, 
                  String companyName, String phoneNumber, String email, String notes,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.clientId = clientId;
        this.clientType = clientType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.companyName = companyName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Integer getClientId() { return clientId; }
    public void setClientId(Integer clientId) { this.clientId = clientId; }

    public String getClientType() { return clientType; }
    public void setClientType(String clientType) { this.clientType = clientType; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        StringBuilder display = new StringBuilder();

        // Add company name if present
        if (companyName != null && !companyName.trim().isEmpty()) {
            display.append(companyName.trim());
        }

        // Add first/last name if present
        String fullName = (firstName != null ? firstName.trim() : "") + " " + (lastName != null ? lastName.trim() : "");
        fullName = fullName.trim();

        if (!fullName.isEmpty()) {
            if (display.length() > 0) {
                display.append(" - ").append(fullName);
            } else {
                display.append(fullName);
            }
        }

        // Fallback if everything is empty
        return display.length() > 0 ? display.toString() : "Unnamed Client";
    }
}
