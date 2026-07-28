# Causal Inference — Interview Questions

### Q1: Potential Outcomes
**Q**: Explain the fundamental problem of causal inference and how different methods address it.

**A**: We can never observe both Y(1) and Y(0) for the same unit. RCTs solve this by making treatment independent of potential outcomes via randomization. Observational methods substitute: matching finds control units with similar covariates, DiD uses untreated units' trend as a counterfactual, IV uses exogenous variation in treatment assignment.

### Q2: DiD Assumptions
**Q**: Your company launches a feature in one country but not another. You want to estimate impact using DiD. What assumptions are you making?

**A**: Parallel trends assumption — in the absence of treatment, the difference between treatment and control would have remained constant. I'd validate by testing pre-treatment trend similarity, performing placebo tests (shifting the treatment date), and including country-specific linear trends in the regression.

### Q3: Instrumental Variables
**Q**: Find an IV for "effect of college attendance on earnings." What makes a valid instrument?

**A**: Distance to nearest college at age 17 is a common instrument. Relevance: closer proximity increases college attendance probability. Exclusion: distance affects earnings only through college attendance (questionable if proximity correlates with local labor markets). Exogeneity: conditional on controls, distance is as-if random.

### Q4: Matching vs Weighting
**Q**: Compare propensity score matching vs inverse probability weighting.

**A**: Matching discards untreated units without close treated matches (reduces sample), IPTW uses all data but can produce extreme weights. Matching estimates ATT (if matching treated to control), IPTW estimates ATE (if weights are stabilized). IPTW is more efficient with good overlap; matching is more robust when overlap is poor.

### Q5: Sensitivity Analysis
**Q**: How would you convince a skeptical reviewer that your causal estimates from observational data are credible?

**A**: Multiple approaches: (1) Show covariate balance before and after matching/weighting. (2) Conduct placebo tests (treatment effect on pre-treatment outcomes). (3) Use multiple methods (matching, IPTW, DiD, IV) and show consistency. (4) Rosenbaum bounds: report how large an unobserved confounder would need to be to overturn results.

## Coding

### Q6: Nearest-neighbor matching
```java
public int[] nearestNeighborMatching(double[][] treatedFeats, double[][] controlFeats) {
    int[] matches = new int[treatedFeats.length];
    boolean[] used = new boolean[controlFeats.length];
    for (int t = 0; t < treatedFeats.length; t++) {
        double bestDist = Double.MAX_VALUE;
        int bestIdx = -1;
        for (int c = 0; c < controlFeats.length; c++) {
            if (used[c]) continue;
            double dist = euclidean(treatedFeats[t], controlFeats[c]);
            if (dist < bestDist) { bestDist = dist; bestIdx = c; }
        }
        matches[t] = bestIdx;
        used[bestIdx] = true;
    }
    return matches;
}
```
