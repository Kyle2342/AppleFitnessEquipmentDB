package com.applefitnessequipment.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Model class matching EquipmentQuotes table schema exactly.
 * @SCHEMA_SINGLE_SOURCE_OF_TRUTH: applefitnessequipmentdb_schema.sql
 *
 * NOTE: This class should be kept in sync with or merged with EquipmentQuote.java
 */
public class EquipmentQuoteComplete {
    // PK
    private Integer equipmentQuoteId;

    // FKs
    private Integer clientId;
    private Integer clientBillingLocationId;
    private Integer clientJobLocationId;

    // Core quote data
    private LocalDate quoteDate;
    private String quoteNumber;
    private String status; // ENUM: 'Draft', 'Sent', 'Expired', 'Active', 'Declined', 'Canceled', 'Completed'

    // Sales / logistics fields
    private String contactName;
    private String salespersonName;
    private String shipVia;
    private String freightTerms;
    private String paymentTerms;
    private String fobLocation;

    // Money fields
    private BigDecimal subtotalAmount;
    private BigDecimal totalDiscountAmount;
    private BigDecimal freightAmount;
    private BigDecimal extendedTotalAmount; // GENERATED column
    private BigDecimal salesTaxRatePercent;
    private BigDecimal salesTaxAmount; // GENERATED column
    private BigDecimal quoteTotalAmount; // GENERATED column

    private Boolean clientSignatureBoolean;

    public EquipmentQuoteComplete() {
        this.salespersonName = "Greg Bartram";
        this.shipVia = "AFE Truck/Trailer";
        this.freightTerms = "Ppd & Add";
        this.paymentTerms = "See Notes";
        this.fobLocation = "Truck Curbside";
        this.totalDiscountAmount = BigDecimal.ZERO;
        this.freightAmount = BigDecimal.ZERO;
        this.salesTaxRatePercent = new BigDecimal("6.00");
        this.status = "Draft";
        this.clientSignatureBoolean = false;
    }

    // Getters and Setters
    public Integer getEquipmentQuoteId() {
        return equipmentQuoteId;
    }

    public void setEquipmentQuoteId(Integer equipmentQuoteId) {
        this.equipmentQuoteId = equipmentQuoteId;
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

    public LocalDate getQuoteDate() {
        return quoteDate;
    }

    public void setQuoteDate(LocalDate quoteDate) {
        this.quoteDate = quoteDate;
    }

    public String getQuoteNumber() {
        return quoteNumber;
    }

    public void setQuoteNumber(String quoteNumber) {
        this.quoteNumber = quoteNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getSalespersonName() {
        return salespersonName;
    }

    public void setSalespersonName(String salespersonName) {
        this.salespersonName = salespersonName;
    }

    public String getShipVia() {
        return shipVia;
    }

    public void setShipVia(String shipVia) {
        this.shipVia = shipVia;
    }

    public String getFreightTerms() {
        return freightTerms;
    }

    public void setFreightTerms(String freightTerms) {
        this.freightTerms = freightTerms;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public String getFobLocation() {
        return fobLocation;
    }

    public void setFobLocation(String fobLocation) {
        this.fobLocation = fobLocation;
    }

    public BigDecimal getSubtotalAmount() {
        return subtotalAmount;
    }

    public void setSubtotalAmount(BigDecimal subtotalAmount) {
        this.subtotalAmount = subtotalAmount;
    }

    public BigDecimal getTotalDiscountAmount() {
        return totalDiscountAmount;
    }

    public void setTotalDiscountAmount(BigDecimal totalDiscountAmount) {
        this.totalDiscountAmount = totalDiscountAmount;
    }

    public BigDecimal getFreightAmount() {
        return freightAmount;
    }

    public void setFreightAmount(BigDecimal freightAmount) {
        this.freightAmount = freightAmount;
    }

    public BigDecimal getExtendedTotalAmount() {
        return extendedTotalAmount;
    }

    public void setExtendedTotalAmount(BigDecimal extendedTotalAmount) {
        this.extendedTotalAmount = extendedTotalAmount;
    }

    public BigDecimal getSalesTaxRatePercent() {
        return salesTaxRatePercent;
    }

    public void setSalesTaxRatePercent(BigDecimal salesTaxRatePercent) {
        this.salesTaxRatePercent = salesTaxRatePercent;
    }

    public BigDecimal getSalesTaxAmount() {
        return salesTaxAmount;
    }

    public void setSalesTaxAmount(BigDecimal salesTaxAmount) {
        this.salesTaxAmount = salesTaxAmount;
    }

    public BigDecimal getQuoteTotalAmount() {
        return quoteTotalAmount;
    }

    public void setQuoteTotalAmount(BigDecimal quoteTotalAmount) {
        this.quoteTotalAmount = quoteTotalAmount;
    }

    public Boolean getClientSignatureBoolean() {
        return clientSignatureBoolean;
    }

    public void setClientSignatureBoolean(Boolean clientSignatureBoolean) {
        this.clientSignatureBoolean = clientSignatureBoolean;
    }

    // Backward compatibility getters (deprecated - use new names)
    @Deprecated
    public Integer getQuoteId() {
        return equipmentQuoteId;
    }

    @Deprecated
    public void setQuoteId(Integer quoteId) {
        this.equipmentQuoteId = quoteId;
    }

    @Deprecated
    public Integer getBillingLocationId() {
        return clientBillingLocationId;
    }

    @Deprecated
    public void setBillingLocationId(Integer billingLocationId) {
        this.clientBillingLocationId = billingLocationId;
    }

    @Deprecated
    public Integer getJobLocationId() {
        return clientJobLocationId;
    }

    @Deprecated
    public void setJobLocationId(Integer jobLocationId) {
        this.clientJobLocationId = jobLocationId;
    }
}
