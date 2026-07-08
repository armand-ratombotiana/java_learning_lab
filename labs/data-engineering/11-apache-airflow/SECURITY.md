# Security for Apache Airflow

## Authentication & Authorization
- LDAP, OAuth, OpenID Connect, or Google Auth
- RBAC roles: Admin, Op, User, Viewer, Public
- Fernet key for variable/connection encryption

## Secrets Management
```python
from airflow.models import Variable
secret = Variable.get('my_secret', deserialize_json=True)
conn = BaseHook.get_connection('my_db')
```

## Network
- Private subnets for workers
- TLS for webserver
- Separate metadata database with encrypted connections
