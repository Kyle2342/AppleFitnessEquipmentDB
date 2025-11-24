package com.applefitnessequipment.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PreventiveMaintenanceAgreement {
    private Integer pmaId;
    private Integer clientId;
    private Integer billingLocationId;
    private Integer jobLocationId;

    // FROM company information
    private String fromCompanyName;
    private String fromStreetAddress;
    private String fromCity;
    private String fromState;
    private String fromZIPCode;
    private String fromPhone;
    private String fromFax;

    // Client snapshot information
    private String clientTypeSnapshot;
    private String clientCompanyNameSnapshot;
    private String clientFirstNameSnapshot;
    private String clientLastNameSnapshot;

    // BILL TO snapshot
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
    private String billToZIPCode;
    private String billToCountry;
    private String billToPhone;
    private String billToFax;
    private String billToPONumber;

    // JOB AT snapshot
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
    private String jobAtZIPCode;
    private String jobAtCountry;
    private String jobAtEmail;
    private String jobAtPONumber;

    // Header snapshot
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

    // Agreement Details
    private String agreementNumber;
    private String agreementTerms;
    private LocalDate startDate;
    private LocalDate endDate;
    private String visitFrequency;
    private String status;

    // Financial Terms
    private BigDecimal chargePerMile;
    private BigDecimal chargePerHour;
    private BigDecimal visitPrice;
    private BigDecimal taxRatePercent;
    private Boolean requiresAdditionalInsurance;

    // Legal & Tracking
    private Integer cancelationNoticeDays;
    private Integer paymentDueAfterWorkDays;
    private BigDecimal lateFeePercentage;
    private Boolean clientSignatureBoolean;
    private String facilityAgentSignature;
    private LocalDate signatureDate;

    // Record keeping
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PreventiveMaintenanceAgreement() {}

    // Getters and Setters
    public Integer getPmaId() {
        return pmaId;
    }

    public void setPmaId(Integer pmaId) {
        this.pmaId = pmaId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getBillingLocationId() {
        return billingLocationId;
    }

    public void setBillingLocationId(Integer billingLocationId) {
        this.billingLocationId = billingLocationId;
    }

    public Integer getJobLocationId() {
        return jobLocationId;
    }

    public void setJobLocationId(Integer jobLocationId) {
        this.jobLocationId = jobLocationId;
    }

    public String getFromCompanyName() {
        return fromCompanyName;
    }

    public void setFromCompanyName(String fromCompanyName) {
        this.fromCompanyName = fromCompanyName;
    }

    public String getFromStreetAddress() {
        return fromStreetAddress;
    }

    public void setFromStreetAddress(String fromStreetAddress) {
        this.fromStreetAddress = fromStreetAddress;
    }

    public String getFromCity() {
        return fromCity;
    }

    public void setFromCity(String fromCity) {
        this.fromCity = fromCity;
    }

    public String getFromState() {
        return fromState;
    }

    public void setFromState(String fromState) {
        this.fromState = fromState;
    }

    public String getFromZIPCode() {
        return fromZIPCode;
    }

    public void setFromZIPCode(String fromZIPCode) {
        this.fromZIPCode = fromZIPCode;
    }

    public String getFromPhone() {
        return fromPhone;
    }

    public void setFromPhone(String fromPhone) {
        this.fromPhone = fromPhone;
    }

    public String getFromFax() {
        return fromFax;
    }

    public void setFromFax(String fromFax) {
        this.fromFax = fromFax;
    }

    public String getClientTypeSnapshot() {
        return clientTypeSnapshot;
    }

    public void setClientTypeSnapshot(String clientTypeSnapshot) {
        this.clientTypeSnapshot = clientTypeSnapshot;
    }

    public String getClientCompanyNameSnapshot() {
        return clientCompanyNameSnapshot;
    }

    public void setClientCompanyNameSnapshot(String clientCompanyNameSnapshot) {
        this.clientCompanyNameSnapshot = clientCompanyNameSnapshot;
    }

    public String getClientFirstNameSnapshot() {
        return clientFirstNameSnapshot;
    }

    public void setClientFirstNameSnapshot(String clientFirstNameSnapshot) {
        this.clientFirstNameSnapshot = clientFirstNameSnapshot;
    }

    public String getClientLastNameSnapshot() {
        return clientLastNameSnapshot;
    }

    public void setClientLastNameSnapshot(String clientLastNameSnapshot) {
        this.clientLastNameSnapshot = clientLastNameSnapshot;
    }

    public String getBillToCompanyName() {
        return billToCompanyName;
    }

    public void setBillToCompanyName(String billToCompanyName) {
        this.billToCompanyName = billToCompanyName;
    }

    public String getBillToContactName() {
        return billToContactName;
    }

    public void setBillToContactName(String billToContactName) {
        this.billToContactName = billToContactName;
    }

    public String getBillToStreetAddress() {
        return billToStreetAddress;
    }

    public void setBillToStreetAddress(String billToStreetAddress) {
        this.billToStreetAddress = billToStreetAddress;
    }

    public String getBillToBuildingName() {
        return billToBuildingName;
    }

    public void setBillToBuildingName(String billToBuildingName) {
        this.billToBuildingName = billToBuildingName;
    }

    public String getBillToSuite() {
        return billToSuite;
    }

    public void setBillToSuite(String billToSuite) {
        this.billToSuite = billToSuite;
    }

    public String getBillToRoomNumber() {
        return billToRoomNumber;
    }

    public void setBillToRoomNumber(String billToRoomNumber) {
        this.billToRoomNumber = billToRoomNumber;
    }

    public String getBillToDepartment() {
        return billToDepartment;
    }

    public void setBillToDepartment(String billToDepartment) {
        this.billToDepartment = billToDepartment;
    }

    public String getBillToCounty() {
        return billToCounty;
    }

    public void setBillToCounty(String billToCounty) {
        this.billToCounty = billToCounty;
    }

    public String getBillToCity() {
        return billToCity;
    }

    public void setBillToCity(String billToCity) {
        this.billToCity = billToCity;
    }

    public String getBillToState() {
        return billToState;
    }

    public void setBillToState(String billToState) {
        this.billToState = billToState;
    }

    public String getBillToZIPCode() {
        return billToZIPCode;
    }

    public void setBillToZIPCode(String billToZIPCode) {
        this.billToZIPCode = billToZIPCode;
    }

    public String getBillToCountry() {
        return billToCountry;
    }

    public void setBillToCountry(String billToCountry) {
        this.billToCountry = billToCountry;
    }

    public String getBillToPhone() {
        return billToPhone;
    }

    public void setBillToPhone(String billToPhone) {
        this.billToPhone = billToPhone;
    }

    public String getBillToFax() {
        return billToFax;
    }

    public void setBillToFax(String billToFax) {
        this.billToFax = billToFax;
    }

    public String getBillToPONumber() {
        return billToPONumber;
    }

    public void setBillToPONumber(String billToPONumber) {
        this.billToPONumber = billToPONumber;
    }

    public String getJobAtCompanyName() {
        return jobAtCompanyName;
    }

    public void setJobAtCompanyName(String jobAtCompanyName) {
        this.jobAtCompanyName = jobAtCompanyName;
    }

    public String getJobAtContactName() {
        return jobAtContactName;
    }

    public void setJobAtContactName(String jobAtContactName) {
        this.jobAtContactName = jobAtContactName;
    }

    public String getJobAtStreetAddress() {
        return jobAtStreetAddress;
    }

    public void setJobAtStreetAddress(String jobAtStreetAddress) {
        this.jobAtStreetAddress = jobAtStreetAddress;
    }

    public String getJobAtBuildingName() {
        return jobAtBuildingName;
    }

    public void setJobAtBuildingName(String jobAtBuildingName) {
        this.jobAtBuildingName = jobAtBuildingName;
    }

    public String getJobAtSuite() {
        return jobAtSuite;
    }

    public void setJobAtSuite(String jobAtSuite) {
        this.jobAtSuite = jobAtSuite;
    }

    public String getJobAtRoomNumber() {
        return jobAtRoomNumber;
    }

    public void setJobAtRoomNumber(String jobAtRoomNumber) {
        this.jobAtRoomNumber = jobAtRoomNumber;
    }

    public String getJobAtDepartment() {
        return jobAtDepartment;
    }

    public void setJobAtDepartment(String jobAtDepartment) {
        this.jobAtDepartment = jobAtDepartment;
    }

    public String getJobAtCounty() {
        return jobAtCounty;
    }

    public void setJobAtCounty(String jobAtCounty) {
        this.jobAtCounty = jobAtCounty;
    }

    public String getJobAtCity() {
        return jobAtCity;
    }

    public void setJobAtCity(String jobAtCity) {
        this.jobAtCity = jobAtCity;
    }

    public String getJobAtState() {
        return jobAtState;
    }

    public void setJobAtState(String jobAtState) {
        this.jobAtState = jobAtState;
    }

    public String getJobAtZIPCode() {
        return jobAtZIPCode;
    }

    public void setJobAtZIPCode(String jobAtZIPCode) {
        this.jobAtZIPCode = jobAtZIPCode;
    }

    public String getJobAtCountry() {
        return jobAtCountry;
    }

    public void setJobAtCountry(String jobAtCountry) {
        this.jobAtCountry = jobAtCountry;
    }

    public String getJobAtEmail() {
        return jobAtEmail;
    }

    public void setJobAtEmail(String jobAtEmail) {
        this.jobAtEmail = jobAtEmail;
    }

    public String getJobAtPONumber() {
        return jobAtPONumber;
    }

    public void setJobAtPONumber(String jobAtPONumber) {
        this.jobAtPONumber = jobAtPONumber;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getPhoneNumber1() {
        return phoneNumber1;
    }

    public void setPhoneNumber1(String phoneNumber1) {
        this.phoneNumber1 = phoneNumber1;
    }

    public String getPhoneNumber2() {
        return phoneNumber2;
    }

    public void setPhoneNumber2(String phoneNumber2) {
        this.phoneNumber2 = phoneNumber2;
    }

    public String getAgreementNumber() {
        return agreementNumber;
    }

    public void setAgreementNumber(String agreementNumber) {
        this.agreementNumber = agreementNumber;
    }

    public String getAgreementTerms() {
        return agreementTerms;
    }

    public void setAgreementTerms(String agreementTerms) {
        this.agreementTerms = agreementTerms;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getVisitFrequency() {
        return visitFrequency;
    }

    public void setVisitFrequency(String visitFrequency) {
        this.visitFrequency = visitFrequency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getChargePerMile() {
        return chargePerMile;
    }

    public void setChargePerMile(BigDecimal chargePerMile) {
        this.chargePerMile = chargePerMile;
    }

    public BigDecimal getChargePerHour() {
        return chargePerHour;
    }

    public void setChargePerHour(BigDecimal chargePerHour) {
        this.chargePerHour = chargePerHour;
    }

    public BigDecimal getVisitPrice() {
        return visitPrice;
    }

    public void setVisitPrice(BigDecimal visitPrice) {
        this.visitPrice = visitPrice;
    }

    public BigDecimal getTaxRatePercent() {
        return taxRatePercent;
    }

    public void setTaxRatePercent(BigDecimal taxRatePercent) {
        this.taxRatePercent = taxRatePercent;
    }

    public Boolean getRequiresAdditionalInsurance() {
        return requiresAdditionalInsurance;
    }

    public void setRequiresAdditionalInsurance(Boolean requiresAdditionalInsurance) {
        this.requiresAdditionalInsurance = requiresAdditionalInsurance;
    }

    public Integer getCancelationNoticeDays() {
        return cancelationNoticeDays;
    }

    public void setCancelationNoticeDays(Integer cancelationNoticeDays) {
        this.cancelationNoticeDays = cancelationNoticeDays;
    }

    public Integer getPaymentDueAfterWorkDays() {
        return paymentDueAfterWorkDays;
    }

    public void setPaymentDueAfterWorkDays(Integer paymentDueAfterWorkDays) {
        this.paymentDueAfterWorkDays = paymentDueAfterWorkDays;
    }

    public BigDecimal getLateFeePercentage() {
        return lateFeePercentage;
    }

    public void setLateFeePercentage(BigDecimal lateFeePercentage) {
        this.lateFeePercentage = lateFeePercentage;
    }

    public Boolean getClientSignatureBoolean() {
        return clientSignatureBoolean;
    }

    public void setClientSignatureBoolean(Boolean clientSignatureBoolean) {
        this.clientSignatureBoolean = clientSignatureBoolean;
    }

    public String getFacilityAgentSignature() {
        return facilityAgentSignature;
    }

    public void setFacilityAgentSignature(String facilityAgentSignature) {
        this.facilityAgentSignature = facilityAgentSignature;
    }

    public LocalDate getSignatureDate() {
        return signatureDate;
    }

    public void setSignatureDate(LocalDate signatureDate) {
        this.signatureDate = signatureDate;
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
        return agreementNumber != null ? agreementNumber : "New PM Agreement";
    }
}
