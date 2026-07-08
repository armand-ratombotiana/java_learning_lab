# Code Deep Dive: Data Governance

See Java source files in src/main/java/com/dataeng/nineteen/ for:
- PiiDetector.java: Automated PII detection using patterns and classifiers
- DataMasker.java: Dynamic masking with format preservation

Key patterns:
```java
// PII detection
PiiDetector detector = new PiiDetector();
PiiResult result = detector.analyzeColumn("email", sampleValues);
System.out.println("PII type: " + result.getPiiType() +
    ", confidence: " + result.getConfidence());

// Dynamic masking
DataMasker masker = new DataMasker();
String masked = masker.mask("john@example.com", MaskingType.EMAIL);
// Result: j***@example.com
```
