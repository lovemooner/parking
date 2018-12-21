import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTest {

    public static void main(String[] args) throws InterruptedException {
//        final ThreadPoolExecutor executor2= new ThreadPoolExecutor(0, 5,
//                40l, TimeUnit.SECONDS,
//                new SynchronousQueue<Runnable>(true),//SynchronousQueue LinkedBlockingQueue
//              new  MyRunsPolicy());
//              new  ThreadPoolExecutor.CallerRunsPolicy());

        final ThreadPoolExecutor executor1 = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        test();


    }


    static void test() {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5,
                10l, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        executor.allowCoreThreadTimeOut(true);

        for (int i = 0; i < 15; i++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100l);
                        System.out.println(Thread.currentThread().getName() + " end");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "t" + i);
            executor.submit(t);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    int activeCount = ((ThreadPoolExecutor) executor).getActiveCount();
                    int queueSize = ((ThreadPoolExecutor) executor).getQueue().size();
                    long completedTaskCount = ((ThreadPoolExecutor) executor).getCompletedTaskCount();
                    long task = ((ThreadPoolExecutor) executor).getTaskCount();
                    System.out.println("WorkQueue Size:" + queueSize
                            + ",Active Count:" + activeCount
                            + ",Completed Task Count:" + completedTaskCount
                            + ",Task Count:" + task);
                    if (activeCount == 0 && queueSize == 0) {
                        System.out.println("All Task Completed");
//                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
//                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(30000l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < 1500; i++) {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000l);
                                System.out.println(Thread.currentThread().getName() + " end");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }, "t" + i);
                    executor.submit(t);
                }
            }
        }).start();

    }
}
