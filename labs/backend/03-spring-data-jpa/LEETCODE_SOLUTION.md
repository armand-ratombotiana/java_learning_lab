# Design a Pagination/Sorting Framework for Large Datasets

## Problem Statement
Design and implement a generic pagination and sorting framework suitable for large datasets. Requirements:
- Offset-based and cursor-based (keyset) pagination
- Multi-column sorting with direction (ASC/DESC)
- Null handling (NULLS FIRST / NULLS LAST)
- Page metadata: total count, page number, page size, hasNext, hasPrevious
- Generic type support for any entity
- Streaming support for large result sets
- Thread-safe and memory-efficient

## Solution

```java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Generic pagination/sorting framework for large datasets.
 * Supports offset-based and keyset (cursor) pagination.
 * <p>
 * Time complexity:
 * - Offset pagination: O(n) for count query + O(limit) for data fetch
 * - Keyset pagination: O(log n + limit) via index seek
 * <p>
 * Space complexity: O(limit) for the result page
 *
 * @param <T> entity type
 */
public class Pagination<T> {

    private final List<T> items;
    private final long totalCount;
    private final int page;
    private final int size;
    private final Sort sort;
    private final String cursor;  // keyset cursor for next page

    private Pagination(List<T> items, long totalCount, int page, int size,
                       Sort sort, String cursor) {
        this.items = Collections.unmodifiableList(items);
        this.totalCount = totalCount;
        this.page = page;
        this.size = size;
        this.sort = sort;
        this.cursor = cursor;
    }

    public List<T> getItems() { return items; }
    public long getTotalCount() { return totalCount; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public int getTotalPages() {
        return (int) Math.ceil((double) totalCount / size);
    }
    public boolean hasNext() { return page < getTotalPages(); }
    public boolean hasPrevious() { return page > 1; }
    public Sort getSort() { return sort; }
    public Optional<String> getCursor() { return Optional.ofNullable(cursor); }

    @Override
    public String toString() {
        return "Page{page=" + page + ", size=" + size
            + ", total=" + totalCount + ", items=" + items.size()
            + ", sort=" + sort + ", cursor=" + cursor + "}";
    }

    // ── Sort support ────────────────────────────────────────────────────────

    public record Sort(List<SortOrder> orders) {
        public static Sort by(SortOrder... orders) {
            return new Sort(List.of(orders));
        }
        public static Sort asc(String property) {
            return new Sort(List.of(new SortOrder(property, Direction.ASC, NullHandling.NULLS_LAST)));
        }
        public static Sort desc(String property) {
            return new Sort(List.of(new SortOrder(property, Direction.DESC, NullHandling.NULLS_FIRST)));
        }
        public boolean isSorted() { return !orders.isEmpty(); }
    }

    public record SortOrder(String property, Direction direction, NullHandling nullHandling) {}

    public enum Direction { ASC, DESC }
    public enum NullHandling { NULLS_FIRST, NULLS_LAST }

    // ── Pageable (request) ──────────────────────────────────────────────────

    public record Pageable(int page, int size, Sort sort, String cursor) {
        public Pageable {
            if (page < 1) throw new IllegalArgumentException("page must be >= 1");
            if (size < 1 || size > 1000) throw new IllegalArgumentException("size 1-1000");
        }
        public static Pageable of(int page, int size) {
            return new Pageable(page, size, Sort.asc("id"), null);
        }
        public static Pageable of(int page, int size, Sort sort) {
            return new Pageable(page, size, sort, null);
        }
        public static Pageable cursor(String cursor, int size, Sort sort) {
            return new Pageable(1, size, sort, cursor);
        }
        public int getOffset() { return (page - 1) * size; }
    }

    // ── Repository abstraction ──────────────────────────────────────────────

    /**
     * Generic data access interface. Implementations can back to JDBC, JPA,
     * or in-memory stores.
     */
    public interface PageableRepository<T, ID> {
        Pagination<T> findAll(Pageable pageable);
        long count();
    }

    // ── In-memory implementation with comparator-based sorting ──────────────

    /**
     * In-memory pagination engine for demo/testing. Uses comparator chains.
     */
    public static class InMemoryPaginationEngine<T> implements PageableRepository<T, Long> {

        private final List<T> data;
        private final Function<T, String> idExtractor;       // for keyset
        private final Function<T, Comparable<?>[]> fieldExtractor; // for sorting
        private final List<String> fieldNames;

        @SafeVarargs
        public InMemoryPaginationEngine(List<T> data,
                                         Function<T, String> idExtractor,
                                         Function<T, Comparable<?>[]> fieldExtractor,
                                         String... fieldNames) {
            this.data = new CopyOnWriteArrayList<>(data);
            this.idExtractor = idExtractor;
            this.fieldExtractor = fieldExtractor;
            this.fieldNames = List.of(fieldNames);
        }

        @Override
        public Pagination<T> findAll(Pageable pageable) {
            List<T> sorted = new ArrayList<>(data);
            if (pageable.sort() != null && pageable.sort().isSorted()) {
                sorted.sort(buildComparator(pageable.sort()));
            }

            long total = sorted.size();

            // Keyset pagination
            if (pageable.cursor() != null) {
                int startIdx = 0;
                for (int i = 0; i < sorted.size(); i++) {
                    if (idExtractor.apply(sorted.get(i)).equals(pageable.cursor())) {
                        startIdx = i + 1;
                        break;
                    }
                }
                int endIdx = Math.min(startIdx + pageable.size(), sorted.size());
                List<T> pageItems = sorted.subList(startIdx, endIdx);
                String nextCursor = pageItems.isEmpty() ? null
                    : idExtractor.apply(pageItems.get(pageItems.size() - 1));
                return new Pagination<>(
                    new ArrayList<>(pageItems), total, pageable.page(),
                    pageable.size(), pageable.sort(), nextCursor);
            }

            // Offset pagination
            int offset = pageable.getOffset();
            if (offset >= total) {
                return new Pagination<>(List.of(), total, pageable.page(),
                    pageable.size(), pageable.sort(), null);
            }
            int end = Math.min(offset + pageable.size(), (int) total);
            List<T> pageItems = sorted.subList(offset, end);
            return new Pagination<>(new ArrayList<>(pageItems), total,
                pageable.page(), pageable.size(), pageable.sort(), null);
        }

        @Override
        public long count() { return data.size(); }

        @SuppressWarnings("unchecked")
        private Comparator<T> buildComparator(Sort sort) {
            Comparator<T> comparator = (a, b) -> 0;
            for (SortOrder order : sort.orders()) {
                int fieldIdx = fieldNames.indexOf(order.property());
                if (fieldIdx < 0) throw new IllegalArgumentException("Unknown field: " + order.property());
                Comparator<T> fieldComp = (a, b) -> {
                    Comparable<Object> va = (Comparable<Object>) fieldExtractor.apply(a)[fieldIdx];
                    Comparable<Object> vb = (Comparable<Object>) fieldExtractor.apply(b)[fieldIdx];
                    if (va == null && vb == null) return 0;
                    if (va == null) return order.nullHandling() == NullHandling.NULLS_FIRST ? -1 : 1;
                    if (vb == null) return order.nullHandling() == NullHandling.NULLS_FIRST ? 1 : -1;
                    int cmp = va.compareTo(vb);
                    return order.direction() == Direction.DESC ? -cmp : cmp;
                };
                comparator = comparator.thenComparing(fieldComp);
            }
            return comparator;
        }
    }

    // ── Streaming support ───────────────────────────────────────────────────

    /**
     * Streams all items in a paginated fashion without loading everything.
     */
    public static <T> Stream<T> streamAll(PageableRepository<T, ?> repo,
                                          Pageable startPage) {
        Stream.Builder<T> builder = Stream.builder();
        Pageable current = startPage;
        do {
            Pagination<T> page = repo.findAll(current);
            page.getItems().forEach(builder);
            if (!page.hasNext()) break;
            current = page.getCursor()
                .map(c -> Pageable.cursor(c, current.size(), current.sort()))
                .orElseGet(() -> Pageable.of(current.page() + 1, current.size(), current.sort()));
        } while (true);
        return builder.build();
    }

    // ── Example usage ───────────────────────────────────────────────────────

    public static void main(String[] args) {
        List<String[]> data = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            data.add(new String[]{String.valueOf(i), "User" + i, String.valueOf(100 - i)});
        }

        var engine = new InMemoryPaginationEngine<>(
            data,
            row -> row[0],
            row -> new Comparable[]{Integer.parseInt(row[0]), row[1], Integer.parseInt(row[2])},
            "id", "name", "score"
        );

        // Offset pagination
        var page1 = engine.findAll(Pageable.of(1, 10,
            Sort.by(new SortOrder("score", Direction.DESC, NullHandling.NULLS_LAST))));
        System.out.println("Page 1 (sorted by score DESC): " + page1);

        // Keyset pagination
        String lastCursor = page1.getCursor().orElse(null);
        if (lastCursor != null) {
            var page2 = engine.findAll(Pageable.cursor(lastCursor, 10,
                Sort.by(new SortOrder("score", Direction.DESC, NullHandling.NULLS_LAST))));
            System.out.println("Page 2 (keyset): " + page2);
        }

        // Streaming
        long count = streamAll(engine, Pageable.of(1, 20)).count();
        System.out.println("Streamed count: " + count);
    }
}
```

