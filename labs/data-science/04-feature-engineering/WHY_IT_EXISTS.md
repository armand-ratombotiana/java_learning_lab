# Why Feature Engineering Exists

Feature engineering exists because raw data is rarely in the right form for machine learning models. Models are mathematical functions — they need numeric inputs, bounded ranges, and independent predictors. Feature engineering transforms domain knowledge into mathematical features that a model can consume.

## The Gap It Bridges

- **Raw data** has text, dates, categorical labels, missing values — models need numbers
- **Domain knowledge** exists in human minds but not in databases — feature engineering codifies it
- **Model capacity** is limited — good features let simpler models match complex ones

```java
// Without feature engineering: raw string dates
"2024-01-15"  // meaningless to a model as a string

// With feature engineering
int dayOfWeek = 1;   // Monday
int month = 1;       // January
boolean isWeekend = false;
int daysSinceEpoch = 19742;  // monotonic trend
```
