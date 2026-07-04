# History of Experimentation

## 1700s: Early Medical Experiments

- James Lind (1747) conducted the first controlled trial — tested citrus fruits against scurvy on sailors
- Not randomized, but used comparison groups

## 1920s: Fisher's Randomization

- Ronald Fisher (1925) introduced randomization in agricultural experiments
- Developed ANOVA for analyzing multi-group experiments
- Published *Statistical Methods for Research Workers*

## 1950s: Clinical Trials

- The British Medical Research Council (1948) conducted the first randomized double-blind trial (streptomycin for tuberculosis)
- Randomization, blinding, and placebo became the medical standard

## 1990s: Online Experimentation

- Despite medical and agricultural roots, online experimentation was rare
- Most web changes were made based on opinions

## 2000s: The A/B Testing Revolution

- Google (2000): first documented A/B test — tested 41 shades of blue for search result links
- Amazon, eBay, Microsoft followed
- Kohavi, Longbotham, Sommerfield, Henne (2009): *Controlled experiments on the web: survey and practical guide*

## 2010s: Platform Era

- Optimizely (2010), Google Optimize (2017) — SaaS A/B testing platforms
- Bayesian A/B testing (Evan Miller, 2010+) — intuitive interpretation
- Multi-armed bandits for automated experimentation

## 2020s: ML-Enhanced Experimentation

- CUPED (Controlled-experiment Using Pre-Experiment Data) — variance reduction
- Sequential testing — faster decisions without peeking
- Switchback experiments — for network effects (Uber, Airbnb)

```java
// Modern experimentation: CUPED variance reduction
double cupedAdjusted = observedEffect - theta * (preTreatmentMetric - popMean);
```
