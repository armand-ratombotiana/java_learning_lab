# Mental Models for Number Theory

## Euclidean Algorithm — "The Measuring Stick"

Imagine finding the largest ruler that can measure both 9 and 6 units exactly. Try 6: 9 = 1*6 + 3 (remainder 3). Now measure 6 with remainder 3: 6 = 2*3 + 0. The last non-zero remainder 3 is the GCD. This is Euclid's algorithm: repeatedly replace the larger number with the remainder when divided by the smaller.

## Sieve of Eratosthenes — "The Prime Filter"

Picture a row of numbered boxes from 1 to n. Cross out 1 (not prime). Circle 2, then cross out every 2nd box. Circle 3, cross out every 3rd box. Skip 4 (already crossed). Circle 5, cross out every 5th box. Continue until you've circled all remaining numbers. The circled numbers are prime.

## Miller-Rabin — "The Witness"

To check if a number might be prime, ask a series of witnesses. Each witness provides evidence: "Based on my test, this number is probably prime." A single witness saying "composite" is definite. To be sure, you need enough witnesses that the probability of all being wrong is negligible.

## CRT — "The Coordinate System"

A number modulo N can be represented by its residues modulo the factors of N. This is like representing a point in 2D by its (x,y) coordinates. Operations on the number can be done independently on each coordinate and then reconstructed.
