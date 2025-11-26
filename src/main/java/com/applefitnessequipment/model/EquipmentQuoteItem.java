package com.applefitnessequipment.model;

import java.math.BigDecimal;

/**
 * Model class matching EquipmentQuotesItems table schema exactly.
 * @SCHEMA_SINGLE_SOURCE_OF_TRUTH: applefitnessequipmentdb_schema.sql
 */
public class EquipmentQuoteItem {
    // PK
    private Integer equipmentQuoteItemId;

    // FK
    private Integer equipmentQuoteId;

    private Integer rowNumber;
    private BigDecimal qty;
    private String model;
    private String description;
    private BigDecimal unitCost;
    private BigDecimal discountUnitPrice;
    private BigDecimal unitTotal; // GENERATED column (Qty * DiscountUnitPrice)

    public EquipmentQuoteItem() {
        this.qty = BigDecimal.ONE;
    }

    // Getters and Setters
    public Integer getEquipmentQuoteItemId() {
        return equipmentQuoteItemId;
    }

    public void setEquipmentQuoteItemId(Integer equipmentQuoteItemId) {
        this.equipmentQuoteItemId = equipmentQuoteItemId;
    }

    public Integer getEquipmentQuoteId() {
        return equipmentQuoteId;
    }

    public void setEquipmentQuoteId(Integer equipmentQuoteId) {
        this.equipmentQuoteId = equipmentQuoteId;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public BigDecimal getDiscountUnitPrice() {
        return discountUnitPrice;
    }

    public void setDiscountUnitPrice(BigDecimal discountUnitPrice) {
        this.discountUnitPrice = discountUnitPrice;
    }

    public BigDecimal getUnitTotal() {
        return unitTotal;
    }

    public void setUnitTotal(BigDecimal unitTotal) {
        this.unitTotal = unitTotal;
    }
}
