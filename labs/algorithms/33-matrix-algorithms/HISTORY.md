# History of Matrix Algorithms

1848: Augustin-Louis Cauchy established the foundations of matrix theory. Jacobi introduced the Jacobi method for eigenvalue computation.

1883: Carl Friedrich Gauss developed Gaussian elimination for solving linear systems (though the method was known in Chinese mathematics since 200 BC as fangcheng).

1909: Andrey Markov used matrix multiplication to model random processes (Markov chains).

1948: John von Neumann and Herman Goldstine analyzed the numerical stability of Gaussian elimination, establishing error analysis for matrix computations.

1969: Volker Strassen published the first sub-cubic matrix multiplication algorithm, achieving O(n^{log_2 7}) ≈ O(n^{2.807}). This was the first improvement over naive O(n^3) in over 100 years.

1978: Don Coppersmith and Shmuel Winograd developed the O(n^{2.376}) matrix multiplication algorithm, using tensor techniques.

1980s: LAPACK (Linear Algebra Package) was developed, providing standardized, optimized implementations of LU, QR, SVD, and eigenvalue decompositions. It remains the standard interface for linear algebra.

1990: Gene Golub and William Kahan published the definitive SVD algorithm after pioneering work on the topic.

2012: Francois Le Gall improved matrix multiplication to O(n^{2.37286}). The current best bound is O(n^{2.371552}) by Williams and Alman (2021).

2014: Deep learning's need for large-scale matrix multiplication drove hardware optimization (GPUs from NVIDIA with tensor cores, Google TPUs). Matrix multiplication became the dominant computational workload in AI.