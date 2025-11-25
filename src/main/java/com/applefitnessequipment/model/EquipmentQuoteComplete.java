package com.applefitnessequipment.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EquipmentQuoteComplete {
    private Integer quoteId;
    private Integer clientId;
    private Integer billingLocationId;
    private Integer jobLocationId;
    private String clientTypeSnapshot;
    private String clientCompanyNameSnapshot;
    private String clientFirstNameSnapshot;
    private String clientLastNameSnapshot;
    
    // Billing Address
    private String billToCompanyName;
    private String billToContactName;
    private String billToStreetAddress;
    private String billToBuildingName;
    private String billToSuite;
    private String billToRoomNumber;
    private String billToDepartment;
    private String billToCounty;
    private String billToCity;
    private String billToState;
    private String billToZipCode;
    private String billToCountry;
    private String billToPhone;
    private String billToFax;
    private String billToPONumber;
    
    // Job Address
    private String jobAtCompanyName;
    private String jobAtContactName;
    private String jobAtStreetAddress;
    private String jobAtBuildingName;
    private String jobAtSuite;
    private String jobAtRoomNumber;
    private String jobAtDepartment;
    private String jobAtCounty;
    private String jobAtCity;
    private String jobAtState;
    private String jobAtZipCode;
    private String jobAtCountry;
    private String jobAtEmail;
    private String jobAtPONumber;
    
    // Quote Details
    private String quoteNumber;
    private LocalDate quoteDate;
    private String status;
    private String contactName;
    private String salespersonName;
    private String shipVia;
    private String freightTerms;
    private String paymentTerms;
    private String fobLocation;
    
    // Amounts
    private BigDecimal totalDiscountAmount;
    private BigDecimal subtotalAmount;
    private BigDecimal freightAmount;
    private BigDecimal extendedTotalAmount;
    private BigDecimal salesTaxRatePercent;
    private BigDecimal salesTaxAmount;
    private BigDecimal quoteTotalAmount;
    
    // Additional Info
    private String notes;
    private String termsAndConditions;
    private String extraInfo;
    private Boolean clientSignatureBoolean;

    public EquipmentQuoteComplete() {}

    // Getters and Setters
    public Integer getQuoteId() { return quoteId; }
    public void setQuoteId(Integer quoteId) { this.quoteId = quoteId; }

    public Integer getClientId() { return clientId; }
    public void setClientId(Integer clientId) { this.clientId = clientId; }

    public Integer getBillingLocationId() { return billingLocationId; }
    public void setBillingLocationId(Integer billingLocationId) { this.billingLocationId = billingLocationId; }

    public Integer getJobLocationId() { return jobLocationId; }
    public void setJobLocationId(Integer jobLocationId) { this.jobLocationId = jobLocationId; }

    public String getClientTypeSnapshot() { return clientTypeSnapshot; }
    public void setClientTypeSnapshot(String clientTypeSnapshot) { this.clientTypeSnapshot = clientTypeSnapshot; }

    public String getClientCompanyNameSnapshot() { return clientCompanyNameSnapshot; }
    public void setClientCompanyNameSnapshot(String clientCompanyNameSnapshot) { this.clientCompanyNameSnapshot = clientCompanyNameSnapshot; }

    public String getClientFirstNameSnapshot() { return clientFirstNameSnapshot; }
    public void setClientFirstNameSnapshot(String clientFirstNameSnapshot) { this.clientFirstNameSnapshot = clientFirstNameSnapshot; }

    public String getClientLastNameSnapshot() { return clientLastNameSnapshot; }
    public void setClientLastNameSnapshot(String clientLastNameSnapshot) { this.clientLastNameSnapshot = clientLastNameSnapshot; }

    // Billing Address Getters/Setters
    public String getBillToCompanyName() { return billToCompanyName; }
    public void setBillToCompanyName(String billToCompanyName) { this.billToCompanyName = billToCompanyName; }

    public String getBillToContactName() { return billToContactName; }
    public void setBillToContactName(String billToContactName) { this.billToContactName = billToContactName; }

    public String getBillToStreetAddress() { return billToStreetAddress; }
    public void setBillToStreetAddress(String billToStreetAddress) { this.billToStreetAddress = billToStreetAddress; }

    public String getBillToBuildingName() { return billToBuildingName; }
    public void setBillToBuildingName(String billToBuildingName) { this.billToBuildingName = billToBuildingName; }

    public String getBillToSuite() { return billToSuite; }
    public void setBillToSuite(String billToSuite) { this.billToSuite = billToSuite; }

    public String getBillToRoomNumber() { return billToRoomNumber; }
    public void setBillToRoomNumber(String billToRoomNumber) { this.billToRoomNumber = billToRoomNumber; }

    public String getBillToDepartment() { return billToDepartment; }
    public void setBillToDepartment(String billToDepartment) { this.billToDepartment = billToDepartment; }

    public String getBillToCounty() { return billToCounty; }
    public void setBillToCounty(String billToCounty) { this.billToCounty = billToCounty; }

    public String getBillToCity() { return billToCity; }
    public void setBillToCity(String billToCity) { this.billToCity = billToCity; }

    public String getBillToState() { return billToState; }
    public void setBillToState(String billToState) { this.billToState = billToState; }

    public String getBillToZipCode() { return billToZipCode; }
    public void setBillToZipCode(String billToZipCode) { this.billToZipCode = billToZipCode; }

    public String getBillToCountry() { return billToCountry; }
    public void setBillToCountry(String billToCountry) { this.billToCountry = billToCountry; }

    public String getBillToPhone() { return billToPhone; }
    public void setBillToPhone(String billToPhone) { this.billToPhone = billToPhone; }

    public String getBillToFax() { return billToFax; }
    public void setBillToFax(String billToFax) { this.billToFax = billToFax; }

    public String getBillToPONumber() { return billToPONumber; }
    public void setBillToPONumber(String billToPONumber) { this.billToPONumber = billToPONumber; }

    // Job Address Getters/Setters
    public String getJobAtCompanyName() { return jobAtCompanyName; }
    public void setJobAtCompanyName(String jobAtCompanyName) { this.jobAtCompanyName = jobAtCompanyName; }

    public String getJobAtContactName() { return jobAtContactName; }
    public void setJobAtContactName(String jobAtContactName) { this.jobAtContactName = jobAtContactName; }

    public String getJobAtStreetAddress() { return jobAtStreetAddress; }
    public void setJobAtStreetAddress(String jobAtStreetAddress) { this.jobAtStreetAddress = jobAtStreetAddress; }

    public String getJobAtBuildingName() { return jobAtBuildingName; }
    public void setJobAtBuildingName(String jobAtBuildingName) { this.jobAtBuildingName = jobAtBuildingName; }

    public String getJobAtSuite() { return jobAtSuite; }
    public void setJobAtSuite(String jobAtSuite) { this.jobAtSuite = jobAtSuite; }

    public String getJobAtRoomNumber() { return jobAtRoomNumber; }
    public void setJobAtRoomNumber(String jobAtRoomNumber) { this.jobAtRoomNumber = jobAtRoomNumber; }

    public String getJobAtDepartment() { return jobAtDepartment; }
    public void setJobAtDepartment(String jobAtDepartment) { this.jobAtDepartment = jobAtDepartment; }

    public String getJobAtCounty() { return jobAtCounty; }
    public void setJobAtCounty(String jobAtCounty) { this.jobAtCounty = jobAtCounty; }

    public String getJobAtCity() { return jobAtCity; }
    public void setJobAtCity(String jobAtCity) { this.jobAtCity = jobAtCity; }

    public String getJobAtState() { return jobAtState; }
    public void setJobAtState(String jobAtState) { this.jobAtState = jobAtState; }

    public String getJobAtZipCode() { return jobAtZipCode; }
    public void setJobAtZipCode(String jobAtZipCode) { this.jobAtZipCode = jobAtZipCode; }

    public String getJobAtCountry() { return jobAtCountry; }
    public void setJobAtCountry(String jobAtCountry) { this.jobAtCountry = jobAtCountry; }

    public String getJobAtEmail() { return jobAtEmail; }
    public void setJobAtEmail(String jobAtEmail) { this.jobAtEmail = jobAtEmail; }

    public String getJobAtPONumber() { return jobAtPONumber; }
    public void setJobAtPONumber(String jobAtPONumber) { this.jobAtPONumber = jobAtPONumber; }

    // Quote Details Getters/Setters
    public String getQuoteNumber() { return quoteNumber; }
    public void setQuoteNumber(String quoteNumber) { this.quoteNumber = quoteNumber; }

    public LocalDate getQuoteDate() { return quoteDate; }
    public void setQuoteDate(LocalDate quoteDate) { this.quoteDate = quoteDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getSalespersonName() { return salespersonName; }
    public void setSalespersonName(String salespersonName) { this.salespersonName = salespersonName; }

    public String getShipVia() { return shipVia; }
    public void setShipVia(String shipVia) { this.shipVia = shipVia; }

    public String getFreightTerms() { return freightTerms; }
    public void setFreightTerms(String freightTerms) { this.freightTerms = freightTerms; }

    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }

    public String getFobLocation() { return fobLocation; }
    public void setFobLocation(String fobLocation) { this.fobLocation = fobLocation; }

    // Amounts Getters/Setters
    public BigDecimal getTotalDiscountAmount() { return totalDiscountAmount; }
    public void setTotalDiscountAmount(BigDecimal totalDiscountAmount) { this.totalDiscountAmount = totalDiscountAmount; }

    public BigDecimal getSubtotalAmount() { return subtotalAmount; }
    public void setSubtotalAmount(BigDecimal subtotalAmount) { this.subtotalAmount = subtotalAmount; }

    public BigDecimal getFreightAmount() { return freightAmount; }
    public void setFreightAmount(BigDecimal freightAmount) { this.freightAmount = freightAmount; }

    public BigDecimal getExtendedTotalAmount() { return extendedTotalAmount; }
    public void setExtendedTotalAmount(BigDecimal extendedTotalAmount) { this.extendedTotalAmount = extendedTotalAmount; }

    public BigDecimal getSalesTaxRatePercent() { return salesTaxRatePercent; }
    public void setSalesTaxRatePercent(BigDecimal salesTaxRatePercent) { this.salesTaxRatePercent = salesTaxRatePercent; }

    public BigDecimal getSalesTaxAmount() { return salesTaxAmount; }
    public void setSalesTaxAmount(BigDecimal salesTaxAmount) { this.salesTaxAmount = salesTaxAmount; }

    public BigDecimal getQuoteTotalAmount() { return quoteTotalAmount; }
    public void setQuoteTotalAmount(BigDecimal quoteTotalAmount) { this.quoteTotalAmount = quoteTotalAmount; }

    // Additional Info Getters/Setters
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getTermsAndConditions() { return termsAndConditions; }
    public void setTermsAndConditions(String termsAndConditions) { this.termsAndConditions = termsAndConditions; }

    public String getExtraInfo() { return extraInfo; }
    public void setExtraInfo(String extraInfo) { this.extraInfo = extraInfo; }

    public Boolean getClientSignatureBoolean() { return clientSignatureBoolean; }
    public void setClientSignatureBoolean(Boolean clientSignatureBoolean) { this.clientSignatureBoolean = clientSignatureBoolean; }
}
