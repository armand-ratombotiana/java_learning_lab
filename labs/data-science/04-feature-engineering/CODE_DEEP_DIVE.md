package feature;

import java.util.*;
import java.util.stream.Collectors;

public class FeatureEngineering {
    public static class Scaler {
        public static double[] standardize(double[] data) {
            double mean = Arrays.stream(data).sum() / data.length;
            double std = Math.sqrt(variance(data, mean));
            return Arrays.stream(data).map(x -> (x - mean) / std).toArray();
        }
        
        public static double[] minMaxScale(double[] data) {
            double min = Arrays.stream(data).min().orElse(0);
            double max = Arrays.stream(data).max().orElse(1);
            if (max == min) return Arrays.stream(data).map(x -> 0.5).toArray();
            return Arrays.stream(data).map(x -> (x - min) / (max - min)).toArray();
        }
        
        public static double[] robustScale(double[] data) {
            double[] sorted = data.clone();
            Arrays.sort(sorted);
            double q1 = percentile(sorted, 25);
            double q3 = percentile(sorted, 75);
            double iqr = q3 - q1;
            double median = percentile(sorted, 50);
            return Arrays.stream(data).map(x -> iqr == 0 ? 0 : (x - median) / iqr).toArray();
        }
        
        public static double[] logTransform(double[] data) {
            return Arrays.stream(data).map(x -> x > 0 ? Math.log(x) : 0).toArray();
        }
        
        private static double variance(double[] data, double mean) {
            return Arrays.stream(data).map(x -> Math.pow(x - mean, 2)).sum() / data.length;
        }
        
        private static double percentile(double[] sorted, double p) {
            double pos = p * (sorted.length - 1) / 100;
            int idx = (int) Math.floor(pos);
            double frac = pos - idx;
            if (idx + 1 < sorted.length) {
                return sorted[idx] * (1 - frac) + sorted[idx + 1] * frac;
            }
            return sorted[idx];
        }
    }
    
    public static class Encoder {
        public static int[] labelEncode(String[] data) {
            Map<String, Integer> map = new HashMap<>();
            int label = 0;
            int[] encoded = new int[data.length];
            
            for (int i = 0; i < data.length; i++) {
                if (!map.containsKey(data[i])) {
                    map.put(data[i], label++);
                }
                encoded[i] = map.get(data[i]);
            }
            return encoded;
        }
        
        public static double[][] oneHotEncode(String[] data) {
            Map<String, Integer> map = new LinkedHashMap<>();
            for (String val : data) {
                if (!map.containsKey(val)) {
                    map.put(val, map.size());
                }
            }
            
            double[][] encoded = new double[data.length][map.size()];
            for (int i = 0; i < data.length; i++) {
                int col = map.get(data[i]);
                encoded[i][col] = 1;
            }
            return encoded;
        }
        
        public static double[] targetEncode(String[] categorical, double[] target) {
            Map<String, List<Double>> groups = new HashMap<>();
            for (int i = 0; i < categorical.length; i++) {
                groups.computeIfAbsent(categorical[i], k -> new ArrayList<>()).add(target[i]);
            }
            
            double globalMean = Arrays.stream(target).sum() / target.length;
            double[] encoded = new double[categorical.length];
            
            for (int i = 0; i < categorical.length; i++) {
                List<Double> values = groups.get(categorical[i]);
                double localMean = values.stream().mapToDouble(Double::doubleValue).average().orElse(globalMean);
                double count = values.size();
                double smoothFactor = count / (count + 10);
                encoded[i] = smoothFactor * localMean + (1 - smoothFactor) * globalMean;
            }
            return encoded;
        }
        
