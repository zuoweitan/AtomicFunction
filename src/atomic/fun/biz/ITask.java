package atomic.fun.biz;

public interface ITask<P extends ITask> extends Runnable {

    /**
     * 串行
     * @param task
     */
    void seqNext(P task);

    /**
     * 失败串行
     * @param task
     */
    void failNext(P task);

    /**
     * 成功串行
     * @param task
     */
    void successNext(P task);

    void run();
}
