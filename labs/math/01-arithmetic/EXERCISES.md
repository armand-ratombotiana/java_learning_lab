# Exercises: Arithmetic

## Level 1: Basic Operations

**1.** Calculate: 1234 + 5678 = ?
- Solution: 1234 + 5678 = 6912

**2.** Find the product: 987 × 654
- Solution: 987 × 654 = 645,798

**3.** Compute: 10000 - 4321 = ?
- Solution: 10000 - 4321 = 5679

**4.** Evaluate: 144 ÷ 12 = ?
- Solution: 144 ÷ 12 = 12

**5.** What is 2⁸?
- Solution: 2⁸ = 256

## Level 2: Number Theory

**6.** Determine if 127 is prime. Prove your answer.
- Solution: 127 is prime. Proof: √127 ≈ 11.3. Check divisibility by all primes ≤ 11: 2 (no, odd), 3 (1+2+7=10, not divisible), 5 (no), 7 (127÷7=18.1), 11 (127÷11=11.5). None divide 127.

**7.** Find gcd(144, 96) using the Euclidean algorithm.
- Solution: 144 = 96×1 + 48, 96 = 48×2 + 0 → gcd = 48

**8.** Find lcm(144, 96).
- Solution: lcm = (144×96)/48 = 288

**9.** Using extended Euclidean algorithm, find x, y such that 35x + 15y = gcd(35, 15).
- Solution: 35 = 15×2 + 5, 15 = 5×3 + 0. Working backwards: 5 = 35 - 15×2. gcd(35,15)=5. So 35(-1) + 15(2) = 5. Solution: x = -1, y = 2

**10.** Find all prime factors of 1260.
- Solution: 1260 = 2² × 3² × 5 × 7

## Level 3: Modular Arithmetic

**11.** Compute: 2¹⁰ mod 13
- Solution: 2¹⁰ mod 13 = 1024 mod 13 = 1024 - 13×78 = 1024 - 1014 = 10

**12.** Find the modular inverse of 3 mod 17.
- Solution: Need x such that 3x ≡ 1 (mod 17). 3×6 = 18 ≡ 1 (mod 17). So inverse is 6.

**13.** Solve: x ≡ 2 (mod 5), x ≡ 3 (mod 7) using CRT.
- Solution: x = 2 + 5k. Testing k=0 to 6: 2,7,12,17,22,27,32. 27 ≡ 3 (mod 7). Solution: x ≡ 27 (mod 35)

**14.** What is the last digit of 7¹⁰⁰?
- Solution: 7⁴ = 2401 ends in 1. 7¹⁰⁰ = (7⁴)²⁵ ends in 1.

**15.** If 5x ≡ 3 (mod 7), find x.
- Solution: 5×3=15≡1 (mod 7), so x≡3×3=9≡2 (mod 7). Solution: x=2

## Level 4: Sequences and Series

**16.** Find the 15th Fibonacci number.
- Solution: F₁₅ = 610

**17.** Calculate: 1 + 2 + 3 + ... + 100
- Solution: n(n+1)/2 = 100×101/2 = 5050

**18.** Find the sum of first 20 even numbers.
- Solution: 2+4+6+...+40 = 2(1+2+...+20) = 2×20×21/2 = 420

**19.** What is 50!
- Solution: 50! ≈ 3.0414 × 10⁶⁴ (large number, expressed as approximation)

**20.** Compute the geometric series: 1 + 2 + 4 + 8 + ... + 1024
- Solution: a = 1, r = 2, n = 11 terms. Sum = a(rⁿ-1)/(r-1) = (2¹¹-1)/(2-1) = 2047

## Level 5: Base Conversion

**21.** Convert 1101011₂ to decimal.
- Solution: 1×2⁶+1×2⁵+0×2⁴+1×2³+0×2²+1×2¹+1×2⁰ = 64+32+0+8+0+2+1 = 107

**22.** Convert 255₁₀ to binary.
- Solution: 255 = 11111111₂

**23.** Convert 0xFF in hexadecimal to decimal.
- Solution: 0xFF = 15×16 + 15 = 255

**24.** Convert decimal 1000 to base 7.
- Solution: 1000 ÷ 7 = 142 r 6; 142 ÷ 7 = 20 r 2; 20 ÷ 7 = 2 r 6; 2 ÷ 7 = 0 r 2. Result: 2626₇

