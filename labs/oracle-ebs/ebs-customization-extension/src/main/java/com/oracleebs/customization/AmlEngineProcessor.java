package com.oracleebs.customization;

import java.util.*;

public class AmlEngineProcessor {
    public enum TransactionType { PO_AMOUNT, INVOICE_AMOUNT, TRAVEL_EXPENSE }

    public static class ApprovalRule {
        private final String ruleName;
        private final TransactionType transactionType;
        private final double threshold;
        private final String approverGroup;
        private final boolean autoApprove;

        public ApprovalRule(String name, TransactionType type, double threshold, String group, boolean auto) {
            this.ruleName = name;
            this.transactionType = type;
            this.threshold = threshold;
            this.approverGroup = group;
            this.autoApprove = auto;
        }

        public String getRuleName() { return ruleName; }
        public TransactionType getTransactionType() { return transactionType; }
        public double getThreshold() { return threshold; }
        public String getApproverGroup() { return approverGroup; }
        public boolean isAutoApprove() { return autoApprove; }
    }

    public static class ApprovalRequest {
        private final String requestId;
        private final TransactionType type;
        private final double amount;
        private final String initiator;
        private String assignee;
        private String status;

        public ApprovalRequest(String id, TransactionType type, double amount, String initiator) {
            this.requestId = id;
            this.type = type;
            this.amount = amount;
            this.initiator = initiator;
            this.status = "PENDING";
        }

        public String getRequestId() { return requestId; }
        public TransactionType getType() { return type; }
        public double getAmount() { return amount; }
        public String getInitiator() { return initiator; }
        public String getAssignee() { return assignee; }
        public void setAssignee(String a) { assignee = a; }
        public String getStatus() { return status; }
        public void setStatus(String s) { status = s; }
    }

    private final List<ApprovalRule> rules;
    private final Map<String, List<String>> approverGroups;
    private final Map<String, ApprovalRequest> requests;

    public AmlEngineProcessor() {
        this.rules = new ArrayList<>();
        this.approverGroups = new LinkedHashMap<>();
        this.requests = new LinkedHashMap<>();
    }

    public void addRule(ApprovalRule rule) { rules.add(rule); }
    public void addApprover(String group, String approver) {
        approverGroups.computeIfAbsent(group, k -> new ArrayList<>()).add(approver);
    }

    public ApprovalRequest submitRequest(String reqId, TransactionType type, double amount, String initiator) {
        ApprovalRequest req = new ApprovalRequest(reqId, type, amount, initiator);

        for (ApprovalRule rule : rules) {
            if (rule.getTransactionType() == type && amount <= rule.getThreshold()) {
                if (rule.isAutoApprove()) {
                    req.setStatus("AUTO_APPROVED");
                } else {
                    var group = approverGroups.get(rule.getApproverGroup());
                    if (group != null && !group.isEmpty()) {
                        req.setAssignee(group.get(0));
                        req.setStatus("PENDING_APPROVAL");
                    }
                }
                break;
            }
        }

        if (req.getStatus().equals("PENDING")) {
            req.setStatus("REQUIRES_MANUAL");
        }

        requests.put(reqId, req);
        return req;
    }

    public Optional<ApprovalRequest> getRequest(String reqId) {
        return Optional.ofNullable(requests.get(reqId));
    }

    public static AmlEngineProcessor createDefault() {
        AmlEngineProcessor ame = new AmlEngineProcessor();
        ame.addRule(new ApprovalRule("LOW_VALUE_PO", TransactionType.PO_AMOUNT, 5000, "MANAGERS", true));
        ame.addRule(new ApprovalRule("MED_VALUE_PO", TransactionType.PO_AMOUNT, 50000, "MANAGERS", false));
        ame.addRule(new ApprovalRule("HIGH_VALUE_PO", TransactionType.PO_AMOUNT, Double.MAX_VALUE, "DIRECTORS", false));
        ame.addRule(new ApprovalRule("INVOICE_AUTO", TransactionType.INVOICE_AMOUNT, 1000, "ACCOUNTS_PAYABLE", true));
        ame.addApprover("MANAGERS", "John Manager");
        ame.addApprover("MANAGERS", "Jane Supervisor");
        ame.addApprover("DIRECTORS", "Bob Director");
        ame.addApprover("ACCOUNTS_PAYABLE", "Alice AP");
        return ame;
    }
}
