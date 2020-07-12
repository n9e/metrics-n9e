package com.didiyun.n9e.metrics;

public class MetricTuple {

    private String endpoint;
    private String name;
    private String tags;
    private long timestamp;
    private Object value;

    public MetricTuple(String endpoint, String name, String tags, Object value, long timestamp) {
        this.endpoint = endpoint;
        this.name = name;
        this.tags = tags;
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
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
                "endpoint='" + endpoint + '\'' +
                ", name='" + name + '\'' +
                ", tags='" + tags + '\'' +
                ", timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }
}