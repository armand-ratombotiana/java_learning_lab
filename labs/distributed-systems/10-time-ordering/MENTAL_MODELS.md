# Mental Models for Time and Ordering

## The String Model (Lamport Clocks)
- Each event is a bead on a string
- String gets longer as events happen
- Cannot tell which events are concurrent

## The Spreadsheet Model (Vector Clocks)
- Each process has a column in the spreadsheet
- Each event increments that column
- Compare two rows to determine causality

## The Kitchen Timer Model (HLC)
- Physical clock sets the max time
- Logical counter for events in same physical tick
- Best of both worlds

## The GPS Model (TrueTime)
- Clock interval [earliest, latest]
- Not a single time but a range
- Wait for uncertainty to pass before committing
