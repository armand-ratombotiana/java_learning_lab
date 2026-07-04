# Mental Models for Event Sourcing

## The Accounting Ledger
Event sourcing is exactly like an accounting ledger. You never erase an entry; you always add a new one (credit or debit). The current balance is calculated by summing all entries.

## The Git Repository
Git is an event-sourced system. Every commit is an event, and the current state is derived from replaying all commits. You can checkout any commit and see the state at that point in time.

## The Film Strip
Each event is a frame in a film. The current state is the frame currently displayed. You can rewind to any frame and see the state at that moment.

## The Diary
Your diary records events day by day. Your current life situation is the result of all past events. You can always look back at what happened on any given day.

## The Building Blocks Model
Events are like Lego bricks. The current structure (state) is built from the bricks. You can deconstruct to any previous structure by removing bricks in reverse order.

```java
// Event = Frame in a filmstrip
// Aggregate = The projector that plays frames in order
// Current state = The frame currently displayed
// Snapshot = A bookmark on the filmstrip for quick access

public class AccountProjector {
    private Money balance = Money.ZERO;
    
    public void play(AccountCreatedEvent e) { balance = e.getInitialBalance(); }
    public void play(MoneyDepositedEvent e) { balance = balance.add(e.getAmount()); }
    public void play(MoneyWithdrawnEvent e) { balance = balance.subtract(e.getAmount()); }
    
    public Money getCurrentBalance() { return balance; }
    
    public void snapshot() {
        // Save current state as a checkpoint
        saveSnapshot(new AccountSnapshot(this.accountId, this.balance, this.version));
    }
}
```
