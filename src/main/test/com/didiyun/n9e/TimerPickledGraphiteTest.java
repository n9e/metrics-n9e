package com.didiyun.n9e;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.graphite.PickledGraphite;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TimerPickledGraphiteTest {

    public static Random random = new Random();

    public static void main(String[] args) throws InterruptedException {
        MetricRegistry registry = new MetricRegistry();


        final PickledGraphite pickledGraphite = new PickledGraphite(new InetSocketAddress("graphite.example.com", 2004));
        final GraphiteReporter reporter = GraphiteReporter.forRegistry(registry)
                .prefixedWith("web1.example.com")
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(pickledGraphite);
        reporter.start(1, TimeUnit.MINUTES);

        Timer timer = registry.timer(MetricRegistry.name(TimerPickledGraphiteTest.class,"get-latency"));

        Timer.Context ctx;

        while(true){
            ctx = timer.time();
            Thread.sleep(random.nextInt(1000));
            ctx.stop();
        }

    }

}