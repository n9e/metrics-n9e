package com.didiyun.n9e.metrics;

public class MetricTuple {

    private String name;
    private String tags;
    private long timestamp;
    private Object value;

    public MetricTuple(String name, String tags, Object value, long timestamp) {
        this.name = name;
        this.tags = tags;
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "MetricTuple{" +
                "name='" + name + '\'' +
                ", tags='" + tags + '\'' +
                ", timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }
}