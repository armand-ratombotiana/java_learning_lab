package com.sd.apidesign;

import java.util.*;
import java.util.stream.*;

public class Pagination {

    public static class PageRequest {
        public final int page;
        public final int size;
        public final String cursor;

        public PageRequest(int page, int size) {
            this.page = page;
            this.size = size;
            this.cursor = null;
        }

        public PageRequest(String cursor, int size) {
            this.page = -1;
            this.size = size;
            this.cursor = cursor;
        }
    }

    public static class PageResponse<T> {
        public final List<T> items;
        public final int page;
        public final int totalPages;
        public final long totalItems;
        public final String nextCursor;
        public final boolean hasMore;

        public PageResponse(List<T> items, int page, int size, long total) {
            this.items = items;
            this.page = page;
            this.totalPages = (int) Math.ceil((double) total / size);
            this.totalItems = total;
            this.nextCursor = null;
            this.hasMore = page < totalPages - 1;
        }

        public PageResponse(List<T> items, String nextCursor, boolean hasMore) {
            this.items = items;
            this.page = 0;
            this.totalPages = 0;
            this.totalItems = 0;
            this.nextCursor = nextCursor;
            this.hasMore = hasMore;
        }
    }

    public static class PaginatedRepository {
        private final List<String> allData;

        public PaginatedRepository(List<String> data) {
            this.allData = new ArrayList<>(data);
        }

        public PageResponse<String> getPage(PageRequest req) {
            int start = req.page * req.size;
            int end = Math.min(start + req.size, allData.size());
            List<String> items = allData.subList(start, end);
            return new PageResponse<>(items, req.page, req.size, allData.size());
        }

        public PageResponse<String> getPageWithCursor(PageRequest req) {
            int startIdx = 0;
            if (req.cursor != null) {
                for (int i = 0; i < allData.size(); i++) {
                    if (allData.get(i).equals(req.cursor)) {
                        startIdx = i + 1;
                        break;
                    }
                }
            }
            int end = Math.min(startIdx + req.size, allData.size());
            List<String> items = allData.subList(startIdx, end);
            String nextCursor = end < allData.size() ? allData.get(end - 1) : null;
            return new PageResponse<>(items, nextCursor, end < allData.size());
        }
    }

    public static void main(String[] args) {
        List<String> allItems = IntStream.range(1, 53)
            .mapToObj(i -> "item-" + i)
            .toList();

        PaginatedRepository repo = new PaginatedRepository(allItems);

        System.out.println("=== Offset-based Pagination ===");
        PageResponse<String> page1 = repo.getPage(new PageRequest(0, 10));
        System.out.println("Page 1: " + page1.items.size() + " items, total=" + page1.totalItems
            + ", pages=" + page1.totalPages + ", hasMore=" + page1.hasMore);

        PageResponse<String> page5 = repo.getPage(new PageRequest(4, 10));
        System.out.println("Page 5: " + page5.items.size() + " items, hasMore=" + page5.hasMore);

        System.out.println("\n=== Cursor-based Pagination ===");
        PageResponse<String> cursorPage = repo.getPageWithCursor(new PageRequest("item-5", 5));
        System.out.println("Cursor page: " + cursorPage.items + " nextCursor=" + cursorPage.nextCursor
            + " hasMore=" + cursorPage.hasMore);
    }
}
