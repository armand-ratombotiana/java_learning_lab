# Feature Engineering Quiz

## Section 1: Scaling (1-10)

**Question 1:** What does z-score standardization do?
- A) Scales to [0, 1]
- B) Centers to mean 0, std 1
- C) Removes outliers
- D) Reduces dimensionality

**Question 2:** When is Min-Max scaling appropriate?
- A) When data has outliers
- B) When bounded output needed
- C) For normal distributions
- D) For categorical data

**Question 3:** What does robust scaling use?
- A) Mean and std
- B) Median and IQR
- C) Min and max
- D) Mode and range

**Question 4:** When is log transformation useful?
- A) For normal data
- B) For right-skewed data
- C) For left-skewed data
- D) For binary data

**Question 5:** What is the formula for z-score?
- A) (x - min) / (max - min)
- B) (x - mean) / std
- C) x / mean
- D) x * std + mean

**Question 6:** What does power transformation do?
- A) Reduces outliers
- B) Makes data more normal
- C) Increases variance
- D) Removes missing values

**Question 7:** Which scaling is sensitive to outliers?
- A) Standardization
- B) Min-Max
- C) Robust
- D) Log transform

**Question 8:** What is clipping used for?
- A) Add features
- B) Limit extreme values
- C) Scale data
- D) Encode categories

**Question 9:** Why scale features for ML?
- A) Improve speed only
- B) Ensure fair feature contribution
- C) Remove missing values
- D) Increase dimensionality

**Question 10:** Which transform handles negative values?
- A) Log transform
- B) Box-Cox
- C) Yeo-Johnson
- D) Min-Max

---

## Section 2: Encoding (11-20)

**Question 11:** What does label encoding do?
- A) Creates binary columns
- B) Assigns integers to categories
- C) Calculates target means
- D) Counts frequencies

**Question 12:** When is one-hot encoding preferred?
- A) For ordinal categories
- B) For high cardinality
- C) For low cardinality nominal categories
- D) For numeric features

**Question 13:** What is target encoding?
- A) Frequency of each category
- B) Mean of target per category
- C) Binary encoding
- D) Random encoding

**Question 14:** What problem does target encoding solve?
- A) Memory efficiency
- B) High cardinality categorical encoding
- C) Missing values
- D) Outliers

**Question 15:** What is frequency encoding?
- A) Mean of target per category
- B) Count/frequency of category
- C) Binary encoding
- D) Ordinal encoding

**Question 16:** Why add missingness indicators?
- A) Reduce memory
- B) Capture information about why data is missing
- C) Remove missing values
- D) Normalize data

**Question 17:** What is ordinal encoding?
- A) Random integer assignment
- B) Integer with meaningful order
- C) Binary encoding
- D) Count encoding

**Question 18:** When is binary encoding useful?
- A) Low cardinality
- B) Very high cardinality
- C) Numeric features
- D) Text features

**Question 19:** What does leave-one-out encoding do?
- A) Uses all data for target encoding
- B) Excludes current observation from target encoding
- C) Creates multiple encodings
- D) Leaves categorical as is

**Question 20:** What is Weight of Evidence (WOE)?
- A) Count of categories
- B) Log odds of target per category
- C) Binary encoding
- D) Mean encoding

---

## Section 3: Imputation (21-30)

**Question 21:** When is mean imputation appropriate?
- A) Data with outliers
- B) Missing at random, symmetric distribution
- C) Non-random missing
- D) Categorical data

**Question 22:** What does forward fill do?
- A) Fill with mean
- B) Use last valid value to fill forward
- C) Fill with zeros
- D) Remove missing

**Question 23:** What is KNN imputation?
- A) Fill with global mean
- B) Use K nearest neighbors to impute
- C) Remove rows with missing
- D) Fill with mode

**Question 24:** Why add missingness indicators?
- A) Increase data size
- B) Capture pattern of missingness
- C) Reduce variance
- D) Normalize data

**Question 25:** What is multiple imputation?
- A) Fill with multiple means
- B) Create multiple complete datasets
- C) Fill with K values
- D) Remove missing values

**Question 26:** When is median preferred over mean for imputation?
- A) Normal distribution
- B) Skewed distribution
- C) Uniform distribution
- D) Binary data

**Question 27:** What does backward fill do?
- A) Uses mean
- B) Uses next valid value to fill backward
- C) Removes missing
- D) Fills with zeros

**Question 28:** What is interpolation imputation?
- A) Fill with mean
- B) Estimate between known values
- C) Fill with mode
- D) Remove rows

**Question 29:** What is a danger of simple imputation?
- A) Too slow
- B) Can reduce variance artificially
- C) Uses too much memory
- D) Changes data type

**Question 30:** When is constant imputation appropriate?
- A) Always
- B) When domain suggests specific value
- C) For all missing values
- D) For numeric only

---

## Answer Key

1-B, 2-B, 3-B, 4-B, 5-B, 6-B, 7-B, 8-B, 9-B, 10-C,
11-B, 12-C, 13-B, 14-B, 15-B, 16-B, 17-B, 18-B, 19-B, 20-B,
21-B, 22-B, 23-B, 24-B, 25-B, 26-B, 27-B, 28-B, 29-B, 30-B