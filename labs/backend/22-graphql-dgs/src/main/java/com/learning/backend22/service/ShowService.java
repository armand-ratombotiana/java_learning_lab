package com.learning.backend22.service;

import com.learning.backend22.model.Show;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ShowService {

    private final Map<String, Show> shows = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {
        shows.put("show-1", new Show("show-1", "Stranger Things", 2016, 8.7));
        shows.put("show-2", new Show("show-2", "The Crown", 2016, 8.6));
        shows.put("show-3", new Show("show-3", "Dark", 2017, 8.8));
        shows.put("show-4", new Show("show-4", "Money Heist", 2017, 8.3));
    }

    public List<Show> getAllShows() {
        return List.copyOf(shows.values());
    }

    public Show getShow(String id) {
        return shows.get(id);
    }
}
