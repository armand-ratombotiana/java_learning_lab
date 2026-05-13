package causal;

import java.util.*;
import java.util.stream.Collectors;

public class CausalInference {
    public static class PropensityScore {
        public static double[] estimateLogistic(double[][] features, int[] treatment) {
            int n = features.length;
            double[] propScores = new double[n];
            
            for (int i = 0; i < n; i++) {
                double logit = features[i][0] * 0.5 + features[i][1] * 0.3;
                propScores[i] = 1 / (1 + Math.exp(-logit));
                propScores[i] = Math.max(0.1, Math.min(0.9, propScores[i]));
            }
            
            return propScores;
        }
        
        public static double[] ipw(double[] outcomes, int[] treatment, double[] propScores) {
            double ate = 0;
            double treated = 0, control = 0;
            
            for (int i = 0; i < outcomes.length; i++) {
                if (treatment[i] == 1) {
                    ate += outcomes[i] / propScores[i];
                    treated++;
                } else {
                    ate -= outcomes[i] / (1 - propScores[i]);
                    control++;
                }
            }
            
            ate /= outcomes.length;
            return new double[]{ate};
        }
        
        public static int[][] match(double[] propScores, int[] treatment, int k) {
            List<Integer> treatedIdx = new ArrayList<>();
            List<Integer> controlIdx = new ArrayList<>();
            
            for (int i = 0; i < treatment.length; i++) {
                if (treatment[i] == 1) treatedIdx.add(i);
                else controlIdx.add(i);
            }
            
            int[][] matches = new int[treatedIdx.size()][k];
            
            for (int i = 0; i < treatedIdx.size(); i++) {
                double targetProp = propScores[treatedIdx.get(i)];
                
                List<Integer> sortedControl = new ArrayList<>(controlIdx);
                sortedControl.sort(Comparator.comparingDouble(j -> 
                    Math.abs(propScores[j] - targetProp)));
                
                for (int j = 0; j < k && j < sortedControl.size(); j++) {
                    matches[i][j] = sortedControl.get(j);
                }
            }
            
            return matches;
        }
    }
    
    public static class MatchingEstimator {
        public static double estimateATT(double[] outcomes, int[] treatment, int[][] matches) {
            double treatedSum = 0, controlSum = 0;
            int n = matches.length;
            
            for (int i = 0; i < n; i++) {
                treatedSum += outcomes[matches[i][0]];
            }
            
            for (int i = 0; i < n; i++) {
                double matchedSum = 0;
                for (int j = 0; j < matches[i].length; j++) {
                    matchedSum += outcomes[matches[i][j]];
                }
                controlSum += matchedSum / matches[i].length;
            }
            
            return (treatedSum - controlSum) / n;
        }
    }
    
    public static class Stratification {
        public static double estimateATEByStrata(double[] outcomes, int[] treatment, double[] propScores, int nStrata) {
            double[] strataATE = new double[nStrata];
            int[] strataCount = new int[nStrata];
            
            double minProp = Arrays.stream(propScores).min().orElse(0);
            double maxProp = Arrays.stream(propScores).max().orElse(1);
            double stratumWidth = (maxProp - minProp) / nStrata;
            
            for (int i = 0; i < outcomes.length; i++) {
                int stratum = Math.min(nStrata - 1, (int) ((propScores[i] - minProp) / stratumWidth));
                double effect = 0;
                
                double treatedMean = Arrays.stream(propScores).filter(p -> p >= minProp + stratum * stratumWidth 
                    && p < minProp + (stratum + 1) * stratumWidth && treatment[i] == 1)
                    .mapToObj(idx -> outcomes[idx]).mapToDouble(Double::doubleValue).orElse(0);
                
                double controlMean = Arrays.stream(propScores).filter(p -> p >= minProp + stratum * stratumWidth 
                    && p < minProp + (stratum + 1) * stratumWidth && treatment[i] == 0)
                    .mapToObj(idx -> outcomes[idx]).mapToDouble(Double::doubleValue).orElse(0);
                
                effect = treatedMean - controlMean;
                strataATE[stratum] += effect;
                strataCount[stratum]++;
            }
            
            double totalATE = 0;
            int totalCount = 0;
            
            for (int i = 0; i < nStrata; i++) {
                if (strataCount[i] > 0) {
                    totalATE += strataATE[i] / strataCount[i];
                    totalCount++;
                }
            }
            
            return totalCount > 0 ? totalATE / totalCount : 0;
        }
    }
    
    public static class DID {
        public static double differenceInDifferences(double[] treatedPre, double[] treatedPost,
                                                      double[] controlPre, double[] controlPost) {
            double treatedDiff = Arrays.stream(treatedPost).sum() / treatedPost.length -
                               Arrays.stream(treatedPre).sum() / treatedPre.length;
            double controlDiff = Arrays.stream(controlPost).sum() / controlPost.length -
                               Arrays.stream(controlPre).sum() / controlPre.length;
            return treatedDiff - controlDiff;
        }
    }
    
