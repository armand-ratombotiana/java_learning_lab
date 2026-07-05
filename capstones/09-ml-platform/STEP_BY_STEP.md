# Step by Step: ML Platform

## Training a Model

1. Data scientist uploads training config: model_type=XGBoost, features=[f1,f2,f3], target=label, hyperparameters={max_depth:6, eta:0.3}
2. TrainingOrchestrator reads config, queries offline feature store for training data
3. Data is split: 80% train, 10% validate, 10% test
4. Spark/XGBoost4J training job runs; model is trained, metrics computed
5. Training metrics logged: accuracy=0.94, precision=0.92, recall=0.89, F1=0.90, AUC=0.97
6. Model artifact (XGBoost booster or ONNX) saved to S3/model-registry/
7. ModelRegistry creates version entry: name=churn-pred, version=3, metrics={...}, status=CREATED
8. Evaluation: model evaluated against test set; if metrics > threshold, status -> STAGING
9. Manual approval: operations team promotes to PRODUCTION
10. ServingService loads new model version, registers it; traffic gradually shifted
