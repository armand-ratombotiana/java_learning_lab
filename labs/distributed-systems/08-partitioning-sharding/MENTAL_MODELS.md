# Mental Models for Partitioning

## The Filing Cabinet Model
- **Range sharding**: Files organized A-M in one drawer, N-Z in another
- **Hash sharding**: Files assigned by hash of filename
- **Consistent hashing**: Each drawer is a point on a circular rack

## The Library Model
- **Range**: Books by author's last name (A-C, D-F, etc.)
- **Hash**: Books assigned by hash of ISBN
- **Directory**: Card catalog tells you which shelf

## The Pizza Model
- Each pizza is a shard
- Toppings distributed across the pizza
- Each slice gets some of each topping (consistent hashing)
- Add more pizza = more capacity (rebalancing)
