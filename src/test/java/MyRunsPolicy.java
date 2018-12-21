import java.util.concurrent.ThreadPoolExecutor;

public class MyRunsPolicy {
    public MyRunsPolicy() {
    }


    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        if (!e.isShutdown()) {
            while (e.getQueue().remainingCapacity() != 0) {
                e.execute(r);
            }
            ;
        }
    }
}

