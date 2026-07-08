package com.arch.platform;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SelfServiceCatalog {
    private final Map<String, CatalogItem> items = new ConcurrentHashMap<>();
    private final Map<String, List<ApprovalRequest>> pendingApprovals = new ConcurrentHashMap<>();

    public void registerItem(CatalogItem item) {
        items.put(item.id(), item);
    }

    public ProvisionResult provision(String itemId, String requester, Map<String, String> params) {
        CatalogItem item = items.get(itemId);
        if (item == null) throw new IllegalArgumentException("Unknown catalog item: " + itemId);
        if (item.requiresApproval()) {
            ApprovalRequest request = new ApprovalRequest(UUID.randomUUID().toString(), itemId, requester, params, ApprovalStatus.PENDING);
            pendingApprovals.computeIfAbsent(itemId, k -> new CopyOnWriteArrayList<>()).add(request);
            return new ProvisionResult(request.requestId(), ProvisionStatus.PENDING_APPROVAL, "Approval required");
        }
        return executeProvision(item, params);
    }

    public ProvisionResult approve(String requestId, String approver) {
        for (List<ApprovalRequest> requests : pendingApprovals.values()) {
            for (ApprovalRequest req : requests) {
                if (req.requestId().equals(requestId)) {
                    req.approve(approver);
                    CatalogItem item = items.get(req.itemId());
                    return executeProvision(item, req.params());
                }
            }
        }
        throw new IllegalArgumentException("Unknown request: " + requestId);
    }

    private ProvisionResult executeProvision(CatalogItem item, Map<String, String> params) {
        return new ProvisionResult(UUID.randomUUID().toString(), ProvisionStatus.COMPLETED, "Provisioned: " + item.name());
    }

    public List<CatalogItem> getAvailableItems() { return List.copyOf(items.values()); }
    public List<ApprovalRequest> getPendingApprovals() {
        return pendingApprovals.values().stream().flatMap(List::stream).filter(r -> r.status() == ApprovalStatus.PENDING).toList();
    }
}

record CatalogItem(String id, String name, String description, boolean requiresApproval, List<String> requiredParams) {}
record ProvisionResult(String requestId, ProvisionStatus status, String message) {}
record ApprovalRequest(String requestId, String itemId, String requester, Map<String, String> params, ApprovalStatus status) {
    public void approve(String approver) { /* state change */ }
}

enum ProvisionStatus { COMPLETED, FAILED, PENDING_APPROVAL }
enum ApprovalStatus { PENDING, APPROVED, REJECTED }
