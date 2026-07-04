# Replication: Step by Step

## Setting Up MySQL Replication

### Step 1: Configure leader
```sql
-- Enable binlog on leader
server-id = 1
log_bin = /var/log/mysql/mysql-bin.log
binlog_format = ROW
```

### Step 2: Create replication user
```sql
CREATE USER 'replicator'@'%' IDENTIFIED BY 'password';
GRANT REPLICATION SLAVE ON *.* TO 'replicator'@'%';
```

### Step 3: Configure follower
```sql
server-id = 2
relay-log = /var/log/mysql/mysql-relay-bin.log
```

### Step 4: Start replication
```sql
CHANGE MASTER TO
  MASTER_HOST='leader-host',
  MASTER_USER='replicator',
  MASTER_PASSWORD='password',
  MASTER_LOG_FILE='mysql-bin.000001',
  MASTER_LOG_POS=0;
START SLAVE;
```
