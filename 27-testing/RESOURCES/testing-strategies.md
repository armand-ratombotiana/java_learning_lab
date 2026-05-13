# Testing Strategies

## Test Pyramid

```
┌─────────────────────────────────────────────────────────────────┐
│                        TEST PYRAMID                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│                           ╱╲                                     │
│                          ╱  ╲   E2E Tests (5%)                  │
│                         ╱────╲  - Browser automation            │
│                        ╱      ╲ - Full flow testing            │
│                       ╱────────╲                                │
│                      ╱  Integration  (20%)                      │
│                     ╱     Tests (25%)                          │
│                    ╱───────  - Component interaction            │
│                   ╱         - Database integration             │
│                  ╱──────────                                    │
│                 ╱  Unit Tests (70%)                             │
│                ╱   - Fast, isolated                             │
│               ╱────── - Mock dependencies                       │
│              ╱        - Most coverage                           │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Unit Testing

```java
@Nested
@DisplayName("OrderService")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("should create order successfully")
    void createOrder_Success() {
        // Arrange
        OrderRequest request = new OrderRequest("product-1", 2);
        when(orderRepository.save(any(Order.class)))
            .thenAnswer(invocation -> {
                Order order = invocation.getArgument(0);
                order.setId("order-1");
                return order;
            });

        // Act
        OrderResponse response = orderService.createOrder(request);

        // Assert
        assertThat(response.getId()).isEqualTo("order-1");
        assertThat(response.getStatus()).isEqualTo(OrderStatus.PENDING);
        verify(orderRepository).save(any(Order.class));
        verify(notificationService).sendOrderConfirmation("order-1");
    }

    @Test
    @DisplayName("should throw when product not found")
    void createOrder_ProductNotFound() {
        // Arrange
        OrderRequest request = new OrderRequest("invalid-id", 1);
        when(productClient.getProduct("invalid-id"))
            .thenThrow(new ProductNotFoundException());

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(request))
            .isInstanceOf(ProductNotFoundException.class);
    }
}
```

## Integration Testing

```java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/users should create user")
    void createUser_ReturnsCreated() throws Exception {
        // Arrange
        CreateUserRequest request = new CreateUserRequest("Alice", "alice@test.com");

        // Act
        ResultActions result = mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        // Assert
        result.andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Alice"))
            .andExpect(header().string("Location", containsString("/api/users/")));
    }

    @Test
    @DisplayName("GET /api/users should return paginated results")
    void getUsers_ReturnsPaginated() throws Exception {
        mockMvc.perform(get("/api/users")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.totalElements").exists());
    }
}
```

## Test Data Builders

```java
public class OrderBuilder {
    private String id = "order-1";
    private String productId = "product-1";
    private int quantity = 1;
    private OrderStatus status = OrderStatus.PENDING;
    private LocalDateTime createdAt = LocalDateTime.now();

    public OrderBuilder id(String id) { this.id = id; return this; }
    public OrderBuilder productId(String productId) { this.productId = productId; return this; }
    public OrderBuilder quantity(int quantity) { this.quantity = quantity; return this; }
    public OrderBuilder status(OrderStatus status) { this.status = status; return this; }

    public Order build() {
        Order order = new Order();
        order.setId(id);
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setStatus(status);
        order.setCreatedAt(createdAt);
        return order;
    }

    public static OrderBuilder anOrder() { return new OrderBuilder(); }
}

// Usage
Order order = anOrder()
    .status(OrderStatus.CONFIRMED)
    .quantity(5)
    .build();
```

## BDD Style Testing

```java
class OrderProcessSteps {

    private OrderService orderService;
    private Order order;
    private Exception thrownException;

    @Given("an order with status PENDING")
    public void orderWithStatusPending() {
        order = anOrder().status(OrderStatus.PENDING).build();
    }

    @When("the order is confirmed")
    public void orderIsConfirmed() {
        orderService.confirmOrder(order.getId());
    }

    @Then("the order status should be CONFIRMED")
    public void orderStatusIsConfirmed() {
        Order updated = orderService.findById(order.getId());
        assertThat(updated.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }
}
```

## Mocking Patterns

```java
// Stub - predefined responses
when(repository.findById("1")).thenReturn(Optional.of(user));

// Argument matchers
when(service.process(anyString(), any())).thenReturn(result);
when(service.process(eq("test"), any())).thenReturn(result);

// Verification
verify(repository, times(1)).save(any());
verify(repository, never()).delete(any());
verify(repository, atLeastOnce()).findAll();

// Captors
ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
verify(repository).save(userCaptor.capture());
assertThat(userCaptor.getValue().getName()).isEqualTo("Alice");
```

## Test Organization

```
test/
├── unit/
│   └── com/example/service/
│       ├── OrderServiceTest.java
│       ├── OrderServiceTest$CreateOrderValidationTest.java
│       └── UserServiceTest.java
├── integration/
│   └── com/example/controller/
│       ├── UserControllerTest.java
│       └── OrderControllerTest.java
├── contract/
│   └── com/example/
│       └── UserContractTest.java
└── performance/
    └── com/example/
        └── LoadTest.java
```

## Coverage Guidelines

```
┌─────────────────────────────────────────────────────────────────┐
│  WHAT TO TEST                                                    │
├─────────────────────────────────────────────────────────────────┤
│  ✓ Business logic and calculations                              │
│  ✓ Edge cases and boundary conditions                           │
│  ✓ Error handling and exceptions                               │
│  ✓ State transitions                                            │
│  ✓ Happy path AND failure paths                                 │
├─────────────────────────────────────────────────────────────────┤
│  DON'T TEST                                                      │
├─────────────────────────────────────────────────────────────────┤
│  ✗ Generated code (getters, setters)                            │
│  ✗ Simple data transformations                                  │
│  ✗ Third-party libraries                                        │
│  ✗ Configuration loading                                        │
└─────────────────────────────────────────────────────────────────┘
```

## Naming Conventions

| Pattern | Example |
|---------|---------|
| Method_Scenario_Expected | `createOrder_ValidInput_ReturnsCreated` |
| Given_When_Then | `givenUserExists_whenDelete_thenNoContent` |
| Should_Expected | `shouldThrowException_WhenInvalidInput` |

## Test Isolation Principles

1. **Tests are independent** - no shared state
2. **Tests are repeatable** - deterministic results
3. **Tests are self-validating** - automatic pass/fail
4. **Tests are fast** - execute in milliseconds