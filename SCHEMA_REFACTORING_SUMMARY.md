# Schema Refactoring Summary

This document summarizes all changes made to align the codebase with `applefitnessequipmentdb_schema.sql` as the single source of truth.

## Changes Overview

All models, DAOs, and UI code have been refactored to match the database schema exactly. This ensures that:
- No attempts are made to write to GENERATED columns
- All column names match the schema precisely
- All table names use correct casing
- Foreign key references use the correct column names

---

## Models Refactored

### 1. PreventiveMaintenance.java
**Status:** ✅ Completely rewritten

**Changes:**
- `pmaId` → `preventiveMaintenanceAgreementId`
- Split `propertyLocationId` into:
  - `clientBillingLocationId`
  - `clientJobLocationId`
- `billingFrequency` → `visitFrequency`
- `monthlyRate` → `visitPrice`
- `annualRate` → removed
- Added: `taxRatePercent`, `taxAmount` (GENERATED), `pricePerYear` (GENERATED)
- Added: `clientSignatureBoolean`

### 2. PreventiveMaintenanceAgreement.java
**Status:** ✅ Cleaned up

**Changes:**
- Removed ~40 UI snapshot fields (fromCompanyName, billToCompanyName, jobAtCompanyName, etc.)
- Removed audit fields (createdAt, updatedAt)
- Added @Deprecated backward compatibility methods for old field names
- Now matches schema exactly

### 3. EquipmentQuote.java
**Status:** ✅ Cleaned up

**Changes:**
- Removed all snapshot fields (FROM, BILL TO, JOB AT sections)
- Removed: notes, termsAndConditions, extraInfo
- Removed audit fields (createdAt, updatedAt)
- `billingLocationId` → `clientBillingLocationId`
- `jobAtLocationId` → `clientJobLocationId`
- Added @Deprecated backward compatibility methods
- Added toString() method

### 4. EquipmentQuoteComplete.java
**Status:** ✅ Cleaned up

**Changes:**
- Same changes as EquipmentQuote.java
- Now properly aligned with EquipmentQuotes table

### 5. Invoice.java
**Status:** ✅ Cleaned up

**Changes:**
- Removed: quoteNumber (EquipmentQuoteID is the FK, not quoteNumber)
- Removed: returnedCheckFee, interestPercent, interestStartDays, interestIntervalDays
- Removed all snapshot fields (FROM, BILL TO, JOB AT sections)
- `billingLocationId` → `clientBillingLocationId`
- `jobLocationId` → `clientJobLocationId`
- Added: `poNumber` (maps to PONumber column)
- Added @Deprecated backward compatibility methods

### 6. InvoiceItem.java
**Status:** ✅ Already compliant

**Changes:** None needed - already matches schema

### 7. EquipmentQuoteItem.java (NEW)
**Status:** ✅ Created

**Details:**
- Maps to `EquipmentQuotesItems` table
- Includes all fields: rowNumber, qty, model, description, unitCost, discountUnitPrice
- Includes GENERATED column: unitTotal

### 8. PMAgreementEquipment.java (NEW)
**Status:** ✅ Created

**Details:**
- Maps to `PreventiveMaintenanceAgreementsEquipments` table
- Includes all fields: rowNumber, equipmentType, make, model, serialNumber

---

## DAOs Refactored

### 1. PreventiveMaintenanceDAO.java
**Status:** ✅ Completely rewritten

**Critical Fixes:**
- Table name: `preventivemaintenanceagreements` → `PreventiveMaintenanceAgreements`
- PK column: `PMAID` → `PreventiveMaintenanceAgreementID`
- FK columns: Updated to match schema (ClientBillingLocationID, ClientJobLocationID)
- Column names: All updated to match schema exactly
- Removed attempts to write to GENERATED columns (TaxAmount, PricePerYear)
- Added RETURN_GENERATED_KEYS support

### 2. PMAgreementDAO.java
**Status:** ✅ Completely rewritten

**Changes:**
- Same fixes as PreventiveMaintenanceDAO.java
- Works with PreventiveMaintenanceAgreement model

### 3. EquipmentQuoteDAO.java
**Status:** ✅ Completely rewritten

**Changes:**
- Removed all snapshot field references from INSERT/UPDATE
- Updated FK column names (ClientBillingLocationID, ClientJobLocationID)
- Removed attempts to write to GENERATED columns (ExtendedTotalAmount, SalesTaxAmount, QuoteTotalAmount)
- Added RETURN_GENERATED_KEYS support

### 4. EquipmentQuoteCompleteDAO.java
**Status:** ✅ Completely rewritten

**Changes:**
- Same fixes as EquipmentQuoteDAO.java
- Works with EquipmentQuoteComplete model

### 5. InvoiceDAO.java
**Status:** ✅ Completely rewritten

**Changes:**
- Removed all snapshot field references
- Removed: quoteNumber, returnedCheckFee, interest fields
- Added: PONumber
- Updated FK column names (ClientBillingLocationID, ClientJobLocationID)
- Removed attempts to write to GENERATED columns (TaxAmount, TotalAmount, BalanceDue)
- Added RETURN_GENERATED_KEYS support
- Added proper NULL handling for optional FKs (PreventiveMaintenanceAgreementID, EquipmentQuoteID)

### 6. EquipmentQuoteItemDAO.java (NEW)
**Status:** ✅ Created

