# Embeddings - CODE DEEP DIVE

## Java Implementations

### 1. Word2Vec Skip-Gram Model

```java
public class SkipGramModel {
    private double[][] inputEmbeddings;
    private double[][] outputEmbeddings;
    private int vocabularySize;
    private int embeddingSize;
    private double learningRate = 0.025;
    
    public SkipGramModel(int vocabSize, int embeddingSize) {
        this.vocabularySize = vocabSize;
        this.embeddingSize = embeddingSize;
        
        this.inputEmbeddings = createRandomMatrix(vocabSize, embeddingSize);
        this.outputEmbeddings = createRandomMatrix(vocabSize, embeddingSize);
    }
    
    private double[][] createRandomMatrix(int rows, int cols) {
        double[][] matrix = new double[rows][cols];
        Random rand = new Random(42);
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = (rand.nextDouble() - 0.5) * 0.5;
            }
        }
        
        return matrix;
    }
    
    public void train(int[] centerWords, int[] contextWords) {
        for (int i = 0; i < centerWords.length; i++) {
            int centerWord = centerWords[i];
            
            double[] centerEmbedding = inputEmbeddings[centerWord];
            
            double[] outputLayer = new double[vocabularySize];
            
            for (int j = 0; j < vocabularySize; j++) {
                outputLayer[j] = dotProduct(centerEmbedding, outputEmbeddings[j]);
            }
            
            double[] probs = softmax(outputLayer);
            
            for (int j = 0; j < contextWords.length; j++) {
                int targetWord = contextWords[j];
                
                double error = 1.0 - probs[targetWord];
                
                updateEmbeddings(centerWord, targetWord, error);
            }
        }
    }
    
    private double dotProduct(double[] a, double[] b) {
        double sum = 0;
        
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        
        return sum;
    }
    
    private double[] softmax(double[] x) {
        double max = x[0];
        
        for (int i = 1; i < x.length; i++) {
            if (x[i] > max) max = x[i];
        }
        
        double sum = 0;
        double[] exp = new double[x.length];
        
        for (int i = 0; i < x.length; i++) {
            exp[i] = Math.exp(x[i] - max);
            sum += exp[i];
        }
        
        double[] result = new double[x.length];
        
        for (int i = 0; i < x.length; i++) {
            result[i] = exp[i] / sum;
        }
        
        return result;
    }
    
    private void updateEmbeddings(int centerIdx, int targetIdx, double error) {
        double[] gradInput = new double[embeddingSize];
        
        for (int i = 0; i < embeddingSize; i++) {
            gradInput[i] = error * outputEmbeddings[targetIdx][i] * learningRate;
            inputEmbeddings[centerIdx][i] += gradInput[i];
        }
        
        for (int i = 0; i < embeddingSize; i++) {
            outputEmbeddings[targetIdx][i] += error * inputEmbeddings[centerIdx][i] * learningRate;
        }
    }
    
    public double[] getWordEmbedding(int wordIndex) {
        return inputEmbeddings[wordIndex];
    }
    
    public int[] mostSimilar(int wordIndex, int topK) {
        double[] targetEmbedding = inputEmbeddings[wordIndex];
        
        double[] similarities = new double[vocabularySize];
        
        for (int i = 0; i < vocabularySize; i++) {
            similarities[i] = cosineSimilarity(targetEmbedding, inputEmbeddings[i]);
        }
        
        Integer[] indices = new Integer[vocabularySize];
        
        for (int i = 0; i < vocabularySize; i++) {
            indices[i] = i;
        }
        
        Arrays.sort(indices, (a, b) -> Double.compare(similarities[b], similarities[a]));
        
        int[] result = new int[topK];
        
        for (int i = 0; i < topK; i++) {
            result[i] = indices[i];
        }
        
        return result;
    }
    
    private double cosineSimilarity(double[] a, double[] b) {
        double dot = dotProduct(a, b);
        double normA = Math.sqrt(dotProduct(a, a));
        double normB = Math.sqrt(dotProduct(b, b));
        
        return dot / (normA * normB + 1e-10);
    }
}
```

### 2. CBOW Model

