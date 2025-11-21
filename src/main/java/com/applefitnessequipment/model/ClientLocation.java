package com.applefitnessequipment.model;

import java.time.LocalDateTime;

public class ClientLocation {
    private Integer clientLocationId;
    private Integer clientId;
    private String locationType; // Billing, Job
    private String companyName;
    private String contactName;
    private String streetAddress;
    private String buildingName;
    private String suite;
    private String roomNumber;
    private String department;
    private String city;
    private String county;
    private String state;
    private String zipCode;
    private String country;
    private String phone;
    private String fax;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ClientLocation() {}

    // Getters and Setters
    public Integer getClientLocationId() { return clientLocationId; }
    public void setClientLocationId(Integer clientLocationId) { this.clientLocationId = clientLocationId; }

    public Integer getClientId() { return clientId; }
    public void setClientId(Integer clientId) { this.clientId = clientId; }

    public String getLocationType() { return locationType; }
    public void setLocationType(String locationType) { this.locationType = locationType; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }

    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }

    public String getSuite() { return suite; }
    public void setSuite(String suite) { this.suite = suite; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCounty() { return county; }
    public void setCounty(String county) { this.county = county; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getFax() { return fax; }
    public void setFax(String fax) { this.fax = fax; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        String address = streetAddress != null ? streetAddress : "";
        String cityState = (city != null ? city : "") + ", " + (state != null ? state : "");
        String type = locationType != null ? locationType + ": " : "";
        return type + address + (address.isEmpty() ? "" : ", ") + cityState.trim();
    }
}