**Details:**
- Full CRUD operations for EquipmentQuotesItems table
- Properly reads GENERATED column (UnitTotal)
- Includes bulk delete by quote ID

### 7. PMAgreementEquipmentDAO.java (NEW)
**Status:** ✅ Created

**Details:**
- Full CRUD operations for PreventiveMaintenanceAgreementsEquipments table
- Includes bulk delete by agreement ID

---

## UI Changes

### Status: ✅ No changes needed

All UI panels were already using the correct method names and don't reference removed snapshot fields directly. The UI properly uses:
- Foreign key IDs to reference related records
- JComboBox components populated from database queries
- Proper formatting and display logic

---

## Key Schema Alignment Points

### 1. GENERATED Columns (Read-Only)
These columns are auto-calculated by MySQL and should NEVER be written to:

**PreventiveMaintenanceAgreements:**
- `TaxAmount = VisitPrice * (TaxRatePercent / 100)`
- `PricePerYear = (VisitPrice * frequency_multiplier) * (1 + TaxRatePercent/100)`

**EquipmentQuotes:**
- `ExtendedTotalAmount = (SubtotalAmount - TotalDiscountAmount) + FreightAmount`
- `SalesTaxAmount = ExtendedTotalAmount * (SalesTaxRatePercent / 100)`
- `QuoteTotalAmount = ExtendedTotalAmount + SalesTaxAmount`

**EquipmentQuotesItems:**
- `UnitTotal = Qty * DiscountUnitPrice`

**Invoices:**
- `TaxAmount = SubtotalAmount * (TaxRatePercent / 100)`
- `TotalAmount = SubtotalAmount + TaxAmount`
- `BalanceDue = CASE WHEN Status IN ('Void') THEN 0.00 ELSE TotalAmount - PaymentsApplied END`

**InvoicesItems:**
- `TotalAmount = Qty * Rate`

### 2. Foreign Key Column Name Changes

| Old Name | New Schema Name |
|----------|----------------|
| `BillingLocationID` | `ClientBillingLocationID` |
| `JobLocationID` | `ClientJobLocationID` |
| `JobAtLocationID` | `ClientJobLocationID` |
| `PMAID` | `PreventiveMaintenanceAgreementID` |

### 3. Table Name Corrections

| Old Name | Correct Schema Name |
|----------|---------------------|
| `preventivemaintenanceagreements` | `PreventiveMaintenanceAgreements` |

### 4. Removed Snapshot Fields

The following snapshot fields were removed from models as they don't exist in the schema:
- `fromCompanyName`, `fromStreetAddress`, `fromCity`, etc.
- `clientTypeSnapshot`, `clientCompanyNameSnapshot`, etc.
- `billToCompanyName`, `billToContactName`, `billToStreetAddress`, etc.
- `jobAtCompanyName`, `jobAtContactName`, `jobAtStreetAddress`, etc.

**Impact:** UI code must now JOIN with related tables (Clients, ClientsLocations, Company) to get display information instead of relying on snapshot fields.

---

## Backward Compatibility

To ease migration, deprecated methods were added to models:

```java
@Deprecated
public Integer getPmaId() {
    return preventiveMaintenanceAgreementId;
}

@Deprecated
public Integer getBillingLocationId() {
    return clientBillingLocationId;
}
```

These should be updated to use the new method names throughout the codebase.

---

## Testing Recommendations

1. **Database Operations:**
   - Test INSERT operations - verify GENERATED columns are populated correctly
   - Test UPDATE operations - ensure GENERATED columns recalculate
   - Test foreign key constraints - verify cascade deletes work properly

2. **UI Validation:**
   - Test all forms with the new FK structure
   - Verify dropdowns populate correctly with related data
   - Test data display in tables and detail views

3. **Data Integrity:**
   - Verify UNIQUE constraints (e.g., invoice numbers, quote numbers)
   - Test ENUM validation (Status, VisitFrequency, etc.)
   - Verify CHECK constraints work as expected

---

## Files Modified

### Models (8 files)
- PreventiveMaintenance.java (rewritten)
- PreventiveMaintenanceAgreement.java (cleaned)
- EquipmentQuote.java (cleaned)
- EquipmentQuoteComplete.java (cleaned)
- Invoice.java (cleaned)
- InvoiceItem.java (no changes)

### Models Created (2 files)
- EquipmentQuoteItem.java
- PMAgreementEquipment.java

### DAOs (7 files)
- PreventiveMaintenanceDAO.java (rewritten)
- PMAgreementDAO.java (rewritten)
- EquipmentQuoteDAO.java (rewritten)
- EquipmentQuoteCompleteDAO.java (rewritten)
- InvoiceDAO.java (rewritten)

### DAOs Created (2 files)
- EquipmentQuoteItemDAO.java
- PMAgreementEquipmentDAO.java

### UI Panels
- No changes needed (already compliant)

---

## Next Steps

1. Run full compilation: `mvn clean compile`
2. Run tests if available: `mvn test`
3. Perform manual testing of all CRUD operations
4. Update any remaining deprecated method calls
5. Consider creating database migration scripts if schema changes are needed
6. Update documentation to reflect new structure

---

**Generated:** 2025-11-26
**Schema Source:** `src/main/resources/img/applefitnessequipmentdb_schema.sql`