    public static class IVEstimator {
        public static double twoSLS(double[] instrument, double[] treatment, double[] outcome) {
            double meanZ = Arrays.stream(instrument).sum() / instrument.length;
            double meanT = Arrays.stream(treatment).sum() / treatment.length;
            double meanY = Arrays.stream(outcome).sum() / outcome.length;
            
            double covZT = 0, varZ = 0;
            for (int i = 0; i < instrument.length; i++) {
                covZT += (instrument[i] - meanZ) * (treatment[i] - meanT);
                varZ += (instrument[i] - meanZ) * (instrument[i] - meanZ);
            }
            
            double firstStage = varZ > 0 ? covZT / varZ : 0;
            
            double covZY = 0;
            for (int i = 0; i < instrument.length; i++) {
                covZY += (instrument[i] - meanZ) * (outcome[i] - meanY);
            }
            
            double reducedForm = covZY / varZ;
            
            return varZ > 0 ? reducedForm / firstStage : 0;
        }
    }
    
    public static class BalanceCheck {
        public static double[] standardizeMeanDiff(double[][] covariates, int[] treatment) {
            List<double[]> treatedCovs = new ArrayList<>();
            List<double[]> controlCovs = new ArrayList<>();
            
            for (int i = 0; i < treatment.length; i++) {
                if (treatment[i] == 1) treatedCovs.add(covariates[i]);
                else controlCovs.add(covariates[i]);
            }
            
            double[] smd = new double[covariates[0].length];
            
            for (int j = 0; j < covariates[0].length; j++) {
                double meanT = treatedCovs.stream().mapToDouble(c -> c[j]).average().orElse(0);
                double meanC = controlCovs.stream().mapToDouble(c -> c[j]).average().orElse(0);
                
                double varT = treatedCovs.stream().mapToDouble(c -> Math.pow(c[j] - meanT, 2)).sum() / treatedCovs.size();
                double varC = controlCovs.stream().mapToDouble(c -> Math.pow(c[j] - meanC, 2)).sum() / controlCovs.size();
                
                double pooledStd = Math.sqrt((varT + varC) / 2);
                smd[j] = pooledStd > 0 ? (meanT - meanC) / pooledStd : 0;
            }
            
            return smd;
        }
        
        public static boolean isBalanced(double[] smd, double threshold) {
            for (double v : smd) {
                if (Math.abs(v) > threshold) return false;
            }
            return true;
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Causal Inference Demo ===\n");
        
        int n = 1000;
        double[][] features = new double[n][2];
        int[] treatment = new int[n];
        double[] outcomes = new double[n];
        
        Random rand = new Random(42);
        
        for (int i = 0; i < n; i++) {
            features[i][0] = rand.nextGaussian() * 10 + 50;
            features[i][1] = rand.nextGaussian() * 5 + 20;
            
            double propensity = 1 / (1 + Math.exp(-(0.1 * features[i][0] + 0.2 * features[i][1] - 5)));
            treatment[i] = rand.nextDouble() < propensity ? 1 : 0;
            
            double baseOutcome = 100 + 0.5 * features[i][0] + 0.3 * features[i][1];
            double treatmentEffect = 10;
            double noise = rand.nextGaussian() * 5;
            outcomes[i] = baseOutcome + treatmentEffect * treatment[i] + noise;
        }
        
        System.out.println("=== Propensity Score Estimation ===");
        double[] propScores = PropensityScore.estimateLogistic(features, treatment);
        System.out.printf("Mean propensity (treated): %.3f%n", 
            Arrays.stream(propScores).filter(p -> treatment[Arrays.binarySearch(propScores, p)] == 1).average().orElse(0));
        System.out.printf("Mean propensity (control): %.3f%n",
            Arrays.stream(propScores).filter(p -> treatment[Arrays.binarySearch(propScores, p)] == 0).average().orElse(0));
        
        System.out.println("\n=== IPW Estimation ===");
        double[] ateEstimate = PropensityScore.ipw(outcomes, treatment, propScores);
        System.out.printf("ATE estimate: %.2f%n", ateEstimate[0]);
        System.out.printf("True ATE: 10.00%n");
        
        System.out.println("\n=== Matching ===");
        int[][] matches = PropensityScore.match(propScores, treatment, 5);
        double attEstimate = MatchingEstimator.estimateATT(outcomes, treatment, matches);
        System.out.printf("ATT estimate: %.2f%n", attEstimate);
        
        System.out.println("\n=== Balance Check ===");
        double[] smd = BalanceCheck.standardizeMeanDiff(features, treatment);
        System.out.println("Standardized mean differences:");
        System.out.printf("  Feature 0: %.4f%n", smd[0]);
        System.out.printf("  Feature 1: %.4f%n", smd[1]);
        System.out.println("Balanced (|SMD| < 0.1): " + BalanceCheck.isBalanced(smd, 0.1));
        
        System.out.println("\n=== Difference in Differences ===");
        double[] treatedPre = new double[50], treatedPost = new double[50];
        double[] controlPre = new double[50], controlPost = new double[50];
        
        for (int i = 0; i < 50; i++) {
            treatedPre[i] = 100 + rand.nextGaussian() * 5;
            treatedPost[i] = 115 + rand.nextGaussian() * 5;
            controlPre[i] = 95 + rand.nextGaussian() * 5;
            controlPost[i] = 105 + rand.nextGaussian() * 5;
        }
        
        double didEstimate = DID.differenceInDifferences(treatedPre, treatedPost, controlPre, controlPost);
        System.out.printf("DiD estimate: %.2f%n", didEstimate);
    }
}