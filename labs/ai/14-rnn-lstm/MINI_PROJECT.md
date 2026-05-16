# RNN & LSTM - MINI PROJECT

## Project: Stock Price Prediction System

Build an LSTM-based system to predict stock prices from historical time series data.

### Implementation

```java
public class StockPricePredictor {
    private LSTMCell lstm;
    private double[][] outputWeight;
    private int lookback = 30;
    private int hiddenSize = 100;
    private double learningRate = 0.001;
    
    public StockPricePredictor(int featureSize) {
        this.lstm = new LSTMCell(featureSize, hiddenSize);
        this.outputWeight = new double[1][hiddenSize];
        
        initializeWeights();
    }
    
    private void initializeWeights() {
        Random rand = new Random(42);
        
        for (int i = 0; i < hiddenSize; i++) {
            outputWeight[0][i] = (rand.nextDouble() - 0.5) * 0.1;
        }
    }
    
    public double predict(double[][] sequence) {
        if (sequence.length != lookback) {
            throw new IllegalArgumentException("Sequence length must be " + lookback);
        }
        
        double[] h = new double[hiddenSize];
        double[] c = new double[hiddenSize];
        
        for (int t = 0; t < lookback; t++) {
            LSTMState state = lstm.forward(sequence[t], h, c);
            h = state.h;
            c = state.c;
        }
        
        double prediction = 0;
        
        for (int i = 0; i < hiddenSize; i++) {
            prediction += outputWeight[0][i] * h[i];
        }
        
        return prediction;
    }
    
    public void train(List<double[][]> sequences, List<Double> targets, int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            double totalError = 0;
            
            for (int i = 0; i < sequences.size(); i++) {
                double prediction = predict(sequences.get(i));
                double target = targets.get(i);
                double error = target - prediction;
                
                totalError += error * error;
                
                updateWeights(error);
            }
            
            double rmse = Math.sqrt(totalError / sequences.size());
            
            System.out.println("Epoch " + (epoch + 1) + 
                             ", RMSE: " + String.format("%.4f", rmse));
        }
    }
    
    private void updateWeights(double error) {
        double gradient = error * learningRate;
        
        for (int i = 0; i < hiddenSize; i++) {
            outputWeight[0][i] += gradient;
        }
    }
    
    public double[] forecast(double[] recentPrices, int days) {
        double[] forecasts = new double[days];
        double[][] inputWindow = new double[lookback][];
        
        for (int i = 0; i < lookback; i++) {
            inputWindow[i] = new double[]{recentPrices[i]};
        }
        
        for (int day = 0; day < days; day++) {
            double prediction = predict(inputWindow);
            forecasts[day] = prediction;
            
            System.arraycopy(inputWindow, 1, inputWindow, 0, lookback - 1);
            inputWindow[lookback - 1] = new double[]{prediction};
        }
        
        return forecasts;
    }
}
```

### Data Normalization

```java
public class DataNormalizer {
    private double min;
    private double max;
    
    public void fit(double[] data) {
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;
        
        for (double v : data) {
            if (v < min) min = v;
            if (v > max) max = v;
        }
    }
    
    public double[] transform(double[] data) {
        double[] normalized = new double[data.length];
        
        for (int i = 0; i < data.length; i++) {
            normalized[i] = (data[i] - min) / (max - min);
        }
        
        return normalized;
    }
    
    public double inverseTransform(double value) {
        return value * (max - min) + min;
    }
    
    public double[][][] normalizeSequences(double[][] sequences) {
        double[][][] normalized = new double[sequences.length][][];
        
        List<Double> allValues = new ArrayList<>();
        
        for (double[][] seq : sequences) {
            for (double[] frame : seq) {
                for (double v : frame) {
                    allValues.add(v);
                }
            }
        }
        
        double[] flat = allValues.stream().mapToDouble(Double::doubleValue).toArray();
        fit(flat);
        
        for (int i = 0; i < sequences.length; i++) {
            normalized[i] = new double[sequences[i].length][];
            
            for (int j = 0; j < sequences[i].length; j++) {
                normalized[i][j] = transform(sequences[i][j]);
            }
        }
        
        return normalized;
    }
}
```

### Main Training Loop

```java
public class TrainStockModel {
    public static void main(String[] args) {
        StockPricePredictor predictor = new StockPricePredictor(1);
        
        double[] rawData = generateSyntheticData(1000);
        
        DataNormalizer normalizer = new DataNormalizer();
        double[] normalized = normalizer.transform(rawData);
        
        List<double[][]> sequences = new ArrayList<>();
        List<Double> targets = new ArrayList<>();
        
        int lookback = 30;
        
        for (int i = 0; i < normalized.length - lookback - 1; i++) {
            double[][] sequence = new double[lookback][];
            
            for (int j = 0; j < lookback; j++) {
                sequence[j] = new double[]{normalized[i + j]};
            }
            
            sequences.add(sequence);
            targets.add(normalized[i + lookback]);
        }
        
        predictor.train(sequences, targets, 20);
        
        double[] recentPrices = new double[30];
        
        for (int i = 0; i < 30; i++) {
            recentPrices[i] = normalized[normalized.length - 30 + i];
        }
        
        double[] forecast = predictor.forecast(recentPrices, 5);
        
        System.out.println("5-day forecast (raw):");
        
        for (int i = 0; i < forecast.length; i++) {
            double actual = normalizer.inverseTransform(forecast[i]);
            System.out.println("Day " + (i + 1) + ": " + String.format("%.2f", actual));
        }
    }
    
    private static double[] generateSyntheticData(int length) {
        Random rand = new Random(42);
        double[] data = new double[length];
        
        double price = 100.0;
        
        for (int i = 0; i < length; i++) {
            price += (rand.nextDouble() - 0.5) * 5;
            price = Math.max(10, price);
            
            data[i] = price;
        }
        
        return data;
    }
}
```

### Evaluation Metrics

```java
public class EvaluationMetrics {
    public static double mse(double[] predictions, double[] targets) {
        double sum = 0;
        
        for (int i = 0; i < predictions.length; i++) {
            double diff = predictions[i] - targets[i];
            sum += diff * diff;
        }
        
        return sum / predictions.length;
    }
    
    public static double rmse(double[] predictions, double[] targets) {
        return Math.sqrt(mse(predictions, targets));
    }
    
    public static double mae(double[] predictions, double[] targets) {
        double sum = 0;
        
        for (int i = 0; i < predictions.length; i++) {
            sum += Math.abs(predictions[i] - targets[i]);
        }
        
        return sum / predictions.length;
    }
    
    public static double mape(double[] predictions, double[] targets) {
        double sum = 0;
        
        for (int i = 0; i < predictions.length; i++) {
            if (targets[i] != 0) {
                sum += Math.abs((predictions[i] - targets[i]) / targets[i]);
            }
        }
        
        return sum / predictions.length * 100;
    }
    
    public static double directionalAccuracy(double[] predictions, double[] targets) {
        int correct = 0;
        
        for (int i = 1; i < predictions.length; i++) {
            boolean predUp = predictions[i] > predictions[i - 1];
            boolean actualUp = targets[i] > targets[i - 1];
            
            if (predUp == actualUp) {
                correct++;
            }
        }
        
        return (double) correct / (predictions.length - 1);
    }
}
```

## Deliverables

- [ ] Implement LSTM cell with 4 gates
- [ ] Build time series predictor with lookback window
- [ ] Add data normalization
- [ ] Train on stock price data
- [ ] Generate multi-day forecasts
- [ ] Calculate evaluation metrics (RMSE, MAE, MAPE)
- [ ] Visualize predictions vs actual