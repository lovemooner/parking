package com.oracle.util;

import com.oracle.pojo.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolFactory {
    public static final Logger LOG = LoggerFactory.getLogger(ThreadPoolFactory.class);

    public static ThreadPoolExecutor newThreadPool() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(Constants.POOL_CORE_SIZE, Constants.POOL_CORE_SIZE,
                Constants.POOL_KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    public static void startMonitor(ExecutorService executor, long millis) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    int activeCount = ((ThreadPoolExecutor) executor).getActiveCount();
                    int queueSize = ((ThreadPoolExecutor) executor).getQueue().size();
                    long completedTaskCount = ((ThreadPoolExecutor) executor).getCompletedTaskCount();
                    long task = ((ThreadPoolExecutor) executor).getTaskCount();
                    LOG.info("WorkQueueSize:" + queueSize
                            + ",ActiveCount:" + activeCount
                            + ",CompletedTaskCount:" + completedTaskCount
                            + ",TaskCount:" + task);
                    if (activeCount == 0 && queueSize == 0) {
                        LOG.info("All Task Completed");
                        break;
                    }
                    try {
                        Thread.sleep(millis);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


}
