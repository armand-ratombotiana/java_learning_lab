# Mock Interview: Causal Inference

**Interviewer**: We launched a feature in the US but not in Canada. We see a 5% increase in engagement in the US post-launch. Can we attribute this to the feature?

**Candidate**: Not directly — this is a classic before-after comparison without counterfactual. The increase could be due to seasonality, other concurrent changes, or external events. I'd start with a difference-in-differences approach using Canada as the control group. The key assumption is parallel trends: that US and Canada engagement moved similarly before launch.

**Interviewer**: What if Canada isn't a valid control because of different user behavior?

**Candidate**: Then I'd check for alternative comparison groups. Could use synthetic control — construct a weighted combination of other countries that matches US pre-trend. Or use CausalImpact (Bayesian structural time series) which models the counterfactual post-intervention time series. I'd also look for within-US variation — e.g., users who were exposed earlier vs later.

**Interviewer**: Let's say we have user-level data with demographics. How would you estimate the effect with confounders?

**Candidate**: Propensity score matching or weighting. I'd model the probability of being in the treated group (launch-time US users) as a function of demographics using logistic regression. Then match treated to control users on the propensity score (or use IPTW). After matching, I'd check covariate balance via standardized mean differences. If balance is achieved, the difference in outcomes estimates the ATT.

**Interviewer**: What if an unobserved confounder biases the result?

**Candidate**: Rosenbaum sensitivity analysis. I'd compute how large an unobserved confounder's effect would need to be (in terms of Γ, the odds ratio of receiving treatment) to render my result non-significant. If Γ is large (e.g., > 3), the result is robust. I might also consider an instrumental variable if one exists.

**Interviewer**: Find an instrument for "using the new recommendation algorithm" on "watch time."

**Candidate**: I'd look for a source of exogenous variation in algorithm exposure. For example, if the algorithm was rolled out gradually by user ID hash (A/B test), that's random. If it was rolled out by region, region assignment could serve as an instrument, but exclusion is violated if regions have different content licensing. A better instrument: UI changes that make the algorithm more visible, rolled out to a random subset.

**Interviewer**: Let's code. Implement ATT estimation via one-to-one propensity score matching.

**Candidate**:
```java
public double estimateATT(double[][] features, boolean[] treated, double[] outcomes) {
    double[] ps = estimatePropensity(features, treated);
    List<Integer> treatedIdx = new ArrayList<>(), controlIdx = new ArrayList<>();
    for (int i = 0; i < treated.length; i++) {
        if (treated[i]) treatedIdx.add(i); else controlIdx.add(i);
    }
    
    double sumDiff = 0.0;
    boolean[] used = new boolean[controlIdx.size()];
    for (int tIdx : treatedIdx) {
        double bestDist = Double.MAX_VALUE;
        int bestMatch = -1;
        for (int c = 0; c < controlIdx.size(); c++) {
            if (used[c]) continue;
            double dist = Math.abs(ps[tIdx] - ps[controlIdx.get(c)]);
            if (dist < bestDist) { bestDist = dist; bestMatch = c; }
        }
        if (bestMatch != -1) {
            sumDiff += outcomes[tIdx] - outcomes[controlIdx.get(bestMatch)];
            used[bestMatch] = true;
        }
    }
    return sumDiff / treatedIdx.size();
}
```

**Interviewer**: What if the closest match has a very different propensity score?

**Candidate**: Set a caliper — maximum allowed propensity score difference (commonly 0.05 on the logit scale). Units without matches within the caliper are discarded. This improves balance at the cost of generalizability (we're now estimating the ATT for the subpopulation of treated units that have close matches).
