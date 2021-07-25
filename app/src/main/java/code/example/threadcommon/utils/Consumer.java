package code.example.threadcommon.utils;

/**
 * Created by muyao on 2021/7/25
 * Description: 当前线程使用的Consumer回调
 */
public interface Consumer<T> {
    void accept(T t);
}
