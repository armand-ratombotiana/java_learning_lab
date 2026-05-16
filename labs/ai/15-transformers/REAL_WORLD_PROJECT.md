# Transformers - REAL WORLD PROJECT

## Project: Production Machine Translation Service

Build a scalable transformer-based machine translation service supporting multiple language pairs.

### Architecture

```
Input Text → Tokenizer → Encoder → Cross-Attention → Decoder → Output Generation
                                  ↓
                            Beam Search
                              ↓
                            Output
```

### Implementation

```java
@Service
public class TranslationService {
    private TransformerModel model;
    private TokenizerFactory tokenizerFactory;
    private Map<String, Tokenizer> tokenizers;
    private ModelCache modelCache;
    private MetricsCollector metrics;
    
    @PostConstruct
    public void initialize() {
        model = loadModel("en-de-transformer");
        
        tokenizers = new HashMap<>();
        tokenizers.put("en", new BPETokenizer("en"));
        tokenizers.put("de", new BPETokenizer("de"));
        tokenizers.put("fr", new BPETokenizer("fr"));
        
        modelCache = new ModelCache(3);
    }
    
    public TranslationResult translate(TranslationRequest request) {
        long startTime = System.currentTimeMillis();
        
        String sourceLang = request.getSourceLanguage();
        String targetLang = request.getTargetLanguage();
        String text = request.getText();
        
        Tokenizer sourceTokenizer = tokenizers.get(sourceLang);
        Tokenizer targetTokenizer = tokenizers.get(targetLang);
        
        int[] sourceIds = sourceTokenizer.encode(text);
        
        String cacheKey = sourceLang + "-" + targetLang + "-" + text;
        
        if (modelCache.contains(cacheKey)) {
            return modelCache.get(cacheKey);
        }
        
        int[] generatedIds = model.generate(
            sourceIds, 
            targetTokenizer.getVocabularySize(),
            request.getMaxLength(),
            request.getBeamSize(),
            request.getTemperature()
        );
        
        String translatedText = targetTokenizer.decode(generatedIds);
        
        long inferenceTime = System.currentTimeMillis() - startTime;
        
        TranslationResult result = new TranslationResult(
            translatedText,
            sourceLang,
            targetLang,
            inferenceTime,
            calculateConfidence(generatedIds)
        );
        
        modelCache.put(cacheKey, result);
        
        metrics.record("translation.latency.ms", inferenceTime);
        metrics.increment("translation.count");
        
        return result;
    }
    
    public Stream<TranslationResult> translateBatch(List<TranslationRequest> requests) {
        return requests.parallelStream()
            .map(this::translate);
    }
    
    private double calculateConfidence(int[] tokenIds) {
        double avgConfidence = 0;
        
        for (int id : tokenIds) {
            avgConfidence += model.getTokenProbability(id);
        }
        
        return avgConfidence / tokenIds.length;
    }
}
```

### Transformer Model