**25.** Convert 1234₅ to decimal.
- Solution: 1×5³ + 2×5² + 3×5¹ + 4×5⁰ = 125 + 50 + 15 + 4 = 194

## Level 6: Properties and Proofs

**26.** Prove that the sum of two even numbers is even.
- Solution: Let a=2m, b=2n. Then a+b=2(m+n), which is even. QED.

**27.** Prove that product of two odd numbers is odd.
- Solution: Let a=2m+1, b=2n+1. Then ab = (2m+1)(2n+1) = 4mn+2m+2n+1 = 2(2mn+m+n)+1, which is odd. QED.

**28.** Prove by induction: 1² + 2² + ... + n² = n(n+1)(2n+1)/6
- Solution: Base: n=1: 1²=1, RHS=1(2)(3)/6=1. Inductive step: Assume true for n, add (n+1)², simplify to get formula for n+1.

**29.** Show that √3 is irrational.
- Solution: Suppose √3 = a/b in lowest terms. Then 3 = a²/b² → a² = 3b². a² divisible by 3, so a=3c. Then 9c²=3b² → b²=3c². b² divisible by 3, so b=3d. This contradicts lowest terms assumption. Therefore √3 is irrational.

**30.** Prove: If a|b and a|c, then a|(b ± c)
- Solution: b = ak, c = al. Then b ± c = a(k ± l), so a|(b ± c). QED.

## Level 7: Advanced Problems

**31.** Find the remainder when 2⁵⁰ is divided by 7.
- Solution: 2³=8≡1 mod7. 2⁵⁰ = (2³)¹⁶ × 2² = 1¹⁶ × 4 = 4 mod 7

**32.** Find the smallest positive integer n such that 2ⁿ ≡ 1 (mod 13).
- Solution: 2¹² ≡ 1 (mod 13) by Fermat. Check smaller: 2⁶=64≡12, 2⁴=16≡3, 2³=8≡8. Smallest is n=12.

**33.** Calculate φ(100).
- Solution: 100=2²×5². φ(100)=100×(1-1/2)×(1-1/5)=100×½×⅘=40

**34.** Find all solutions to x² ≡ 1 (mod 15).
- Solution: x²≡1 mod 3 → x≡±1 mod 3. x²≡1 mod 5 → x≡±1 mod 5. CRT gives x≡1,4,11,14 mod 15.

**35.** Prove: n² - n is always even for integer n.
- Solution: n²-n = n(n-1). This is product of two consecutive integers, one even, so product is even.

## Level 8: Real-World Applications

**36.** A store offers 25% off, then an additional 15% off. What's the total discount?
- Solution: Price becomes 0.75×0.85 = 0.6375 of original. Total discount = 1 - 0.6375 = 36.25%

**37.** If you invest $1000 at 5% annual compound interest, what's it worth after 10 years?
- Solution: A = 1000(1.05)¹⁰ ≈ $1628.89

**38.** In how many ways can you make change for $1 using pennies, nickels, dimes, quarters?
- Solution: 242 ways (can be solved by generating function or systematic enumeration)

**39.** How many zeros does 100! end with?
- Solution: Number of trailing zeros = floor(100/5)+floor(100/25) = 20+4 = 24

**40.** A password consists of 8 characters from A-Z, a-z, 0-9. How many possible passwords?
- Solution: 26+26+10 = 62 characters. 62⁸ ≈ 2.18 × 10¹⁴ possible passwords.

## Bonus Challenges

**41.** Find the 1000th prime number.
- Solution: The 1000th prime is 7919.

**42.** Prove: gcd(a, b) × lcm(a, b) = a × b
- Solution: Let d=gcd(a,b). Then a=da', b=db' with gcd(a',b')=1. lcm(a,b)=da'b'. So d×lcm = d²a'b' = (da')(db') = ab. QED.

**43.** Find all n such that n² ≡ n (mod 12)
- Solution: n² - n = n(n-1) divisible by 12. Solutions: n ≡ 0,1,4,9,10,12 mod 12. More specifically: n = 0, 1, 4, 9, 10 mod 12.

**44.** Evaluate: 1 + 1/(1×2) + 1/(1×2×3) + ... + 1/100!
- Solution: This is approximately e - 2 ≈ 0.71828 (since 1/0! + 1/1! + 1/2! + ... = e)

**45.** Show that any odd integer can be written as difference of two perfect squares.
- Solution: Let n=2k+1 = (k+1)² - k². QED.