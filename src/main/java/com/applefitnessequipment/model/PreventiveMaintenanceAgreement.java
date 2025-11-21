package com.applefitnessequipment.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PreventiveMaintenanceAgreement {
    private Integer pmaId;
    private Integer clientId;
    private Integer billingLocationId;
    private Integer jobLocationId;
    private String propertyName;
    private String facilityName;
    private String addressLine;
    private String city;
    private String state;
    private String zipCode;
    private String contactName;
    private String contactEmail;
    private String phoneNumber1;
    private String phoneNumber2;
    private String agreementNumber;
    private String coverageText;
    private LocalDate startDate;
    private LocalDate endDate;
    private String visitFrequency; // Monthly, Quarterly, Semi-Annual, Annual
    private String status; // Draft, Active, Expired, Canceled
    private BigDecimal chargePerMile;
    private BigDecimal chargePerHour;
    private BigDecimal visitPrice;
    private BigDecimal taxRatePercent;
    private Boolean requiresAdditionalInsurance;
    private Integer cancelationNoticeDays;
    private Integer paymentDueAfterWorkDays;
    private BigDecimal lateFeePercentage;
    private String facilityAgentSignature;
    private LocalDate signatureDate;

    public PreventiveMaintenanceAgreement() {}

    // Getters and Setters
    public Integer getPmaId() { return pmaId; }
    public void setPmaId(Integer pmaId) { this.pmaId = pmaId; }

    public Integer getClientId() { return clientId; }
    public void setClientId(Integer clientId) { this.clientId = clientId; }

    public Integer getBillingLocationId() { return billingLocationId; }
    public void setBillingLocationId(Integer billingLocationId) { this.billingLocationId = billingLocationId; }

    public Integer getJobLocationId() { return jobLocationId; }
    public void setJobLocationId(Integer jobLocationId) { this.jobLocationId = jobLocationId; }

    public String getPropertyName() { return propertyName; }
    public void setPropertyName(String propertyName) { this.propertyName = propertyName; }

    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

    public String getAddressLine() { return addressLine; }
    public void setAddressLine(String addressLine) { this.addressLine = addressLine; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getPhoneNumber1() { return phoneNumber1; }
    public void setPhoneNumber1(String phoneNumber1) { this.phoneNumber1 = phoneNumber1; }

    public String getPhoneNumber2() { return phoneNumber2; }
    public void setPhoneNumber2(String phoneNumber2) { this.phoneNumber2 = phoneNumber2; }

    public String getAgreementNumber() { return agreementNumber; }
    public void setAgreementNumber(String agreementNumber) { this.agreementNumber = agreementNumber; }

    public String getCoverageText() { return coverageText; }
    public void setCoverageText(String coverageText) { this.coverageText = coverageText; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getVisitFrequency() { return visitFrequency; }
    public void setVisitFrequency(String visitFrequency) { this.visitFrequency = visitFrequency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getChargePerMile() { return chargePerMile; }
    public void setChargePerMile(BigDecimal chargePerMile) { this.chargePerMile = chargePerMile; }

    public BigDecimal getChargePerHour() { return chargePerHour; }
    public void setChargePerHour(BigDecimal chargePerHour) { this.chargePerHour = chargePerHour; }

    public BigDecimal getVisitPrice() { return visitPrice; }
    public void setVisitPrice(BigDecimal visitPrice) { this.visitPrice = visitPrice; }

    public BigDecimal getTaxRatePercent() { return taxRatePercent; }
    public void setTaxRatePercent(BigDecimal taxRatePercent) { this.taxRatePercent = taxRatePercent; }

    public Boolean getRequiresAdditionalInsurance() { return requiresAdditionalInsurance; }
    public void setRequiresAdditionalInsurance(Boolean requiresAdditionalInsurance) { 
        this.requiresAdditionalInsurance = requiresAdditionalInsurance; 
    }

    public Integer getCancelationNoticeDays() { return cancelationNoticeDays; }
    public void setCancelationNoticeDays(Integer cancelationNoticeDays) { 
        this.cancelationNoticeDays = cancelationNoticeDays; 
    }

    public Integer getPaymentDueAfterWorkDays() { return paymentDueAfterWorkDays; }
    public void setPaymentDueAfterWorkDays(Integer paymentDueAfterWorkDays) { 
        this.paymentDueAfterWorkDays = paymentDueAfterWorkDays; 
    }

    public BigDecimal getLateFeePercentage() { return lateFeePercentage; }
    public void setLateFeePercentage(BigDecimal lateFeePercentage) { 
        this.lateFeePercentage = lateFeePercentage; 
    }

    public String getFacilityAgentSignature() { return facilityAgentSignature; }
    public void setFacilityAgentSignature(String facilityAgentSignature) { 
        this.facilityAgentSignature = facilityAgentSignature; 
    }

    public LocalDate getSignatureDate() { return signatureDate; }
    public void setSignatureDate(LocalDate signatureDate) { this.signatureDate = signatureDate; }
}
