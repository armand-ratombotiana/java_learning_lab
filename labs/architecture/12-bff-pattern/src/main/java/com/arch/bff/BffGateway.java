package com.arch.bff;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public sealed interface BffGateway permits MobileBff, WebBff {
    String getUserId();
    Map<String, Object> getDashboard();
    Map<String, Object> getUserProfile(String userId);
    boolean submitOrder(String productId, int quantity);
    String getClientType();
}