```java
public class CBOWModel {
    private double[][] inputEmbeddings;
    private double[][] outputEmbeddings;
    private int vocabularySize;
    private int embeddingSize;
    private int windowSize;
    
    public CBOWModel(int vocabSize, int embeddingSize, int windowSize) {
        this.vocabularySize = vocabSize;
        this.embeddingSize = embeddingSize;
        this.windowSize = windowSize;
        
        this.inputEmbeddings = createRandomMatrix(vocabSize, embeddingSize);
        this.outputEmbeddings = createRandomMatrix(vocabSize, embeddingSize);
    }
    
    private double[][] createRandomMatrix(int rows, int cols) {
        double[][] matrix = new double[rows][cols];
        Random rand = new Random(42);
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = (rand.nextDouble() - 0.5) * 0.5;
            }
        }
        
        return matrix;
    }
    
    public void train(int[][] contextWords, int[] targetWords) {
        for (int i = 0; i < contextWords.length; i++) {
            double[] contextEmbedding = averageContext(contextWords[i]);
            
            double[] outputLayer = new double[vocabularySize];
            
            for (int j = 0; j < vocabularySize; j++) {
                outputLayer[j] = dotProduct(contextEmbedding, outputEmbeddings[j]);
            }
            
            double[] probs = softmax(outputLayer);
            
            int targetWord = targetWords[i];
            double error = 1.0 - probs[targetWord];
            
            updateEmbeddings(contextWords[i], targetWord, error);
        }
    }
    
    private double[] averageContext(int[] contextWords) {
        double[] avg = new double[embeddingSize];
        
        for (int word : contextWords) {
            for (int i = 0; i < embeddingSize; i++) {
                avg[i] += inputEmbeddings[word][i];
            }
        }
        
        for (int i = 0; i < embeddingSize; i++) {
            avg[i] /= contextWords.length;
        }
        
        return avg;
    }
    
    private double dotProduct(double[] a, double[] b) {
        double sum = 0;
        
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        
        return sum;
    }
    
    private double[] softmax(double[] x) {
        double max = x[0];
        
        for (int i = 1; i < x.length; i++) {
            if (x[i] > max) max = x[i];
        }
        
        double sum = 0;
        
        for (int i = 0; i < x.length; i++) {
            sum += Math.exp(x[i] - max);
        }
        
        double[] result = new double[x.length];
        
        for (int i = 0; i < x.length; i++) {
            result[i] = Math.exp(x[i] - max) / sum;
        }
        
        return result;
    }
    
    private void updateEmbeddings(int[] contextWords, int targetWord, double error) {
        double[] contextAvg = averageContext(contextWords);
        
        for (int word : contextWords) {
            for (int i = 0; i < embeddingSize; i++) {
                inputEmbeddings[word][i] += error * outputEmbeddings[targetWord][i] * 0.01;
            }
        }
        
        for (int i = 0; i < embeddingSize; i++) {
            outputEmbeddings[targetWord][i] += error * contextAvg[i] * 0.01;
        }
    }
}
```

### 3. Semantic Search with Embeddings

```java
public class SemanticSearchEngine {
    private Map<Integer, double[]> documentEmbeddings;
    private Map<String, Integer> docIndex;
    private int embeddingSize = 128;
    
    public SemanticSearchEngine() {
        this.documentEmbeddings = new HashMap<>();
        this.docIndex = new HashMap<>();
    }
    
    public void indexDocument(String docId, String content) {
        double[] embedding = computeDocumentEmbedding(content);
        
        int index = documentEmbeddings.size();
        
        documentEmbeddings.put(index, embedding);
        docIndex.put(docId, index);
    }
    
    private double[] computeDocumentEmbedding(String content) {
        String[] tokens = content.toLowerCase().split("\\s+");
        
        double[] embedding = new double[embeddingSize];
        
        for (String token : tokens) {
            double[] tokenEmbed = getTokenEmbedding(token);
            
            for (int i = 0; i < embeddingSize; i++) {
                embedding[i] += tokenEmbed[i];
            }
        }
        
        for (int i = 0; i < embeddingSize; i++) {
            embedding[i] /= tokens.length;
        }
        
        return embedding;
    }
    
    private double[] getTokenEmbedding(String token) {
        double[] embed = new double[embeddingSize];
        
        for (int i = 0; i < embeddingSize; i++) {
            embed[i] = Math.sin(token.hashCode() * Math.pow(10000, -i / (double) embeddingSize));
        }
        
        return embed;
    }
    
    public List<SearchResult> search(String query, int topK) {
        double[] queryEmbedding = computeDocumentEmbedding(query);
        
        List<ScoredDocument> scores = new ArrayList<>();
        
        for (Map.Entry<Integer, double[]> entry : documentEmbeddings.entrySet()) {
            double similarity = cosineSimilarity(queryEmbedding, entry.getValue());
            
            scores.add(new ScoredDocument(entry.getKey(), similarity));
        }
        
        scores.sort((a, b) -> Double.compare(b.score, a.score));
        
        List<SearchResult> results = new ArrayList<>();
        
        for (int i = 0; i < Math.min(topK, scores.size()); i++) {
            int docIndex = scores.get(i).docIndex;
            
            String docId = docIndex.entrySet().stream()
                .filter(e -> e.getValue() == docIndex)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("");
            
            results.add(new SearchResult(docId, scores.get(i).score));
        }
        
        return results;
    }
    
    private double cosineSimilarity(double[] a, double[] b) {
        double dot = 0;
        double normA = 0;
        double normB = 0;
        
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        
        return dot / (Math.sqrt(normA) * Math.sqrt(normB) + 1e-10);
    }
}

class ScoredDocument {
    int docIndex;
    double score;
    
    public ScoredDocument(int docIndex, double score) {
        this.docIndex = docIndex;
        this.score = score;
    }
}

class SearchResult {
    String docId;
    double score;
    
    public SearchResult(String docId, double score) {
        this.docId = docId;
        this.score = score;
    }
}
```

