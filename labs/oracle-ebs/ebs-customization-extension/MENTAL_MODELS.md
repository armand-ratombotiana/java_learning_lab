# Mental Models: EBS Customization and Extension

## 1. The Multi-Tier Model

Think of EBS as a three-layer cake: Presentation (top), Application (middle), Database (bottom). Each layer talks only to neighbors.

## 2. The Responsibility Model

Think of a responsibility as a hat. Different hats grant different access. Users can wear multiple hats.

## 3. The Concurrent Processing Model

Concurrent managers are conveyor belts. Requests are boxes on the belt. Workers pick and process them. Results go to output area.

## 4. The MOAC Model

Operating units are separate rooms. MOAC lets you see all rooms from one hallway. You can only act in rooms you have access to.

## 5. The EBR Model

Parallel universe for code changes. Users in old universe don't see changes until merge event (cutover).

## 6. The Workflow Model

Flowchart that executes itself. Nodes are activities. Arrows are transitions. Token represents execution state.
