package com.didiyun.n9e.example;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.didiyun.n9e.metrics.N9EReporter;
import com.didiyun.n9e.metrics.N9ESender;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class CounterExample {

    public static Queue<String> q = new LinkedBlockingQueue<String>();

    public static Counter pendingJobs;

    public static Random random = new Random();

    public static void addJob(String job) {
        pendingJobs.inc();
        q.offer(job);
    }

    public static String takeJob() {
        pendingJobs.dec();
        return q.poll();
    }

    public static void main(String[] args) throws InterruptedException {
        MetricRegistry registry = new MetricRegistry();
        N9EReporter reporter = N9EReporter.forRegistry(registry).withTags("service=a,module=b").build(new N9ESender("http://127.0.0.1/v1/push?nid=1"));
        reporter.start(5, TimeUnit.SECONDS);

        pendingJobs = registry.counter(MetricRegistry.name(Queue.class,"pending-jobs","size"));

        int num = 1;
        while(true){
            Thread.sleep(200);
            if (random.nextDouble() > 0.7){
                String job = takeJob();
                System.out.println("take job : "+job);
            }else{
                String job = "Job-"+num;
                addJob(job);
                System.out.println("add job : "+job);
            }
            num++;
        }
    }
}