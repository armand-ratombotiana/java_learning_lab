# Common Mistakes: Recommendation Engine

- **Ignoring cold start**: New users/items with no interactions get poor recommendations. Always have popularity-backed fallback and content-based signals.
- **No diversity enforcement**: Recommending 10 items from same category creates boring experience. Enforce category diversity in ranking.
- **Stale factors**: Daily retrain isn't enough for fast-changing catalogs. Use incremental updates.
- **Feedback loop bias**: If you only recommend popular items, user interactions reinforce popularity bias. Add exploration (epsilon-greedy) during serving.
- **Not filtering seen items**: Users get annoyed seeing items they already bought. Filter interaction history.
- **Evaluation on training data**: Always evaluate on holdout set. Training loss does not reflect recommendation quality.