        public static double[] frequencyEncode(String[] data) {
            Map<String, Long> counts = Arrays.stream(data)
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));
            long total = data.length;
            double[] encoded = new double[data.length];
            for (int i = 0; i < data.length; i++) {
                encoded[i] = (double) counts.get(data[i]) / total;
            }
            return encoded;
        }
    }
    
    public static class Imputer {
        public static double[] imputeMean(double[] data) {
            double mean = Arrays.stream(data).filter(d -> !Double.isNaN(d) && !Double.isInfinite(d)).sum();
            int count = (int) Arrays.stream(data).filter(d -> !Double.isNaN(d) && !Double.isInfinite(d)).count();
            mean /= count;
            return Arrays.stream(data).map(d -> Double.isNaN(d) || Double.isInfinite(d) ? mean : d).toArray();
        }
        
        public static double[] imputeMedian(double[] data) {
            double[] sorted = Arrays.stream(data).filter(d -> !Double.isNaN(d) && !Double.isInfinite(d)).toArray();
            Arrays.sort(sorted);
            double median = sorted.length > 0 ? sorted[sorted.length / 2] : 0;
            return Arrays.stream(data).map(d -> Double.isNaN(d) || Double.isInfinite(d) ? median : d).toArray();
        }
        
        public static double[] imputeForwardFill(double[] data) {
            double[] result = data.clone();
            Double lastValid = null;
            for (int i = 0; i < result.length; i++) {
                if (!Double.isNaN(result[i]) && !Double.isInfinite(result[i])) {
                    lastValid = result[i];
                } else if (lastValid != null) {
                    result[i] = lastValid;
                }
            }
            return result;
        }
        
        public static double[] imputeBackwardFill(double[] data) {
            double[] result = data.clone();
            Double nextValid = null;
            for (int i = result.length - 1; i >= 0; i--) {
                if (!Double.isNaN(result[i]) && !Double.isInfinite(result[i])) {
                    nextValid = result[i];
                } else if (nextValid != null) {
                    result[i] = nextValid;
                }
            }
            return result;
        }
    }
    
    public static class Binner {
        public static int[] equalWidthBins(double[] data, int numBins) {
            double min = Arrays.stream(data).min().orElse(0);
            double max = Arrays.stream(data).max().orElse(1);
            double width = (max - min) / numBins;
            
            int[] bins = new int[data.length];
            for (int i = 0; i < data.length; i++) {
                bins[i] = Math.min(numBins - 1, (int) ((data[i] - min) / width));
            }
            return bins;
        }
        
        public static int[] equalFrequencyBins(double[] data, int numBins) {
            double[] sorted = data.clone();
            Arrays.sort(sorted);
            
            int perBin = sorted.length / numBins;
            int[] bins = new int[data.length];
            
            for (int i = 0; i < data.length; i++) {
                for (int b = 0; b < numBins; b++) {
                    int start = b * perBin;
                    int end = (b == numBins - 1) ? sorted.length : (b + 1) * perBin;
                    if (data[i] >= sorted[start] && (b == numBins - 1 || data[i] < sorted[end])) {
                        bins[i] = b;
                        break;
                    }
                }
            }
            return bins;
        }
    }
    
    public static class PolynomialFeatures {
        public static double[][] createPolynomialFeatures(double[] x, int degree) {
            double[][] features = new double[x.length][degree + 1];
            for (int i = 0; i < x.length; i++) {
                for (int d = 0; d <= degree; d++) {
                    features[i][d] = Math.pow(x[i], d);
                }
            }
            return features;
        }
        
        public static double[][] createInteractionFeatures(double[] x1, double[] x2) {
            double[][] features = new double[x1.length][4];
            for (int i = 0; i < x1.length; i++) {
                features[i][0] = 1;
                features[i][1] = x1[i];
                features[i][2] = x2[i];
                features[i][3] = x1[i] * x2[i];
            }
            return features;
        }
    }
    
    public static class FeatureSelector {
        public static List<Integer> varianceThreshold(double[][] data, double threshold) {
            List<Integer> selected = new ArrayList<>();
            for (int j = 0; j < data[0].length; j++) {
                double mean = 0;
                for (int i = 0; i < data.length; i++) mean += data[i][j];
                mean /= data.length;
                
                double variance = 0;
                for (int i = 0; i < data.length; i++) variance += Math.pow(data[i][j] - mean, 2);
                variance /= data.length;
                
                if (variance >= threshold) selected.add(j);
            }
            return selected;
        }
        
        public static List<Integer> correlationThreshold(double[][] data, double threshold) {
            List<Integer> selected = new ArrayList<>();
            selected.add(0);
            
            for (int j = 1; j < data[0].length; j++) {
                boolean independent = true;
                for (int k : selected) {
                    double corr = Math.abs(pearsonCorrelation(data, j, k));
                    if (corr >= threshold) {
                        independent = false;
                        break;
                    }
                }
                if (independent) selected.add(j);
            }
            return selected;
        }
        
        private static double pearsonCorrelation(double[][] data, int col1, int col2) {
            int n = data.length;
            double mean1 = 0, mean2 = 0;
            for (int i = 0; i < n; i++) {
                mean1 += data[i][col1];
                mean2 += data[i][col2];
            }
            mean1 /= n;
            mean2 /= n;
            
            double sum = 0, sum1 = 0, sum2 = 0;
            for (int i = 0; i < n; i++) {
                double d1 = data[i][col1] - mean1;
                double d2 = data[i][col2] - mean2;
                sum += d1 * d2;
                sum1 += d1 * d1;
                sum2 += d2 * d2;
            }
            
            return sum / Math.sqrt(sum1 * sum2);
        }
    }
    
    public static class PCA {
        private double[][] components;
        private double[] explainedVariance;
        
        public void fit(double[][] data, int nComponents) {
            int m = data[0].length;
            double[] means = new double[m];
            
            for (int j = 0; j < m; j++) {
                for (int i = 0; i < data.length; i++) {
                    means[j] += data[i][j];
                }
                means[j] /= data.length;
            }
            
            double[][] centered = new double[data.length][m];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < m; j++) {
                    centered[i][j] = data[i][j] - means[j];
                }
            }
            
            double[][] covariance = new double[m][m];
            for (int i = 0; i < m; i++) {
                for (int j = i; j < m; j++) {
                    double cov = 0;
                    for (int k = 0; k < data.length; k++) {
                        cov += centered[k][i] * centered[k][j];
                    }
                    cov /= (data.length - 1);
                    covariance[i][j] = cov;
                    covariance[j][i] = cov;
                }
            }
            
            components = powerIteration(covariance, nComponents);
            
            explainedVariance = new double[nComponents];
            double totalVar = Arrays.stream(covariance).flatMapToDouble(row -> Arrays.stream(row)).map(Math::abs).sum();
            for (int i = 0; i < nComponents; i++) {
                double var = 0;
                for (int j = 0; j < m; j++) {
                    var += covariance[j][j];
                }
                explainedVariance[i] = var / totalVar;
            }
        }
        
        private double[][] powerIteration(double[][] matrix, int n) {
            Random rand = new Random(42);
            double[][] vectors = new double[matrix.length][n];
            
            for (int k = 0; k < n; k++) {
                double[] v = new double[matrix.length];
                for (int i = 0; i < v.length; i++) v[i] = rand.nextDouble() - 0.5;
                
                for (int iter = 0; iter < 100; iter++) {
                    double[] newV = new double[v.length];
                    for (int i = 0; i < matrix.length; i++) {
                        for (int j = 0; j < matrix.length; j++) {
                            newV[i] += matrix[i][j] * v[j];
                        }
                    }
                    double norm = Math.sqrt(Arrays.stream(newV).map(x -> x * x).sum());
                    if (norm > 0) {
                        for (int i = 0; i < v.length; i++) v[i] = newV[i] / norm;
                    }
                }
                for (int i = 0; i < vectors.length; i++) vectors[i][k] = v[i];
            }
            return vectors;
        }
        
        public double[][] transform(double[][] data) {
            int nComponents = components[0].length;
            double[][] result = new double[data.length][nComponents];
            
            for (int i = 0; i < data.length; i++) {
                for (int c = 0; c < nComponents; c++) {
                    double sum = 0;
                    for (int j = 0; j < data[0].length; j++) {
                        sum += data[i][j] * components[j][c];
                    }
                    result[i][c] = sum;
                }
            }
            return result;
        }
        
        public double[] getExplainedVariance() {
            return explainedVariance;
        }
    }
    
    public static class TextFeatures {
        public static Map<String, Integer> bagOfWords(String[] documents) {
            Map<String, Integer> vocab = new LinkedHashMap<>();
            for (String doc : documents) {
                for (String word : doc.toLowerCase().split("\\W+")) {
                    vocab.putIfAbsent(word, vocab.size());
                }
            }
            return vocab;
        }
        
        public static double[][] tfidf(String[] documents) {
            Map<String, Integer> vocab = bagOfWords(documents);
            int vocabSize = vocab.size();
            double[][] tfidf = new double[documents.length][vocabSize];
            
            double[] idf = new double[vocabSize];
            for (String word : vocab.keySet()) {
                int docCount = 0;
                for (String doc : documents) {
                    if (doc.toLowerCase().contains(word)) docCount++;
                }
                idf[vocab.get(word)] = Math.log((double) documents.length / (1 + docCount));
            }
            
            for (int i = 0; i < documents.length; i++) {
                String[] words = documents[i].toLowerCase().split("\\W+");
                Map<String, Integer> counts = new HashMap<>();
                for (String word : words) {
                    if (vocab.containsKey(word)) {
                        counts.merge(word, 1, Integer::sum);
                    }
                }
                
                double maxCount = counts.values().stream().mapToInt(Integer::intValue).max().orElse(1);
                for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                    int idx = vocab.get(entry.getKey());
                    tfidf[i][idx] = (entry.getValue() / maxCount) * idf[idx];
                }
            }
            return tfidf;
        }
    }
    
    public static class DateTimeFeatures {
        public static double[] extractFeatures(java.time.LocalDate date) {
            return new double[]{
                date.getYear(),
                date.getMonthValue(),
                date.getDayOfMonth(),
                date.getDayOfWeek().getValue(),
                date.getDayOfYear(),
                isWeekend(date) ? 1 : 0,
                isQuarterStart(date) ? 1 : 0
            };
        }
        
        private static boolean isWeekend(java.time.LocalDate date) {
            return date.getDayOfWeek().getValue() >= 6;
        }
        
        private static boolean isQuarterStart(java.time.LocalDate date) {
            return date.getMonthValue() % 3 == 1;
        }
        
        public static double daysSince(java.time.LocalDate date, java.time.LocalDate reference) {
            return java.time.temporal.ChronoUnit.DAYS.between(reference, date);
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Feature Engineering Demo ===\n");
        
        double[] data = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
        System.out.println("Original data: " + Arrays.toString(data));
        
        System.out.println("\n--- Standardization ---");
        double[] standardized = Scaler.standardize(data);
        System.out.println(Arrays.toString(standardized));
        
        System.out.println("\n--- Min-Max Scaling ---");
        double[] scaled = Scaler.minMaxScale(data);
        System.out.println(Arrays.toString(scaled));
        
        System.out.println("\n--- Label Encoding ---");
        String[] categories = {"cat", "dog", "bird", "cat", "bird"};
        int[] encoded = Encoder.labelEncode(categories);
        System.out.println(Arrays.toString(encoded));
        
        System.out.println("\n--- One-Hot Encoding ---");
        double[][] oneHot = Encoder.oneHotEncode(categories);
        System.out.println("Categories: " + Arrays.toString(categories));
        for (double[] row : oneHot) {
            System.out.println(Arrays.toString(row));
        }
        
        System.out.println("\n--- Equal Width Binning ---");
        int[] bins = Binner.equalWidthBins(data, 4);
        System.out.println(Arrays.toString(bins));
        
        System.out.println("\n--- Missing Value Imputation ---");
        double[] withMissing = {10, Double.NaN, 30, Double.NaN, 50};
        double[] imputed = Imputer.imputeMean(withMissing);
        System.out.println("With NaN: " + Arrays.toString(withMissing));
        System.out.println("Imputed: " + Arrays.toString(imputed));
    }
}