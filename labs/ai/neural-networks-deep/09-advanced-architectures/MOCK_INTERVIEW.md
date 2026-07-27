# Mock Interview: Design a Neural Architecture for Video Understanding

## Scenario
You are interviewing for a computer vision research role at a video analytics company. They want to design a model for action recognition in long videos.

## Interviewer Opening Question
"Design a neural architecture for video understanding that can handle hour-long videos and recognize complex activities."

## Candidate Response
"I'd design a hierarchical architecture: (1) A spatiotemporal encoder using 3D ConvNeXt or Video Swin Transformer for short clips. (2) A temporal aggregator using a memory-efficient transformer with sliding window attention. (3) A global pooling module for video-level classification. The key is handling long-range dependencies without O(N^2) memory."

## Interviewer Probing Questions

**Q: How do you handle the memory cost of video?**
"Use sparse sampling: sample 8-32 frames per clip instead of all 30 fps. During training, randomly sample clips. During inference, use a sliding window with stride. Also use memory-efficient attention like FlashAttention."

**Q: 3D Conv vs Video Transformer — which wins?**
"Video Swin Transformer achieves better accuracy on Kinetics, but 3D ConvNeXt is more efficient. For long videos, I'd use a hybrid: 3D CNN for frame-level features, then a transformer for temporal reasoning."

**Q: How do you handle multiple temporal scales?**
"Feature pyramid across temporal dimension: early layers capture fine-grained motion (short clips), later layers capture long-range patterns. A multi-stage temporal pooling aggregates features at multiple resolutions."

## Candidate Solution (Python)

```python
import torch
import torch.nn as nn
import torch.nn.functional as F
import math
from typing import Optional, Tuple

class VideoPatchEmbed(nn.Module):
    """Patch embedding for video: (B, C, T, H, W) -> (B, num_patches, D)."""
    def __init__(self, in_channels=3, embed_dim=768, patch_size=(2, 16, 16)):
        super().__init__()
        self.patch_size = patch_size
        self.proj = nn.Conv3d(in_channels, embed_dim,
                              kernel_size=patch_size, stride=patch_size)

    def forward(self, x):
        x = self.proj(x)  # (B, D, T_p, H_p, W_p)
        x = x.flatten(2).transpose(1, 2)  # (B, num_patches, D)
        return x

class FactorizedAttention(nn.Module):
    """Spatiotemporal factorized attention for video."""
    def __init__(self, dim, num_heads, window_size=(4, 7, 7)):
        super().__init__()
        self.dim = dim
        self.num_heads = num_heads
        self.window_size = window_size  # (T, H, W)
        self.d_k = dim // num_heads

        self.W_qkv = nn.Linear(dim, dim * 3, bias=False)
        self.W_o = nn.Linear(dim, dim, bias=False)

    def _window_partition(self, x, window_size):
        B, N, C = x.shape
        T, H, W = self._compute_shape(B, N)
        x = x.view(B, T, H, W, C)
        w_t, w_h, w_w = window_size
        x = x.view(B, T // w_t, w_t, H // w_h, w_h, W // w_w, w_w, C)
        x = x.permute(0, 1, 3, 5, 2, 4, 6, 7).contiguous()
        x = x.view(-1, w_t * w_h * w_w, C)
        return x

    def _compute_shape(self, B, N):
        # Simplified shape computation
        T = int(N ** (1/3))
        H = int(N ** (1/3))
        W = N // (T * H)
        return T, H, W

    def forward(self, x):
        B, N, C = x.shape
        qkv = self.W_qkv(x).reshape(B, N, 3, self.num_heads, self.d_k).permute(2, 0, 3, 1, 4)
        Q, K, V = qkv[0], qkv[1], qkv[2]

        # Apply within windows for efficiency
        Q = self._window_partition(Q, self.window_size)
        K = self._window_partition(K, self.window_size)
        V = self._window_partition(V, self.window_size)

        scores = torch.matmul(Q, K.transpose(-2, -1)) / math.sqrt(self.d_k)
        attn = F.softmax(scores, dim=-1)
        x = torch.matmul(attn, V)

        # Reverse window partition (simplified)
        x = self.W_o(x.view(B, N, C))
        return x

class VideoSwinBlock(nn.Module):
    """Video Swin Transformer block."""
    def __init__(self, dim, num_heads, window_size=(4, 7, 7)):
        super().__init__()
        self.norm1 = nn.LayerNorm(dim)
        self.attn = FactorizedAttention(dim, num_heads, window_size)
        self.norm2 = nn.LayerNorm(dim)
        self.ffn = nn.Sequential(
            nn.Linear(dim, dim * 4),
            nn.GELU(),
            nn.Linear(dim * 4, dim)
        )

    def forward(self, x):
        x = x + self.attn(self.norm1(x))
        x = x + self.ffn(self.norm2(x))
        return x

class TemporalAggregator(nn.Module):
    """Long-range temporal aggregation with memory efficient attention."""
    def __init__(self, dim, num_heads, num_clips=32):
        super().__init__()
        self.num_clips = num_clips
        self.position_embeddings = nn.Parameter(torch.randn(1, num_clips, dim))
        self.cross_attention = nn.MultiheadAttention(dim, num_heads, batch_first=True)

    def forward(self, clip_features: torch.Tensor):
        # clip_features: (B, num_clips, D)
        B = clip_features.shape[0]
        # Add learnable query tokens
        queries = self.position_embeddings.expand(B, -1, -1)
        # Cross-attend clip features to queries
        aggregated, _ = self.cross_attention(queries, clip_features, clip_features)
        return aggregated.mean(dim=1)  # (B, D)

class VideoUnderstandingModel(nn.Module):
    """Full video understanding architecture."""
    def __init__(self, num_classes=400, embed_dim=768, depth=12, num_heads=12):
        super().__init__()
        self.patch_embed = VideoPatchEmbed(in_channels=3, embed_dim=embed_dim)
        self.blocks = nn.ModuleList([
            VideoSwinBlock(embed_dim, num_heads) for _ in range(depth)
        ])
        self.norm = nn.LayerNorm(embed_dim)
        self.temporal_agg = TemporalAggregator(embed_dim, num_heads)

    def forward_clip(self, clip):
        # clip: (B, 3, T, H, W)
        x = self.patch_embed(clip)
        for block in self.blocks:
            x = block(x)
        x = self.norm(x)
        return x.mean(dim=1)  # Global average pooling per clip

    def forward_video(self, clips):
        # clips: (B, num_clips, 3, T, H, W)
        B, num_clips = clips.shape[:2]
        clips_flat = clips.view(-1, *clips.shape[2:])
        clip_features = self.forward_clip(clips_flat)
        clip_features = clip_features.view(B, num_clips, -1)
        return self.temporal_agg(clip_features)

    def forward(self, x):
        return self.forward_video(x)
```

## Interviewer Feedback
"Excellent design with clear hierarchical structure. The factorized attention for spatiotemporal processing and the temporal aggregator for long-range dependencies are well thought out. This architecture balances accuracy and memory efficiency."

## Key Takeaways
- Hierarchical: clip-level encoder + video-level temporal aggregator
- Factorized attention reduces spatiotemporal complexity
- Sparse sampling (8-32 frames) is essential for memory efficiency
- Window attention enables processing of long videos
- Hybrid 3D CNN + transformer often outperforms pure approaches
