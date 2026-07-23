package memory;

import java.lang.invoke.VarHandle;
import java.lang.invoke.MethodHandles;

/**
 * Memory barrier demonstration using VarHandle and volatile.
 * 
 * Shows:
 * - acquire/release semantics (like memory barriers)
 * - opaque access (prevents reordering, no visibility guarantee)
 * - volatile read/write with full barrier
 * 
 * CPU memory barrier instructions:
 * - LoadLoad: lwsync (PowerPC), MFENCE (x86)
 * - LoadStore: lwsync, MFENCE
 * - StoreLoad: sync, MFENCE (most expensive — needed for volatile writes)
 * - StoreStore: dmb st (ARM)
 * 
 * On x86, volatile reads are ordinary loads (no barrier needed due to TSO).
 * On x86, volatile writes are expensive (mfence or locked instruction).
 */
public class MemoryBarrierExample {

    static class Account {
        private volatile int balance; // volatile: full StoreLoad barrier on write

        void deposit(int amount) {
            // volatile write implies StoreLoad barrier
            balance += amount;
        }

        int getBalance() {
            // volatile read implies LoadLoad/LoadStore barrier
            return balance;
        }
    }

    // Using VarHandle for finer-grained control
    static class AccountVarHandle {
        private int balance;
        private static final VarHandle BALANCE;

        static {
            try {
                BALANCE = MethodHandles.lookup()
                    .findVarHandle(AccountVarHandle.class, "balance", int.class);
            } catch (Exception e) {
                throw new ExceptionInInitializerError(e);
            }
        }

        void deposit(int amount) {
            // Plain volatile write
            BALANCE.setVolatile(this, (int) BALANCE.getVolatile(this) + amount);
        }

        int getBalance() {
            // Plain volatile read with acquire semantics
            return (int) BALANCE.getVolatile(this);
        }

        // Opaque access — prevents reordering but no visibility guarantee
        int getOpaque() {
            return (int) BALANCE.getOpaque(this);
        }

        // Acquire release
        int getAcquire() {
            return (int) BALANCE.getAcquire(this);
        }

        void setRelease(int value) {
            BALANCE.setRelease(this, value);
        }
    }

    public static void main(String[] args) throws Exception {
        Account acct = new Account();
        acct.deposit(100);
        assert acct.getBalance() == 100;

        // VarHandle
        AccountVarHandle vh = new AccountVarHandle();
        vh.deposit(50);
        assert vh.getBalance() == 50;
        assert vh.getAcquire() == 50;

        System.out.println("All MemoryBarrierExample tests passed.");
    }
}