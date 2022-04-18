package atomic.fun.biz;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskExecutors {

    private static TaskExecutors instance = new TaskExecutors();


    public static TaskExecutors getInstance() {
        return instance;
    }

    private ExecutorService executors = Executors.newCachedThreadPool();

    public void execute(BaseAFTask task) {
        executors.execute(task);
    }
}