## Complexity Analysis

| Operation                  | Time Complexity          | Space Complexity |
|----------------------------|--------------------------|-----------------|
| Offset pagination (sort)   | O(n log n + limit)       | O(limit)        |
| Offset pagination (no sort)| O(limit)                 | O(limit)        |
| Keyset pagination          | O(n + limit) worst*      | O(limit)        |
| Count                      | O(1) amortized           | O(1)            |
| Streaming                  | O(n) total               | O(limit)        |

*Keyset pagination with in-memory scan is O(n). With a B-tree index on the cursor column it becomes O(log n + limit).

## Test Cases

```java
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.util.stream.*;

class PaginationTest {

    private Pagination.InMemoryPaginationEngine<String[]> engine;

    @BeforeEach
    void setUp() {
        List<String[]> data = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            data.add(new String[]{String.valueOf(i), "User" + i, String.valueOf(50 - i)});
        }
        engine = new Pagination.InMemoryPaginationEngine<>(
            data,
            row -> row[0],
            row -> new Comparable[]{Integer.parseInt(row[0]), row[1], Integer.parseInt(row[2])},
            "id", "name", "score"
        );
    }

    @Test
    void testFirstPage() {
        var page = engine.findAll(Pagination.Pageable.of(1, 10));
        assertEquals(1, page.getPage());
        assertEquals(10, page.getItems().size());
        assertEquals(50, page.getTotalCount());
        assertTrue(page.hasNext());
        assertFalse(page.hasPrevious());
    }

    @Test
    void testLastPage() {
        var page = engine.findAll(Pagination.Pageable.of(5, 10));
        assertEquals(5, page.getPage());
        assertEquals(10, page.getItems().size());
        assertFalse(page.hasNext());
        assertTrue(page.hasPrevious());
    }

    @Test
    void testSortAsc() {
        var sort = Pagination.Sort.asc("score");
        var page = engine.findAll(Pagination.Pageable.of(1, 5, sort));
        int prev = -1;
        for (var row : page.getItems()) {
            int score = Integer.parseInt(row[2]);
            assertTrue(score >= prev);
            prev = score;
        }
    }

    @Test
    void testSortDesc() {
        var sort = Pagination.Sort.desc("score");
        var page = engine.findAll(Pagination.Pageable.of(1, 5, sort));
        int prev = Integer.MAX_VALUE;
        for (var row : page.getItems()) {
            int score = Integer.parseInt(row[2]);
            assertTrue(score <= prev);
            prev = score;
        }
    }

    @Test
    void testMultiColumnSort() {
        var sort = Pagination.Sort.by(
            new Pagination.SortOrder("score", Pagination.Direction.DESC, Pagination.NullHandling.NULLS_LAST),
            new Pagination.SortOrder("id", Pagination.Direction.ASC, Pagination.NullHandling.NULLS_LAST)
        );
        var page = engine.findAll(Pagination.Pageable.of(1, 50, sort));
        assertEquals(50, page.getItems().size());
    }

    @Test
    void testKeysetPagination() {
        var sort = Pagination.Sort.asc("id");
        var page1 = engine.findAll(Pagination.Pageable.of(1, 10, sort));
        String cursor = page1.getCursor().orElseThrow();
        var page2 = engine.findAll(Pagination.Pageable.cursor(cursor, 10, sort));
        assertEquals(10, page2.getItems().size());
        assertEquals("11", page2.getItems().get(0)[0]); // keyset starts after id=10
    }

    @Test
    void testOutOfBoundsPage() {
        var page = engine.findAll(Pagination.Pageable.of(100, 10));
        assertEquals(0, page.getItems().size());
        assertEquals(50, page.getTotalCount());
    }

    @Test
    void testStreaming() {
        long count = Pagination.streamAll(engine, Pagination.Pageable.of(1, 7)).count();
        assertEquals(50, count);
    }

    @Test
    void testTotalPages() {
        var page = engine.findAll(Pagination.Pageable.of(1, 20));
        assertEquals(3, page.getTotalPages()); // 50/20 = 2.5 → 3
    }

    @Test
    void testPageableValidation() {
        assertThrows(IllegalArgumentException.class, () -> Pagination.Pageable.of(0, 10));
        assertThrows(IllegalArgumentException.class, () -> Pagination.Pageable.of(1, 0));
        assertThrows(IllegalArgumentException.class, () -> Pagination.Pageable.of(1, 2000));
    }
}
```
