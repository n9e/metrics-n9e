package com.didiyun.n9e.metrics;

import java.io.IOException;

public class N9ESender {

    // address是collector的地址，举例：http://127.0.0.1:2058/api/collector/push?nid=23
    private String address;

    public N9ESender(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "N9ESender(" + address + ")";
    }

    // TODO: 调用address指定的地址，post json数据即可
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
    public void send(String metric, String tags, Object value, long timestamp) throws IOException {

    }

}
