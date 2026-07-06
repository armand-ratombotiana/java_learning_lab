# Mathematical Foundation of Garbage Collection

## Allocation Rate and GC Frequency
If the allocation rate is A (MB/s) and the young generation size is Y (MB), the time between minor GCs is approximately Y/A. For A=500 MB/s and Y=2 GB, a minor GC occurs every ~4 seconds. Increasing Y reduces GC frequency but increases pause time.

## Promoted Object Rate
Objects that survive a young collection are promoted to the old generation. If the promotion rate is P (MB/s) and old generation size is O (MB), the time to fill the old generation is O/P. If P exceeds the old generation collection rate, the old gen fills up and a major GC is triggered.

## GC Pause Time (Parallel)
For parallel marking of N live objects with T threads: pause ≈ N/T × k, where k is per-object processing time (~10-100ns). With 10M objects and 4 threads: ~25-250ms.

## G1 Pause Time Prediction
G1 predicts pause time based on:
- Number of regions to collect (more regions = longer pause)
- Live data in those regions (more live data = more evacuation)
- Remembered set size (more cross-references = more scanning)

G1 selects regions to achieve the pause time target (-XX:MaxGCPauseMillis).

## ZGC Marking Throughput
ZGC concurrent marking throughput is limited by:
- Load barrier overhead (~2-5% CPU)
- Mark queue processing (parallel)
- The marking bitmap

Typical throughput: 1-5 GB/s of live object scanning per core.

## Heap Fragmentation
For a heap with free blocks of sizes S1, S2, ..., Sn, fragmentation prevents allocating an object of size M if no single block is ≥ M. The fragmentation ratio is (total free - largest free) / total free. G1's region-based design limits fragmentation because any free region can accommodate any object that fits in a region.

## GC Overhead
Total GC overhead = sum(pause_time_i) / total_time. If overhead exceeds ~10-20%, the application spends too much time in GC. Increase heap size or adjust GC tuning.

## Reference Processing
Soft, weak, phantom, and final references require special handling during GC. The cost is proportional to the number of reference objects, not the number of reachable objects.
