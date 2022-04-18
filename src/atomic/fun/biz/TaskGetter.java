package atomic.fun.biz;

public class TaskGetter {

    public static BaseAFTask createTask(String name) {
        return new AFSingleTask(name);
    }

    public static BaseAFTaskConnect connectTask(BaseAFTask leftTask, AFOPNode opNode, BaseAFTask rightTask) {
        if (opNode.isDot()) {
            return new AFTaskDotConnect(leftTask, opNode, rightTask);
        } else if (opNode.isPlus()) {
            return new AFTaskPlusConnect(leftTask, opNode, rightTask);
        } else if (opNode.isMinus()) {
            return new AFTaskMinusConnect(leftTask, opNode, rightTask);
        } else if (opNode.isSeq()) {
            return new AFTaskSeqConnect(leftTask, opNode, rightTask);
        }
        return null;
    }
}
