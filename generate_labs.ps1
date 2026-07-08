param(
    [string]$BasePath = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\networking"
)

$labs = @(
    @{Name="11-dns-load-balancing"; Topic="DNS Load Balancing"; Topics=@("DNS Resolution","Round-Robin DNS","GeoDNS","Anycast","DNS-based Failover","DNSSEC","DNS Caching"); Package="dns-load-balancing"; Num=11},
    @{Name="12-http3-quic"; Topic="HTTP/3 and QUIC"; Topics=@("HTTP/3","QUIC Protocol","0-RTT","Multiplexing","Connection Migration","HTTP/3 vs HTTP/2","QUIC Loss Detection"); Package="http3-quic"; Num=12},
    @{Name="13-network-security-deep"; Topic="Advanced Network Security"; Topics=@("TLS 1.3","mTLS","Certificate Pinning","HSTS","HPKP","OCSP Stapling","TLS Handshake"); Package="network-security-deep"; Num=13},
    @{Name="14-message-queues-protocols"; Topic="Message Queues and Protocols"; Topics=@("AMQP","MQTT","STOMP","Protocol Comparison","QoS Levels","Pub/Sub","Message Routing"); Package="message-queues-protocols"; Num=14},
    @{Name="15-grpc-advanced"; Topic="Advanced gRPC"; Topics=@("gRPC Load Balancing","Deadline Propagation","Interceptors","Reflection","Health Checking","gRPC Streaming","Error Handling"); Package="grpc-advanced"; Num=15}
)

$topicsStr = @()
$labs | ForEach-Object { $topicsStr += $_.Topics -join ", " }

Write-Output "Ready to generate labs"