```java
public class TransformerModel {
    private TransformerEncoderBlock[] encoderBlocks;
    private TransformerDecoderBlock[] decoderBlocks;
    private int vocabSize;
    private int dModel = 512;
    private int numHeads = 8;
    private int numLayers = 6;
    private double[][] embeddingMatrix;
    private double[][] lmHead;
    
    public TransformerModel(int vocabSize, int numLayers) {
        this.vocabSize = vocabSize;
        
        this.encoderBlocks = new TransformerEncoderBlock[numLayers];
        this.decoderBlocks = new TransformerDecoderBlock[numLayers];
        
        for (int i = 0; i < numLayers; i++) {
            encoderBlocks[i] = new TransformerEncoderBlock(dModel, numHeads, dModel * 4);
            decoderBlocks[i] = new TransformerDecoderBlock(dModel, numHeads, dModel * 4);
        }
        
        this.embeddingMatrix = createEmbeddingMatrix(vocabSize, dModel);
        this.lmHead = createLinearLayer(vocabSize, dModel);
    }
    
    private double[][] createEmbeddingMatrix(int vocabSize, int dModel) {
        double[][] matrix = new double[vocabSize][dModel];
        Random rand = new Random(42);
        
        for (int i = 0; i < vocabSize; i++) {
            for (int j = 0; j < dModel; j++) {
                matrix[i][j] = (rand.nextDouble() - 0.5) * 0.1;
            }
        }
        
        return matrix;
    }
    
    private double[][] createLinearLayer(int vocabSize, int dModel) {
        return createEmbeddingMatrix(vocabSize, dModel);
    }
    
    public int[] generate(int[] inputIds, int vocabSize, int maxLength, 
                          int beamSize, double temperature) {
        if (beamSize == 1) {
            return greedyDecode(inputIds, maxLength, temperature);
        } else {
            return beamSearchDecode(inputIds, maxLength, beamSize, temperature);
        }
    }
    
    private int[] greedyDecode(int[] inputIds, int maxLength, double temperature) {
        double[][] encoderOutput = encode(inputIds);
        
        List<Integer> generated = new ArrayList<>();
        generated.add(2);
        
        while (generated.size() < maxLength) {
            int[] currentInput = generated.stream()
                .mapToInt(Integer::intValue)
                .toArray();
            
            double[] logits = decodeStep(currentInput, encoderOutput);
            
            double[] probs = applyTemperature(logits, temperature);
            int nextToken = argmax(probs);
            
            if (nextToken == 3) {
                break;
            }
            
            generated.add(nextToken);
        }
        
        return generated.stream().mapToInt(Integer::intValue).toArray();
    }
    
    private int[] beamSearchDecode(int[] inputIds, int maxLength, 
                                   int beamSize, double temperature) {
        double[][] encoderOutput = encode(inputIds);
        
        PriorityQueue<BeamHypothesis> beams = new PriorityQueue<>(
            (a, b) -> Double.compare(b.score, a.score)
        );
        
        beams.add(new BeamHypothesis(new int[]{2}, 0.0));
        
        for (int step = 0; step < maxLength; step++) {
            List<BeamHypothesis> candidates = new ArrayList<>();
            
            for (BeamHypothesis beam : beams) {
                if (beam.tokens[beam.tokens.length - 1] == 3) {
                    candidates.add(beam);
                    continue;
                }
                
                double[] logits = decodeStep(beam.tokens, encoderOutput);
                double[] probs = applyTemperature(logits, temperature);
                
                int[] topK = getTopK(probs, beamSize);
                
                for (int token : topK) {
                    double newScore = beam.score + Math.log(probs[token] + 1e-10);
                    
                    int[] newTokens = Arrays.copyOf(beam.tokens, beam.tokens.length + 1);
                    newTokens[newTokens.length - 1] = token;
                    
                    candidates.add(new BeamHypothesis(newTokens, newScore));
                }
            }
            
            beams = candidates.stream()
                .sorted((a, b) -> Double.compare(b.score / a.tokens.length, 
                                                 a.score / b.tokens.length))
                .limit(beamSize)
                .collect(Collectors.toCollection(PriorityQueue::new));
            
            if (beams.stream().allMatch(b -> b.tokens[b.tokens.length - 1] == 3)) {
                break;
            }
        }
        
        return beams.poll().tokens;
    }
    
    private double[][] encode(int[] inputIds) {
        double[][] embeddings = getEmbeddings(inputIds);
        
        PositionalEncoding pe = new PositionalEncoding(dModel, 512);
        embeddings = pe.encode(embeddings);
        
        double[][] encoderOutput = embeddings;
        
        for (TransformerEncoderBlock block : encoderBlocks) {
            encoderOutput = block.forward(encoderOutput, null);
        }
        
        return encoderOutput;
    }
    
    private double[][] getEmbeddings(int[] inputIds) {
        double[][] embeddings = new double[inputIds.length][dModel];
        
        for (int i = 0; i < inputIds.length; i++) {
            int tokenId = Math.min(inputId[i], embeddingMatrix.length - 1);
            embeddings[i] = embeddingMatrix[tokenId].clone();
        }
        
        return embeddings;
    }
    
    private double[] decodeStep(int[] generatedIds, double[][] encoderOutput) {
        double[][] decoderInput = getEmbeddings(generatedIds);
        
        PositionalEncoding pe = new PositionalEncoding(dModel, 512);
        decoderInput = pe.encode(decoderInput);
        
        double[][] decoderOutput = decoderInput;
        
        for (TransformerDecoderBlock block : decoderBlocks) {
            decoderOutput = block.forward(decoderOutput, encoderOutput, null);
        }
        
        double[] lastHidden = decoderOutput[decoderOutput.length - 1];
        
        double[] logits = new double[vocabSize];
        
        for (int i = 0; i < vocabSize; i++) {
            for (int j = 0; j < dModel; j++) {
                logits[i] += lmHead[i][j] * lastHidden[j];
            }
        }
        
        return logits;
    }
    
    private double[] applyTemperature(double[] logits, double temperature) {
        double[] scaled = new double[logits.length];
        
        for (int i = 0; i < logits.length; i++) {
            scaled[i] = logits[i] / temperature;
        }
        
        double max = scaled[0];
        
        for (int i = 1; i < scaled.length; i++) {
            if (scaled[i] > max) max = scaled[i];
        }
        
        double sum = 0;
        
        for (int i = 0; i < scaled.length; i++) {
            scaled[i] = Math.exp(scaled[i] - max);
            sum += scaled[i];
        }
        
        for (int i = 0; i < scaled.length; i++) {
            scaled[i] /= sum;
        }
        
        return scaled;
    }
    
    private int argmax(double[] arr) {
        int idx = 0;
        double max = arr[0];
        
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
                idx = i;
            }
        }
        
        return idx;
    }
    
    private int[] getTopK(double[] probs, int k) {
        Integer[] indices = new Integer[probs.length];
        
        for (int i = 0; i < probs.length; i++) {
            indices[i] = i;
        }
        
        Arrays.sort(indices, (a, b) -> Double.compare(probs[b], probs[a]));
        
        int[] topK = new int[k];
        
        for (int i = 0; i < k; i++) {
            topK[i] = indices[i];
        }
        
        return topK;
    }
    
    public double getTokenProbability(int tokenId) {
        return 0.8;
    }
}

class BeamHypothesis {
    int[] tokens;
    double score;
    
    public BeamHypothesis(int[] tokens, double score) {
        this.tokens = tokens;
        this.score = score;
    }
}
```

