# Mental Models for Advanced Orchestration

## 1. Thermostat (HPA)
HPA works like a thermostat: set target temperature (CPU 80%), system continuously measures and adjusts airflow (replicas) to maintain target.

## 2. Elevator Maintenance (PDB)
PDBs are like elevator maintenance signs — "at least 2 of 3 elevators must remain operational." Maintenance (voluntary disruption) stops if it would violate the minimum.

## 3. Deployment Strategies as Traffic Patterns
- **RollingUpdate** = Escalator (gradual, continuous)
- **Recreate** = Old bridge demolished before new one built (downtime)
- **Blue/Green** = Two identical bridges, switch traffic overnight
- **Canary** = Pedestrian walkway tested before opening to all traffic

## 4. QoS Classes as Hotel Categories
- **Guaranteed** = 5-star suite (reserved resources, premium)
- **Burstable** = Standard room (some flexibility)
- **BestEffort** = Hostel (take what's available, first evicted under pressure)
