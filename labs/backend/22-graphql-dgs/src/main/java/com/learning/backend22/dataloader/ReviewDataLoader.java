package com.learning.backend22.dataloader;

import com.learning.backend22.model.Review;
import com.learning.backend22.service.ReviewService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsDataLoader;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@DgsComponent
@DgsDataLoader(name = "reviewLoader", maxBatchSize = 50)
public class ReviewDataLoader implements java.util.function.Function<List<String>, CompletionStage<List<List<Review>>>> {

    private final ReviewService reviewService;

    public ReviewDataLoader(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Override
    public CompletionStage<List<List<Review>>> apply(List<String> showIds) {
        return CompletableFuture.supplyAsync(() -> {
            var allReviews = reviewService.getReviewsForShows(showIds);
            return showIds.stream()
                .map(id -> allReviews.stream()
                    .filter(r -> r.showId().equals(id))
                    .toList())
                .toList();
        });
    }
}
