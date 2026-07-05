# Step by Step: Recommendation Engine

## Generating Recommendations

1. User Alice requests recommendations (GET /recommendations/alice?limit=10)
2. RecService looks up Alice's user factor vector in Redis
3. If not found (cold start), fall back to popularity-based top-50
4. If found, ANN search finds 100 nearest item vectors (cosine distance)
5. Remove items Alice already purchased/interacted with
6. Compute content-based scores for remaining candidates using TF-IDF on item descriptions
7. HybridRanker computes: final = 0.6 * cf_score + 0.3 * content_score + 0.1 * popularity_score
8. Apply diversity rule: max 3 items from same category in top-10
9. Sort by final score descending, return top-10
10. Log recommendations with impression ID for feedback tracking
