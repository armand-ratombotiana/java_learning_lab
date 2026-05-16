# CNN (Convolutional Neural Networks) - REAL WORLD PROJECT

## Project: Industrial Defect Detection System

Build a production-ready CNN system for detecting defects in manufacturing products on assembly lines.

### Architecture

```
Input Image (224x224x3)
    ↓
ResNet-50 Backbone (pretrained)
    ↓
Feature Pyramid Network
    ↓
Classification Head (Defect/No-Defect)
    ↓
Localization Head (Bounding Box)
    ↓
Defect Type Classifier
```

### Implementation

```java
@Service
public class DefectDetectionService {
    private ResNetExtractor featureExtractor;
    private ObjectDetector detector;
    private DefectClassifier classifier;
    private ModelRegistry modelRegistry;
    
    @PostConstruct
    public void initialize() {
        featureExtractor = new ResNetExtractor(50);
        detector = new ObjectDetector();
        classifier = new DefectClassifier();
        
        loadModels();
    }
    
    public DetectionResult detectDefects(MultipartFile image) throws IOException {
        double[][][] preprocessed = preprocessImage(image);
        
        double[][][] features = featureExtractor.extract(preprocessed);
        List<BoundingBox> boxes = detector.detect(features);
        
        List<DefectPrediction> predictions = new ArrayList<>();
        
        for (BoundingBox box : boxes) {
            double[][][] cropped = cropRegion(preprocessed, box);
            DefectType type = classifier.classify(cropped);
            double confidence = classifier.getConfidence();
            
            predictions.add(new DefectPrediction(box, type, confidence));
        }
        
        return new DetectionResult(predictions, processTime());
    }
    
    private double[][][] preprocessImage(MultipartFile image) throws IOException {
        BufferedImage img = ImageIO.read(image.getInputStream());
        BufferedImage resized = new BufferedImage(224, 224, BufferedImage.TYPE_INT_RGB);
        
        Graphics2D g = resized.createGraphics();
        g.drawImage(img, 0, 0, 224, 224, null);
        g.dispose();
        
        return convertToTensor(resized);
    }
    
    private double[][][] convertToTensor(BufferedImage image) {
        double[][][] tensor = new double[3][224][224];
        
        for (int y = 0; y < 224; y++) {
            for (int x = 0; x < 224; x++) {
                int rgb = image.getRGB(x, y);
                tensor[0][y][x] = ((rgb >> 16) & 0xFF) / 255.0;
                tensor[1][y][x] = ((rgb >> 8) & 0xFF) / 255.0;
                tensor[2][y][x] = (rgb & 0xFF) / 255.0;
            }
        }
        
        return tensor;
    }
    
    private double[][][] cropRegion(double[][][] image, BoundingBox box) {
        int x1 = (int) box.getX();
        int y1 = (int) box.getY();
        int width = (int) box.getWidth();
        int height = (int) box.getHeight();
        
        double[][][] cropped = new double[3][height][width];
        
        for (int c = 0; c < 3; c++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    cropped[c][y][x] = image[c][y1 + y][x1 + x];
                }
            }
        }
        
        return cropped;
    }
    
    private void loadModels() {
        modelRegistry = new ModelRegistry();
        featureExtractor.setWeights(modelRegistry.load("resnet50-weights"));
        classifier.setWeights(modelRegistry.load("defect-classifier-weights"));
    }
}
```

### Real-time Inference Pipeline

```java
@Component
public class InferencePipeline {
    private DefectDetectionService detectionService;
    private MessageQueueProducer producer;
    private MetricsCollector metrics;
    
    public void processFrame(Frame frame) {
        long startTime = System.currentTimeMillis();
        
        try {
            DetectionResult result = detectionService.detectDefects(frame.getImage());
            
            if (result.hasDefects()) {
                publishDefectAlert(result);
                updateQualityMetrics(result);
            }
            
            recordInferenceTime(startTime);
            
        } catch (Exception e) {
            log.error("Inference failed: " + e.getMessage());
            handleFailure(frame);
        }
    }
    
    private void publishDefectAlert(DetectionResult result) {
        DefectAlert alert = new DefectAlert();
        alert.setTimestamp(System.currentTimeMillis());
        alert.setDefects(result.getPredictions());
        alert.setConfidence(result.getAverageConfidence());
        
        producer.send("defect-alerts", alert);
    }
    
    private void updateQualityMetrics(DetectionResult result) {
        metrics.increment("defects.detected", result.getDefectCount());
        metrics.record("detection.confidence", result.getAverageConfidence());
        metrics.updateQualityScore(result);
    }
    
    private void recordInferenceTime(long startTime) {
        long inferenceTime = System.currentTimeMillis() - startTime;
        metrics.record("inference.time.ms", inferenceTime);
    }
}
```

