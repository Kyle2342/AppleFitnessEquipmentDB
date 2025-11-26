package com.applefitnessequipment.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Model class matching PreventiveMaintenanceAgreements table schema exactly.
 * @SCHEMA_SINGLE_SOURCE_OF_TRUTH: applefitnessequipmentdb_schema.sql
 */
public class PreventiveMaintenance {
    // PK
    private Integer preventiveMaintenanceAgreementId;

    // FKs
    private Integer clientId;
    private Integer clientBillingLocationId;
    private Integer clientJobLocationId;

    // Agreement Details
    private String agreementNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private String visitFrequency; // ENUM: 'Monthly', 'Quarterly', 'Semi-Annual', 'Annual'
    private String status; // ENUM: 'Draft', 'Sent', 'Expired', 'Active', 'Declined', 'Canceled', 'Completed'

    // Financial Terms
    private BigDecimal visitPrice;
    private BigDecimal taxRatePercent;
    private BigDecimal taxAmount; // GENERATED column
    private BigDecimal pricePerYear; // GENERATED column

    private Boolean clientSignatureBoolean;

    public PreventiveMaintenance() {
        this.taxRatePercent = new BigDecimal("6.00");
        this.clientSignatureBoolean = false;
    }

    // Getters and Setters
    public Integer getPreventiveMaintenanceAgreementId() {
        return preventiveMaintenanceAgreementId;
    }

    public void setPreventiveMaintenanceAgreementId(Integer preventiveMaintenanceAgreementId) {
        this.preventiveMaintenanceAgreementId = preventiveMaintenanceAgreementId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getClientBillingLocationId() {
        return clientBillingLocationId;
    }

    public void setClientBillingLocationId(Integer clientBillingLocationId) {
        this.clientBillingLocationId = clientBillingLocationId;
    }

    public Integer getClientJobLocationId() {
        return clientJobLocationId;
    }

    public void setClientJobLocationId(Integer clientJobLocationId) {
        this.clientJobLocationId = clientJobLocationId;
    }

    public String getAgreementNumber() {
        return agreementNumber;
    }

    public void setAgreementNumber(String agreementNumber) {
        this.agreementNumber = agreementNumber;
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

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getPricePerYear() {
        return pricePerYear;
    }

    public void setPricePerYear(BigDecimal pricePerYear) {
        this.pricePerYear = pricePerYear;
    }

    public Boolean getClientSignatureBoolean() {
        return clientSignatureBoolean;
    }

    public void setClientSignatureBoolean(Boolean clientSignatureBoolean) {
        this.clientSignatureBoolean = clientSignatureBoolean;
    }
}
