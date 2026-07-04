# Security

## RBAC
Enable RBAC in airflow.cfg for role-based access.

## Variables
Use Airflow Variables (encrypted) for secrets.

## Connections
```python
conn = BaseHook.get_connection("my_db")
password = conn.get_password()
```

## Network
Use private subnets, VPC peering, SSL for webserver.
