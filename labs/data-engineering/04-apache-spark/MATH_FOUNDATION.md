# Math Foundation

## Memory
`Total = executor.memory * instances`
`Execution = Total * fraction * (1 - storageFraction)`
`Storage = Total * fraction * storageFraction`

## Shuffle Cost
`Shuffle = Serialization + Network + Spill + Deserialization`

## Join Costs
- Broadcast: O(n)
- Hash: O(n + m)
- Sort Merge: O(n log n + m log m + n + m)
