# Visual Guide — Greedy

## Activity Selection
`
A(1,4) B(3,5) C(0,6) D(5,7) E(3,8) F(5,9) G(6,10) H(8,11)
Sorted by finish:
A[1-4] → select
D[5-7] → select
H[8-11] → select
Result: {A, D, H}
`

## Huffman Tree
a=5, b=9, c=12, d=13, e=16, f=45
Step 1: a+b=14
Step 2: c+d=25
Step 3: 14+e=30
Step 4: 25+30=55
Step 5: f+55=100

Result: f:0, a:1000, b:1001, c:101, d:110, e:111