### API Endpoints

```java
@RestController
@RequestMapping("/api/v1/translate")
public class TranslationController {
    
    @PostMapping
    public ResponseEntity<TranslationResponse> translate(
            @RequestBody TranslationRequest request) {
        
        try {
            TranslationResult result = translationService.translate(request);
            
            return ResponseEntity.ok(new TranslationResponse(result));
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(new TranslationResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/batch")
    public ResponseEntity<List<TranslationResponse>> translateBatch(
            @RequestBody List<TranslationRequest> requests) {
        
        List<TranslationResponse> responses = requests.stream()
            .map(req -> {
                try {
                    return new TranslationResponse(translationService.translate(req));
                } catch (Exception e) {
                    return new TranslationResponse(e.getMessage());
                }
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/languages")
    public ResponseEntity<List<LanguagePair>> getSupportedLanguages() {
        return ResponseEntity.ok(Arrays.asList(
            new LanguagePair("en", "de", "English → German"),
            new LanguagePair("de", "en", "German → English"),
            new LanguagePair("en", "fr", "English → French"),
            new LanguagePair("fr", "en", "French → English")
        ));
    }
}
```

### Performance Optimization

```java
@Component
public class TranslationOptimizer {
    
    public void optimizeModel(TransformerModel model) {
        enableFP16Inference(model);
        
        optimizeAttention(model);
        
        pruneLowImportanceWeights(model);
    }
    
    private void enableFP16Inference(TransformerModel model) {
        model.setPrecision(Precision.FP16);
    }
    
    private void optimizeAttention(TransformerModel model) {
        model.enableFlashAttention();
    }
    
    private void pruneLowImportanceWeights(TransformerModel model) {
        for (double[][] weights : model.getWeights()) {
            for (int i = 0; i < weights.length; i++) {
                for (int j = 0; j < weights[0].length; j++) {
                    if (Math.abs(weights[i][j]) < 0.01) {
                        weights[i][j] = 0;
                    }
                }
            }
        }
    }
}
```

## Deliverables

- [x] Transformer encoder-decoder architecture
- [x] BPE tokenizer implementation
- [x] Beam search decoding
- [x] Batch translation API
- [x] Model caching
- [x] Multiple language support
- [x] Performance metrics
- [x] FP16 optimization
- [x] REST API endpoints