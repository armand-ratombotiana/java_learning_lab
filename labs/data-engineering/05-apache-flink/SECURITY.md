# Security

## Kerberos
```java
env.getConfig().setGlobalJobParameters(new Configuration() {{
    setString("security.kerberos.login.keytab", "/path/to/keytab");
}});
```

## SSL
Enable SSL in flink-conf.yaml for network encryption.
