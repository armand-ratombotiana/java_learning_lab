# Module 17: Testing Strategies - Mini Project

**Project Name**: Test-Driven Development (TDD) Calculator  
**Difficulty Level**: Intermediate  
**Estimated Time**: 2 hours

---

## 🎯 Objective
Practice Test-Driven Development (TDD) by building a simple business logic class, and learn to isolate dependencies using Mockito.

## 📝 Requirements

### Core Features
1. **The Target Class**:
   - Create a `DiscountService` class. It has one method: `public double calculateDiscount(String userId, double amount)`.
   - It depends on a `UserTierRepository` interface which has a method `String getUserTier(String userId)`.

2. **TDD Flow**:
   - Do NOT write the `DiscountService` implementation first.
   - Write the test methods in a class `DiscountServiceTest` first.

3. **Test Scenarios**:
   - Scenario A: If the user tier is "GOLD", apply a 20% discount. (Write test -> Make it pass).
   - Scenario B: If the user tier is "SILVER", apply a 10% discount. (Write test -> Make it pass).
   - Scenario C: If the user tier is "BRONZE" or anything else, apply a 0% discount. (Write test -> Make it pass).
   - Scenario D: If the `UserTierRepository` throws an exception, default to a 0% discount. (Write test -> Make it pass).

4. **Mockito Integration**:
   - Use Mockito to mock the `UserTierRepository` so you don't need a real database.
   - Use `@Mock`, `@InjectMocks`, and `@ExtendWith(MockitoExtension.class)`.

---

## 💡 Solution Blueprint

1. **Test Class**:
   ```java
   @ExtendWith(MockitoExtension.class)
   public class DiscountServiceTest {
       
       @Mock
       private UserTierRepository repository;
       
       @InjectMocks
       private DiscountService service;
       
       @Test
       public void testGoldDiscount() {
           // Arrange
           when(repository.getUserTier("user1")).thenReturn("GOLD");
           
           // Act
           double discount = service.calculateDiscount("user1", 100.0);
           
           // Assert
           assertEquals(20.0, discount);
       }
   }
   ```

2. **Implementation Class (Written AFTER tests)**:
   ```java
   public class DiscountService {
       private final UserTierRepository repo;
       
       public DiscountService(UserTierRepository repo) { this.repo = repo; }
       
       public double calculateDiscount(String userId, double amount) {
           try {
               String tier = repo.getUserTier(userId);
               if ("GOLD".equals(tier)) return amount * 0.20;
               if ("SILVER".equals(tier)) return amount * 0.10;
           } catch (Exception e) {
               // Log error
           }
           return 0.0;
       }
   }
   ```