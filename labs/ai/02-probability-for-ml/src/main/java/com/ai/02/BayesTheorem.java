package com.ai02;

public class BayesTheorem {

    public static double calculatePosterior(double prior, double likelihood, double evidence) {
        return (likelihood * prior) / evidence;
    }

    public static double calculateEvidence(double priorA, double likelihoodAGivenB,
                                            double priorNotA, double likelihoodNotAGivenB) {
        return likelihoodAGivenB * priorA + likelihoodNotAGivenB * priorNotA;
    }

    public static double[] bayesianUpdate(double[] prior, double[][] likelihoodMatrix, int observation) {
        int n = prior.length;
        double[] posterior = new double[n];
        double evidence = 0;
        for (int i = 0; i < n; i++)
            evidence += likelihoodMatrix[observation][i] * prior[i];
        for (int i = 0; i < n; i++)
            posterior[i] = (likelihoodMatrix[observation][i] * prior[i]) / evidence;
        return posterior;
    }

    public static double naiveBayesClassify(double[][] classProbs, double[] priors, double[] features) {
        int numClasses = priors.length;
        double[] scores = new double[numClasses];
        for (int c = 0; c < numClasses; c++) {
            scores[c] = Math.log(priors[c]);
            for (int f = 0; f < features.length; f++)
                scores[c] += Math.log(classProbs[c][f]);
        }
        int bestClass = 0;
        for (int c = 1; c < numClasses; c++)
            if (scores[c] > scores[bestClass]) bestClass = c;
        return bestClass;
    }

    public static void main(String[] args) {
        System.out.println("=== Bayes Theorem Demo ===");
        double priorDisease = 0.01;
        double likelihoodPositiveGivenDisease = 0.99;
        double falsePositiveRate = 0.05;
        double evidence = calculateEvidence(priorDisease,
            likelihoodPositiveGivenDisease, 1 - priorDisease, falsePositiveRate);
        double posterior = calculatePosterior(priorDisease,
            likelihoodPositiveGivenDisease, evidence);
        System.out.println("P(Disease|Positive) = " + posterior);

        double[] prior = {0.5, 0.5};
        double[][] likelihood = {{0.9, 0.2}, {0.1, 0.8}};
        double[] posteriorUpdate = bayesianUpdate(prior, likelihood, 0);
        System.out.println("Posterior after observation 0: " + java.util.Arrays.toString(posteriorUpdate));
    }
}
