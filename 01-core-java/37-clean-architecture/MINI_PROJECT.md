# Module 37: Clean Architecture - Mini Project

**Project Name**: Clean Architecture Banking Core  
**Difficulty Level**: Advanced  
**Estimated Time**: 3-4 hours

---

## 🎯 Objective
Implement a core banking feature (Money Transfer) strictly adhering to Uncle Bob's Clean Architecture, demonstrating the Dependency Rule, pure domain entities, and separation of concerns via Ports and Adapters.

## 📝 Requirements

### Core Features

1. **Domain Enterprise Rules (Entities)**:
   - Create a pure Java class `Account` with fields `id` and `balance`.
   - Add business methods: `withdraw(double amount)` and `deposit(double amount)`.
   - These methods must contain the domain logic (e.g., throw `InsufficientFundsException` if withdrawing drops balance below zero). This class must NOT contain any Spring or JPA annotations.

2. **Application Business Rules (Use Cases)**:
   - Create an interface `TransferMoneyUseCase` with a method `void transfer(String fromId, String toId, double amount)`.
   - Create the implementation `TransferMoneyInteractor`. It orchestrates the flow: fetch accounts, execute transfer logic on the entities, and save accounts.

3. **Interface Adapters (Ports)**:
   - **Outbound Port**: Create an interface `AccountRepository` with `Account findById(String id)` and `void save(Account account)`. This interface sits in the Use Case/Domain layer. The Interactor calls this interface.
   - **Inbound Port**: The `TransferMoneyUseCase` acts as the inbound port.

4. **Frameworks & Drivers (The Outer Layer)**:
   - **Web**: Create a REST controller `TransferController` that takes an HTTP request and invokes the `TransferMoneyUseCase`.
   - **Database**: Create `SqlAccountRepository` that implements `AccountRepository`. It contains the actual JDBC/JPA logic.
   - **Configuration**: Create a Spring `@Configuration` class to manually wire these dependencies together, injecting the `SqlAccountRepository` into the `TransferMoneyInteractor`.

---

## 💡 Solution Blueprint

1. **Domain Layer (No dependencies on frameworks)**:
   ```java
   public class Account {
       private String id;
       private double balance;
       
       public void withdraw(double amount) {
           if (balance < amount) throw new RuntimeException("Insufficient Funds");
           balance -= amount;
       }
       public void deposit(double amount) { balance += amount; }
   }
   ```

2. **Use Case Layer**:
   ```java
   public interface AccountRepository {
       Account load(String id);
       void save(Account account);
   }

   public class TransferMoneyInteractor implements TransferMoneyUseCase {
       private final AccountRepository repository;
       
       public TransferMoneyInteractor(AccountRepository repo) { this.repository = repo; }
       
       public void transfer(String fromId, String toId, double amount) {
           Account from = repository.load(fromId);
           Account to = repository.load(toId);
           
           from.withdraw(amount);
           to.deposit(amount);
           
           repository.save(from);
           repository.save(to);
       }
   }
   ```

3. **Infrastructure / Outer Layer**:
   ```java
   @RestController
   public class TransferController {
       private final TransferMoneyUseCase useCase;
       // Inject and call useCase.transfer(...)
   }
   ```