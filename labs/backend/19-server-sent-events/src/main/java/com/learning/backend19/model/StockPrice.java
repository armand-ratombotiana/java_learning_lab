package com.learning.backend19.model;

import java.time.Instant;

public record StockPrice(String symbol, double price, Instant timestamp) {}