### 4. GloVe-style Embedding Training

```java
public class GloVeTrainer {
    private double[][] embeddings;
    private int vocabularySize;
    private int embeddingSize;
    private double[][] cooccurrenceMatrix;
    private double xMax = 100;
    private double alpha = 0.75;
    
    public GloVeTrainer(int vocabSize, int embedSize) {
        this.vocabularySize = vocabSize;
        this.embeddingSize = embedSize;
        
        this.embeddings = createRandomMatrix(vocabSize, embedSize * 2);
    }
    
    private double[][] createRandomMatrix(int rows, int cols) {
        double[][] matrix = new double[rows][cols];
        Random rand = new Random(42);
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = (rand.nextDouble() - 0.5) * 0.5;
            }
        }
        
        return matrix;
    }
    
    public void setCooccurrenceMatrix(double[][] matrix) {
        this.cooccurrenceMatrix = matrix;
    }
    
    public void train(int epochs, double learningRate) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            double totalLoss = 0;
            
            for (int i = 0; i < vocabularySize; i++) {
                for (int j = 0; j < vocabularySize; j++) {
                    if (cooccurrenceMatrix[i][j] > 0) {
                        double loss = optimizePair(i, j, learningRate);
                        totalLoss += loss;
                    }
                }
            }
            
            System.out.println("Epoch " + (epoch + 1) + ", Loss: " + totalLoss);
        }
    }
    
    private double optimizePair(int i, int j, double learningRate) {
        double[] wi = Arrays.copyOfRange(embeddings[i], 0, embeddingSize);
        double[] wj = Arrays.copyOfRange(embeddings[j], 0, embeddingSize);
        double[] bi = embeddings[i];
        double[] bj = embeddings[j];
        
        double xij = cooccurrenceMatrix[i][j];
        double weight = computeWeight(xij);
        
        double logXij = Math.log(xij + 1e-10);
        
        double prediction = dotProduct(wi, wj) + bi[0] + bj[0];
        double diff = prediction - logXij;
        
        double loss = weight * diff * diff;
        
        double[] grad = new double[embeddingSize];
        
        for (int k = 0; k < embeddingSize; k++) {
            grad[k] = diff * wj[k] * weight * learningRate;
            
            embeddings[i][k] -= grad[k];
            embeddings[j][k] -= diff * wi[k] * weight * learningRate;
        }
        
        return loss;
    }
    
    private double computeWeight(double x) {
        if (x < xMax) {
            return Math.pow(x / xMax, alpha);
        }
        
        return 1.0;
    }
    
    private double dotProduct(double[] a, double[] b) {
        double sum = 0;
        
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        
        return sum;
    }
    
    public double[] getEmbedding(int wordIndex) {
        return Arrays.copyOfRange(embeddings[wordIndex], 0, embeddingSize);
    }
}
```

### 5. Sentence Embeddings

