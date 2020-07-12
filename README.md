# metrics-n9e

[metrics](https://metrics.dropwizard.io/)是一个很通用的指标埋点SDK，本项目的作用是适配[n9e](https://github.com/didi/nightingale)，把埋点指标推送给n9e

各公司在用的时候，可以做成一个jar给业务方使用，也可以用源文件的方式共享。不同公司可以根据自己的情况微调，比如一些分位数据可能不需要那么多，就可以少汇报一些。在example下面有使用样例。