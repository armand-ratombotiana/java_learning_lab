# Math: EBS Setup and Configuration

## 1. Allocation Algorithms

In EBS, proportional allocation is used for cost distributions, expense allocations, and resource scheduling. The formula is: total multiplied by weight divided by sum of weights.

## 2. Weighted Average Costing

Inventory valuation uses weighted average: average price equals sum of quantity times price divided by sum of quantity.

## 3. Scheduling Mathematics

MRP uses Critical Path Method. Earliest start equals max of predecessors earliest finish. Earliest finish equals earliest start plus duration.

## 4. Available to Promise

ATP at time t equals on-hand at t minus sum of demand from t to t+n plus sum of supply from t to t+n.

## 5. Payroll Calculations

Gross pay equals rate times hours times multiplier. Tax equals max of zero or gross times rate minus allowances times exemption.

## 6. Depreciation

Straight-line: cost minus salvage divided by life. Declining balance: book value times rate.

## 7. Currency Conversion

Converted amount equals original amount times exchange rate. EBS supports spot, period-end, and average rates.

## 8. Summary

These mathematical foundations appear throughout EBS modules. The Java simulations implement these formulas with appropriate precision.
