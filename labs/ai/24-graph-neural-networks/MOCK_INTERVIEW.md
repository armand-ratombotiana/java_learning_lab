# Mock Interview: Graph Neural Networks

## Question 1: GNN Message Passing
**Q**: Explain the message passing framework in GNNs. Implement a simple GNN layer.

**A**: Message passing: each node aggregates information from its neighbors.

h_v^{(k+1)} = UPDATE(h_v^{(k)}, AGGREGATE({h_u^{(k)} for u in N(v)}))

```python
class GCNLayer(nn.Module):
    def __init__(self, in_dim, out_dim):
        super().__init__()
        self.W = nn.Linear(in_dim, out_dim)

    def forward(self, h, adj):
        # adj: normalized adjacency matrix (N x N)
        # h: node features (N x in_dim)
        h_prime = adj @ self.W(h)  # Message passing: average neighbor messages
        return F.relu(h_prime)
```

Key operations:
- **Message**: Transform node features: m_uv = W * h_u
- **Aggregate**: Sum, mean, max of neighbor messages: m_v = sum(m_uv)
- **Update**: Combine with own features: h_v' = sigma(W_self * h_v + m_v)

## Question 2: GNN Architectures
**Q**: Compare GCN, GAT, and GraphSAGE.

**A**:
| Model | Aggregation | Weight | Scalability |
|-------|-------------|--------|-------------|
| GCN | Mean of neighbor features | Normalized adjacency | Full-batch |
| GAT | Weighted mean (attention) | Learned attention weights | Full-batch |
| GraphSAGE | Mean/LSTM/Pooling of sampled neighbors | Uniform | Mini-batch (scalable) |

**GCN**: h_v' = sigma(sum(W * h_u / deg(v)*deg(u))). Simple but assumes all neighbors equally important.

**GAT (Graph Attention Networks)**: 
e_uv = a(W*h_u || W*h_v)  (attention coefficient)
alpha_uv = softmax(leaky_relu(e_uv))
h_v' = sigma(sum(alpha_uv * W * h_u))

**GraphSAGE**: Samples fixed-size neighborhood, uses various aggregators (mean, LSTM, pooling). Designed for large graphs.

## Question 3: Over-smoothing
**Q**: What is the over-smoothing problem in GNNs? How do you mitigate it?

**A**: As you stack more GNN layers, node representations become increasingly similar (converge to same value).

**Causes**: Repeated message passing = repeated averaging = loss of discriminative information.

**Mitigations**:
- **Residual connections**: h^{k+1} = h^k + GNN(h^k)
- **PairNorm**: Normalize node features to prevent collapse
- **DropEdge**: Randomly drop edges during training
- **JK-Net**: Jumping knowledge connections from all layers
- **Skip connections**: Like DenseNet style connections
- **DeeperGCN**: GeniePath, RevGNN (reversible)
- **Use fewer layers**: Many tasks only need 2-3 layers

## Question 4: Graph Tasks
**Q**: Compare node, edge, and graph-level prediction tasks. How do you handle each?

**A**:
- **Node classification**: Predict label per node (e.g., user interest prediction). Use final node embeddings + classifier.
- **Edge prediction**: Predict existence/type of edge (e.g., friend recommendation). Use pair of node embeddings: score = h_u^T * h_v or MLP([h_u, h_v]).
- **Graph classification**: Predict label per graph (e.g., molecular property). Pool all node embeddings: h_G = sum/mean/max(h_v), then classify.

```python
class GraphClassifier(nn.Module):
    def __init__(self, node_dim, hidden_dim, n_classes):
        super().__init__()
        self.gnn = GNN(node_dim, hidden_dim)
        self.readout = nn.Sequential(
            nn.Linear(hidden_dim, hidden_dim), nn.ReLU(),
            nn.Linear(hidden_dim, n_classes))

    def forward(self, h, adj, batch_idx):
        h = self.gnn(h, adj)
        # Global pooling: aggregate across graph
        h_g = scatter_mean(h, batch_idx, dim=0)  # Pool per graph
        return self.readout(h_g)
```

## Question 5: Scalability
**Q**: How do you scale GNNs to large graphs (billions of nodes)?

**A**: 
- **Neighbor sampling** (GraphSAGE, PinSAGE): Sample fixed-size neighborhood
- **Cluster-GCN**: Cluster nodes, train on subgraphs
- **GraphSAINT**: Importance-based subgraph sampling
- **ShadowGNN**: Decouple depth (local) and breadth (distributed)
- **Distributed training**: Partition graph across machines (PyG distributed, DGL distributed)

```python
# Neighbor sampling (simplified)
def sample_neighbors(edges, node, num_samples=10):
    neighbors = edges[edges[:,0] == node, 1]
    if len(neighbors) > num_samples:
        return np.random.choice(neighbors, num_samples, replace=False)
    return neighbors
```

**Industrial scale**: PinSAGE (Pinterest) uses random walks + importance sampling for neighborhood definition, enabling training on 3B node graphs.
