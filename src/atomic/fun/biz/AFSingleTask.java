package atomic.fun.biz;

import java.util.Random;

public class AFSingleTask extends BaseAFTask {

    public AFSingleTask(String name) {
        super(name);
    }

    @Override
    protected void runSelf() {
        boolean result = new Random().nextBoolean();
        System.out.println(getName() + " runSelf and result is " + result);
        setResult(result);
        setOutParam(null);
    }

}
