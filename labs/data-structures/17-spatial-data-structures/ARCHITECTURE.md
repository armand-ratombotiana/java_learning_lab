# Architecture

Spatial data structures typically integrate as:
`
Application Layer
  â†’ Query Service (nearest, range)
    â†’ Spatial Index (QuadTree / KdTree)
      â†’ Coordinate System
`

Design decisions:
- Static vs dynamic data
- 2D vs 3D vs ND
- In-memory vs disk-based
- Exact vs approximate queries
