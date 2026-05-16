# Probability for ML - REAL WORLD PROJECT

## Project: Medical Diagnosis System

Build a medical diagnosis system using Bayesian inference with real patient data.

### Architecture

```
Patient Data → Prior Probabilities → Likelihoods → Posterior → Diagnosis
                    ↑                    ↑
              Disease          Test Results
             Prevalence           (Sensitivity,
                                  Specificity)
```

### Implementation

```java
@Service
public class MedicalDiagnosisService {
    private Map<String, Double> diseasePrevalence;
    private Map<String, Map<String, Double>> testLikelihoods;
    
    public DiagnosisResult diagnose(PatientData patient) {
        List<DiseaseHypothesis> hypotheses = new ArrayList<>();
        
        for (String disease : diseasePrevalence.keySet()) {
            double prior = diseasePrevalence.get(disease);
            double likelihood = computeLikelihood(disease, patient);
            double evidence = computeEvidence(patient);
            double posterior = (likelihood * prior) / evidence;
            
            hypotheses.add(new DiseaseHypothesis(disease, posterior));
        }
        
        return new DiagnosisResult(
            hypotheses.stream()
                .sorted(Comparator.comparing(DiseaseHypothesis::getProbability).reversed())
                .collect(Collectors.toList())
        );
    }
    
    private double computeLikelihood(String disease, PatientData patient) {
        double likelihood = 1.0;
        
        for (TestResult test : patient.getTestResults()) {
            double sensitivity = testLikelihoods.get(disease)
                .getOrDefault(test.getName() + "_sensitivity", 0.9);
            double specificity = testLikelihoods.get(disease)
                .getOrDefault(test.getName() + "_specificity", 0.9);
            
            if (test.isPositive()) {
                likelihood *= sensitivity;
            } else {
                likelihood *= (1 - specificity);
            }
        }
        
        return likelihood;
    }
}
```

### Multiple Disease Analysis

```java
public class BayesianNetwork {
    private Map<String, List<String>> dependencies;
    private Map<String, ConditionalProbabilityTable> cpts;
    
    public double[] computeJointDistribution(String[] diseases) {
        // Compute P(D₁, D₂, ..., Dₙ) using variable elimination
        // For small number of diseases, compute full joint
        // For larger, use approximate inference (MCMC, Loopy BP)
        
        int n = diseases.length;
        double[] joint = new double[1 << n];
        
        for (int mask = 0; mask < (1 << n); mask++) {
            double prob = 1.0;
            for (int i = 0; i < n; i++) {
                boolean hasDisease = (mask & (1 << i)) > 0;
                prob *= hasDisease ? 
                    getMarginal(diseases[i]) : 
                    (1 - getMarginal(diseases[i]));
            }
            joint[mask] = prob;
        }
        
        return joint;
    }
}
```

### Sensitivity Analysis

```java
public class SensitivityAnalysis {
    public Map<String, Double> analyzeImpact(String disease, 
                                               List<double[]> patientProfiles) {
        Map<String, Double> impacts = new HashMap<>();
        
        double baseRate = getPrevalence(disease);
        double[] perturbedRates = {
            baseRate * 0.5, baseRate, baseRate * 1.5
        };
        
        for (double rate : perturbedRates) {
            double accuracy = evaluateWithPrevalence(disease, rate, patientProfiles);
            impacts.put("prevalence_" + rate, accuracy);
        }
        
        return impacts;
    }
    
    private double evaluateWithPrevalence(String disease, double prevalence,
                                          List<double[]> profiles) {
        // Evaluate classifier performance with different priors
        setPrevalence(disease, prevalence);
        return runCrossValidation(profiles);
    }
}
```

### ROC Analysis

```java
public class ROCAnalysis {
    public double computeAUC(List<DiagnosisResult> results) {
        // Sort by probability descending
        // Compute True Positive Rate and False Positive Rate at each threshold
        
        List<double[]> thresholds = new ArrayList<>();
        for (DiagnosisResult r : results) {
            thresholds.add(new double[]{r.getMaxProbability(), r.isTruePositive() ? 1 : 0});
        }
        thresholds.sort(Comparator.comparingDouble(t -> t[0]).reversed());
        
        int tp = 0, fp = 0;
        double totalPositives = countPositives(results);
        double totalNegatives = results.size() - totalPositives;
        
        List<double[]> rocPoints = new ArrayList<>();
        for (double[] t : thresholds) {
            if (t[1] == 1) tp++;
            else fp++;
            
            rocPoints.add(new double[]{
                (double) fp / totalNegatives,  // FPR
                (double) tp / totalPositives   // TPR
            });
        }
        
        return computeAreaUnderCurve(rocPoints);
    }
}
```

## Deliverables

- [x] Bayesian inference engine
- [x] Disease prior probabilities (from epidemiology data)
- [x] Test likelihoods (sensitivity/specificity)
- [x] Multi-disease analysis
- [x] Sensitivity analysis
- [x] ROC curve and AUC computation
- [x] Uncertainty quantification
- [x] API endpoint for diagnosis