# Saga Pattern Code Deep Dive

This lab provides a pure Java simulation of a Saga Orchestrator managing a distributed E-Commerce transaction.

## 💻 Pure Java Implementation

```java file="labs/system-design/07-transactions/saga-pattern/SOLUTION/SagaOrchestratorDemo.java"
package systemdesign.transactions.saga;

/**
 * A simulation of an Orchestration-based Saga.
 */
public class SagaOrchestratorDemo {

    // Simulating external microservices
    static class InventoryService {
        boolean reserve(String orderId) {
            System.out.println("[Inventory] Reserving stock for order: " + orderId);
            return true; // Simulate success
        }
        void release(String orderId) {
            System.out.println("[Inventory] COMPENSATING: Releasing stock for order: " + orderId);
        }
    }

    static class PaymentService {
        boolean charge(String orderId, boolean simulateFailure) {
            System.out.println("[Payment] Attempting to charge card for order: " + orderId);
            if (simulateFailure) {
                System.out.println("[Payment] FAILED: Insufficient funds.");
                return false;
            }
            return true;
        }
        void refund(String orderId) {
            System.out.println("[Payment] COMPENSATING: Refunding payment for order: " + orderId);
        }
    }

    static class OrderService {
        void approve(String orderId) {
            System.out.println("[Order] SUCCESS: Order " + orderId + " approved.");
        }
        void reject(String orderId) {
            System.out.println("[Order] COMPENSATING: Order " + orderId + " rejected.");
        }
    }

    // The Saga Orchestrator State Machine
    static class CreateOrderSaga {
        private final InventoryService inventoryService;
        private final PaymentService paymentService;
        private final OrderService orderService;

        CreateOrderSaga(InventoryService inv, PaymentService pay, OrderService ord) {
            this.inventoryService = inv;
            this.paymentService = pay;
            this.orderService = ord;
        }

        public void execute(String orderId, boolean simulatePaymentFailure) {
            System.out.println("\n--- Starting Saga for Order: " + orderId + " ---");
            
            // Step 1: Reserve Inventory
            boolean invSuccess = inventoryService.reserve(orderId);
            if (!invSuccess) {
                orderService.reject(orderId);
                return;
            }

            // Step 2: Charge Payment
            boolean paySuccess = paymentService.charge(orderId, simulatePaymentFailure);
            if (!paySuccess) {
                // SAGA FAILURE: Execute Compensating Transactions in reverse order
                System.out.println("\n--- Saga Failed. Executing Compensating Transactions ---");
                inventoryService.release(orderId);
                orderService.reject(orderId);
                return;
            }

            // Step 3: Approve Order
            orderService.approve(orderId);
            System.out.println("--- Saga Completed Successfully ---");
        }
    }

    public static void main(String[] args) {
        InventoryService inv = new InventoryService();
        PaymentService pay = new PaymentService();
        OrderService ord = new OrderService();
        
        CreateOrderSaga saga = new CreateOrderSaga(inv, pay, ord);

        // Scenario 1: Happy Path
        saga.execute("ORD-100", false);

        // Scenario 2: Payment Failure triggering Compensation
        saga.execute("ORD-200", true);
    }
}
```

## 🔍 Key Takeaways
1. **The Orchestrator's Role**: Notice how `CreateOrderSaga.execute()` contains the entire business workflow. The individual services (`InventoryService`, `PaymentService`) know nothing about the Saga or each other. They just expose APIs to reserve, charge, and compensate.
2. **Reverse Compensation**: When the payment fails in Step 2, the Orchestrator does not need to call `refund()` on the Payment service (because the charge failed). It must call `release()` on the Inventory service (Step 1) and `reject()` on the Order service (Step 0) to bring the distributed system back to a consistent state.