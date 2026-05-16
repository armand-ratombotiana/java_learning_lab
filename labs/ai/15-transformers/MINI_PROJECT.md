# Transformers - MINI PROJECT

## Project: Text Classification with Transformer Encoder

Build a transformer-based text classifier for sentiment analysis.

### Implementation

```java
public class TextClassifier {
    private TransformerEncoderBlock encoder;
    private double[] classificationHead;
    private int vocabSize;
    private int dModel = 128;
    private int numHeads = 4;
    private int numLayers = 2;
    private double learningRate = 0.001;
    
    public TextClassifier(int vocabSize) {
        this.vocabSize = vocabSize;
        
        this.encoder = new TransformerEncoderBlock(dModel, numHeads, dModel * 4);
        
        this.classificationHead = new double[dModel];
        
        initializeWeights();
    }
    
    private void initializeWeights() {
        Random rand = new Random(42);
        
        for (int i = 0; i < dModel; i++) {
            classificationHead[i] = (rand.nextDouble() - 0.5) * 0.1;
        }
    }
    
    public double predict(int[] tokens) {
        double[][] embeddings = getEmbeddings(tokens);
        
        double[][] encoderOutput = embeddings;
        
        for (int i = 0; i < numLayers; i++) {
            encoderOutput = encoder.forward(encoderOutput, null);
        }
        
        double[] pooled = pool(encoderOutput);
        
        double score = 0;
        
        for (int i = 0; i < dModel; i++) {
            score += classificationHead[i] * pooled[i];
        }
        
        return sigmoid(score);
    }
    
    private double[][] getEmbeddings(int[] tokens) {
        double[][] embeddings = new double[tokens.length][dModel];
        
        for (int i = 0; i < tokens.length; i++) {
            for (int j = 0; j < dModel; j++) {
                embeddings[i][j] = Math.sin(tokens[i] * Math.pow(10000, -j / (double) dModel));
            }
        }
        
        PositionalEncoding pe = new PositionalEncoding(dModel, 512);
        return pe.encode(embeddings);
    }
    
    private double[] pool(double[][] hidden) {
        double[] pooled = new double[dModel];
        
        for (int j = 0; j < dModel; j++) {
            double sum = 0;
            
            for (int i = 0; i < hidden.length; i++) {
                sum += hidden[i][j];
            }
            
            pooled[j] = sum / hidden.length;
        }
        
        return pooled;
    }
    
    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }
    
    public void train(int[][] sequences, int[] labels, int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            double totalLoss = 0;
            
            for (int i = 0; i < sequences.length; i++) {
                double prediction = predict(sequences[i]);
                double target = labels[i];
                
                double loss = -target * Math.log(prediction + 1e-10) - 
                            (1 - target) * Math.log(1 - prediction + 1e-10);
                
                totalLoss += loss;
                
                updateWeights(prediction - target);
            }
            
            double avgLoss = totalLoss / sequences.length;
            
            System.out.println("Epoch " + (epoch + 1) + ", Loss: " + 
                             String.format("%.4f", avgLoss));
        }
    }
    
    private void updateWeights(double error) {
        double gradient = error * learningRate;
        
        for (int i = 0; i < dModel; i++) {
            classificationHead[i] -= gradient;
        }
    }
}
```

### Tokenizer

```java
public class SimpleTokenizer {
    private Map<String, Integer> vocab;
    private int vocabSize;
    
    public SimpleTokenizer(int vocabSize) {
        this.vocabSize = vocabSize;
        this.vocab = new HashMap<>();
        
        vocab.put("<PAD>", 0);
        vocab.put("<UNK>", 1);
        vocab.put("<CLS>", 2);
        vocab.put("<SEP>", 3);
    }
    
    public void buildVocabulary(List<String> texts) {
        Map<String, Integer> freq = new HashMap<>();
        
        for (String text : texts) {
            String[] tokens = text.toLowerCase().split("\\s+");
            
            for (String token : tokens) {
                freq.merge(token, 1, Integer::sum);
            }
        }
        
        freq.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(vocabSize - 4)
            .forEach(entry -> vocab.put(entry.getKey(), vocab.size()));
    }
    
    public int[] encode(String text) {
        String[] tokens = text.toLowerCase().split("\\s+");
        int[] encoded = new int[tokens.length];
        
        for (int i = 0; i < tokens.length; i++) {
            encoded[i] = vocab.getOrDefault(tokens[i], 1);
        }
        
        return encoded;
    }
    
    public String decode(int[] tokens) {
        StringBuilder sb = new StringBuilder();
        
        for (int token : tokens) {
            if (token == 0) continue;
            
            String word = vocab.entrySet().stream()
                .filter(e -> e.getValue() == token)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("<UNK>");
            
            sb.append(word).append(" ");
        }
        
        return sb.toString().trim();
    }
}
```

