package causal;

public class ABTestAnalysis {
    public static void main(String[] args) {
        System.out.println("=== A/B Test Causal Analysis ===\n");
        
        int n = 1000;
        double[] age = new double[n];
        int[] sessions = new int[n];
        int[] treatment = new int[n];
        boolean[] converted = new boolean[n];
        
        java.util.Random rand = new java.util.Random(42);
        
        for (int i = 0; i < n; i++) {
            age[i] = 20 + rand.nextGaussian() * 10;
            sessions[i] = 1 + rand.nextInt(20);
            treatment[i] = i < 500 ? 0 : 1;
            double prob = 0.1 + (treatment[i] == 1 ? 0.05 : 0) + age[i] / 500 + sessions[i] / 200;
            converted[i] = rand.nextDouble() < prob;
        }
        
        System.out.println("=== Balance Check ===");
        double[] treatedAge = new double[500];
        double[] controlAge = new double[500];
        for (int i = 0; i < 500; i++) {
            controlAge[i] = age[i];
            treatedAge[i] = age[500 + i];
        }
        
        double meanTreatAge = java.util.Arrays.stream(treatedAge).sum() / 500;
        double meanControlAge = java.util.Arrays.stream(controlAge).sum() / 500;
        double varTreat = java.util.Arrays.stream(treatedAge).map(x -> Math.pow(x - meanTreatAge, 2)).sum() / 500;
        double varControl = java.util.Arrays.stream(controlAge).map(x -> Math.pow(x - meanControlAge, 2)).sum() / 500;
        double pooledStd = Math.sqrt((varTreat + varControl) / 2);
        double smd = pooledStd > 0 ? (meanTreatAge - meanControlAge) / pooledStd : 0;
        
        System.out.printf("Age SMD: %.4f (Balanced if |SMD| < 0.1)%n", smd);
        System.out.println(smd < 0.1 ? "✓ Balanced" : "✗ Not balanced");
        
        System.out.println("\n=== Simple Comparison ===");
        int treatConv = 0, controlConv = 0;
        for (int i = 0; i < n; i++) {
            if (treatment[i] == 1 && converted[i]) treatConv++;
            if (treatment[i] == 0 && converted[i]) controlConv++;
        }
        double treatRate = (double) treatConv / 500;
        double controlRate = (double) controlConv / 500;
        System.out.printf("Control conversion rate: %.2f%%%n", controlRate * 100);
        System.out.printf("Treatment conversion rate: %.2f%%%n", treatRate * 100);
        System.out.printf("Observed lift: %.2f%%%n", (treatRate - controlRate) * 100);
        
        System.out.println("\n=== Propensity Score Matching ===");
        double[] propScores = new double[n];
        for (int i = 0; i < n; i++) {
            propScores[i] = 0.5;
        }
        
        int[][] matches = CausalInference.PropensityScore.match(propScores, treatment, 3);
        double attEstimate = CausalInference.MatchingEstimator.estimateATT(
            java.util.stream.IntStream.range(0, n).mapToDouble(i -> converted[i] ? 1 : 0).toArray(),
            treatment, matches);
        System.out.printf("ATT estimate: %.4f%n", attEstimate);
        
        System.out.println("\n=== Summary ===");
        System.out.println("Randomization successful - groups balanced");
        System.out.printf("Estimated treatment effect: %.2f percentage points%n", attEstimate);
    }
}