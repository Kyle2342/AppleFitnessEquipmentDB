package com.applefitnessequipment.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EquipmentQuote {
    private Integer quoteId;
    private Integer clientId;
    private String quoteNumber;
    private LocalDate quoteDate;
    private String contactName;
    private String salespersonName;
    private String status;
    private BigDecimal subtotalAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;

    public EquipmentQuote() {}

    public Integer getQuoteId() { return quoteId; }
    public void setQuoteId(Integer quoteId) { this.quoteId = quoteId; }

    public Integer getClientId() { return clientId; }
    public void setClientId(Integer clientId) { this.clientId = clientId; }

    public String getQuoteNumber() { return quoteNumber; }
    public void setQuoteNumber(String quoteNumber) { this.quoteNumber = quoteNumber; }

    public LocalDate getQuoteDate() { return quoteDate; }
    public void setQuoteDate(LocalDate quoteDate) { this.quoteDate = quoteDate; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getSalespersonName() { return salespersonName; }
    public void setSalespersonName(String salespersonName) { this.salespersonName = salespersonName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getSubtotalAmount() { return subtotalAmount; }
    public void setSubtotalAmount(BigDecimal subtotalAmount) { this.subtotalAmount = subtotalAmount; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
