# Mock Interview: Implement Convolution Operation from Scratch and Compare with PyTorch

## Scenario
You are interviewing for a deep learning engineer role. The interviewer wants to verify your understanding of the fundamental convolution operation.

## Interviewer Opening Question
"Implement 2D convolution from scratch using NumPy and compare your implementation's output to PyTorch's Conv2d."

## Candidate Response
"I'll implement direct convolution with nested loops over batch, channels, height, width, and kernel positions. Then I'll optimize with im2col — unrolling the image patches into a matrix for efficient GEMM-based convolution."

## Interviewer Probing Questions

**Q: What's the time complexity of direct convolution?**
"O(N * C_out * H_out * W_out * C_in * K_h * K_w). For a 3x3 kernel on a 224x224 image with 64 channels, that's roughly 8.7 billion operations per layer."

**Q: How does im2col improve performance?**
"It trades memory for speed. By unrolling patches into a matrix, the convolution becomes a single matrix multiply (GEMM), which is heavily optimized in BLAS libraries. Memory overhead is about K_h * K_w times the original."

**Q: What about dilated or strided convolutions?**
"Strided convolutions skip positions in the output map. Dilation introduces gaps in the kernel. I can handle both by modifying how patches are extracted."

## Candidate Solution (Python)

```python
import numpy as np
import torch
import torch.nn.functional as F

def conv2d_naive(x, weight, bias=None, stride=1, padding=0):
    """
    x: (N, C_in, H, W)
    weight: (C_out, C_in, K_h, K_w)
    """
    N, C_in, H, W = x.shape
    C_out, _, K_h, K_w = weight.shape

    # Pad input
    if padding > 0:
        x_pad = np.pad(x, ((0,0), (0,0), (padding,padding), (padding,padding)))
    else:
        x_pad = x

    H_pad, W_pad = x_pad.shape[2], x_pad.shape[3]
    H_out = (H_pad - K_h) // stride + 1
    W_out = (W_pad - K_w) // stride + 1

    out = np.zeros((N, C_out, H_out, W_out))

    for n in range(N):
        for c_out in range(C_out):
            for h in range(H_out):
                for w in range(W_out):
                    h_start = h * stride
                    w_start = w * stride
                    patch = x_pad[n, :, h_start:h_start+K_h, w_start:w_start+K_w]
                    out[n, c_out, h, w] = np.sum(patch * weight[c_out])
                    if bias is not None:
                        out[n, c_out, h, w] += bias[c_out]
    return out

def im2col(x, K_h, K_w, stride=1, padding=0):
    N, C_in, H, W = x.shape
    if padding > 0:
        x = np.pad(x, ((0,0), (0,0), (padding,padding), (padding,padding)))
    H_out = (x.shape[2] - K_h) // stride + 1
    W_out = (x.shape[3] - K_w) // stride + 1
    cols = np.zeros((N, C_in * K_h * K_w, H_out * W_out))
    for h in range(H_out):
        for w in range(W_out):
            patch = x[:, :, h*stride:h*stride+K_h, w*stride:w*stride+K_w]
            cols[:, :, h * W_out + w] = patch.reshape(N, -1)
    return cols

def conv2d_im2col(x, weight, bias=None, stride=1, padding=0):
    N, C_in, H, W = x.shape
    C_out, _, K_h, K_w = weight.shape
    H_out = (H + 2 * padding - K_h) // stride + 1
    W_out = (W + 2 * padding - K_w) // stride + 1
    cols = im2col(x, K_h, K_w, stride, padding)  # (N, C_in*K_h*K_w, H_out*W_out)
    w_flat = weight.reshape(C_out, -1)            # (C_out, C_in*K_h*K_w)
    out = np.matmul(w_flat, cols)                 # (C_out, N * H_out * W_out)
    out = out.reshape(C_out, N, H_out, W_out).transpose(1, 0, 2, 3)
    if bias is not None:
        out += bias.reshape(1, -1, 1, 1)
    return out

# Verification against PyTorch
def verify():
    x_np = np.random.randn(2, 3, 32, 32).astype(np.float32)
    w_np = np.random.randn(4, 3, 3, 3).astype(np.float32)
    b_np = np.random.randn(4).astype(np.float32)

    naive_out = conv2d_naive(x_np, w_np, b_np, stride=1, padding=1)
    im2col_out = conv2d_im2col(x_np, w_np, b_np, stride=1, padding=1)

    x_t = torch.from_numpy(x_np)
    w_t = torch.from_numpy(w_np)
    b_t = torch.from_numpy(b_np)
    torch_out = F.conv2d(x_t, w_t, b_t, stride=1, padding=1).numpy()

    print(f"Naive matches PyTorch: {np.allclose(naive_out, torch_out, atol=1e-5)}")
    print(f"Im2col matches PyTorch: {np.allclose(im2col_out, torch_out, atol=1e-5)}")
    print(f"Max error (naive): {np.abs(naive_out - torch_out).max()}")
    print(f"Max error (im2col): {np.abs(im2col_out - torch_out).max()}")

# Performance comparison
def benchmark():
    import time
    x = np.random.randn(8, 64, 128, 128).astype(np.float32)
    w = np.random.randn(128, 64, 3, 3).astype(np.float32)
    start = time.time()
    conv2d_im2col(x, w, stride=1, padding=1)
    print(f"im2col: {time.time() - start:.3f}s")
```

## Interviewer Feedback
"Excellent implementation with both naive and optimized versions. The im2col approach shows you understand how convolution maps to matrix multiplication. Verification against PyTorch is thorough."

## Key Takeaways
- Direct convolution is intuitive but slow for real workloads
- im2col + GEMM is the standard high-performance approach
- Always verify custom implementations against a reference framework
- Understanding strided/dilated convolution requires modifying patch extraction
- Memory overhead of im2col is a known trade-off
