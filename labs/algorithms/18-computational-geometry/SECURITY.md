# Security — Computational Geometry

## Denial of Service

Pathological point distributions can cause worst-case O(n^2) behavior in algorithms with poor average-case guarantees. Use monotone chain instead of Jarvis March for predictable performance.

## Floating Point Injection

User-controlled coordinates with extreme values (NaN, Infinity) can crash algorithms or cause infinite loops. Validate and sanitize coordinate inputs.

## Algorithm Subversion

Crafted inputs can cause stack overflow in recursive algorithms (closest pair). Use iterative implementations or limit recursion depth.

## Precision Attacks

Adversarial inputs near the epsilon threshold can cause inconsistent orientation results. Use exact rational arithmetic for security-critical applications.
