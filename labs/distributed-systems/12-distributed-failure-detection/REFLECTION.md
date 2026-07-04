# Failure Detection: Reflection

## Key Insights
- No perfect failure detection in async systems (FLP)
- Phi-accrual adapts to network conditions automatically
- Suspicion phase significantly reduces false positives
- Heartbeat timeout must account for GC pauses

## Questions
1. How does your system currently detect failures?
2. What's your false positive rate for failure detection?
3. Are your timeout values tuned for your network conditions?
4. Do you account for GC pauses in failure detection?

## Personal Notes
- Phi-accrual is one of the most practical innovations in failure detection
- GC pauses are the most common cause of false positives in JVM systems
- Always test failure detection under load (network delays, CPU throttling)
