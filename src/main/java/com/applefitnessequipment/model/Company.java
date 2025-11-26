package com.applefitnessequipment.model;

public class Company {
    private Integer companyId;
    private String companyName;
    private String streetAddress;
    private String city;
    private String county;
    private String state;
    private String zipCode;
    private String country;
    private String phone;
    private String fax;
    private String email;
    private String websiteURL;

    // Constructors
    public Company() {}

    public Company(Integer companyId, String companyName, String streetAddress, String city,
                   String county, String state, String zipCode, String country, String phone,
                   String fax, String email, String websiteURL) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.streetAddress = streetAddress;
        this.city = city;
        this.county = county;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
        this.phone = phone;
        this.fax = fax;
        this.email = email;
        this.websiteURL = websiteURL;
    }

    // Getters and Setters
    public Integer getCompanyId() { return companyId; }
    public void setCompanyId(Integer companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }

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

    public String getWebsiteURL() { return websiteURL; }
    public void setWebsiteURL(String websiteURL) { this.websiteURL = websiteURL; }

    @Override
    public String toString() {
        return companyName != null ? companyName : "Company";
    }
}
