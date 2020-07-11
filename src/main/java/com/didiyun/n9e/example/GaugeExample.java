package com.didiyun.n9e.example;

import com.codahale.metrics.*;
import com.didiyun.n9e.metrics.N9EReporter;
import com.didiyun.n9e.metrics.N9ESender;
import com.didiyun.n9e.tools.Hostname;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class GaugeExample {

    public static Queue<String> q = new LinkedList<String>();

    public static void main(String[] args) throws InterruptedException {
        MetricRegistry registry = new MetricRegistry();

        N9ESender n9eSender = new N9ESender("http://127.0.0.1/api/collector/push?nid=1",100);
        N9EReporter reporter = N9EReporter.forRegistry(registry)
                .withTags("service=n9e-judge,region=bj,instance="+ Hostname.get())
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(n9eSender);
        reporter.start(1, TimeUnit.MINUTES);

        registry.register(MetricRegistry.name(GaugeExample.class,"queue", "size"),
                new Gauge<Integer>() {

                    public Integer getValue() {
                        return q.size();
                    }
                });

        while(true){
            Thread.sleep(1000);
            q.add("Job-xxx");
        }
    }
}