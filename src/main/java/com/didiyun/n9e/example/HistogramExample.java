package com.didiyun.n9e.example;

import com.codahale.metrics.*;
import com.didiyun.n9e.metrics.N9EReporter;
import com.didiyun.n9e.metrics.N9ESender;
import com.didiyun.n9e.tools.Hostname;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class HistogramExample {

    public static Random random = new Random();

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

        Histogram histogram = new Histogram(new ExponentiallyDecayingReservoir());
        registry.register(MetricRegistry.name(HistogramExample.class, "request", "histogram"), histogram);

        while(true){
            Thread.sleep(1000);
            histogram.update(random.nextInt(100000));
        }

    }
}
