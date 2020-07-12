package com.didiyun.n9e.metrics;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.didiyun.n9e.tools.HttpSender;
import com.sun.istack.internal.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class N9ESender {

    private static final Logger LOGGER = LoggerFactory.getLogger(N9ESender.class);

    // address是collector的地址，举例：http://127.0.0.1:2058/api/collector/push?nid=23
    private String address;
    private HttpSender httpSender;
    private int batchSize = N9ESenderConstant.DEFAULT_BATCH_SIZE;
    private List<MetricTuple> metrics = new LinkedList<MetricTuple>();

    public N9ESender(@NotNull String address) {
        this.address = address;
        this.httpSender = new HttpSender(address);
    }

    public N9ESender(@NotNull String address, @NotNull int batchSize) {
        this.address = address;
        if (batchSize > 1) {
            this.batchSize = batchSize;
        }
        this.httpSender = new HttpSender(address);
    }

    @Override
    public String toString() {
        return "N9ESender(" + address + ")";
    }

    public void flush() throws IOException {
        writeMetrics();
    }

    public void send(String endpoint, String metric, String tags, Object value, long timestamp) throws IOException {
        metrics.add(new MetricTuple(endpoint, metric, tags, value, timestamp));

        if (metrics.size() >= batchSize) {
            writeMetrics();
        }
    }

    private void writeMetrics() throws IOException {
        try {
            post();
            LOGGER.debug("Wrote {} metrics", metrics.size());
        } catch (IOException e) {
            throw e;
        } finally {
            // if there was an error, we might miss some data. for now, drop those on the floor and
            // try to keep going.
            metrics.clear();
        }
    }

    /*
     * e.g.
     * [
     *   {
     *       "metric": "com.a.x.request.latency",
     *       "tags": "service=a,module=b",
     *       "value": 12.21,
     *       "timestamp": 12345676543
     *   }
     * ]
     *
     * */
    private void post() throws IOException {
        if (metrics == null || metrics.isEmpty()) return;
        JSONArray arrayJson = new JSONArray();
        for (MetricTuple item : metrics) {
            JSONObject itemJson = new JSONObject();
            itemJson.put(N9ESenderConstant.METRIC_TUPLE_ENDPOINT, item.getEndpoint());
            itemJson.put(N9ESenderConstant.METRIC_TUPLE_METRIC, item.getName());
            itemJson.put(N9ESenderConstant.METRIC_TUPLE_TAGS, item.getTags());
            itemJson.put(N9ESenderConstant.METRIC_TUPLE_VALUE, item.getValue());
            itemJson.put(N9ESenderConstant.METRIC_TUPLE_TIMESTAMP, item.getTimestamp());
            arrayJson.add(itemJson);
        }

        httpSender.postJSON(arrayJson.toJSONString());
    }

    /**
     * N9ESender常量数据
     * */
    private interface N9ESenderConstant {
        String METRIC_TUPLE_ENDPOINT = "endpoint";
        String METRIC_TUPLE_METRIC = "metric";
        String METRIC_TUPLE_TIMESTAMP = "timestamp";
        String METRIC_TUPLE_TAGS = "tags";
        String METRIC_TUPLE_VALUE = "value";
        int DEFAULT_BATCH_SIZE = 100;
    }
}
