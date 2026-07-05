# Debugging: Fraud Detection

## Common Issues

### ML score always 0.5 (neutral)
- Check if model file is loaded correctly
- Verify feature vector matches model input dimensions
- Check ONNX Runtime logs for shape mismatches

### Rule engine returning unexpected REJECT
- Check rule order: expensive rules running before cheap filters
- Verify rule thresholds in config file
- Check if blacklist contains false positives

### Feature store cache misses
- Check Redis connectivity
- Verify feature key naming convention
- Check TTL expiration: maybe windows are larger than TTL

### Pipeline latency spikes
- Check Kafka consumer lag
- Profile feature extraction: expensive computations (geo-distance) should be cached
- Check thread pool saturation in MLScorer
