package code.example.threadcommon.utils;

import java.util.concurrent.Executor;

/**
 * Created by muyao on 2021/7/23
 * Description:
 */
public class MainExecutor implements Executor {

    @Override
    public void execute(Runnable command) {
        ThreadUtil.runOnUIThread(command);
    }
}
