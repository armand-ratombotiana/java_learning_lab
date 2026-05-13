# Real World Project: Healthcare Patient Data Pipeline

## Project Overview
Build a production-ready data pipeline for a healthcare system to clean, standardize, and analyze patient records for clinical research.

## Background
A regional hospital network needs to consolidate patient data from multiple legacy systems. Data quality issues include:
- Duplicate patient records
- Inconsistent medical codes
- Missing critical fields
- Various date formats
- Non-standardized addresses

## Requirements

### Phase 1: Data Ingestion
- Load patient data from multiple CSV sources
- Load insurance provider data
- Load medical code mappings (ICD-10, CPT)
- Generate ingestion report

### Phase 2: Data Cleaning
- Deduplicate patients using multiple identifiers
- Standardize dates to ISO format
- Clean and validate phone numbers
- Parse and standardize addresses
- Handle missing critical fields (death date, etc.)
- Validate medical codes against reference tables

### Phase 3: Data Enrichment
- Map ICD-10 codes to descriptions
- Map CPT codes to procedure names
- Join with insurance provider data
- Calculate patient risk scores
- Add derived temporal features

### Phase 4: Analysis
- Patient demographics distribution
- Common diagnoses frequency
- Insurance coverage analysis
- Readmission rate analysis
- Length of stay statistics

### Phase 5: Output
- Generate cleaned patient dataset
- Generate analytics summary
- Generate data quality certification
- Create audit trail

## Sample Data Files

### patients_raw.csv
```
patient_id,mrn,first_name,last_name,dob,gender,phone,address,city,state,zip,insurance_id,primary_diagnosis,secondary_diagnosis,admission_date,discharge_date,total_charges
```

### insurance_providers.csv
```
insurance_id,provider_name,plan_type,coverage_level
```

### icd10_codes.csv
```
code,description,category
```

### cpt_codes.csv
```
code,description,modifier
```

## Implementation Guidelines

1. **Error Handling**: Log all errors, continue processing valid records
2. **Validation Rules**: Each field should have validation rules
3. **Audit Trail**: Log every transformation for compliance
4. **Performance**: Handle datasets up to 1M records efficiently
5. **Configuration**: Externalize paths and settings

## Expected Deliverables

1. Complete Java pipeline with all phases
2. Sample data files
3. Configuration file
4. Processing logs
5. Quality certification report
6. Analytics summary

## Bonus Features

1. Parallel processing for large files
2. Checksum verification for data integrity
3. Automated regression testing
4. Email notifications for pipeline failures
5. Dashboard for monitoring