### Data Augmentation

```java
public class TextAugmentation {
    private Random random = new Random(42);
    
    public String augment(String text) {
        String[] tokens = text.split("\\s+");
        
        if (random.nextBoolean()) {
            tokens = synonymReplacement(tokens, 1);
        }
        
        if (random.nextBoolean()) {
            tokens = randomDeletion(tokens, 0.1);
        }
        
        if (random.nextBoolean()) {
            tokens = randomSwap(tokens, 1);
        }
        
        return String.join(" ", tokens);
    }
    
    private String[] synonymReplacement(String[] tokens, int n) {
        List<String> replaceable = new ArrayList<>();
        
        for (String token : tokens) {
            if (token.length() > 3) {
                replaceable.add(token);
            }
        }
        
        for (int i = 0; i < n && !replaceable.isEmpty(); i++) {
            int idx = random.nextInt(replaceable.size());
            tokens[findToken(tokens, replaceable.get(idx))] = "<UNK>";
            replaceable.remove(idx);
        }
        
        return tokens;
    }
    
    private int findToken(String[] tokens, String target) {
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equals(target)) {
                return i;
            }
        }
        return 0;
    }
    
    private String[] randomDeletion(String[] tokens, double p) {
        if (tokens.length == 1) return tokens;
        
        String[] result = Arrays.stream(tokens)
            .filter(t -> random.nextDouble() > p || tokens.length == 1)
            .toArray(String[]::new);
        
        return result.length == 0 ? new String[]{tokens[random.nextInt(tokens.length)]} : result;
    }
    
    private String[] randomSwap(String[] tokens, int n) {
        String[] result = tokens.clone();
        
        for (int i = 0; i < n; i++) {
            int idx1 = random.nextInt(result.length);
            int idx2 = random.nextInt(result.length);
            
            String temp = result[idx1];
            result[idx1] = result[idx2];
            result[idx2] = temp;
        }
        
        return result;
    }
}
```

### Main Training

```java
public class TrainClassifier {
    public static void main(String[] args) {
        List<String> trainTexts = Arrays.asList(
            "I love this product",
            "This is terrible",
            "Great experience",
            "Very disappointing",
            "Amazing quality",
            "Worst purchase ever",
            "Highly recommend",
            "Not worth money"
        );
        
        List<Integer> trainLabels = Arrays.asList(1, 0, 1, 0, 1, 0, 1, 0);
        
        SimpleTokenizer tokenizer = new SimpleTokenizer(1000);
        tokenizer.buildVocabulary(trainTexts);
        
        int[][] sequences = new int[trainTexts.size()][];
        
        for (int i = 0; i < trainTexts.size(); i++) {
            sequences[i] = tokenizer.encode(trainTexts.get(i));
        }
        
        TextClassifier classifier = new TextClassifier(1000);
        
        int[] labels = trainLabels.stream().mapToInt(Integer::intValue).toArray();
        
        classifier.train(sequences, labels, 20);
        
        String testText = "This is amazing";
        int[] testTokens = tokenizer.encode(testText);
        
        double prediction = classifier.predict(testTokens);
        
        System.out.println("Prediction for '" + testText + "': " + 
                         (prediction > 0.5 ? "POSITIVE" : "NEGATIVE"));
        System.out.println("Confidence: " + String.format("%.2f", prediction));
    }
}
```

## Deliverables

- [ ] Implement transformer encoder block
- [ ] Add positional encoding
- [ ] Build token embedding layer
- [ ] Create classification head
- [ ] Train on sentiment data
- [ ] Evaluate with accuracy metric
- [ ] Add text augmentation