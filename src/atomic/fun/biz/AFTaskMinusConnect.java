package atomic.fun.biz;

public class AFTaskMinusConnect extends BaseAFTaskConnect {

    public AFTaskMinusConnect(BaseAFTask leftTask, AFOPNode opNode, BaseAFTask rightTask) {
        super(leftTask, opNode, rightTask);
    }

    @Override
    protected void runSelf() {
        leftTask.setTaskListener(() -> {
            setResult(leftTask.getTaskResult());
            notifyTaskDone();
        });
        leftTask.failNext(rightTask);
        leftTask.run();
    }
}
