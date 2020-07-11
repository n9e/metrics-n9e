package com.didiyun.n9e.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class N9ESender {

    private static final Logger LOGGER = LoggerFactory.getLogger(N9ESender.class);

    // address是collector的地址，举例：http://127.0.0.1:2058/api/collector/push?nid=23
    private String address;
    private int batchSize = 100;
    private List<MetricTuple> metrics = new LinkedList<MetricTuple>();

    public N9ESender(String address) {
        this.address = address;
    }

    public N9ESender(String address, int batchSize) {
        this.address = address;
        this.batchSize = batchSize;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public String toString() {
        return "N9ESender(" + address + ")";
    }

    public void flush() throws IOException {
        writeMetrics();
    }

    public void send(String metric, String tags, Object value, long timestamp) throws IOException {
        metrics.add(new MetricTuple(metric, tags, value, timestamp));

        if (metrics.size() >= batchSize) {
            writeMetrics();
        }
    }

    private void writeMetrics() throws IOException {
        if (metrics.size() == 0) {
            return;
        }

        try {
            post();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Wrote {} metrics", metrics.size());
            }
        } catch (IOException e) {
            throw e;
        } finally {
            // if there was an error, we might miss some data. for now, drop those on the floor and
            // try to keep going.
            metrics.clear();
        }
    }

    /*
     * TODO: item.name就是metric，整理成json array，发送即可
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
        for (MetricTuple item : metrics) {
            System.out.println(item.toString());
        }
    }

}
