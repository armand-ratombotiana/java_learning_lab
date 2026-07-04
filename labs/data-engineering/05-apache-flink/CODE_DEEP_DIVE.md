# Code Deep Dive: Flink Fraud Detection

## KeyedProcessFunction
```java
public class FraudDetector extends KeyedProcessFunction<String, Transaction, Alert> {
    private ValueState<Double> lastAmountState;

    @Override
    public void open(Configuration params) {
        lastAmountState = getRuntimeContext()
            .getState(new ValueStateDescriptor<>("lastAmount", Types.DOUBLE));
    }

    @Override
    public void processElement(Transaction tx, Context ctx, Collector<Alert> out) {
        Double last = lastAmountState.value();
        if (last != null && tx.getAmount() > 1000 && last > 1000
            && tx.getTimestamp() - lastTimestamp < 60000) {
            out.collect(new Alert("Suspicious activity", tx.getAccountId()));
        }
        lastAmountState.update(tx.getAmount());
    }
}
```
