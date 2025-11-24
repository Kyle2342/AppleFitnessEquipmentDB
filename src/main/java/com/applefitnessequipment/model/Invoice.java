package com.applefitnessequipment.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Invoice {
    private Integer invoiceId;
    private Integer clientId;
    private Integer billingLocationId;
    private Integer jobLocationId;
    private Integer preventiveMaintenanceAgreementId;
    private Integer equipmentQuoteId;
    private String invoiceNumber;
    private String quoteNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private String poNumber;
    private String terms;
    private String status;
    private BigDecimal subtotalAmount;
    private BigDecimal taxRatePercent;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal paymentsApplied;
    private BigDecimal balanceDue;  // Generated column in database
    private LocalDate paidDate;
    private BigDecimal returnedCheckFee;
    private BigDecimal interestPercent;
    private Integer interestStartDays;
    private Integer interestIntervalDays;
    private String fromCompanyName;
    private String fromStreetAddress;
    private String fromCity;
    private String fromState;
    private String fromZIPCode;
    private String fromPhone;
    private String fromFax;
    private String clientTypeSnapshot;
    private String clientCompanyNameSnapshot;
    private String clientFirstNameSnapshot;
    private String clientLastNameSnapshot;
    private String billToCompanyName;
    private String billToContactName;
    private String billToStreetAddress;
    private String billToBuildingName;
    private String billToSuite;
    private String billToRoomNumber;
    private String billToDepartment;
    private String billToCity;
    private String billToCounty;
    private String billToState;
    private String billToZIPCode;
    private String billToCountry;
    private String billToPhone;
    private String billToPONumber;
    private String jobAtCompanyName;
    private String jobAtContactName;
    private String jobAtStreetAddress;
    private String jobAtBuildingName;
    private String jobAtSuite;
    private String jobAtRoomNumber;
    private String jobAtDepartment;
    private String jobAtCity;
    private String jobAtCounty;
    private String jobAtState;
    private String jobAtZIPCode;
    private String jobAtCountry;
    private String jobAtPhone;
    private String jobAtPONumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Invoice() {}

    // Getters and Setters
    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
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

    public Integer getPreventiveMaintenanceAgreementId() {
        return preventiveMaintenanceAgreementId;
    }

    public void setPreventiveMaintenanceAgreementId(Integer preventiveMaintenanceAgreementId) {
        this.preventiveMaintenanceAgreementId = preventiveMaintenanceAgreementId;
    }

    public Integer getEquipmentQuoteId() {
        return equipmentQuoteId;
    }

    public void setEquipmentQuoteId(Integer equipmentQuoteId) {
        this.equipmentQuoteId = equipmentQuoteId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getQuoteNumber() {
        return quoteNumber;
    }

    public void setQuoteNumber(String quoteNumber) {
        this.quoteNumber = quoteNumber;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getSubtotalAmount() {
        return subtotalAmount;
    }

    public void setSubtotalAmount(BigDecimal subtotalAmount) {
        this.subtotalAmount = subtotalAmount;
    }

    public BigDecimal getTaxRatePercent() {
        return taxRatePercent;
    }

    public void setTaxRatePercent(BigDecimal taxRatePercent) {
        this.taxRatePercent = taxRatePercent;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPaymentsApplied() {
        return paymentsApplied;
    }

    public void setPaymentsApplied(BigDecimal paymentsApplied) {
        this.paymentsApplied = paymentsApplied;
    }

    public BigDecimal getBalanceDue() {
        return balanceDue;
    }

    public void setBalanceDue(BigDecimal balanceDue) {
        this.balanceDue = balanceDue;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }

    public BigDecimal getReturnedCheckFee() {
        return returnedCheckFee;
    }

    public void setReturnedCheckFee(BigDecimal returnedCheckFee) {
        this.returnedCheckFee = returnedCheckFee;
    }

    public BigDecimal getInterestPercent() {
        return interestPercent;
    }

    public void setInterestPercent(BigDecimal interestPercent) {
        this.interestPercent = interestPercent;
    }

    public Integer getInterestStartDays() {
        return interestStartDays;
    }

    public void setInterestStartDays(Integer interestStartDays) {
        this.interestStartDays = interestStartDays;
    }

    public Integer getInterestIntervalDays() {
        return interestIntervalDays;
    }

    public void setInterestIntervalDays(Integer interestIntervalDays) {
        this.interestIntervalDays = interestIntervalDays;
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

    public String getBillToCity() {
        return billToCity;
    }

    public void setBillToCity(String billToCity) {
        this.billToCity = billToCity;
    }

    public String getBillToCounty() {
        return billToCounty;
    }

    public void setBillToCounty(String billToCounty) {
        this.billToCounty = billToCounty;
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

    public String getJobAtCity() {
        return jobAtCity;
    }

    public void setJobAtCity(String jobAtCity) {
        this.jobAtCity = jobAtCity;
    }

    public String getJobAtCounty() {
        return jobAtCounty;
    }

    public void setJobAtCounty(String jobAtCounty) {
        this.jobAtCounty = jobAtCounty;
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

    public String getJobAtPhone() {
        return jobAtPhone;
    }

    public void setJobAtPhone(String jobAtPhone) {
        this.jobAtPhone = jobAtPhone;
    }

    public String getJobAtPONumber() {
        return jobAtPONumber;
    }

    public void setJobAtPONumber(String jobAtPONumber) {
        this.jobAtPONumber = jobAtPONumber;
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
        return invoiceNumber != null ? invoiceNumber : "New Invoice";
    }
}
