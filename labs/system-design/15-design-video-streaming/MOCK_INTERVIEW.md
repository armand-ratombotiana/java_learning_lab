# Mock Interview: Video Streaming Design

> System Design Mock Interview — 45-minute session

---

## Setup

**Role**: Streaming Platform Engineer Interviewer  
**Candidate Level**: Senior Engineer (L5)  
**Problem**: Design a video streaming platform like YouTube or Netflix.

---

## Transcript

**Interviewer**: "Design a video streaming platform. Users upload videos, and millions of users watch them. Requirements: 500 hours of video uploaded per minute, 1B views/day, support 4K, playback start <2 seconds, adaptive bitrate."

**Candidate**: "Let me estimate: 500h/min upload → 15TB/min raw. Storage after encoding: ~500MB/h → 4TB/min → 6PB/day. Bandwidth: 1B views × 20 min avg × 5Mbps = ~200Tbps peak. We need aggressive compression and CDN distribution."

**Interviewer**: "Start with the upload pipeline."

**Candidate**: "Upload pipeline: 1) Client uploads video in chunks (resumable, 5MB chunks), 2) Upload service assembles chunks and stores the raw video in blob storage (S3/GCS), 3) Upload service publishes a 'video_uploaded' event to a queue, 4) Transcoding workers pick up the job."

**Interviewer**: "Design the transcoding pipeline."

**Candidate**: "Transcoding converts raw video into multiple resolutions (144p, 360p, 720p, 1080p, 4K) and codecs (H.264, VP9, AV1). The pipeline: 1) Transcoding Manager receives upload event, creates transcoding jobs, 2) Jobs are queued by priority (high: recently uploaded popular content, low: old content), 3) Worker pool picks jobs, transcodes using FFmpeg, 4) Results stored in blob storage, 5) Manifest file (MPD/HLS) generated referencing all renditions."

**Interviewer**: "How does playback work?"

**Candidate**: "Playback: 1) Client requests video page → server returns HTML with video player, 2) Player fetches manifest file from CDN, 3) Player starts downloading the lowest resolution segment, 4) Player measures bandwidth, 5) Player requests higher/lower resolution segments based on bandwidth and buffer health. This is adaptive bitrate streaming (ABR)."

**Interviewer**: "How does the CDN work?"

**Candidate**: "CDN hierarchy: 1) Edge servers (thousands, at ISP pops) — cache popular content, 2) Regional caches — larger, less popular content, 3) Origin — source of truth. Content delivery: 1) Popular content (top 1% of videos → 90% of views) is pre-populated to all edge nodes, 2) Less popular content is cached on-demand when first requested, 3) Long-tail content is served from origin."

**Interviewer**: "How do you handle video recommendations?"

**Candidate**: "Recommendation system: 1) Offline pipeline (daily): compute embeddings for all videos using content analysis (audio, visual, text features), 2) Online pipeline: user watch history → generate candidates (similar videos, related channels, trending), 3) Ranking: ML model predicts watch probability based on user embedding × video embedding similarity, recency, popularity. 4) Re-ranking: diversify results, avoid showing too many from same channel."

**Interviewer**: "What about live streaming?"

**Candidate**: "Live streaming has stricter latency requirements. 1) Ingest: RTMP or WebRTC from streamer to ingest server, 2) Transcoding: low-latency on-the-fly transcoding (fewer resolutions to save time), 3) Distribution: CDN with chunked transfer encoding (1-2s segments vs 6s for VOD), 4) Player: low-latency HLS (LL-HLS) or WebRTC playback. For interactive (live chat, polls), real-time event fanout."

**Interviewer**: "How do you serve video ads?"

**Candidate**: "Ad insertion: 1) Ad decision server receives ad request (user_id, video_id, demographics), 2) Real-time bidding (RTB) or programmatic ad selection, 3) Ad server returns ad URL, 4) Player inserts ad segments via client-side ad insertion (CSAI) or server-side (SSAI). Server-side is better for ad-block resistance and consistent experience, but less flexible."

---

## Key Takeaways

- **Upload pipeline**: Chunked resumable upload → blob storage → transcoding queue
- **Transcoding ladder**: Multiple resolutions + codecs per video
- **Adaptive bitrate**: Player switches quality based on network conditions
- **CDN hierarchy**: Edge → Regional → Origin, pre-populate popular content
- **Recommendation**: Offline embedding + online candidate ranking
- **Live streaming**: Low-latency protocols (LL-HLS, WebRTC), 1-2s segments
