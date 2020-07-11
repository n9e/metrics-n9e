# metrics-n9e

依赖：

[metrics](https://metrics.dropwizard.io/)是一个很通用的指标埋点SDK，本项目的作用是适配[n9e](https://github.com/didi/nightingale)，把埋点指标推送给n9e：

```
// 使用n9e的sender，nid是服务树的节点id
N9ESender n9eSender = new N9ESender("http://127.0.0.1/api/collector/push?nid=1",100);

// 使用n9e的reporter，service(唯一标识自己的服务名)，region(机房分区)，instance(实例地址，如果是k8s可以用podname)
N9EReporter reporter = N9EReporter.forRegistry(registry)
        .withTags("service=n9e-judge,region=bj,instance="+ Hostname.get())
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .filter(MetricFilter.ALL)
        .build(n9eSender);
```

各公司在用的时候，可以做成一个jar给业务方使用，也可以用源文件的方式共享。不同公司可以根据自己的情况微调，比如一些分位数据可能不需要那么多，就可以少汇报一些。在example下面有使用样例