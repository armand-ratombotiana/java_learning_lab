package com.oracleebs.scm;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryManagementSimulator {
    public enum TransactionType { RECEIPT, ISSUE, TRANSFER, ADJUSTMENT }

    public static class InventoryItem {
        private final String itemCode;
        private final String description;
        private final String uom;
        private int onHand;
        private final int safetyStock;
        private final boolean lotControlled;
        private final boolean serialControlled;

        public InventoryItem(String itemCode, String desc, String uom, int onHand, int safetyStock, boolean lot, boolean serial) {
            this.itemCode = itemCode;
            this.description = desc;
            this.uom = uom;
            this.onHand = onHand;
            this.safetyStock = safetyStock;
            this.lotControlled = lot;
            this.serialControlled = serial;
        }

        public String getItemCode() { return itemCode; }
        public String getDescription() { return description; }
        public String getUom() { return uom; }
        public int getOnHand() { return onHand; }
        public int getSafetyStock() { return safetyStock; }
        public boolean isLotControlled() { return lotControlled; }
        public boolean isSerialControlled() { return serialControlled; }
        public void setOnHand(int v) { onHand = v; }
    }

    public static class Transaction {
        private final String transactionId;
        private final String itemCode;
        private final TransactionType type;
        private final int quantity;
        private final String subinventory;
        private final Date transactionDate;
        private boolean posted;

        public Transaction(String id, String item, TransactionType type, int qty, String sub) {
            this.transactionId = id;
            this.itemCode = item;
            this.type = type;
            this.quantity = qty;
            this.subinventory = sub;
            this.transactionDate = new Date();
            this.posted = false;
        }

        public String getTransactionId() { return transactionId; }
        public String getItemCode() { return itemCode; }
        public TransactionType getType() { return type; }
        public int getQuantity() { return quantity; }
        public String getSubinventory() { return subinventory; }
        public boolean isPosted() { return posted; }
        public void setPosted(boolean p) { posted = p; }
    }

    private final Map<String, InventoryItem> items;
    private final List<Transaction> transactions;

    public InventoryManagementSimulator() {
        this.items = new ConcurrentHashMap<>();
        this.transactions = new ArrayList<>();
    }

    public void addItem(InventoryItem item) {
        items.put(item.getItemCode(), item);
    }

    public Transaction createTransaction(String id, String itemCode, TransactionType type, int qty, String sub) {
        InventoryItem item = items.get(itemCode);
        if (item == null) throw new IllegalArgumentException("Item not found: " + itemCode);
        Transaction t = new Transaction(id, itemCode, type, qty, sub);
        transactions.add(t);
        return t;
    }

    public boolean postTransaction(String transactionId) {
        Transaction t = transactions.stream()
            .filter(tx -> tx.getTransactionId().equals(transactionId))
            .findFirst().orElse(null);
        if (t == null || t.isPosted()) return false;

        InventoryItem item = items.get(t.getItemCode());
        int delta = switch (t.getType()) {
            case RECEIPT, ADJUSTMENT -> t.getQuantity();
            case ISSUE -> -t.getQuantity();
            case TRANSFER -> 0;
        };
        item.setOnHand(item.getOnHand() + delta);
        t.setPosted(true);
        return true;
    }

    public boolean isBelowSafetyStock(String itemCode) {
        InventoryItem item = items.get(itemCode);
        return item != null && item.getOnHand() < item.getSafetyStock();
    }

    public List<Transaction> getUnpostedTransactions() {
        return transactions.stream().filter(t -> !t.isPosted()).toList();
    }

    public Optional<InventoryItem> getItem(String code) {
        return Optional.ofNullable(items.get(code));
    }

    public static InventoryManagementSimulator createDefault() {
        InventoryManagementSimulator sim = new InventoryManagementSimulator();
        sim.addItem(new InventoryItem("ITEM001", "Widget A", "EA", 100, 20, false, false));
        sim.addItem(new InventoryItem("ITEM002", "Gadget B", "EA", 50, 10, true, true));
        sim.addItem(new InventoryItem("ITEM003", "Component C", "KG", 200, 50, false, false));
        return sim;
    }
}
