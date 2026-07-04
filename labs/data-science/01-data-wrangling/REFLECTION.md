# Reflection: Data Wrangling

## Key Takeaways

- Most data science effort goes into wrangling, not modeling
- Every wrangling decision (how to impute, what to drop) is a modeling choice that affects results
- Immutability and explicit schema make pipelines safe and debuggable
- Automated validation is the only way to trust data at scale

## Questions to Internalize

1. When I fill a missing value, what assumption am I making about the mechanism of missingness (MCAR, MAR, MNAR)?
2. If I remove outliers, what tail of the distribution am I censoring — and does that matter for the business question?
3. How would I detect silently that a source system changed its schema or data semantics?
4. What is the cost (in model quality) of each wrangling shortcut I'm taking?

## Growth Areas

| Skill | Beginner | Proficient | Master |
|---|---|---|---|
| Missing value handling | Drop rows | Mean/median impute | Multiple imputation (MICE) |
| Outlier detection | Z-score | IQR method | Isolation Forest |
| Type coercion | Manual parse | Schema validation | ML-based type inference |
| Pipeline testing | Manual spot-check | Unit tests per stage | Data diff + lineage tracking |
