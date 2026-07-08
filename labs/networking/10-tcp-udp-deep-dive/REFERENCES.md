# TCP/UDP Deep Dive — References

## RFCs (Request for Comments)

### Core TCP Specifications
| RFC | Title | Year |
|-----|-------|------|
| RFC 793 | Transmission Control Protocol | 1981 |
| RFC 1122 | Requirements for Internet Hosts — Communication Layers | 1989 |
| RFC 5681 | TCP Congestion Control | 2009 |
| RFC 6298 | Computing TCP's Retransmission Timer | 2011 |
| RFC 7323 | TCP Extensions for High Performance (Window Scale, Timestamps) | 2014 |
| RFC 7414 | TCP Roadmap | 2015 |

### Congestion Control
| RFC | Title | Year |
|-----|-------|------|
| RFC 896 | Congestion Control in IP/TCP Internetworks (Nagle) | 1984 |
| RFC 2001 | TCP Slow Start, Congestion Avoidance, Fast Retransmit, Fast Recovery | 1997 |
| RFC 2581 | TCP Congestion Control | 1999 |
| RFC 3390 | Increasing TCP's Initial Window | 2002 |
| RFC 3465 | TCP Congestion Control with Appropriate Byte Counting | 2003 |
| RFC 3782 | TCP NewReno | 2004 |
| RFC 6928 | Increasing TCP's Initial Window to 10 Segments | 2013 |
| RFC 8312 | TCP Cubic | 2018 |

### TCP Enhancements
| RFC | Title | Year |
|-----|-------|------|
| RFC 896 | Nagle's Algorithm | 1984 |
| RFC 1323 | TCP Extensions for High Performance | 1992 |
| RFC 2018 | TCP Selective Acknowledgment (SACK) | 1996 |
| RFC 2883 | TCP SACK Permitted Option | 2000 |
| RFC 3042 | TCP Limited Transmit | 2001 |
| RFC 4015 | TCP ECN Validation | 2005 |
| RFC 5961 | Improving TCP's Robustness to Blind In-Window Attacks | 2010 |
| RFC 6191 | Reducing TIME_WAIT Using TCP Timestamps | 2011 |
| RFC 7763 | TCP Trampoline | 2016 |

### UDP Specifications
| RFC | Title | Year |
|-----|-------|------|
| RFC 768 | User Datagram Protocol | 1980 |
| RFC 8085 | UDP Usage Guidelines | 2017 |
| RFC 5405 | Unicast UDP Usage Guidelines | 2009 |

### QUIC and HTTP/3
| RFC | Title | Year |
|-----|-------|------|
| RFC 9000 | QUIC: A UDP-Based Multiplexed and Secure Transport | 2021 |
| RFC 9001 | Using TLS to Secure QUIC | 2021 |
| RFC 9002 | QUIC Loss Detection and Congestion Control | 2021 |
| RFC 9114 | HTTP/3 | 2022 |

## Books

| Title | Author | Year |
|-------|--------|------|
| TCP/IP Illustrated, Volume 1 (2nd Ed.) | Kevin Fall, W. Richard Stevens | 2011 |
| TCP/IP Illustrated, Volume 2 | Gary Wright, W. Richard Stevens | 1995 |
| Internetworking with TCP/IP, Volume 1 | Douglas Comer | 2014 |
| Computer Networks (5th Ed.) | Andrew Tanenbaum, David Wetherall | 2010 |
| Unix Network Programming, Volume 1 (3rd Ed.) | W. Richard Stevens, Bill Fenner, Andrew Rudoff | 2003 |
| High Performance Browser Networking | Ilya Grigorik | 2013 |
| The TCP/IP Guide | Charles Kozierok | 2005 |
| Network Programming in Java | Elliotte Rusty Harold | 2004 |
| Java Network Programming (4th Ed.) | Elliotte Rusty Harold | 2013 |

## Academic Papers

| Paper | Authors | Year |
|-------|---------|------|
| "Congestion Avoidance and Control" | Van Jacobson | 1988 |
| "TCP/IP: The ARPA Protocol Suite" | Jon Postel | 1985 |
| "TCP Vegas: New Techniques for Congestion Detection and Avoidance" | Brakmo, Peterson | 1994 |
| "CUBIC: A New TCP-Friendly High-Speed TCP Variant" | Ha, Rhee, Xu | 2008 |
| "BBR: Congestion-Based Congestion Control" | Cardwell, et al. | 2016 |
| "An Analysis of TCP Throughput" | Padhye, et al. | 1998 |
| "Designing a High-Performance TCP in User Space" | Jeong, et al. | 2019 |

## Tools

### Packet Capture and Analysis
| Tool | Description | URL |
|------|-------------|-----|
| Wireshark | GUI packet analyzer | https://www.wireshark.org/ |
| tcpdump | CLI packet capture | https://www.tcpdump.org/ |
| tshark | CLI packet analyzer (part of Wireshark) | https://www.wireshark.org/docs/man-pages/tshark.html |
| ngrep | Network grep | https://github.com/jpr5/ngrep |

### Network Testing
| Tool | Description | URL |
|------|-------------|-----|
| iperf3 | Network throughput tester | https://iperf.fr/ |
| netperf | Network performance benchmark | https://github.com/HewlettPackard/netperf |
| mtr | Traceroute + ping combined | https://www.bitwizard.nl/mtr/ |
| nmap | Network scanner | https://nmap.org/ |
| sockperf | Socket latency testing | https://github.com/Mellanox/sockperf |

### TCP Tuning
| Tool | Description |
|------|-------------|
| ss | Socket statistics (replacement for netstat) |
| tc | Traffic control (Linux) |
| ethtool | NIC configuration and status |
| sysctl | Kernel parameter tuning |

## Java Documentation

| Resource | URL |
|----------|-----|
| java.net.Socket | https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/net/Socket.html |
| java.net.ServerSocket | https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/net/ServerSocket.html |
| java.net.DatagramSocket | https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/net/DatagramSocket.html |
| java.net.MulticastSocket | https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/net/MulticastSocket.html |
| java.net.InetAddress | https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/net/InetAddress.html |
| java.net.SocketOptions | https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/net/SocketOptions.html |

## Online Resources

| Resource | URL |
|----------|-----|
| TCP State Machine Diagram | https://www.iana.org/assignments/tcp-parameters/tcp-parameters.xhtml |
| Linux TCP/IP Tuning | https://github.com/leandromoreira/linux-network-performance-parameters |
| Beej's Guide to Network Programming | https://beej.us/guide/bgnet/ |
| Cloudflare Learning Center | https://www.cloudflare.com/learning/ |
| Computer Networking: A Top-Down Approach | Kurose & Ross (accompanying site) |
