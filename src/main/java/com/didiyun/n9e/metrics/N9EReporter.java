package com.didiyun.n9e.metrics;

import com.codahale.metrics.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

/**
 * A reporter which publishes metric values to a N9E server.
 *
 * @see <a href="https://n9e.didiyun.com/">N9E - A Distributed and High-Performance Monitoring System</a>
 */
public class N9EReporter extends ScheduledReporter {

    public static Builder forRegistry(MetricRegistry registry) {
        return new Builder(registry);
    }

    public static class Builder {
        private final MetricRegistry registry;
        private Clock clock;
        private String prefix;
        private String tags;
        private String endpoint;
        private int step;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private MetricFilter filter;

        private Builder(MetricRegistry registry) {
            this.registry = registry;
            this.clock = Clock.defaultClock();
            this.prefix = null;
            this.tags = "";
            this.endpoint = "";
            this.step = 60;
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.filter = MetricFilter.ALL;
        }

        public Builder withClock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public Builder prefixedWith(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public Builder withTags(String tags) {
            this.tags = tags;
            return this;
        }

        public Builder withEndpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder withStep(int step) {
            this.step = step;
            return this;
        }

        public Builder convertRatesTo(TimeUnit rateUnit) {
            this.rateUnit = rateUnit;
            return this;
        }

        public Builder convertDurationsTo(TimeUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }

        public Builder filter(MetricFilter filter) {
            this.filter = filter;
            return this;
        }


        public N9EReporter build(N9ESender n9eSender) {
            return new N9EReporter(registry,
                    n9eSender,
                    clock,
                    prefix,
                    tags,
                    endpoint,
                    step,
                    rateUnit,
                    durationUnit,
                    filter);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(N9EReporter.class);

    private final N9ESender n9eSender;
    private final Clock clock;
    private final String prefix;
    private final String tags;
    private final String endpoint;
    private final int step;

    private N9EReporter(MetricRegistry registry,
                        N9ESender n9eSender,
                        Clock clock,
                        String prefix,
                        String tags,
                        String endpoint,
                        int step,
                        TimeUnit rateUnit,
                        TimeUnit durationUnit,
                        MetricFilter filter) {
        super(registry, "n9e-reporter", filter, rateUnit, durationUnit);
        this.n9eSender = n9eSender;
        this.clock = clock;
        this.prefix = prefix;
        this.tags = tags;
        this.endpoint = endpoint;
        this.step = step;
    }

    @Override
    public void report(SortedMap<String, Gauge> gauges,
                       SortedMap<String, Counter> counters,
                       SortedMap<String, Histogram> histograms,
                       SortedMap<String, Meter> meters,
                       SortedMap<String, Timer> timers) {
        final long timestamp = clock.getTime() / 1000;

        try {
            for (Map.Entry<String, Gauge> entry : gauges.entrySet()) {
                reportGauge(entry.getKey(), entry.getValue(), timestamp);
            }

            for (Map.Entry<String, Counter> entry : counters.entrySet()) {
                reportCounter(entry.getKey(), entry.getValue(), timestamp);
            }

            for (Map.Entry<String, Histogram> entry : histograms.entrySet()) {
                reportHistogram(entry.getKey(), entry.getValue(), timestamp);
            }

            for (Map.Entry<String, Meter> entry : meters.entrySet()) {
                reportMetered(entry.getKey(), entry.getValue(), timestamp);
            }

            for (Map.Entry<String, Timer> entry : timers.entrySet()) {
                reportTimer(entry.getKey(), entry.getValue(), timestamp);
            }

            n9eSender.flush();
        } catch (IOException e) {
            LOGGER.warn("Unable to report to N9E", n9eSender, e);
        }
    }

    private void reportTimer(String name, Timer timer, long timestamp) throws IOException {
        final Snapshot snapshot = timer.getSnapshot();

        send(prefix(name), convertDuration(snapshot.getMax()), timestamp, "func=max");
        send(prefix(name), convertDuration(snapshot.getMean()), timestamp, "func=mean");
        send(prefix(name), convertDuration(snapshot.getMin()), timestamp, "func=min");
        send(prefix(name), convertDuration(snapshot.getStdDev()),timestamp, "func=stddev");

        send(prefix(name), convertDuration(snapshot.get999thPercentile()), timestamp, "quantile=p50");
        send(prefix(name), convertDuration(snapshot.get999thPercentile()), timestamp, "quantile=p75");
        send(prefix(name), convertDuration(snapshot.get999thPercentile()), timestamp, "quantile=p95");
        send(prefix(name), convertDuration(snapshot.get999thPercentile()), timestamp, "quantile=p98");
        send(prefix(name), convertDuration(snapshot.get999thPercentile()), timestamp, "quantile=p99");
        send(prefix(name), convertDuration(snapshot.get999thPercentile()), timestamp, "quantile=p999");

        reportMetered(name, timer, timestamp);
    }

    private void reportMetered(String name, Metered meter, long timestamp) throws IOException {
        send(prefix(name, "count"), meter.getCount(), timestamp);
        send(prefix(name, "m1_rate"), convertRate(meter.getOneMinuteRate()), timestamp);
        send(prefix(name, "m5_rate"), convertRate(meter.getFiveMinuteRate()), timestamp);
        send(prefix(name, "m15_rate"), convertRate(meter.getFifteenMinuteRate()), timestamp);
        send(prefix(name, "mean_rate"), convertRate(meter.getMeanRate()), timestamp);
    }

    private void reportHistogram(String name, Histogram histogram, long timestamp) throws IOException {
        final Snapshot snapshot = histogram.getSnapshot();
        send(prefix(name, "count"), histogram.getCount(), timestamp);
        send(prefix(name), snapshot.getMax(), timestamp, "func=max");
        send(prefix(name), snapshot.getMean(), timestamp, "func=mean");
        send(prefix(name), snapshot.getMin(), timestamp, "func=min");
        send(prefix(name), snapshot.getStdDev(), timestamp, "func=stddev");
        send(prefix(name), snapshot.getMedian(), timestamp, "quantile=p50");
        send(prefix(name), snapshot.get75thPercentile(), timestamp, "quantile=p75");
        send(prefix(name), snapshot.get95thPercentile(), timestamp, "quantile=p95");
        send(prefix(name), snapshot.get98thPercentile(), timestamp, "quantile=p98");
        send(prefix(name), snapshot.get99thPercentile(), timestamp, "quantile=p99");
        send(prefix(name), snapshot.get999thPercentile(), timestamp, "quantile=p999");
    }

    private void reportCounter(String name, Counter counter, long timestamp) throws IOException {
        send(prefix(name, "count"), counter.getCount(), timestamp);
    }

    private void reportGauge(String name, Gauge gauge, long timestamp) throws IOException {
        Object value = gauge.getValue();
        if (value != null) {
            send(prefix(name), value, timestamp);
        }
    }

    private String prefix(String... components) {
        return MetricRegistry.name(prefix, components);
    }

    private void send(String name, Object value, long timestamp) throws IOException {
        n9eSender.send(endpoint, name, tags, value, timestamp, step);
    }

    private void send(String name, Object value, long timestamp, String tags) throws IOException {
        if (this.tags == null || "".equals(this.tags)) {
            n9eSender.send(endpoint, name, tags, value, timestamp, step);
        } else {
            n9eSender.send(endpoint, name, this.tags + "," + tags, value, timestamp, step);
        }
    }
}