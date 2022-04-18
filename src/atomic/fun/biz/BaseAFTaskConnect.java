package atomic.fun.biz;

public abstract class BaseAFTaskConnect extends BaseAFTask {

    protected AFOPNode opNode;
    protected BaseAFTask leftTask;
    protected BaseAFTask rightTask;

    public BaseAFTaskConnect(BaseAFTask leftTask, AFOPNode opNode, BaseAFTask rightTask) {
        super(leftTask.getName() + opNode.getName() + rightTask.getName());
        this.leftTask = leftTask;
        this.rightTask = rightTask;
        this.opNode = opNode;
    }

    @Override
    public void run() {
        runSelf();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "opNode=" + opNode +
                ", leftTask=" + leftTask +
                ", rightTask=" + rightTask +
                '}';
    }
}
