# Time and Ordering: Internals

## Hybrid Logical Clock (HLC)

### Components
- **l**: Logical component (incremented within same physical time)
- **c**: Physical component (system clock)
- When physical time advances: l = max(l, pt), c = pt
- When local event: l = l + 1, c = c
- On receive: l = max(l, other.l, pt), c = max(c, other.c, pt)

```java
public class HLC {
    private long l; // logical
    private long c; // physical (wall clock)
    
    public synchronized HC now() {
        long pt = System.currentTimeMillis();
        if (pt > c) {
            l = Math.max(l, pt - c) + 1;
            c = pt;
        } else {
            l = l + 1;
        }
        return new HC(c, l);
    }
    
    public synchronized void update(HLC other) {
        long pt = System.currentTimeMillis();
        c = Math.max(Math.max(c, other.c), pt);
        l = Math.max(Math.max(l, other.l), c - pt) + 1;
    }
    
    record HC(long physical, long logical) implements Comparable<HC> {
        public int compareTo(HC o) {
            int cmp = Long.compare(physical, o.physical);
            return cmp != 0 ? cmp : Long.compare(logical, o.logical);
        }
    }
}
```

## TrueTime (Spanner)
- GPS + atomic clocks in each datacenter
- Returns interval [earliest, latest]
- Uncertainty ε = latest - earliest
- Wait for ε before making commit visible
- Typical ε = 1-7ms
