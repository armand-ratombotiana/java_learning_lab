# Visual Guide — String Matching

## KMP Prefix Function Visualization

Pattern: ABABAC
i=0: A          pi[0]=0
i=1: A B        pi[1]=0  (A != B)
i=2: A B A      pi[2]=1  (A matches A)
i=3: A B A B    pi[3]=2  (AB matches AB)
i=4: A B A B A  pi[4]=3  (ABA matches ABA)
i=5: A B A B A C pi[5]=0 (ABABAC no suffix-prefix match)

## Boyer-Moore Shift

Text:    F I N D _ I N _ A _ L A K E _ F I N D _ M E
Pattern:         A _ L A K E
                  ^ mismatch at K vs I -> bad char 'I' at position 2 -> shift by 2

## Rabin-Karp Window Rolling

Window 1: "FIND" -> hash h1
Window 2: "IND_" -> hash h2 = (h1 - 'F'*B^3)*B + '_'
Window 3: "ND_I" -> hash h3 = (h2 - 'I'*B^3)*B + 'I'
