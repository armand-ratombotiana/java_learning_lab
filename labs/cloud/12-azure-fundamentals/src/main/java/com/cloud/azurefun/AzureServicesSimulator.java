package com.cloud.azurefun;

import java.util.*;
import java.util.concurrent.*;

public class AzureServicesSimulator {

    public static class VirtualMachine {
        private final String name;
        private final String region;
        private final String size;
        private PowerState powerState;
        private final String publicIp;
        private final Map<String, String> tags = new HashMap<>();

        public enum PowerState { RUNNING, STOPPED, DEALLOCATED }

        public VirtualMachine(String name, String region, String size) {
            this.name = name;
            this.region = region;
            this.size = size;
            this.powerState = PowerState.RUNNING;
            this.publicIp = generateIp();
        }

        private String generateIp() {
            Random r = new Random();
            return r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
        }

        public void start() { if (powerState != PowerState.RUNNING) { powerState = PowerState.RUNNING; } }
        public void stop() { if (powerState == PowerState.RUNNING) { powerState = PowerState.STOPPED; } }
        public void deallocate() { powerState = PowerState.DEALLOCATED; }
        public String getName() { return name; }
        public String getRegion() { return region; }
        public String getSize() { return size; }
        public PowerState getPowerState() { return powerState; }
        public String getPublicIp() { return publicIp; }
        public void setTag(String key, String value) { tags.put(key, value); }

        @Override public String toString() { return name + " [" + size + "] " + powerState + " @" + publicIp; }
    }

    public static class BlobStorageAccount {
        private final String accountName;
        private final Map<String, byte[]> blobs = new ConcurrentHashMap<>();

        public BlobStorageAccount(String accountName) { this.accountName = accountName; }

        public void uploadBlob(String container, String blobName, byte[] data) {
            blobs.put(container + "/" + blobName, data);
        }

        public byte[] downloadBlob(String container, String blobName) {
            return blobs.get(container + "/" + blobName);
        }

        public List<String> listBlobs(String container) {
            return blobs.keySet().stream()
                .filter(k -> k.startsWith(container + "/"))
                .map(k -> k.substring(container.length() + 1))
                .toList();
        }

        public void deleteBlob(String container, String blobName) {
            blobs.remove(container + "/" + blobName);
        }
    }

    public static class ManagedIdentity {
        private final String clientId;
        private final String tenantId;
        private final String resourceId;
        private final Map<String, String> accessTokens = new ConcurrentHashMap<>();

        public ManagedIdentity(String clientId, String tenantId, String resourceId) {
            this.clientId = clientId; this.tenantId = tenantId; this.resourceId = resourceId;
        }

        public String getAccessToken(String resource) {
            return accessTokens.computeIfAbsent(resource, r ->
                "token-" + clientId.hashCode() + "-" + r.hashCode() + "-" + System.currentTimeMillis());
        }

        public String getClientId() { return clientId; }
        public String getTenantId() { return tenantId; }
    }

    public static void main(String[] args) {
        VirtualMachine vm = new VirtualMachine("web-vm-01", "eastus", "Standard_D2s_v3");
        System.out.println("Created: " + vm);
        vm.stop();
        System.out.println("After stop: " + vm.getPowerState());
        vm.start();

        BlobStorageAccount storage = new BlobStorageAccount("mystorageaccount");
        storage.uploadBlob("data", "file.txt", "Hello Azure Blob!".getBytes());
        byte[] downloaded = storage.downloadBlob("data", "file.txt");
        System.out.println("Blob content: " + new String(downloaded));

        ManagedIdentity identity = new ManagedIdentity("client-123", "tenant-456", "/subscriptions/sub-1");
        String token = identity.getAccessToken("https://vault.azure.net");
        System.out.println("Access token: " + token.substring(0, 20) + "...");
    }
}