### Batch Processing for Quality Control

```java
@Service
public class BatchQualityController {
    private DefectDetectionService detectionService;
    private QualityReportGenerator reportGenerator;
    
    public QualityReport analyzeBatch(List<ProductImage> batch) {
        List<DefectSummary> summaries = new ArrayList<>();
        int totalDefects = 0;
        int totalProducts = batch.size();
        
        for (ProductImage product : batch) {
            DetectionResult result = detectionService.detectDefects(product.getImage());
            
            DefectSummary summary = new DefectSummary();
            summary.setProductId(product.getId());
            summary.setDefects(result.getPredictions());
            summary.setPass(result.getDefectCount() == 0);
            
            summaries.add(summary);
            
            if (result.getDefectCount() > 0) {
                totalDefects++;
            }
        }
        
        return reportGenerator.generate(summaries, totalDefects, totalProducts);
    }
    
    public Map<DefectType, Long> getDefectDistribution(List<DefectSummary> summaries) {
        Map<DefectType, Long> distribution = new EnumMap<>(DefectType.class);
        
        for (DefectSummary summary : summaries) {
            for (DefectPrediction pred : summary.getDefects()) {
                distribution.merge(pred.getType(), 1L, Long::sum);
            }
        }
        
        return distribution;
    }
}
```

### Model Monitoring and Retraining

```java
@Component
public class ModelMonitor {
    private PredictionLogger predictionLogger;
    private DriftDetector driftDetector;
    private ModelRetrainer retrainer;
    
    public void logPrediction(DefectPrediction prediction, boolean actual) {
        PredictionRecord record = new PredictionRecord();
        record.setTimestamp(System.currentTimeMillis());
        record.setPrediction(prediction);
        record.setActual(actual);
        
        predictionLogger.log(record);
        
        if (driftDetector.detectDrift(prediction)) {
            triggerRetraining();
        }
    }
    
    private void triggerRetraining() {
        List<PredictionRecord> recentPredictions = predictionLogger.getRecent(1000);
        
        double accuracy = calculateAccuracy(recentPredictions);
        
        if (accuracy < 0.95) {
            retrainer.scheduleRetraining(recentPredictions);
        }
    }
    
    private double calculateAccuracy(List<PredictionRecord> records) {
        long correct = records.stream()
            .filter(r -> r.getPrediction().getType() == 
                        DefectType.fromActual(r.isActual()))
            .count();
        
        return (double) correct / records.size();
    }
}
```

### API Endpoints

```java
@RestController
@RequestMapping("/api/v1/defects")
public class DefectController {
    
    @PostMapping("/detect")
    public ResponseEntity<DetectionResult> detectDefects(
            @RequestParam("image") MultipartFile image) {
        
        try {
            DetectionResult result = detectionService.detectDefects(image);
            return ResponseEntity.ok(result);
            
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/batch")
    public ResponseEntity<QualityReport> analyzeBatch(
            @RequestParam("images") List<MultipartFile> images) {
        
        List<ProductImage> batch = images.stream()
            .map(this::convertToProductImage)
            .collect(Collectors.toList());
        
        QualityReport report = qualityController.analyzeBatch(batch);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<DefectStats> getStats() {
        DefectStats stats = modelMonitor.getStatistics();
        return ResponseEntity.ok(stats);
    }
}
```

## Deliverables

- [x] ResNet-50 backbone with pretrained weights
- [x] Real-time defect detection API
- [x] Multi-defect type classification
- [x] Bounding box localization
- [x] Batch processing for quality control
- [x] Model monitoring and drift detection
- [x] Automated retraining pipeline
- [x] Performance metrics dashboard
- [x] API documentation