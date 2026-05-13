# Visual Guide to Collections

## ArrayList Structure

```
┌───┬───┬───┬───┬───┬───┬───┬───┬───┬───┐
│ 0 │ 1 │ 2 │ 3 │ 4 │ 5 │ 6 │ 7 │ 8 │ 9 │
└───┴───┴───┴───┴───┴───┴───┴───┴───┴───┘
```

## HashMap Structure

```
┌───┐
│ 0 │ → [Key1, Value1] → [Key2, Value2]
├───┤
│ 1 │ → null
├───┤
│ 2 │ → [Key3, Value3]
├───┤
│...│
└───┘
   Bucket Array              Linked List
```

## LinkedList Structure

```
null ← [prev|data|next] ↔ [prev|data|next] ↔ [prev|data|next] → null
```

## TreeMap Structure

```
        [Root]
       /      \
   [Left]    [Right]
     /          \
  ...           ...
```

Red-Black tree showing balanced structure