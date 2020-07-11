package com.didiyun.n9e.example;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.didiyun.n9e.metrics.N9EReporter;
import com.didiyun.n9e.metrics.N9ESender;
import com.didiyun.n9e.tools.Hostname;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MeterExample {

    public static Random random = new Random();

    public static void request(Meter meter){
        System.out.println("request");
        meter.mark();
    }

    public static void request(Meter meter, int n){
        while(n > 0){
            request(meter);
            n--;
        }
    }

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

        Meter meterTps = registry.meter(MetricRegistry.name(MeterExample.class,"request","tps"));

        while(true){
            request(meterTps,random.nextInt(5));
            Thread.sleep(1000);
        }

    }
}