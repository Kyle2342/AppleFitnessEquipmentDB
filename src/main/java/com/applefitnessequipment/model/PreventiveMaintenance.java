package com.applefitnessequipment.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PreventiveMaintenance {
    private Integer pmaId;
    private Integer clientId;
    private Integer propertyLocationId;
    private String agreementNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private String billingFrequency;
    private String status;
    private BigDecimal monthlyRate;
    private BigDecimal annualRate;

    public PreventiveMaintenance() {}

    // Getters and Setters
    public Integer getPmaId() { return pmaId; }
    public void setPmaId(Integer pmaId) { this.pmaId = pmaId; }

    public Integer getClientId() { return clientId; }
    public void setClientId(Integer clientId) { this.clientId = clientId; }

    public Integer getPropertyLocationId() { return propertyLocationId; }
    public void setPropertyLocationId(Integer propertyLocationId) { this.propertyLocationId = propertyLocationId; }

    public String getAgreementNumber() { return agreementNumber; }
    public void setAgreementNumber(String agreementNumber) { this.agreementNumber = agreementNumber; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getBillingFrequency() { return billingFrequency; }
    public void setBillingFrequency(String billingFrequency) { this.billingFrequency = billingFrequency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getMonthlyRate() { return monthlyRate; }
    public void setMonthlyRate(BigDecimal monthlyRate) { this.monthlyRate = monthlyRate; }

    public BigDecimal getAnnualRate() { return annualRate; }
    public void setAnnualRate(BigDecimal annualRate) { this.annualRate = annualRate; }
}
