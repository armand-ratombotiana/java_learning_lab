# NVIDIA AI Interview Guide

Interview preparation for ML/AI roles at NVIDIA. Covers deep learning, GPU computing, CUDA, and ML infrastructure roles.

---

## 1. Role Types at NVIDIA AI

### Machine Learning Engineer (Deep Learning)
- Optimizes deep learning models for NVIDIA GPUs
- Libraries: cuDNN, cuBLAS, TensorRT, CUTLASS
- Focus: performance optimization, kernel fusion
- Strong CUDA/C++ skills required

### AI Research Scientist (NVIDIA Research)
- Publishes at top venues (NeurIPS, ICML, CVPR, ICLR)
- Areas: computer vision, NLP, robotics, graphics
- PhD required or equivalent

### ML Platform Engineer
- Training/serving infrastructure
- GPU cluster management
- MLOps, data pipelines
- Software engineering focus

### Applied ML Engineer
- ML solutions using NVIDIA platforms
- RAPIDS (GPU-accelerated data science)
- Deep learning inference (TensorRT)
- Customer-facing (Solution Architect roles)

### Autonomous Vehicle ML Engineer (Drive)
- Perception, planning, control
- Real-time ML on embedded GPUs
- Safety-critical systems

---

## 2. Interview Process

### Process Timeline

| Step | Duration | Format |
|------|----------|--------|
| Recruiter Screen | 30 min | Background + interests |
| Technical Phone Screen | 60 min | CUDA + ML fundamentals |
| Virtual On-site | 4-5 hours | 4-5 rounds |
| Team Matching | 30-45 min | Team-specific discussion |
| Offer | - | Negotiation |

### Round Breakdown

| Round | Duration | Focus |
|-------|----------|-------|
| GPU/CUDA Coding | 60 min | GPU kernel implementation |
| ML Algorithms | 45 min | Deep understanding |
| System Design | 45 min | GPU cluster + distributed |
| C++/Python Coding | 45 min | General algorithms |
| Behavioral | 30 min | Innovation + collaboration |
| Research (Research roles) | 45 min | Paper presentation |

---

## 3. GPU/CUDA Coding Round

### Core CUDA Concepts

| Concept | Importance | Interview Application |
|---------|-----------|----------------------|
| Grid/Block/Thread Hierarchy | Critical | Kernel design, occupancy optimization |
| Shared Memory | Critical | Matrix multiplication tiling |
| Memory Coalescing | High | Efficient data access patterns |
| Warp-level Primitives | High | Reduce, shuffle, ballot |
| Streams & Events | Medium | Concurrency, overlap compute/transfer |
| Tensor Cores | High | Mixed precision, WMMA API |
| Atomics | Medium | Histogram, reductions |

### Common CUDA Problems

| Problem | Key Techniques | Difficulty |
|---------|---------------|------------|
| Matrix Multiplication (tiled) | Shared memory tiling, bank conflicts | Hard |
| Vector Addition | Memory coalescing, grid-stride loops | Easy |
| Reduction (sum, max) | Warp reduction, shared memory | Medium |
| Softmax (online algorithm) | Warp-level primitives, numerical stability | Hard |
| Histogram | Atomics, privatization, shared memory | Medium |
| 2D Convolution | Shared memory tiles, halo regions | Hard |
| Prefix Sum (Scan) | Blelloch/Hillis-Steele, warp scan | Hard |
| Radix Sort | Scan, histogram, reorder | Hard |
| Batch Normalization | Fused kernel, persistent threads | Hard |
| Attention Kernel | Tiled approach, online softmax | Very Hard |

