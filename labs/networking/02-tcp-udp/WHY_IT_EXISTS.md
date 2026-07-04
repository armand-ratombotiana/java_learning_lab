# TCP/UDP - Why It Exists

## The Problem

IP (Internet Protocol) provides packet delivery but offers no guarantees:
- Packets may be lost, duplicated, or reordered
- No mechanism for ensuring data arrives intact
- No congestion control

## TCP's Purpose
TCP adds reliability on top of IP:
1. **Reliable delivery**: Acknowledgments and retransmission
2. **Ordered delivery**: Sequence numbers reorder packets
3. **Flow control**: Receiver controls sender's rate (sliding window)
4. **Congestion control**: Network-aware rate adaptation

## UDP's Purpose
UDP exists for applications where speed matters more than reliability:
1. **Low latency**: No connection setup or acknowledgment overhead
2. **Real-time communication**: VoIP, video calls, gaming
3. **Simple protocols**: DNS, DHCP, SNMP
4. **Multicast/broadcast**: One-to-many communication
