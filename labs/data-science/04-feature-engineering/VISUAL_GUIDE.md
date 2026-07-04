# Visual Guide to Feature Engineering

## Log Transform Effect

```
Before (right-skewed):         After (log-transformed):
   ██                              ████████
   ██████                          ██████████
   ████████████               ████████████████
   ██████████████████           ████████████████████
   ████████████████████████      ████████████████████████
   0   100  200  300             0.0  2.0  4.0  6.0
```

## One-Hot Encoding Visual

```
Before:  After:
City     is_NY  is_SF  is_CHI
NY        1       0       0
SF        0       1       0
CHI       0       0       1
NY        1       0       0
```

## Interaction Effect Example

```
Predicting house price: price = f(sqft, bedrooms, sqft × bedrooms)

sqft × bedrooms captures: a 2000 sqft house with 2 bedrooms (1000 sqft/bedroom)
                          vs a 2000 sqft house with 4 bedrooms (500 sqft/bedroom)
                          → different price implications
```

## Feature Importance Visualization

```
Feature Importance (Gain)
┌─────────────────────────────────────────────┐
│ income          ████████████████████████   │
│ age             ████████████              │
│ credit_score    █████████                 │
│ zip_code_encoded ███████                  │
│ is_employed      ████                      │
│ num_accounts     ██                         │
│ log_transactions  █                          │
└─────────────────────────────────────────────┘
```
