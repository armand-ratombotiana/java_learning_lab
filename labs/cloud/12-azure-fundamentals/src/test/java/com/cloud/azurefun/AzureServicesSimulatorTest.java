package com.cloud.azurefun;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class AzureServicesSimulatorTest {
    @Test void testVirtualMachineLifecycle() {
        var vm = new AzureServicesSimulator.VirtualMachine("test-vm", "eastus", "Standard_D2s_v3");
        assertEquals(AzureServicesSimulator.VirtualMachine.PowerState.RUNNING, vm.getPowerState());
        vm.stop();
        assertEquals(AzureServicesSimulator.VirtualMachine.PowerState.STOPPED, vm.getPowerState());
        vm.start();
        assertEquals(AzureServicesSimulator.VirtualMachine.PowerState.RUNNING, vm.getPowerState());
        vm.deallocate();
        assertEquals(AzureServicesSimulator.VirtualMachine.PowerState.DEALLOCATED, vm.getPowerState());
    }

    @Test void testBlobStorage() {
        var storage = new AzureServicesSimulator.BlobStorageAccount("teststorage");
        storage.uploadBlob("container1", "file.txt", "Hello".getBytes());
        assertArrayEquals("Hello".getBytes(), storage.downloadBlob("container1", "file.txt"));
        assertEquals(1, storage.listBlobs("container1").size());
        storage.deleteBlob("container1", "file.txt");
        assertNull(storage.downloadBlob("container1", "file.txt"));
    }

    @Test void testManagedIdentity() {
        var identity = new AzureServicesSimulator.ManagedIdentity("client-1", "tenant-1", "/subscriptions/1");
        String token = identity.getAccessToken("https://vault.azure.net");
        assertNotNull(token);
        assertTrue(token.startsWith("token-"));
    }
}