```cpp
// Example: Tiled Matrix Multiplication with Shared Memory
__global__ void matmul_tiled(float* A, float* B, float* C,
                              int M, int N, int K) {
    const int TILE_SIZE = 16;
    __shared__ float As[TILE_SIZE][TILE_SIZE];
    __shared__ float Bs[TILE_SIZE][TILE_SIZE];

    int row = blockIdx.y * TILE_SIZE + threadIdx.y;
    int col = blockIdx.x * TILE_SIZE + threadIdx.x;

    float sum = 0.0f;

    for (int tile = 0; tile < (K + TILE_SIZE - 1) / TILE_SIZE; tile++) {
        // Cooperative loading into shared memory
        if (row < M && tile * TILE_SIZE + threadIdx.x < K)
            As[threadIdx.y][threadIdx.x] = A[row * K + tile * TILE_SIZE + threadIdx.x];
        else
            As[threadIdx.y][threadIdx.x] = 0.0f;

        if (col < N && tile * TILE_SIZE + threadIdx.y < K)
            Bs[threadIdx.y][threadIdx.x] = B[(tile * TILE_SIZE + threadIdx.y) * N + col];
        else
            Bs[threadIdx.y][threadIdx.x] = 0.0f;

        __syncthreads();

        // Compute partial dot product
        for (int k = 0; k < TILE_SIZE; k++)
            sum += As[threadIdx.y][k] * Bs[k][threadIdx.x];

        __syncthreads();
    }

    if (row < M && col < N)
        C[row * N + col] = sum;
}

// Example: Online Softmax for Attention (simplified)
__global__ void online_softmax(float* __restrict__ input,
                                float* __restrict__ output,
                                int rows, int cols) {
    int row = blockIdx.x * blockDim.x + threadIdx.x;
    if (row >= rows) return;

    float max_val = -INFINITY;
    float sum = 0.0f;

    #pragma unroll
    for (int i = 0; i < cols; i++) {
        float old_max = max_val;
        float val = input[row * cols + i];

        if (val > max_val) {
            max_val = val;
            sum = sum * __expf(old_max - max_val) + 1.0f;
        } else {
            sum += __expf(val - max_val);
        }
    }

    for (int i = 0; i < cols; i++) {
        output[row * cols + i] = __expf(input[row * cols + i] - max_val) / sum;
    }
}
```

### Performance Optimization Techniques

