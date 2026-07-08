package com.learning.backend22.service;

import com.learning.backend22.model.Review;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ReviewService {

    private final Map<String, Review> reviews = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {
        reviews.put("rev-1", new Review("rev-1", "show-1", "alice", 9, "Amazing!"));
        reviews.put("rev-2", new Review("rev-2", "show-1", "bob", 8, "Great show"));
        reviews.put("rev-3", new Review("rev-3", "show-2", "charlie", 7, "Good"));
        reviews.put("rev-4", new Review("rev-4", "show-3", "dave", 10, "Masterpiece"));
    }

    public List<Review> getReviewsForShows(List<String> showIds) {
        return reviews.values().stream()
            .filter(r -> showIds.contains(r.showId()))
            .toList();
    }

    public List<Review> getReviewsForShow(String showId) {
        return reviews.values().stream()
            .filter(r -> r.showId().equals(showId))
            .toList();
    }
}