```java
public class SentenceEncoder {
    private SkipGramModel wordModel;
    private int embeddingSize = 128;
    
    public SentenceEncoder(int vocabSize) {
        this.wordModel = new SkipGramModel(vocabSize, embeddingSize);
    }
    
    public double[] encode(String sentence) {
        String[] tokens = sentence.toLowerCase().split("\\s+");
        
        double[] embedding = new double[embeddingSize];
        
        for (String token : tokens) {
            int tokenId = getTokenId(token);
            double[] wordEmbed = wordModel.getWordEmbedding(tokenId);
            
            for (int i = 0; i < embeddingSize; i++) {
                embedding[i] += wordEmbed[i];
            }
        }
        
        for (int i = 0; i < embeddingSize; i++) {
            embedding[i] /= tokens.length;
        }
        
        return embedding;
    }
    
    public double[] encodeWithPooling(String sentence, String poolingType) {
        String[] tokens = sentence.toLowerCase().split("\\s+");
        
        List<double[]> wordEmbeddings = new ArrayList<>();
        
        for (String token : tokens) {
            int tokenId = getTokenId(token);
            wordEmbeddings.add(wordModel.getWordEmbedding(tokenId));
        }
        
        double[] embedding;
        
        switch (poolingType) {
            case "mean":
                embedding = meanPooling(wordEmbeddings);
                break;
            case "max":
                embedding = maxPooling(wordEmbeddings);
                break;
            case "cls":
                embedding = wordEmbeddings.get(0);
                break;
            default:
                embedding = meanPooling(wordEmbeddings);
        }
        
        return embedding;
    }
    
    private double[] meanPooling(List<double[]> embeddings) {
        double[] result = new double[embeddingSize];
        
        for (double[] embed : embeddings) {
            for (int i = 0; i < embeddingSize; i++) {
                result[i] += embed[i];
            }
        }
        
        for (int i = 0; i < embeddingSize; i++) {
            result[i] /= embeddings.size();
        }
        
        return result;
    }
    
    private double[] maxPooling(List<double[]> embeddings) {
        double[] result = new double[embeddingSize];
        
        Arrays.fill(result, Double.NEGATIVE_INFINITY);
        
        for (double[] embed : embeddings) {
            for (int i = 0; i < embeddingSize; i++) {
                result[i] = Math.max(result[i], embed[i]);
            }
        }
        
        return result;
    }
    
    private int getTokenId(String token) {
        return Math.abs(token.hashCode()) % 10000;
    }
    
    public double similarity(String s1, String s2) {
        double[] embed1 = encode(s1);
        double[] embed2 = encode(s2);
        
        return cosineSimilarity(embed1, embed2);
    }
    
    private double cosineSimilarity(double[] a, double[] b) {
        double dot = 0;
        double normA = 0;
        double normB = 0;
        
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        
        return dot / (Math.sqrt(normA) * Math.sqrt(normB) + 1e-10);
    }
}
```

### 6. FastText-style Subword Embeddings

```java
public class FastTextEmbeddings {
    private Map<String, double[]> subwordEmbeddings;
    private int embeddingSize = 100;
    private int minNgram = 3;
    private int maxNgram = 6;
    
    public FastTextEmbeddings() {
        this.subwordEmbeddings = new HashMap<>();
    }
    
    public double[] getWordEmbedding(String word) {
        List<String> subwords = getSubwords(word);
        
        double[] embedding = new double[embeddingSize];
        
        for (String subword : subwords) {
            double[] subwordEmbed = getSubwordEmbedding(subword);
            
            for (int i = 0; i < embeddingSize; i++) {
                embedding[i] += subwordEmbed[i];
            }
        }
        
        for (int i = 0; i < embeddingSize; i++) {
            embedding[i] /= subwords.size();
        }
        
        return embedding;
    }
    
    private List<String> getSubwords(String word) {
        List<String> subwords = new ArrayList<>();
        
        String paddedWord = "<" + word + ">";
        
        for (int n = minNgram; n <= maxNgram; n++) {
            if (paddedWord.length() < n) continue;
            
            for (int i = 0; i <= paddedWord.length() - n; i++) {
                String ngram = paddedWord.substring(i, i + n);
                subwords.add(ngram);
            }
        }
        
        subwords.add(word);
        
        return subwords;
    }
    
    private double[] getSubwordEmbedding(String subword) {
        if (!subwordEmbeddings.containsKey(subword)) {
            double[] embed = new double[embeddingSize];
            Random rand = new Random(subword.hashCode());
            
            for (int i = 0; i < embeddingSize; i++) {
                embed[i] = (rand.nextDouble() - 0.5) * 0.5;
            }
            
            subwordEmbeddings.put(subword, embed);
        }
        
        return subwordEmbeddings.get(subword);
    }
    
    public double[] getOOVEmbedding(String word, List<String> similarWords) {
        List<String> subwords = getSubwords(word);
        
        double[] embedding = new double[embeddingSize];
        
        for (String subword : subwords) {
            if (subwordEmbeddings.containsKey(subword)) {
                double[] subwordEmbed = subwordEmbeddings.get(subword);
                
                for (int i = 0; i < embeddingSize; i++) {
                    embedding[i] += subwordEmbed[i];
                }
            }
        }
        
        for (int i = 0; i < embeddingSize; i++) {
            embedding[i] /= subwords.size();
        }
        
        return embedding;
    }
}
```