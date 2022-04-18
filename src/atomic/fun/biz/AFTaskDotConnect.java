package atomic.fun.biz;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class AFTaskDotConnect extends BaseAFTaskConnect {

    public AFTaskDotConnect(BaseAFTask leftTask, AFOPNode opNode, BaseAFTask rightTask) {
        super(leftTask, opNode, rightTask);
    }

    @Override
    protected void runSelf() {
        AtomicInteger count = new AtomicInteger(2);
        ITaskListener iTaskListener = () -> {
            if (count.decrementAndGet() == 0) {
                setResult(leftTask.getTaskResult() &&  rightTask.getTaskResult());
                notifyTaskDone();
            }
        };
        leftTask.setTaskListener(iTaskListener);
        rightTask.setTaskListener(iTaskListener);

        TaskExecutors.getInstance().execute(leftTask);
        TaskExecutors.getInstance().execute(rightTask);
    }

}
