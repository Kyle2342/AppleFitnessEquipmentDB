package com.applefitnessequipment.model;

public class Client {
    private Integer clientId;
    private String clientType; // Individual, Business
    private String firstName;
    private String lastName;
    private String companyName;
    private String phoneNumber;
    private String email;
    private String notes;

    // Constructors
    public Client() {}

    public Client(Integer clientId, String clientType, String firstName, String lastName,
                  String companyName, String phoneNumber, String email, String notes) {
        this.clientId = clientId;
        this.clientType = clientType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.companyName = companyName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.notes = notes;
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

    @Override
    public String toString() {
        boolean isBusiness = "Business".equalsIgnoreCase(clientType);

        String company = companyName != null ? companyName.trim() : "";
        String fullName = ((firstName != null ? firstName.trim() : "") + " " +
                (lastName != null ? lastName.trim() : "")).trim();

        if (isBusiness) {
            if (!company.isEmpty()) return company;
            if (!fullName.isEmpty()) return fullName;
        } else { // Individual
            if (!fullName.isEmpty()) return fullName;
            if (!company.isEmpty()) return company;
        }

        return "Unnamed Client";
    }
}
