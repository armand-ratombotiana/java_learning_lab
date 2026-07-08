# Step-by-Step: Working with Real-Time Feature Store

1. Install Feast: pip install feast
2. Initialize project: feast init my_feature_repo
3. Define entity: entity = Entity(name="user_id", join_keys=["user_id"])
4. Define feature view with batch source and transformations
5. Apply registry: feast apply (uploads definitions to registry)
6. Materialize features to online store: feast materialize-incremental
7. Generate training data: get_historical_features(entity_df, features)
8. Serve features: feast serve (starts REST server)
9. Test serving: curl http://localhost:6566/get-online-features
10. Monitor materialization lag and online/offline consistency
