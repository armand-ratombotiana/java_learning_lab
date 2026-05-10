package com.learning.streaming;

import java.util.*;
import java.util.function.*;

public class StreamingSolution {

    public static class StreamJob {
        private String name;
        private String source;
        private List<StreamOperator> operators;
        private String sink;

        public StreamJob(String name) {
            this.name = name;
            this.operators = new ArrayList<>();
        }

        public StreamJob source(String source) {
            this.source = source;
            return this;
        }

        public StreamJob addOperator(StreamOperator operator) {
            this.operators.add(operator);
            return this;
        }

        public StreamJob sink(String sink) {
            this.sink = sink;
            return this;
        }

        public String getName() { return name; }
        public List<StreamOperator> getOperators() { return operators; }
    }

    public static class StreamOperator {
        private String type;
        private String name;
        private Function<Object, Object> function;

        public StreamOperator(String type, String name) {
            this.type = type;
            this.name = name;
        }

        public void setFunction(Function<Object, Object> func) {
            this.function = func;
        }

        public Object apply(Object input) {
            return function != null ? function.apply(input) : input;
        }

        public String getType() { return type; }
        public String getName() { return name; }
    }

    public StreamJob createJob(String name) {
        return new StreamJob(name);
    }

    public StreamOperator createMapOperator(String name, Function<Object, Object> mapper) {
        StreamOperator op = new StreamOperator("map", name);
        op.setFunction(mapper);
        return op;
    }

    public StreamOperator createFilterOperator(String name, Predicate<Object> predicate) {
        StreamOperator op = new StreamOperator("filter", name);
        op.setFunction(obj -> predicate.test(obj) ? obj : null);
        return op;
    }

    public StreamOperator createFlatMapOperator(String name, Function<Object, Iterable<Object>> flatMapper) {
        StreamOperator op = new StreamOperator("flatmap", name);
        op.setFunction(obj -> {
            Iterable<Object> result = flatMapper.apply(obj);
            List<Object> list = new ArrayList<>();
            result.forEach(list::add);
            return list;
        });
        return op;
    }

    public StreamOperator createReduceOperator(String name, BinaryOperator<Object> reducer) {
        StreamOperator op = new StreamOperator("reduce", name);
        op.setFunction(reducer);
        return op;
    }

    public static class Window {
        private String type;
        private long size;
        private long slide;

        public Window(String type, long size) {
            this.type = type;
            this.size = size;
        }

        public Window sliding(long slide) {
            this.slide = slide;
            return this;
        }

        public String getType() { return type; }
        public long getSize() { return size; }
        public long getSlide() { return slide; }
    }

    public Window createTumblingWindow(long size) {
        return new Window("tumbling", size);
    }

    public Window createSlidingWindow(long size, long slide) {
        return new Window("sliding", size).sliding(slide);
    }

    public Window createSessionWindow(long gap) {
        return new Window("session", gap);
    }

    public static class Watermark {
        private long delay;
        private long maxOutOfOrderness;

        public Watermark(long delay) {
            this.delay = delay;
            this.maxOutOfOrderness = delay;
        }

        public long getDelay() { return delay; }
        public long getMaxOutOfOrderness() { return maxOutOfOrderness; }
    }

    public Watermark createWatermark(long delay) {
        return new Watermark(delay);
    }

    public static class Checkpoint {
        private String interval;
        private String mode;

        public Checkpoint(String interval, String mode) {
            this.interval = interval;
            this.mode = mode;
        }

        public String getInterval() { return interval; }
        public String getMode() { return mode; }
    }

    public Checkpoint createCheckpoint(String interval) {
        return new Checkpoint(interval, "exactly-once");
    }

    public static class FlinkJob {
        private StreamJob job;
        private Checkpoint checkpoint;

        public FlinkJob(StreamJob job) {
            this.job = job;
        }

        public FlinkJob withCheckpoint(Checkpoint checkpoint) {
            this.checkpoint = checkpoint;
            return this;
        }

        public void submit() {}
    }

    public FlinkJob createFlinkJob(StreamJob job) {
        return new FlinkJob(job);
    }

    public static class StormTopology {
        private Map<String, Spout> spouts;
        private Map<String, Bolt> bolts;

        public StormTopology() {
            this.spouts = new HashMap<>();
            this.bolts = new HashMap<>();
        }

        public StormTopology addSpout(String name, Spout spout) {
            spouts.put(name, spout);
            return this;
        }

        public StormTopology addBolt(String name, Bolt bolt) {
            bolts.put(name, bolt);
            return this;
        }

        public void submit() {}
    }

    public static class Spout {
        private String name;

        public Spout(String name) {
            this.name = name;
        }

        public String getName() { return name; }
    }

    public static class Bolt {
        private String name;
        private String[] inputs;

        public Bolt(String name, String... inputs) {
            this.name = name;
            this.inputs = inputs;
        }

        public String getName() { return name; }
        public String[] getInputs() { return inputs; }
    }

    public StormTopology createTopology() {
        return new StormTopology();
    }

    public Spout createSpout(String name) {
        return new Spout(name);
    }

    public Bolt createBolt(String name, String... inputs) {
        return new Bolt(name, inputs);
    }
}