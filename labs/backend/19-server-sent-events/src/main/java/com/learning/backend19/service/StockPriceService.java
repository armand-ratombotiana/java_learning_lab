package com.learning.backend19.service;

import com.learning.backend19.model.StockPrice;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class StockPriceService {

    private static final double BASE_PRICE = 150.0;

    public StockPrice generatePrice(String symbol) {
        double change = ThreadLocalRandom.current().nextDouble(-5.0, 5.0);
        double price = BASE_PRICE + change;
        return new StockPrice(symbol, Math.round(price * 100.0) / 100.0, Instant.now());
    }
}
