# Interview Questions: Apache Beam

### Core
**Q**: How does Beam handle late data?
**A**: allowedLateness specifies how long to wait for late arrivals; late firings trigger additional pane emissions

### Architecture
**Q**: What is the difference between Beam and Flink?
**A**: Beam is an abstraction layer providing a unified API; Flink is a runner. Beam provides API portability across runners.

### Optimization
**Q**: What is fusion in Beam?
**A**: Optimization merging adjacent ParDo transforms into a single stage to avoid serialization/communication overhead

### Windowing
**Q**: Explain the difference between Fixed, Sliding, and Session windows.
**A**: Fixed: non-overlapping; Sliding: overlapping with slide period; Session: dynamic based on inactivity gap
