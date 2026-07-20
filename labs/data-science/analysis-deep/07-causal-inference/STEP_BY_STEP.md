# Step-by-Step Guide: Causal Inference

## Implementation Walkthrough

### Step 1: Set Up Project Structure
`ash
mkdir -p src/main/java/com/mathlab/causalinference
mkdir -p src/test/java/com/mathlab/causalinference
`

### Step 2: Define the Main Class
`java
package com.mathlab.causalinference;
public class CausalInference { }
`

### Step 3: Implement Core Algorithm
1. Define public API method
2. Validate inputs
3. Implement computation
4. Return result

### Step 4: Add Helper Methods
Extract sub-computations for readability and reuse.

### Step 5: Write Unit Tests
`java
@Test void testBasic() {
    assertEquals(expected, CausalInference.compute(input), 1e-10);
}
`

### Step 6: Run Tests
`ash
mvn test
`

### Step 7: Optimize
Profile and apply targeted optimizations.

### Step 8: Document
Add Javadoc and ensure documentation is current.

## Verification Checklist
- [ ] Basic functionality works
- [ ] Edge cases handled
- [ ] Numerical accuracy acceptable
- [ ] Performance acceptable
- [ ] Tests cover critical paths
