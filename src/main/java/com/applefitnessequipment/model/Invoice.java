package com.applefitnessequipment.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Model class matching Invoices table schema exactly.
 * @SCHEMA_SINGLE_SOURCE_OF_TRUTH: applefitnessequipmentdb_schema.sql
 */
public class Invoice {
    // PK
    private Integer invoiceId;

    // FKs
    private Integer clientId;
    private Integer clientBillingLocationId;
    private Integer clientJobLocationId;
    private Integer preventiveMaintenanceAgreementId;
    private Integer equipmentQuoteId;

    // Core invoice data
    private LocalDate invoiceDate;
    private String invoiceNumber;
    private String poNumber;
    private String terms;
    private LocalDate dueDate;
    private String status; // ENUM: 'Draft', 'Open', 'Paid', 'Overdue', 'Void'

    // Money fields
    private BigDecimal subtotalAmount;
    private BigDecimal taxRatePercent;
    private BigDecimal taxAmount; // GENERATED column
    private BigDecimal totalAmount; // GENERATED column
    private BigDecimal paymentsApplied;
    private BigDecimal balanceDue; // GENERATED column
    private LocalDate paidDate;

    public Invoice() {
        this.terms = "Net 30";
        this.taxRatePercent = new BigDecimal("6.00");
        this.paymentsApplied = BigDecimal.ZERO;
        this.status = "Draft";
    }

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

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
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

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
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

    // Backward compatibility getters (deprecated - use new names)
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
