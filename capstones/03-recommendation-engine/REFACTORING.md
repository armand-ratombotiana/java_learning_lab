# Refactoring: Recommendation Engine

## Current Pain Points
- Monolithic trainer handles both collaborative and content-based training
- ANN index rebuild requires full service restart
- Cold start logic scattered across multiple components
- No A/B testing infrastructure for model comparison
- Feature computation duplicated in offline and online pipelines

## Suggested Improvements
- Separate training into `CollaborativeTrainer` and `ContentTrainer` services
- Implement hot-reload for ANN index with versioned indices
- Centralize cold start logic in `ColdStartHandler` with strategies: POPULARITY, CONTENT, HYBRID
- Add A/B testing service: route % traffic to candidate models, compare metrics
- Unify feature computation in a shared `FeatureService` used by both pipelines
