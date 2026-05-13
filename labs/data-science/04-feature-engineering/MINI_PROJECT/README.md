# Mini Project: Feature Engineering for House Prices

## Objective
Create comprehensive feature engineering pipeline for house price prediction.

## Dataset
Housing data with features:
- Numerical: square footage, bedrooms, bathrooms, year built, lot size
- Categorical: neighborhood, condition, style
- Date: sale date

## Tasks

### 1. Numerical Features
- Scale features (standardization, min-max)
- Create polynomial features
- Create interaction features
- Handle outliers

### 2. Categorical Features
- One-hot encode low cardinality
- Target encode high cardinality
- Create ordinal encoding for ordered categories

### 3. Date Features
- Extract year, month, day
- Calculate age of house
- Extract season/quarter

### 4. Missing Value Handling
- Impute numerical with mean/median
- Impute categorical with mode
- Create missing indicators

### 5. Feature Selection
- Remove low variance features
- Remove highly correlated features
- Select based on importance

## Output
Complete feature engineering pipeline with documentation.