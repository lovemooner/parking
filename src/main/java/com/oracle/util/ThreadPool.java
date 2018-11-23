package com.oracle.util;

import com.oracle.pojo.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class ThreadPool {
    public static final Logger LOG = LoggerFactory.getLogger(ThreadPool.class);

    public static ExecutorService newThreadPool() {
        return new ThreadPoolExecutor(Constants.POOL_CORE_SIZE, Constants.POOL_MAXIMUM_SIZE,
                Constants.POOL_KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(Constants.POOL_MAXIMUM_SIZE));  //SynchronousQueue LinkedBlockingQueue
//        return Executors.newCachedThreadPool();
//        return Executors.newFixedThreadPool();
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
                    LOG.info("WorkQueue Size:" + queueSize
                            + ",Active Count:" + activeCount
                            + ",Completed Task Count:" + completedTaskCount
                            + ",Task Count:" + task);
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
