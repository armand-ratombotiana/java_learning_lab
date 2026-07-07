# Debugging: Space-Filling Curves

## Common Debugging Techniques

### Print Debugging

Add temporary logging to trace the execution of key operations. Log the state before and after each modification.

### Visual Inspection

For structures with complex internal organization, rendering the structure as text can help identify structural violations.

### Unit Test Isolation

Isolate failing test cases and reduce them to the minimal reproduction case. This simplifies debugging significantly.

## Debugging Checklist

1. **Validate Invariants**: After each operation, verify that all structural invariants are maintained.

2. **Check Boundary Conditions**: Test with empty structure, single element, and full capacity.

3. **Verify Index Calculations**: Double-check all hash computations and index manipulations.

4. **Monitor Performance**: Performance degradation often indicates a bug causing excessive rebalancing or traversal.

## Tools and Techniques

- Use assertions to verify invariants during development
- Profile memory usage to detect leaks or excessive allocation
- Use debugger breakpoints on critical paths
- Add toString methods for debugging output

## Common Bug Patterns

- Hash collisions not properly resolved
- Metadata not updated after modifications
- Rebalancing triggered at wrong thresholds
- Thread safety violations in concurrent usage
- Off-by-one errors in index operations