**Occupancy Optimization**:
- Maximize threads per block (but don't exceed register limit)
- Use `__launch_bounds__` to help compiler
- Balance thread count vs. shared memory per block

**Memory Access Patterns**:
- Coalesced global memory access (adjacent threads access adjacent addresses)
- Shared memory bank conflict avoidance
- Padding to avoid bank conflicts

**Compute vs. Memory Bound**:
- Roofline model analysis
- For compute bound: use tensor cores, reduce precision
- For memory bound: fuse kernels, use shared memory

---

## 4. ML Algorithms Round

### Deep Understanding Topics

| Topic | Key Knowledge | NVIDIA Context |
|-------|---------------|----------------|
| Gradient Descent | All variants, convergence | Mixed precision training |
| Backpropagation | Chain rule, automatic differentiation | cuDNN, CUTLASS |
| Convolution | Algorithm selection (FFT, Winograd, im2col) | cuDNN algorithm heuristics |
| Attention | Multi-head, causal masking, Flash Attention | FasterTransformer |
| Normalization | Batch, Layer, Instance, Group | Fused normalization kernels |
| Quantization | INT8, FP8, NF4, calibration | TensorRT quantization |
| Pruning | Structured, unstructured, sparsity | cuSPARSELt |
| Mixture of Experts | Routing, load balancing | Megatron-LM |
| Distributed Training | DP, TP, PP, sequence parallelism | NeMo, Megatron-LM |

### Sample Algorithm Questions

```
1. How do tensor cores work internally?
   Tensor cores perform D = A * B + C where A,B are FP16, C,D are FP16/FP32
   Multiply 4x4 matrices in a single cycle

2. Compare FP32, FP16, BF16, FP8 precision trade-offs
   Range: BF16 > FP16 > FP32 | Precision: FP32 > FP16 > BF16 > FP8

3. How does the roofline model help optimize kernels?
   Compute-bound: optimize math throughput
   Memory-bound: optimize data movement, caching

4. Explain Flash Attention and its GPU optimization strategy
   Tiled approach, online softmax, no NxN attention matrix materialization
   Uses shared memory efficiently for QK^T computation tiles

5. How would you profile and optimize a PyTorch model on NVIDIA GPUs?
   Use nsys (Nsight Systems) for high-level profiling
   Use ncu (Nsight Compute) for kernel-level analysis
```

---

## 5. System Design Round

### Common Systems

| System | Key Considerations | NVIDIA Technology |
|--------|-------------------|-------------------|
| Training Cluster | GPU interconnect, storage, cooling | DGX, NVLink, InfiniBand |
| Inference Serving | Throughput, latency, batch size | Triton Inference Server |
| Model Optimization Pipeline | Calibration, quantization, compiler | TensorRT |
| GPU Cluster Scheduler | Job queue, resource allocation | Slurm, Kubernetes + GPU operator |
| Large Model Training | Pipeline, tensor, sequence parallelism | Megatron-LM, NeMo |
| Data Pipeline | Loading, preprocessing, augmentation | DALI, RAPIDS |

### Design: Training Infrastructure for Large Language Model

**Requirements**:
- Train a 70B parameter model
- 1000+ GPUs available
- Training time: < 3 months
- Failure tolerance: handle GPU failures
- Monitoring: real-time loss, throughput, GPU utilization

**Architecture**:

```
Compute Cluster Design:
- DGX H100 nodes (8 GPUs each, NVLink connected)
- ~125 nodes for 1000 GPUs
- InfiniBand NDR (400 Gb/s) inter-node

Parallelism Strategy:
- Intra-node: 8-way Tensor Parallelism (NVLink)
- Inter-node: Pipeline Parallelism (8 stages) + Data Parallelism

Memory Optimization:
- ZeRO Stage 1 (optimizer sharding)
- Activation checkpointing
- Mixed precision (BF16)

Fault Tolerance:
- Async checkpointing to parallel filesystem
- Elastic training: remove failed nodes, continue
- Automated node recovery

Monitoring:
- NVIDIA DCGM for GPU health
- NeMo-Megatron logging for training metrics
- Prometheus + Grafana for visualization
```

---

## 6. C++/Python Coding Round

### C++ Focus Areas

| Topic | Frequency | Example |
|-------|-----------|---------|
| Memory Management | High | RAII, smart pointers, custom allocators |
| Templates | High | Template metaprogramming, SFINAE |
| Concurrency | High | Thread pools, atomics, mutexes |
| STL | High | Algorithms, containers, iterators |
| Move Semantics | Medium | Perfect forwarding, rvalue references |
| Design Patterns | Medium | Factory, singleton (rare), adapter |

### Python Focus Areas

| Topic | Frequency | Example |
|-------|-----------|---------|
| NumPy | Very High | Vectorization, broadcasting |
| PyTorch | Very High | Custom autograd functions, C++ extensions |
| Performance | High | PyPy, Cython, Numba, vectorization |
| Async | Medium | Async/await for I/O bound workloads |
| C Extensions | High | pybind11, C API |

```cpp
// Example: Custom allocator for GPU memory
template <typename T>
class DeviceAllocator {
public:
    using value_type = T;

    DeviceAllocator() = default;

    T* allocate(std::size_t n) {
        T* ptr = nullptr;
        cudaMalloc(&ptr, n * sizeof(T));
        if (!ptr) throw std::bad_alloc();
        return ptr;
    }

    void deallocate(T* ptr, std::size_t) {
        cudaFree(ptr);
    }
};

// Example: RAII GPU memory management
class GPUBuffer {
public:
    GPUBuffer(size_t size) : size_(size) {
        cudaMalloc(&data_, size * sizeof(float));
    }

    ~GPUBuffer() {
        if (data_) cudaFree(data_);
    }

    GPUBuffer(const GPUBuffer&) = delete;
    GPUBuffer& operator=(const GPUBuffer&) = delete;

    GPUBuffer(GPUBuffer&& other) noexcept
        : data_(other.data_), size_(other.size_) {
        other.data_ = nullptr;
        other.size_ = 0;
    }

    float* data() { return data_; }
    size_t size() const { return size_; }

private:
    float* data_ = nullptr;
    size_t size_ = 0;
};
```

---

## 7. NVIDIA Ecosystem Knowledge

### Hardware Architecture

**GPU Architecture (Hopper H100)**:
- 132 SMs per GPU (each with tensor cores)
- 80 GB HBM3 memory (3.35 TB/s bandwidth)
- Transformer Engine (FP8 Tensor Cores)
- NVLink 4.0 (900 GB/s)
- PCIe Gen 5

**Memory Hierarchy**:
| Memory | Size | Bandwidth | Scope |
|--------|------|-----------|-------|
| Global (HBM) | 80 GB | 3.35 TB/s | Grid |
| Shared Memory | 228 KB/SM | ~30 TB/s | Block |
| Registers | 64K/SM | ~60 TB/s | Thread |
| L1 Cache | 256 KB/SM | ~20 TB/s | Block |
| L2 Cache | 50 MB | ~6 TB/s | Grid |

**NVLink Topology**:
- DGX H100: 8 GPUs fully connected via NVSwitch
- All-to-all bandwidth: 900 GB/s each direction
- NVLink + NVSwitch = single GPU domain

### Software Stack

**CUDA**: Core GPU computing platform
**cuDNN**: Deep neural network primitives
**cuBLAS**: Linear algebra
**TensorRT**: Inference optimization
**Triton Inference Server**: Multi-framework serving
**NCCL**: Multi-GPU communication
**Megatron-LM**: Large model training
**NeMo**: Conversational AI framework
**RAPIDS**: GPU-accelerated data science
**DALI**: Data loading pipeline
**Nsight Systems/Compute**: Profiling tools

---

## 8. Research Preparation

### NVIDIA Research Papers to Know

**Vision**:
- "U-Net: Convolutional Networks for Biomedical Image Segmentation" (2015)
- "StyleGAN" series (2019-2021)
- "NeRF: Representing Scenes as Neural Radiance Fields" (2020)

**Neural Rendering**:
- "Neuralangelo: High-Fidelity Neural Surface Reconstruction" (2023)
- "GANcraft: Unsupervised 3D Neural Rendering" (2022)

**Autonomous Driving**:
- "PointPillars: Fast Encoders for Object Detection from Point Clouds" (2019)
- "End-to-End Autonomous Driving" (various)

**Physics ML**:
- "Physics-Informed Neural Networks" (PINNs, 2019)
- "FourCastNet: A Global Data-driven High-resolution Weather Model" (2022)

**Large Language Models**:
- "Megatron-LM: Training Multi-Billion Parameter Language Models" (2020)
- "Efficient Large-Scale Language Model Training on GPU Clusters" (2021)

---

## 9. Key Resources

### Books
- "Programming Massively Parallel Processors" (Kirk & Hwu) - CUDA bible
- "CUDA by Example" (Sanders & Kandrot)
- "C++ Concurrency in Action" (Williams)
- "Deep Learning" (Goodfellow et al.)

### Online Resources
- NVIDIA CUDA Programming Guide (docs.nvidia.com/cuda)
- CUDA Samples and SDK
- NVIDIA Developer Blog (developer.nvidia.com/blog)
- GPU Gems series
- GTC conference talks

### Practice
- LeetCode (not priority - focus on CUDA)
- Implement common ML algorithms in CUDA
- Profile and optimize existing CUDA kernels
- Study open-source CUDA implementations (cuDNN style)

### Preparation Tips
- Know GPU memory hierarchy cold
- Be able to write a simple CUDA kernel from scratch
- Understand tensor core operations
- Know the TensorRT optimization pipeline
- Be ready to hand-write and analyze C++ code
- Study GPU communication patterns (NCCL)

---

## 10. Example Interview Question Walkthrough

### Problem: Implement Fused Softmax in CUDA

**Requirements**:
- Input: float* of shape [rows, cols]
- Output: softmax over the last dimension
- Must be numerically stable
- Optimize for memory bandwidth

**Solution Approach**:

```cpp
__global__ void fused_softmax(float* input, float* output,
                               int rows, int cols) {
    extern __shared__ float shared[];
    int row = blockIdx.x;

    if (row >= rows) return;

    // Phase 1: Find max
    float max_val = -INFINITY;
    for (int i = threadIdx.x; i < cols; i += blockDim.x) {
        max_val = max(max_val, input[row * cols + i]);
    }

    // Warp reduction for max
    // ... (warp shuffle reduction)

    // Phase 2: Compute sum
    float sum = 0.0f;
    for (int i = threadIdx.x; i < cols; i += blockDim.x) {
        float exp_val = expf(input[row * cols + i] - max_val);
        sum += exp_val;
        // Store exp in shared for phase 3 reuse
        shared[i] = exp_val;
    }

    // Warp reduction for sum
    // ... (warp shuffle reduction)

    // Phase 3: Normalize
    for (int i = threadIdx.x; i < cols; i += blockDim.x) {
        output[row * cols + i] = shared[i] / sum;
    }
}
```

**Optimization Discussion**:
- Online softmax fusion (combine phases 1 and 2)
- Persistent threads for many rows
- Using tensor cores (if FP16/BF16)
- Loop unrolling for small cols
- Vectorized loads (float4 for aligned data)
