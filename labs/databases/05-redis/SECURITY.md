# Security: Redis

## Authentication
```conf
# redis.conf
requirepass strong_password_here
masterauth strong_password_here  # for replica connections
```

```java
// Jedis
Jedis jedis = new Jedis("localhost", 6379);
jedis.auth("strong_password_here");

// Lettuce
RedisURI uri = RedisURI.create("redis://:strong_password_here@localhost:6379");
RedisClient client = RedisClient.create(uri);

// Spring Boot
spring.redis.password=strong_password_here
```

## ACL (Access Control Lists) — Redis 6.0+

```conf
# Default user (limited)
user default off

# Application user (read/write on app:* keys)
user app_user on >app_password +@all -@dangerous ~app:*

# Read-only user for analytics
user analytics on >analytics_pass +@read ~app:*
```

```java
// Connect as specific ACL user
RedisURI uri = RedisURI.create("redis://app_user:app_password@localhost:6379");
```

## TLS/SSL

```conf
# redis.conf
tls-port 6380
port 0
tls-cert-file /etc/redis/redis.crt
tls-key-file /etc/redis/redis.key
tls-ca-cert-file /etc/redis/ca.crt
tls-auth-clients yes
tls-protocols "TLSv1.2 TLSv1.3"
```

```yaml
spring:
  redis:
    ssl: true
    url: rediss://user:pass@host:6380
```

## Network Security

```conf
# redis.conf
bind 127.0.0.1 10.0.0.1    # restrict to specific IPs
protected-mode yes          # only bind to loopback + requirepass
rename-command FLUSHALL ""  # disable dangerous commands
rename-command CONFIG ""    # or rename to hard-to-guess name
rename-command EVAL ""      # disable Lua if not needed
rename-command SHUTDOWN ""  # prevent remote shutdown
```

## Firewall Rules

```bash
# Only allow application servers
iptables -A INPUT -p tcp --dport 6379 -s 10.0.0.0/8 -j ACCEPT
iptables -A INPUT -p tcp --dport 6379 -j DROP

# Separate cluster bus port
iptables -A INPUT -p tcp --dport 16379 -s 10.0.0.0/8 -j ACCEPT
```

## Data Encryption in Transit
```conf
# For Sentinel
sentinel tls-port 26379
sentinel tls-cert-file /etc/redis/sentinel.crt

# For Cluster
cluster-announce-tls-port 16379
tls-cluster yes
tls-replication yes
```

## Security Best Practices
1. Never run Redis as root
2. Use non-default ports
3. Always enable `requirepass` or ACL
4. Bind to specific network interfaces
5. Disable dangerous commands via `rename-command`
6. Use TLS in production
7. Enable protected mode
8. Keep Redis version up to date
