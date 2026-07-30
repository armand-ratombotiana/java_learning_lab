# Behavioral Interview Guide — Statistics

STAR framework for statistics-related behavioral questions.

## Common Questions & STAR Responses

### Q1: "Tell me about a time you used data to influence a decision"

- **Situation**: Product team wanted to launch a new feature
- **Task**: Determine if feature improves user engagement
- **Action**: Designed A/B test with proper randomization and sample size calculation; used permutation test for robustness
- **Result**: Feature showed statistically significant lift (p<0.01, +5% engagement); recommended launch; tracked post-launch for novelty effect

### Q2: "Describe a time your analysis was wrong"

- **Situation**: Initial analysis showed feature decreased retention
- **Task**: Root cause analysis of unexpected result
- **Action**: Discovered Simpson's Paradox — confounding by user segment; stratified analysis revealed positive effect in all segments
- **Result**: Corrected analysis, saved feature from being cancelled; implemented segment-aware analysis pipeline

### Q3: "How do you communicate statistical results to non-technical stakeholders?"

- **Situation**: Executive team was confused by p-values and confidence intervals
- **Task**: Explain results of A/B test clearly
- **Action**: Used concrete example with expected lift percentage; showed a "what this means for our business" slide with dollar impact
- **Result**: Executives made data-informed decision; changed company policy to always present practical significance alongside statistical significance

## Key Phrases to Use

- "Statistical significance ≠ practical significance"
- "Correlation does not imply causation"
- "We controlled for confounding variables using..."
- "The effect size was..." (always report effect size)
- "Our sample size calculation determined we needed N observations"

## Red Flags to Avoid

- Saying you never made a statistical mistake
- Confusing Type I and Type II errors
- Ignoring assumptions of the test used
- P-hacking or cherry-picking significant results
- Not mentioning multiple comparison corrections
