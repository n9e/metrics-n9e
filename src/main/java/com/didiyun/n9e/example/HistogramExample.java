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

        // endpoint 可以使用机器 hostname 也可以使用 ip ，取决于各公司的自身情况，要和 collector 模块的 identity 配置保持一致
        // 下面的例子是使用的本机 hostname ；如果是在容器环境，pod的标识经常变化，导致 endpoint 经常变化，不适合用例子中所给的方式
        // 容器场景有个特定的使用逻辑，是在/api/collector/push后面附加QueryString：nid=xx，然后附加instance tag，取消endpoint
        N9ESender n9eSender = new N9ESender("http://127.0.0.1/api/collector/push",100);
        N9EReporter reporter = N9EReporter.forRegistry(registry)
                .withTags("service=n9e-judge,region=bj")
                .withEndpoint(Hostname.get())
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
