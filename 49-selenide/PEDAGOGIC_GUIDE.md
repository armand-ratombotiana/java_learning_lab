# Pedagogic Guide - Selenide

## Learning Path

### Phase 1: Fundamentals
1. Understand element selection syntax
2. Learn basic actions (click, type, submit)
3. Use condition assertions

### Phase 2: Intermediate
1. Implement Page Object pattern
2. Handle dynamic elements with $ and $$
3. Configure timeouts and implicitlyWait

### Phase 3: Advanced
1. Custom conditions
2. Browser configuration
3. Test listeners and reports

## Key Concepts

| Concept | Description |
|---------|-------------|
| $() | Single element selection |
| $$() | Multiple element selection |
| should() | Assert with wait |
| shouldHave() | Assert text/content |

## Comparison with Selenium
- Selenide: Concise, auto-wait, built-in features
- Selenium: More control, requires more setup

## Best Practices
- Use Page Objects for maintainability
- Avoid sleeps, use conditions instead
- Take screenshots on failures