package atomic.fun.biz;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseAFTask implements ITask<BaseAFTask> {

    private String name;

    public BaseAFTask(String name) {
        this.name = name;
    }

    private BaseAFTask successNext;
    private BaseAFTask failNext;
    private BaseAFTask seqNext;

    private ITaskListener taskListener;

    private AtomicBoolean taskDone = new AtomicBoolean(false);

    /**
     * 任务执行的产物
     */
    private Object outParam;

    /**
     * 任务的输入
     */
    private Object inParam;

    private AtomicBoolean taskResult = new AtomicBoolean(false);

    public String getName() {
        return name;
    }

    @Override
    public void seqNext(BaseAFTask task) {
        seqNext = task;
    }

    @Override
    public void failNext(BaseAFTask task) {
        failNext = task;
    }

    @Override
    public void successNext(BaseAFTask task) {
        successNext = task;
    }

    @Override
    public void run() {
        runSelf();
        notifyTaskDone();
    }

    protected void notifyTaskDone() {
        final BaseAFTask target = getTargetNext();
        ITaskListener iTaskListener = target != null ? () -> {
                    boolean ret = target.getTaskResult();
                    setResult(ret);
                    if (taskListener != null) {
                        taskListener.onTaskDone();
                    }
                } : null;
        if (target != null) {
            target.setTaskListener(iTaskListener);
            target.run();
        } else {
            if (taskListener != null) {
                taskListener.onTaskDone();
            }
        }
    }

    private BaseAFTask getTargetNext() {
        BaseAFTask target = null;
        boolean success = getTaskResult();
        if (seqNext != null) {
            target = seqNext;
        }
        if (success && successNext != null) {
            target = successNext;
        }
        if (!success && failNext != null) {
            target = failNext;
        }
        return target;
    }

    protected abstract void runSelf();

    protected void setResult(boolean result) {
        taskResult.set(result);
    }

    public boolean getTaskResult() {
        return taskResult.get();
    }

    protected void setOutParam(Object param) {
        this.outParam = param;
    }

    public void setInParam(Object inParam) {
        this.inParam = inParam;
    }

    public Object getOutParam() {
        return outParam;
    }

    protected void setTaskListener(ITaskListener taskListener) {
        this.taskListener = taskListener;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "name='" + name + '\'' +
                ", successNext=" + successNext +
                ", failNext=" + failNext +
                ", seqNext=" + seqNext +
                ", taskListener=" + taskListener +
                ", taskDone=" + taskDone +
                ", outParam=" + outParam +
                ", inParam=" + inParam +
                ", taskResult=" + taskResult +
                '}';
    }
}
