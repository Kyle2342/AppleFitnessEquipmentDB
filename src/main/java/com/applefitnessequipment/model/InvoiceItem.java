package com.applefitnessequipment.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvoiceItem {
    private Integer invoiceItemId;
    private Integer invoiceId;
    private Integer rowNumber;
    private String description;
    private BigDecimal qty;
    private BigDecimal rate;
    private BigDecimal totalAmount;  // Generated column (Qty * Rate)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public InvoiceItem() {}

    // Getters and Setters
    public Integer getInvoiceItemId() { return invoiceItemId; }
    public void setInvoiceItemId(Integer invoiceItemId) { this.invoiceItemId = invoiceItemId; }

    public Integer getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Integer invoiceId) { this.invoiceId = invoiceId; }

    public Integer getRowNumber() { return rowNumber; }
    public void setRowNumber(Integer rowNumber) { this.rowNumber = rowNumber; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getQty() { return qty; }
    public void setQty(BigDecimal qty) { this.qty = qty; }

    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
