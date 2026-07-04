# Reflection: Exploratory Data Analysis

## Key Takeaways

- EDA is not a step — it's an ongoing conversation with the data that continues through the modeling lifecycle
- Summary statistics without visualization are dangerous (Anscombe's quartet)
- Every EDA finding is a hypothesis, not a conclusion — it requires validation on holdout data
- Automating EDA is good for speed; manual EDA is essential for depth

## Questions to Internalize

1. What patterns might I be missing because I only look at averages?
2. Am I treating missing values as random when they might be systematic?
3. What would surprise me about this data — and have I checked for it?
4. If I showed this EDA to a domain expert, what would they say is wrong?

## Growth Areas

| Skill | Beginner | Proficient | Master |
|---|---|---|---|
| Distribution analysis | Mean/std | Histogram + box plot | Density estimation + QQ plots |
| Relationship detection | Pearson r | Spearman + scatter | Mutual info + partial correlations |
| Missing data | Count missing | Visualize missing patterns | Test MCAR/MAR/MNAR |
| EDA automation | Manual per dataset | Scripted profile function | Profile library + CI checks |
