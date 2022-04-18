package atomic.fun.biz;


public class AFTaskPlusConnect extends BaseAFTaskConnect {

    public AFTaskPlusConnect(BaseAFTask leftTask, AFOPNode opNode, BaseAFTask rightTask) {
        super(leftTask, opNode, rightTask);
    }

    @Override
    protected void runSelf() {
        leftTask.setTaskListener(() -> {
            setResult(leftTask.getTaskResult());
            notifyTaskDone();
        });
        leftTask.successNext(rightTask);
        leftTask.run();
    }
}
