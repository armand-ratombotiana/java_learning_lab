# Pedagogic Guide: Integration Testing

## Learning Path

### Phase 1: Foundation (Day 1)
- Understand Spring Boot test annotations
- Learn test context creation
- Study test slices and their purpose

### Phase 2: Core Skills (Day 2-3)
- Write `@SpringBootTest` cases
- Configure MockMvc for web testing
- Use test slices for focused testing

### Phase 3: Advanced (Day 4-5)
- Customize test contexts
- Test security configurations
- Optimize test performance

## Key Concepts

| Concept | Description |
|---------|-------------|
| `@SpringBootTest` | Full integration test with context |
| `WebEnvironment` | Configure web layer for tests |
| Test Slices | Focused tests (DataJpaTest, WebMvcTest) |
| MockMvc | Test controllers without server |

## Prerequisites
- Spring Boot fundamentals
- JUnit 5 knowledge
- Basic REST understanding

## Common Pitfalls
- Slow context startup; use slices
- Missing web layer config
- Test data pollution