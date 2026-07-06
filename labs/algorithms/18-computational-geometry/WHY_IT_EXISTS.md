# Why Computational Geometry Algorithms Exist

Computational geometry emerged as a distinct field in the 1970s, driven by the need to solve geometric problems efficiently on computers. Before specialized algorithms, geometric problems were solved using general computational approaches that failed to leverage the structure of geometric space.

The field was formalized by Michael Shamos in his 1978 Ph.D. thesis, which introduced many fundamental problems and their solutions. The convex hull problem is the "sorting" of computational geometry — it is a fundamental building block that appears in countless applications.

The closest pair problem has applications in collision detection in physics simulations, clustering in data mining, and proximity analysis in GIS. The naive O(n^2) solution becomes impractical for large point sets (millions of points from LIDAR scans).

Line segment intersection detection is essential for VLSI design (checking wire crossings), GIS (overlay analysis), and computer graphics (visibility determination). The sweep-line algorithm by Shamos and Hoey (1976) provided an O((n+k) log n) solution where k is the number of intersections.

Graham Scan (1972) was one of the first efficient convex hull algorithms. It demonstrated how sorting could be leveraged to reduce computational complexity from O(n^2) to O(n log n). Ronald Graham developed it for computational number theory, not realizing it would become a cornerstone of computational geometry.

Andrew's monotone chain (1979) simplified Graham Scan by avoiding polar angle computation, instead using just x-coordinate sorting. This eliminates the need for trigonometric functions and handle collinear points more gracefully.
