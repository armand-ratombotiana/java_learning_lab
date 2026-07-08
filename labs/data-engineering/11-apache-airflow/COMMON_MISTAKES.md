# Common Mistakes with Apache Airflow

1. Not Setting catchup=False: Deploying new DAG triggers backfill of all missed intervals; set catchup=False unless backfill intended
2. Large XCom Values: XCom limit is 48KB; store large data in S3/GCS and pass the object path reference
3. Long DAG Parse Time: Too many DAG files or complex parsing; keep DAGs simple and minimize imports at top level
4. Missing depends_on_past: Tasks running concurrently that should be sequential; set depends_on_past=True when needed
5. Pool Exhaustion: Too many tasks competing for limited pool slots; add pools or increase default_pool size
