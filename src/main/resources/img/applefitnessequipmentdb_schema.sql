-- @SCHEMA_SINGLE_SOURCE_OF_TRUTH
-- This file defines the canonical MySQL schema for applefitnessequipmentdb.
-- All DAOs, models, and UI code must match this schema exactly.

/* ============================================================
   TABLE 1: Employees
============================================================ */
CREATE TABLE Employees (
	-- PK
    EmployeeID          	INT AUTO_INCREMENT PRIMARY KEY,				-- auto generated dont worry about and dont display on ui

    -- Personal info
    FirstName           	VARCHAR(50) NOT NULL,						-- display to take user input(forced to enter, typeable text field)
    LastName            	VARCHAR(50) NOT NULL,						-- display to take user input(forced to enter, typeable text field)
    DateOfBirth         	DATE NULL,									-- display to take user input(not forced to enter, typeable text field, format on ui as mm/dd/yyyy)
    Gender              	ENUM('Male','Female') NULL,					-- display to take user input(not forced to enter, non-typeable preset text field with dropdown)
    Email       			VARCHAR(100) NULL,							-- display to take user input(not forced to enter, typeable text field)
    PhoneNumber         	VARCHAR(20)  NULL,							-- display to take user input(not forced to enter, typeable text field, format on ui as (###) ###-####)

    -- Address
    BuildingName        VARCHAR(100) NULL,      						-- display to take user input(not forced to enter, typeable text field)
    SuiteNumber         VARCHAR(50)  NULL,      						-- display to take user input(not forced to enter, typeable text field)
    StreetAddress       VARCHAR(100) NOT NULL,  						-- display to take user input(forced to enter, typeable text field)
    City                VARCHAR(50)  NOT NULL,							-- display to take user input(forced to enter, typeable text field)
    State               CHAR(2)  NOT NULL DEFAULT 'PA',					-- display to take user input(forced to enter, typeable text field, default pre-filled as PA)
    ZIPCode             VARCHAR(20)  NOT NULL,							-- display to take user input(forced to enter, typeable text field)
    Country             VARCHAR(50)  NOT NULL DEFAULT 'USA',			-- display to take user input(forced to enter, typeable text field, default pre-filled as USA)

    -- Employment details
    PositionTitle       	VARCHAR(100) NOT NULL,     					-- display to take user input(forced to enter, typeable text field)
    EmploymentType      	ENUM('Full-Time','Part-Time') NOT NULL,		-- display to take user input(forced to enter, non-typeable preset text field with dropdown)
	PayType 				ENUM('Hourly','Salary') NOT NULL,			-- display to take user input(forced to enter, non-typeable preset text field with dropdown)
	PayRate 				DECIMAL(10,2) NOT NULL,  					-- display to take user input(forced to enter, typeable text field)
    HireDate            	DATE NOT NULL,								-- display to take user input(forced to enter, typeable text field, format on ui as mm/dd/yyyy)
    TerminationDate     	DATE NULL,									-- display to take user input(not forced to enter)
    ActiveStatus        	BOOLEAN NOT NULL DEFAULT 1,					-- display to take user input(forced to enter, non-typeable preset text field with dropdown, default pre-filled as true)
    
    
    -- Named checks
    CONSTRAINT chk_employees_payrate_nonnegative						-- ui check also
        CHECK (PayRate >= 0),
        
    CONSTRAINT chk_employees_valid_termination_date
        CHECK (TerminationDate IS NULL OR TerminationDate >= HireDate)	-- ui check also
);

/* ============================================================
   TABLE 2: Employee Time Logs
============================================================ */
CREATE TABLE EmployeesTimeLogs (
	-- PK
    EmployeeTimeLogID	INT AUTO_INCREMENT PRIMARY KEY,										-- auto generated dont worry about and dont display on ui
	
    -- FK
    EmployeeID        	INT NOT NULL,  														-- display to take user input(forced to enter, non-typeable preset text field with dropdown, display options format as FirstName MiddleInitial. LastName)
    
	-- Date information
	DayOfWeek         	ENUM('Monday','Tuesday','Wednesday','Thursday','Friday') NOT NULL,	-- display to take user input(forced to enter, non-typeable preset text field with dropdown)
    LogDate           	DATE NOT NULL, 														-- display to take user input(forced to enter, typeable text field, default pre-fill on ui as todays date, format on ui as mm/dd/yyyy)

    -- Clock-in / Clock-out
    TimeIn      		TIME NOT NULL,														-- display to take user input(forced to enter, typeable text field with proper formatting)
    TimeOut     		TIME NOT NULL,														-- display to take user input(forced to enter, typeable text field with proper formatting)

    -- Total Time
    TotalHours DECIMAL(5,2) 																-- auto generated, so only worry about displaying it and make sure the ui follows the rules
	AS (
			(TIME_TO_SEC(TimeOut) - TIME_TO_SEC(TimeIn)) / 3600
	   ) STORED,
        
	-- Miles traveled
    Miles             DECIMAL(5,2) NULL,													-- display to take user input(not forced to enter, typeable text field)
    
    -- Named checks
    CONSTRAINT chk_hours_nonnegative 														-- ui check also
        CHECK (TotalHours >= 0),

    CONSTRAINT chk_miles_nonnegative														-- ui check also
        CHECK (Miles IS NULL OR Miles >= 0),												

    CONSTRAINT chk_timeout_after_timein														-- ui check also
        CHECK (TimeOut > TimeIn),

    CONSTRAINT chk_weekday_matches_logdate													-- ui check also
        CHECK (DAYNAME(LogDate) = DayOfWeek),
    
    -- Unique checks
    UNIQUE KEY uq_employee_logdate (EmployeeID, LogDate),									-- ui check also

    -- Relationship constraint
    CONSTRAINT fk_timelog_employee FOREIGN KEY (EmployeeID)
        REFERENCES Employees(EmployeeID)
			ON DELETE CASCADE,      														-- if an employee is deleted, automatically delete their time logs

    -- Index
    INDEX idx_timelogs_employeeid (EmployeeID)
);

/* ============================================================
   TABLE 3: Clients
============================================================ */
CREATE TABLE Clients (
	-- PK
    ClientID        INT AUTO_INCREMENT PRIMARY KEY,							-- auto generated dont worry about and dont display on ui

    -- Type of client
    ClientType      ENUM('Individual','Business') NOT NULL,					-- display to take user input(forced to enter, non-typeable preset text field with dropdown)

    -- For individual and company clients
    FirstName       VARCHAR(50)  NULL,										-- display to take user input(not forced to enter, typeable text field)
    LastName        VARCHAR(50)  NULL,										-- display to take user input(not forced to enter, typeable text field)

    -- For business clients only
    CompanyName     VARCHAR(150) NULL,										-- display to take user input(not forced to enter, typeable text field)

    -- Contact info
    PhoneNumber     VARCHAR(20)  NULL,										-- display to take user input(not forced to enter, typeable text field, format on ui as (###) ###-####)
    Email           VARCHAR(100) NULL,										-- display to take user input(not forced to enter, typeable text field)
	
    -- Notes
    Notes          	TEXT NULL,												-- display to take user input(not forced to enter, typeable text field)
    
    -- Named check
    CONSTRAINT chk_client_name_requirements	
		CHECK ((ClientType = 'Business'   AND CompanyName IS NOT NULL)		-- ui check also
			OR (ClientType = 'Individual' AND FirstName IS NOT NULL AND LastName IS NOT NULL))	-- business clients can also have a FirstName and LastName
);

/* ============================================================
   TABLE 4: Client Locations
============================================================ */
CREATE TABLE ClientsLocations (
	-- PK
    ClientLocationID   INT AUTO_INCREMENT PRIMARY KEY,			-- auto generated dont worry about and dont display on ui
    
	-- FK
    ClientID           INT NOT NULL, 							-- display to take user input(forced to enter, typeable text field with dropdown, display options format as CompanyName - FirstName LastName)
    
    -- Type of client location
    LocationType       ENUM('Billing','Job') NOT NULL,			-- display to take user input(forced to enter, non-typeable preset text field with dropdown)
	
	-- Refinements to the street location
    BuildingName     	VARCHAR(100) NULL,       				-- display to take user input(not forced to enter, typeable text field)
    RoomNumber        	VARCHAR(50)  NULL,       				-- display to take user input(not forced to enter, typeable text field)
    
	-- Street Location
    StreetAddress     	VARCHAR(150) NOT NULL,   				-- display to take user input(forced to enter, typeable text field)
    City      			VARCHAR(50)  NOT NULL,					-- display to take user input(forced to enter, typeable text field)
    State              	CHAR(2)  	 NOT NULL DEFAULT 'PA',		-- display to take user input(forced to enter, typeable text field, default pre-filled as PA)
    ZIPCode            	VARCHAR(20)  NOT NULL,					-- display to take user input(forced to enter, typeable text field)
    Country            	VARCHAR(50)  NOT NULL DEFAULT 'USA',	-- display to take user input(forced to enter, typeable text field, default pre-filled as USA)

    -- Location contact information
    Phone              	VARCHAR(20)  NULL,						-- display to take user input(not forced to enter, typeable text field, format on ui as (###) ###-####)
	Fax              	VARCHAR(20)  NULL,						-- display to take user input(not forced to enter, typeable text field, format on ui as (###) ###-####)
    Email              	VARCHAR(100) NULL,						-- display to take user input(not forced to enter, typeable text field)

	-- Relationship constraint
    CONSTRAINT fk_clientlocation_client
        FOREIGN KEY (ClientID) REFERENCES Clients(ClientID)
			ON DELETE CASCADE,									-- if a client is deleted, automatically delete their locations
            
	-- Index
    INDEX idx_clientlocations_clientid (ClientID),
    INDEX idx_clientlocations_clientid_type (ClientID, LocationType)
);

/* ============================================================
   TABLE 5: Company
============================================================ */
CREATE TABLE Company (
	-- PK
    CompanyID         	 TINYINT UNSIGNED NOT NULL PRIMARY KEY DEFAULT 1,					-- auto generated, fixed as 1, don't show or edit on UI
    
    -- Core company identity
    CompanyName         VARCHAR(150) NOT NULL DEFAULT 'Apple Fitness Equipment',   			-- display to take user input(forced to enter, typeable text field, default pre-filled as Apple Fitness Equipment)
    StreetAddress       VARCHAR(100) NOT NULL DEFAULT '1412 Majestic View Dr.',    			-- display to take user input(forced to enter, typeable text field, default pre-filled as 1412 Majestic View Dr.)
    City                VARCHAR(50)  NOT NULL DEFAULT 'State College',             			-- display to take user input(forced to enter, typeable text field, default pre-filled as State College)
    County              VARCHAR(50)  NOT NULL DEFAULT 'Centre',                    			-- display to take user input(forced to enter, typeable text field, default pre-filled as Centre)
    State               CHAR(2)  	 NOT NULL DEFAULT 'PA',                        			-- display to take user input(forced to enter, typeable text field, default pre-filled as PA)
    ZIPCode             VARCHAR(20)  NOT NULL DEFAULT '16801',                     			-- display to take user input(forced to enter, typeable text field, default pre-filled as 16801)
    Country             VARCHAR(50)  NOT NULL DEFAULT 'USA',                       			-- display to take user input(forced to enter, typeable text field, default pre-filled as USA)

    -- Contact info
    Phone               VARCHAR(45)  NOT NULL DEFAULT '8148262922',                			-- display to take user input(forced to enter, typeable text field, default pre-filled as 8148262922, format on ui as (###) ###-####)
    Fax                 VARCHAR(45)  NOT NULL DEFAULT '8148262933',                			-- display to take user input(forced to enter, typeable text field, default pre-filled as 8148262933, format on ui as (###) ###-####)
    Email               VARCHAR(100) NOT NULL DEFAULT 'gbartram90@gmail.com',      			-- display to take user input(forced to enter, typeable text field, default pre-filled as gbartram90@gmail.com)
    WebsiteURL          VARCHAR(150) NOT NULL DEFAULT 'https://applefitnessequipment.com/',	-- display to take user input(forced to enter, typeable text field, default pre-filled as https://applefitnessequipment.com/)

	-- Named check
	CONSTRAINT chk_single_company CHECK (CompanyID = 1)										-- prevents inserting a second company record
);

/* ============================================================
   TABLE 6: Preventive Maintenance Agreements
============================================================ */
CREATE TABLE PreventiveMaintenanceAgreements (
	-- PK
    PreventiveMaintenanceAgreementID	INT AUTO_INCREMENT PRIMARY KEY,

    -- FK
    ClientID              				INT NOT NULL,
    ClientBillingLocationID     		INT NOT NULL,
    ClientJobLocationID         		INT NOT NULL,
    
    -- Agreement Details
    AgreementNumber       VARCHAR(50) NOT NULL UNIQUE,
    StartDate             DATE NOT NULL,
    EndDate               DATE NOT NULL,
    VisitFrequency        ENUM('Monthly', 'Quarterly', 'Semi-Annual', 'Annual') 	NOT NULL,
    Status                ENUM('Draft', 'Sent', 'Expired', 'Active', 'Declined', 'Canceled', 'Completed') 	NOT NULL DEFAULT 'Draft',

    -- Financial Terms
    VisitPrice            DECIMAL(10,2) NOT NULL,
    TaxRatePercent        DECIMAL(5,2) 	NOT NULL DEFAULT 6.00,
    TaxAmount 			  DECIMAL(10,2) GENERATED ALWAYS AS (VisitPrice * (TaxRatePercent / 100)) STORED,
    PricePerYear          DECIMAL(10,2) GENERATED ALWAYS AS ((VisitPrice * 
                               CASE 
                                    WHEN VisitFrequency='Monthly' THEN 12
                                    WHEN VisitFrequency='Quarterly' THEN 4
                                    WHEN VisitFrequency='Semi-Annual' THEN 2
                                    WHEN VisitFrequency='Annual' THEN 1
                               END) * (1 + (TaxRatePercent / 100))) STORED,

	ClientSignatureBoolean 			BOOLEAN NOT NULL DEFAULT 0,

	CONSTRAINT fk_pma_client FOREIGN KEY (ClientID)
		REFERENCES Clients(ClientID)
        ON DELETE RESTRICT,

	CONSTRAINT fk_pma_bill_location FOREIGN KEY (ClientBillingLocationID)
		REFERENCES ClientsLocations(ClientLocationID)
        ON DELETE RESTRICT,

	CONSTRAINT fk_pma_job_location FOREIGN KEY (ClientJobLocationID)
		REFERENCES ClientsLocations(ClientLocationID)
        ON DELETE RESTRICT
);

/* ============================================================
   TABLE 7: PM Agreement Equipment List
============================================================ */
CREATE TABLE PreventiveMaintenanceAgreementsEquipments (
	-- PK
    PreventiveMaintenanceAgreementEquipmentID INT AUTO_INCREMENT PRIMARY KEY,
    
    -- FK
    PreventiveMaintenanceAgreementID 	INT NOT NULL,  

	RowNumber        INT NOT NULL,      
    EquipmentType    VARCHAR(100) NOT NULL,
    Make             VARCHAR(100) NOT NULL,
    Model            VARCHAR(100) NOT NULL,
    SerialNumber     VARCHAR(100) NOT NULL,

	UNIQUE (PreventiveMaintenanceAgreementID, RowNumber),

    CONSTRAINT fk_equipment_agreement FOREIGN KEY (PreventiveMaintenanceAgreementID)
        REFERENCES PreventiveMaintenanceAgreements(PreventiveMaintenanceAgreementID)
        ON DELETE CASCADE
        
);

/* ============================================================
   TABLE 8: Equipment Quotes
============================================================ */
CREATE TABLE EquipmentQuotes (
    -- PK
    EquipmentQuoteID		INT AUTO_INCREMENT PRIMARY KEY,	-- auto generated dont worry about and dont display on ui

    -- FK
    ClientID             	INT NOT NULL,             					-- display to take user input(forced to enter, typeable text field with dropdown, display options format as CompanyName - FirstName LastName)
    ClientBillingLocationID	INT NOT NULL,             					-- display to take user input(forced to enter, typeable text field with dropdown with only selectable clients billing locations, display options format as StreetAddress, City, State ZIPCode, Country)
    ClientJobLocationID 	INT NOT NULL,             					-- display to take user input(forced to enter, typeable text field with dropdown with only selectable clients job locations, display options format as StreetAddress, City, State ZIPCode, Country)
    
	-- Core quote data
	QuoteDate             DATE        NOT NULL,
    QuoteNumber           VARCHAR(45) NOT NULL UNIQUE,   
    Status                ENUM('Draft', 'Sent', 'Expired', 'Active', 'Declined', 'Canceled', 'Completed') NOT NULL DEFAULT 'Draft',

    -- Sales / logistics fields
	ContactName      	  VARCHAR(150) NOT NULL,      
    SalespersonName       VARCHAR(150) NOT NULL DEFAULT 'Greg Bartram',      	
    ShipVia				  VARCHAR(150) NOT NULL DEFAULT 'AFE Truck/Trailer',    
	FreightTerms          VARCHAR(150) NOT NULL DEFAULT 'Ppd & Add',      		
    PaymentTerms          VARCHAR(150) NOT NULL DEFAULT 'See Notes',  			
	FOBLocation           VARCHAR(150) NOT NULL DEFAULT 'Truck Curbside',       

    -- Money fields
	SubtotalAmount        DECIMAL(10,2) NOT NULL,
	TotalDiscountAmount   DECIMAL(10,2) NOT NULL DEFAULT 0.00,
	FreightAmount         DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    ExtendedTotalAmount   DECIMAL(10,2)
        GENERATED ALWAYS AS (
            (SubtotalAmount - TotalDiscountAmount) + FreightAmount
        ) STORED, 
    SalesTaxRatePercent   DECIMAL(5,2)  NOT NULL DEFAULT 6.00,
    SalesTaxAmount        DECIMAL(10,2)
        GENERATED ALWAYS AS (
            ROUND(ExtendedTotalAmount * (SalesTaxRatePercent / 100),2)
        ) STORED,
    QuoteTotalAmount      DECIMAL(10,2)
        GENERATED ALWAYS AS (
            ExtendedTotalAmount + SalesTaxAmount
        ) STORED,
		
	ClientSignatureBoolean BOOLEAN NOT NULL DEFAULT 0,

    CONSTRAINT fk_equipmentquotes_client
        FOREIGN KEY (ClientID)       REFERENCES Clients(ClientID)
        ON DELETE RESTRICT,
        
    CONSTRAINT fk_equipmentquotes_bill_location
        FOREIGN KEY (ClientBillingLocationID) REFERENCES ClientsLocations(ClientLocationID)
        ON DELETE RESTRICT,

    CONSTRAINT fk_equipmentquotes_job_location
        FOREIGN KEY (ClientJobLocationID)   REFERENCES ClientsLocations(ClientLocationID)
        ON DELETE RESTRICT
);

/* ============================================================
   TABLE 9: Equipment Quote Line Items
============================================================ */
CREATE TABLE EquipmentQuotesItems (
	-- PK
    EquipmentQuoteItemID 	 INT AUTO_INCREMENT PRIMARY KEY,
    
    -- FK
    EquipmentQuoteID         INT NOT NULL,

    RowNumber                INT 			NOT NULL,   -- row order on the form (1,2,3,...)
    Qty                      DECIMAL(10,2) 	NOT NULL,  	-- "Qty"
    Model                    VARCHAR(50) 	NULL,       -- "Model"
    Description              VARCHAR(255) 	NOT NULL,   -- "Description"
    UnitCost                 DECIMAL(10,2) 	NOT NULL,   -- "Unit Cost" (list/original)
    DiscountUnitPrice        DECIMAL(10,2) 	NOT NULL,   -- "Disc Unit Price" (what customer pays)
	UnitTotal DECIMAL(10,2)  GENERATED ALWAYS AS (Qty * DiscountUnitPrice) STORED,
    
    UNIQUE (EquipmentQuoteID, RowNumber),

    CONSTRAINT fk_eq_quote
        FOREIGN KEY (EquipmentQuoteID) REFERENCES EquipmentQuotes(EquipmentQuoteID)
		ON DELETE CASCADE
);

/* ============================================================
   TABLE 10: Invoices
============================================================ */
CREATE TABLE Invoices (
    -- PK
    InvoiceID            				INT AUTO_INCREMENT PRIMARY KEY,				-- auto generated dont worry about and dont display on ui

    -- FK
    ClientID             	   			INT NOT NULL,             					-- display to take user input(forced to enter, typeable text field with dropdown, display options format as CompanyName - FirstName LastName)
    ClientBillingLocationID    			INT NOT NULL,             					-- display to take user input(forced to enter, typeable text field with dropdown with only selectable clients billing locations, display options format as StreetAddress, City, State ZIPCode, Country)
    ClientJobLocationID        			INT NOT NULL,             					-- display to take user input(forced to enter, typeable text field with dropdown with only selectable clients job locations, display options format as StreetAddress, City, State ZIPCode, Country)
    PreventiveMaintenanceAgreementID    INT NULL,									-- display to take user input(not forced to enter, typeable text field with dropdown with only selectable clients PMA's, display options format by PMA AgreementNumber)
    EquipmentQuoteID     				INT NULL,									-- display to take user input(not forced to enter, typeable text field with dropdown with only selectable clients PMA's, display options format by EQ QuoteNumber)

    -- Core invoice data
    InvoiceDate          DATE        NOT NULL,										-- display to take user input(forced to enter, typeable text field, default pre-fill on ui as todays date, format on ui as mm/dd/yyyy)
    InvoiceNumber        VARCHAR(45) NOT NULL UNIQUE,								-- display to take user input(forced to enter and must be unique, typeable text field, format on ui as ##-##-###)
	PONumber      						VARCHAR(45) NULL,							-- display to take user input(not forced to enter, typeable text field)
    Terms                VARCHAR(50) NOT NULL DEFAULT 'Net 30',						-- display to take user input(forced to enter, typeable text field, default pre-filled as Net 30)
    DueDate              DATE        NOT NULL,										-- display to take user input(forced to enter, typeable text field, default pre-fill as InvoiceDate + 30 days, format on ui as mm/dd/yyyy)
    Status               ENUM('Draft','Open','Paid','Overdue','Void')				-- display to take user input(forced to enter, non-typeable preset text field with dropdown, default pre-fill on ui as draft)
                         NOT NULL DEFAULT 'Draft',

    -- Money fields
    SubtotalAmount       DECIMAL(10,2)  NOT NULL,									-- display only! no user input(non-typeable text field, auto filled with all the InvoiceItems + their total amounts)
    TaxRatePercent       DECIMAL(5,2)  	NOT NULL DEFAULT 6.00,  					-- display to take user input(forced to enter, typeable text field, default pre-filled as 6)
    TaxAmount        	 DECIMAL(10,2)												-- auto generated, so only worry about displaying it and make sure the ui follows the rules
        GENERATED ALWAYS AS (
            ROUND(SubtotalAmount * (TaxRatePercent / 100), 2)
        ) STORED,
	TotalAmount      DECIMAL(10,2)													-- auto generated, so only worry about displaying it and make sure the ui follows the rules
        GENERATED ALWAYS AS (
            SubtotalAmount + TaxAmount
        ) STORED,
    PaymentsApplied      DECIMAL(10,2)  NOT NULL DEFAULT 0.00,						-- display to take user input(forced to enter, typeable text field, default pre-filled as 0)
    BalanceDue 			 DECIMAL(10,2)												-- auto generated, so only worry about displaying it and make sure the ui follows the rules
    GENERATED ALWAYS AS (															
        CASE
            WHEN Status IN ('Void') THEN 0.00
            ELSE TotalAmount - PaymentsApplied
        END
    ) STORED,
    PaidDate      		 DATE NULL,													-- display to take user input(not forced to enter, typeable text field)
		
    -- Relationship constraint
    CONSTRAINT fk_invoice_pmagreement
        FOREIGN KEY (PreventiveMaintenanceAgreementID)
            REFERENCES PreventiveMaintenanceAgreements(PreventiveMaintenanceAgreementID)
		    ON DELETE RESTRICT,														-- If a PMA is attached to an invoice, you cannot delete that PMA

	CONSTRAINT fk_invoice_equipmentquote
        FOREIGN KEY (EquipmentQuoteID)
            REFERENCES EquipmentQuotes(EquipmentQuoteID)
		    ON DELETE RESTRICT,														-- If an Equipment Quote is attached to an invoice, you cannot delete that quote

    CONSTRAINT fk_invoice_client
        FOREIGN KEY (ClientID)
            REFERENCES Clients(ClientID)
            ON DELETE RESTRICT,														-- Prevents deleting a client thats still attached to any invoices

    CONSTRAINT fk_invoice_bill_location
        FOREIGN KEY (ClientBillingLocationID)
            REFERENCES ClientsLocations(ClientLocationID)
            ON DELETE RESTRICT,														-- Prevents deleting a bill location thats still attached to any invoice

    CONSTRAINT fk_invoice_job_location
        FOREIGN KEY (ClientJobLocationID)
            REFERENCES ClientsLocations(ClientLocationID)
            ON DELETE RESTRICT														-- Prevents deleting a job location thats still attached to any invoice
);

/* ============================================================
   TABLE 11: Invoice Line Items
============================================================ */
CREATE TABLE InvoicesItems (
    -- PK
    InvoiceItemID        INT AUTO_INCREMENT PRIMARY KEY,						-- auto generated dont worry about and dont display on ui
    
	-- FK
    InvoiceID            INT NOT NULL,   							
    
    RowNumber            INT NOT NULL,   										-- line order on invoice (1,2,3...)
	Description          VARCHAR(255)  NOT NULL,  								-- line text (equipment, labor, freight, etc.)
	Qty                  DECIMAL(10,2) NOT NULL DEFAULT 1.00,
    Rate                 DECIMAL(10,2) NOT NULL,
    TotalAmount 		 DECIMAL(10,2) GENERATED ALWAYS AS (Qty * Rate) STORED,

    -- Unique constraint so same invoice can't accidentally duplicate row numbers
    UNIQUE (InvoiceID, RowNumber),

    CONSTRAINT fk_invoice_items_invoice
        FOREIGN KEY (InvoiceID) REFERENCES Invoices(InvoiceID)
		ON DELETE CASCADE
);