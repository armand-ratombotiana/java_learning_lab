package com.dataeng.ten;

import org.apache.flink.api.common.functions.AggregateFunction;

public class SensorAggregator implements AggregateFunction<SensorReading, SensorAggregator.Accumulator, AggregateResult> {

    public static class Accumulator {
        double sum = 0;
        long count = 0;
        double max = Double.MIN_VALUE;
    }

    @Override
    public Accumulator createAccumulator() { return new Accumulator(); }

    @Override
    public Accumulator add(SensorReading r, Accumulator acc) {
        acc.sum += r.getValue();
        acc.count++;
        acc.max = Math.max(acc.max, r.getValue());
        return acc;
    }

    @Override
    public AggregateResult getResult(Accumulator acc) {
        return new AggregateResult("", 0L, acc.sum / acc.count, acc.max, acc.count);
    }

    @Override
    public Accumulator merge(Accumulator a, Accumulator b) {
        a.sum += b.sum;
        a.count += b.count;
        a.max = Math.max(a.max, b.max);
        return a;
    }
}
