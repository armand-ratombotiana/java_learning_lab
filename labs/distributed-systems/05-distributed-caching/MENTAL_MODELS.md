# Mental Models for Distributed Caching

## The Library Model
- **Cache**: Popular books on a front shelf
- **Database**: All books in the back stacks
- **Miss**: Send to the back stacks to find the book
- **Eviction**: Remove books no one has read recently

## The Notebook Model
- Write important info on a sticky note on your desk (cache)
- Full details in the filing cabinet (database)
- When someone changes the filing cabinet, update the sticky note

## The Coffee Shop Model
- **Cache**: Barista remembers regular orders
- **Miss**: New customer, write down order
- **Eviction**: Forgets orders after a while (TTL)
- **Coherence**: All baristas have the same memory
