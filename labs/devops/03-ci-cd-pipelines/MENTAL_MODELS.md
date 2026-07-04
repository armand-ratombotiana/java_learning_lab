# Mental Models for CI/CD

## 1. Factory Assembly Line
- **Source commit** = Raw materials arriving
- **Build** = Machining parts
- **Test** = Quality inspection
- **Deploy staging** = Assembly
- **Integration test** = Final quality check
- **Deploy production** = Shipping to customers

## 2. Pipeline as Safety Net
Each pipeline stage is a safety net catching different types of failures:
- Compilation errors (build stage)
- Logic errors (unit tests)
- Integration errors (integration tests)
- Security vulnerabilities (SAST/DAST)
- Performance regressions (load tests)

## 3. Shift Left
Move testing and validation earlier in the pipeline. Fixing a bug during development costs 10x less than in production. "Test early, test often."

## 4. Build Once, Deploy Many
The artifact built from the source commit is immutable and promoted through environments. Never rebuild for different environments — this guarantees what's tested is what's deployed.
