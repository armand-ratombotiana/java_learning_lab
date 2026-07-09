package com.oracleebs.scm;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PricingEngine {
    public enum DiscountType { PERCENTAGE, FIXED_AMOUNT, TIERED }
    public enum SurchargeType { FREIGHT, HANDLING, TAX }

    public static class PriceList {
        private final String name;
        private final String currency;
        private final Map<String, Double> basePrices;

        public PriceList(String name, String currency) {
            this.name = name;
            this.currency = currency;
            this.basePrices = new ConcurrentHashMap<>();
        }

        public void setBasePrice(String itemCode, double price) { basePrices.put(itemCode, price); }
        public String getName() { return name; }
        public String getCurrency() { return currency; }
        public double getBasePrice(String itemCode) { return basePrices.getOrDefault(itemCode, 0.0); }
    }

    public static class Discount {
        private final String name;
        private final DiscountType type;
        private final double value;
        private final double minOrderValue;

        public Discount(String name, DiscountType type, double value, double minOrder) {
            this.name = name;
            this.type = type;
            this.value = value;
            this.minOrderValue = minOrder;
        }

        public String getName() { return name; }
        public DiscountType getType() { return type; }
        public double getValue() { return value; }
        public double getMinOrderValue() { return minOrderValue; }
    }

    private final Map<String, PriceList> priceLists;
    private final List<Discount> discounts;
    private final Map<String, Double> surcharges;

    public PricingEngine() {
        this.priceLists = new ConcurrentHashMap<>();
        this.discounts = new ArrayList<>();
        this.surcharges = new ConcurrentHashMap<>();
    }

    public void addPriceList(PriceList pl) { priceLists.put(pl.getName(), pl); }
    public void addDiscount(Discount d) { discounts.add(d); }
    public void setSurcharge(SurchargeType type, double amount) { surcharges.put(type.name(), amount); }

    public double calculatePrice(String itemCode, String priceListName, int quantity, double orderTotal) {
        PriceList pl = priceLists.get(priceListName);
        if (pl == null) throw new IllegalArgumentException("Price list not found");
        double base = pl.getBasePrice(itemCode);
        double subtotal = base * quantity;

        for (Discount d : discounts) {
            if (orderTotal >= d.getMinOrderValue()) {
                subtotal = switch (d.getType()) {
                    case PERCENTAGE -> subtotal * (1 - d.getValue() / 100);
                    case FIXED_AMOUNT -> subtotal - d.getValue();
                    case TIERED -> applyTieredDiscount(subtotal, quantity, d);
                };
            }
        }

        for (double surcharge : surcharges.values()) {
            subtotal += surcharge;
        }

        return Math.max(0, subtotal);
    }

    private double applyTieredDiscount(double subtotal, int qty, Discount d) {
        if (qty > 100) return subtotal * (1 - d.getValue() * 2 / 100);
        if (qty > 50) return subtotal * (1 - d.getValue() / 100);
        return subtotal;
    }

    public static PricingEngine createDefault() {
        PricingEngine engine = new PricingEngine();
        PriceList pl = new PriceList("STANDARD", "USD");
        pl.setBasePrice("ITEM001", 100.0);
        pl.setBasePrice("ITEM002", 250.0);
        pl.setBasePrice("ITEM003", 50.0);
        engine.addPriceList(pl);
        engine.addDiscount(new Discount("VOLUME_5", DiscountType.PERCENTAGE, 5.0, 500.0));
        engine.addDiscount(new Discount("FLAT_50", DiscountType.FIXED_AMOUNT, 50.0, 1000.0));
        engine.setSurcharge(SurchargeType.FREIGHT, 15.0);
        return engine;
    }
}
