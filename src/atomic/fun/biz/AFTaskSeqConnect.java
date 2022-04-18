package atomic.fun.biz;

public class AFTaskSeqConnect extends BaseAFTaskConnect {

    public AFTaskSeqConnect(BaseAFTask leftTask, AFOPNode opNode, BaseAFTask rightTask) {
        super(leftTask, opNode, rightTask);
    }

    @Override
    protected void runSelf() {
        leftTask.setTaskListener(() -> {
            setResult(leftTask.getTaskResult());
            notifyTaskDone();
        });
        leftTask.seqNext(rightTask);
        leftTask.run();
    }
}
