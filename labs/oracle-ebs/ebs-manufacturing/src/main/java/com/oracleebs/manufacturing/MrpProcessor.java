package com.oracleebs.manufacturing;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MrpProcessor {
    public static class Demand {
        private final String itemCode;
        private final int quantity;
        private final LocalDate dueDate;
        private final String orderType;

        public Demand(String item, int qty, LocalDate due, String type) {
            this.itemCode = item;
            this.quantity = qty;
            this.dueDate = due;
            this.orderType = type;
        }

        public String getItemCode() { return itemCode; }
        public int getQuantity() { return quantity; }
        public LocalDate getDueDate() { return dueDate; }
        public String getOrderType() { return orderType; }
    }

    public static class Supply {
        private final String itemCode;
        private final int quantity;
        private final LocalDate availableDate;
        private final String source;

        public Supply(String item, int qty, LocalDate avail, String source) {
            this.itemCode = item;
            this.quantity = qty;
            this.availableDate = avail;
            this.source = source;
        }

        public String getItemCode() { return itemCode; }
        public int getQuantity() { return quantity; }
        public LocalDate getAvailableDate() { return availableDate; }
        public String getSource() { return source; }
    }

    public static class MrpAction {
        public enum ActionType { RELEASE_ORDER, RESCHEDULE_IN, RESCHEDULE_OUT, CANCEL_ORDER }

        private final String itemCode;
        private final ActionType action;
        private final int recommendedQty;
        private final LocalDate actionDate;
        private final String reason;

        public MrpAction(String item, ActionType action, int qty, LocalDate date, String reason) {
            this.itemCode = item;
            this.action = action;
            this.recommendedQty = qty;
            this.actionDate = date;
            this.reason = reason;
        }

        public String getItemCode() { return itemCode; }
        public ActionType getAction() { return action; }
        public int getRecommendedQty() { return recommendedQty; }
        public LocalDate getActionDate() { return actionDate; }
        public String getReason() { return reason; }
    }

    private final Map<String, List<Demand>> demands;
    private final Map<String, List<Supply>> supplies;
    private final Map<String, Integer> leadTimes;

    public MrpProcessor() {
        this.demands = new ConcurrentHashMap<>();
        this.supplies = new ConcurrentHashMap<>();
        this.leadTimes = new ConcurrentHashMap<>();
    }

    public void addDemand(Demand d) {
        demands.computeIfAbsent(d.getItemCode(), k -> new ArrayList<>()).add(d);
    }

    public void addSupply(Supply s) {
        supplies.computeIfAbsent(s.getItemCode(), k -> new ArrayList<>()).add(s);
    }

    public void setLeadTime(String itemCode, int days) {
        leadTimes.put(itemCode, days);
    }

    public List<MrpAction> runMrp() {
        List<MrpAction> actions = new ArrayList<>();
        Set<String> allItems = new HashSet<>(demands.keySet());
        allItems.addAll(supplies.keySet());

        for (String item : allItems) {
            int totalDemand = demands.getOrDefault(item, List.of()).stream()
                .mapToInt(Demand::getQuantity).sum();
            int totalSupply = supplies.getOrDefault(item, List.of()).stream()
                .mapToInt(Supply::getQuantity).sum();
            int net = totalSupply - totalDemand;

            if (net < 0) {
                int lt = leadTimes.getOrDefault(item, 1);
                actions.add(new MrpAction(item, MrpAction.ActionType.RELEASE_ORDER, -net,
                    LocalDate.now().plusDays(lt), "Net deficit: " + net));
            }
        }
        return actions;
    }

    public int getTotalDemand(String item) {
        return demands.getOrDefault(item, List.of()).stream().mapToInt(Demand::getQuantity).sum();
    }

    public int getTotalSupply(String item) {
        return supplies.getOrDefault(item, List.of()).stream().mapToInt(Supply::getQuantity).sum();
    }

    public static MrpProcessor createDefault() {
        MrpProcessor mrp = new MrpProcessor();
        mrp.setLeadTime("RAW001", 3);
        mrp.setLeadTime("RAW002", 5);
        mrp.setLeadTime("FIN001", 2);
        mrp.addDemand(new Demand("FIN001", 100, LocalDate.now().plusDays(7), "SALES_ORDER"));
        mrp.addDemand(new Demand("RAW001", 200, LocalDate.now().plusDays(3), "FORECAST"));
        mrp.addSupply(new Supply("RAW001", 50, LocalDate.now(), "ON_HAND"));
        mrp.addSupply(new Supply("FIN001", 30, LocalDate.now().plusDays(1), "WIP"));
        return mrp;
    }
}
