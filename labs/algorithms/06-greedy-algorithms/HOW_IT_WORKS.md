# How Greedy Works

## Activity Selection
`java
List<Activity> select(List<Activity> activities) {
    activities.sort(Comparator.comparing(Activity::end));
    List<Activity> selected = new ArrayList<>();
    int lastEnd = Integer.MIN_VALUE;
    for (Activity a : activities) {
        if (a.start >= lastEnd) {
            selected.add(a);
            lastEnd = a.end;
        }
    }
    return selected;
}
`

## Fractional Knapsack
`java
double fractionalKnapsack(Item[] items, int capacity) {
    Arrays.sort(items, (a, b) -> 
        Double.compare(b.value/b.weight, a.value/a.weight));
    double totalValue = 0;
    for (Item item : items) {
        if (capacity >= item.weight) {
            totalValue += item.value;
            capacity -= item.weight;
        } else {
            totalValue += item.value * ((double) capacity / item.weight);
            break;
        }
    }
    return totalValue;
}
`
"@

wf "INTERNALS.md" @"
# Greedy — Internal Mechanics

## Huffman Coding
`java
class HuffmanNode implements Comparable<HuffmanNode> {
    char ch; int freq; HuffmanNode left, right;
    public int compareTo(HuffmanNode o) { return this.freq - o.freq; }
}

HuffmanNode buildTree(Map<Character, Integer> freqMap) {
    PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
    freqMap.forEach((ch, f) -> pq.add(new HuffmanNode(ch, f)));
    while (pq.size() > 1) {
        HuffmanNode left = pq.poll(), right = pq.poll();
        HuffmanNode parent = new HuffmanNode('\0', left.freq + right.freq);
        parent.left = left; parent.right = right;
        pq.add(parent);
    }
    return pq.poll();
}

void buildCodes(HuffmanNode node, String code, Map<Character, String> codes) {
    if (node.left == null && node.right == null) {
        codes.put(node.ch, code);
        return;
    }
    buildCodes(node.left, code + "0", codes);
    buildCodes(node.right, code + "1", codes);
}
`
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Greedy

## Exchange Argument
Prove greedy optimality: Show any optimal solution can be transformed into greedy solution without decreasing optimality.

Example for Activity Selection:
1. Greedy picks A (earliest finish)
2. In optimal, let B be first activity
3. If B ≠ A, replace B with A (A finishes no later than B)
4. Result still has room for remaining activities
5. By induction, greedy is optimal

## Matroid Definition
M = (E, I): E is finite set, I ⊆ 2ᴱ (independent sets)
- Ø ∈ I
- Hereditary: A ∈ I, B ⊆ A → B ∈ I
- Augmentation: A,B ∈ I, |A| < |B| → ∃e ∈ B\A: A∪{e} ∈ I
