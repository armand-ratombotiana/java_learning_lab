# Common Mistakes

- Merge Sort: Creating new arrays at each merge — use single auxiliary array
- Quick Sort: Poor pivot choice on sorted data — always use median-of-three
- Quick Sort: Stack overflow — switch to Insertion Sort for small partitions
- Heap Sort: Off-by-one in heap indexing — children are 2i+1, 2i+2
- Heap Sort: Using siftUp instead of siftDown for heap construction
- Stability assumptions — Quick Sort and Heap Sort are NOT stable
- Integer overflow: (lo+hi)/2 can overflow; use lo+(hi-lo)/2
