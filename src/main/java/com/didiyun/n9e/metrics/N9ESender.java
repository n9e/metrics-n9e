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

    public void send(String name, String tags, Object value, long timestamp) throws IOException {
        // 调用address指定的地址，post json数据即可
    }

}
