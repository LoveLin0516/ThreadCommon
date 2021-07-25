package code.example.threadcommon.utils;

/**
 * Created by muyao on 2021/7/25
 * Description: 程序任务执行接口
 */
public interface Callable<T> {
    T call();
}
