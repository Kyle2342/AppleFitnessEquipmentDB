package com.applefitnessequipment.model;

/**
 * Model class matching PreventiveMaintenanceAgreementsEquipments table schema exactly.
 * @SCHEMA_SINGLE_SOURCE_OF_TRUTH: applefitnessequipmentdb_schema.sql
 */
public class PMAgreementEquipment {
    // PK
    private Integer preventiveMaintenanceAgreementEquipmentId;

    // FK
    private Integer preventiveMaintenanceAgreementId;

    private Integer rowNumber;
    private String equipmentType;
    private String make;
    private String model;
    private String serialNumber;

    public PMAgreementEquipment() {}

    // Getters and Setters
    public Integer getPreventiveMaintenanceAgreementEquipmentId() {
        return preventiveMaintenanceAgreementEquipmentId;
    }

    public void setPreventiveMaintenanceAgreementEquipmentId(Integer preventiveMaintenanceAgreementEquipmentId) {
        this.preventiveMaintenanceAgreementEquipmentId = preventiveMaintenanceAgreementEquipmentId;
    }

    public Integer getPreventiveMaintenanceAgreementId() {
        return preventiveMaintenanceAgreementId;
    }

    public void setPreventiveMaintenanceAgreementId(Integer preventiveMaintenanceAgreementId) {
        this.preventiveMaintenanceAgreementId = preventiveMaintenanceAgreementId;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
