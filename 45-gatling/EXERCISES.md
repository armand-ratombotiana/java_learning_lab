# Exercises - Gatling

## Exercise 1: Basic Simulation
Create an HTTP load test:

1. Define Simulation with injection profile
2. Create scenario with HTTP requests
3. Add check for status code 200
4. Run and review HTML report

## Exercise 2: Dynamic Data
Use feeders for data-driven testing:

1. Create CSV feeder with test data
2. Use feeder in scenario with ${variable}
3. Implement random feeder selection
4. Test with multiple data variations

## Exercise 3: Session Management
Handle cookies and sessions:

1. Capture session variables from response
2. Use captured values in subsequent requests
3. Handle authentication tokens
4. Test with session persistence

## Exercise 4: Advanced Scenarios
Build complex user workflows:

1. Create scenario with pause times
2. Add conditional logic based on response
3. Implement retry mechanism
4. Test multi-step business process

## Exercise 5: CI/CD Integration
Automate performance testing:

1. Configure Maven plugin for Gatling
2. Set up performance thresholds
3. Integrate with CI pipeline
4. Generate trend reports

## Bonus Challenge
Build a simulation that: simulates 500 concurrent users, each executing a unique user journey (browse, search, add-to-cart, checkout), with peak hours simulation (ramp up/down).