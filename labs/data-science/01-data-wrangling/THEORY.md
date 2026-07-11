# Data Wrangling Theory & Intuition

## 💡 The Reality of Real-World Data
In academic settings, datasets are usually clean, complete, and perfectly formatted. In the real world, data is messy, inconsistent, incomplete, and full of errors. 
The famous adage in Data Science is **"Garbage In, Garbage Out" (GIGO)**. Even the most advanced Deep Learning model will fail if trained on bad data. Data Wrangling (or Data Cleaning) is the process of transforming raw data into a format suitable for modeling. It often consumes 70-80% of a Data Scientist's time.

## 🧹 Core Wrangling Operations

### 1. Handling Missing Data (NaNs)
Data can be missing for various reasons (sensor failure, user skipped a question).
- **Deletion**: Remove the entire row (if the dataset is large and the missing data is random) or the entire column (if >50% of the data is missing).
- **Imputation**: Fill in the missing values. 
  - *Mean/Median/Mode Imputation*: Replace missing values with the average of that column.
  - *Predictive Imputation*: Use a machine learning model to predict what the missing value should be based on other columns.

### 2. Feature Scaling
Machine learning models that rely on distance calculations (like K-Means, SVM, or Neural Networks) are highly sensitive to the scale of the data. If "Age" ranges from 0-100 and "Salary" ranges from 0-1,000,000, the model will treat "Salary" as infinitely more important simply because the numbers are bigger.
- **Normalization (Min-Max Scaling)**: Scales all values to a fixed range, usually [0, 1].
- **Standardization (Z-score)**: Centers the data around a mean of 0 with a standard deviation of 1.

### 3. Encoding Categorical Data
Math models only understand numbers, not text like "Red", "Green", "Blue".
- **Label Encoding**: Converts categories to integers (Red=1, Green=2, Blue=3). *Warning*: This implies an ordinal relationship (Blue > Red), which is bad for nominal data like colors.
- **One-Hot Encoding**: Creates a new binary column for every category. (Is_Red: 1/0, Is_Green: 1/0, Is_Blue: 1/0). This is the standard approach for nominal data.

### 4. Outlier Detection
Outliers are extreme values that deviate significantly from the rest of the data. They can heavily skew models like Linear Regression. They are often detected using Z-scores or the Interquartile Range (IQR) and are either removed or capped.